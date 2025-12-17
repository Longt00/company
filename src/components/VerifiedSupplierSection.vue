<template>
  <section class="verified-supplier-section">
    <div class="container">
      <!-- 顶部文字内容 -->
      <div class="text-content">
        <h2 class="main-title">
          <span class="verified-badge">
            <img src="@/images/icon/verified.png">
          </span>
          <span class="supplier-text">Supplier</span>
        </h2>
        <p class="verification-statement">
          This supplier has been verified onsite by world-leading inspection company, SGS SGS Group
        </p>
      </div>

      <!-- 主全景图片查看器 -->
      <div class="main-viewer-container">
        <div class="main-viewer">
          <!-- 主全景图片 -->
          <div v-if="mainVRImage.url" class="vr-container">
            <!-- 尝试使用iframe显示VR图 -->
            <iframe
              v-if="isVRUrl(mainVRImage.url)"
              :src="mainVRImage.url"
              class="vr-iframe"
              frameborder="0"
              allowfullscreen
              @error="onMainImageError"
            ></iframe>
            <!-- 如果不是VR图，使用普通img标签 -->
            <img
              v-else
              :src="mainVRImage.url"
              :alt="mainVRImage.alt"
              class="main-panoramic-image"
              @error="onMainImageError"
            >
          </div>

          <!-- 主图片占位符 -->
          <div v-else class="main-image-placeholder">
            <div class="placeholder-icon">
              <svg width="48" height="48" viewBox="0 0 24 24" fill="none">
                <path d="M21 19V5C21 3.9 20.1 3 19 3H5C3.9 3 3 3.9 3 5V19C3 20.1 3.9 21 5 21H19C20.1 21 21 20.1 21 19ZM8.5 13.5L11 16.51L14.5 12L19 18H5L8.5 13.5Z" fill="currentColor"/>
              </svg>
            </div>
            <div class="placeholder-text">
              <h3>VR全景图片</h3>
              <p>正在加载...</p>
            </div>
          </div>

          <!-- 左上角遮罩层 - 验证信息 -->
          <!--<div class="top-left-overlay">
            <div class="verification-info">
              <div class="verified-header">
                <span class="check-icon">✓</span>
                <span class="verified-text">已验证供应商</span>
              </div>
              <div class="verification-detail">
                由世界领先的检验公司
              </div>
              <div class="verification-company">
                SGS集团现场验证
              </div>
              <span class="verification-date">{{ verificationDate }}</span>
            </div>
          </div>
          -->

          <!-- 右上角QR码 -->
          <div class="top-right-overlay">
            <div class="qr-section">
              <div class="qr-code-container">
                <img
                  v-if="qrCode.url"
                  :src="qrCode.url"
                  :alt="qrCode.alt"
                  class="qr-code"
                  @error="onQRCodeError"
                >
                <div v-else class="qr-placeholder">
                  <svg width="32" height="32" viewBox="0 0 32 32" fill="white">
                    <rect x="4" y="4" width="8" height="8" fill="white"/>
                    <rect x="20" y="4" width="8" height="8" fill="white"/>
                    <rect x="4" y="20" width="8" height="8" fill="white"/>
                    <rect x="20" y="20" width="8" height="8" fill="white"/>
                    <rect x="14" y="14" width="4" height="4" fill="white"/>
                  </svg>
                </div>
              </div>
              <div class="qr-text">扫码了解更多</div>
            </div>
          </div>

          <!-- 全屏按钮 - 独立放在main-viewer右下角 -->
          <button class="fullscreen-btn" @click="toggleFullscreen" title="全屏查看">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none">
              <path d="M8 3H5C3.89 3 3 3.89 3 5V8M21 8V5C21 3.89 20.11 3 19 3H16M16 21H19C20.11 21 21 20.11 21 19V16M8 21H5C3.89 21 3 20.11 3 19V16" stroke="white" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </button>
        </div>
      </div>
    </div>
  </section>
</template>

<script>
import qrCodeImage from '@/images/website/home/QR code.png'

export default {
  name: 'VerifiedSupplierSection',
  data() {
    return {
      activeThumbnail: 0,
      verificationDate: '2025.08.13',
      mainVRImage: {},
      qrCode: {},
      thumbnails: [],
      defaultVRUrl: ''
    }
  },
  mounted() {
    // 设置默认VR图链接
    this.defaultVRUrl = 'https://preview-lyj.aliyuncs.com/preview/4a518821e21849b9b207c1768f662702?lang=en';

    this.fetchVRImages();
    this.fetchQRCode();
    this.fetchThumbnails();
  },
  methods: {
    // 设置默认VR链接（由用户调用）
    setDefaultVRUrl(url) {
      this.defaultVRUrl = url;
    },

    // 获取默认缩略图
    getDefaultThumbnails() {
      return [
        {
          url: this.defaultVRUrl,
          alt: '主视图'
        },
        {
          url: this.defaultVRUrl,
          alt: '视角1'
        },
        {
          url: this.defaultVRUrl,
          alt: '视角2'
        },
        {
          url: this.defaultVRUrl,
          alt: '视角3'
        }
      ];
    },

    // 判断是否为VR图URL
    isVRUrl(url) {
      if (!url) return false;
      // 检查常见的VR图域名
      const vrDomains = ['aliyuncs.com', 'matterport.com', 'kuula.co', 'pano.com'];
      return vrDomains.some(domain => url.includes(domain));
    },
    // 获取工厂图片数据 - 使用媒体API
    async fetchVRImages() {
      try {
        console.log('🔄 [VerifiedSupplierSection] 使用媒体API获取工厂照片...');

        // 使用媒体API获取工厂照片
        const { getFactoryPhotos } = await import('../api/media');
        const response = await getFactoryPhotos();
        console.log('✅ [VerifiedSupplierSection] 媒体API响应:', response);

        // 检查响应格式并处理数据
        if (response && response.data && response.data.code === 200 && response.data.data && response.data.data.files) {
          const factoryPhotos = response.data.data.files;

          if (factoryPhotos.length > 0) {
            // 使用第一张图片作为主图
            const mainPhoto = factoryPhotos[0];
            this.mainVRImage = {
              url: mainPhoto.fileUrl,
              alt: mainPhoto.description || '工厂全景图片'
            };

            // 使用其他图片作为缩略图
            this.thumbnails = factoryPhotos.slice(0, 3).map((photo, index) => ({
              url: photo.fileUrl,
              alt: photo.description || `工厂视角 ${index + 1}`
            }));

            console.log(`✅ [VerifiedSupplierSection] 成功获取 ${factoryPhotos.length} 张工厂照片`);
            console.log('🖼️ [VerifiedSupplierSection] 主图:', this.mainVRImage);
            console.log('🎯 [VerifiedSupplierSection] 缩略图:', this.thumbnails);
            return;
          } else {
            console.log('⚠️ [VerifiedSupplierSection] 工厂照片分类中没有找到图片文件');
          }
        } else {
          console.warn('⚠️ [VerifiedSupplierSection] 媒体API响应格式异常:', response);
        }

      } catch (error) {
        console.error('❌ [VerifiedSupplierSection] 获取工厂照片失败:', error);
      }

      // 回退到默认数据
      console.log('📦 [VerifiedSupplierSection] 使用默认工厂图片数据');
      this.mainVRImage = {
        url: this.defaultVRUrl,
        alt: '工厂全景图片 - 生产车间'
      };
      this.thumbnails = this.getDefaultThumbnails();
    },

    // 获取QR码数据 - 使用默认数据
    fetchQRCode() {
      // 直接使用默认QR码图片，移除旧API调用
      this.qrCode = {
        url: qrCodeImage,
        alt: 'QR码 - 扫码了解更多'
      };
      console.log('使用默认QR码图片');
    },

    // 获取缩略图数据 - 使用默认数据
    fetchThumbnails() {
      // 直接使用默认缩略图，移除旧API调用
      this.thumbnails = [
        {
          url: '@/images/website/company & factory/2.png',
          alt: '供应商关联图片1'
        },
        {
          url: '@/images/website/company & factory/3.png',
          alt: '供应商关联图片2'
        }
      ];
      console.log('使用默认缩略图数据');
    },

    // 切换缩略图
    switchToThumbnail(index) {
      this.activeThumbnail = index;
      // 可以在这里添加切换主图片的逻辑
      if (this.thumbnails[index] && this.thumbnails[index].url) {
        this.mainVRImage = this.thumbnails[index];
      }
    },

    // 全屏切换
    toggleFullscreen() {
      // 如果是VR图链接，直接在新窗口打开（最可靠的方式）
      if (this.mainVRImage && this.mainVRImage.url && this.isVRUrl(this.mainVRImage.url)) {
        window.open(this.mainVRImage.url, '_blank');
        return;
      }

      // 普通图片尝试全屏
      const element = this.$el.querySelector('.main-viewer');
      if (!element) {
        console.error('找不到main-viewer元素');
        return;
      }

      // 检查当前全屏状态
      if (!this.isFullscreen()) {
        this.enterFullscreen(element);
      } else {
        this.exitFullscreen();
      }
    },

    // 检查是否处于全屏状态
    isFullscreen() {
      return !!(
        document.fullscreenElement ||
        document.webkitFullscreenElement ||
        document.mozFullScreenElement ||
        document.msFullscreenElement
      );
    },

    // 进入全屏模式
    enterFullscreen(element) {
      if (element.requestFullscreen) {
        element.requestFullscreen().catch(err => {
          console.error('无法进入全屏模式:', err);
        });
      } else if (element.webkitRequestFullscreen) {
        element.webkitRequestFullscreen();
      } else if (element.mozRequestFullScreen) {
        element.mozRequestFullScreen();
      } else if (element.msRequestFullscreen) {
        element.msRequestFullscreen();
      } else {
        console.error('浏览器不支持全屏API');
        // 备用方案：在新窗口打开VR图
        if (this.mainVRImage && this.mainVRImage.url) {
          window.open(this.mainVRImage.url, '_blank');
        }
      }
    },

    // 退出全屏模式
    exitFullscreen() {
      if (document.exitFullscreen) {
        document.exitFullscreen().catch(err => {
          console.error('退出全屏模式失败:', err);
        });
      } else if (document.webkitExitFullscreen) {
        document.webkitExitFullscreen();
      } else if (document.mozCancelFullScreen) {
        document.mozCancelFullScreen();
      } else if (document.msExitFullscreen) {
        document.msExitFullscreen();
      }
    },

    // 图片错误处理
    onMainImageError() {
      console.error('主图片加载失败');
      this.mainVRImage = {};
    },

    onQRCodeError() {
      console.error('QR码图片加载失败，使用默认图片');
      // 如果图片加载失败，使用默认QR码图片
      this.qrCode = {
        url: qrCodeImage,
        alt: 'QR码 - 扫码了解更多'
      };
    },

    onThumbnailError(index) {
      console.error(`缩略图 ${index} 加载失败`);
      this.$set(this.thumbnails, index, {
        ...this.thumbnails[index],
        url: ''
      });
    }
  }
}
</script>

<style scoped>
.verified-supplier-section {
  padding: 70px 0 30px 0; 
  background: #fff;
}

@media (max-width: 768px) {
  .verified-supplier-section {
    padding: 16px 0 12px 0 !important;
  }
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

/* 顶部文字内容 */
.text-content {
  margin-bottom: 30px;
}

@media (max-width: 768px) {
  .text-content {
    margin-bottom: 12px;
  }
}

.main-title {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 15px;
  font-size: 24px;
  font-weight: bold;
}

.verified-badge {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #3B82F6;
  font-weight: bold;
  background: #f5f5f5;
  padding: 8px 12px;
  border-radius: 6px;
}

.verified-badge img {
  width: auto;
  height: 24px;
  object-fit: contain;
}

.supplier-text {
  color: #fff;
  font-weight: bold;
}

.verification-statement {
  font-size: 14px;
  color: #fff;
  line-height: 1.5;
  margin: 0;
}

/* 主查看器容器 */
.main-viewer-container {
  position: relative;
}

.main-viewer {
  position: relative;
  width: 100%;
  height: 400px;
  border-radius: 12px;
  overflow: hidden;
  background: #f8f9fa;
}

.main-panoramic-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.vr-container {
  width: 100%;
  height: 100%;
  position: relative;
}

.vr-iframe {
  width: 100%;
  height: 100%;
  border: none;
  border-radius: 12px;
}

/* 隐藏VerifiedSupplierSection中可能的错误相关元素 */
.verified-supplier-section #sub-frame-error,
.verified-supplier-section div[id*="sub-frame"],
.verified-supplier-section div[class*="sub-frame"],
.verified-supplier-section div[id*="error"][class*="chrome"],
.verified-supplier-section div[id*="error"][class*="blocked"],
.main-viewer #sub-frame-error,
.vr-container #sub-frame-error {
  display: none !important;
  visibility: hidden !important;
  opacity: 0 !important;
  width: 0 !important;
  height: 0 !important;
  position: absolute !important;
  top: -9999px !important;
  left: -9999px !important;
  z-index: -9999 !important;
}

/* 隐藏VR相关错误元素 */
.vr-iframe[style*="sub-frame-error"],
.vr-container iframe[style*="error"],
.main-viewer iframe[style*="error"],
.verified-supplier-section iframe[style*="error"] {
  display: none !important;
  visibility: hidden !important;
}

/* 主图片占位符 */
.main-image-placeholder {
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  border: 2px dashed #dee2e6;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
  padding: 20px;
}

.placeholder-icon {
  color: #6c757d;
  margin-bottom: 15px;
  opacity: 0.7;
}

.placeholder-text h3 {
  font-size: 18px;
  font-weight: 600;
  color: #495057;
  margin-bottom: 8px;
}

.placeholder-text p {
  font-size: 14px;
  color: #6c757d;
}

/* 查看器覆盖层 */
.viewer-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  pointer-events: none;
}

.viewer-overlay > * {
  pointer-events: auto;
}

/* 验证信息 */
.verification-info {
  position: absolute;
  top: 20px;
  left: 20px;
  background: rgba(255, 255, 255, 0.95);
  padding: 8px 12px;
  border-radius: 6px;
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
  color: #333;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.check-icon {
  color: #10B981;
  font-weight: bold;
}

.verification-text {
  font-weight: 500;
}

.verification-date {
  color: #666;
  font-weight: normal;
}

/* QR码容器 */
.qr-code-container {
  position: absolute;
  top: 20px;
  right: 20px;
  background: rgba(255, 255, 255, 0.95);
  padding: 8px;
  border-radius: 6px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.qr-code {
  width: 60px;
  height: 60px;
  display: block;
}

.qr-placeholder {
  width: 60px;
  height: 60px;
  background: #f8f9fa;
  border: 1px dashed #dee2e6;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #6c757d;
}

/* 全屏按钮 */
.fullscreen-btn {
  position: absolute;
  bottom: 20px;
  right: 20px;
  background: rgba(0, 0, 0, 0.6);
  border: none;
  border-radius: 50%;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: white;
  transition: all 0.3s ease;
}

.fullscreen-btn:hover {
  background: rgba(0, 0, 0, 0.8);
  transform: scale(1.1);
}

/* 位置标签 */
.location-tag {
  position: absolute;
  bottom: 20px;
  left: 50%;
  transform: translateX(-50%);
  background: rgba(255, 255, 255, 0.95);
  padding: 6px 10px;
  border-radius: 20px;
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #333;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.tag-text {
  font-weight: 500;
}

/* 缩略图容器 */
.thumbnails-container {
  display: flex;
  gap: 15px;
  margin-top: 20px;
  justify-content: center;
}

.thumbnail-item {
  position: relative;
  cursor: pointer;
  border-radius: 8px;
  overflow: hidden;
  transition: all 0.3s ease;
}

.thumbnail-item.active {
  border: 3px solid #FF6A00;
}

.thumbnail-image {
  width: 120px;
  height: 80px;
  object-fit: cover;
  display: block;
}

.thumbnail-placeholder {
  width: 120px;
  height: 80px;
  background: #f8f9fa;
  border: 1px dashed #dee2e6;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #6c757d;
}

.thumbnail-label {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: rgba(0, 0, 0, 0.7);
  color: white;
  font-size: 10px;
  padding: 4px 6px;
  text-align: center;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .verified-supplier-section {
    padding: 15px 0 10px 0;
  }

  .container {
    padding: 0 12px;
  }

  .text-content {
    margin-bottom: 16px;
  }

  .main-title {
    font-size: 18px;
    margin-bottom: 10px;
  }

  .verification-statement {
    font-size: 12px;
  }

  .main-viewer {
    height: 280px;
  }

  .verification-info {
    top: 8px;
    left: 8px;
    padding: 5px 8px;
    font-size: 10px;
  }

  .top-right-overlay {
    padding: 10px;
    gap: 8px;
  }

  .qr-code-container {
    width: 50px;
    height: 50px;
    padding: 6px;
  }

  .qr-text {
    font-size: 10px;
  }

  .fullscreen-btn {
    bottom: 10px;
    right: 10px;
    width: 32px;
    height: 32px;
  }

  .location-tag {
    bottom: 8px;
    padding: 4px 8px;
    font-size: 10px;
  }

  .thumbnails-container {
    gap: 8px;
  }

  .thumbnail-image,
  .thumbnail-placeholder {
    width: 80px;
    height: 60px;
  }
}

@media (max-width: 480px) {
  .verified-supplier-section {
    padding: 12px 0 8px 0;
  }

  .container {
    padding: 0 10px;
  }

  .text-content {
    margin-bottom: 12px;
  }

  .main-title {
    font-size: 16px;
    gap: 6px;
  }

  .verified-badge {
    padding: 6px 8px;
  }

  .verified-badge img {
    width: auto;
    height: 18px;
    object-fit: contain;
  }

  .verification-statement {
    font-size: 11px;
  }

  .main-viewer {
    height: 220px;
    border-radius: 8px;
  }

  .top-right-overlay {
    padding: 8px;
    gap: 6px;
  }

  .qr-code-container {
    width: 44px;
    height: 44px;
    padding: 5px;
  }

  .fullscreen-btn {
    width: 28px;
    height: 28px;
  }

  .thumbnail-image,
  .thumbnail-placeholder {
    width: 70px;
    height: 50px;
  }
}

/* 左上角遮罩层样式 */
.top-left-overlay {
  position: absolute;
  top: 0;
  left: 0;
  background: rgba(0,0,0,0.8);
  backdrop-filter: blur(10px);
  padding: 25px 20px;
  color: white;
  z-index: 10;
  max-width: 280px;
}

/* 右上角QR码遮罩层 */
.top-right-overlay {
  position: absolute;
  top: 0;
  right: 0;
  padding: 20px;
  color: white;
  z-index: 10;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 15px;
}

.verification-info {
  margin-bottom: 0;
}

.verified-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
}

.check-icon {
  color: #28a745;
  font-weight: bold;
  font-size: 16px;
  flex-shrink: 0;
}

.verified-text {
  font-weight: bold;
  font-size: 16px;
}

.verification-detail {
  font-size: 14px;
  line-height: 1.4;
  margin-bottom: 4px;
  opacity: 0.9;
}

.verification-company {
  font-size: 14px;
  line-height: 1.4;
  margin-bottom: 12px;
  opacity: 0.9;
}

.verification-date {
  font-size: 12px;
  opacity: 0.7;
  display: block;
}

.qr-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.qr-code-container {
  background: white;
  border-radius: 8px;
  padding: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 80px;
  height: 80px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.3);
}

.qr-code {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.qr-placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
}

.qr-text {
  font-size: 12px;
  color: white;
  text-align: center;
  font-weight: 500;
  margin: 0;
}

/* 全屏按钮 - 放在main-viewer右下角 */
.fullscreen-btn {
  position: absolute;
  bottom: 15px;
  right: 15px;
  background: rgba(0, 0, 0, 0.6);
  border: none;
  border-radius: 8px;
  width: 44px;
  height: 44px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: white;
  transition: all 0.3s ease;
  z-index: 100;
}

.fullscreen-btn:hover {
  background: rgba(0, 0, 0, 0.8);
  transform: scale(1.1);
}

</style>

