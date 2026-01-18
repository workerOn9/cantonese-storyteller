use tauri::command;
use serde::{Deserialize, Serialize};
use std::path::Path;
use reqwest::multipart;

/// Java后端API配置
const JAVA_API_BASE_URL: &str = "http://localhost:8080/api";

/// 语音合成请求结构体
#[derive(Serialize, Deserialize)]
#[serde(rename_all = "camelCase")]
pub struct SynthesisRequest {
    pub chapter_id: i64,
    pub user_id: i64,
    pub voice_model_id: String,
    pub text: String,
}

/// 训练声音模型
#[command]
pub async fn train_voice_model(
    user_id: i64,
    audio_path: String,
    dialect: String,
) -> Result<String, String> {
    log::info!("开始训练声音模型: user_id={}, audio_path={}, dialect={}", user_id, audio_path, dialect);

    // 检查音频文件是否存在
    if !Path::new(&audio_path).exists() {
        let error_msg = format!("音频文件不存在: {}", audio_path);
        log::error!("{}", error_msg);
        return Err(error_msg);
    }

    // 构建Java后端API URL
    let api_url = format!("{}/voice/train", JAVA_API_BASE_URL);

    // 创建HTTP客户端
    let client = reqwest::Client::new();

    // 读取音频文件 (使用 tokio 异步文件操作)
    let file_content = match tokio::fs::read(&audio_path).await {
        Ok(content) => content,
        Err(e) => {
            let error_msg = format!("读取音频文件失败: {}", e);
            log::error!("{}", error_msg);
            return Err(error_msg);
        }
    };

    // 获取文件名
    let filename = Path::new(&audio_path)
        .file_name()
        .and_then(|n| n.to_str())
        .unwrap_or("audio.wav");

    // 构建multipart表单 - 使用正确的 reqwest multipart API
    let audio_part = multipart::Part::bytes(file_content)
        .file_name(filename.to_string())
        .mime_str("audio/wav")
        .map_err(|e| format!("构建MIME类型失败: {}", e))?;

    let form = multipart::Form::new()
        .text("userId", user_id.to_string())
        .text("dialect", dialect)
        .part("audio", audio_part);

    log::info!("发送请求到Java后端: {}", api_url);

    // 发送HTTP请求
    let response = match client
        .post(&api_url)
        .multipart(form)
        .send()
        .await
    {
        Ok(resp) => resp,
        Err(e) => {
            let error_msg = format!("连接Java后端失败: {}", e);
            log::error!("{}", error_msg);
            // 如果Java后端不可用，使用模拟实现
            log::warn!("Java后端不可用，使用模拟实现");
            let mock_model_id = format!("cantonese_model_{}_{}", user_id, chrono::Local::now().timestamp());
            tokio::time::sleep(tokio::time::Duration::from_secs(2)).await;
            return Ok(mock_model_id);
        }
    };

    // 处理响应
    match response.status().as_u16() {
        200..=299 => {
            // 请求成功，解析响应体获取模型ID
            let model_id = match response.text().await {
                Ok(id) => id,
                Err(e) => {
                    let error_msg = format!("解析响应失败: {}", e);
                    log::error!("{}", error_msg);
                    return Err(error_msg);
                }
            };
            log::info!("声音模型训练成功: {}", model_id);
            Ok(model_id)
        }
        _ => {
            let status = response.status();
            let error_body = response.text().await.unwrap_or_default();
            let error_msg = format!("Java后端返回错误: {} - {}", status, error_body);
            log::error!("{}", error_msg);
            Err(error_msg)
        }
    }
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
