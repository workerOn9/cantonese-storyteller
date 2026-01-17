/**
 * 服务模块集合
 * 
 * 该模块包含所有业务逻辑服务，提供核心功能的实现。
 * 
 * 模块功能：
 * - audio_service: 音频处理服务（验证、信息提取）
 * - cache_service: 本地缓存服务（SQLite数据库操作）
 */

pub mod audio_service;  // 音频处理和管理服务
pub mod cache_service;  // 本地数据缓存服务