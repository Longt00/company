<template>
  <div class="product-card" :class="{ 'selected': selected }">
    <!-- 批量选择复选框 -->
    <div v-if="showBatchActions" class="batch-checkbox">
      <input
        type="checkbox"
        :id="`checkbox-${product.id}`"
        :checked="selected"
        @change="handleSelection"
        class="form-check-input"
      />
    </div>
    
    <div class="card-number">{{ index + 1 }}</div>
    
    <div class="card-content">
      <div class="product-image" @click="$emit('preview', product.image)">
        <img v-if="product.image" :src="product.image" :alt="product.name">
        <div v-else class="image-placeholder">
          <i class="bi bi-image"></i>
          <span>暂无图片</span>
        </div>
      </div>

      <div class="product-info">
        <div class="info-header">
          <h4 class="product-name">{{ product.name }}</h4>
          <div class="product-badges">
            <span v-if="product.isMain" class="badge badge-main">MAIN</span>
            <span v-if="product.isNew" class="badge badge-new">NEW</span>
            <span v-if="product.isFeatured" class="badge badge-featured">HOT</span>
          </div>
        </div>

        <div class="actions">
          <button
            class="btn btn-sm"
            :class="product.isFeatured ? 'btn-warning' : 'btn-outline-secondary'"
            @click="toggleFeatured"
            :title="product.isFeatured ? '取消热门推荐' : '设置为热门推荐'"
          >
            <i class="bi" :class="product.isFeatured ? 'bi-star-fill' : 'bi-star'"></i>
            {{ product.isFeatured ? 'HOT' : '设为HOT' }}
          </button>
          <button
            class="btn btn-sm"
            :class="product.isMain ? 'btn-primary' : 'btn-outline-info'"
            @click="toggleMain"
            :title="product.isMain ? '取消主产品' : '设置为主产品'"
          >
            <i class="bi" :class="product.isMain ? 'bi-house-fill' : 'bi-house'"></i>
            {{ product.isMain ? 'MAIN' : '设为MAIN' }}
          </button>
          <button
            class="btn btn-sm"
            :class="product.isNew ? 'btn-success' : 'btn-outline-success'"
            @click="toggleNew"
            :title="product.isNew ? '取消新产品' : '设置为新产品'"
          >
            <i class="bi" :class="product.isNew ? 'bi-bag-fill' : 'bi-bag'"></i>
            {{ product.isNew ? 'NEW' : '设为NEW' }}
          </button>
          <button
            class="btn btn-sm btn-outline-primary"
            @click="$emit('edit', product)"
          >
            <i class="bi bi-pencil"></i> 编辑
          </button>
          <button
            class="btn btn-sm btn-outline-danger"
            @click="$emit('delete', product.id)"
          >
            <i class="bi bi-trash"></i> 删除
          </button>
        </div>

        <div class="info-details">
          <div class="detail-item">
            <i class="bi bi-tag"></i>
            <span class="label">分类:</span>
            <span class="value">{{ product.category || '未分类' }}</span>
          </div>
          <div class="detail-item">
            <i class="bi bi-clock"></i>
            <span class="label">创建时间:</span>
            <span class="value">{{ formatDate(product.createdAt) }}</span>
          </div>
          <div class="detail-item">
            <i class="bi bi-check-circle-fill" :class="statusClass"></i>
            <span class="label">状态:</span>
            <span class="status-badge" :class="statusClass">{{ statusText }}</span>
          </div>
        </div>

        <div class="product-description">
          <div class="description-label">
            <i class="bi bi-file-text"></i>
            产品介绍:
          </div>
          <p>{{ product.description || '暂无介绍' }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  product: {
    type: Object,
    required: true
  },
  index: {
    type: Number,
    required: true
  },
  selected: {
    type: Boolean,
    default: false
  },
  showBatchActions: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['edit', 'delete', 'preview', 'toggleFeatured', 'toggleMain', 'toggleNew', 'selection-change'])

const handleSelection = () => {
  emit('selection-change', props.product.id, !props.selected)
}

const statusText = computed(() => {
  switch(props.product.status) {
    case 'published':
      return '上架'
    case 'inactive':
      return '下架'
    case 'draft':
      return '草稿'
    default:
      return '未知'
  }
})

const statusClass = computed(() => {
  return props.product.status || 'draft'
})

const formatDate = (date) => {
  if (!date) return '未知'
  const d = new Date(date)
  return d.toLocaleDateString('zh-CN')
}

const toggleFeatured = () => {
  emit('toggleFeatured', props.product.id, !props.product.isFeatured)
}

const toggleMain = () => {
  emit('toggleMain', props.product.id, !props.product.isMain)
}

const toggleNew = () => {
  emit('toggleNew', props.product.id, !props.product.isNew)
}
</script>

<style scoped>
.product-card {
  background: white;
  border: 1px solid #e0e0e0;
  border-radius: 12px;
  padding: 20px;
  transition: all 0.3s;
  position: relative;
}

.product-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  border-color: #667eea;
}

.card-number {
  position: absolute;
  top: 20px;
  left: 20px;
  width: 36px;
  height: 36px;
  background: linear-gradient(135deg, #4a90e2 0%, #357abd 100%);
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 16px;
  box-shadow: 0 2px 6px rgba(74, 144, 226, 0.3);
  z-index: 1;
}

.card-content {
  display: flex;
  gap: 25px;
  margin-top: 20px;
}

.product-image {
  width: 300px;
  height: 200px;
  flex-shrink: 0;
  border-radius: 10px;
  overflow: hidden;
  cursor: pointer;
  transition: transform 0.3s;
  border: 2px solid #e0e0e0;
}

.product-image:hover {
  transform: scale(1.02);
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.image-placeholder {
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: #f8f9fa;
  color: #999;
}

.image-placeholder i {
  font-size: 48px;
  margin-bottom: 10px;
}

.image-placeholder span {
  font-size: 14px;
}

.product-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.info-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 15px;
  border-bottom: 2px solid #f0f0f0;
}

.product-name {
  margin: 0;
  font-size: 22px;
  font-weight: 600;
  color: #2c3e50;
  flex: 1;
}

.product-badges {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.badge {
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 11px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.badge-main {
  background: linear-gradient(135deg, #007bff 0%, #0056b3 100%);
  color: white;
}

.badge-new {
  background: linear-gradient(135deg, #28a745 0%, #1e7e34 100%);
  color: white;
}

.badge-featured {
  background: linear-gradient(135deg, #ffc107 0%, #e0a800 100%);
  color: #212529;
}

.actions {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.info-details {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
}

.detail-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
}

.detail-item i {
  font-size: 16px;
  color: #667eea;
}

.detail-item i.published {
  color: #28a745;
}

.detail-item i.inactive {
  color: #dc3545;
}

.detail-item i.draft {
  color: #ffc107;
}

.detail-item .label {
  color: #666;
  font-weight: 500;
}

.detail-item .value {
  color: #2c3e50;
}

.status-badge {
  padding: 3px 10px;
  border-radius: 12px;
  font-size: 13px;
  font-weight: 500;
}

.status-badge.published {
  background: #d4edda;
  color: #155724;
}

.status-badge.inactive {
  background: #f8d7da;
  color: #721c24;
}

.status-badge.draft {
  background: #fff3cd;
  color: #856404;
}

.product-description {
  background: #f8f9fa;
  padding: 15px;
  border-radius: 8px;
  border-left: 4px solid #667eea;
}

.description-label {
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 10px;
  font-size: 15px;
}

.description-label i {
  margin-right: 6px;
  color: #667eea;
}

.product-description p {
  margin: 0;
  color: #666;
  line-height: 1.6;
  font-size: 14px;
}

/* 批量选择复选框 */
.batch-checkbox {
  position: absolute;
  top: 15px;
  right: 15px;
  z-index: 10;
  background: white;
  border-radius: 4px;
  padding: 2px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.batch-checkbox .form-check-input {
  width: 20px;
  height: 20px;
  cursor: pointer;
  border: 2px solid #667eea;
}

.batch-checkbox .form-check-input:checked {
  background-color: #667eea;
  border-color: #667eea;
}

/* 选中状态样式 */
.product-card.selected {
  border-color: #667eea;
  background: #f5f7ff;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.2);
}

@media (max-width: 768px) {
  .card-content {
    flex-direction: column;
  }

  .product-image {
    width: 100%;
    height: 200px;
  }

  .info-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }

  .actions {
    width: 100%;
  }

  .actions .btn {
    flex: 1;
  }

  .info-details {
    flex-direction: column;
    gap: 10px;
  }
}
</style>
