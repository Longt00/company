<template>
  <div class="company-info-section">

    <!-- 公司信息条带 -->
    <div class="company-info-bar">
      <div class="container">
        <div class="company-info-content">
        <!-- 子容器 -->
        <div class="company-info-wrapper">

            <!-- 公司信息 -->
            <div class="company-details">
              <!-- 公司Logo和名称 -->
              <div class="company-header">
                <div class="company-logo">
                  <img
                    :src="logoUrl || defaultLogo"
                    alt="Company Logo"
                    class="logo-image"
                    @error="handleLogoError"
                  />
                </div>
                <div class="company-name-section">
                  <div
                    class="company-info-text"
                    v-html="companyInfoText.replace(/\n/g, '<br>')"
                    style="white-space: pre-wrap"
                  ></div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { getHomeCompanyInfo } from '../api/richContent'
import { getMediaFiles } from '../api/media'
import defaultLogo from '@/images/logo/logo.png'

export default {
  name: 'CompanyInfoSection',
  data() {
    return {
      isDropdownVisible: false,
      // 直接显示文本内容，不解析
      companyInfoText: '',
      isLoading: true,
      logoUrl: null,
      defaultLogo
    }
  },
  computed: {
    showDropdown() {
      return this.isDropdownVisible
    }
  },
  async mounted() {
    await Promise.all([
      this.fetchCompanyInfo(),
      this.fetchCompanyLogo()
    ])
  },
  methods: {
    async fetchCompanyInfo() {
      try {
        this.isLoading = true
        const response = await getHomeCompanyInfo()

        if (
          response &&
          response.code === 200 &&
          response.data &&
          response.data.length > 0
        ) {
          this.companyInfoText = response.data[0].content || ''
          console.log('✅ [CompanyInfoSection] 成功加载公司信息')
        } else {
          console.warn('⚠️ [CompanyInfoSection] API响应格式异常')
        }
      } catch (error) {
        console.error('❌ [CompanyInfoSection] API连接失败:', error)
      } finally {
        this.isLoading = false
      }
    },
    async fetchCompanyLogo() {
      try {
        // 使用媒体API获取logo分类的文件列表
        const response = await getMediaFiles('logo')
        console.log('🖼️ [CompanyInfoSection] Logo接口响应:', response?.data)

        const resData = response?.data
        if (resData && resData.code === 200 && resData.data) {
          const { files } = resData.data
          
          // 从文件列表中获取最新上传的Logo（按id降序，取最大的）
          if (files && Array.isArray(files) && files.length > 0) {
            const latestFile = files.reduce((latest, file) => 
              file.id > latest.id ? file : latest
            , files[0])
            
            // 构建相对路径 URL
            const storageName = latestFile.storageName || latestFile.filePath
            if (storageName) {
              this.logoUrl = `/api/files/${storageName}`
              console.log('✅ [CompanyInfoSection] 获取最新Logo:', this.logoUrl)
            }
          }
        }
      } catch (error) {
        console.error('❌ [CompanyInfoSection] 获取公司Logo失败:', error)
        this.logoUrl = null
      }
    },
    handleLogoError(event) {
      // 图片加载失败时回退到本地默认Logo
      console.error('❌ [CompanyInfoSection] Logo图片加载失败，URL:', event.target.src)
      console.error('❌ [CompanyInfoSection] 回退到默认Logo')
      this.logoUrl = null
      event.target.src = defaultLogo
    },
    toggleDropdown() {
      this.isDropdownVisible = !this.isDropdownVisible
    },
    showDropdownMenu() {
      this.isDropdownVisible = true
    },
    hideDropdown() {
      this.isDropdownVisible = false
    },
  },
}
</script>

<style scoped>
.company-info-section {
  background: #f8f9fa; /* 使用导航栏背景色 */
  position: relative;
  margin: 0; /* 移除section的外边距 */
  padding: 0; /* 移除section的内边距 */
}

.background-image-container {
  background-image: url('../images/background/background-header.jpg');
  background-size: cover;
  background-position: center bottom;
  background-repeat: no-repeat;
  height: 500px; /* 背景图片高度 */
  position: relative;
}

.company-info-bar {
  background-image: url('../images/background/background-header.jpg');
  background-size: cover;
  background-position: center bottom;
  background-repeat: no-repeat;
  position: relative;
  margin: 0; /* 移除条带的外边距 */
}

.company-info-content {
  display: flex;
  align-items: center;
  justify-content: space-between; /* 左右分布 */
  padding: 20px 0;
  gap: 20px;
  position: relative;
  z-index: 2;
  margin: 0; /* 确保没有外边距 */
}

/* WhatsApp 容器：仅作为占位，不再控制浮窗定位 */
.whatsapp-container {
  position: static;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  position: relative;
  z-index: 1;
  height: 300px; /* 容器高度扩大100px */
}

/* 子容器样式 */
.company-info-wrapper {
  width: 100%;
  height: 100%;
  position: relative;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-left: -100px; /* 向左移动100px */
  transform: translateY(20px); /* 向下移动20px */
}

.company-info-content {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  padding: 45px 0;
  gap: 10px;
}

.company-details {
  flex: 1;
}

.company-header {
  display: flex;
  align-items: flex-start;
  gap: 20px;
  margin-bottom: 20px;
}

.company-logo {
  width: 110px;
  height: 110px;
  background: #f8f9fa;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  border: 2px solid #ffffff;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.logo-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.company-name-section {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 0px;
  flex: 1;
}

.company-info-text {
  font-size: 17px;
  font-weight: 500;
  color: #ffffff;
  line-height: 1.6;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.5);
}

.right-section {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 16px;
}

.sgs-verification {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  padding: 8px 12px;
  background: #f0fdf4;
  border: 1px solid #bbf7d0;
  border-radius: 4px;
}

.by-sgs {
  color: #6b7280;
}

.action-buttons {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

/* 下拉弹窗样式 */
.dropdown-modal {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  background: transparent;
  display: flex;
  align-items: flex-start;
  justify-content: flex-start;
  z-index: 2000;
  padding-top: 10px;
  pointer-events: none;
}

.dropdown-modal .modal-content {
  pointer-events: auto;
}

.modal-content {
  background: white;
  border-radius: 8px;
  padding: 25px;
  max-width: 480px;
  width: 90%;
  max-height: 80vh;
  overflow-y: auto;
  box-shadow: 0 10px 15px -3px rgba(0, 0, 0, 0.1),
    0 4px 6px -2px rgba(0, 0, 0, 0.05);
  position: relative;
  border: 1px solid #e5e7eb;
  margin-left: 80px;
}

.verified-banner {
  background: linear-gradient(135deg, #e0f2fe 0%, #f0f9ff 100%);
  color: #1e40af;
  padding: 8px 16px;
  border-radius: 6px;
  font-weight: bold;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 20px;
  text-align: center;
  justify-content: center;
}

.supplier-name {
  font-size: 20px;
  font-weight: bold;
  color: #374151;
  margin: 0 0 8px 0;
}

.supplier-type {
  font-size: 16px;
  font-weight: bold;
  color: #1e40af;
  margin: 0 0 15px 0;
}

.badges {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 15px;
}

.country-badge {
  background: #dc2626;
  color: white;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: bold;
  display: flex;
  align-items: center;
  gap: 4px;
}

.china-flag {
  font-size: 12px;
}

.years-badge {
  background: #f3f4f6;
  color: #374151;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: bold;
}

.premium-icon {
  background: #fcd34d;
  color: white;
  width: 24px;
  height: 24px;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  font-size: 14px;
}

.supplier-index {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #374151;
  font-size: 14px;
  margin-bottom: 20px;
}

.diamond-icon {
  color: #d1d5db;
}

.chevron {
  color: #9ca3af;
}

.metrics-section {
  border-top: 1px solid #d1d5db;
  border-bottom: 1px solid #d1d5db;
  padding: 15px 0;
  margin-bottom: 15px;
}

.metrics-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 15px;
}

.metrics-row:last-child {
  margin-bottom: 0;
}

.metric {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.metric-label {
  color: #374151;
  font-size: 14px;
}

.metric-value {
  color: #374151;
  font-size: 16px;
  font-weight: bold;
  text-decoration: underline;
}

.services-section {
  margin-bottom: 15px;
  padding-bottom: 15px;
  border-bottom: 1px solid #d1d5db;
}

.quality-section {
  margin-bottom: 20px;
}

.section-title {
  color: #374151;
  font-size: 14px;
  font-weight: normal;
  margin: 0 0 10px 0;
}

.services-list,
.quality-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.services-list li,
.quality-list li {
  color: #374151;
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 5px;
}

.bottom-links {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 15px;
}

.supplier-assessments {
  color: #374151;
  text-decoration: underline;
  font-size: 14px;
}

.contact-detail-btn {
  background: none;
  border: none;
  color: #3b82f6;
  font-weight: bold;
  text-decoration: underline;
  font-size: 16px;
  cursor: pointer;
}

.contact-detail-btn:hover {
  color: #1e40af;
}

@media (max-width: 768px) {
  .background-image-container {
    height: auto;
    min-height: 180px;
    background-size: cover;
    background-position: center;
  }

  .container {
    height: auto;
    min-height: 160px;
    padding: 16px;
  }

  .company-info-wrapper {
    margin-left: 0;
    transform: none;
  }

  .company-info-content {
    padding: 16px 0;
    gap: 12px;
    flex-direction: column;
    align-items: flex-start;
  }

  .company-header {
    flex-direction: row;
    align-items: center;
    gap: 12px;
    width: 100%;
  }

  .company-logo {
    width: 70px;
    height: 70px;
    flex-shrink: 0;
  }

  .company-name-section {
    flex: 1;
    min-width: 0;
  }

  .company-info-text {
    font-size: 13px;
    line-height: 1.4;
    word-wrap: break-word;
    overflow-wrap: break-word;
  }

  .right-section {
    align-items: flex-start;
  }

  .modal-content {
    padding: 16px;
    margin: 12px;
    max-width: calc(100vw - 24px);
  }

  /* 移动端：WhatsAppFloat 自身已使用 position: fixed，这里不再额外定位容器 */
}

@media (max-width: 480px) {
  .background-image-container {
    min-height: 140px;
  }

  .container {
    min-height: 130px;
    padding: 12px;
  }

  .company-info-content {
    padding: 12px 0;
    gap: 10px;
  }

  .company-header {
    gap: 10px;
  }

  .company-logo {
    width: 56px;
    height: 56px;
  }

  .company-info-text {
    font-size: 12px;
    line-height: 1.35;
  }

  /* 小屏幕：同样不再重写 WhatsApp 浮窗位置 */
}

/* 超小屏幕优化 */
@media (max-width: 360px) {
  .company-logo {
    width: 48px;
    height: 48px;
  }

  .company-info-text {
    font-size: 11px;
  }

  .container {
    padding: 10px;
  }
}
</style>
