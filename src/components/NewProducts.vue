<template>
  <section class="new-products">
    <div class="container">
      <div class="section-header">
        <h2 class="section-title">NEW PRODUCTS</h2>
      </div>

      <div class="products-grid">
        <div
          v-for="(product, index) in products"
          :key="product.id || `product-${index}`"
          class="product-card"
          @click="viewProduct(product)"
        >
          <div class="product-image-container">
            <img
              :src="product.image"
              :alt="product.title"
              class="product-image"
            />
          </div>
          <div class="product-info">
            <h3 class="product-title">{{ product.title }}</h3>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<script>
import { getNewProducts } from '../api/products'

export default {
  name: 'NewProducts',
  data() {
    return {
      products: []
    }
  },
  mounted() {
    this.fetchNewProducts();
  },
  methods: {
    // 获取最新产品数据
    async fetchNewProducts() {
      // 使用公开新品接口获取数据：GET /api/public/products/new
      const response = await getNewProducts({ page: 1, size: 50 });

      const apiProducts = response?.data?.data?.content || [];

      // 后端已保证仅返回上架且标记为新品的产品，这里只做数量截取和映射
      const latestProducts = apiProducts.slice(0, 8);

      this.products = latestProducts.map((product, index) => ({
        id: product.id || `product-${index}`,
        title: product.name || product.productName || product.title || `New Product ${index + 1}`,
        image: product.mainImage || product.image || product.images?.[0],

        category: product.category || product.type || 'uncategorized'
      }));
    },

    viewProduct(product) {
      console.log('查看产品:', product);

      // 跳转到产品详情页面
      const productRoute = {
        name: 'ProductDetail',
        params: {
          id: product.id
        },
        query: {
          category: product.category,
          title: product.title
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
          'event_label': product.title,
          'product_id': product.id,
          'product_category': product.category
        });
      }

      // 或者发送到后端API记录用户行为
      console.log('产品点击事件:', {
        productId: product.id,
        title: product.title,
        category: product.category,
        timestamp: new Date().toISOString()
      });
    }
  }
}
</script>

<style scoped>
.new-products {
  padding: 60px 0;
  background: #f8f9fa;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.section-header {
  text-align: center;
  margin-bottom: 40px;
}

.section-title {
  font-size: 28px;
  font-weight: 700;
  color: #fff;
  margin: 0;
  position: relative;
  display: inline-block;
  text-transform: uppercase;
  letter-spacing: 1px;
}

.section-title::after {
  content: '';
  position: absolute;
  bottom: -8px;
  left: 50%;
  transform: translateX(-50%);
  width: 50px;
  height: 3px;
  background: #fff;
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.product-card {
  background: #fff;
  border-radius: 4px;
  overflow: hidden;
  transition: all 0.3s ease;
  cursor: pointer;
  border: 1px solid #e8e8e8;
  position: relative;
}

.product-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
  border-color: #ff6a00;
}

.product-card:active {
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

/* 移除可点击提示效果，避免干扰 */

.product-image-container {
  position: relative;
  width: 100%;
  aspect-ratio: 1/1;
  overflow: hidden;
  background: #fafafa;
  display: flex;
  align-items: center;
  justify-content: center;
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

.product-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f5f5;
  border-bottom: 1px solid #eee;
}

.product-info {
  padding: 15px 12px;
  text-align: center;
  background: #fff;
  position: relative;
  z-index: 2;
}

.product-title {
  font-size: 14px;
  font-weight: 500;
  color: #333;
  margin: 0;
  line-height: 1.4;
  min-height: 38px;
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
  word-wrap: break-word;
  overflow-wrap: break-word;
  hyphens: auto;
}

.product-price {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  margin-bottom: 6px;
}

.current-price {
  font-size: 16px;
  font-weight: 700;
  color: #ff6a00;
}

.original-price {
  font-size: 12px;
  color: #999;
  text-decoration: line-through;
}

.product-discount {
  display: flex;
  justify-content: center;
}

.discount-badge {
  background: #ff6a00;
  color: white;
  font-size: 10px;
  font-weight: 600;
  padding: 2px 6px;
  border-radius: 3px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

/* 移除产品标签效果，简化设计 */

/* 响应式设计 */
@media (max-width: 1024px) {
  .products-grid {
    grid-template-columns: repeat(3, 1fr);
    gap: 18px;
  }

  .section-title {
    font-size: 24px;
  }

  .product-title {
    font-size: 13px;
    min-height: 34px;
    line-height: 1.3;
  }
}

@media (max-width: 768px) {
  .new-products {
    padding: 40px 0;
  }

  .container {
    padding: 0 15px;
  }

  .products-grid {
    grid-template-columns: repeat(5, 1fr);
    gap: 10px;
  }

  .section-header {
    margin-bottom: 30px;
  }

  .section-title {
    font-size: 22px;
  }

  .product-card {
    border-radius: 8px;
  }

  .product-info {
    padding: 8px 6px;
  }

  .product-title {
    font-size: 10px;
    min-height: 24px;
    line-height: 1.2;
  }

  .current-price {
    font-size: 12px;
  }

  .original-price {
    font-size: 10px;
  }

  .discount-badge {
    font-size: 8px;
    padding: 1px 4px;
  }
}

@media (max-width: 480px) {
  .new-products {
    padding: 30px 0;
  }

  .section-header {
    margin-bottom: 25px;
  }

  .section-title {
    font-size: 20px;
  }

  .products-grid {
    grid-template-columns: repeat(5, 1fr);
    gap: 8px;
  }

  .product-card {
    border-radius: 6px;
  }

  .product-info {
    padding: 6px 4px;
  }

  .product-title {
    font-size: 9px;
    min-height: 20px;
    line-height: 1.2;
  }

  .current-price {
    font-size: 11px;
  }

  .discount-badge {
    font-size: 7px;
    padding: 1px 3px;
  }
}
</style>