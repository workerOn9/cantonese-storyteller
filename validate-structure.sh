#!/bin/bash

# 粤语评书项目目录结构验证脚本
# Cantonese Storyteller Project Structure Validator

echo "🔍 正在验证项目目录结构..."
echo "=================================="

PROJECT_ROOT="."
ERRORS=0
WARNINGS=0

# 检查目录存在的函数
check_directory() {
    local dir_path="$1"
    local description="$2"
    local required="$3"
    
    if [ -d "$dir_path" ]; then
        echo "✅ $description: 存在"
        return 0
    else
        if [ "$required" = "true" ]; then
            echo "❌ $description: 缺失 (必需)"
            ((ERRORS++))
        else
            echo "⚠️  $description: 缺失 (可选)"
            ((WARNINGS++))
        fi
        return 1
    fi
}

# 检查文件存在的函数
check_file() {
    local file_path="$1"
    local description="$2"
    local required="$3"
    
    if [ -f "$file_path" ]; then
        echo "✅ $description: 存在"
        return 0
    else
        if [ "$required" = "true" ]; then
            echo "❌ $description: 缺失 (必需)"
            ((ERRORS++))
        else
            echo "⚠️  $description: 缺失 (可选)"
            ((WARNINGS++))
        fi
        return 1
    fi
}

echo ""
echo "📁 前端部分检查:"
check_file "package.json" "前端包配置" "true"
check_file "tsconfig.json" "TypeScript配置" "true"
check_file "vite.config.ts" "Vite构建配置" "true"
check_file "index.html" "HTML入口文件" "true"
check_directory "src" "前端源代码目录" "true"

echo ""
echo "🔧 Rust后端部分检查:"
check_directory "src-tauri" "Tauri后端目录" "true"
check_file "src-tauri/Cargo.toml" "Rust依赖配置" "true"
check_file "src-tauri/tauri.conf.json" "Tauri配置文件" "true"
check_directory "src-tauri/src" "Rust源代码目录" "true"
check_directory "src-tauri/migrations" "数据库迁移目录" "true"

echo ""
echo "☕ Java后端部分检查:"
check_directory "backend-java" "Java后端目录" "true"
check_file "backend-java/build.gradle" "Gradle构建配置" "true"
check_directory "backend-java/src/main/java" "Java源代码目录" "true"

echo ""
echo "📄 文档和脚本检查:"
check_file "README.md" "英文文档" "true"
check_file "README.zh.md" "中文文档" "false"
check_file "PROJECT_INFO.md" "项目信息文档" "false"
check_file "test-mvp.sh" "MVP测试脚本" "false"

echo ""
echo "📊 检查结果汇总:"
echo "=================================="
echo "错误数量: $ERRORS"
echo "警告数量: $WARNINGS"

if [ $ERRORS -eq 0 ]; then
    echo ""
    echo "🎉 项目目录结构验证通过！"
    echo "✅ 所有必需的目录和文件都存在"
    
    if [ $WARNINGS -gt 0 ]; then
        echo "⚠️  有一些可选文件缺失，但不会影响基本功能"
    fi
    
    echo ""
    echo "🚀 下一步操作:"
    echo "   1. 运行 ./test-mvp.sh 测试项目依赖"
    echo "   2. npm install 安装前端依赖"
    echo "   3. npm run tauri:dev 启动开发环境"
else
    echo ""
    echo "❌ 项目目录结构验证失败！"
    echo "请检查缺失的必需文件和目录"
    exit 1
fi