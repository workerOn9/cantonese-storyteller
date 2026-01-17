use sqlx::sqlite::SqlitePool;
use std::path::PathBuf;

/// 数据库连接池包装结构体
/// 提供SQLite数据库连接和连接池管理
pub struct Database {
    pool: SqlitePool,
}

impl Database {
    /// 创建新的数据库连接
    /// 
    /// 在指定的应用程序数据目录中创建或连接到SQLite数据库
    /// 自动运行数据库迁移以创建必要的表结构
    /// 
    /// 参数:
    /// - app_data_dir: 应用程序数据目录路径
    /// 
    /// 返回:
    /// - Ok: 数据库实例
    /// - Err: 数据库连接或迁移错误
    pub async fn new(app_data_dir: PathBuf) -> Result<Self, sqlx::Error> {
        // 构建数据库文件路径
        let db_path = app_data_dir.join("app.db");
        let database_url = format!("sqlite:{}", db_path.display());
        
        // 连接到SQLite数据库
        let pool = SqlitePool::connect(&database_url).await?;
        
        // 运行数据库迁移，创建必要的表结构
        sqlx::migrate!("./migrations")
            .run(&pool)
            .await?;
        
        Ok(Self { pool })
    }
    
    /// 获取数据库连接池引用
    /// 
    /// 用于在其他服务中执行数据库操作
    pub fn get_pool(&self) -> &SqlitePool {
        &self.pool
    }
}