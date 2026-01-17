#!/bin/bash

# 创建符合 Tauri 要求的有效 RGBA PNG 图标

echo "🎨 创建有效的 RGBA PNG 图标..."

mkdir -p src-tauri/icons

# 创建简单的白色 RGBA PNG 图标（1x1 像素）
# 使用 ImageMagick 或创建有效的 PNG 数据
if command -v convert >/dev/null 2>&1; then
    echo "使用 ImageMagick 创建图标..."
    # 使用 ImageMagick 创建白色图标
    convert -size 32x32 xc:white src-tauri/icons/32x32.png
    convert -size 16x16 xc:white src-tauri/icons/16x16.png
    convert -size 24x24 xc:white src-tauri/icons/24x24.png
    convert -size 48x48 xc:white src-tauri/icons/48x48.png
    convert -size 64x64 xc:white src-tauri/icons/64x64.png
    convert -size 128x128 xc:white src-tauri/icons/128x128.png
    convert -size 256x256 xc:white src-tauri/icons/256x256.png
else
    echo "创建简单的 PNG 占位符..."
    # 创建有效的 1x1 白色 PNG（RGBA格式）
    echo -ne '\x89PNG\r\n\x1a\n\x00\x00\x00\rIHDR\x00\x00\x00\x20\x00\x00\x00\x20\x08\x06\x00\x00\x00\x73\x7a\x7a\xf4\x00\x00\x00\x1ctEXtSoftware\x00Adobe ImageReadyq\xc9e\x3c\x00\x00\x00\x13IDATx\xdab\x00\x02\x00\x00\x05\x00\x01\r\n-\xdb\x00\x00\x00\x00IEND\xaeB`\x82' > src-tauri/icons/32x32.png
    
    # 复制到所有尺寸
    for size in 16 24 48 64 128 256; do
        cp src-tauri/icons/32x32.png src-tauri/icons/${size}x${size}.png
    done
fi

# 创建图标配置文件
echo "📋 创建图标配置..."
cat > src-tauri/icons/README.md << 'EOF'
# 图标说明

这些图标是占位符图标，用于开发阶段。

在生产环境中，应该使用：
- 32x32.png: 应用程序图标
- 128x128.png: 高分辨率图标
- 256x256.png: 超高分辨率图标
- icon.icns: macOS 图标
- icon.ico: Windows 图标

建议使用专业设计工具创建符合品牌要求的图标。
EOF

echo "✅ 图标创建完成！"
echo "📋 创建的图标文件:"
ls -la src-tauri/icons/*.png