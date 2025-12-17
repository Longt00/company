<template>
  <section class="main-products">
    <div class="container">
      <!-- 标题部分 -->
      <div class="section-header">
        <h2 class="main-title">MAIN PRODUCTS</h2>
      </div>

      <!-- 产品展示容器 -->
      <div class="products-container">
        <!-- 分类导航 -->
        <div class="category-nav">
          <div
            v-for="(category, index) in categories"
            :key="index"
            class="category-tab"
            :class="{ active: activeCategory === index }"
            @click="switchCategory(index)"
          >
            {{ category.name }}
          </div>
        </div>

        <!-- 产品网格 -->
        <div class="products-grid" ref="productsGrid">
          <div
            v-for="(product, index) in currentProducts"
            :key="`${activeCategory}-${index}`"
            class="product-card"
            @mouseenter="startSwing"
            @mouseleave="stopSwing"
            @click="goToProductDetail(product, index)"
          >
            <!-- 产品图片 -->
            <div class="product-image-container">
              <img
                :src="product.image"
                :alt="product.name"
                class="product-image"
                @error="handleImageError"
              />
            </div>

            <!-- 产品信息 -->
            <div class="product-info">
              <h3 class="product-title">{{ product.name }}</h3>
            </div>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<script>
import { getProductImage } from '@/assets/productImages.js'
import { getMainProducts, getProductCategories } from '../api/products'

export default {
  name: 'MainProductsSection',
  data() {
    return {
      activeCategory: 0,
      categories: [], // 动态获取分类
      allProducts: {}, // 动态设置，根据实际分类名
      featuredProducts: [],
      swingAnimation: null
    }
  },
  computed: {
    currentProducts() {
      if (this.categories.length === 0) return [];
      const category = this.categories[this.activeCategory];
      const categoryKey = category.key || category.name.toLowerCase().replace(/\s+/g, '-');
      return this.allProducts[categoryKey] || [];
    }
  },
  mounted() {
    this.fetchCategories(); // 先获取分类
    this.fetchAllProducts();
    this.fetchFeaturedProducts();
  },
  methods: {
    // 获取分类列表
    async fetchCategories() {
      try {
        console.log('📑 [MainProductsSection] 开始加载分类列表...');
        const response = await getProductCategories();
        console.log('✅ [MainProductsSection] 分类API完整响应:', response);

        let categories = null;
        if (response && response.data) {
          // 尝试多种可能的数据格式
          if (response.data.data && Array.isArray(response.data.data)) {
            categories = response.data.data;
          } else if (Array.isArray(response.data)) {
            categories = response.data;
          }
        }

        if (categories && Array.isArray(categories) && categories.length > 0) {
          console.log('✅ [MainProductsSection] 获取到分类列表:', categories);

          // 处理分类数据，提取一级分类
          const processedCategories = this.processCategories(categories);
          this.categories = processedCategories.slice(0, 6); // 最多显示6个分类

          console.log('✅ [MainProductsSection] 最终分类列表:', this.categories);
        } else {
          console.warn('⚠️ [MainProductsSection] 未能获取分类列表，使用默认分类');
          this.categories = this.getDefaultCategories();
        }
      } catch (error) {
        console.error('❌ [MainProductsSection] 加载分类列表失败:', error);
        this.categories = this.getDefaultCategories();
      }
    },

    // 处理分类数据
    processCategories(categories) {
      const categoryMap = new Map();

      categories.forEach(categoryPath => {
        // 按 > 分割分类路径
        const parts = categoryPath.split('>').map(part => part.trim());

        if (parts.length >= 1) {
          // 取一级分类
          const categoryName = parts[0];
          const categoryKey = categoryName.toLowerCase().replace(/\s+/g, '-');

          if (!categoryMap.has(categoryKey)) {
            categoryMap.set(categoryKey, {
              name: categoryName,
              key: categoryKey
            });
          }
        }
      });

      const allCategories = Array.from(categoryMap.values());

      // 定义目标顺序
      const targetOrder = ['women', 'men', 'children', 'accessories'];

      // 按照目标顺序排序分类
      const sortedCategories = allCategories.sort((a, b) => {
        const aIndex = targetOrder.indexOf(a.key);
        const bIndex = targetOrder.indexOf(b.key);

        // 如果都在目标顺序中，按照目标顺序排列
        if (aIndex !== -1 && bIndex !== -1) {
          return aIndex - bIndex;
        }

        // 如果只有一个在目标顺序中，目标顺序中的排在前面
        if (aIndex !== -1) return -1;
        if (bIndex !== -1) return 1;

        // 如果都不在目标顺序中，按字母顺序排列
        return a.name.localeCompare(b.name);
      });

      return sortedCategories;
    },

    // 默认分类
    getDefaultCategories() {
      return [
        { name: 'Women', key: 'women' },
        { name: 'Men', key: 'men' },
        { name: 'Children', key: 'children' },
        { name: 'Accessories', key: 'accessories' }
      ];
    },

    // 获取所有分类的产品数据（主要产品）
    async fetchAllProducts() {
      try {
        console.log('🔄 [MainProductsSection] 开始加载产品数据...');

        // 等待分类加载完成
        if (this.categories.length === 0) {
          console.log('⏳ [MainProductsSection] 分类未加载完成，等待...');
          setTimeout(() => this.fetchAllProducts(), 500);
          return;
        }

        // 使用公开主要产品API获取数据（无需认证）：GET /api/public/products/main
        const response = await getMainProducts({ page: 1, size: 200 });

        console.log('✅ [MainProductsSection] API响应:', response);

        // 处理API响应格式
        if (response && response.data && response.data.data && response.data.data.content && Array.isArray(response.data.data.content)) {
          const apiProducts = response.data.data.content;
          console.log('✅ [MainProductsSection] 获取到API产品数据:', apiProducts.length, '个商品');

          // 筛选上架产品 (status: 1)
          const activeProducts = apiProducts.filter(product => product.status === 1);
          console.log('✅ [MainProductsSection] 筛选后上架产品:', activeProducts.length, '个商品');

          // 根据动态分类组织产品数据
          const categorizedProducts = {};

          this.categories.forEach(category => {
            const categoryKey = category.key;
            const categoryName = category.name.toLowerCase();

            // 使用多种匹配方式：分类名称、产品名称等
            let matchedProducts = activeProducts.filter(p => {
              const productCategory = (p.category || '').toLowerCase();
              const productName = (p.name || p.productName || '').toLowerCase();

              return productCategory.includes(categoryName) ||
                     productName.includes(categoryName) ||
                     productCategory.includes(categoryKey) ||
                     productName.includes(categoryKey);
            });

            // 按创建时间排序（最新的在前）
            matchedProducts.sort((a, b) => {
              const dateA = new Date(a.createdAt || a.updateTime || a.created_at || a.updated_at || 0);
              const dateB = new Date(b.createdAt || b.updateTime || b.created_at || b.updated_at || 0);
              return dateB - dateA;
            });

            // 每个分类最多5个产品
            categorizedProducts[categoryKey] = matchedProducts.slice(0, 5);
          });

          // 打印分类统计
          const categoryStats = {};
          this.categories.forEach(category => {
            categoryStats[category.name] = categorizedProducts[category.key]?.length || 0;
          });
          console.log('🔍 [MainProductsSection] 动态分类统计:', categoryStats);

          // 转换为组件期望的格式
          this.allProducts = {};
          this.categories.forEach((category, categoryIndex) => {
            const categoryKey = category.key;
            const products = categorizedProducts[categoryKey] || [];

            this.allProducts[categoryKey] = products.map((product, index) => ({
              id: product.id, // 保留真实的产品ID
              name: product.name || product.productName || `${category.name} Product ${index + 1}`,
              image: product.mainImage || product.images?.[0] || this.getDefaultProductImage(categoryIndex * 10 + index + 1),
              priceMin: product.priceRange?.min || '10.00',
              priceMax: product.priceRange?.max || '20.00',
              category: product.category // 添加分类信息
            }));
          });

          console.log('✅ [MainProductsSection] 产品数据处理完成:', this.allProducts);
        } else {
          console.warn('⚠️ [MainProductsSection] API响应格式异常或无数据，使用默认产品');
          this.allProducts = this.getDefaultProducts();
        }
      } catch (error) {
        console.error('❌ [MainProductsSection] 获取产品数据失败:', error);
        this.allProducts = this.getDefaultProducts();
      }
    },

    // 默认产品数据 - 根据当前分类动态生成
    getDefaultProducts() {
      const defaultProducts = {};

      if (this.categories.length === 0) {
        // 如果没有分类，使用默认分类
        const defaultCategories = this.getDefaultCategories();
        defaultCategories.forEach((category, index) => {
          defaultProducts[category.key] = this.generateDefaultProductsForCategory(category.name, index * 10 + 1);
        });
      } else {
        // 根据实际分类生成默认数据
        this.categories.forEach((category, index) => {
          defaultProducts[category.key] = this.generateDefaultProductsForCategory(category.name, index * 10 + 1);
        });
      }

      return defaultProducts;
    },

    // 为指定分类生成默认产品数据
    generateDefaultProductsForCategory(categoryName, startImageIndex) {
      return Array.from({ length: 8 }, (_, index) => ({
        name: `${categoryName} Product ${index + 1}`,
        image: getProductImage(startImageIndex + index),
        priceMin: (8 + Math.random() * 12).toFixed(2),
        priceMax: (12 + Math.random() * 18).toFixed(2)
      }));
    },

    // 切换分类
    switchCategory(index) {
      this.switchCategoryWithAnimation(index);
    },

    
    // 带动画效果的分类切换
    async switchCategoryWithAnimation(index) {
      if (this.activeCategory === index) return;

      const productsGrid = this.$refs.productsGrid;
      if (productsGrid) {
        // 添加切换动画
        productsGrid.classList.add('switching');

        // 等待淡出动画
        await new Promise(resolve => setTimeout(resolve, 300));

        // 切换分类
        this.activeCategory = index;

        // 等待DOM更新
        await this.$nextTick();

        // 移除切换动画类
        productsGrid.classList.remove('switching');

        // 重新触发动画
        const productCards = productsGrid.querySelectorAll('.product-card');
        productCards.forEach((card, i) => {
          card.style.animation = 'none';
          setTimeout(() => {
            card.style.animation = `fadeInUp 0.6s ease ${0.1 + i * 0.05}s forwards`;
          }, 10);
        });
      } else {
        this.activeCategory = index;
      }
    },

    // 处理图片加载错误
    handleImageError(event) {
      event.target.src = getProductImage(1); // 使用默认图片
    },

    // 开始摆动动画
    startSwing(event) {
      const card = event.currentTarget;
      if (this.swingAnimation) {
        clearInterval(this.swingAnimation);
      }

      let angle = 0;
      const swingRange = 3; // 摆动角度范围
      const speed = 0.1;

      this.swingAnimation = setInterval(() => {
        angle += speed;
        const swing = Math.sin(angle) * swingRange;
        card.style.transform = `rotate(${swing}deg)`;
      }, 16); // 60fps
    },

    // 停止摆动动画
    stopSwing(event) {
      const card = event.currentTarget;
      if (this.swingAnimation) {
        clearInterval(this.swingAnimation);
        this.swingAnimation = null;
      }

      // 平滑回到原始位置
      card.style.transition = 'transform 0.3s ease-out';
      card.style.transform = 'rotate(0deg)';

      setTimeout(() => {
        card.style.transition = '';
      }, 300);
    },

    // 跳转到产品详情页
    goToProductDetail(product, index) {
      console.log('查看产品:', product);

      // 跳转到产品详情页面
      const productRoute = {
        name: 'ProductDetail',
        params: {
          id: product.id
        },
        query: {
          category: product.category,
          title: product.name
        }
      };

      // 尝试使用Vue Router进行导航
      if (this.$router) {
        this.$router.push(productRoute);
      } else {
        // 如果router不可用，使用传统的页面跳转方式
        const queryString = new URLSearchParams(productRoute.query).toString();
        const url = `/product/${product.id}${queryString ? '?' + queryString : ''}`;
        window.location.href = url;
      }

      // 记录产品点击事件（可用于分析）
      this.trackProductClick(product);
    },

    // 产品点击事件追踪（可选功能）
    trackProductClick(product) {
      // 可以在这里添加Google Analytics或其他分析工具的事件追踪
      if (typeof gtag !== 'undefined') {
        gtag('event', 'product_click', {
          'event_category': 'Products',
          'event_label': product.name,
          'product_id': product.id,
          'product_category': product.category
        });
      }

      // 或者发送到后端API记录用户行为
      console.log('产品点击事件:', {
        productId: product.id,
        name: product.name,
        category: product.category,
        timestamp: new Date().toISOString()
      });
    },

    // 获取特色产品数据
    async fetchFeaturedProducts() {
      try {
        // 使用产品管理API获取特色产品数据
        const token = localStorage.getItem('token');
        if (!token) {
          console.warn('未找到token，使用默认特色产品数据');
          this.featuredProducts = this.getDefaultFeaturedProducts();
          return;
        }

        const headers = {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        };

        const queryParams = new URLSearchParams({
          page: 1,
          limit: 10,
          featured: true
        });

        const response = await fetch(`/api/products?${queryParams}`, {
          method: 'GET',
          headers
        });

        if (response.ok) {
          const result = await response.json();
          if (result.success && result.data && result.data.products) {
            // 转换为组件期望的格式
            this.featuredProducts = result.data.products.map((product, index) => ({
              id: product.id || this.generateProductId(product, index),
              name: product.name || `Featured Product ${index + 1}`,
              image: product.mainImage || product.images?.[0] || this.getDefaultProductImage(index + 1),
              priceMin: product.priceRange?.min || '12.00',
              priceMax: product.priceRange?.max || '25.00',
              badge: product.isNew ? 'New' : (product.isFeatured ? 'Featured' : null)
            }));
          } else {
            this.featuredProducts = this.getDefaultFeaturedProducts();
          }
        } else {
          this.featuredProducts = this.getDefaultFeaturedProducts();
        }
      } catch (error) {
        console.error('获取特色产品数据失败:', error);
        this.featuredProducts = this.getDefaultFeaturedProducts();
      }
    },

      // 默认特色产品数据
    getDefaultFeaturedProducts() {
      return [
        {
          name: 'Brown Ripped Jeans',
          image: '@/images/products/1.png',
          priceMin: '11.67',
          priceMax: '14.58'
        },
        {
          name: 'Grey Cargo Jeans',
          image: '@/images/products/2.png',
          priceMin: '11.00',
          priceMax: '13.75'
        },
        {
          name: 'Dark Blue Ripped Skinny Jeans',
          image: '@/images/products/3.png',
          priceMin: '8.33',
          priceMax: '10.42'
        },
        {
          name: 'Light Blue Skinny Jeans',
          image: '@/images/products/4.png',
          priceMin: '6.67',
          priceMax: '8.33'
        },
        {
          name: 'Blue Plaid Denim Jacket',
          image: '@/images/products/5.png',
          priceMin: '18.67',
          priceMax: '23.33'
        },
        {
          name: 'Dark Blue Denim Jacket',
          image: '@/images/products/6.png',
          priceMin: '20.00',
          priceMax: '25.00'
        }
      ];
    }
  }
}
</script>

<style scoped>
.main-products {
  padding: 80px 0;
  background: #f5f5f5;
  position: relative;
}

.main-products::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><defs><pattern id="wave" x="0" y="0" width="100" height="100" patternUnits="userSpaceOnUse"><path d="M0,50 Q25,25 50,50 T100,50 L100,100 L0,100 Z" fill="rgba(255,255,255,0.1)"/></pattern></defs><rect width="100" height="100" fill="url(%23wave)"/></svg>') repeat;
  opacity: 0.3;
  pointer-events: none;
}  

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  position: relative;
  z-index: 1;
}

/* 标题部分 */
.section-header {
  text-align: center;
  margin-bottom: 40px;
}

.main-title {
  font-size: 36px;
  font-weight: bold;
  color: #fff;
  margin: 0;
  letter-spacing: 1px;
}

/* 特色商品网格 */
.featured-products-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 25px;
  margin-bottom: 60px;
}

.featured-product-card {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  cursor: pointer;
}

.featured-product-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

.featured-product-image {
  width: 100%;
  height: 200px;
  overflow: hidden;
  background: #f8f9fa;
}

.featured-product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.featured-product-card:hover .featured-product-image img {
  transform: scale(1.05);
}

.featured-product-info {
  padding: 15px;
  text-align: center;
}

.featured-product-name {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0 0 8px 0;
  line-height: 1.4;
}

.featured-product-price {
  font-size: 14px;
  color: #666;
  margin: 0;
  font-weight: 500;
}

/* 分类导航 */
.category-nav {
  display: flex;
  justify-content: center;
  gap: 40px;
  margin-bottom: 40px;
  padding-bottom: 20px;
  border-bottom: 1px solid #e0e0e0;
}

.category-tab {
  font-size: 16px;
  font-weight: 500;
  color: #666;
  cursor: pointer;
  padding: 8px 0;
  position: relative;
  transition: all 0.3s ease;
}

.category-tab:hover {
  color: #333;
}

.category-tab.active {
  color: #333;
  font-weight: 600;
}

.category-tab.active::after {
  content: '';
  position: absolute;
  bottom: -8px;
  left: 0;
  right: 0;
  height: 2px;
  background: #333;
  border-radius: 1px;
}

/* 产品展示容器 */
.products-container {
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  padding: 40px;
  position: relative;
}



/* 产品网格 */
.products-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 20px;
  transition: all 0.8s ease;
  opacity: 1;
}

/* 产品卡片 */
.product-card {
  background: white;
  border-radius: 0px;
  overflow: hidden;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  cursor: pointer;
  position: relative;
  display: flex;
  flex-direction: column;
  height: 100%;
}

.product-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0);
  transition: background 0.3s ease;
  z-index: 1;
  pointer-events: none;
}

.product-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

/* 产品图片容器 */
.product-image-container {
  position: relative;
  width: 100%;
  height: 250px;
  overflow: hidden;
  background: #f8f9fa;
}

.product-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.product-card:hover .product-image {
  transform: scale(1.05);
}

/* 产品切换动画 */
.products-grid.switching {
  opacity: 0.3;
  transform: scale(0.98);
}

.product-card {
  animation: fadeInUp 0.6s ease forwards;
}

.product-card:nth-child(1) { animation-delay: 0.1s; }
.product-card:nth-child(2) { animation-delay: 0.15s; }
.product-card:nth-child(3) { animation-delay: 0.2s; }
.product-card:nth-child(4) { animation-delay: 0.25s; }
.product-card:nth-child(5) { animation-delay: 0.3s; }
.product-card:nth-child(6) { animation-delay: 0.35s; }
.product-card:nth-child(7) { animation-delay: 0.4s; }
.product-card:nth-child(8) { animation-delay: 0.45s; }
.product-card:nth-child(9) { animation-delay: 0.5s; }
.product-card:nth-child(10) { animation-delay: 0.55s; }

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 产品信息 */
.product-info {
  padding: 20px;
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.product-title {
  font-size: 16px;
  font-weight: 600;
  color: #333;
  margin: 0;
  line-height: 1.4;
  height: 44px;
  overflow: hidden;
  display: -webkit-box;
  line-clamp: 2;
  -webkit-box-orient: vertical;
}

.product-price {
  font-size: 18px;
  font-weight: 700;
  color: #e60012;
  margin: 0;
}

.view-details-btn {
  background: #e60012;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  margin-top: auto;
}

.view-details-btn:hover {
  background: #cc0010;
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(230, 0, 18, 0.3);
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .featured-products-grid {
    grid-template-columns: repeat(3, 1fr);
    gap: 20px;
  }

  .products-grid {
    grid-template-columns: repeat(4, 1fr);
    gap: 18px;
  }

  .category-nav {
    gap: 30px;
  }

  
}

@media (max-width: 992px) {
  .featured-products-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 18px;
  }

  .featured-product-image {
    height: 180px;
  }

  .products-grid {
    grid-template-columns: repeat(3, 1fr);
    gap: 16px;
  }

  .main-title {
    font-size: 32px;
  }

  .category-nav {
    gap: 25px;
  }

  .category-tab {
    font-size: 15px;
  }

  .product-image-container {
    height: 220px;
  }

  
}

@media (max-width: 768px) {
  .main-products {
    padding: 60px 0;
  }

  .featured-products-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 15px;
    margin-bottom: 40px;
  }

  .featured-product-image {
    height: 160px;
  }

  .products-container {
    padding: 20px 15px;
  }

  .products-grid {
    grid-template-columns: repeat(3, 1fr);
    gap: 10px;
  }

  .main-title {
    font-size: 28px;
  }

  .category-nav {
    gap: 15px;
    flex-wrap: wrap;
    justify-content: center;
    margin-bottom: 20px;
    padding-bottom: 15px;
  }

  .category-tab {
    font-size: 13px;
  }

  .product-card {
    border-radius: 8px;
  }

  .product-image-container {
    height: auto;
    aspect-ratio: 1/1.2;
  }

  .product-info {
    padding: 8px 6px;
  }

  .product-title {
    font-size: 10px;
    height: auto;
    min-height: 24px;
    line-height: 1.2;
  }
}

@media (max-width: 480px) {
  .container {
    padding: 0 12px;
  }

  .featured-products-grid {
    grid-template-columns: repeat(3, 1fr);
    gap: 8px;
    margin-bottom: 20px;
  }

  .featured-product-image {
    height: 100px;
  }

  .featured-product-info {
    padding: 8px;
  }

  .featured-product-name {
    font-size: 10px;
  }

  .featured-product-price {
    font-size: 9px;
  }

  .products-container {
    padding: 15px 10px;
  }

  .products-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 8px;
  }

  .main-title {
    font-size: 22px;
  }

  .product-card {
    border-radius: 6px;
  }

  .product-image-container {
    height: auto;
    aspect-ratio: 1/1.2;
  }

  .product-info {
    padding: 6px 4px;
  }

  .product-title {
    font-size: 9px;
    height: auto;
    min-height: 20px;
    line-height: 1.2;
  }

  .category-nav {
    gap: 12px;
    flex-direction: row;
    flex-wrap: wrap;
    justify-content: center;
    margin-bottom: 15px;
    padding-bottom: 12px;
  }

  .category-tab {
    font-size: 12px;
    padding: 6px 0;
  }

  .price-box {
    padding: 8px 10px;
    font-size: 11px;
  }
}
</style>