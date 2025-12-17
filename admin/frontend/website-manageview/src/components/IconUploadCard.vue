<template>
  <div class="icon-upload-card">
    <div class="card-header">
      <h4>{{ iconData.title }}</h4>
      <span class="status-badge" :class="{ 'has-image': iconData.image }">
        {{ iconData.image ? '已上传' : '未上传' }}
      </span>
    </div>

    <div class="icon-info">
      <div class="info-item">
        <i class="bi bi-aspect-ratio"></i>
        <span>{{ iconData.size }}</span>
      </div>
      <div class="info-item">
        <i class="bi bi-file-earmark"></i>
        <span>最大 {{ iconData.maxSize }}MB</span>
      </div>
      <div class="info-item">
        <i class="bi bi-info-circle"></i>
        <span>{{ iconData.description }}</span>
      </div>
    </div>

    <!-- 图标上传区域 -->
    <div class="icon-container">
      <!-- 如果有图片 -->
      <div v-if="iconData.image" class="icon-preview" @click="showFullImage(iconData.image.url)">
        <img :src="iconData.image.url" :alt="iconData.title">
        <div class="image-overlay">
          <button class="btn-overlay" @click.stop="handleDeleteImage">
            <i class="bi bi-trash"></i>
          </button>
        </div>
        <div class="position-indicator">{{ iconData.position }}</div>
      </div>

      <!-- 如果没有图片，显示上传区域 -->
      <div v-else class="upload-area">
        <input
          type="file"
          :id="`file-${iconData.type}`"
          @change="handleFileSelect"
          accept="image/jpeg,image/png,image/gif,image/webp,image/svg+xml"
          style="display: none"
        >
        <label :for="`file-${iconData.type}`" class="upload-placeholder">
          <div class="upload-icon">
            <i class="bi bi-cloud-upload"></i>
          </div>
          <div class="upload-text">
            <h5>{{ iconData.position }}</h5>
            <p>点击上传图标</p>
            <small>{{ iconData.size }} • 最大 {{ iconData.maxSize }}MB</small>
          </div>
        </label>
      </div>
    </div>
  </div>
</template>

<script setup>
import { defineProps, defineEmits } from 'vue'

const props = defineProps({
  iconData: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['upload', 'delete', 'preview'])

// 处理文件选择
const handleFileSelect = (event) => {
  const file = event.target.files[0]
  if (file) {
    emit('upload', props.iconData.type, file)
  }
  // 重置文件输入，允许重复选择同一文件
  event.target.value = ''
}

// 处理删除图片
const handleDeleteImage = () => {
  emit('delete', props.iconData.type, props.iconData.image?.id)
}

// 显示大图
const showFullImage = (imageUrl) => {
  emit('preview', imageUrl)
}

</script>

<style scoped>
.icon-upload-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.1);
  border: 1px solid #e8e8e8;
  transition: all 0.3s ease;
  min-height: 280px;
  display: flex;
  flex-direction: column;
}

.icon-upload-card:hover {
  box-shadow: 0 4px 20px rgba(0,0,0,0.15);
  transform: translateY(-2px);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.card-header h4 {
  margin: 0;
  color: #333;
  font-size: 16px;
  font-weight: 600;
}

.status-badge {
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
  background: #f0f0f0;
  color: #666;
  transition: all 0.3s ease;
}

.status-badge.has-image {
  background: #d4edda;
  color: #155724;
}

.icon-info {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-bottom: 15px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 12px;
  color: #666;
}

.info-item i {
  font-size: 14px;
  color: #999;
}

.icon-container {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: 150px;
}

.icon-preview {
  position: relative;
  width: 100%;
  height: 120px;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
  border: 2px solid #e0e0e0;
  transition: all 0.3s ease;
}

.icon-preview:hover {
  border-color: #667eea;
}

.icon-preview img {
  width: 100%;
  height: 100%;
  object-fit: contain;
  background: #fafafa;
}

.position-indicator {
  position: absolute;
  top: 8px;
  left: 8px;
  background: rgba(0,0,0,0.7);
  color: white;
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 11px;
  font-weight: 600;
}

.upload-area {
  width: 100%;
  height: 120px;
}

.upload-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
  border: 2px dashed #d0d0d0;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  background: #fafafa;
}

.upload-placeholder:hover {
  border-color: #667eea;
  background: #f8f9ff;
}

.upload-icon {
  font-size: 28px;
  color: #667eea;
  margin-bottom: 8px;
}

.upload-text {
  text-align: center;
}

.upload-text h5 {
  margin: 0 0 4px 0;
  font-size: 14px;
  font-weight: 600;
  color: #333;
}

.upload-text p {
  margin: 0 0 4px 0;
  font-size: 12px;
  color: #666;
}

.upload-text small {
  font-size: 10px;
  color: #999;
}

.image-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0,0,0,0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.icon-preview:hover .image-overlay {
  opacity: 1;
}

.btn-overlay {
  background: rgba(220,53,69,0.9);
  border: none;
  color: white;
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-overlay:hover {
  background: rgba(220,53,69,1);
  transform: scale(1.1);
}


@media (max-width: 768px) {
  .icon-upload-card {
    padding: 15px;
    min-height: 250px;
  }

  .icon-preview {
    height: 100px;
  }

  .upload-area {
    height: 100px;
  }

  .upload-icon {
    font-size: 24px;
    margin-bottom: 6px;
  }

  .upload-text h5 {
    font-size: 13px;
  }

  .upload-text p {
    font-size: 11px;
  }

  .upload-text small {
    font-size: 9px;
  }
}
</style>