/**
 * 命令模块集合
 * 
 * 该模块包含所有Tauri命令处理器，用于处理前端与Rust后端之间的IPC通信。
 * 
 * 模块功能：
 * - audio: 音频录制和处理命令
 * - api: API集成和外部服务调用命令  
 * - fs: 文件系统操作命令
 */

pub mod audio;  // 音频录制和处理相关命令
pub mod api;    // API集成和外部服务调用命令
pub mod fs;     // 文件系统操作和管理命令