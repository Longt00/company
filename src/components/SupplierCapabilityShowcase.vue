<template>
  <section class="supplier-capability-showcase">
    <div class="container">

      <div class="showcase-layout">

        <!-- 左侧公司信息区 -->
        <div class="capability-section">
          <!-- 公司联系信息卡片 -->
          <div class="contact-card">
            <!-- 加载状态 -->
            <div v-if="loading" class="loading-container">
              <div class="loading-spinner"></div>
              <p>正在加载公司信息...</p>
            </div>

            <!-- 错误状态 -->
            <div v-else-if="error" class="error-container">
              <div class="error-icon">⚠️</div>
              <h3>加载失败</h3>
              <p>{{ error }}</p>
              <button @click="loadCompanyData" class="retry-btn">重试</button>
            </div>

            <!-- 正常内容 -->
            <div v-else class="contact-content">
              <div class="company-header">
                <img
                  :src="logoUrl || defaultLogo1"
                  alt="Company Logo"
                  class="company-logo"
                  @error="handleLogoError"
                />
                <h2 class="card-title">{{ companyName }}</h2>
              </div>

              <!-- 公司简介 -->
              <div class="company-description" v-if="companyInfo && companyInfo.companyDescription">
                <p>{{ companyInfo.companyDescription }}</p>
              </div>
              <div class="company-description" v-else>
                <p>Guangzhou Kaicheng Garment Co., Ltd. is a professional denim apparel manufacturer and exporter with over 15 years of experience in the fashion industry. We specialize in producing high-quality denim jeans, jackets, shirts, and custom apparel for clients worldwide.</p>
                <p>Our factory is equipped with advanced production lines and a skilled workforce of over 200 employees, ensuring the highest quality standards and timely delivery for all our B2B partners. We offer OEM/ODM services and welcome custom orders tailored to your specific requirements.</p>
              </div>

              <!-- 联系方式 -->
              <div class="contact-methods">
                <div class="contact-column">
                  <div class="contact-item">
                    <div class="contact-icon">
                      <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
                        <path d="M3.5 2C3.5 1.17 4.17 0.5 5 0.5H11C11.83 0.5 12.5 1.17 12.5 2V14C12.5 14.83 11.83 15.5 11 15.5H5C4.17 15.5 3.5 14.83 3.5 14V2Z" stroke="currentColor" stroke-width="1" fill="none"/>
                        <path d="M6 3.5H10" stroke="currentColor" stroke-width="1"/>
                        <path d="M6 5.5H10" stroke="currentColor" stroke-width="1"/>
                        <path d="M6 7.5H8" stroke="currentColor" stroke-width="1"/>
                      </svg>
                    </div>
                    <div class="contact-details">
                      <span class="contact-label">Company phone:</span>
                      <a v-if="companyInfo && companyInfo.companyPhone" :href="`tel:${companyInfo.companyPhone}`" class="contact-link">{{ companyInfo.companyPhone }}</a>
                      <a v-else href="tel:+862032886689" class="contact-link">+86 20 3288 6689</a>
                    </div>
                  </div>

                  <div class="contact-item">
                    <div class="contact-icon">
                      <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
                        <circle cx="8" cy="8" r="6" stroke="currentColor" stroke-width="1" fill="none"/>
                        <path d="M8 2C8 2 8 6 8 8C8 10 8 14 8 14" stroke="currentColor" stroke-width="1"/>
                        <path d="M2 8C2 8 6 8 8 8C10 8 14 8 14 8" stroke="currentColor" stroke-width="1"/>
                      </svg>
                    </div>
                    <div class="contact-details">
                      <span class="contact-label">Company website:</span>
                      <div class="website-links">
                        <template v-if="companyInfo && companyInfo.companyWebsite">
                          <a :href="companyInfo.companyWebsite" target="_blank" class="website-link">{{ companyInfo.companyWebsite }}</a>
                        </template>
                        <template v-else>
                          <a href="http://www.c-jeans.com" target="_blank" class="website-link">www.c-jeans.com</a>
                          <a href="https://c-jeans.en.alibaba.com/" target="_blank" class="website-link">https://c-jeans.en.alibaba.com/</a>
                        </template>
                      </div>
                    </div>
                  </div>

                  <!-- 公司位置 -->
                  <div class="location-address">
                    <div class="address-icon">
                      <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
                        <path d="M8 14C8 14 2 9 2 6C2 3 4.5 1 8 1C11.5 1 14 3 14 6C14 9 8 14 8 14Z" stroke="currentColor" stroke-width="1" fill="none"/>
                        <circle cx="8" cy="6" r="2" stroke="currentColor" stroke-width="1" fill="none"/>
                      </svg>
                    </div>
                    <div class="address-text">
                      <span class="contact-label location-label">Company location:</span><br />
                      <p>{{ companyAddress }}</p>
                    </div>
                  </div>
                </div>

                <div class="contact-column">
                  <!-- 添加邮箱联系方式 -->
                  <div class="contact-item">
                    <div class="contact-icon">
                      <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
                        <path d="M2 3H14C14.55 3 15 3.45 15 4V12C15 12.55 14.55 13 14 13H2C1.45 13 1 12.55 1 12V4C1 3.45 1.45 3 2 3Z" stroke="currentColor" stroke-width="1" fill="none"/>
                        <path d="M3 4L8 7L13 4" stroke="currentColor" stroke-width="1"/>
                      </svg>
                    </div>
                    <div class="contact-details">
                      <span class="contact-label">Company email:</span>
                      <a v-if="companyInfo && companyInfo.companyEmail" :href="`mailto:${companyInfo.companyEmail}`" class="contact-link">{{ companyInfo.companyEmail }}</a>
                      <a v-else href="mailto:info@c-jeans.com" class="contact-link">info@c-jeans.com</a>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 右侧多媒体展示区 -->
        <div class="media-section">
          <div class="media-container">
            <!-- 轮播图展示区域 -->
            <div class="media-player">
              <div class="player-wrapper">
                <CategoryCarousel
                  :category="getCurrentCategoryApiParam()"
                  :categoryName="getCurrentCategoryName()"
                  :autoplay="true"
                  :interval="4000"
                />
              </div>
            </div>

            <!-- 底部内容导航栏 -->
            <div class="content-navigation-bar">
              <!-- 左侧切换按钮 -->
              <button class="nav-btn nav-prev" @click="previousCategory" :disabled="currentCategoryIndex === 0">
                <svg width="16" height="16" viewBox="0 0 16 16" fill="currentColor">
                  <path d="M10 3L5 8L10 13" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
              </button>

              <!-- 分类标签组 -->
              <div class="category-tabs-container">
                <div class="category-tabs" :style="{ transform: `translateX(-${tabOffset}px)` }">
                  <button
                    v-for="category in mediaCategories"
                    :key="category.id"
                    class="category-tab"
                    :class="{ active: activeCategory === category.id }"
                    @click="switchCategory(category.id)"
                  >
                    {{ category.name }}
                  </button>
                </div>
              </div>

              <!-- 右侧切换按钮 -->
              <button class="nav-btn nav-next" @click="nextCategory" :disabled="currentCategoryIndex === mediaCategories.length - 1">
                <svg width="16" height="16" viewBox="0 0 16 16" fill="currentColor">
                  <path d="M6 3L11 8L6 13" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<script>
import { videoApi } from '../api/video'
import CategoryCarousel from './CategoryCarousel.vue'
import { getCompanyInfo } from '../api/company'
import { getMediaFiles } from '../api/media'
import defaultLogo1 from '@/images/logo/logo-1.jpg'

export default {
  name: 'SupplierCapabilityShowcase',
  components: {
    CategoryCarousel
  },
  data() {
    return {
      // 公司信息数据
      companyInfo: null,
      loading: false,
      error: null,
      logoUrl: null,
      defaultLogo1,  // 暴露给模板使用

      // 能力数据
      capabilities: [
        {
          id: 1,
          name: 'Agile supply chain',
          description: 'Flexible and responsive supply chain management'
        },
        {
          id: 2,
          name: 'Minor customization',
          description: 'Support for small-scale product customization'
        },
        {
          id: 3,
          name: 'Raw material identification and traceability',
          description: 'Comprehensive quality control and management processes'
        },
        {
          id: 4,
          name: 'Finished product inspection',
          description: 'In-house research and development team'
        },
      ],
      totalCapabilities: 10,

      // 多媒体数据
      activeCategory: 'production',
      currentCategoryIndex: 0,
      mediaCategories: [
        { id: 'overview', name: 'Overview', count: 7 },
        { id: 'production', name: 'Production', count: 8 },
        { id: 'rd', name: 'R&D', count: 5 },
        { id: 'quality', name: 'Quality control', count: 3 },
        { id: 'intro', name: 'Company Intro', count: 1 }
      ],

      tabOffset: 0 // 标签偏移量
    }
  },
  computed: {
    companyName() {
      return this.companyInfo?.companyName || this.companyInfo?.companyShortName || 'Hongyu Clothing Co., Ltd.'
    },
    companyAddress() {
      return this.companyInfo?.companyAddress || 'No. 20 Youyi Street, Huimei New Village, Xintang Town, Guangzhou, Guangdong, China'
    }
  },
  methods: {
    // 获取当前分类的API参数
    getCurrentCategoryApiParam() {
      const categoryMapping = {
        'overview': 'about-overview',
        'production': 'about-production',
        'rd': 'about-rd',
        'quality': 'about-quality',
        'intro': 'company-intro'
      }
      return categoryMapping[this.activeCategory] || this.activeCategory
    },

    // 获取当前分类的中文名称
    getCurrentCategoryName() {
      const categoryMapping = {
        'overview': '公司概览',
        'production': '生产展示',
        'rd': '研发部门',
        'quality': '质量控制',
        'intro': '公司介绍'
      }
      return categoryMapping[this.activeCategory] || this.activeCategory
    },

    // 显示评价详情
    showReviews() {
      console.log('显示评价详情')
      // 可以打开模态框或跳转到评价页面
    },

    // 显示能力详情
    showCapabilityInfo(capability) {
      console.log('显示能力详情:', capability.name, capability.description)
      // 可以显示tooltip或模态框
    },

    // 显示所有能力
    showAllCapabilities() {
      console.log('显示所有能力认证')
      // 可以跳转到详细的能力认证页面
    },

    /**
     * 获取公司信息和联系方式
     * 逻辑参考 ContractPage.vue，保持接口行为一致
     */
    async loadCompanyData() {
      this.loading = true
      this.error = null

      try {
        // 并行获取公司基本信息和Logo
        await Promise.all([
          this.fetchCompanyInfoInternal(),
          this.fetchCompanyLogo()
        ])
      } catch (err) {
        // 外层异常兜底
        this.error = err.message || '加载公司信息失败，请稍后重试'
      } finally {
        this.loading = false
      }
    },

    async fetchCompanyInfoInternal() {
      try {
        // 调用公开API获取公司信息
        const infoResponse = await getCompanyInfo()

        if (infoResponse && infoResponse.data && infoResponse.data.code === 200 && infoResponse.data.data) {
          this.companyInfo = infoResponse.data.data
        } else {
          throw new Error('公司基本信息返回格式不符合预期或无数据')
        }

        // 已移除 getCompanyContact 调用，统一使用 companyInfo.companyPhone

      } catch (err) {
        // 兜底默认公司信息
        this.error = err.message || '加载公司信息失败，请稍后重试'
        this.companyInfo = {
          companyName: 'Guangzhou Kaicheng Clothing Co., Ltd.',
          companyEmail: 'info@c-jeans.com',
          companyWebsite: 'http://www.c-jeans.com',
          companyAddress: 'No. 20 Youyi Street, Huimei New Village, Xintang Town, Guangzhou, Guangdong, China',
          companyPhone: '020-12345678'
        }
      }
    },

    async fetchCompanyLogo() {
      try {
        // 使用媒体API获取logo分类的文件列表
        const response = await getMediaFiles('logo')
        console.log('🖼️ [SupplierCapabilityShowcase] Logo接口响应:', response?.data)
        
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
              console.log('✅ [SupplierCapabilityShowcase] 获取最新Logo:', this.logoUrl)
            }
          }
        }
      } catch (error) {
        console.error('❌ [SupplierCapabilityShowcase] 获取公司Logo失败:', error)
        this.logoUrl = null
      }
    },

    handleLogoError(event) {
      // 图片加载失败时回退到本地默认Logo
      console.error('❌ [SupplierCapabilityShowcase] Logo图片加载失败，URL:', event.target.src)
      console.error('❌ [SupplierCapabilityShowcase] 回退到默认Logo')
      this.logoUrl = null
      event.target.src = defaultLogo1
    },

    // 切换媒体分类
    switchCategory(categoryId) {
      this.activeCategory = categoryId
      this.currentCategoryIndex = this.mediaCategories.findIndex(cat => cat.id === categoryId)
      this.updateTabOffset()
    },

    // 上一个分类
    previousCategory() {
      if (this.currentCategoryIndex > 0) {
        this.currentCategoryIndex--
        this.activeCategory = this.mediaCategories[this.currentCategoryIndex].id
        this.updateTabOffset()
      }
    },

    // 下一个分类
    nextCategory() {
      if (this.currentCategoryIndex < this.mediaCategories.length - 1) {
        this.currentCategoryIndex++
        this.activeCategory = this.mediaCategories[this.currentCategoryIndex].id
        this.updateTabOffset()
      }
    },

    // 更新标签偏移量，使当前选中的标签可见
    updateTabOffset() {
      this.$nextTick(() => {
        const container = this.$el.querySelector('.category-tabs-container')
        const tabs = this.$el.querySelector('.category-tabs')
        const activeTab = this.$el.querySelector('.category-tab.active')
        
        if (!container || !tabs || !activeTab) return
        
        const containerWidth = container.offsetWidth
        const tabLeft = activeTab.offsetLeft
        const tabWidth = activeTab.offsetWidth
        
        // 计算需要的偏移量，使当前标签居中显示
        const targetOffset = tabLeft - (containerWidth / 2) + (tabWidth / 2)
        
        // 限制偏移范围
        const maxOffset = tabs.scrollWidth - containerWidth
        this.tabOffset = Math.max(0, Math.min(targetOffset, maxOffset))
      })
    },
  },

  // 生命周期钩子
  mounted() {
    console.log('🎬 SupplierCapabilityShowcase组件初始化')
    // 页面加载时获取公司数据
    this.loadCompanyData()
  }
}
</script>

<style scoped>
/* 整体容器 */
.supplier-capability-showcase {
  padding: 60px 0;
  background: transparent;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

/* 双栏布局 */
.showcase-layout {
  display: grid;
  grid-template-columns: 3fr 2fr;
  gap: 30px;
  align-items: start;
  position: relative;
  z-index: 1;
}

/* 左侧能力指标区 */
.capability-section {
  background: transparent;
  border-radius: 8px;
  padding: 25px;
}

/* 公司联系信息卡片 - 与 ContractPage.vue 一致 */
.contact-card {
  background-image:
    linear-gradient(rgba(0, 0, 0, 0.6), rgba(0, 0, 0, 0.6)),
    url('../images/background/background-info.jpg');
  background-size: 100% 100%;
  background-position: center;
  background-repeat: no-repeat;
  background-blend-mode: overlay;
  border-radius: 8px;
  padding: 40px;
  margin-bottom: 10px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  color: white;
  min-height: 480px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

/* 加载和错误状态样式 */
.loading-container,
.error-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 200px;
  text-align: center;
  padding: 20px;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid rgba(255, 255, 255, 0.3);
  border-radius: 50%;
  border-top: 4px solid #fff;
  animation: spin 1s linear infinite;
  margin-bottom: 16px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.loading-container p {
  color: rgba(255, 255, 255, 0.9);
  font-size: 14px;
  margin: 0;
}

.error-container {
  color: white;
}

.error-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.error-container h3 {
  margin: 0 0 8px 0;
  font-size: 18px;
  font-weight: 600;
}

.error-container p {
  margin: 0 0 16px 0;
  color: rgba(255, 255, 255, 0.8);
  font-size: 14px;
  line-height: 1.4;
}

.retry-btn {
  background: #ff6a00;
  color: white;
  border: none;
  padding: 8px 20px;
  border-radius: 4px;
  font-size: 14px;
  cursor: pointer;
  transition: background 0.3s;
}

.retry-btn:hover {
  background: #e55a00;
}

/* 公司头部区域 */
.company-header {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 30px;
}

.company-logo {
  width: 60px;
  height: 60px;
  object-fit: contain;
  border-radius: 8px;
  background: white;
  padding: 8px;
}

/* 卡片标题 */
.card-title {
  font-size: 22px;
  font-weight: bold;
  color: white;
  margin: 0;
}

/* 公司简介 */
.company-description {
  margin-bottom: 30px;
  padding: 20px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 8px;
  backdrop-filter: blur(5px);
  border-left: 4px solid rgba(255, 255, 255, 0.3);
}

.company-description p {
  color: rgba(255, 255, 255, 0.95);
  font-size: 15px;
  line-height: 1.6;
  margin: 0 0 15px 0;
}

.company-description p:last-child {
  margin-bottom: 0;
}

/* 联系方式 */
.contact-methods {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 30px;
}

.contact-column {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.contact-item {
  display: flex;
  align-items: flex-start;
  gap: 12px;
}

.contact-icon {
  width: 20px;
  height: 20px;
  color: white;
  flex-shrink: 0;
  margin-top: 2px;
}

.contact-details {
  flex: 1;
}

.contact-label {
  display: block;
  font-size: 15px;
  color: rgba(255, 255, 255, 0.9);
  margin-bottom: 5px;
  font-weight: 600;
}

.contact-link {
  color: #66d9ff;
  text-decoration: none;
  font-size: 15px;
  font-weight: 500;
}

.contact-link:hover {
  color: #99e6ff;
  text-decoration: underline;
}

.website-links {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.website-link {
  color: #66d9ff;
  text-decoration: none;
  font-size: 15px;
  line-height: 1.4;
}

.website-link:hover {
  text-decoration: underline;
}

/* 位置信息样式（与 ContractPage.vue 一致） */
.location-address {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  margin-top: 12px;
}

.address-icon {
  width: 20px;
  height: 20px;
  color: white;
  flex-shrink: 0;
  margin-top: 2px;
}

.address-text {
  font-size: 15px;
  color: rgba(255, 255, 255, 0.9);
  line-height: 1.5;
}

.location-label {
  display: inline;
  margin-right: 6px;
}

/* 右侧多媒体展示区 */
.media-section {
  background: transparent;
  border-radius: 8px;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  height: 100%;
}

.media-container {
  height: 100%;
  display: flex;
  flex-direction: column;
}

/* 顶部媒体播放器 - 轮播图容器 */
.media-player {
  position: relative;
  flex: 1;
  height: 100%;
}

.player-wrapper {
  position: relative;
  width: 100%;
  height: 100%;
  background: #f5f5f5;
  border-radius: 8px;
  overflow: hidden;
}


/* 底部内容导航栏 */
.content-navigation-bar {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 8px 8px 8px;
  background: white;
  border-radius: 6px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  margin-top: 10px;
}

/* 导航按钮 */
.nav-btn {
  background: #f8f9fa;
  border: 1px solid #dee2e6;
  border-radius: 50%;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
  color: #6c757d;
  flex-shrink: 0;
}

.nav-btn:hover:not(:disabled) {
  background: #007bff;
  border-color: #007bff;
  color: white;
}

.nav-btn:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

/* 分类标签容器 */
.category-tabs-container {
  flex: 1;
  overflow: hidden;
  position: relative;
}

.category-tabs {
  display: flex;
  gap: 8px;
  transition: transform 0.3s ease;
  white-space: nowrap;
}

/* 分类标签 */
.category-tab {
  background: #f8f9fa;
  border: 1px solid #dee2e6;
  border-radius: 16px;
  padding: 6px 12px;
  font-size: 12px;
  color: #6c757d;
  cursor: pointer;
  transition: all 0.3s ease;
  white-space: nowrap;
  font-weight: 500;
  flex-shrink: 0;
}

.category-tab:hover {
  background: #e9ecef;
  border-color: #007bff;
  color: #007bff;
  transform: translateY(-1px);
}

.category-tab.active {
  background: #212529;
  border-color: #212529;
  color: white;
  transform: translateY(-1px);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .showcase-layout {
    grid-template-columns: 1fr;
    gap: 30px;
  }

  .capability-section {
    max-width: 600px;
    margin: 0 auto;
  }
}

@media (max-width: 768px) {
  .supplier-capability-showcase {
    padding: 40px 0;
  }

  .container {
    padding: 0 15px;
  }

  .capability-section {
    padding: 20px;
  }

  .performance-title {
    font-size: 20px;
  }

  .rating-score {
    font-size: 28px;
  }

  .verification-badges {
    top: 10px;
    left: 10px;
  }

  .badge {
    font-size: 10px;
    padding: 3px 6px;
  }

  .content-navigation {
    padding: 12px 15px;
    gap: 10px;
  }

  .category-tab {
    font-size: 13px;
    padding: 6px 12px;
  }
}

@media (max-width: 480px) {
  .capability-section {
    padding: 15px;
  }

  .rating-module {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }

  .metric-item {
    gap: 10px;
  }

  .video-controls {
    padding: 10px;
    gap: 10px;
  }

  .time-display {
    font-size: 11px;
  }
}

</style>