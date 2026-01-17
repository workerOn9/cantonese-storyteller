use tauri::command;
use std::path::PathBuf;
use tokio::fs;

/// 获取音频文件
/// 
/// 从指定路径读取音频文件内容
/// 
/// 参数:
/// - path: 音频文件路径
/// 
/// 返回:
/// - Ok: 音频文件字节数据
/// - Err: 错误信息
#[command]
pub async fn get_audio_file(path: String) -> Result<Vec<u8>, String> {
    let path_buf = PathBuf::from(path);
    
    // 异步读取音频文件
    fs::read(path_buf)
        .await
        .map_err(|e| format!("读取音频文件失败: {}", e))
}

/// 保存音频文件
/// 
/// 将音频数据保存到指定路径，如果父目录不存在则创建
/// 
/// 参数:
/// - path: 目标文件路径
/// - data: 音频文件字节数据
/// 
/// 返回:
/// - Ok: 保存成功
/// - Err: 错误信息
#[command]
pub async fn save_audio_file(path: String, data: Vec<u8>) -> Result<(), String> {
    let path_buf = PathBuf::from(path);
    
    // 如果父目录不存在，创建它
    if let Some(parent) = path_buf.parent() {
        fs::create_dir_all(parent)
            .await
            .map_err(|e| format!("创建目录失败: {}", e))?;
    }
    
    // 异步写入音频文件
    fs::write(path_buf, data)
        .await
        .map_err(|e| format!("保存音频文件失败: {}", e))
}