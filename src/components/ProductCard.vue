<template>
  <div class="product-card" :class="{ 'list-view': viewMode === 'list' }" @click="goToProductDetail">
    <!-- 商品图片区域 -->
    <div class="product-image-container">
      <img
        :src="product.image"
        :alt="product.name"
        class="product-image"
        @error="handleImageError"
      />

      <!-- 新品标签 -->
      <div v-if="product.isNew" class="new-badge">
        <span>NEW</span>
      </div>

      <!-- 悬停时显示的操作按钮 -->
      <div class="product-overlay">
        <button class="quick-view-btn" @click.stop="quickView">
          <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
            <path d="M8 3C5 3 2.5 4.5 1 7C2.5 9.5 5 11 8 11C10.5 11 13 9.5 14.5 7C13 4.5 10.5 3 8 3Z" stroke="currentColor" stroke-width="1" stroke-linecap="round" stroke-linejoin="round"/>
            <circle cx="8" cy="7" r="2" stroke="currentColor" stroke-width="1"/>
          </svg>
          Quick View
        </button>
      </div>
    </div>

    <!-- 商品信息区域 -->
    <div class="product-info">
      <!-- 商品标题 -->
      <h3 class="product-name">{{ product.name }}</h3>

      <!-- 商品描述 -->
      <p v-if="product.description && viewMode === 'list'" class="product-description">
        {{ product.description }}
      </p>

      <!-- 商品评分（列表视图显示） -->
      <div v-if="viewMode === 'list'" class="product-rating">
        <div class="stars">
          <span v-for="i in 5" :key="i" class="star" :class="{ filled: i <= Math.floor(product.rating) }">★</span>
        </div>
        <span class="rating-value">{{ product.rating }}</span>
      </div>

      <!-- 操作按钮区域 -->
      <div class="product-actions">
        <button class="chat-now-btn" @click.stop="chatNow">
          <svg width="16" height="16" viewBox="0 0 16 16" fill="none">
            <path d="M13 2H3C2.4 2 2 2.4 2 3V16L5 13H13C13.6 13 14 12.6 14 12V3C14 2.4 13.6 2 13 2Z" stroke="currentColor" stroke-width="1" stroke-linecap="round" stroke-linejoin="round"/>
            <path d="M6 7H10M6 10H8" stroke="currentColor" stroke-width="1" stroke-linecap="round" stroke-linejoin="round"/>
          </svg>
          Chat Now
        </button>
      </div>
    </div>

    <!-- 商品详情模态框 -->
    <div v-if="showQuickView" class="quick-view-modal" @click="closeQuickView">
      <div class="modal-content" @click.stop>
        <button class="close-modal" @click="closeQuickView">×</button>
        <div class="modal-body">
          <div class="modal-image">
            <img :src="product.image" :alt="product.name" />
          </div>
          <div class="modal-info">
            <h3>{{ product.name }}</h3>
            <p v-if="product.description">{{ product.description }}</p>
            <div class="modal-price">
              <span class="price">${{ product.price }}</span>
              <span class="moq">MOQ: {{ product.moq }} units</span>
            </div>
            <div class="modal-rating">
              <div class="stars">
                <span v-for="i in 5" :key="i" class="star" :class="{ filled: i <= Math.floor(product.rating) }">★</span>
              </div>
              <span>{{ product.rating }} / 5.0</span>
            </div>
            <div class="modal-actions">
              <button class="chat-btn" @click.stop="chatNow">Chat Now</button>
              <button class="cart-btn" @click.stop="addToCart">Add to Cart</button>
            </div>
          </div>
        </div>
      </div>
    </div>

  </div>
</template>

<script>
export default {
  name: 'ProductCard',
  props: {
    product: {
      type: Object,
      required: true
    },
    viewMode: {
      type: String,
      default: 'grid'
    }
  },
  data() {
    return {
      showQuickView: false
    }
  },
  methods: {
    handleImageError(event) {
      // 图片加载失败时使用本地默认图片
      event.target.src = '/src/images/website/products/1.png'
      console.log('Image failed to load, using placeholder')
    },
    quickView() {
      this.showQuickView = true
    },
    closeQuickView() {
      this.showQuickView = false
    },
    chatNow() {
      this.$emit('chat-now', this.product)
    },
    addToCart() {
      this.$emit('add-to-cart', this.product)
      // 可以添加购物车动画或提示
      console.log('Added to cart:', this.product.name)
    },
    goToProductDetail() {
      this.$router.push(`/product/${this.product.id}`)
    }
  }
}
</script>

<style scoped>
/* 商品卡片容器 */
.product-card {
  background: white;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  transition: all 0.3s ease;
  cursor: pointer;
  border: 1px solid #f0f0f0;
  display: flex;
  flex-direction: column;
  height: 100%;
}

.product-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
  border-color: #333;
}

/* 列表视图样式 */
.product-card.list-view {
  flex-direction: row;
  align-items: center;
  padding: 16px;
  gap: 20px;
}

.product-card.list-view .product-image-container {
  width: 120px;
  height: 120px;
  flex-shrink: 0;
}

.product-card.list-view .product-info {
  flex: 1;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 20px;
}

.product-card.list-view .product-details {
  flex: 1;
}

.product-card.list-view .product-actions {
  flex-direction: column;
  gap: 8px;
}

/* 商品图片容器 */
.product-image-container {
  position: relative;
  width: 100%;
  aspect-ratio: 1;
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

/* 新品标签 */
.new-badge {
  position: absolute;
  top: 8px;
  left: 8px;
  background: #333;
  color: white;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 10px;
  font-weight: 600;
  letter-spacing: 0.5px;
  z-index: 1;
}

/* 悬停覆盖层 */
.product-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.7);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.product-card:hover .product-overlay {
  opacity: 1;
}

.quick-view-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 16px;
  background: white;
  color: #333;
  border: none;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.quick-view-btn:hover {
  background: #333;
  color: white;
  transform: scale(1.05);
}

/* 商品信息区域 */
.product-info {
  padding: 16px;
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.product-name {
  font-size: 14px;
  font-weight: 600;
  color: #333;
  line-height: 1.4;
  margin: 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
  min-height: 40px;
}

.product-description {
  font-size: 13px;
  color: #666;
  line-height: 1.5;
  margin: 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

/* 商品评分 */
.product-rating {
  display: flex;
  align-items: center;
  gap: 6px;
}

.stars {
  display: flex;
  gap: 2px;
}

.star {
  color: #ddd;
  font-size: 12px;
}

.star.filled {
  color: #ffc107;
}

.rating-value {
  font-size: 12px;
  color: #666;
  font-weight: 500;
}

/* 商品元信息 */
.product-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex: 1;
}

.price-section {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.product-price {
  font-size: 18px;
  font-weight: 700;
  color: #333;
}

.product-moq {
  font-size: 11px;
  color: #666;
  background: #f0f0f0;
  padding: 2px 6px;
  border-radius: 3px;
  display: inline-block;
}

/* 操作按钮 */
.product-actions {
  display: flex;
  gap: 8px;
  margin-top: auto;
}

.chat-now-btn {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  padding: 8px 12px;
  background: #25d366;
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.chat-now-btn:hover {
  background: #128c7e;
  transform: translateY(-1px);
}

.add-to-cart-btn {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  padding: 8px 12px;
  background: #333;
  color: white;
  border: none;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.add-to-cart-btn:hover {
  background: #555;
  transform: translateY(-1px);
}

/* 快速查看模态框 */
.quick-view-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2000;
  padding: 20px;
}

.modal-content {
  background: white;
  border-radius: 12px;
  max-width: 800px;
  width: 100%;
  max-height: 90vh;
  overflow-y: auto;
  position: relative;
}

.close-modal {
  position: absolute;
  top: 12px;
  right: 12px;
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: #666;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1;
  transition: all 0.3s ease;
}

.close-modal:hover {
  background: #f0f0f0;
  color: #333;
}

.modal-body {
  display: flex;
  padding: 32px;
  gap: 32px;
}

.modal-image {
  width: 300px;
  height: 300px;
  border-radius: 8px;
  overflow: hidden;
  flex-shrink: 0;
}

.modal-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.modal-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.modal-info h3 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: #333;
}

.modal-info p {
  margin: 0;
  color: #666;
  line-height: 1.6;
}

.modal-price {
  display: flex;
  align-items: center;
  gap: 12px;
}

.modal-price .price {
  font-size: 24px;
  font-weight: 700;
  color: #333;
}

.modal-price .moq {
  font-size: 14px;
  color: #666;
  background: #f0f0f0;
  padding: 4px 8px;
  border-radius: 4px;
}

.modal-rating {
  display: flex;
  align-items: center;
  gap: 8px;
}

.modal-rating .stars {
  display: flex;
  gap: 2px;
}

.modal-rating .star {
  font-size: 16px;
}

.modal-actions {
  display: flex;
  gap: 12px;
  margin-top: auto;
}

.modal-actions button {
  flex: 1;
  padding: 12px 24px;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
}

.chat-btn {
  background: #25d366;
  color: white;
}

.chat-btn:hover {
  background: #128c7e;
}

.cart-btn {
  background: #333;
  color: white;
}

.cart-btn:hover {
  background: #555;
}


/* 响应式设计 */
@media (max-width: 768px) {
  .product-name {
    font-size: 13px;
    min-height: 36px;
  }

  .product-price {
    font-size: 16px;
  }

  .product-actions {
    flex-direction: column;
    gap: 6px;
  }

  .chat-now-btn,
  .add-to-cart-btn {
    padding: 10px;
    font-size: 11px;
  }

  .modal-body {
    flex-direction: column;
    padding: 20px;
    gap: 20px;
  }

  .modal-image {
    width: 100%;
    height: 250px;
  }

  .modal-actions {
    flex-direction: column;
  }
}

@media (max-width: 480px) {
  .product-info {
    padding: 12px;
  }

  .product-name {
    font-size: 12px;
    min-height: 32px;
  }

  .product-price {
    font-size: 14px;
  }

  .product-moq {
    font-size: 10px;
    padding: 2px 4px;
  }

  .modal-body {
    padding: 16px;
  }

  .modal-info h3 {
    font-size: 18px;
  }

  .modal-price .price {
    font-size: 20px;
  }

  .product-card.list-view {
    flex-direction: column;
    padding: 12px;
  }

  .product-card.list-view .product-image-container {
    width: 100%;
    height: 200px;
  }

  .product-card.list-view .product-info {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
}
</style>