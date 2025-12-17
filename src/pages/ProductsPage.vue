<template>
  <div class="b2b-marketplace-page">
    <!-- 公司信息展示区域 -->
    <CompanyInfoSection />

    <!-- 导航头部 -->
    <Header />

    <!-- B2B电商主体区域 -->
    <div class="b2b-marketplace-container">
      <!-- 顶部控制栏 -->
      <div class="marketplace-header">
        <div class="header-center">
          <h1 class="page-title">{{ currentCategoryTitle }}</h1>
        </div>
      </div>

      <!-- 左侧分类导航 + 右侧商品展示 -->
      <div class="marketplace-content">
        <!-- 左侧分类导航容器 -->
        <aside class="category-navigation">
          <!-- Product Categories 盒子 -->
          <div class="categories-box">
            <h3 class="nav-title">Product Categories</h3>

            <!-- 三级分类导航系统 -->
            <div class="hierarchical-navigation"
                 @mouseleave="handleNavigationLeave"
                 @mouseenter="handleNavigationEnter">

              
              <!-- 一级分类栏 -->
              <nav class="first-level-nav">
                <div
                  v-for="category in firstLevelCategories"
                  :key="category.id"
                  class="first-level-item"
                  :class="{
                    hovered: hoveredFirstLevel === category.id,
                    'has-children': category.hasChildren,
                    'pinned': isMenuPinned && clickedFirstLevel === category.id
                  }"
                  @mouseenter="handleFirstLevelHover(category.id)"
                  @click="handleFirstLevelClick(category)"
                >
                  <div class="category-content">
                    <span class="category-name">{{ category.name }}</span>
                    <svg v-if="category.hasChildren" class="arrow-icon" width="12" height="12" viewBox="0 0 12 12" fill="none">
                      <path d="M4 2L8 6L4 10" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
                    </svg>
                  </div>
                </div>
              </nav>

              <!-- 二级分类下拉层 -->
              <div
                v-if="showSecondLevel && hoveredFirstLevel"
                class="second-level-dropdown active"
                @mouseenter="handleSecondLevelEnter"
                @mouseleave="handleNavigationLeave"
              >
                <div class="dropdown-header">
                  <h4>{{ getFirstLevelCategoryName(hoveredFirstLevel) }}</h4>
                </div>
                <div class="second-level-list">
                  <div
                    v-for="secondCategory in getSecondLevelCategories(hoveredFirstLevel)"
                    :key="secondCategory.id"
                    class="second-level-item"
                    :data-category-id="secondCategory.id"
                    :class="{ hovered: hoveredSecondLevel === secondCategory.id }"
                    @mouseenter="handleSecondLevelHover(secondCategory.id)"
                    @click="handleSecondLevelClick(secondCategory)"
                    @touchstart="handleSecondLevelTouchStart(secondCategory.id)"
                    @touchend="handleSecondLevelTouchEnd(secondCategory.id)"
                  >
                    <div class="subcategory-content">
                      <span class="subcategory-name">{{ secondCategory.name }}</span>
                      <svg v-if="secondCategory.hasChildren" class="arrow-icon" width="10" height="10" viewBox="0 0 10 10" fill="none">
                        <path d="M3 2L7 5L3 8" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
                      </svg>
                    </div>
                  </div>
                </div>
              </div>

              <!-- 三级分类下拉层 - 独立于二级菜单 -->
              <div
                v-if="showThirdLevel && hoveredSecondLevel"
                class="third-level-dropdown standalone"
                :class="{ active: showThirdLevel }"
                @mouseenter="handleThirdLevelEnter"
                @mouseleave="handleThirdLevelLeave"
              >
                <div class="dropdown-header">
                  <h4>{{ getSecondLevelCategoryName(hoveredSecondLevel) }}</h4>
                </div>
                <div class="third-level-list">
                  <div
                    v-for="thirdCategory in getThirdLevelCategories(hoveredSecondLevel)"
                    :key="thirdCategory.id"
                    class="third-level-item"
                    @click="selectThirdLevelCategory(thirdCategory)"
                  >
                    <div class="final-category-content">
                      <span class="final-category-name">{{ thirdCategory.name }}</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>

            </div>

          <!-- Hot Products 盒子 -->
          <div class="hot-products-box" v-if="paginatedHotProducts.length > 0">
            <h3 class="section-title">Hot Products</h3>
            <div class="hot-products-list">
              <div
                v-for="product in paginatedHotProducts"
                :key="product.id"
                class="hot-product-item"
                @click="viewProductDetail(product.id)"
              >
                <img :src="product.image" :alt="product.name" class="hot-product-image" />
                <div class="hot-product-info">
                  <h5 class="hot-product-name">{{ product.name }}</h5>
                </div>
              </div>
            </div>

            <!-- 热门商品分页控件 -->
            <div class="hot-products-pagination" v-if="hotProductsTotalPages > 1">
              <button
                class="pagination-btn prev-btn"
                :disabled="hotProductsCurrentPage === 1"
                @click="prevHotProductsPage"
              >
                <svg width="12" height="12" viewBox="0 0 12 12" fill="none">
                  <path d="M7 2L3 6L7 10" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
              </button>

              <div class="pagination-dots">
                <span
                  v-for="page in hotProductsTotalPages"
                  :key="page"
                  class="pagination-dot"
                  :class="{ active: page === hotProductsCurrentPage }"
                  @click="goToHotProductsPage(page)"
                ></span>
              </div>

              <button
                class="pagination-btn next-btn"
                :disabled="hotProductsCurrentPage === hotProductsTotalPages"
                @click="nextHotProductsPage"
              >
                <svg width="12" height="12" viewBox="0 0 12 12" fill="none">
                  <path d="M5 2L9 6L5 10" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
                </svg>
              </button>
            </div>
          </div>
        </aside>

        <!-- 右侧商品展示区域 -->
        <main class="products-main">
          <!-- B2B 商品网格展示 -->
          <div v-if="paginatedProducts.length > 0" class="b2b-products-grid">
            <div
              v-for="product in paginatedProducts"
              :key="product.id"
              class="b2b-product-card"
              @click="viewProductDetail(product.id)"
            >
              <!-- 商品图片区域 -->
              <div class="b2b-product-image-container">
                <img
                  :src="product.image"
                  :alt="product.name"
                  class="b2b-product-image"
                  @error="handleImageError"
                />

                </div>

              <!-- 商品信息区域 -->
              <div class="b2b-product-info">
                <!-- 商品标题 -->
                <h3 class="b2b-product-title">{{ product.name }}</h3>
                <p class="b2b-product-subtitle">{{ product.description }}</p>

                <!-- 商品规格信息 -->
                <!-- <div class="b2b-product-specs">
                  <div class="spec-item">
                    <span class="spec-label">Material:</span>
                    <span class="spec-value">{{ product.specifications?.Material || 'Premium Cotton' }}</span>
                  </div>
                  <div class="spec-item">
                    <span class="spec-label">Style:</span>
                    <span class="spec-value">{{ product.specifications?.Style || 'Fashion' }}</span>
                  </div>
                </div> -->

  
                <!-- Chat Now 按钮 -->
                <!-- <button
                  class="b2b-chat-btn"
                  @click.stop="contactWhatsApp(product)"
                >
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor">
                    <path d="M.057 24l1.687-6.163c-1.041-1.804-1.588-3.849-1.587-5.946.003-6.556 5.338-11.891 11.893-11.891 3.181.001 6.167 1.24 8.413 3.488 2.245 2.248 3.481 5.236 3.48 8.414-.003 6.557-5.338 11.892-11.893 11.892-1.99-.001-3.951-.5-5.688-1.448l-6.305 1.654zm6.597-3.807c1.676.995 3.276 1.591 5.392 1.592 5.448 0 9.886-4.434 9.889-9.885.002-5.462-4.415-9.89-9.881-9.892-5.452 0-9.887 4.434-9.889 9.884-.001 2.225.651 3.891 1.746 5.634l-.999 3.648 3.742-.981zm11.387-5.464c-.074-.124-.272-.198-.57-.347-.297-.149-.756-.374-1.243-.623-.487-.248-.84-.124-.967.074-.074.124-.347.436-.545.657-.198.223-.396.248-.693.099-.297-.149-1.255-.463-2.39-1.475-.883-.788-1.48-1.761-1.653-2.059-.173-.297-.018-.458.13-.606.134-.133.297-.347.446-.52.149-.174.198-.298.297-.497.099-.198.05-.372-.025-.52-.074-.149-.669-1.612-.916-2.207-.242-.579-.487-.5-.669-.51-.173-.008-.372-.01-.57-.01-.198 0-.52.074-.792.372-.272.297-1.04 1.016-1.04 2.479 0 1.462 1.065 2.875 1.213 3.074.149.198 2.096 3.2 5.077 4.487.709.306 1.262.489 1.694.625.712.227 1.36.195 1.871.118.571-.085 1.758-.719 2.006-1.413.248-.694.248-1.289.173-1.413z"/>
                  </svg>
                  Chat now
                </button> -->
              </div>
            </div>
          </div>

          <!-- 加载状态 -->
          <div v-else-if="isLoading" class="b2b-loading-state">
            <div class="loading-spinner"></div>
            <p>Loading products...</p>
          </div>

          <!-- 空状态 -->
          <div v-else class="b2b-empty-state">
            <div class="empty-icon">📦</div>
            <h3>No products found</h3>
            <p>Try adjusting your filters or search criteria</p>
          </div>

          <!-- B2B 分页组件 -->
          <div class="b2b-pagination-section">
            <Pagination
              :current-page="currentPage"
              :total-pages="totalPages"
              @page-change="handlePageChange"
            />
          </div>
        </main>
      </div>
    </div>

    <!-- 底部 -->
    <Footer />
  </div>
</template>

<script>
import CompanyInfoSection from '../components/CompanyInfoSection.vue'
import Header from '../components/Header.vue'
import Footer from '../components/Footer.vue'
import Pagination from '../components/Pagination.vue'
import { getAllProducts, getProductsByCategory, searchProducts, getProductCategories } from '../api/products'
import { buildProductWhatsAppUrl } from '@/config/whatsapp'
import { openWhatsAppUrl } from '@/utils/whatsappHelper'

export default {
  name: 'ProductsPage',
  components: {
    CompanyInfoSection,
    Header,
    Footer,
    Pagination
  },
  data() {
    return {
      // 加载状态
      isLoading: true,
      
      // 基础数据
      products: [],
      selectedCategory: 'all',
      currentPage: 1,
      viewMode: 'grid',
      showFilterMenu: false,
      currentFilter: 'top-picks',
      itemsPerPage: 8,
      hotProducts: [],
      hotProductsPerPage: 4, // 每页显示4个热门商品
      hotProductsCurrentPage: 1, // 当前页码

      // 筛选选项
      filterOptions: [
        { value: 'top-picks', label: 'Top picks' },
        { value: 'price-low', label: 'Price: Low to High' },
        { value: 'price-high', label: 'Price: High to Low' },
        { value: 'newest', label: 'Newest First' },
        { value: 'rating', label: 'Highest Rated' }
      ],

      // 三级导航状态
      hoveredFirstLevel: null,
      hoveredSecondLevel: null,
      selectedFirstLevel: null,
      selectedSecondLevel: null,
      selectedThirdLevel: null,
      showSecondLevel: false,
      showThirdLevel: false,
      navigationTimer: null,

      // 点击展开状态
      clickedFirstLevel: null,
      isMenuPinned: false, // 是否固定显示菜单

      // 三级分类数据结构
      firstLevelCategories: [
        {
          id: 'men',
          name: 'Men',
          icon: '👔',
          description: 'Men\'s clothing and accessories',
          hasChildren: true
        },
        {
          id: 'women',
          name: 'Women',
          icon: '👗',
          description: 'Women\'s clothing and accessories',
          hasChildren: true
        },
        {
          id: 'children',
          name: 'Children',
          icon: '👶',
          description: 'Kids and baby clothing',
          hasChildren: true
        },
        {
          id: 'denim-accessories',
          name: 'Denim hat/Denim bag',
          icon: '🧢',
          description: 'Denim accessories and bags',
          hasChildren: true
        },
        {
          id: 'customization',
          name: 'Customization services',
          icon: '✨',
          description: 'Custom denim processing services',
          hasChildren: true
        },
        {
          id: 'ungrouped',
          name: 'Ungrouped',
          icon: '📦',
          description: 'Other products and categories',
          hasChildren: true
        }
      ],

      // 二级分类数据
      secondLevelCategories: {
        men: [
          { id: 'men-bottoms', name: 'Bottoms', count: 15, hasChildren: true },
          { id: 'men-tops', name: 'Tops', count: 22, hasChildren: true },
          { id: 'men-coat-shirts', name: 'Coat/Shirt', count: 18, hasChildren: true }
        ],
        women: [
          { id: 'women-bottoms', name: 'Bottoms', count: 20, hasChildren: true },
          { id: 'women-tops', name: 'Tops', count: 25, hasChildren: true },
          { id: 'women-coat-shirts', name: 'Coat/Shirt', count: 16, hasChildren: true },
          { id: 'women-maternity', name: 'Maternity clothing', count: 12, hasChildren: true }
        ],
        children: [
          { id: 'children-girls', name: 'Girls', count: 18, hasChildren: true },
          { id: 'children-boys', name: 'Boys', count: 20, hasChildren: true }
        ],
        'denim-accessories': [
          { id: 'denim-products', name: 'Denim Products', count: 8, hasChildren: true }
        ],
        customization: [
          { id: 'custom-services', name: 'Customization Services', count: 6, hasChildren: true }
        ],
        ungrouped: [
          { id: 'other-products', name: 'Other Products', count: 4, hasChildren: true }
        ]
      },

      // 三级分类数据
      thirdLevelCategories: {
        'men-bottoms': [
          { id: 'men-trousers', name: 'Trousers', count: 8 },
          { id: 'men-shorts', name: 'Shorts', count: 7 }
        ],
        'men-tops': [
          { id: 'men-hoodie', name: 'Hoodie', count: 10 },
          { id: 'men-tshirt', name: 'T-shirt', count: 12 }
        ],
        'men-coat-shirts': [
          { id: 'men-suit', name: 'Suit', count: 6 },
          { id: 'men-dress-shirt', name: 'Dress Shirt', count: 7 },
          { id: 'men-casual-shirt', name: 'Casual Shirt', count: 5 }
        ],
        'women-bottoms': [
          { id: 'women-trousers', name: 'Trousers', count: 10 },
          { id: 'women-shorts', name: 'Shorts', count: 6 },
          { id: 'women-skirts', name: 'Skirts', count: 4 }
        ],
        'women-tops': [
          { id: 'women-hoodie', name: 'Hoodie', count: 8 },
          { id: 'women-tshirt', name: 'T-shirt', count: 10 },
          { id: 'women-blouse', name: 'Blouse', count: 7 }
        ],
        'women-coat-shirts': [
          { id: 'women-suit', name: 'Suit', count: 5 },
          { id: 'women-blouse', name: 'Blouse', count: 6 },
          { id: 'women-dress-shirt', name: 'Dress Shirt', count: 5 }
        ],
        'women-maternity': [
          { id: 'women-maternity-tops', name: 'Maternity Tops', count: 4 },
          { id: 'women-maternity-bottoms', name: 'Maternity Bottoms', count: 4 },
          { id: 'women-maternity-dresses', name: 'Maternity Dresses', count: 4 }
        ],
        'children-girls': [
          { id: 'girls-trousers', name: 'Trousers', count: 10 },
          { id: 'girls-tops', name: 'Tops', count: 8 }
        ],
        'children-boys': [
          { id: 'boys-trousers', name: 'Trousers', count: 12 },
          { id: 'boys-tops', name: 'Tops', count: 8 }
        ],
        'denim-products': [
          { id: 'denim-baseball-cap', name: 'Denim Baseball Cap', count: 3 },
          { id: 'denim-bucket-hat', name: 'Denim Bucket Hat', count: 2 },
          { id: 'denim-backpack', name: 'Denim Backpack', count: 3 }
        ],
        'custom-services': [
          { id: 'custom-embroidery', name: 'Custom Embroidery', count: 3 },
          { id: 'custom-printing', name: 'Custom Printing', count: 3 }
        ],
        'other-products': [
          { id: 'uncategorized', name: 'Uncategorized', count: 4 }
        ]
      }
    }
  },
  computed: {
    // 顶部标题：随左侧分类导航联动
    currentCategoryTitle() {
      // 若选中了三级分类，优先显示完整路径
      if (this.selectedThirdLevel) {
        const secondName = this.getSecondLevelCategoryName(this.selectedSecondLevel)
        const secondId = this.selectedSecondLevel
        let thirdName = ''

        if (secondId && this.thirdLevelCategories[secondId]) {
          const third = this.thirdLevelCategories[secondId].find(item => item.id === this.selectedThirdLevel)
          thirdName = third ? third.name : ''
        }

        if (secondName && thirdName) {
          return `${secondName} > ${thirdName}`
        }
        if (thirdName) return thirdName
      }

      // 若选中了二级分类
      if (this.selectedSecondLevel) {
        const firstName = this.getFirstLevelCategoryName(this.selectedFirstLevel)
        const secondName = this.getSecondLevelCategoryName(this.selectedSecondLevel)

        if (firstName && secondName) {
          return `${firstName} > ${secondName}`
        }
        if (secondName) return secondName
      }

      // 若只选中了一级分类
      if (this.selectedFirstLevel) {
        const firstName = this.getFirstLevelCategoryName(this.selectedFirstLevel)
        if (firstName) return firstName
      }

      // 默认标题
      return 'All products'
    },

    // 分页后的热门商品
    paginatedHotProducts() {
      const start = (this.hotProductsCurrentPage - 1) * this.hotProductsPerPage
      const end = start + this.hotProductsPerPage
      return this.hotProducts.slice(start, end)
    },

    // 热门商品总页数
    hotProductsTotalPages() {
      return Math.ceil(this.hotProducts.length / this.hotProductsPerPage)
    },

    // 显示的产品（根据分类筛选）
    displayProducts() {
      if (!this.selectedCategory || this.selectedCategory === 'all') {
        return this.products
      }

      // 支持多级分类路径匹配
      return this.products.filter(product => {
        if (!product.category) return false

        // 精确匹配
        if (product.category === this.selectedCategory) {
          return true
        }

        // 路径匹配（如果选中了多级分类路径，匹配包含该路径的产品）
        if (this.selectedCategory.includes(' > ')) {
          return product.category.includes(this.selectedCategory) ||
                 this.selectedCategory.includes(product.category)
        }

        // 部分匹配
        return product.category.includes(this.selectedCategory) ||
               this.selectedCategory.includes(product.category)
      })
    },

    // 排序后的产品
    sortedProducts() {
      const products = [...this.displayProducts]

      switch (this.currentFilter) {
        case 'price-low':
          return products.sort((a, b) => a.price - b.price)
        case 'price-high':
          return products.sort((a, b) => b.price - a.price)
        case 'newest':
          return products.sort((a, b) => b.isNew - a.isNew)
        case 'rating':
          return products.sort((a, b) => b.rating - a.rating)
        default:
          return products
      }
    },

    // 当前页的产品
    paginatedProducts() {
      const start = (this.currentPage - 1) * this.itemsPerPage
      const end = start + this.itemsPerPage
      return this.sortedProducts.slice(start, end)
    },

    // 总页数
    totalPages() {
      return Math.ceil(this.displayProducts.length / this.itemsPerPage)
    }
  },

  async mounted() {
    this.isLoading = true
    
    try {
      // 检查是否有搜索关键词
      const searchKeyword = this.$route.query.search

      if (searchKeyword) {
        // 有搜索关键词时，并行加载分类和搜索结果
        await Promise.all([
          this.loadCategories(),
          this.searchProducts(searchKeyword)
        ])
      } else {
        // 无搜索关键词时，并行加载分类和产品
        await Promise.all([
          this.loadCategories(),
          this.loadProducts()
        ])
      }
    } catch (error) {
      console.error('❌ [ProductsPage] 页面加载失败:', error)
    } finally {
      this.isLoading = false
    }

    this.setupEventListeners()

    // 监听路由变化，支持在产品页面内搜索
    this.$watch(
      () => this.$route.query.search,
      (newSearchKeyword) => {
        if (newSearchKeyword) {
          console.log(`🔍 [ProductsPage] 路由搜索关键词变化: "${newSearchKeyword}"`)
          this.searchProducts(newSearchKeyword)
        } else {
          console.log('🔄 [ProductsPage] 清除搜索，加载所有产品')
          this.loadProducts()
        }
      }
    )

    },

  methods: {
    // 加载分类列表
    async loadCategories() {
      try {
        console.log('📑 [ProductsPage] 开始加载分类列表...')
        const response = await getProductCategories()
        console.log('✅ [ProductsPage] 分类API完整响应:', response)
        console.log('📦 [ProductsPage] response.data:', response.data)

        let categories = null
        if (response && response.data) {
          // 尝试多种可能的数据格式
          if (response.data.data && Array.isArray(response.data.data)) {
            categories = response.data.data
          } else if (Array.isArray(response.data)) {
            categories = response.data
          }
        }

        if (categories && Array.isArray(categories) && categories.length > 0) {
          console.log('✅ [ProductsPage] 获取到分类列表:', categories)

          // 解析分类路径并构建三级分类结构
          this.buildCategoryHierarchy(categories)

          console.log('✅ [ProductsPage] 分类列表构建完成:')
          console.log('  - 一级分类:', this.firstLevelCategories)
          console.log('  - 二级分类:', this.secondLevelCategories)
          console.log('  - 三级分类:', this.thirdLevelCategories)
        } else {
          console.warn('⚠️ [ProductsPage] 未能获取分类列表，使用默认分类')
        }
      } catch (error) {
        console.error('❌ [ProductsPage] 加载分类列表失败:', error)
        console.error('❌ [ProductsPage] 错误详情:', error.response || error.message)
        // 失败时保留原有的静性分类
      }
    },

    // 构建分类层级结构
    buildCategoryHierarchy(categories) {
      const firstLevel = new Map()
      const secondLevel = {}
      const thirdLevel = {}

      categories.forEach(categoryPath => {
        // 按 > 分割分类路径
        const parts = categoryPath.split('>').map(part => part.trim())
        
        if (parts.length === 1) {
          // 只有一级分类
          const firstCat = parts[0]
          const firstId = firstCat.toLowerCase().replace(/\s+/g, '-')
          if (!firstLevel.has(firstId)) {
            firstLevel.set(firstId, {
              id: firstId,
              name: firstCat,
              icon: '📎',
              description: firstCat
            })
          }
        } else if (parts.length === 2) {
          // 有二级分类
          const [firstCat, secondCat] = parts
          const firstId = firstCat.toLowerCase().replace(/\s+/g, '-')
          const secondId = `${firstId}-${secondCat.toLowerCase().replace(/\s+/g, '-')}`
          
          // 添加一级分类
          if (!firstLevel.has(firstId)) {
            firstLevel.set(firstId, {
              id: firstId,
              name: firstCat,
              icon: '📎',
              description: firstCat
            })
          }
          
          // 添加二级分类
          if (!secondLevel[firstId]) {
            secondLevel[firstId] = []
          }
          if (!secondLevel[firstId].find(c => c.id === secondId)) {
            secondLevel[firstId].push({
              id: secondId,
              name: secondCat,
              hasChildren: false
            })
          }
        } else if (parts.length >= 3) {
          // 有三级分类
          const [firstCat, secondCat, thirdCat] = parts
          const firstId = firstCat.toLowerCase().replace(/\s+/g, '-')
          const secondId = `${firstId}-${secondCat.toLowerCase().replace(/\s+/g, '-')}`
          const thirdId = `${secondId}-${thirdCat.toLowerCase().replace(/\s+/g, '-')}`
          
          // 添加一级分类
          if (!firstLevel.has(firstId)) {
            firstLevel.set(firstId, {
              id: firstId,
              name: firstCat,
              icon: '📎',
              description: firstCat
            })
          }
          
          // 添加二级分类
          if (!secondLevel[firstId]) {
            secondLevel[firstId] = []
          }
          let secondCatObj = secondLevel[firstId].find(c => c.id === secondId)
          if (!secondCatObj) {
            secondCatObj = {
              id: secondId,
              name: secondCat,
              hasChildren: true
            }
            secondLevel[firstId].push(secondCatObj)
          } else {
            secondCatObj.hasChildren = true
          }
          
          // 添加三级分类
          if (!thirdLevel[secondId]) {
            thirdLevel[secondId] = []
          }
          if (!thirdLevel[secondId].find(c => c.id === thirdId)) {
            thirdLevel[secondId].push({
              id: thirdId,
              name: thirdCat
            })
          }
        }
      })

      // 转换 Map 为数组,并标记是否有子分类
      this.firstLevelCategories = Array.from(firstLevel.values()).map(category => ({
        ...category,
        hasChildren: !!secondLevel[category.id] && secondLevel[category.id].length > 0
      }))
      this.secondLevelCategories = secondLevel
      this.thirdLevelCategories = thirdLevel
    },

    // 加载商品数据
    async loadProducts() {
      try {
        console.log('🔄 [ProductsPage] 开始加载商品数据...')

        // 获取所有产品（不分页，获取前100个）
        const response = await getAllProducts({ page: 1, size: 100 })
        console.log('✅ [ProductsPage] API响应:', response)

        // 处理API响应格式
        if (response && response.data && response.data.data && response.data.data.content && Array.isArray(response.data.data.content)) {
          const apiProducts = response.data.data.content
          console.log('✅ [ProductsPage] 获取到API产品数据:', apiProducts.length, '个商品')

          // 筛选上架产品 (status: 1)
          const activeProducts = apiProducts.filter(product => product.status === 1)
          console.log('✅ [ProductsPage] 筛选后上架产品:', activeProducts.length, '个商品')

          this.products = activeProducts.map(product => ({
            id: product.id || 'product_' + Math.random(),
            name: product.name || product.productName || 'Product Name',
            description: product.description || 'Product Description',
            price: product.price || 0,
            moq: product.stockQuantity || 100,
            image: product.mainImage || '/images/placeholder-400x400.svg',
            isNew: !product.createdAt || (new Date() - new Date(product.createdAt)) < 7 * 24 * 60 * 60 * 1000, // 7天内为新品
            rating: 4.0 + Math.random(),
            isFeatured: product.isFeatured || false,
            viewCount: 0,
            tags: product.tags && Array.isArray(product.tags) ? product.tags : (product.tags && typeof product.tags === 'string' ? product.tags.split(',').map(tag => tag.trim()) : ['Fashion', 'Quality']),
            category: product.category || 'Uncategorized',
            specifications: {
              Material: product.material || 'Premium Cotton',
              Style: product.style || 'Fashion',
              'Fabric Type': product.fabricType || 'Denim',
              Origin: product.origin || 'Imported'
            }
          }))

          console.log('✅ [ProductsPage] 商品数据处理完成:', this.products.length, '个商品')

          // 设置热门商品 - 取前8个推荐商品
          this.hotProducts = this.products
            .filter(p => p.isFeatured)
            .slice(0, 8)
            .map(product => ({
              id: product.id,
              name: product.name,
              price: product.price,
              image: product.image,
              category: product.category
            }))

          // 更新分类计数
          this.updateCategoryCounts()
        } else {
          console.error('⚠️ [ProductsPage] API返回空数据或格式错误', response?.data)
          this.products = []
        }
      } catch (error) {
        console.error('❌ [ProductsPage] 加载商品失败:', error)
        this.products = []
      }
    },

    // 更新分类计数
    updateCategoryCounts() {
      // 更新三级分类中的产品计数
      this.updateThirdLevelCategoryCounts()
    },

    // 更新三级分类的产品计数
    updateThirdLevelCategoryCounts() {
      // 统计产品到三级分类的映射
      const productCountMap = {}

      this.products.forEach(product => {
        if (product.category) {
          // 根据产品的分类路径统计到对应的分类
          const categoryPath = product.category

          // 尝试匹配到具体的三级分类
          for (const [secondLevelId, thirdCategories] of Object.entries(this.thirdLevelCategories)) {
            for (const thirdCategory of thirdCategories) {
              if (categoryPath.includes(thirdCategory.name)) {
                const key = `${secondLevelId}-${thirdCategory.id}`
                productCountMap[key] = (productCountMap[key] || 0) + 1
              }
            }
          }
        }
      })

      // 更新三级分类计数
      for (const [secondLevelId, thirdCategories] of Object.entries(this.thirdLevelCategories)) {
        thirdCategories.forEach(thirdCategory => {
          const key = `${secondLevelId}-${thirdCategory.id}`
          thirdCategory.count = productCountMap[key] || 0
        })
      }

      // 更新二级分类计数（将其子分类的计数相加）
      for (const [, secondCategories] of Object.entries(this.secondLevelCategories)) {
        secondCategories.forEach(secondCategory => {
          let totalCount = 0
          if (this.thirdLevelCategories[secondCategory.id]) {
            this.thirdLevelCategories[secondCategory.id].forEach(third => {
              totalCount += third.count || 0
            })
          }
          secondCategory.count = totalCount
        })
      }
    },

    // 切换筛选菜单
    toggleFilterMenu() {
      this.showFilterMenu = !this.showFilterMenu
    },

    // 选择筛选条件
    selectFilter(filterValue) {
      this.currentFilter = filterValue
      this.showFilterMenu = false
      this.currentPage = 1
    },

    // 获取筛选标签
    getFilterLabel(filterValue) {
      const filter = this.filterOptions.find(f => f.value === filterValue)
      return filter ? filter.label : 'Top picks'
    },

    // 生成星级显示
    generateStars(rating) {
      const fullStars = Math.floor(rating)
      const halfStar = rating % 1 >= 0.5
      const emptyStars = 5 - fullStars - (halfStar ? 1 : 0)
      return '★'.repeat(fullStars) + (halfStar ? '☆' : '') + '☆'.repeat(emptyStars)
    },

    // 格式化价格
    formatPrice(price) {
      return price.toFixed(2)
    },

    // 查看产品详情
    viewProductDetail(productId) {
      this.$router.push(`/product/${productId}`)
    },

    // WhatsApp联系
    contactWhatsApp(product) {
      // 使用统一的WhatsApp配置和产品相关消息模板
      const productInfo = {
        productName: product.name,
        productPrice: product.price ? `$${product.price}` : 'pricing information',
        customOption: 'customization options'
      }

      const whatsappUrl = buildProductWhatsAppUrl(productInfo)
      openWhatsAppUrl(whatsappUrl)
    },

    // 搜索产品
    async searchProducts(keyword) {
      try {
        console.log(`🔍 [ProductsPage] 开始搜索产品: "${keyword}"`)

        // 每次都进行新的搜索，确保获取最新的上架产品数据
        const searchResult = await searchProducts(keyword, 1, 100)
        console.log('✅ [ProductsPage] 搜索API响应:', searchResult)

        if (searchResult && searchResult.data && searchResult.data.data && searchResult.data.data.content && Array.isArray(searchResult.data.data.content)) {
          const searchProducts = searchResult.data.data.content
          console.log('✅ [ProductsPage] 获取到搜索结果:', searchProducts.length, '个商品')

          // 筛选上架产品 (status: 1)
          const activeProducts = searchProducts.filter(product => product.status === 1)
          console.log('✅ [ProductsPage] 搜索结果筛选后上架产品:', activeProducts.length, '个商品')

          this.products = activeProducts.map(product => ({
            id: product.id || 'product_' + Math.random(),
            name: product.name || product.productName || 'Product Name',
            description: product.description || 'Product Description',
            price: product.price || 0,
            moq: product.stockQuantity || 100,
            image: product.mainImage || '/images/placeholder-400x400.svg',
            isNew: !product.createdAt || (new Date() - new Date(product.createdAt)) < 7 * 24 * 60 * 60 * 1000,
            rating: 4.0 + Math.random(),
            isFeatured: product.isFeatured || false,
            viewCount: 0,
            tags: product.tags && Array.isArray(product.tags) ? product.tags : (product.tags && typeof product.tags === 'string' ? product.tags.split(',').map(tag => tag.trim()) : ['Fashion', 'Quality']),
            category: product.category || 'Uncategorized',
            specifications: {
              Material: product.material || 'Premium Cotton',
              Style: product.style || 'Fashion',
              'Fabric Type': product.fabricType || 'Denim',
              Origin: product.origin || 'Imported'
            }
          }))

          console.log('✅ [ProductsPage] 搜索产品数据处理完成:', this.products.length, '个商品')
          this.updateCategoryCounts()
        } else {
          console.warn('⚠️ [ProductsPage] 搜索结果为空')
          this.products = []
        }
      } catch (error) {
        console.error('❌ [ProductsPage] 搜索产品失败:', error)
        this.products = []
      }
    },

    // 图片错误处理
    handleImageError(event) {
      event.target.src = '/images/placeholder-300x300.svg'
    },

    // 分页处理
    handlePageChange(page) {
      this.currentPage = page
      window.scrollTo({ top: 0, behavior: 'smooth' })
    },

    // 设置事件监听器
    setupEventListeners() {
      document.addEventListener('click', this.handleClickOutside)

      // 页面滚动时重新定位三级菜单
      window.addEventListener('scroll', this.handleScroll)

      // 窗口大小改变时重新定位三级菜单
      window.addEventListener('resize', this.handleResize)
    },

    // 清理事件监听器
    beforeDestroy() {
      document.removeEventListener('click', this.handleClickOutside)
      window.removeEventListener('scroll', this.handleScroll)
      window.removeEventListener('resize', this.handleResize)
    },

    // 处理外部点击
    handleClickOutside(event) {
      const filterDropdown = event.target.closest('.filter-dropdown')
      if (!filterDropdown && this.showFilterMenu) {
        this.showFilterMenu = false
      }
    },

    // 处理页面滚动
    handleScroll() {
      if (this.showThirdLevel && this.hoveredSecondLevel) {
        this.$nextTick(() => {
          this.positionThirdLevelMenu(this.hoveredSecondLevel)
        })
      }
    },

    // 处理窗口大小改变
    handleResize() {
      if (this.showThirdLevel && this.hoveredSecondLevel) {
        this.$nextTick(() => {
          this.positionThirdLevelMenu(this.hoveredSecondLevel)
        })
      }
    },

    // ========== 三级导航方法 ==========

    // 一级分类hover处理
    handleFirstLevelHover(categoryId) {
      const firstCategory = this.firstLevelCategories.find(c => c.id === categoryId)
      const secondCategories = this.getSecondLevelCategories(categoryId)

      // 如果菜单被固定，不改变悬停状态
      if (this.isMenuPinned && this.clickedFirstLevel === categoryId) {
        return
      }

      this.hoveredFirstLevel = categoryId
      // 修复：只要二级分类数据存在就显示二级菜单，不依赖hasChildren属性
      this.showSecondLevel = secondCategories.length > 0

      this.hoveredSecondLevel = null
      this.showThirdLevel = false
      this.clearNavigationTimer()

      // 延迟检查DOM元素 - 修复DOM操作逻辑
      this.$nextTick(() => {
        // 使用更具体的选择器，避免选择错误的元素
        const dropdown = document.querySelector('.second-level-dropdown.debug-show')


        if (dropdown) {
          console.log(`[DOM] 二级菜单classList:`, dropdown.className)
          console.log(`[DOM] 二级菜单style:`, dropdown.style.cssText)
          console.log(`[DOM] 二级菜单offsetParent:`, dropdown.offsetParent)
          console.log(`[DOM] 二级菜单getBoundingClientRect:`, dropdown.getBoundingClientRect())

          // 强制设置显示样式 - 使用更高的优先级
          const computedStyle = window.getComputedStyle(dropdown)
          console.log(`[DOM] 计算样式z-index:`, computedStyle.zIndex)

          // 使用CSS.setProperty设置更高优先级的样式
          dropdown.style.setProperty('display', 'block', 'important')
          dropdown.style.setProperty('opacity', '1', 'important')
          dropdown.style.setProperty('visibility', 'visible', 'important')
          dropdown.style.setProperty('position', 'absolute', 'important')
          dropdown.style.setProperty('z-index', '999999', 'important')
          dropdown.style.setProperty('background', 'lime', 'important')
          dropdown.style.setProperty('border', '3px solid yellow', 'important')
          dropdown.style.setProperty('top', '100%', 'important')
          dropdown.style.setProperty('left', 'calc(100% + 15px)', 'important')
          dropdown.style.setProperty('width', '280px', 'important')
          dropdown.style.setProperty('min-height', '200px', 'important')
          dropdown.style.setProperty('pointer-events', 'auto', 'important')

          // 强制重新渲染
          dropdown.offsetHeight // 触发重排

          console.log(`[DOM] 强制设置后的style:`, dropdown.style.cssText)
          console.log(`[DOM] 强制设置后的getBoundingClientRect:`, dropdown.getBoundingClientRect())

          // 检查元素是否真的可见
          const rect = dropdown.getBoundingClientRect()
          const isVisible = rect.width > 0 && rect.height > 0 &&
                          computedStyle.display !== 'none' &&
                          computedStyle.visibility !== 'hidden' &&
                          computedStyle.opacity !== '0'
          console.log(`[DOM] 元素可见性检查:`, {
            width: rect.width,
            height: rect.height,
            display: computedStyle.display,
            visibility: computedStyle.visibility,
            opacity: computedStyle.opacity,
            isVisible: isVisible
          })

          // 检查是否有其他元素在相同位置
          const elementsAtPoint = document.elementsFromPoint(rect.left + 50, rect.top + 50)
          console.log(`[DOM] 该位置的元素层级:`, elementsAtPoint.map(el => ({
            tag: el.tagName,
            class: el.className,
            zIndex: window.getComputedStyle(el).zIndex,
            display: window.getComputedStyle(el).display
          })))

          // 尝试添加到body作为绝对定位参考
          if (!dropdown.parentElement || dropdown.parentElement.tagName !== 'BODY') {
            console.log(`[DOM] 元素不在body中，当前父元素:`, dropdown.parentElement)
          }

        } else {
          console.log(`[DOM] 未找到二级菜单元素`)

          // 查找所有可能的二级菜单元素
          const allDropdowns = document.querySelectorAll('[class*="second-level"]')
          console.log(`[DOM] 找到的所有相关元素:`, Array.from(allDropdowns).map(el => ({
            tag: el.tagName,
            class: el.className,
            id: el.id,
            display: window.getComputedStyle(el).display
          })))
        }

        // 检查父容器的样式是否影响子元素
        const navContainer = document.querySelector('.hierarchical-navigation')
        const categoryBox = document.querySelector('.categories-box')
        console.log(`[DOM] 导航容器:`, navContainer)
        console.log(`[DOM] 分类盒子:`, categoryBox)

        if (navContainer) {
          const navStyle = window.getComputedStyle(navContainer)
          console.log(`[DOM] 导航容器关键样式:`, {
            overflow: navStyle.overflow,
            overflowX: navStyle.overflowX,
            overflowY: navStyle.overflowY,
            position: navStyle.position,
            zIndex: navStyle.zIndex,
            clip: navStyle.clip,
            clipPath: navStyle.clipPath,
            contain: navStyle.contain
          })
        }

        if (categoryBox) {
          const boxStyle = window.getComputedStyle(categoryBox)
          console.log(`[DOM] 分类盒子关键样式:`, {
            overflow: boxStyle.overflow,
            overflowX: boxStyle.overflowX,
            overflowY: boxStyle.overflowY,
            position: boxStyle.position,
            zIndex: boxStyle.zIndex,
            clip: boxStyle.clip,
            clipPath: boxStyle.clipPath,
            contain: boxStyle.contain
          })
        }
      })
    },

    // 二级分类hover处理
    handleSecondLevelHover(categoryId) {
      console.log(`[导航] 二级分类hover: ${categoryId}, 一级分类: ${this.hoveredFirstLevel}`)
      this.hoveredSecondLevel = categoryId
      const secondCategory = this.getSecondLevelCategory(this.hoveredFirstLevel, categoryId)
      console.log(`[导航] 二级分类数据:`, secondCategory)
      if (secondCategory && secondCategory.hasChildren) {
        this.showThirdLevel = true
        this.$nextTick(() => {
          this.positionThirdLevelMenu(categoryId)
        })
        console.log(`[导航] 显示三级菜单`)
      } else {
        this.showThirdLevel = false
        console.log(`[导航] 隐藏三级菜单`)
      }
      this.clearNavigationTimer()
    },

    // 获取一级分类名称
    getFirstLevelCategoryName(categoryId) {
      const category = this.firstLevelCategories.find(c => c.id === categoryId)
      return category ? category.name : ''
    },

    // 获取二级分类名称
    getSecondLevelCategoryName(categoryId) {
      for (const firstLevel in this.secondLevelCategories) {
        const category = this.secondLevelCategories[firstLevel].find(c => c.id === categoryId)
        if (category) return category.name
      }
      return ''
    },

    // 获取二级分类列表
    getSecondLevelCategories(firstLevelId) {
      return this.secondLevelCategories[firstLevelId] || []
    },

    // 获取三级分类列表
    getThirdLevelCategories(secondLevelId) {
      return this.thirdLevelCategories[secondLevelId] || []
    },

    // 获取特定二级分类
    getSecondLevelCategory(firstLevelId, secondLevelId) {
      const categories = this.secondLevelCategories[firstLevelId] || []
      return categories.find(c => c.id === secondLevelId)
    },

    // 移动端二级分类触摸开始
    handleSecondLevelTouchStart(categoryId) {
      this.touchStartTime = Date.now()
    },

    // 移动端二级分类触摸结束
    handleSecondLevelTouchEnd(categoryId) {
      const touchDuration = Date.now() - this.touchStartTime

      // 如果是短触摸（点击而不是滑动），触发三级菜单
      if (touchDuration < 300) {
        const secondCategory = this.getSecondLevelCategory(this.hoveredFirstLevel, categoryId)
        if (secondCategory && secondCategory.hasChildren) {
          this.hoveredSecondLevel = categoryId
          this.showThirdLevel = true
          this.$nextTick(() => {
            this.positionThirdLevelMenu(categoryId)
          })
        }
      }
    },

    // 处理一级分类点击（支持移动端交互）
    handleFirstLevelClick(category) {
      // 检测是否为移动设备
      const isMobile = window.innerWidth <= 768

      if (category.hasChildren) {
        // 桌面端和移动端都支持点击展开功能
        if (this.clickedFirstLevel === category.id && this.isMenuPinned) {
          // 如果已经固定显示，则取消固定
          this.clickedFirstLevel = null
          this.isMenuPinned = false
          this.showSecondLevel = false
          this.hoveredFirstLevel = null
          // 取消固定菜单
        } else {
          // 固定显示菜单
          this.clickedFirstLevel = category.id
          this.isMenuPinned = true
          this.hoveredFirstLevel = category.id
          this.showSecondLevel = true
          // 固定显示菜单
        }

        if (isMobile) {
          // 移动端额外逻辑：如果已经显示，则隐藏（切换逻辑）
          if (this.hoveredFirstLevel === category.id && !this.isMenuPinned) {
            this.hoveredFirstLevel = null
            this.showSecondLevel = false
          }
        }
      } else if (!category.hasChildren) {
        // 没有子分类：直接选择该分类
        this.selectFirstLevelCategory(category)
      }
    },

    // 处理二级分类点击（支持移动端和PC端交互）
    handleSecondLevelClick(category) {
      // 检测是否为移动设备
      const isMobile = window.innerWidth <= 768

      if (category.hasChildren) {
        if (isMobile) {
          // 移动端：切换三级菜单显示状态（保持原有逻辑不变）
          if (this.hoveredSecondLevel === category.id) {
            this.hoveredSecondLevel = null
            this.showThirdLevel = false
          } else {
            this.hoveredSecondLevel = category.id
            this.showThirdLevel = true
            this.$nextTick(() => {
              this.positionThirdLevelMenu(category.id)
            })
          }
        } else {
          // PC端：点击也能切换三级菜单（作为hover的补充）
          if (this.hoveredSecondLevel === category.id && this.showThirdLevel) {
            this.hoveredSecondLevel = null
            this.showThirdLevel = false
          } else {
            this.hoveredSecondLevel = category.id
            this.showThirdLevel = true
            this.$nextTick(() => {
              this.positionThirdLevelMenu(category.id)
            })
          }
        }
      } else {
        // 没有子分类：直接选择该分类（PC和移动端逻辑一致）
        this.selectSecondLevelCategory(category)
      }
    },

    // 选择一级分类
    selectFirstLevelCategory(category) {
      this.selectedFirstLevel = category.id
      this.selectedSecondLevel = null
      this.selectedThirdLevel = null

      // 根据一级分类筛选产品 - 只传递分类名称
      this.filterProductsByCategory('first', category.name)
    },

    // 选择二级分类
    selectSecondLevelCategory(category) {
      // 记录对应的一级分类，保证顶部标题可以显示完整路径
      if (this.hoveredFirstLevel) {
        this.selectedFirstLevel = this.hoveredFirstLevel
      }
      this.selectedSecondLevel = category.id
      this.selectedThirdLevel = null

      // 根据二级分类筛选产品 - 只传递分类名称
      this.filterProductsByCategory('second', category.name)
    },

    // 选择三级分类
    selectThirdLevelCategory(category) {
      console.log(`🎯 [导航] 三级分类点击: ${category.name}, ID: ${category.id}`)
      // 确保父级选中状态被记录
      if (this.hoveredFirstLevel) {
        this.selectedFirstLevel = this.hoveredFirstLevel
      }
      if (this.hoveredSecondLevel) {
        this.selectedSecondLevel = this.hoveredSecondLevel
      }
      this.selectedThirdLevel = category.id

      // 根据三级分类筛选产品 - 只传递分类名称
      this.filterProductsByCategory('third', category.name)
    },

    // 根据分类筛选产品
    async filterProductsByCategory(level, categoryName) {
      console.log(`🎯 [ProductsPage] 筛选分类: ${level}, 名称: ${categoryName}`)
      
      // 直接使用分类名称,不构建完整路径
      // API只接受最后一级的分类名称
      this.selectedCategory = categoryName
      this.currentPage = 1 // 重置到第一页

      // 关闭下拉菜单
      this.showSecondLevel = false
      this.showThirdLevel = false

      // 如果有分类名称，调用API查询该分类的产品
      if (categoryName) {
        await this.loadProductsByCategory(categoryName)
      } else {
        // 如果没有分类名称，加载所有产品
        await this.loadProducts()
      }
    },

    // 根据分类加载产品
    async loadProductsByCategory(category) {
      try {
        console.log(`🔄 [ProductsPage] 开始加载分类 "${category}" 的产品...`)
        const response = await getProductsByCategory(category, { page: 1, size: 100 })
        console.log('✅ [ProductsPage] 分类产品API完整响应:', response)
        console.log('📦 [ProductsPage] response.data:', response.data)
        console.log('📦 [ProductsPage] response.data.data:', response.data?.data)

        // 尝试多种可能的数据格式
        let apiProducts = []
        
        if (response && response.data) {
          // 格式1: { data: { data: { content: [...] } } }
          if (response.data.data && response.data.data.content && Array.isArray(response.data.data.content)) {
            apiProducts = response.data.data.content
          }
          // 格式2: { data: { content: [...] } }
          else if (response.data.content && Array.isArray(response.data.content)) {
            apiProducts = response.data.content
          }
          // 格式3: { data: { records: [...] } }
          else if (response.data.records && Array.isArray(response.data.records)) {
            apiProducts = response.data.records
          }
          // 格式4: { data: [...] }
          else if (Array.isArray(response.data)) {
            apiProducts = response.data
          }
        }

        if (apiProducts.length > 0) {
          console.log(`✅ [ProductsPage] 获取到分类 "${category}" 的产品数据:`, apiProducts.length, '个商品')

          // 筛选上架产品 (status: 1)
          const activeProducts = apiProducts.filter(product => product.status === 1)
          console.log(`✅ [ProductsPage] 分类 "${category}" 筛选后上架产品:`, activeProducts.length, '个商品')

          this.products = activeProducts.map(product => ({
            id: product.id || 'product_' + Math.random(),
            name: product.name || product.productName || 'Product Name',
            description: product.description || 'Product Description',
            price: product.price || 0,
            moq: product.stockQuantity || 100,
            image: product.mainImage || '/images/placeholder-400x400.svg',
            isNew: !product.createdAt || (new Date() - new Date(product.createdAt)) < 7 * 24 * 60 * 60 * 1000,
            rating: 4.0 + Math.random(),
            isFeatured: product.isFeatured || false,
            viewCount: 0,
            tags: product.tags && Array.isArray(product.tags) ? product.tags : (product.tags && typeof product.tags === 'string' ? product.tags.split(',').map(tag => tag.trim()) : ['Fashion', 'Quality']),
            category: product.category || category,
            specifications: {
              Material: product.material || 'Premium Cotton',
              Style: product.style || 'Fashion',
              'Fabric Type': product.fabricType || 'Denim',
              Origin: product.origin || 'Imported'
            }
          }))

          console.log('✅ [ProductsPage] 分类产品数据处理完成:', this.products.length, '个商品')

          // 重新设置热门商品
          this.hotProducts = this.products
            .filter(p => p.isFeatured)
            .slice(0, 8)
            .map(product => ({
              id: product.id,
              name: product.name,
              price: product.price,
              image: product.image,
              category: product.category
            }))

          // 更新分类计数
          this.updateCategoryCounts()
        } else {
          console.warn(`⚠️ [ProductsPage] 分类 "${category}" 没有找到产品`)
          console.warn('⚠️ [ProductsPage] 无法识别的响应格式:', response)
          this.products = []
          this.hotProducts = []
        }
      } catch (error) {
        console.error(`❌ [ProductsPage] 加载分类 "${category}" 产品失败:`, error)
        console.error('❌ [ProductsPage] 错误详情:', error.response || error.message)
        this.products = []
        this.hotProducts = []
      }
    },

    // 导航进入处理
    handleNavigationEnter() {
      this.clearNavigationTimer()
    },

    // 导航离开处理
    handleNavigationLeave() {
      // 如果菜单被固定，不隐藏
      if (this.isMenuPinned) {
        // 菜单已固定，不隐藏
        return
      }

      this.clearNavigationTimer()
      this.navigationTimer = setTimeout(() => {
        this.showSecondLevel = false
        this.showThirdLevel = false
        this.hoveredFirstLevel = null
        this.hoveredSecondLevel = null
      }, 300) // 增加延迟时间到300ms，给用户足够时间移动到下拉菜单
    },

    // 二级菜单进入处理
    handleSecondLevelEnter() {
      console.log(`[导航] 二级菜单进入`)
      this.clearNavigationTimer()
    },

    // 二级菜单离开处理
    handleSecondLevelLeave() {
      console.log(`[导航] 二级菜单离开`)
      this.clearNavigationTimer()
      this.navigationTimer = setTimeout(() => {
        // 只有一级分类没有hover时才隐藏二级菜单
        if (!this.hoveredFirstLevel) {
          this.showSecondLevel = false
          this.showThirdLevel = false
          this.hoveredSecondLevel = null
        }
      }, 300) // 增加延迟时间到300ms
    },

    // 三级菜单进入处理
    handleThirdLevelEnter() {
      console.log(`[导航] 三级菜单进入`)
      this.clearNavigationTimer()
    },

    // 三级菜单离开处理
    handleThirdLevelLeave() {
      console.log(`[导航] 三级菜单离开`)
      this.clearNavigationTimer()
      this.navigationTimer = setTimeout(() => {
        // 只有二级分类没有hover时才隐藏三级菜单
        if (!this.hoveredSecondLevel) {
          this.showThirdLevel = false
        }
      }, 200) // 三级菜单离开时较快隐藏
    },

    // 清除导航计时器
    clearNavigationTimer() {
      if (this.navigationTimer) {
        clearTimeout(this.navigationTimer)
        this.navigationTimer = null
      }
    },

    // 设置三级菜单位置
    positionThirdLevelMenu(secondCategoryId) {
      const secondLevelElement = document.querySelector(`.second-level-item[data-category-id="${secondCategoryId}"]`)
      const thirdLevelElement = document.querySelector('.third-level-dropdown.standalone')

      if (secondLevelElement && thirdLevelElement) {
        // 检测是否为移动设备
        const isMobile = window.innerWidth <= 768

        if (isMobile) {
          // 移动端：不设置位置，让CSS的bottom: 0样式生效
          thirdLevelElement.style.left = ''
          thirdLevelElement.style.top = ''
          thirdLevelElement.style.right = ''
          thirdLevelElement.style.bottom = ''
        } else {
          // 桌面端：设置三级菜单位置在二级菜单右侧
          const secondRect = secondLevelElement.getBoundingClientRect()
          const horizontalGap = 10
          const leftPosition = secondRect.right + horizontalGap
          const topPosition = secondRect.top

          thirdLevelElement.style.left = `${leftPosition}px`
          thirdLevelElement.style.top = `${topPosition}px`
          thirdLevelElement.style.right = 'auto'
          thirdLevelElement.style.bottom = 'auto'
        }
      }
    },

    // ========== 热门商品分页方法 ==========

    // 上一页
    prevHotProductsPage() {
      if (this.hotProductsCurrentPage > 1) {
        this.hotProductsCurrentPage--
      }
    },

    // 下一页
    nextHotProductsPage() {
      if (this.hotProductsCurrentPage < this.hotProductsTotalPages) {
        this.hotProductsCurrentPage++
      }
    },

    // 跳转到指定页
    goToHotProductsPage(page) {
      this.hotProductsCurrentPage = page
    }
  }
}
</script>

<style scoped>
/* B2B电商专业样式系统 */
.b2b-marketplace-page {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background: transparent;
}
.b2b-marketplace-container {
  flex: 1 0 auto;
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 clamp(20px, 4vw, 80px);
}

.b2b-marketplace-page :deep(.footer) {
  flex-shrink: 0;
  margin-top: auto !important;
}

/* 顶部控制栏 */
.marketplace-header {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-bottom: 32px;
  padding: 20px 24px;
  background: white;
  border-radius: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.header-center .page-title {
  font-size: 28px;
  font-weight: 700;
  color: #1a202c;
  margin: 0;
  text-align: center;
}

/* 筛选下拉菜单 */
.filter-dropdown {
  position: relative;
}

.filter-button {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 16px;
  background: white;
  border: 2px solid #e2e8f0;
  border-radius: 12px;
  font-size: 14px;
  font-weight: 500;
  color: #4a5568;
  cursor: pointer;
  transition: all 0.3s ease;
}

.filter-button:hover {
  border-color: #3182ce;
  color: #3182ce;
}

.filter-dropdown.active .filter-button {
  border-color: #3182ce;
  color: #3182ce;
}

.filter-menu {
  position: absolute;
  top: calc(100% + 8px);
  left: 0;
  right: 0;
  background: white;
  border: 2px solid #e2e8f0;
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  z-index: 100;
  overflow: hidden;
}

.filter-option {
  display: block;
  width: 100%;
  padding: 12px 16px;
  background: none;
  border: none;
  text-align: left;
  font-size: 14px;
  color: #4a5568;
  cursor: pointer;
  transition: all 0.2s ease;
}

.filter-option:hover {
  background: #f7fafc;
  color: #3182ce;
}

.filter-option.active {
  background: #3182ce;
  color: white;
}

/* 视图切换 */
.view-toggle {
  display: flex;
  gap: 4px;
  background: #f1f5f9;
  border-radius: 8px;
  padding: 2px;
}

.view-btn {
  padding: 8px 12px;
  background: none;
  border: none;
  border-radius: 6px;
  color: #64748b;
  cursor: pointer;
  transition: all 0.2s ease;
}

.view-btn:hover {
  color: #3182ce;
}

.view-btn.active {
  background: white;
  color: #3182ce;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

/* 主体内容区域 */
.marketplace-content {
  display: grid;
  grid-template-columns: minmax(260px, 320px) minmax(0, 1fr);
  gap: clamp(20px, 3vw, 40px);
  align-items: flex-start;
  /* 确保右侧内容不会遮挡左侧的二级菜单 */
  position: relative;
  z-index: 1;
}

/* 左侧分类导航 */
.category-navigation {
  display: flex;
  flex-direction: column;
  gap: clamp(16px, 2.5vh, 28px);
  position: sticky;
  top: 100px;
  z-index: 1; /* 进一步降低层级避免遮挡 */
  align-self: flex-start;
  max-height: calc(100vh - 120px);
  overflow-y: auto;
  overflow: visible !important; /* 强制允许所有方向的溢出显示 */
  /* 确保不裁切任何子元素 */
  clip: auto !important;
  clip-path: none !important;
  contain: none !important;
}

/* 分类盒子 */
.categories-box {
  background: white;
  border-radius: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  overflow: visible !important; /* 强制允许下拉菜单显示在外面 */
  position: relative;
}

.nav-title {
  padding: clamp(16px, 2vh, 20px) clamp(18px, 2vw, 24px) clamp(12px, 1.5vh, 16px);
  font-size: clamp(14px, 1.4vw, 16px);
  font-weight: 600;
  color: #1a202c;
  margin: 0;
}

/* 热门商品盒子 */
.hot-products-box {
  background: white;
  border-radius: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  overflow: hidden;
  padding-bottom: clamp(12px, 2vh, 20px);
}

/* 三级导航系统容器 */
.hierarchical-navigation {
  position: relative;
  overflow: visible !important; /* 强制允许所有子元素溢出显示 */
  z-index: 1; /* 设置较低的z-index，让子元素能够正常显示 */
}

/* 一级分类栏 */
.first-level-nav {
  display: flex;
  flex-direction: column;
}

.first-level-item {
  display: flex;
  align-items: center;
  padding: 12px 24px;
  background: none;
  border: none;
  text-align: left;
  cursor: pointer;
  transition: all 0.2s ease;
  position: relative;
}

/* 有子分类的一级分类不可点击,只能hover */
.first-level-item.has-children {
  cursor: default;
}

.first-level-item:hover {
  background: #f8fafc;
}

.first-level-item.hovered {
  background: #e2e8f0;
  color: #3182ce;
}

.first-level-item.pinned {
  background: #3182ce;
  color: white;
  font-weight: bold;
  box-shadow: 0 2px 8px rgba(49, 130, 206, 0.3);
}

.first-level-item.pinned .arrow-icon {
  transform: rotate(90deg);
}

.category-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
  gap: 8px;
}

.category-icon {
  font-size: 16px;
  width: 20px;
  text-align: center;
}

.category-name {
  font-size: 14px;
  font-weight: 500;
  flex: 1;
}

.arrow-icon {
  color: currentColor;
  opacity: 0.7;
  transition: transform 0.2s ease;
}

.first-level-item:hover .arrow-icon {
  transform: translateX(2px);
  opacity: 1;
}

/* 二级分类下拉层 */
.second-level-dropdown {
  position: absolute;
  top: -10px;
  left: calc(100% + 15px);
  min-width: 220px;
  background: white;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  z-index: 9999 !important; /* 使用最高层级确保显示在所有元素之上 */
  opacity: 0;
  visibility: hidden;
  transform: translateX(-10px);
  transition: all 0.2s ease;
  max-height: 400px;
  overflow-y: auto;
  /* 确保不被父容器裁切 */
  clip: auto !important;
  clip-path: none !important;
  contain: none !important;
  pointer-events: none; /* 隐藏时不可点击 */
}

.second-level-dropdown.active {
  opacity: 1;
  visibility: visible;
  transform: translateX(10px);
  pointer-events: auto; /* 显示时可以点击 */
}

/* 更强的覆盖规则 - 修复Vue scoped样式冲突 */
div[data-v-1afd9795].second-level-dropdown.debug-show,
.second-level-dropdown[data-v-1afd9795].debug-show {
  display: block !important;
  opacity: 1 !important;
  visibility: visible !important;
  position: fixed !important; /* 使用fixed定位完全脱离父容器 */
  z-index: 999999 !important; /* 提高层级 */
  background: lime !important;
  border: 3px solid yellow !important;
  top: 200px !important;
  left: 400px !important;
  width: 300px !important;
  min-height: 200px !important;
  /* 确保完全不被裁切 */
  clip: auto !important;
  clip-path: none !important;
  contain: none !important;
  transform: none !important;
  pointer-events: auto !important; /* 确保可交互 */
  box-sizing: border-box !important;
  padding: 20px !important;
  color: black !important;
  font-size: 14px !important;
}

.dropdown-header {
  padding: 16px 16px 12px;
  border-bottom: 1px solid #f1f5f9;
  background: #f8fafc;
  border-radius: 12px 12px 0 0;
  position: sticky;
  top: 0;
  z-index: 1;
}

.dropdown-header h4 {
  margin: 0;
  font-size: 14px;
  font-weight: 600;
  color: #1a202c;
}

.second-level-list {
  padding: 8px 0;
}

/* 二级分类项目容器 - 为三级分类提供定位参考 */
.second-level-item {
  display: block;
  width: 100%;
  padding: 10px 16px;
  background: none;
  border: none;
  text-align: left;
  cursor: pointer;
  transition: all 0.2s ease;
  position: relative; /* 添加相对定位作为三级分类的参考 */
}

.second-level-item:hover {
  background: #f8fafc;
  color: #3182ce;
}

.second-level-item.hovered {
  background: #e2e8f0;
  color: #3182ce;
}

.subcategory-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
  gap: 8px;
}

.subcategory-name {
  font-size: 14px;
  font-weight: 500;
  flex: 1;
}

/* 三级分类下拉层 - 相对于二级分类项定位 */
.third-level-dropdown {
  position: absolute;
  top: 0;
  left: 100%;
  min-width: 200px;
  background: white;
  border: 1px solid #e2e8f0;
  border-radius: 12px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  z-index: 102; /* 确保在二级菜单之上 */
  opacity: 0;
  visibility: hidden;
  transform: translateX(-10px);
  transition: all 0.2s ease;
  max-height: 300px;
  overflow-y: auto;
}

/* 独立的三级分类下拉层 - 完全脱离父容器定位 */
.third-level-dropdown.standalone {
  position: fixed; /* 使用 fixed 定位完全脱离父容器 */
  z-index: 9999; /* 使用最高层级 */
  opacity: 0;
  visibility: hidden;
  transform: translateX(-10px); /* 与二级菜单保持一致的初始偏移 */
  transition: all 0.2s ease;
  pointer-events: none; /* 隐藏时不可点击 */
}

.third-level-dropdown.standalone.active {
  opacity: 1;
  visibility: visible;
  transform: translateX(0);
  pointer-events: auto;
}

@media (min-width: 769px) {
  .third-level-dropdown.standalone {
    bottom: auto;
    right: auto;
    border-radius: 12px;
    max-height: 300px;
    width: auto;
    min-width: 200px;
  }
}

.third-level-list {
  padding: 8px 0;
}

.third-level-item {
  display: block;
  width: 100%;
  padding: 10px 16px;
  background: none;
  border: none;
  text-align: left;
  cursor: pointer;
  transition: all 0.2s ease;
}

.third-level-item:hover {
  background: #f8fafc;
  color: #3182ce;
}

.final-category-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
  gap: 8px;
}

.final-category-name {
  font-size: 13px;
  font-weight: 500;
  flex: 1;
  color: #374151;
}

.third-level-item:hover .final-category-name {
  color: #3182ce;
}

/* 热门商品 */
.section-title {
  font-size: clamp(14px, 1.4vw, 16px);
  font-weight: 600;
  color: #1a202c;
  margin: 0;
  padding: clamp(16px, 2vh, 20px) clamp(18px, 2vw, 24px) clamp(12px, 1.5vh, 16px);
}

.hot-products-list {
  display: flex;
  flex-direction: column;
  gap: clamp(6px, 1vh, 10px);
  padding: 0 clamp(16px, 2vw, 24px);
}

.hot-product-item {
  display: flex;
  align-items: center;
  gap: clamp(8px, 1.2vw, 14px);
  padding: clamp(6px, 0.8vw, 10px);
  background: #f8fafc;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.hot-product-item:hover {
  background: #e2e8f0;
}

.hot-product-image {
  width: clamp(36px, 3vw, 44px);
  height: clamp(36px, 3vw, 44px);
  border-radius: 6px;
  object-fit: cover;
}

.hot-product-info {
  flex: 1;
  min-width: 0;
}

.hot-product-name {
  font-size: clamp(11px, 1.1vw, 13px);
  font-weight: 500;
  color: #1a202c;
  margin: 0 0 2px 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* 热门商品分页控件 */
.hot-products-pagination {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: clamp(6px, 1vw, 10px);
  padding: clamp(8px, 1.5vh, 12px) clamp(16px, 2vw, 24px) clamp(12px, 2vh, 20px);
  margin-top: 8px;
}

.pagination-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: clamp(24px, 2.4vw, 30px);
  height: clamp(24px, 2.4vw, 30px);
  background: #f1f5f9;
  border: none;
  border-radius: 6px;
  color: #64748b;
  cursor: pointer;
  transition: all 0.2s ease;
}

.pagination-btn:hover:not(:disabled) {
  background: #3182ce;
  color: white;
}

.pagination-btn:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

.pagination-dots {
  display: flex;
  gap: 6px;
}

.pagination-dot {
  width: 6px;
  height: 6px;
  background: #d1d5db;
  border-radius: 50%;
  cursor: pointer;
  transition: all 0.2s ease;
}

.pagination-dot:hover {
  background: #9ca3af;
}

.pagination-dot.active {
  width: 16px;
  border-radius: 3px;
  background: #3182ce;
}

/* ===== B2B 电商商品网格展示样式 ===== */

/* 右侧商品展示区域 */
.products-main {
  min-height: 600px;
  display: flex;
  flex-direction: column;
}

/* B2B 商品网格布局 */
.b2b-products-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 20px;
  margin-bottom: 40px;
}

/* B2B 商品卡片 - 极简商务风格 */
.b2b-product-card {
  background: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  cursor: pointer;
  border: 1px solid #f0f0f0;
}

.b2b-product-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  border-color: #e0e0e0;
}

/* B2B 商品图片容器 */
.b2b-product-image-container {
  position: relative;
  width: 100%;
  aspect-ratio: 1;
  overflow: hidden;
  background: #fafafa;
}

.b2b-product-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.b2b-product-card:hover .b2b-product-image {
  transform: scale(1.05);
}

/* B2B 商品信息区域 */
.b2b-product-info {
  padding: 16px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

/* B2B 商品标题 */
.b2b-product-title {
  font-size: 14px;
  font-weight: 600;
  color: #333;
  margin: 0 0 4px;
  line-height: 1.4;
  display: -webkit-box;
  line-clamp: 2;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  min-height: 2.8em;
}

.b2b-product-subtitle {
  font-size: 12px;
  color: #666;
  margin: 0;
  line-height: 1.4;
  display: -webkit-box;
  line-clamp: 2;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  min-height: 1.4em;
}

/* B2B 商品规格信息 */
.b2b-product-specs {
  display: flex;
  flex-direction: column;
  gap: 6px;
  padding: 8px 0;
  border-top: 1px solid #f5f5f5;
  border-bottom: 1px solid #f5f5f5;
}

.spec-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12px;
}

.spec-label {
  color: #888;
  font-weight: 500;
}

.spec-value {
  color: #333;
  font-weight: 600;
}

/* B2B Chat Now 按钮 */
.b2b-chat-btn {
  width: 100%;
  padding: 12px 16px;
  background: #25d366;
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.b2b-chat-btn:hover {
  background: #128c7e;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(37, 211, 102, 0.3);
}

.b2b-chat-btn:active {
  transform: translateY(0);
}

/* B2B 空状态 */
.b2b-empty-state {
  text-align: center;
  padding: 80px 20px;
  color: #666;
}

.b2b-empty-state .empty-icon {
  font-size: 48px;
  margin-bottom: 16px;
  opacity: 0.5;
}

.b2b-empty-state h3 {
  font-size: 18px;
  margin: 0 0 8px 0;
  color: #fff;
}

.b2b-empty-state p {
  font-size: 14px;
  margin: 0;
  color: #fff;
}

/* B2B 加载状态 */
.b2b-loading-state {
  text-align: center;
  padding: 80px 20px;
  color: #fff;
}

.b2b-loading-state .loading-spinner {
  width: 48px;
  height: 48px;
  border: 4px solid rgba(255, 255, 255, 0.2);
  border-top-color: #3182ce;
  border-radius: 50%;
  margin: 0 auto 16px;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.b2b-loading-state p {
  font-size: 16px;
  margin: 0;
  color: rgba(255, 255, 255, 0.8);
}

/* B2B 分页区域 */
.b2b-pagination-section {
  margin-top: auto;
  padding-top: 20px;
}

/* ===== B2B 响应式设计 ===== */

/* 平板设备 - 3列布局 */
@media (max-width: 1200px) {
  .b2b-products-grid {
    grid-template-columns: repeat(3, minmax(0, 1fr));
    gap: 16px;
  }
}

/* 小平板设备 - 2列布局 */
@media (max-width: 900px) {
  .b2b-products-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
    gap: 16px;
  }

  .b2b-product-title {
    font-size: 13px;
  }

  .b2b-product-subtitle {
    font-size: 11px;
  }

  .b2b-chat-btn {
    font-size: 12px;
    padding: 10px 14px;
  }
}

/* 移动设备 - 3列布局 */
@media (max-width: 768px) {
  .b2b-marketplace-container {
    padding: 10px;
  }

  .marketplace-content {
    grid-template-columns: 1fr;
    gap: 12px;
  }

  /* 移动端分类导航 - 横向一行显示 */
  .category-navigation {
    display: block;
    position: static;
  }

  .categories-box {
    padding: 10px;
    border-radius: 10px;
  }

  .nav-title {
    display: none;
  }

  .first-level-nav {
    display: flex;
    flex-direction: row;
    flex-wrap: wrap;
    gap: 8px;
    justify-content: center;
  }

  .first-level-item {
    padding: 6px 12px;
    background: #f5f5f5;
    border-radius: 16px;
    flex: 0 0 auto;
  }

  .first-level-item:hover,
  .first-level-item.hovered {
    background: #e2e8f0;
  }

  .category-name {
    font-size: 12px;
  }

  .arrow-icon {
    display: inline-block;
    width: 12px;
    height: 12px;
  }

  /* 移动端二级下拉菜单 - 显示在分类下方 */
  .second-level-dropdown {
    position: fixed;
    top: auto;
    bottom: 0;
    left: 0;
    right: 0;
    max-height: 50vh;
    border-radius: 16px 16px 0 0;
    transform: translateY(100%);
    z-index: 100;
  }

  .second-level-dropdown.active {
    transform: translateY(0);
  }

  /* 移动端三级下拉菜单 */
  .third-level-dropdown:not(.standalone) {
    position: fixed;
    top: auto !important;
    bottom: 0;
    left: 0 !important;
    right: 0;
    max-height: 50vh;
    border-radius: 16px 16px 0 0;
    transform: translateY(100%);
    z-index: 101;
  }

  .third-level-dropdown:not(.standalone).active {
    opacity: 1;
    visibility: visible;
    transform: translateY(0);
    pointer-events: auto;
  }

  /* 移动端standalone三级菜单 */
  .third-level-dropdown.standalone {
    position: fixed;
    top: auto !important;
    bottom: 0;
    left: 0 !important;
    right: 0;
    max-height: 50vh;
    min-width: auto;
    width: 100%;
    border-radius: 16px 16px 0 0;
    transform: translateY(100%);
    z-index: 102;
  }

  .third-level-dropdown.standalone.active {
    opacity: 1;
    visibility: visible;
    transform: translateY(0) !important;
    pointer-events: auto;
    z-index: 102;
  }

  /* 隐藏热门商品盒子 */
  .hot-products-box {
    display: none;
  }

  .products-main {
    order: 1;
    min-height: auto;
  }

  .marketplace-header {
    padding: 10px 12px;
    margin-bottom: 12px;
    border-radius: 10px;
  }

  .header-center .page-title {
    font-size: 16px;
  }

  /* 产品网格 - 3列布局 */
  .b2b-products-grid {
    grid-template-columns: repeat(3, 1fr);
    gap: 8px;
    margin-bottom: 16px;
  }

  .b2b-product-card {
    border-radius: 6px;
  }

  .b2b-product-card:hover {
    transform: none;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  }

  .b2b-product-card:hover .b2b-product-image {
    transform: none;
  }

  /* 产品图片容器 - 保持正方形 */
  .b2b-product-image-container {
    padding-top: 100%;
    position: relative;
  }

  .b2b-product-image {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    object-fit: cover;
  }

  .b2b-product-info {
    padding: 6px;
    gap: 4px;
  }

  .b2b-product-title {
    font-size: 10px;
    min-height: auto;
    -webkit-line-clamp: 2;
    line-height: 1.2;
  }

  .b2b-product-subtitle {
    display: none;
  }

  /* 移动端隐藏规格信息 */
  .b2b-product-specs {
    display: none;
  }

  .b2b-chat-btn {
    padding: 3px 6px;
    font-size: 8px;
    gap: 3px;
    border-radius: 4px;
  }

  .b2b-chat-btn svg {
    width: 8px;
    height: 8px;
  }
}

/* 小屏手机设备 - 保持3列 */
@media (max-width: 480px) {
  .b2b-marketplace-container {
    padding: 8px;
  }

  .marketplace-header {
    padding: 8px 10px;
    margin-bottom: 10px;
  }

  .header-center .page-title {
    font-size: 14px;
  }

  .first-level-item {
    padding: 5px 10px;
  }

  .category-name {
    font-size: 11px;
  }

  /* 保持3列布局 */
  .b2b-products-grid {
    grid-template-columns: repeat(3, 1fr);
    gap: 6px;
  }

  .b2b-product-info {
    padding: 5px;
    gap: 3px;
  }

  .b2b-product-title {
    font-size: 9px;
  }

  .b2b-chat-btn {
    font-size: 9px;
    padding: 3px 5px;
    gap: 2px;
  }

  .b2b-chat-btn svg {
    width: 8px;
    height: 8px;
  }

  .b2b-empty-state {
    padding: 30px 12px;
  }

  .b2b-empty-state .empty-icon {
    font-size: 32px;
  }

  .b2b-empty-state h3 {
    font-size: 14px;
  }

  .b2b-empty-state p {
    font-size: 12px;
  }
}

/* 超小屏幕 - 保持3列 */
@media (max-width: 360px) {
  .b2b-marketplace-container {
    padding: 6px;
  }

  .categories-box {
    padding: 8px;
  }

  .first-level-item {
    padding: 4px 8px;
  }

  .category-name {
    font-size: 10px;
  }

  .b2b-products-grid {
    gap: 4px;
  }

  .b2b-product-info {
    padding: 4px;
  }

  .b2b-product-title {
    font-size: 8px;
  }

  .b2b-chat-btn {
    font-size: 8px;
    padding: 4px;
  }
}
</style>