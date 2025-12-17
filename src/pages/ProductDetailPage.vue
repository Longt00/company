<template>
  <div class="product-detail-page">
    <!-- 导航头部 -->
    <Header />

    <!-- 产品详情内容 -->
    <div class="product-detail-container">
      <div class="container">
        <!-- 返回按钮 -->
        <div class="back-nav">
          <button @click="goBack" class="back-btn">
            <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
              <path d="M12 8H4M4 8L8 4M4 8L8 12" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
            Back to Products
          </button>
        </div>

        <!-- 加载状态 -->
        <div v-if="loading" class="loading-container">
          <div class="loading-spinner"></div>
          <p>Loading product details...</p>
        </div>

        <!-- 错误状态 -->
        <div v-else-if="error" class="error-container">
          <div class="error-icon">😕</div>
          <h3>{{ error }}</h3>
          <p>Unable to load product details</p>
          <button @click="loadProduct" class="retry-btn">Try Again</button>
        </div>

        <!-- 产品详情展示 -->
        <div v-else-if="product" class="product-content">
          <div class="product-detail-grid">
            <!-- 左侧：产品媒体展示（支持视频 + 图片） -->
            <div class="product-images-section">
              <!-- 媒体缩略图导航：视频在前，其后是图片，带上下箭头控制 -->
              <div v-if="mediaItems.length > 0" class="thumbnail-nav-wrapper">
                <button
                  type="button"
                  class="thumb-arrow thumb-arrow-up"
                  @click="scrollThumbnails('up')"
                >
                </button>

                <div ref="thumbnailList" class="image-thumbnails">
                  <div
                    v-for="(item, index) in mediaItems"
                    :key="index"
                    :class="['thumbnail-item', { active: currentMediaIndex === index }]"
                    @click="setCurrentMedia(index)"
                  >
                    <template v-if="item.type === 'video'">
                      <div class="thumbnail-video">
                        <video :src="item.src" muted playsinline></video>
                        <div class="thumbnail-video-icon">
                        </div>
                      </div>
                    </template>
                    <template v-else>
                      <img :src="item.src" :alt="`${product.name} - Image ${index + 1}`" />
                    </template>
                  </div>
                </div>

                <button
                  type="button"
                  class="thumb-arrow thumb-arrow-down"
                  @click="scrollThumbnails('down')"
                >
                </button>
              </div>

              <!-- 主媒体展示区域：优先展示视频，其次图片，带左右翻页按钮 -->
              <div class="main-image-container">
                <button
                  v-if="mediaItems.length > 1"
                  type="button"
                  class="main-arrow main-arrow-left"
                  @click="prevMedia"
                >
                </button>

                <template v-if="currentMedia && currentMedia.type === 'video'">
                  <video
                    :src="currentMedia.src"
                    class="main-product-video"
                    controls
                    playsinline
                  ></video>
                </template>
                <template v-else>
                  <img
                    :src="currentMedia ? currentMedia.src : currentImage"
                    :alt="product.name"
                    class="main-product-image"
                    @error="handleImageError"
                  />
                </template>

                <button
                  v-if="mediaItems.length > 1"
                  type="button"
                  class="main-arrow main-arrow-right"
                  @click="nextMedia"
                >
                </button>

                <!-- 产品标签 -->
                <div v-if="product.isFeatured" class="product-badge featured-badge">Featured</div>
              </div>
            </div>

            <!-- 右侧：产品信息 -->
            <div class="product-info-section">
              <!-- 产品标题和基本信息 -->
              <div class="product-header">
                <h1 class="product-title">{{ product.name }}</h1>
                <p v-if="product.description" class="product-subtitle">{{ product.description }}</p>

                <!-- 产品分类 -->
                <div v-if="product.category" class="product-category">
                  <span class="category-label">Category:</span>
                  <span class="category-value">{{ product.category }}</span>
                </div>
              </div>

              <!-- 产品规格：核心修改：将折叠类绑定到外层容器 -->
              <div 
                v-if="hasSpecifications" 
                class="product-specifications"
                :class="{ 'collapsed-container': !specsExpanded }"
              >
                <div class="spec-header">
                  <h3 class="spec-title">Product Specifications</h3>
                  <button
                    @click="toggleSpecsExpanded"
                    class="spec-toggle-btn"
                    :class="{ expanded: specsExpanded }"
                  >
                    <svg width="16" height="16" viewBox="0 0 16 16" fill="currentColor">
                      <path d="M4.646 6.646a.5.5 0 0 1 .708 0L8 9.293l2.646-2.647a.5.5 0 0 1 .708.708l-3 3a.5.5 0 0 1-.708 0l-3-3a.5.5 0 0 1 0-.708z"/>
                    </svg>
                  </button>
                </div>
                <div class="spec-grid">
                  <div v-for="(value, key) in displaySpecifications" :key="key" class="spec-item">
                    <span class="spec-label">{{ formatSpecLabel(key) }}:</span>
                    <span class="spec-value">{{ value }}</span>
                  </div>
                </div>
              </div>

              <!-- 联系按钮区域 -->
              <div class="contact-actions">
                <button @click="contactWhatsApp" class="whatsapp-btn">
                  <svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor">
                    <path d="M17.472 14.382c-.297-.149-1.758-.867-2.03-.967-.273-.099-.471-.149-.67.149-.197 0-.987.896-1.264.967-.277.07-.665-.059-.943-.197-.277-.139-2.305-1.264-2.849-1.557-.544-.293-.598-.417-.943-.014-.345.383-.345.383-1.119.033-1.119.033-.736.348-1.331 1.885-.595 1.537.627 4.443 3.245 4.443 3.245 1.618 1.953 2.229 3.44 2.531 4.266.302.826.137 1.277.074 1.419-.063.142-.402.416-.67.565-.268.149-.896.374-.896.374z"/>
                    <path d="M12 0C5.373 0 0 5.373 0 12s5.373 12 12 12c2.55 0 4.905-.808 6.837-2.184L24 24l-2.184-5.163C23.192 16.905 24 14.55 24 12 24 5.373 18.627 0 12 0zm0 21.818c-2.341 0-4.508-.731-6.293-1.978l-1.443.343.344-1.443C2.731 16.508 2 14.341 2 12c0-5.522 4.478-10 10-10s10 4.478 10 10-4.478 10-10 10z"/>
                  </svg>
                  Contact via WhatsApp
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 页脚 -->
    <Footer />
  </div>
</template>

<script>
import Header from '../components/Header.vue'
import Footer from '../components/Footer.vue'
import { getProductDetail } from '../api/products'
import { getProductImage } from '@/assets/productImages.js'
import { getPublicWhatsAppContact } from '../api/company'
import { buildProductWhatsAppUrl, WHATSAPP_CONFIG } from '@/config/whatsapp'
import { openWhatsAppUrl } from '@/utils/whatsappHelper'

export default {
  name: 'ProductDetailPage',
  components: {
    Header,
    Footer
  },
  data() {
    return {
      product: null,
      loading: true,
      error: null,
      currentImageIndex: 0,
      productImages: [],
      // 媒体（视频 + 图片）统一列表，驱动主区域和缩略图
      currentMediaIndex: 0,
      mediaItems: [],
      // 规格展开/收起状态
      specsExpanded: false,
      // 动态WhatsApp号码
      dynamicWhatsAppNumber: null
    }
  },
  computed: {
    currentImage() {
      return this.productImages[this.currentImageIndex] || this.getDefaultProductImage()
    },
    // 当前展示的媒体（可能是视频或图片）
    currentMedia() {
      if (this.mediaItems.length === 0) return null
      return this.mediaItems[this.currentMediaIndex] || this.mediaItems[0]
    },

    hasSpecifications() {
      return this.product && this.product.specifications && Object.keys(this.product.specifications).length > 0
    },
    displaySpecifications() {
      if (!this.product || !this.product.specifications) return {}

      // 定义需要排除的属性列表
      const excludedKeys = ['Craftsmanship', 'Origin']
      // 过滤掉空值和指定排除的规格
      const specs = {}
      Object.keys(this.product.specifications).forEach(key => {
        const value = this.product.specifications[key]
        // 条件：值非空 且 不在排除列表中
        if (value && value.trim() !== '' && !excludedKeys.includes(key)) {
          specs[key] = value
        }
      })
      return specs
    }
  },
  async mounted() {
    await this.loadProduct()
    await this.fetchWhatsAppNumber()
  },
  methods: {
    async loadProduct() {
      this.loading = true
      this.error = null

      try {
        const productId = this.$route.params.id

        if (!productId) {
          throw new Error('Product ID is missing')
        }

        console.log('🔄 [ProductDetailPage] Loading product:', productId)

        // 使用新的公开API获取产品数据
        try {
          const response = await getProductDetail(productId)

          if (response && response.data && response.data.data) {
            this.product = this.mapProductData(response.data.data)
            this.initializeProductImages()

            console.log('✅ [ProductDetailPage] Product loaded from public API:', this.product)
            return
          }
        } catch (apiError) {
          console.warn('⚠️ [ProductDetailPage] Public API failed, using fallback:', apiError)
        }

        // 如果API失败，使用默认产品数据（但移除规格的默认值）
        this.product = this.createDefaultProduct(productId)
        this.initializeProductImages()

        console.log('✅ [ProductDetailPage] Using default product data')

      } catch (error) {
        console.error('❌ [ProductDetailPage] Failed to load product:', error)
        this.error = 'Failed to load product details'
      } finally {
        this.loading = false
      }
    },

    mapProductData(apiProduct) {
      // 构建规格对象，只保留后端实际返回的有值属性
      const specifications = {}

      // 只添加有值的属性
      if (apiProduct.material) specifications.Material = apiProduct.material
      if (apiProduct.style) specifications.Style = apiProduct.style
      if (apiProduct.fabricType) specifications['Fabric Type'] = apiProduct.fabricType
      if (apiProduct.fabricWeight) specifications['Fabric Weight'] = apiProduct.fabricWeight
      if (apiProduct.waistStyle) specifications['Waist Style'] = apiProduct.waistStyle
      if (apiProduct.jeansStyle) specifications['Jeans Style'] = apiProduct.jeansStyle
      if (apiProduct.patternFit) specifications['Pattern Fit'] = apiProduct.patternFit
      if (apiProduct.craftsmanship) specifications.Craftsmanship = apiProduct.craftsmanship
      if (apiProduct.weavingMethod) specifications['Weaving Method'] = apiProduct.weavingMethod
      if (apiProduct.washingProcess) specifications['Washing Process'] = apiProduct.washingProcess
      if (apiProduct.needleDetectionProcess) specifications['Needle Detection'] = apiProduct.needleDetectionProcess
      if (apiProduct.patternType) specifications['Pattern Type'] = apiProduct.patternType
      if (apiProduct.printingMethod) specifications['Printing Method'] = apiProduct.printingMethod
      if (apiProduct.fashionElements) specifications['Fashion Elements'] = apiProduct.fashionElements
      if (apiProduct.logoPosition && apiProduct.logoPosition !== '无') specifications['Logo Position'] = apiProduct.logoPosition
      if (apiProduct.supplyType) specifications['Supply Type'] = apiProduct.supplyType
      if (apiProduct.origin) specifications.Origin = apiProduct.origin
      if (apiProduct.model) specifications.Model = apiProduct.model
      if (apiProduct.season) specifications.Season = apiProduct.season
      if (apiProduct.salesUnit) specifications['Sales Unit'] = apiProduct.salesUnit

      return {
        id: apiProduct.id,
        name: apiProduct.name || apiProduct.productName || 'Product Name',
        description: apiProduct.description || '',
        price: apiProduct.price || 0,
        stock: apiProduct.stockQuantity || 100,
        category: apiProduct.category || 'Uncategorized',
        featuredImage: apiProduct.mainImage || this.getDefaultProductImage(),
        additionalImages: apiProduct.productImages || [],
        // 可选视频字段：如果有则用于左侧媒体区的优先展示
        videoUrl: apiProduct.videoPath || null,
        // 移除规格的默认兜底，只保留实际有值的规格
        specifications: specifications,
        features: [], // 移除特性的默认值
        isNew: !apiProduct.createdAt || (new Date() - new Date(apiProduct.createdAt)) < 7 * 24 * 60 * 60 * 1000,
        isFeatured: apiProduct.isFeatured || false,
        _originalData: apiProduct
      }
    },

    createDefaultProduct(productId) {
      const productIndex = this.extractProductIndex(productId)
      return {
        id: productId,
        name: this.getDefaultProductName(productIndex),
        description: this.getDefaultProductDescription(productIndex),
        price: this.getDefaultProductPrice(productIndex),
        stock: 100 + Math.floor(Math.random() * 900),
        category: this.getDefaultProductCategory(productIndex),
        featuredImage: getProductImage((productIndex % 40) + 1),
        additionalImages: this.getAdditionalProductImages(productIndex),
        // 移除默认规格，改为空对象
        specifications: {},
        features: [], // 移除特性的默认值
        isNew: productIndex < 10,
        isFeatured: productIndex % 5 === 0
      }
    },

    extractProductIndex(productId) {
      const match = productId.match(/(\d+)$/)
      return match ? parseInt(match[1]) : 1
    },

    getDefaultProductName(index) {
      const names = [
        'Premium Denim Jacket', 'Classic Blue Jeans', 'Slim Fit Denim Shirt',
        'Denim Cargo Pants', 'Vintage Denim Vest', 'Denim Work Jacket',
        'Custom Denim Skirt', 'Denim Baseball Cap', 'Denim Backpack',
        'Denim Shoulder Bag'
      ]
      return names[index % names.length]
    },

    getDefaultProductDescription(index) {
      const descriptions = [
        'High-quality denim product with premium materials and craftsmanship',
        'Stylish and comfortable denim wear for all occasions',
        'Durable denim construction with modern design elements',
        'Premium denim fabric with excellent fit and comfort'
      ]
      return descriptions[index % descriptions.length]
    },

    getDefaultProductPrice(index) {
      return 25 + (index % 10) * 5
    },

    getDefaultProductCategory(index) {
      const categories = ['Men', 'Women', 'Children', 'Denim Accessories']
      return categories[index % categories.length]
    },

    getAdditionalProductImages(mainIndex) {
      const images = []
      const startIndex = (mainIndex % 40) + 1
      for (let i = 1; i <= Math.min(3, 40 - startIndex + 1); i++) {
        images.push(getProductImage(startIndex + i))
      }
      return images
    },

    // 移除 getDefaultSpecifications 方法（不再需要默认规格）

    // 移除 getDefaultFeatures 方法（不再需要默认特性）

    getDefaultProductImage() {
      return getProductImage(1)
    },

    initializeProductImages() {
      if (this.product) {
        // 构建图片列表
        const images = [
          this.product.featuredImage || this.getDefaultProductImage(),
          ...(this.product.additionalImages || [])
        ].filter(Boolean)

        this.productImages = images
        this.currentImageIndex = 0

        // 构建统一媒体列表：若有视频则排在第一位
        const media = []
        if (this.product.videoUrl) {
          media.push({ type: 'video', src: this.product.videoUrl })
        }
        images.forEach(src => {
          media.push({ type: 'image', src })
        })

        this.mediaItems = media
        this.currentMediaIndex = 0
      }
    },

    // 缩略图导航：切换当前媒体
    setCurrentMedia(index) {
      if (index < 0 || index >= this.mediaItems.length) return
      this.currentMediaIndex = index
    },

    // 主图左右切换
    nextMedia() {
      if (this.mediaItems.length === 0) return
      this.currentMediaIndex = (this.currentMediaIndex + 1) % this.mediaItems.length
    },

    prevMedia() {
      if (this.mediaItems.length === 0) return
      this.currentMediaIndex = (this.currentMediaIndex - 1 + this.mediaItems.length) % this.mediaItems.length
    },

    // 缩略图上下滚动
    scrollThumbnails(direction) {
      const list = this.$refs.thumbnailList
      if (!list) return

      const offset = 80 + 12 // 单个缩略图高度 + 间距
      const delta = direction === 'up' ? -offset : offset
      list.scrollBy({ top: delta, behavior: 'smooth' })
    },

    handleImageError(event) {
      event.target.src = this.getDefaultProductImage()
    },

    formatSpecLabel(key) {
      // 自定义标签映射：实现属性名称的修改
      const labelMap = {
        'Material': 'Style No.', // 将Material改为Style No.
        'Style': 'Weight', // 将Style改为Weight
        'Fabric Type': 'Content' // 将Fabric Type改为Content（修正拼写错误Fabric Typ）
      }
      // 优先使用映射的名称，没有则按原规则格式化
      if (labelMap[key]) {
        return labelMap[key]
      }
      // 原格式化逻辑（处理驼峰命名或下划线转换为可读格式）
      return key.replace(/([A-Z])/g, ' $1')
                 .replace(/_/g, ' ')
                 .replace(/^\w/, c => c.toUpperCase())
    },

    goBack() {
      this.$router.go(-1)
    },

    toggleSpecsExpanded() {
      this.specsExpanded = !this.specsExpanded
    },

    async fetchWhatsAppNumber() {
      try {
        const response = await getPublicWhatsAppContact()
        console.log('📱 [ProductDetailPage] WhatsApp接口响应:', response?.data)

        // 兼容多种响应格式
        const resData = response?.data
        if (!resData) {
          console.log('ℹ️ [ProductDetailPage] 接口无响应数据，使用默认配置')
          return
        }

        // 尝试提取号码：支持多种后端响应格式
        let rawNumber = null

        // 格式1: {code: 200, data: {whatsapp: "...", whatsappNumber: "...", phoneNumber: "..."}}
        if (resData.code === 200 && resData.data) {
          rawNumber = resData.data.whatsappNumber || resData.data.whatsapp || resData.data.phoneNumber
        }
        // 格式2: 直接在message字段中 (兼容旧版API)
        else if (resData.success && resData.message && typeof resData.message === 'string') {
          rawNumber = resData.message
        }
        // 格式3: 直接返回数据的格式
        else if (resData.whatsappNumber || resData.phoneNumber || resData.whatsapp || resData.phone) {
          rawNumber = resData.whatsappNumber || resData.phoneNumber || resData.whatsapp || resData.phone
        }

        if (rawNumber) {
          // 清洗号码：保留数字和+号
          let clean = String(rawNumber).replace(/[^\d+]/g, '')
          // 处理国际格式：00开头转为+
          if (clean.startsWith('00')) {
            clean = '+' + clean.slice(2)
          }
          // 确保有国际区号（如果没有+且长度合理，假设是中国号码）
          if (!clean.startsWith('+') && clean.length >= 10) {
            // 如果是11位且以1开头，可能是中国手机号
            if (clean.length === 11 && clean.startsWith('1')) {
              clean = '+86' + clean
            }
          }
          this.dynamicWhatsAppNumber = clean
          console.log('✅ [ProductDetailPage] 成功获取WhatsApp号码:', this.dynamicWhatsAppNumber)
          return
        }

        console.log('ℹ️ [ProductDetailPage] 后台暂未配置WhatsApp号码，使用默认配置')
      } catch (error) {
        console.log('ℹ️ [ProductDetailPage] WhatsApp接口暂不可用，使用默认配置:', error.message)
      }
    },

    contactWhatsApp() {
      if (!this.product) return

      // 使用动态号码或默认号码
      const phoneNumber = this.dynamicWhatsAppNumber || WHATSAPP_CONFIG.phoneNumber

      // 使用产品相关消息模板
      const productInfo = {
        productName: this.product.name,
        productPrice: this.product.price ? `$${this.product.price}` : 'pricing information',
        customOption: 'customization options'
      }

      const whatsappUrl = buildProductWhatsAppUrl(productInfo, phoneNumber)
      openWhatsAppUrl(whatsappUrl)
    },

    shareProduct(platform) {
      if (!this.product) return

      const url = encodeURIComponent(window.location.href)
      const title = encodeURIComponent(this.product.name)
      const description = encodeURIComponent(this.product.description || 'Check out this amazing product!')

      let shareUrl = ''

      switch (platform) {
        case 'facebook':
          shareUrl = `https://www.facebook.com/sharer/sharer.php?u=${url}`
          break
        case 'twitter':
          shareUrl = `https://twitter.com/intent/tweet?url=${url}&text=${title} - ${description}`
          break
        case 'linkedin':
          shareUrl = `https://www.linkedin.com/sharing/share-offsite/?url=${url}&title=${title}&summary=${description}`
          break
      }

      if (shareUrl) {
        window.open(shareUrl, '_blank', 'width=600,height=400')
      }
    }
  }
}
</script>

<style scoped>
/* 页面基础样式 */
.product-detail-page {
  min-height: 100vh;
  background: transparent;
  display: flex;
  flex-direction: column;
}

.product-detail-container {
  padding: 40px 0;
  background: transparent;
  flex: 1;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

/* 返回导航 */
.back-nav {
  margin-bottom: 30px;
}

.back-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 16px;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  color: #64748b;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.back-btn:hover {
  background: #f1f5f9;
  color: #3182ce;
  border-color: #3182ce;
}

/* 加载和错误状态 */
.loading-container,
.error-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 20px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  min-height: 400px;
}

.loading-spinner {
  width: 50px;
  height: 50px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid #3182ce;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 20px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.loading-container p,
.error-container h3 {
  color: #64748b;
  font-size: 16px;
  margin: 0;
}

.error-container {
  text-align: center;
}

.error-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.error-container h3 {
  color: #e74c3c;
  font-size: 20px;
  margin-bottom: 8px;
}

.error-container p {
  color: #64748b;
  margin-bottom: 20px;
}

.retry-btn {
  background: #3182ce;
  color: white;
  border: none;
  padding: 12px 24px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: background 0.3s ease;
}

.retry-btn:hover {
  background: #2563eb;
}

/* 产品详情网格 */
.product-content {
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  overflow: hidden;
  background: transparent;
}

.product-detail-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 40px;
  padding: 40px;
  background: transparent;
}

/* 产品媒体展示（左侧缩略图 + 右侧主媒体） */
.product-images-section {
  display: flex;
  gap: 24px;
  align-items: stretch;
}

/* 缩略图整体包裹：左侧竖条，包含上下按钮和缩略图列表 */
.thumbnail-nav-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
  padding: 16px 8px;
  border-radius: 16px;
  background: transparent;
}

.image-thumbnails {
  display: flex;
  flex-direction: column;
  gap: 12px;
  max-height: 320px;
  overflow-y: auto;
  padding: 4px 0;
}

/* 缩略图翻页按钮：上 / 下 */
.thumb-arrow {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  border: 1px solid #e5e7eb;
  background: #ffffff;
  box-shadow: 0 1px 3px rgba(15,23,42,0.12);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  padding: 0;
}

.thumb-arrow::before {
  content: '';
  display: inline-block;
  border-style: solid;
  border-width: 0 2px 2px 0;
  border-color: #111827;
  padding: 4px;
}

.thumb-arrow-up::before {
  transform: rotate(-135deg);
}

.thumb-arrow-down::before {
  transform: rotate(45deg);
}

.thumbnail-item {
  flex: 0 0 auto;
  width: 80px;
  height: 80px;
  border: 2px solid #e2e8f0;
  border-radius: 6px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s ease;
  background: #fff;
}

.thumbnail-item img,
.thumbnail-video video {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

.thumbnail-video {
  position: relative;
  width: 100%;
  height: 100%;
}

.thumbnail-video-icon {
  position: absolute;
  right: 6px;
  bottom: 6px;
  width: 20px;
  height: 20px;
  border-radius: 50%;
  background: rgba(0, 0, 0, 0.7);
  color: #fff;
  font-size: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.thumbnail-item.active {
  border-color: #3182ce;
  box-shadow: 0 0 0 1px rgba(49, 130, 206, 0.4);
}

.thumbnail-item:hover {
  border-color: #3182ce;
  transform: scale(1.05);
}

/* 主媒体容器：中间白底区域 */
.main-image-container {
  position: relative;
  flex: 1;
  max-width: 520px;
  margin: 0 auto;
  aspect-ratio: 1;
  border-radius: 16px;
  background: #ffffff;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.main-product-image,
.main-product-video {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

/* 主图左右翻页按钮：左 / 右 */
.main-arrow {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  width: 36px;
  height: 36px;
  border-radius: 50%;
  border: 1px solid #e5e7eb;
  background: #ffffff;
  box-shadow: 0 2px 6px rgba(15,23,42,0.25);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  z-index: 5;
  padding: 0;
}

.main-arrow::before {
  content: '';
  display: inline-block;
  border-style: solid;
  border-width: 0 2px 2px 0;
  border-color: #111827;
  padding: 5px;
}

.main-arrow-left {
  left: 16px;
}

.main-arrow-left::before {
  transform: rotate(135deg);
}

.main-arrow-right {
  right: 16px;
}

.main-arrow-right::before {
  transform: rotate(-45deg);
}

.main-arrow:hover {
  background: #f9fafb;
  transform: translateY(-50%) scale(1.02);
}

/* 产品标签 - 隐藏Featured标签 */
.product-badge,
.featured-badge {
  display: none;
}

/* 产品信息区域 */
.product-info-section {
  display: flex;
  flex-direction: column;
  gap: 24px;
  background: transparent;
  min-height: 600px;
  position: relative;
}

.product-header {
  border-bottom: 1px solid #e5e7eb;
  padding-bottom: 24px;
}

.product-title {
  font-size: 28px;
  font-weight: 700;
  color: #ffffff;
  margin: 0 0 12px 0;
  line-height: 1.3;
}

.product-subtitle {
  font-size: 16px;
  color: #ffffff;
  margin: 0 0 16px 0;
  line-height: 1.5;
}

.product-category {
  display: flex;
  align-items: center;
  gap: 8px;
}

.category-label {
  font-size: 14px;
  color: #ffffff;
  font-weight: 500;
}

.category-value {
  font-size: 14px;
  color: #ffffff;
  font-weight: 600;
}

/* 产品规格：核心修改区域 */
.product-specifications {
  background: #2d3748;
  border: 1px solid #4a5568;
  border-radius: 8px;
  padding: 20px;
  max-height: none; /* 展开时不限制高度 */
  overflow-y: auto; /* 展开时允许垂直滚动 */
  position: relative;
  transition: max-height 0.3s ease-in-out;
}

/* 折叠状态的外层容器样式 */
.product-specifications.collapsed-container {
  max-height: 180px; /* 折叠时限制高度 */
  overflow: hidden; /* 折叠时截断溢出，禁用滚动 */
}

/* 折叠状态的遮罩：仅在折叠时显示 */
.product-specifications.collapsed-container::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 60px; /* 足够覆盖所有可能的文字溢出 */
  background: linear-gradient(transparent, #2d3748 50%);
  pointer-events: none;
  z-index: 2; /* 低于标题，高于规格内容 */
}

.spec-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
  position: sticky;
  top: 0;
  background: #2d3748;
  padding: 0 0 8px 0;
  z-index: 3; /* 高于遮罩，避免被覆盖 */
}

.spec-title {
  font-size: 16px;
  font-weight: 600;
  color: #f7fafc;
  margin: 0;
}

.spec-toggle-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 28px;
  height: 28px;
  border: none;
  background: #4a5568;
  color: #cbd5e0;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.spec-toggle-btn:hover {
  background: #718096;
  color: #f7fafc;
}

.spec-toggle-btn.expanded svg {
  transform: rotate(180deg);
}

.spec-toggle-btn svg {
  transition: transform 0.3s ease;
}

.spec-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 12px;
  position: relative;
  z-index: 1; /* 低于标题和遮罩 */
}

/* 单个规格项：限制高度和溢出，避免文字换行溢出 */
.spec-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 0;
  border-bottom: 1px solid #4a5568;
  line-height: 1.4;
  max-height: 40px; /* 限制单个项高度 */
  overflow: hidden; /* 截断单个项内的文字 */
}

.spec-item:last-child {
  border-bottom: none;
}

.spec-label {
  font-size: 14px;
  color: #cbd5e0;
  font-weight: 500;
}

.spec-value {
  font-size: 14px;
  color: #f7fafc;
  font-weight: 600;
}

/* 滚动条样式：仅在展开状态显示（折叠时不会滚动） */
.product-specifications::-webkit-scrollbar {
  width: 6px;
}

.product-specifications::-webkit-scrollbar-track {
  background: #4a5568;
  border-radius: 3px;
}

.product-specifications::-webkit-scrollbar-thumb {
  background: #718096;
  border-radius: 3px;
}

.product-specifications::-webkit-scrollbar-thumb:hover {
  background: #a0aec0;
}

/* 产品特点（已移除默认值，相关样式保留但无实际作用） */
.product-features {
  background: #f0fdf4;
  border-radius: 8px;
  padding: 20px;
}

.features-title {
  font-size: 16px;
  font-weight: 600;
  color: #ffffff;
  margin: 0 0 16px 0;
}

.features-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.feature-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 0;
  color: #059669;
  font-size: 14px;
  font-weight: 500;
}

.feature-item svg {
  flex-shrink: 0;
  color: #10b981;
}

/* 库存信息 */
.stock-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.stock-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background: #f8fafc;
  border-radius: 6px;
}

.stock-label {
  font-size: 14px;
  color: #64748b;
  font-weight: 500;
}

.stock-value {
  font-size: 14px;
  font-weight: 600;
}

.stock-value.in-stock {
  color: #10b981;
}

.stock-value.out-of-stock {
  color: #ef4444;
}

/* 联系按钮 */
.contact-actions {
  display: flex;
  gap: 16px;
  margin-top: auto;
  padding-top: 20px;
  padding: 16px;
  margin: 20px -40px -40px -40px;
}

.whatsapp-btn,
.inquiry-btn {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 14px 20px;
  border: none;
  border-radius: 8px;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.whatsapp-btn {
  background: #25d366;
  color: white;
}

.whatsapp-btn:hover {
  background: #128c7e;
  transform: translateY(-1px);
}

.inquiry-btn {
  background: #3182ce;
  color: white;
}

.inquiry-btn:hover {
  background: #2563eb;
  transform: translateY(-1px);
}

/* 分享功能 */
.share-section {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px 0;
  border-top: 1px solid #e5e7eb;
}

.share-label {
  font-size: 14px;
  color: #64748b;
  font-weight: 500;
}

.share-buttons {
  display: flex;
  gap: 8px;
}

.share-btn {
  width: 36px;
  height: 36px;
  border: 1px solid #e2e8f0;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
}

.share-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.share-btn.facebook:hover {
  background: #1877f2;
  color: white;
  border-color: #1877f2;
}

.share-btn.twitter:hover {
  background: #1da1f2;
  color: white;
  border-color: #1da1f2;
}

.share-btn.linkedin:hover {
  background: #0077b5;
  color: white;
  border-color: #0077b5;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .product-detail-grid {
    grid-template-columns: 1fr;
    gap: 30px;
    padding: 30px;
  }

  .product-title {
    font-size: 24px;
  }
}

@media (max-width: 768px) {
  .product-detail-container {
    padding: 20px 0;
  }

  .product-detail-grid {
    padding: 20px;
    gap: 24px;
  }

  .product-title {
    font-size: 20px;
  }

  .main-image-container {
    aspect-ratio: auto; /* 移除强制正方形比例 */
    min-height: 350px; /* 设置最小高度 */
    max-height: 600px; /* 设置最大高度 */
  }

  .main-product-image {
    object-fit: contain; /* 改为contain以显示完整图片 */
    height: auto; /* 允许高度自适应 */
    max-height: 600px; /* 限制最大高度 */
    padding: 15px; /* 添加内边距 */
    box-sizing: border-box;
  }

  .main-product-video {
    height: auto;
    max-height: 600px;
    object-fit: contain;
  }

  .contact-actions {
    flex-direction: column;
  }

  .whatsapp-btn,
  .inquiry-btn {
    padding: 12px 16px;
    font-size: 14px;
  }

  .spec-grid {
    grid-template-columns: 1fr;
  }

  /* 移动端调整折叠高度和遮罩 */
  .product-specifications.collapsed-container {
    max-height: 150px;
  }

  .product-specifications.collapsed-container::after {
    height: 50px;
  }
}

@media (max-width: 480px) {
  .container {
    padding: 0 12px;
  }

  .product-detail-grid {
    padding: 16px;
  }

  .product-title {
    font-size: 18px;
  }

  .main-image-container {
    border-radius: 6px;
    aspect-ratio: auto; /* 移除强制正方形比例 */
    min-height: 300px; /* 设置最小高度 */
    max-height: 500px; /* 设置最大高度 */
  }

  .main-product-image {
    object-fit: contain; /* 改为contain以显示完整图片 */
    width: 100%;
    height: auto; /* 允许高度自适应 */
    max-height: 500px; /* 限制最大高度 */
    padding: 10px; /* 添加内边距 */
    box-sizing: border-box;
  }

  .main-product-video {
    width: 100%;
    height: auto;
    max-height: 500px;
    object-fit: contain;
  }

  .thumbnail-item {
    width: 60px;
    height: 60px;
  }

  /* 小屏移动端调整折叠高度和遮罩 */
  .product-specifications.collapsed-container {
    max-height: 130px;
  }

  .product-specifications.collapsed-container::after {
    height: 40px;
  }
}
</style>