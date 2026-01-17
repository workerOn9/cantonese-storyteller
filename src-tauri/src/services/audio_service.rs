use std::path::PathBuf;
use tokio::fs;
use hound;

/// 音频服务
/// 提供音频文件验证和信息提取功能
pub struct AudioService;

impl AudioService {
    /// 创建新的音频服务实例
    pub fn new() -> Self {
        Self
    }

    /// 验证音频文件
    /// 
    /// 检查文件是否存在、大小是否合适（MVP版本限制为50MB）以及是否为有效的WAV格式
    /// 
    /// 参数:
    /// - path: 音频文件路径
    /// 
    /// 返回:
    /// - Ok(true): 文件验证成功
    /// - Err: 错误信息
    pub async fn validate_audio_file(path: &str) -> Result<bool, String> {
        let path_buf = PathBuf::from(path);
        
        // 检查文件是否存在
        if !path_buf.exists() {
            return Err("音频文件不存在".to_string());
        }
        
        // 检查文件大小（MVP版本限制为50MB）
        let metadata = fs::metadata(path_buf)
            .await
            .map_err(|e| format!("读取文件元数据失败: {}", e))?;
        
        if metadata.len() > 50 * 1024 * 1024 {
            return Err("音频文件过大（最大50MB）".to_string());
        }
        
        // 验证WAV格式
        match hound::WavReader::open(path) {
            Ok(_) => Ok(true),
            Err(e) => Err(format!("无效的WAV文件: {}", e)),
        }
    }

    /// 获取音频信息
    /// 
    /// 从WAV文件中提取技术信息，如采样率、通道数、位深度和时长
    /// 
    /// 参数:
    /// - path: WAV文件路径
    /// 
    /// 返回:
    /// - Ok: 音频信息结构体
    /// - Err: 错误信息
    pub async fn get_audio_info(path: &str) -> Result<AudioInfo, String> {
        let reader = hound::WavReader::open(path)
            .map_err(|e| format!("打开WAV文件失败: {}", e))?;
        
        let spec = reader.spec();
        let duration = reader.duration();
        
        Ok(AudioInfo {
            sample_rate: spec.sample_rate,  // 采样率
            channels: spec.channels,         // 通道数
            bits_per_sample: spec.bits_per_sample,  // 位深度
            duration_seconds: duration as f64 / spec.sample_rate as f64,  // 时长（秒）
        })
    }
}

/// 音频信息结构体
/// 包含WAV文件的技术规格信息
#[derive(Debug, Clone)]
pub struct AudioInfo {
    /// 采样率（Hz）
    pub sample_rate: u32,
    /// 通道数（1=单声道，2=立体声）
    pub channels: u16,
    /// 位深度（每个样本的位数）
    pub bits_per_sample: u16,
    /// 时长（秒）
    pub duration_seconds: f64,
}