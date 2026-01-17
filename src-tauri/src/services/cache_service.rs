use sqlx::sqlite::SqlitePool;
use serde::{Deserialize, Serialize};
use chrono::{DateTime, Utc};
use sqlx::Row; // 添加 Row trait 导入

/// 缓存章节结构体
#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct CachedChapter {
    pub chapter_id: i64,
    pub title: String,
    pub text: String,
    pub audio_path: Option<String>,
    pub created_at: DateTime<Utc>,
}

/// 缓存服务
pub struct CacheService {
    pool: SqlitePool,
}

impl CacheService {
    /// 创建新的缓存服务实例
    pub async fn new(database_url: &str) -> Result<Self, sqlx::Error> {
        let pool = SqlitePool::connect(database_url).await?;
        
        // 使用运行时查询而不是编译时查询
        sqlx::query(
            r#"
            CREATE TABLE IF NOT EXISTS cached_chapters (
                chapter_id INTEGER PRIMARY KEY,
                title TEXT NOT NULL,
                text TEXT NOT NULL,
                audio_path TEXT,
                created_at TEXT NOT NULL
            )
            "#
        )
        .execute(&pool)
        .await?;
        
        Ok(Self { pool })
    }
    
    /// 缓存章节
    pub async fn cache_chapter(&self, 
        chapter: &CachedChapter
    ) -> Result<(), sqlx::Error> {
        sqlx::query(
            r#"
            INSERT OR REPLACE INTO cached_chapters 
            (chapter_id, title, text, audio_path, created_at)
            VALUES (?, ?, ?, ?, ?)
            "#
        )
        .bind(chapter.chapter_id)
        .bind(&chapter.title)
        .bind(&chapter.text)
        .bind(&chapter.audio_path)
        .bind(chapter.created_at.to_rfc3339())
        .execute(&self.pool)
        .await?;
        
        Ok(())
    }
    
    /// 获取缓存的章节
    pub async fn get_cached_chapter(
        &self, 
        chapter_id: i64
    ) -> Result<Option<CachedChapter>, sqlx::Error> {
        let record = sqlx::query(
            r#"
            SELECT chapter_id, title, text, audio_path, created_at
            FROM cached_chapters
            WHERE chapter_id = ?
            "#
        )
        .bind(chapter_id)
        .fetch_optional(&self.pool)
        .await?;
        
        match record {
            Some(record) => {
                // 解析RFC3339格式的时间戳
                let created_at_str: String = record.get("created_at");
                let created_at = DateTime::parse_from_rfc3339(&created_at_str)
                    .map(|dt| dt.with_timezone(&Utc))
                    .unwrap_or_else(|_| Utc::now());
                
                Ok(Some(CachedChapter {
                    chapter_id: record.get("chapter_id"),
                    title: record.get("title"),
                    text: record.get("text"),
                    audio_path: record.get("audio_path"),
                    created_at,
                }))
            }
            None => Ok(None),
        }
    }
}
