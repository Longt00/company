<template>
  <header class="header">
    <div class="container">
      <div class="header-content">
        <!-- 移动端汉堡菜单按钮 -->
        <button class="mobile-menu-btn" @click="toggleMobileMenu" aria-label="MENU">
          <svg v-if="!mobileMenuOpen" width="24" height="24" viewBox="0 0 24 24" fill="none">
            <path d="M3 12h18M3 6h18M3 18h18" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
          </svg>
          <svg v-else width="24" height="24" viewBox="0 0 24 24" fill="none">
            <path d="M18 6L6 18M6 6l12 12" stroke="currentColor" stroke-width="2" stroke-linecap="round"/>
          </svg>
          <span>MENU</span>
        </button>

        <!-- 主导航菜单 -->
        <nav class="main-nav" :class="{ 'mobile-menu-open': mobileMenuOpen }">
          <ul class="nav-list">
            <li class="nav-item">
              <router-link to="/" class="nav-link" @click="closeMobileMenu">Home</router-link>
            </li>
            <li class="nav-item">
              <router-link to="/products" class="nav-link" @click="closeMobileMenu">Products</router-link>
            </li>
            <li class="nav-item">
              <router-link to="/company-profile" class="nav-link" @click="closeMobileMenu">Company profile & Contracts</router-link>
            </li>
          </ul>
        </nav>

        <!-- 搜索框 -->
        <div class="search-section">
          <div class="search-box">
            <input
              v-model="searchKeyword"
              type="text"
              placeholder="Search by name or category"
              class="search-input"
              @keyup.enter="performSearch"
            >
            <svg width="16" height="16" viewBox="0 0 16 16" fill="none" class="search-icon" @click="performSearch">
              <path d="M7 14A7 7 0 1 0 7 0a7 7 0 0 0 0 14zM13 13l-2.35-2.35" stroke="#999" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>
          </div>
        </div>
      
      </div>
    </div>

    <!-- 移动端菜单遮罩层 -->
    <div 
      v-if="mobileMenuOpen" 
      class="mobile-menu-backdrop"
      @click="closeMobileMenu"
    ></div>
  </header>
</template>

<script>
import apiClient from '../api/config'

export default {
  name: 'Header',
  data() {
    return {
      searchKeyword: '',
      debounceTimer: null, // 防抖定时器
      mobileMenuOpen: false // 移动端菜单开关状态
    }
  },
  methods: {
    // 切换移动端菜单
    toggleMobileMenu() {
      this.mobileMenuOpen = !this.mobileMenuOpen
      // 防止背景滚动
      if (this.mobileMenuOpen) {
        document.body.style.overflow = 'hidden'
      } else {
        document.body.style.overflow = ''
      }
    },

    // 关闭移动端菜单
    closeMobileMenu() {
      this.mobileMenuOpen = false
      document.body.style.overflow = ''
    },

    navigateToProducts() {
      this.$router.push('/products')
      this.closeMobileMenu() // 导航后关闭菜单
    },

    handleSearchInput() {
      // 实时搜索防抖处理
      if (this.debounceTimer) {
        clearTimeout(this.debounceTimer)
      }

      this.debounceTimer = setTimeout(() => {
        this.performSearch()
      }, 300) // 300ms防抖延迟
    },

    async performSearch() {
      const keyword = this.searchKeyword.trim()
      if (keyword) {
        console.log(`🔍 [Header] 开始搜索产品: "${keyword}"`)

        // 直接导航到产品页面，让ProductsPage处理搜索
        this.$router.push({
          path: '/products',
          query: { search: keyword }
        })
      }
    }
  },
  beforeDestroy() {
    // 清理防抖定时器
    if (this.debounceTimer) {
      clearTimeout(this.debounceTimer)
      this.debounceTimer = null
    }
  }
}
</script>

<style scoped>
/* 通用容器 */
.container {
  max-width: var(--container-xl);
  margin: 0 auto;
  padding: 0 var(--spacing-md);
}

/* 头部样式 */
.header {
  background: #343235;
  border-bottom: 1px solid #444;
  position: sticky;
  top: 0;
  z-index: var(--z-fixed);
  font-family: var(--font-family-base);
  min-height: 45px;
  width: 100%;
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: flex-start;
  padding: 0;
  min-height: 45px;
  gap: 0;
  position: relative;
}

/* 移动端汉堡菜单按钮 */
.mobile-menu-btn {
  display: none; /* 桌面端默认隐藏 */
  background: transparent;
  border: none;
  color: #fff;
  cursor: pointer;
  padding: var(--spacing-sm);
  margin-right: var(--spacing-sm);
  min-width: var(--touch-target-min);
  min-height: var(--touch-target-min);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: var(--transition-fast);
  gap: 4px;
}

.mobile-menu-btn:hover {
  color: var(--color-primary);
  background: rgba(255, 106, 0, 0.1);
  border-radius: var(--radius-md);
}

.mobile-menu-btn:active {
  transform: scale(0.95);
}

/* 移动端菜单遮罩层 */
.mobile-menu-backdrop {
  display: none; /* 桌面端默认隐藏 */
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  z-index: var(--z-modal-backdrop);
  animation: fadeIn var(--transition-base);
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

/* Logo区域 */
.logo-section {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-shrink: 0;
}

.logo {
  width: 40px;
  height: 40px;
  background: #ff6a00;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.logo-text {
  color: #fff;
  font-size: 16px;
  font-weight: 700;
}

.company-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.company-name {
  font-size: 14px;
  font-weight: 700;
  color: #fff;
  margin: 0;
  line-height: 1.2;
}

.verified-badge {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 11px;
  color: #ff6a00;
  font-weight: 500;
}

/* 主导航 */
.main-nav {
  display: flex;
  justify-content: flex-start;
  margin-left: 20px;
}

.nav-list {
  display: flex;
  align-items: center;
  list-style: none;
  margin: 0;
  padding: 0;
  gap: 0;
}

.nav-item {
  position: relative;
}

.nav-link {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 12px 20px;
  color: #fff;
  text-decoration: none;
  font-size: 16px;
  font-weight: 400;
  transition: all 0.3s;
  border-radius: 0;
  height: 45px;
  box-sizing: border-box;
}

.nav-link:hover,
.nav-item a:hover {
  background: #444;
  color: #fff;
}

.dropdown-parent:hover .nav-link {
  background: #444;
  color: #fff;
}

/* 确保Products链接可点击 */
.dropdown-parent .products-trigger {
  position: relative;
  z-index: 10;
  pointer-events: auto;
}

/* router-link 专用样式 */
.nav-item a {
  color: #fff;
  text-decoration: none;
}

.nav-item a:hover,
.nav-item a.router-link-active {
  background: #444;
  color: #fff;
}


/* 下拉菜单基础样式 */
.dropdown-menu {
  position: absolute;
  top: 100%;
  left: 0;
  background: #595959;
  border-radius: 4px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.15);
  min-width: 400px;
  z-index: 1001;
  margin-top: 0;
}

.dropdown-container {
  padding: 0;
}

/* Products触发栏样式 */
.products-trigger {
  background: #333 !important;
  font-weight: 500 !important;
  font-size: 0.95rem !important;
}

.products-trigger:hover {
  background: #444 !important;
}

/* 向上箭头图标 */
.dropdown-arrow.up {
  transform: rotate(180deg);
  transition: transform 0.3s ease;
}

/* Products下拉菜单 */
.products-menu .dropdown-container {
  display: flex;
  flex-direction: column;
}

/* See all categories 项 */
.see-all-item {
  border-bottom: 1px solid #f0f0f0;
}

.see-all-link {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0.7rem 1rem;
  color: #666;
  text-decoration: none;
  font-size: 0.9rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s ease;
}

.see-all-link:hover {
  color: #ff6a00;
  text-decoration: underline;
  background: transparent;
}

.see-all-link .arrow-right {
  transition: all 0.2s ease;
}

.see-all-link:hover .arrow-right {
  transform: translateX(3px);
  stroke: #ff6a00;
}

/* 分类列表 */
.categories-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.category-item {
  position: relative;
}

.category-link {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0.7rem 1rem;
  color: #666;
  text-decoration: none;
  font-size: 0.9rem;
  cursor: pointer;
  transition: all 0.2s ease;
  min-height: 44px;
}

.category-link:hover {
  color: #ff6a00;
  text-decoration: underline;
  background: transparent;
}

.category-name {
  flex: 1;
  line-height: 1.4;
  word-wrap: break-word;
}

/* 子菜单箭头 */
.submenu-arrow {
  flex-shrink: 0;
  margin-left: 8px;
  transition: all 0.2s ease;
  stroke: #666;
}

.category-item.has-submenu:hover .submenu-arrow {
  transform: translateX(3px);
  stroke: #ff6a00;
}

/* 长文本分类项特殊处理 */
.category-item:has(.category-name[style*="length"]) .category-name {
  max-width: 220px;
  line-height: 1.4;
  word-wrap: break-word;
  hyphens: auto;
}

/* 三级分类选择器样式 */
.category-selector-wrapper {
  padding: 16px 20px;
  background: #f8f9fa;
  border-bottom: 1px solid #e9ecef;
}

.apply-category-btn {
  margin-top: 12px;
  width: 100%;
  padding: 8px 16px;
  background: #ff6a00;
  color: #fff;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.apply-category-btn:hover {
  background: #e55a00;
  transform: translateY(-1px);
}

/* 分隔线 */
.menu-divider {
  height: 1px;
  background: #e9ecef;
  margin: 0;
}

/* 快速分类标题 */
.quick-categories {
  padding: 12px 20px 16px;
}

.quick-category-title {
  font-size: 12px;
  font-weight: 600;
  color: #666;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  margin-bottom: 8px;
  padding: 0 4px;
}




/* 搜索框样式 */
.search-section {
  margin-left: auto;
  margin-right: 15px;
   z-index: 9; /* 确保小于浮窗的 z-index（如 9999） */
}

.search-box {
  position: relative;
  display: flex;
  align-items: center;
}

.search-input {
  width: 280px;
  padding: 10px 35px 10px 14px;
  border: 1px solid #555;
  border-radius: 6px;
  background: #fff;
  font-size: 14px;
  color: #333;
  outline: none;
  transition: all 0.3s;
}

.search-input:focus {
  border-color: #ff6a00;
  box-shadow: 0 0 0 2px rgba(255, 106, 0, 0.1);
}

.search-input::placeholder {
  color: #999;
}

.search-icon {
  position: absolute;
  right: 5px;
  cursor: pointer;
  transition: color 0.3s ease;
}

.search-icon:hover {
  stroke: #ff6a00;
}

/* 右侧按钮 */
.action-section {
  flex-shrink: 0;
}

.contact-supplier-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  background: #ff6a00;
  color: #fff;
  border: none;
  padding: 10px 16px;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
}

.contact-supplier-btn:hover {
  background: #e55a00;
  transform: translateY(-1px);
}

/* ==================== 响应式设计 - 使用统一断点 ==================== */

/* PC端 (> 768px) - 确保导航正常显示 */
@media (min-width: 769px) {
  /* 重置导航样式为PC端样式 */
  .main-nav {
    position: static !important;
    display: flex !important;
    justify-content: flex-start;
    margin-left: 20px;
    margin-right: 0;
    width: auto !important;
    height: auto !important;
    top: auto;
    right: auto;
    background: transparent !important;
    padding: 0 !important;
    box-shadow: none !important;
    z-index: auto !important;
  }

  .nav-list {
    display: flex !important;
    flex-direction: row !important;
    align-items: center;
    list-style: none;
    margin: 0;
    padding: 0;
    gap: 0;
  }

  .nav-item {
    position: relative;
    border-bottom: none !important;
  }

  .nav-link {
    display: flex;
    align-items: center;
    gap: 4px;
    padding: 12px 20px;
    color: #fff;
    text-decoration: none;
    font-size: 16px;
    font-weight: 400;
    transition: all 0.3s;
    border-radius: 0;
    height: 45px;
    box-sizing: border-box;
    min-height: auto;
  }

  /* 隐藏移动端特有元素 */
  .mobile-menu-btn {
    display: none !important;
  }

  .mobile-menu-backdrop {
    display: none !important;
  }
}

/* 大屏幕 (< 1200px) */
@media (max-width: 1200px) {
  .container {
    padding: 0 var(--spacing-md);
  }

  .header-content {
    gap: var(--spacing-md);
  }
}

/* 中等屏幕 (< 992px) - 平板 */
@media (max-width: 992px) {
  .main-nav {
    margin-left: var(--spacing-sm);
  }

  .search-input {
    width: 200px;
  }

  .nav-link {
    font-size: var(--font-size-sm);
    padding: var(--spacing-sm) var(--spacing-md);
  }
}

/* 移动端 (< 768px) - 手机 */
@media (max-width: 768px) {
  .container {
    padding: 0 var(--spacing-sm);
  }

  .header-content {
    justify-content: space-between;
    gap: 8px;
  }

  /* 显示汉堡菜单按钮 */
  .mobile-menu-btn {
    display: flex !important;
    padding: 8px;
    margin-right: 4px;
  }

  /* 显示遮罩层 */
  .mobile-menu-backdrop {
    display: block;
  }

  /* 主导航菜单 - 侧边栏样式 */
  .main-nav {
    position: fixed;
    top: 0;
    right: -100%;
    width: 260px;
    height: 100vh;
    background: #2a272b;
    padding: var(--spacing-2xl) var(--spacing-md);
    margin: 0;
    transition: right var(--transition-base);
    z-index: var(--z-modal);
    box-shadow: var(--shadow-2xl);
    overflow-y: auto;
  }

  /* 菜单打开状态 */
  .main-nav.mobile-menu-open {
    right: 0;
  }

  /* 导航列表 - 垂直排列 */
  .nav-list {
    flex-direction: column;
    align-items: stretch;
    gap: 0;
    padding: 0;
  }

  /* 导航项 */
  .nav-item {
    border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  }

  .nav-item:last-child {
    border-bottom: none;
  }

  /* 导航链接 */
  .nav-link {
    display: block;
    padding: var(--spacing-lg) var(--spacing-md);
    font-size: var(--font-size-lg);
    color: #fff;
    min-height: var(--touch-target-min);
    display: flex;
    align-items: center;
  }

  .nav-link:hover,
  .nav-link:active {
    background: rgba(255, 106, 0, 0.1);
    color: var(--color-primary);
  }

  .nav-link.router-link-active {
    background: rgba(255, 106, 0, 0.15);
    color: var(--color-primary);
    border-left: 3px solid var(--color-primary);
  }

  /* 搜索区域 - 限制宽度 */
  .search-section {
    flex: 0 1 auto;
    max-width: 60%;
    margin-right: 8px;
  }

  .search-box {
    width: 100%;
  }

  .search-input {
    width: 100%;
    min-width: 140px;
    font-size: 14px;
    padding: 8px 32px 8px 12px;
  }
}

/* 超小屏幕 (< 576px) - 小手机 */
@media (max-width: 576px) {
  .container {
    padding: 0 8px;
  }

  .header-content {
    gap: 6px;
  }

  .main-nav {
    width: 85vw;
    max-width: 280px;
  }

  .search-section {
    max-width: 55%;
  }

  .search-input {
    min-width: 120px;
    font-size: 13px;
    padding: 7px 28px 7px 10px;
  }

  .search-icon {
    right: 8px;
    width: 14px;
    height: 14px;
  }

  .nav-link {
    font-size: var(--font-size-base);
  }

  .mobile-menu-btn {
    padding: 6px;
  }
}

/* 触摸设备优化 */
@media (hover: none) and (pointer: coarse) {
  /* 移除悬停效果，只保留点击/激活效果 */
  .nav-link:hover {
    background: transparent;
    color: inherit;
  }

  .nav-link:active {
    background: rgba(255, 106, 0, 0.2);
    color: var(--color-primary);
  }

  .mobile-menu-btn:hover {
    background: transparent;
    color: #fff;
  }

  .mobile-menu-btn:active {
    background: rgba(255, 106, 0, 0.2);
    color: var(--color-primary);
  }
}
</style>