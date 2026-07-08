#!/bin/bash

# Android项目构建脚本（带自动签名功能）

echo "开始构建Android时间显示应用..."

# 设置构建路径
APP_DIR="./app"
SRC_DIR="$APP_DIR/src/main"
RES_DIR="$SRC_DIR/res"
MANIFEST="$SRC_DIR/AndroidManifest.xml"
JAVA_DIR="$SRC_DIR/java"
OUT_DIR="./build_output"

# 创建输出目录
rm -rf "$OUT_DIR"
mkdir -p "$OUT_DIR"
mkdir -p "$OUT_DIR/classes"
mkdir -p "$OUT_DIR/res"
mkdir -p "$OUT_DIR/META-INF"
mkdir -p "$OUT_DIR/gen"

# 检查Java是否安装
if ! command -v java &> /dev/null; then
    echo "错误：未找到Java，请确保已安装JDK 8或更高版本"
    exit 1
fi

# 检查Android SDK是否配置
if [ -z "$ANDROID_HOME" ]; then
    echo "错误：ANDROID_HOME环境变量未设置"
    exit 1
fi

echo "Android SDK路径：$ANDROID_HOME"
echo "应用目录：$APP_DIR"
echo "输出目录：$OUT_DIR"

# 设置构建工具路径
BUILD_TOOLS_DIR="$ANDROID_HOME/build-tools/35.0.0"
PLATFORM_DIR="$ANDROID_HOME/platforms/android-34"

# 检查构建工具是否存在
if [ ! -d "$BUILD_TOOLS_DIR" ]; then
    echo "错误：未找到build-tools 35.0.0"
    exit 1
fi

if [ ! -d "$PLATFORM_DIR" ]; then
    echo "错误：未找到android-34平台"
    exit 1
fi

echo "使用build-tools版本：35.0.0"
echo "使用Android平台版本：34"

# 编译资源并生成R.java
echo "编译资源..."
AAPT2="$BUILD_TOOLS_DIR/aapt2"

# 创建临时资源目录，合并应用资源和注册库资源
MERGED_RES_DIR="$OUT_DIR/merged_res"
mkdir -p "$MERGED_RES_DIR"

# 复制应用资源
cp -r "$RES_DIR"/* "$MERGED_RES_DIR/" 2>/dev/null || true

# 从注册库源码目录复制必要的资源（只复制 reggate 相关资源，避免覆盖应用主题）
REG_LIB_SRC="/Users/yy/pro-test/anddex/registration-lib/src/main/res"
if [ -d "$REG_LIB_SRC" ]; then
    echo "合并注册库资源..."
    
    # 复制布局文件（reggate_activity_*.xml）
    mkdir -p "$MERGED_RES_DIR/layout"
    cp "$REG_LIB_SRC/layout/reggate_activity_"*.xml "$MERGED_RES_DIR/layout/" 2>/dev/null || true
    
    # 复制 values 目录中的 reggate 资源（字符串、颜色、主题）
    mkdir -p "$MERGED_RES_DIR/values"
    cp "$REG_LIB_SRC/values/strings.xml" "$MERGED_RES_DIR/values/reggate_strings.xml" 2>/dev/null || true
    cp "$REG_LIB_SRC/values/colors.xml" "$MERGED_RES_DIR/values/reggate_colors.xml" 2>/dev/null || true
    cp "$REG_LIB_SRC/values/themes.xml" "$MERGED_RES_DIR/values/reggate_themes.xml" 2>/dev/null || true
    
    # 复制 raw 目录中的公钥文件
    mkdir -p "$MERGED_RES_DIR/raw"
    cp "$REG_LIB_SRC/raw/"* "$MERGED_RES_DIR/raw/" 2>/dev/null || true
    
    # 复制 drawable 目录中的资源（二维码）
    mkdir -p "$MERGED_RES_DIR/drawable"
    cp "$REG_LIB_SRC/drawable/"* "$MERGED_RES_DIR/drawable/" 2>/dev/null || true
    
    # 复制 assets 目录中的配置文件
    REG_LIB_ASSETS="/Users/yy/pro-test/anddex/registration-lib/src/main/assets"
    if [ -d "$REG_LIB_ASSETS" ]; then
        mkdir -p "$OUT_DIR/assets"
        cp "$REG_LIB_ASSETS/"* "$OUT_DIR/assets/" 2>/dev/null || true
    fi
else
    echo "警告：未找到注册库资源目录"
fi

# 编译合并后的资源
"$AAPT2" compile --dir "$MERGED_RES_DIR" -o "$OUT_DIR/res/resources.zip"

if [ $? -ne 0 ]; then
    echo "错误：资源编译失败"
    exit 1
fi

# 链接资源并生成R.java
"$AAPT2" link \
    -o "$OUT_DIR/app.apk" \
    -I "$PLATFORM_DIR/android.jar" \
    --manifest "$MANIFEST" \
    --java "$OUT_DIR/gen" \
    --no-version-vectors \
    "$OUT_DIR/res/resources.zip"

if [ $? -ne 0 ]; then
    echo "错误：资源链接失败"
    exit 1
fi

echo "资源编译和链接成功！"

# 编译Java代码
echo "编译Java代码..."
JAVAC="$(which javac)"
if [ -z "$JAVAC" ]; then
    echo "错误：未找到javac命令"
    exit 1
fi

# 使用项目中预下载的注册库
REG_LIB_DIR="$APP_DIR/../libs"
REG_LIB_JAR="$REG_LIB_DIR/registration-lib.jar"

if [ -f "$REG_LIB_JAR" ]; then
    echo "使用项目中的注册库"
else
    echo "错误：未找到注册库"
    exit 1
fi

# 收集所有Java文件
JAVA_FILES=$(find "$JAVA_DIR" -name "*.java")
GEN_JAVA_FILES=$(find "$OUT_DIR/gen" -name "*.java")

# 编译Java文件
CLASSPATH="$PLATFORM_DIR/android.jar:$OUT_DIR/classes:$REG_LIB_JAR:$REG_LIB_DIR/jetified-security-crypto-1.1.0-alpha06-runtime.jar:$REG_LIB_DIR/tink-android-1.8.0.jar"

"$JAVAC" -d "$OUT_DIR/classes" \
    -classpath "$CLASSPATH" \
    --release 8 \
    $JAVA_FILES $GEN_JAVA_FILES

if [ $? -ne 0 ]; then
    echo "错误：Java编译失败"
    exit 1
fi

echo "Java编译成功！"

# 打包DEX文件
echo "打包DEX文件..."
D8="$BUILD_TOOLS_DIR/d8"

# 收集所有.class文件
CLASS_FILES=$(find "$OUT_DIR/classes" -name "*.class")

# 打包dex
"$D8" \
    --classpath "$PLATFORM_DIR/android.jar" \
    $CLASS_FILES \
    "$REG_LIB_JAR" \
    "$REG_LIB_DIR/jetified-security-crypto-1.1.0-alpha06-runtime.jar" \
    "$REG_LIB_DIR/tink-android-1.8.0.jar" \
    --output "$OUT_DIR"

if [ $? -ne 0 ]; then
    echo "错误：DEX打包失败"
    exit 1
fi

echo "DEX打包成功！"

# 使用aapt2创建完整的APK文件
echo "创建完整APK文件..."
cd "$OUT_DIR"

# 创建临时目录用于构建APK
mkdir -p "temp_apk"
cd "temp_apk"

# 解压app.apk获取资源文件
unzip -q "../app.apk"

# 复制classes.dex文件
cp "../classes.dex" .

# 删除META-INF目录（如果存在）
rm -rf "META-INF"

# 如果存在assets目录，复制到temp_apk
if [ -d "../assets" ]; then
    cp -r "../assets" .
fi

# 使用zip命令创建APK，确保resources.arsc未压缩
zip -0 -r "../app-unaligned.apk" resources.arsc
zip -r "../app-unaligned.apk" AndroidManifest.xml classes.dex res/ assets/ 2>/dev/null || true

if [ $? -ne 0 ]; then
    echo "错误：APK打包失败"
    exit 1
fi

# 清理临时目录
cd ..
rm -rf "temp_apk"

cd ..

echo "APK打包成功！"

# 检查并生成调试密钥
echo "检查调试密钥..."
DEBUG_KEY_DIR="~/.android"
DEBUG_KEY="$DEBUG_KEY_DIR/debug.keystore"

# 确保调试密钥目录存在
mkdir -p "$DEBUG_KEY_DIR"

# 展开~符号
DEBUG_KEY=$(eval echo "$DEBUG_KEY")
DEBUG_KEY_DIR=$(eval echo "$DEBUG_KEY_DIR")

# 检查调试密钥是否存在
if [ ! -f "$DEBUG_KEY" ]; then
    echo "未找到调试密钥，正在生成..."
    
    # 生成调试密钥
    keytool -genkey -v -keystore "$DEBUG_KEY" \
        -alias androiddebugkey \
        -keyalg RSA \
        -keysize 2048 \
        -validity 10000 \
        -dname "CN=Android Debug,O=Android,C=US" \
        -storepass android \
        -keypass android
    
    if [ $? -ne 0 ]; then
        echo "错误：生成调试密钥失败"
        exit 1
    fi
    
    echo "调试密钥生成成功！"
else
    echo "调试密钥已存在，使用现有密钥"
fi

# 对齐APK
echo "对齐APK..."
ZIPALIGN="$BUILD_TOOLS_DIR/zipalign"

"$ZIPALIGN" -f -v 4 "$OUT_DIR/app-unaligned.apk" "$OUT_DIR/app-aligned.apk"

if [ $? -ne 0 ]; then
    echo "错误：APK对齐失败"
    exit 1
fi

# 签名APK
echo "签名APK..."
APKSIGNER="$BUILD_TOOLS_DIR/apksigner"

"$APKSIGNER" sign \
    --ks "$DEBUG_KEY" \
    --ks-pass pass:android \
    --key-pass pass:android \
    "$OUT_DIR/app-aligned.apk"

if [ $? -ne 0 ]; then
    echo "错误：APK签名失败"
    exit 1
fi

echo "APK签名和对齐成功！"
echo "最终APK文件：$OUT_DIR/app-aligned.apk"
echo ""

# 检查是否需要安装APK
if [ "$1" = "install" ]; then
    echo "正在安装APK..."
    adb install "$OUT_DIR/app-aligned.apk"
    
    if [ $? -eq 0 ]; then
        echo "APK安装成功！"
    else
        echo "错误：APK安装失败"
        echo "请确保设备已连接并已启用USB调试模式"
    fi
else
    echo "要安装APK，请运行："
    echo "adb install $OUT_DIR/app-aligned.apk"
    echo ""
    echo "或者运行：./build.sh install"
fi

echo "构建完成！"
