// UI文本配置文件
// 避免在组件中使用硬编码字符串

export const UI_TEXTS = {
  // 页面标题
  page: {
    productsTitle: 'Product Showcase',
    allProducts: 'All products'
  },

  // 搜索相关
  search: {
    resultsFor: 'Search results for:',
    clear: 'Clear',
    searching: 'Searching...',
    failed: 'Search failed. Please try again.'
  },

  // 加载状态
  loading: {
    products: 'Loading products...',
    categories: 'Loading categories...',
    hotProducts: 'Loading featured products...'
  },

  // 错误状态
  error: {
    products: 'Failed to load products',
    categories: 'Failed to load categories',
    hotProducts: 'Failed to load hot products',
    search: 'Search failed. Please try again.',
    retry: 'Retry'
  },

  // 空状态
  empty: {
    noProducts: 'No products found',
    noProductsIcon: '📦',
    tryDifferentFilter: 'Try adjusting your filters or search terms'
  },

  // 分类标签
  categories: {
    all: 'All products',
    software: 'Software products',
    hardware: 'Hardware products',
    services: 'Services'
  },

  // 产品相关
  product: {
    defaultName: 'Product',
    defaultCategory: 'other',
    defaultDescription: 'Product description'
  },

  // WhatsApp相关
  whatsapp: {
    defaultMessage: 'Hello! I\'m interested in this product. Could you provide more details?'
  },

  // 按钮
  buttons: {
    retry: 'Retry',
    clearSearch: 'Clear',
    filter: 'Filter'
  }
}

export default UI_TEXTS