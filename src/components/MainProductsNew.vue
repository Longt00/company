<template>
  <section class="main-products-section">
    <div class="container">
      <!-- 标题部分 -->
      <div class="section-header">
        <h2 class="main-title">HOT RECOMMENDEDS</h2>
      </div>

  

      <!-- 加载状态 -->
      <div v-if="loading" class="loading-container">
        <div class="loading-spinner"></div>
        <p>Loading products...</p>
      </div>

      <!-- 错误状态 -->
      <div v-else-if="error" class="error-container">
        <p>{{ error }}</p>
        <button @click="fetchAllProducts" class="retry-btn">Retry</button>
      </div>

      <!-- 产品展示容器 -->
      <div v-else class="products-container">

        <!-- 电商商品排行展示区域 - 2行5列 -->
        <div class="products-grid">
          <div
            v-for="(product, index) in products"
            :key="index"
            class="product-card"
            @click="goToProductDetail(product, index)"
          >
            <!-- TOP标签 -->
            <div class="top-ranking-badge">TOP</div>

            <!-- 商品图片容器 -->
            <div class="product-image-container">
              <img
                :src="product.image"
                :alt="product.name"
                class="product-image"
                @error="handleImageError"
              />
            </div>

            <!-- 商品信息 -->
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
import { getFeaturedProducts } from '../api/products'

export default {
  name: 'MainProductsNew',
  data() {
    return {
      loading: false,
      error: null,
      products: []
    }
  },
  mounted() {
    this.fetchAllProducts();
  },
  methods: {
    // 获取热门推荐产品数据
    async fetchAllProducts() {
      this.loading = true;
      this.error = null;

      try {
        console.log('🔄 [MainProductsNew] 开始加载热门推荐产品...');
        console.log('📡 [MainProductsNew] 调用API: getFeaturedProducts({ page: 1, size: 100 })');

        // 使用热门推荐公开API获取产品数据（无需认证）- 对应接口 13.8 获取热门推荐产品列表
        const response = await getFeaturedProducts({ page: 1, size: 100 });

        console.log('✅ [MainProductsNew] API响应成功!');
        console.log('📦 [MainProductsNew] 响应数据结构:', {
          hasData: !!response?.data,
          hasDataData: !!response?.data?.data,
          hasContent: !!response?.data?.data?.content,
          isArray: Array.isArray(response?.data?.data?.content)
        });
        console.log('🔍 [MainProductsNew] 完整响应:', JSON.stringify(response, null, 2));

        // 处理API响应格式
        if (response && response.data && response.data.data && response.data.data.content && Array.isArray(response.data.data.content)) {
          // 13.8 接口已经保证：只返回上架且热门推荐的产品，并按 sortOrder/创建时间排序
          const apiProducts = response.data.data.content;
          console.log('✅ [MainProductsNew] 获取到API热门推荐产品数据:', apiProducts.length, '个商品');

          // 打印第一个产品的字段，方便确认结构
          if (apiProducts.length > 0) {
            console.log('🔍 [MainProductsNew] 第一个产品的所有字段:', Object.keys(apiProducts[0]));
            console.log('🔍 [MainProductsNew] 第一个产品完整数据:', apiProducts[0]);
          }

          const hotProducts = apiProducts.slice();

          // 再按 sortOrder 升序排序（数字越小越靠前），如果没有sortOrder则按创建时间降序
          hotProducts.sort((a, b) => {
            // 优先使用 sortOrder 排序
            const sortOrderA = a.sortOrder !== undefined && a.sortOrder !== null ? a.sortOrder : 999999;
            const sortOrderB = b.sortOrder !== undefined && b.sortOrder !== null ? b.sortOrder : 999999;
            
            if (sortOrderA !== sortOrderB) {
              return sortOrderA - sortOrderB; // 升序，数字越小越靠前
            }
            
            // sortOrder 相同时，按创建时间降序
            const dateA = new Date(a.createdAt || a.updateTime || a.created_at || a.updated_at || 0);
            const dateB = new Date(b.createdAt || b.updateTime || b.created_at || b.updated_at || 0);
            return dateB - dateA;
          });

          // 取前10个热门推荐产品
          const top10Products = hotProducts.slice(0, 10);
          console.log('🏆 [MainProductsNew] 最终TOP10热门产品:', top10Products.map(p => ({
            name: p.name || p.productName,
            sortOrder: p.sortOrder,
            isFeatured: p.isFeatured
          })));

          // 转换为组件期望的格式
          this.products = top10Products.map((product, index) => ({
            id: product.id, // 保留真实的产品ID
            name: product.name || product.productName || `Hot Product ${index + 1}`,
            // 使用接口返回的主图或 productImages 数组中的第一张图片
            image: product.mainImage || (Array.isArray(product.productImages) ? product.productImages[0] : null) || getProductImage(index + 1),
            price: product.price || 0,
            category: product.category,
            description: product.description,
            isFeatured: product.isFeatured,
            sortOrder: product.sortOrder
          }));

          // 如果热门产品不足10个，显示提示信息（不使用默认数据）
          if (this.products.length < 10) {
            console.warn('⚠️ [MainProductsNew] 热门产品只有', this.products.length, '个，少于10个');
            console.warn('💡 [MainProductsNew] 请在管理后台设置更多产品为HOT推荐');
          } else if (this.products.length === 0) {
            console.error('❌ [MainProductsNew] 没有找到任何热门产品！');
            console.error('💡 [MainProductsNew] 请在管理后台将产品设置为HOT推荐（点击⭐按钮）');
          }

          console.log('✅ [MainProductsNew] 产品数据处理完成，共', this.products.length, '个产品');

        } else {
          console.error('❌ [MainProductsNew] API响应格式异常或无数据！');
          console.error('🔍 [MainProductsNew] 响应数据:', response);
          this.products = [];
          this.error = 'API响应格式异常';
        }

      } catch (error) {
        console.error('❌ [MainProductsNew] 获取热门产品数据失败!');
        console.error('🔍 [MainProductsNew] 错误详情:', error);
        console.error('🔍 [MainProductsNew] 错误消息:', error.message);
        console.error('🔍 [MainProductsNew] 错误堆栈:', error.stack);
        this.error = 'Failed to load hot products. Please try again.';
        // 不使用默认数据，保持为空
        this.products = [];
      } finally {
        this.loading = false;
      }
    },

    // 默认产品数据 - 已禁用，不再使用默认数据
    getDefaultProducts() {
      console.warn('⚠️ [MainProductsNew] getDefaultProducts被调用，但已禁用默认数据');
      return []; // 返回空数组，不使用写死的默认产品
    },

    // 处理图片加载错误
    handleImageError(event) {
      event.target.src = getProductImage(1);
    },

    // 跳转到产品详情页
    goToProductDetail(product, index) {
      // 优先使用API返回的ID，否则使用生成的ID
      let productId = product.id;

      if (!productId) {
        // 如果没有API ID，使用原有的ID生成方式
        productId = this.generateProductId(product, index);
      }

      // 使用Vue Router跳转到产品详情页，同时传递查询参数
      this.$router.push({
        name: 'ProductDetail',
        params: { id: productId },
        query: {
          index: index + 1 // 从1开始，对应TOP排名
        }
      });
    },

    // 生成产品ID（作为fallback）
    generateProductId(product, index) {
      // 使用产品名称生成简化的ID
      const namePrefix = product.name
        .toLowerCase()
        .replace(/[^a-z0-9]/g, '-')
        .replace(/-+/g, '-')
        .substring(0, 20);

      // 添加索引确保唯一性
      return `${namePrefix}-${index + 1}`;
    }
  }
}
</script>

<style>
.main-products-section {
  padding: 60px 0;
  background: #f8f9fa;
}

@media (max-width: 768px) {
  .main-products-section {
    padding: 16px 0 !important;
  }
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
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

.sub-title {
  font-weight: bold;
  color: #fff;
  margin: 0;
  letter-spacing: 1px;
}

/* 产品展示容器 */
.products-container {
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  padding: 30px;
}


/* 电商商品排行展示区域 - 2行5列网格布局 */
.main-products-section .products-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 16px;
  max-width: 1400px;
  margin: 0 auto;
  padding: 10px 0;
}

/* 产品卡片 - 电商排行展示 */
.main-products-section .product-card {
  border-radius: 6px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  cursor: pointer;
  position: relative;
  border: 1px solid #f5f5f5;
  display: flex;
  flex-direction: column;
  background: #fff;
}

.product-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  border-color: #e74c3c;
}

/* TOP排行标签 - 强化视觉优先级 */
.main-products-section .top-ranking-badge {
  position: absolute;
  top: 6px;
  left: 6px;
  background: linear-gradient(135deg, #ff4757, #c44569);
  color: white;
  font-size: 10px;
  font-weight: 800;
  padding: 2px 6px;
  border-radius: 3px;
  z-index: 10;
  letter-spacing: 0.3px;
  box-shadow: 0 2px 6px rgba(196, 69, 105, 0.4);
  text-transform: uppercase;
  font-family: 'Arial', sans-serif;
}

/* 产品图片容器 - 正方形无圆角 */
.main-products-section .product-image-container {
  position: relative;
  width: 100%;
  padding-top: 100%; /* 保持正方形比例 */
  overflow: hidden;
  background: #fafafa;
}

.main-products-section .product-image {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.main-products-section .product-card:hover .product-image {
  transform: scale(1.05);
}

/* 产品信息 - 商品标题区域 */
.main-products-section .product-info {
  padding: 8px 6px;
  text-align: center;
  background: #333;
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 40px;
}

.main-products-section .product-title {
  font-size: 12px;
  font-weight: 600;
  color: #fff;
  margin: 0;
  line-height: 1.3;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  text-align: center;
}

/* 电商排行展示区域的加载动画 */
.main-products-section .product-card {
  animation: fadeInUp 0.5s ease forwards;
  opacity: 0;
}

/* 前10个产品的动画延迟 - 2行5列 */
.main-products-section .product-card:nth-child(1) { animation-delay: 0.05s; }
.main-products-section .product-card:nth-child(2) { animation-delay: 0.1s; }
.main-products-section .product-card:nth-child(3) { animation-delay: 0.15s; }
.main-products-section .product-card:nth-child(4) { animation-delay: 0.2s; }
.main-products-section .product-card:nth-child(5) { animation-delay: 0.25s; }
.main-products-section .product-card:nth-child(6) { animation-delay: 0.3s; }
.main-products-section .product-card:nth-child(7) { animation-delay: 0.35s; }
.main-products-section .product-card:nth-child(8) { animation-delay: 0.4s; }
.main-products-section .product-card:nth-child(9) { animation-delay: 0.45s; }
.main-products-section .product-card:nth-child(10) { animation-delay: 0.5s; }

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

/* 加载状态样式 */
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  margin: 30px 0;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid #e74c3c;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 20px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.loading-container p {
  color: #666;
  font-size: 16px;
  margin: 0;
}

/* 错误状态样式 */
.error-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  margin: 30px 0;
}

.error-container p {
  color: #e74c3c;
  font-size: 16px;
  margin-bottom: 20px;
  text-align: center;
}

.retry-btn {
  background: #e74c3c;
  color: white;
  border: none;
  padding: 12px 24px;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: background 0.3s ease;
}

.retry-btn:hover {
  background: #c0392b;
}


/* 响应式设计 */
@media (max-width: 1400px) {
  .products-grid {
    gap: 18px;
  }
}

@media (max-width: 1200px) {
  .products-grid {
    grid-template-columns: repeat(4, 1fr);
    gap: 16px;
  }

  
}

@media (max-width: 992px) {
  .products-grid {
    grid-template-columns: repeat(3, 1fr);
    gap: 15px;
  }

  .main-title {
    font-size: 32px;
  }

  
}

@media (max-width: 768px) {
  .main-products-section {
    padding: 15px 0;
  }

  .container {
    padding: 0 12px;
  }

  .section-header {
    margin-bottom: 20px;
  }

  .products-container {
    padding: 20px 16px;
  }

  .products-grid {
    grid-template-columns: repeat(3, 1fr);
    gap: 12px;
  }

  .main-title {
    font-size: 24px;
  }

  .sub-title {
    font-size: 14px;
  }

  .main-products-section .product-info {
    padding: 6px 4px;
    min-height: 32px;
  }

  .main-products-section .product-title {
    font-size: 10px;
    line-height: 1.2;
  }

  .main-products-section .top-ranking-badge {
    top: 4px;
    left: 4px;
    font-size: 8px;
    padding: 2px 4px;
  }
}

@media (max-width: 480px) {
  .container {
    padding: 0 15px;
  }

  .products-container {
    padding: 20px 15px;
  }

  .products-grid {
    grid-template-columns: repeat(3, 1fr);
    gap: 12px;
  }

  .main-products-section .product-info {
    padding: 5px 3px;
    min-height: 28px;
  }

  .main-products-section .product-title {
    font-size: 9px;
  }

  .main-title {
    font-size: 20px;
  }
}
</style>