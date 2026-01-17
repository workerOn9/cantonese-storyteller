-- 粤语评书应用程序 - SQLite数据库初始模式
-- 创建本地缓存所需的表结构

-- 录制表：存储用户的粤语录音信息
CREATE TABLE IF NOT EXISTS recordings (
    id INTEGER PRIMARY KEY AUTOINCREMENT,              -- 主键ID
    file_path TEXT NOT NULL,                           -- 音频文件路径
    duration_seconds INTEGER NOT NULL,                -- 录音时长（秒）
    file_size_bytes INTEGER NOT NULL,                 -- 文件大小（字节）
    created_at TEXT NOT NULL,                          -- 创建时间
    user_id INTEGER NOT NULL                           -- 用户ID
);

-- 声音模型表：存储训练的声音模型信息
CREATE TABLE IF NOT EXISTS voice_models (
    id INTEGER PRIMARY KEY AUTOINCREMENT,              -- 主键ID
    user_id INTEGER NOT NULL,                          -- 用户ID（外键）
    model_id TEXT NOT NULL UNIQUE,                     -- 模型唯一标识符
    dialect TEXT NOT NULL DEFAULT 'cantonese',        -- 方言类型（默认为粤语）
    trained_at TEXT NOT NULL,                          -- 训练完成时间
    status TEXT NOT NULL DEFAULT 'active'              -- 模型状态（活跃/禁用）
);

-- 章节表：存储书籍章节信息
CREATE TABLE IF NOT EXISTS chapters (
    id INTEGER PRIMARY KEY AUTOINCREMENT,              -- 主键ID
    chapter_id INTEGER NOT NULL UNIQUE,               -- 章节业务ID
    title TEXT NOT NULL,                               -- 章节标题
    text TEXT NOT NULL,                                -- 章节文本内容
    audio_path TEXT,                                   -- 合成音频文件路径（可选）
    created_at TEXT NOT NULL,                          -- 创建时间
    updated_at TEXT NOT NULL                           -- 更新时间
);

-- 合成任务表：跟踪语音合成任务状态
CREATE TABLE IF NOT EXISTS synthesis_tasks (
    id INTEGER PRIMARY KEY AUTOINCREMENT,              -- 主键ID
    task_id TEXT NOT NULL UNIQUE,                     -- 任务唯一标识符
    user_id INTEGER NOT NULL,                          -- 用户ID（外键）
    chapter_id INTEGER NOT NULL,                       -- 章节ID（外键）
    voice_model_id TEXT NOT NULL,                      -- 声音模型ID
    status TEXT NOT NULL DEFAULT 'pending',           -- 任务状态（待处理/处理中/完成/失败）
    audio_url TEXT,                                    -- 合成音频URL（任务成功时）
    error_message TEXT,                                -- 错误信息（任务失败时）
    created_at TEXT NOT NULL,                          -- 任务创建时间
    completed_at TEXT                                  -- 任务完成时间
);