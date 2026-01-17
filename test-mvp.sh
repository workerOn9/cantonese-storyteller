#!/bin/bash

# 粤语评书 - MVP测试脚本
# 该脚本用于验证应用程序的基本功能
# 更新说明：已切换到Bun包管理器，提供更快的依赖安装和构建速度

echo "🎤 粤语评书 - Cantonese Storyteller MVP测试"
echo "============================================"

# 检查前置条件
echo "📋 检查前置条件..."

# 检查Bun（优先）或Node.js
if command -v bun >/dev/null 2>&1; then
    echo "✅ Bun: $(bun --version) - 使用超快的Bun包管理器"
    PACKAGE_MANAGER="bun"
elif command -v node >/dev/null 2>&1; then
    echo "⚠️  Node.js: $(node --version) - 未检测到Bun，使用Node.js作为备选"
    echo "💡 建议：安装Bun以获得更快的构建速度：curl -fsSL https://bun.sh/install | bash"
    PACKAGE_MANAGER="npm"
else
    echo "❌ 未检测到Bun或Node.js。请安装Bun："
    echo "   curl -fsSL https://bun.sh/install | bash"
    echo "   或者安装Node.js 18+"
    exit 1
fi

# 检查Rust
if command -v rustc >/dev/null 2>&1; then
    echo "✅ Rust: $(rustc --version)"
else
    echo "❌ 未找到Rust。请安装Rust："
    echo "   curl --proto '=https' --tlsv1.2 -sSf https://sh.rustup.rs | sh"
    exit 1
fi

# 检查Java
if command -v java >/dev/null 2>&1; then
    echo "✅ Java: $(java --version)"
else
    echo "❌ 未找到Java。请安装JDK 17+"
    exit 1
fi

echo ""
echo "🔧 构建前端依赖..."
if [ "$PACKAGE_MANAGER" = "bun" ]; then
    echo "⚡ 使用Bun安装依赖（比npm快10-100倍）..."
    bun install
else
    echo "📦 使用npm安装依赖..."
    npm install
fi

echo ""
echo "🔧 构建Rust后端..."
cd src-tauri
cargo check
cd ..

echo ""
echo "🔧 构建Java后端..."
cd backend-java
./gradlew build --no-daemon
cd ..

echo ""
echo "✅ 构建成功完成！"
echo ""
echo "🚀 启动应用程序："
echo "   1. 启动PostgreSQL数据库"
if [ "$PACKAGE_MANAGER" = "bun" ]; then
    echo "   2. 运行: bun run tauri:dev"
else
    echo "   2. 运行: npm run tauri:dev"
fi
echo "   3. 在另一个终端运行: cd backend-java && ./gradlew bootRun"
echo ""
echo "📖 使用说明："
echo "   1. 录制您的声音（30秒）"
echo "   2. 训练声音模型"
echo "   3. 用您的声音生成有声读物！"
echo ""
echo "🌟 Bun优势："
echo "   - ⚡ 安装速度比npm快10-100倍"
echo "   - 🚀 内置TypeScript和JSX支持"
echo "   - 📦 自动安装peerDependencies"
echo "   - 🔧 与npm脚本完全兼容"