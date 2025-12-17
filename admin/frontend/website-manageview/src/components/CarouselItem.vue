<template>
  <div class="carousel-item">
    <div class="item-number">{{ index + 1 }}</div>
    
    <div class="item-preview">
      <img 
        v-if="item.url" 
        :src="item.url" 
        :alt="item.title"
      >
      <div v-else class="placeholder">
        <i class="bi bi-image"></i>
      </div>
    </div>

    <div class="item-content">
      <h5 class="item-title">{{ item.title || '未命名轮播图' }}</h5>
      <p class="item-description">{{ item.description || '暂无描述' }}</p>
      <div v-if="item.link" class="item-link">
        <i class="bi bi-link-45deg"></i>
        <span>{{ item.link }}</span>
      </div>
    </div>

    <div class="item-actions">
      <div class="form-check form-switch">
        <input 
          class="form-check-input" 
          type="checkbox" 
          :id="`switch-${item.id}`"
          :checked="item.enabled"
          @change="$emit('toggle', item.id)"
        >
        <label class="form-check-label" :for="`switch-${item.id}`">
          {{ item.enabled ? '已启用' : '已禁用' }}
        </label>
      </div>

      <div class="action-buttons">
        <button 
          class="btn btn-sm btn-outline-secondary"
          @click="$emit('edit', item)"
          title="编辑"
        >
          <i class="bi bi-pencil"></i>
        </button>

        <button 
          class="btn btn-sm btn-outline-danger"
          @click="$emit('delete', item.id)"
          title="删除"
        >
          <i class="bi bi-trash"></i>
        </button>

        <div class="sort-buttons">
          <button 
            class="btn btn-sm btn-outline-primary"
            @click="$emit('move-up')"
            :disabled="index === 0"
            title="上移"
          >
            <i class="bi bi-arrow-up"></i>
          </button>
          <button 
            class="btn btn-sm btn-outline-primary"
            @click="$emit('move-down')"
            :disabled="isLast"
            title="下移"
          >
            <i class="bi bi-arrow-down"></i>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  item: {
    type: Object,
    required: true
  },
  index: {
    type: Number,
    required: true
  },
  total: {
    type: Number,
    default: 0
  }
})

defineEmits(['edit', 'delete', 'move-up', 'move-down', 'toggle'])

const isLast = computed(() => {
  return props.index === props.total - 1
})
</script>

<style scoped>
.carousel-item {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 15px;
  background: white;
  border: 1px solid #e0e0e0;
  border-radius: 10px;
  transition: all 0.3s;
}

.carousel-item:hover {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  border-color: #667eea;
}

.item-number {
  width: 40px;
  height: 40px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 18px;
  flex-shrink: 0;
}

.item-preview {
  width: 120px;
  height: 80px;
  border: 1px solid #ddd;
  border-radius: 8px;
  overflow: hidden;
  flex-shrink: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f8f9fa;
}

.item-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.placeholder {
  color: #999;
  font-size: 32px;
}

.item-content {
  flex: 1;
  min-width: 0;
}

.item-title {
  margin: 0 0 5px 0;
  font-size: 16px;
  font-weight: 600;
  color: #2c3e50;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.item-description {
  margin: 0 0 5px 0;
  font-size: 14px;
  color: #666;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.item-link {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 12px;
  color: #999;
}

.item-link i {
  font-size: 16px;
}

.item-link span {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.item-actions {
  display: flex;
  flex-direction: column;
  gap: 10px;
  align-items: flex-end;
  flex-shrink: 0;
}

.form-check {
  margin: 0;
}

.form-check-label {
  font-size: 13px;
  color: #666;
  cursor: pointer;
}

.form-check-input {
  cursor: pointer;
}

.action-buttons {
  display: flex;
  gap: 8px;
  align-items: center;
}

.sort-buttons {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.sort-buttons .btn {
  padding: 4px 8px;
  line-height: 1;
}

.btn-sm {
  padding: 6px 12px;
}

@media (max-width: 768px) {
  .carousel-item {
    flex-direction: column;
    align-items: stretch;
  }

  .item-number {
    align-self: flex-start;
  }

  .item-preview {
    width: 100%;
    height: 150px;
  }

  .item-actions {
    flex-direction: row;
    justify-content: space-between;
    align-items: center;
  }

  .sort-buttons {
    flex-direction: row;
  }
}
</style>
