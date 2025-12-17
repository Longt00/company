<template>
  <div class="hot-products-section">
    <h4 class="hot-products-title">Hot Products</h4>
    <div class="hot-products-list">
      <div
        v-for="product in hotProducts"
        :key="product.id"
        class="hot-product-item"
        @click="$emit('hot-product-click', product)"
      >
        <div class="hot-product-image">
          <img :src="product.image" :alt="product.name" />
        </div>
        <div class="hot-product-info">
          <h5 class="hot-product-name">{{ product.name }}</h5>
          <div class="hot-product-price">${{ product.price }}</div>
          <div class="hot-product-moq">MOQ: {{ product.moq }} units</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'HotProducts',
  props: {
    hotProducts: {
      type: Array,
      required: true
    }
  },
  emits: ['hot-product-click']
}
</script>

<style scoped>
/* 热门商品区域 */
.hot-products-section {
  padding: 20px;
  border-top: 1px solid #f0f0f0;
  background: #fafafa;
}

.hot-products-title {
  margin: 0 0 20px 0;
  font-size: 16px;
  font-weight: 600;
  color: #333;
}

/* 热门商品列表 */
.hot-products-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.hot-product-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  background: white;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 1px solid #f0f0f0;
}

.hot-product-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  border-color: #333;
}

/* 热门商品图片 */
.hot-product-image {
  width: 50px;
  height: 50px;
  border-radius: 4px;
  overflow: hidden;
  flex-shrink: 0;
}

.hot-product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* 热门商品信息 */
.hot-product-info {
  flex: 1;
  min-width: 0;
}

.hot-product-name {
  margin: 0 0 4px 0;
  font-size: 13px;
  font-weight: 600;
  color: #333;
  line-height: 1.3;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.hot-product-price {
  font-size: 14px;
  font-weight: 600;
  color: #333;
  margin-bottom: 2px;
}

.hot-product-moq {
  font-size: 11px;
  color: #666;
  background: #f0f0f0;
  padding: 2px 6px;
  border-radius: 3px;
  display: inline-block;
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .hot-products-section {
    position: static;
    max-height: none;
  }
}

@media (max-width: 768px) {
  .hot-products-section {
    padding: 15px;
  }

  .hot-product-item {
    padding: 10px;
  }

  .hot-product-image {
    width: 45px;
    height: 45px;
  }

  .hot-product-name {
    font-size: 12px;
  }

  .hot-product-price {
    font-size: 13px;
  }
}

@media (max-width: 480px) {
  .hot-products-section {
    padding: 12px;
  }

  .hot-product-item {
    gap: 8px;
    padding: 8px;
  }

  .hot-product-image {
    width: 40px;
    height: 40px;
  }
}
</style>