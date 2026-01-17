# Bun 迁移指南

本项目已从 npm 迁移到 Bun，以获得更快的包管理速度和更好的开发体验。

## 🚀 Bun 安装

```bash
# macOS/Linux
curl -fsSL https://bun.sh/install | bash

# Windows (PowerShell)
powershell -c "irm bun.sh/install.ps1|iex"
```

## 📦 包管理命令对照表

| npm 命令 | Bun 命令 | 说明 |
|---------|---------|------|
| `npm install` | `bun install` | 安装依赖 |
| `npm run dev` | `bun run dev` | 运行开发服务器 |
| `npm run build` | `bun run build` | 构建项目 |
| `npm run tauri:dev` | `bun run tauri:dev` | 启动 Tauri 开发模式 |
| `npm run tauri:build` | `bun run tauri:build` | 构建 Tauri 应用 |

## ⚡ Bun 的优势

1. **极速安装**: 比 npm 快 10-100 倍
2. **内置 TypeScript**: 无需额外配置
3. **自动 peerDependencies**: 减少依赖冲突
4. **兼容 npm**: 完全兼容现有的 package.json 和脚本
5. **更小的 node_modules**: 优化的依赖管理

## 🔧 环境检查

运行测试脚本检查环境：
```bash
./test-mvp.sh
```

脚本会自动检测 Bun 或 Node.js，优先使用 Bun。

## 🎯 快速开始

```bash
# 1. 安装依赖（使用 Bun）
bun install

# 2. 启动开发环境
bun run tauri:dev

# 3. 启动 Java 后端（在另一个终端）
cd backend-java && ./gradlew bootRun
```

## 📋 注意事项

- 如果未安装 Bun，脚本会自动回退到 Node.js/npm
- Bun 完全兼容现有的 npm 脚本和配置
- 无需修改 tsconfig.json 或 vite.config.ts
- 现有的 React 和 TypeScript 配置保持不变

## 🌟 性能提升

使用 Bun 后，您可以期待：
- 依赖安装速度提升 10-100 倍
- 更快的开发服务器启动
- 更小的磁盘空间占用
- 更好的开发体验