<template>
  <section class="advantages">
    <div class="container">
      <!-- 左右分栏容器 -->
      <div class="advantage-row">
        <!-- 左侧视频区域 -->
        <div class="video-col">
          <!-- 视频容器（后端渲染视频内容） -->
          <div class="video-container">
            <!-- 示例占位，实际由后端注入视频代码 -->
            <div class="placeholder-video">
              <img src="/images/placeholder-400x250.svg" alt="Video Placeholder">
            </div>
          </div>
        </div>

        <!-- 右侧商品列表区域 -->
        <div class="text-col">
          <h2 class="section-title">Product display</h2>
          <h6>High quality, good show</h6>
          <div class="product-list">
            <div
              v-for="(product, index) in products"
              :key="index"
              class="product-item"
              @click="goToProductDetail(product.id)"
            >
              <div class="product-image">
                <img :src="product.image" :alt="product.name">
              </div>
              <div class="product-info">
                <h4 class="product-name">{{ product.name }}</h4>
                <p class="product-description">{{ product.description }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<script>
// 导入产品图片
import product1 from '@/images/website/products/1.png'
import product2 from '@/images/website/products/2.png'
import product3 from '@/images/website/products/3.png'
import product4 from '@/images/website/products/4.png'
import product5 from '@/images/website/products/5.png'

export default {
  name: 'Productdisplay',
  data() {
    return {
      products: [
        {
          id: 1,
          name: 'Fashion T-Shirt',
          description: 'High quality cotton t-shirt with modern design',
          image: product1
        },
        {
          id: 2,
          name: 'Casual Jeans',
          description: 'Comfortable denim jeans perfect for everyday wear',
          image: product2
        },
        {
          id: 3,
          name: 'Summer Dress',
          description: 'Lightweight dress ideal for warm weather occasions',
          image: product3
        },
        {
          id: 4,
          name: 'Sports Jacket',
          description: 'Professional sports jacket with moisture-wicking technology',
          image: product4
        },
        {
          id: 5,
          name: 'Winter Coat',
          description: 'Warm and stylish coat suitable for cold weather conditions',
          image: product5
        }
      ]
    }
  },
  methods: {
    goToProductDetail(productId) {
      // 跳转到商品详情页面
      this.$router.push({
        name: 'ProductDetail',
        params: { id: productId }
      }).catch(err => {
        console.log('Navigation failed:', err)
        // 如果路由不存在，可以跳转到产品页面或显示提示
        this.$router.push('/products').catch(() => {
          console.log('Failed to navigate to products page')
        })
      })
    }
  }
}
</script>

<style scoped>
.advantages {
  padding: 60px 0;
  background: #fff;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

/* 左右分栏行 */
.advantage-row {
  display: flex;
  flex-wrap: wrap; 
  align-items: stretch; 
  gap: 20px; 
}

/* 视频列 */
.video-col {
  flex: 1 1 400px; 
}

.video-container {
  width: 100%;
  height: 100%;
  border: 1px solid #eee; 
  overflow: hidden; 
}

.placeholder-video img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
}

/* 文字列 */
.text-col {
  flex: 1 1 300px; 
}

.section-title {
  font-size: 18px;
  font-weight: bold;
  color:#ffffff;
  margin-bottom: 10px; /* 减小标题下边距 */
  white-space: nowrap; /* 防止换行 */
}

h6 {
  font-size: 18px;
  color: #ffffff;
  margin: 0 0 20px 0; /* 增加h6下边距 */
}

.product-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
  color: #ffffff;
}

.product-item {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 12px;
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.1);
  cursor: pointer;
  transition: all 0.3s ease;
}

.product-item:hover {
  background: rgba(255, 255, 255, 0.2);
  transform: translateY(-2px);
}

.product-image {
  width: 60px;
  height: 60px;
  border-radius: 6px;
  overflow: hidden;
  flex-shrink: 0;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.product-item:hover .product-image img {
  transform: scale(1.1);
}

.product-info {
  flex: 1;
  min-width: 0;
}

.product-name {
  font-size: 16px;
  font-weight: 600;
  color: #ffffff;
  margin: 0 0 5px 0;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.product-description {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.8);
  margin: 0;
  line-height: 1.4;
  display: -webkit-box;
  display: box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  box-orient: vertical;
  overflow: hidden;
}

/* 响应式适配（移动端堆叠） */
@media (max-width: 768px) {
  .advantages {
    padding: 40px 0;
  }

  .advantage-row {
    flex-direction: column;
    gap: 20px;
  }

  .section-title {
    font-size: 16px;
  }

  .product-item {
    padding: 10px;
    gap: 12px;
  }

  .product-image {
    width: 50px;
    height: 50px;
  }

  .product-name {
    font-size: 15px;
  }

  .product-description {
    font-size: 13px;
  }
}
</style>







