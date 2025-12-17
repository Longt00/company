<template>
  <section class="virtual-reality-showroom">
    <div class="container">
      <!-- 标题部分 -->
      <div class="section-header">
        <h2 class="vr-title">Virtual Reality Showroom</h2>
      </div>

      <!-- VR展示区域 -->
      <div class="showroom-content">
        <div class="vr-section">
          <div class="vr-container">
            <!-- VR图占位符 -->
            <div class="vr-placeholder">
              <iframe
                v-if="vrUrl && !vrLoadError"
                :src="vrUrl"
                class="vr-iframe"
                frameborder="0"
                allowfullscreen
                @error="handleVRError"
              ></iframe>
              <div v-else class="vr-placeholder-content">
                <div class="vr-icon">
                  <svg width="40" height="40" viewBox="0 0 24 24" fill="none">
                    <path d="M21 16V8C21 5.79 19.21 4 17 4H7C4.79 4 3 5.79 3 8V16C3 18.21 4.79 20 7 20H17C19.21 20 21 18.21 21 16ZM12 12L9 16H15L12 12Z" fill="#999"/>
                    <circle cx="8" cy="8" r="1" fill="#999"/>
                    <circle cx="16" cy="8" r="1" fill="#999"/>
                  </svg>
                </div>
                <div class="vr-text">VR Showroom</div>
                <div class="vr-subtitle">360° Virtual Tour</div>
              </div>
            </div>
          </div>
          <!-- VR图下方文字和按钮 -->
          <div class="vr-description-section">
            <div class="vr-description-text">
              20+ years in OEM/ODM & one-stop customizationShowroom
            </div>
            <button class="vr-visit-button" @click="openVRShowroom">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none">
                <path d="M19 19H5V5h7V3H5c-1.11 0-2 .9-2 2v14c0 1.1.89 2 2 2h14c1.1 0 2-.9 2-2v-7h-2v7zM14 3v2h3.59l-9.83 9.83 1.41 1.41L19 6.41V10h2V3h-7z" fill="currentColor"/>
              </svg>
              Visit VR Showroom
            </button>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<script>
export default {
  name: 'VirtualRealityShowroom',
  data() {
    return {
      vrUrl: 'https://c-jeans.en.alibaba.com/view/showroom/immersed.htm?model_id=6711050&member_id=234508585&ali_id=4086174964&vaccount_id=243775453&wx_navbar_transparent=true&_aplus_page_enable=true&model=ailab&model_source=ailab&oss_key=91fb889c-28db-4b7d-a1aa-e31ae08c5950', // VR展厅链接
      vrLoadError: false
    }
  },
  methods: {
    // 处理VR加载错误
    handleVRError() {
      console.log('VR展厅加载失败，显示备用内容');
      this.vrLoadError = true;
    },

    // 打开VR展厅链接（直接跳转）
    openVRShowroom() {
      console.log('🖱️ 点击VR展厅按钮，准备跳转到:', this.vrUrl);

      if (this.vrUrl) {
        // 直接在新窗口打开链接
        window.open(this.vrUrl, '_blank');
        console.log('✅ VR展厅链接已在新窗口打开');
      } else {
        console.warn('⚠️ VR展厅链接不存在');
      }
    }
  },
  mounted() {
    // VR展厅链接已设置
    console.log('Virtual Reality Showroom 组件已加载');
    console.log('VR展厅链接:', this.vrUrl);
  }
}
</script>

<style scoped>
.virtual-reality-showroom {
  padding: 60px 0;
  background: #f8f9fa;
}

.container {
  max-width: 1625px;
  margin: 0 auto;
  padding: 0 20px;
}

/* 标题部分 */
.section-header {
  text-align: center;
  margin-bottom: 30px;
  margin-left: 0;
}

.vr-title {
  font-size: 28px;
  font-weight: 700;
  color: #fff;
  margin: 0 0 12px 0;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

/* VR展示区域 - 居中布局 */
.showroom-content {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  margin: 0 auto;
}

.vr-section {
  width: 900px;
  height: 600px;
  display: flex;
  flex-direction: column;
  margin: 0 auto;
}

.vr-container {
  position: relative;
  width: 900px;
  height: 520px;
  border-radius: 8px;
  overflow: hidden;
  background: #fff;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.15);
}

.vr-container-wrapper {
  position: relative;
  width: 100%;
  height: 100%;
  cursor: pointer;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.vr-container-wrapper:hover {
  transform: scale(1.02);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.2);
}

.vr-description-section {
  margin-top: 15px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
}

.vr-description-text {
  text-align: center;
  font-size: 15px;
  color: #fff;
  line-height: 1.4;
}

.vr-visit-button {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  background: #ff6a00;
  color: white;
  border: none;
  border-radius: 25px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(255, 106, 0, 0.3);
}

.vr-visit-button:hover {
  background: #e55a00;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255, 106, 0, 0.4);
}

.vr-visit-button:active {
  transform: translateY(0);
  box-shadow: 0 2px 6px rgba(255, 106, 0, 0.3);
}

.vr-visit-button svg {
  flex-shrink: 0;
}



.vr-iframe {
  width: 100%;
  height: 100%;
  border: none;
}

/* 隐藏VR展厅中可能的错误相关元素 */
.vr-iframe[style*="sub-frame-error"],
.vr-container iframe[style*="error"],
.vr-placeholder iframe[style*="error"],
.vr-container div[class*="error"],
.vr-placeholder div[class*="error"] {
  display: none !important;
  visibility: hidden !important;
}

/* 强制隐藏任何可能的sub-frame-error相关元素 */
#sub-frame-error,
div[id*="sub-frame"],
div[class*="sub-frame"],
div[id*="error"][class*="chrome"],
div[id*="error"][class*="blocked"] {
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

/* 确保VR容器内的错误页面被隐藏 */
.vr-container #sub-frame-error,
.vr-placeholder #sub-frame-error,
.vr-container [id*="error"],
.vr-placeholder [id*="error"] {
  display: none !important;
  visibility: hidden !important;
  opacity: 0 !important;
}

.vr-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #fafafa;
  border: 1px solid #eee;
  cursor: pointer;
  transition: all 0.3s ease;
}



.vr-placeholder-content {
  text-align: center;
  color: #999;
}



.vr-icon {
  margin-bottom: 6px;
}

.vr-text {
  font-size: 14px;
  font-weight: 500;
  margin-bottom: 2px;
}

.vr-subtitle {
  font-size: 10px;
  opacity: 0.8;
  margin-bottom: 16px;
}

/* 错误信息样式 */
.vr-error-info {
  margin-top: 16px;
  text-align: center;
}

.vr-error-text {
  color: #dc3545;
  font-size: 14px;
  margin-bottom: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
}

.vr-error-actions {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.btn-retry {
  background-color: #007bff;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 6px;
  font-size: 12px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 6px;
  transition: background-color 0.3s;
}

.btn-retry:hover {
  background-color: #0056b3;
}

.vr-error-tip {
  font-size: 11px;
  color: #6c757d;
  font-style: italic;
}

.vr-iframe {
  width: 100%;
  height: 100%;
  border: none;
}

.vr-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #fafafa;
  border: 1px solid #eee;
  cursor: pointer;
  transition: all 0.3s ease;
}

.vr-placeholder:hover {
  transform: scale(1.02);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.1);
}

.vr-placeholder-content {
  text-align: center;
  color: #999;
}

.vr-icon {
  margin-bottom: 6px;
}

.vr-text {
  font-size: 14px;
  font-weight: 500;
  margin-bottom: 2px;
}

.vr-subtitle {
  font-size: 10px;
  opacity: 0.8;
  margin-bottom: 16px;
}






/* 响应式设计 */
@media (max-width: 1200px) {
  .vr-section {
    width: 100%;
    max-width: 900px;
    margin: 0 auto;
  }

  .vr-container {
    width: 100%;
    height: 400px;
  }
}

@media (max-width: 768px) {
  .virtual-reality-showroom {
    padding: 24px 0;
  }

  .container {
    padding: 0 12px;
  }

  .section-header {
    margin-bottom: 16px;
  }

  .vr-title {
    font-size: 20px;
    margin-bottom: 8px;
  }

  .vr-section {
    width: 100%;
    height: auto;
  }

  .vr-container {
    width: 100%;
    height: 220px;
    border-radius: 8px;
  }

  
  .vr-description-section {
    margin-top: 12px;
    gap: 10px;
  }

  .vr-description-text {
    font-size: 12px;
    padding: 0 10px;
  }

  .vr-visit-button {
    padding: 10px 20px;
    font-size: 13px;
  }
}

@media (max-width: 480px) {
  .virtual-reality-showroom {
    padding: 20px 0;
  }

  .section-header {
    margin-bottom: 12px;
  }

  .vr-title {
    font-size: 18px;
  }

  .vr-container {
    height: 180px;
    border-radius: 6px;
  }

  .vr-description-section {
    margin-top: 10px;
    gap: 8px;
  }

  .vr-description-text {
    font-size: 11px;
  }

  .vr-visit-button {
    padding: 8px 16px;
    font-size: 12px;
    border-radius: 20px;
  }
}

@media (max-width: 360px) {
  .vr-container {
    height: 160px;
  }

  .vr-title {
    font-size: 16px;
  }

  .vr-description-text {
    font-size: 10px;
  }
}
</style>