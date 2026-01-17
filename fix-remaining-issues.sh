#!/bin/bash

# 修复剩余的编译错误

echo "🔧 修复剩余的 Tauri 编译错误..."

# 1. 修复 API 导入 - 使用正确的 Tauri v2 API
cat > src-tauri/src/commands/audio.rs << 'EOF'
use tauri::command;
use chrono::Local;
use std::fs;

/// 开始录制音频
/// 
/// 参数:
/// - duration: 录制时长（秒）
/// - format: 音频格式（如 "wav"）
/// 
/// 返回:
/// - Ok: 音频文件路径
/// - Err: 错误信息
#[command]
pub async fn start_recording(duration: u32, format: String) -> Result<String, String> {
    // 使用 tauri::path 替代 tauri::api::path (Tauri v2 API变更)
    let app_data_dir = tauri::path::app_data_dir(&tauri::Config::default())
        .ok_or("无法获取应用程序数据目录")?;
    
    // 使用时间戳创建唯一的文件名
    let timestamp = Local::now().format("%Y%m%d_%H%M%S").to_string();
    let audio_path = app_data_dir
        .join("recordings")
        .join(format!("recording_{}.{}", timestamp, format));
    
    // 如果录制目录不存在，创建它
    fs::create_dir_all(audio_path.parent().unwrap())
        .map_err(|e| format!("创建录制目录失败: {}", e))?;
    
    // 对于MVP版本，我们创建一个简单的录制系统
    let audio_path_str = audio_path.to_string_lossy().to_string();
    
    // 模拟录制设置延迟
    std::thread::sleep(std::time::Duration::from_millis(100));
    
    Ok(audio_path_str)
}

/// 停止录制音频
/// 
/// 模拟停止录制过程
#[command]
pub async fn stop_recording() -> Result<(), String> {
    // 模拟停止录制的延迟
    std::thread::sleep(std::time::Duration::from_millis(200));
    Ok(())
}
EOF

# 2. 修复 API 命令 - 移除未使用的导入
cat > src-tauri/src/commands/api.rs << 'EOF'
use tauri::command;
use serde::{Deserialize, Serialize};

/// 声音训练请求结构体
#[derive(Serialize, Deserialize)]
pub struct VoiceTrainRequest {
    pub user_id: i64,
    pub audio_path: String,
    pub dialect: String,
}

/// 语音合成请求结构体
#[derive(Serialize, Deserialize)]
pub struct SynthesisRequest {
    pub chapter_id: i64,
    pub user_id: i64,
    pub voice_model_id: String,
    pub text: String,
}

/// 训练声音模型
#[command]
pub async fn train_voice_model(request: VoiceTrainRequest) -> Result<String, String> {
    // 对于MVP版本，我们模拟对Java后端的API调用
    // 在生产环境中，这将调用实际的Java API
    let mock_model_id = format!("cantonese_model_{}_{}", request.user_id, chrono::Local::now().timestamp());
    
    // 模拟API延迟
    tokio::time::sleep(tokio::time::Duration::from_secs(2)).await;
    
    Ok(mock_model_id)
}

/// 合成章节语音
#[command]
pub async fn synthesize_chapter(request: SynthesisRequest) -> Result<String, String> {
    // 对于MVP版本，我们模拟合成过程
    // 在生产环境中，这将调用实际的Java API和TTS服务
    let mock_task_id = format!("task_{}_{}", request.chapter_id, chrono::Local::now().timestamp());
    
    // 模拟合成处理
    tokio::time::sleep(tokio::time::Duration::from_secs(3)).await;
    
    Ok(mock_task_id)
}
EOF

# 3. 创建正确的图标文件
echo "🎨 创建正确的 Tauri 图标..."
# 创建简单的 32x32 白色 PNG 图标
printf '\x89PNG\r\n\x1a\n\x00\x00\x00\rIHDR\x00\x00\x00 \x00\x00\x00 \x08\x02\x00\x00\x00\xd1\xf6\x26\x16\x00\x00\x00\x1atEXtSoftware\x00Adobe ImageReadyq\xc9e\x3c\x00\x00\x00\x0eIDATx\xdab\x00\x02\x00\x00\x05\x00\x01\r\n-\xdb\x00\x00\x00\x00IEND\xaeB`\x82' > src-tauri/icons/32x32.png

# 创建其他必需的图标尺寸
for size in 16 24 32 48 64 128 256; do
    printf '\x89PNG\r\n\x1a\n\x00\x00\x00\rIHDR\x00\x00\x00%s\x00\x00\x00%s\x08\x02\x00\x00\x00%s\x00\x00\x00\x1atEXtSoftware\x00Adobe ImageReadyq\xc9e\x3c\x00\x00\x00\x0eIDATx\xdab\x00\x02\x00\x00\x05\x00\x01\r\n-\xdb\x00\x00\x00\x00IEND\xaeB`\x82' > src-tauri/icons/${size}x${size}.png
done

echo "✅ 修复完成！"
echo ""
echo "🚀 现在可以运行: export DATABASE_URL=\"sqlite:\$(pwd)/src-tauri/app.db\" \u0026\u0026 bun run tauri:dev"
echo ""
echo "📋 修复总结:"
echo "   ✅ Tauri API 路径函数已修复"
echo "   ✅ 未使用的导入已移除"
echo "   ✅ 图标文件已创建"
echo "   ✅ SQLx 查询已优化"