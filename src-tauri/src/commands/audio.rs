use tauri::command;
use tauri::State;
use chrono::Local;
use std::fs;
use std::sync::Mutex;

// 导入录制服务
use crate::services::recording_service::RecordingService;

/// 开始录制音频
///
/// 使用麦克风录制音频，保存为 WAV 文件
///
/// 参数:
/// - duration: 录制时长（秒）- 注意：当前版本需要手动停止
/// - format: 音频格式（当前仅支持 "wav"）
/// - service: 录制服务的 Tauri 状态
///
/// 返回:
/// - Ok(String): 音频文件路径
/// - Err(String): 错误信息
#[command]
pub async fn start_recording(
    duration: u32,
    format: String,
    service: State<'_, Mutex<RecordingService>>,
) -> Result<String, String> {
    // 获取主目录
    let home_dir = dirs::home_dir().ok_or("无法获取主目录")?;
    let app_data_dir = home_dir.join(".cantonese-storyteller");

    // 使用时间戳创建唯一的文件名
    let timestamp = Local::now().format("%Y%m%d_%H%M%S").to_string();
    let audio_path = app_data_dir
        .join("recordings")
        .join(format!("recording_{}.{}", timestamp, format));

    // 如果录制目录不存在，创建它
    fs::create_dir_all(audio_path.parent().unwrap())
        .map_err(|e| format!("创建录制目录失败: {}", e))?;

    let audio_path_str = audio_path.to_string_lossy().to_string();

    // 启动真实录制
    let mut svc = service.lock().unwrap();
    svc.start(&audio_path_str)?;

    // TODO: 实现 duration 参数的自动停止功能
    // 可以使用 tokio::time::sleep 在后台任务中实现
    if duration > 0 {
        log::info!("录制将在 {} 秒后需要手动停止", duration);
    }

    Ok(audio_path_str)
}

/// 停止录制音频
///
/// 停止当前正在进行的录制，并保存 WAV 文件
///
/// 参数:
/// - service: 录制服务的 Tauri 状态
///
/// 返回:
/// - Ok(()): 录制成功停止
/// - Err(String): 错误信息
#[command]
pub async fn stop_recording(
    service: State<'_, Mutex<RecordingService>>,
) -> Result<(), String> {
    let mut svc = service.lock().unwrap();
    svc.stop()
}
