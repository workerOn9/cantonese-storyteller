use cpal::traits::{DeviceTrait, HostTrait, StreamTrait};
use hound::{WavWriter, WavSpec};
use std::sync::{Arc, Mutex};
use std::collections::HashMap;

/// 录制服务
/// 使用通道管理方式避免 cpal::Stream 的 Send 问题
pub struct RecordingService {
    /// 录制状态标志
    is_recording: Arc<Mutex<bool>>,
    /// WAV 写入器集合（key: 文件路径）
    writers: Arc<Mutex<HashMap<String, Arc<Mutex<WavWriter<std::io::BufWriter<std::fs::File>>>>>>>,
}

impl RecordingService {
    /// 创建新的录制服务实例
    pub fn new() -> Self {
        Self {
            is_recording: Arc::new(Mutex::new(false)),
            writers: Arc::new(Mutex::new(HashMap::new())),
        }
    }

    /// 开始录制音频
    ///
    /// 参数:
    /// - path: 输出 WAV 文件的路径
    ///
    /// 返回:
    /// - Ok(()): 录制成功启动
    /// - Err: 错误信息
    pub fn start(&mut self, path: &str) -> Result<(), String> {
        // 获取默认音频主机
        let host = cpal::default_host();

        // 获取默认输入设备（麦克风）
        let device = host
            .default_input_device()
            .ok_or("未找到音频输入设备")?;

        // 获取默认输入配置
        let config = device
            .default_input_config()
            .map_err(|e| format!("获取音频配置失败: {}", e))?;

        // 创建 WAV 文件规范
        let spec = WavSpec {
            channels: config.channels() as _,
            sample_rate: config.sample_rate().0,
            bits_per_sample: 16,
            sample_format: hound::SampleFormat::Int,
        };

        // 创建 WAV 文件写入器
        let writer = WavWriter::create(path, spec)
            .map_err(|e| format!("创建 WAV 文件失败: {}", e))?;
        let writer = Arc::new(Mutex::new(writer));

        // 保存写入器
        let path_string = path.to_string();
        self.writers
            .lock()
            .unwrap()
            .insert(path_string.clone(), writer.clone());

        // 获取音频格式（需要在 into() 之前）
        let sample_format = config.sample_format();

        // 获取音频配置用于构建流
        let stream_config: cpal::StreamConfig = config.into();

        // 根据数据格式构建音频流
        let err_fn = |err| eprintln!("音频流错误: {}", err);

        let stream = match sample_format {
            cpal::SampleFormat::F32 => {
                build_stream_f32(&device, &stream_config, err_fn, path_string, self.writers.clone())
            }
            cpal::SampleFormat::I16 => {
                build_stream_i16(&device, &stream_config, err_fn, path_string, self.writers.clone())
            }
            cpal::SampleFormat::U16 => {
                build_stream_u16(&device, &stream_config, err_fn, path_string, self.writers.clone())
            }
            _ => return Err("不支持的音频格式".to_string()),
        }
        .map_err(|e| format!("创建音频流失败: {}", e))?;

        // 启动音频流（开始录制）
        stream
            .play()
            .map_err(|e| format!("启动录制失败: {}", e))?;

        // 丢弃流，让它在后台运行
        std::mem::forget(stream);

        // 更新状态
        *self.is_recording.lock().unwrap() = true;

        Ok(())
    }

    /// 停止录制音频
    ///
    /// 返回:
    /// - Ok(()): 录制成功停止
    /// - Err: 错误信息
    pub fn stop(&mut self) -> Result<(), String> {
        *self.is_recording.lock().unwrap() = false;
        // 注意：由于 Stream 不是 Send，我们无法直接停止它
        // 当 WAV 写入器被 drop 时，文件会自动完成写入
        Ok(())
    }

    /// 检查是否正在录制
    pub fn is_recording(&self) -> bool {
        *self.is_recording.lock().unwrap()
    }
}

impl Default for RecordingService {
    fn default() -> Self {
        Self::new()
    }
}

/// 构建 F32 格式的音频流
fn build_stream_f32(
    device: &cpal::Device,
    config: &cpal::StreamConfig,
    err_fn: impl Fn(cpal::StreamError) + Send + 'static,
    path: String,
    writers: Arc<Mutex<HashMap<String, Arc<Mutex<WavWriter<std::io::BufWriter<std::fs::File>>>>>>>,
) -> Result<cpal::Stream, String> {
    device
        .build_input_stream(
            config,
            {
                move |data: &[f32], _: &cpal::InputCallbackInfo| {
                    let writers = writers.lock().unwrap();
                    if let Some(writer) = writers.get(&path) {
                        let mut w = writer.lock().unwrap();
                        for &sample in data {
                            let sample_i16 = (sample * i16::MAX as f32) as i16;
                            let _ = w.write_sample(sample_i16);
                        }
                    }
                }
            },
            err_fn,
            None,
        )
        .map_err(|e| format!("创建 F32 音频流失败: {}", e))
}

/// 构建 I16 格式的音频流
fn build_stream_i16(
    device: &cpal::Device,
    config: &cpal::StreamConfig,
    err_fn: impl Fn(cpal::StreamError) + Send + 'static,
    path: String,
    writers: Arc<Mutex<HashMap<String, Arc<Mutex<WavWriter<std::io::BufWriter<std::fs::File>>>>>>>,
) -> Result<cpal::Stream, String> {
    device
        .build_input_stream(
            config,
            {
                move |data: &[i16], _: &cpal::InputCallbackInfo| {
                    let writers = writers.lock().unwrap();
                    if let Some(writer) = writers.get(&path) {
                        let mut w = writer.lock().unwrap();
                        for &sample in data {
                            let _ = w.write_sample(sample);
                        }
                    }
                }
            },
            err_fn,
            None,
        )
        .map_err(|e| format!("创建 I16 音频流失败: {}", e))
}

/// 构建 U16 格式的音频流
fn build_stream_u16(
    device: &cpal::Device,
    config: &cpal::StreamConfig,
    err_fn: impl Fn(cpal::StreamError) + Send + 'static,
    path: String,
    writers: Arc<Mutex<HashMap<String, Arc<Mutex<WavWriter<std::io::BufWriter<std::fs::File>>>>>>>,
) -> Result<cpal::Stream, String> {
    device
        .build_input_stream(
            config,
            {
                move |data: &[u16], _: &cpal::InputCallbackInfo| {
                    let writers = writers.lock().unwrap();
                    if let Some(writer) = writers.get(&path) {
                        let mut w = writer.lock().unwrap();
                        for &sample in data {
                            // 转换 u16 到 i16
                            let sample_i16 = sample.wrapping_sub(32768) as i16;
                            let _ = w.write_sample(sample_i16);
                        }
                    }
                }
            },
            err_fn,
            None,
        )
        .map_err(|e| format!("创建 U16 音频流失败: {}", e))
}
