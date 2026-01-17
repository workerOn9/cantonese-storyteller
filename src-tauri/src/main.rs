use tauri::Manager;
use std::sync::Mutex;

mod commands;
mod services;
mod db;

use commands::audio;
use commands::api;
use commands::fs;

/// 应用程序状态管理结构体
/// 用于在Tauri应用程序中共享状态
struct AppState {
    /// 录制状态，使用Mutex保证线程安全
    recording_state: Mutex<RecordingState>,
}

/// 录制状态结构体
/// 跟踪当前是否正在录制以及音频文件路径
struct RecordingState {
    /// 是否正在录制
    is_recording: bool,
    /// 音频文件路径（如果有的话）
    audio_path: Option<String>,
}

/// Tauri应用程序入口点
/// 配置和启动整个应用程序
#[cfg_attr(mobile, tauri::mobile_entry_point)]
pub fn run() {
    // 初始化日志记录器
    env_logger::init();

    tauri::Builder::default()
        // 管理应用程序状态，使其对所有命令处理器可用
        .manage(AppState {
            recording_state: Mutex::new(RecordingState {
                is_recording: false,
                audio_path: None,
            }),
        })
    // 注册所有Tauri命令处理器
    .invoke_handler(tauri::generate_handler![
            // 音频命令
            audio::start_recording,
            audio::stop_recording,
            // API命令
            api::train_voice_model,
            api::synthesize_chapter,
            // 文件系统命令
            fs::get_audio_file,
            fs::save_audio_file,
        ])
        .setup(|app| {
            #[cfg(debug_assertions)]
            {
                let window = app.get_webview_window("main").unwrap();
                window.open_devtools();
            }
            Ok(())
        })
        .run(tauri::generate_context!())
        .expect("运行Tauri应用程序时出错");
}

/// 主函数 - 应用程序入口点
fn main() {
    run();
}
