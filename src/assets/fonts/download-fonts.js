// 阿里巴巴普惠体字体下载脚本
// 可以在开发时运行此脚本来下载字体文件

const fs = require('fs');
const path = require('path');

// 字体文件下载链接
const fontUrls = {
  regular: 'https://puhuiti.oss-cn-hangzhou.aliyuncs.com/AlibabaPuHuiTi-2/AlibabaPuHuiTi-2-45-Regular/AlibabaPuHuiTi-2-45-Regular.woff2',
  bold: 'https://puhuiti.oss-cn-hangzhou.aliyuncs.com/AlibabaPuHuiTi-2/AlibabaPuHuiTi-2-65-Bold/AlibabaPuHuiTi-2-65-Bold.woff2',
  medium: 'https://puhuiti.oss-cn-hangzhou.aliyuncs.com/AlibabaPuHuiTi-2/AlibabaPuHuiTi-2-55-Medium/AlibabaPuHuiTi-2-55-Medium.woff2',
  heavy: 'https://puhuiti.oss-cn-hangzhou.aliyuncs.com/AlibabaPuHuiTi-2/AlibabaPuHuiTi-2-75-Heavy/AlibabaPuHuiTi-2-75-Heavy.woff2',
  light: 'https://puhuiti.oss-cn-hangzhou.aliyuncs.com/AlibabaPuHuiTi-2/AlibabaPuHuiTi-2-35-Light/AlibabaPuHuiTi-2-35-Light.woff2'
};

async function downloadFont(url, filename) {
  try {
    const response = await fetch(url);
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    const buffer = await response.arrayBuffer();
    fs.writeFileSync(filename, Buffer.from(buffer));
    console.log(`Downloaded ${filename}`);
  } catch (error) {
    console.error(`Error downloading ${filename}:`, error);
  }
}

async function downloadAllFonts() {
  const fontsDir = path.join(__dirname);

  console.log('开始下载阿里巴巴普惠体字体文件...');

  for (const [weight, url] of Object.entries(fontUrls)) {
    const filename = path.join(fontsDir, `AlibabaPuHuiTi-${weight.charAt(0).toUpperCase() + weight.slice(1)}.woff2`);
    await downloadFont(url, filename);
  }

  console.log('字体文件下载完成！');
}

// 如果直接运行此脚本
if (require.main === module) {
  downloadAllFonts().catch(console.error);
}

module.exports = { downloadAllFonts };