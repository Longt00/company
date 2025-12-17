<template>
  <div class="carousel-card">
    <div class="card-header">
      <h4>{{ carousel.title }}</h4>
      <span class="count-badge">{{ carousel.images.length }}/{{ carousel.maxCount }}</span>
    </div>

    <div class="carousel-info">
      <div class="info-item">
        <i class="bi bi-aspect-ratio"></i>
        <span>{{ carousel.size }}</span>
      </div>
      <div class="info-item">
        <i class="bi bi-file-earmark"></i>
        <span>最大 {{ carousel.maxSize }}MB</span>
      </div>
      <div class="info-item">
        <i class="bi bi-info-circle"></i>
        <span>{{ carousel.description }}</span>
      </div>
    </div>

    <!-- 图片列表 - 根据类型自适应列数 -->
    <div class="images-grid" :class="getGridClass()">
      <!-- 每个位置都是一个独立的上传格子 -->
      <div
        v-for="position in carousel.maxCount"
        :key="`position-${position}`"
        class="image-slot"
        :class="{ empty: !getImageAtPosition(position) }"
      >
        <!-- 如果该位置有图片，显示图片预览 -->
        <div v-if="getImageAtPosition(position)" class="image-preview" @click="showFullImage(getImageAtPosition(position).url)">
          <img :src="getImageAtPosition(position).url" :alt="`${carousel.title}`">
          <div class="image-overlay">
            <button class="btn-overlay" @click.stop="handleDeleteImageAtPosition(position)">
              <i class="bi bi-trash"></i>
            </button>
          </div>
        </div>

        <!-- 如果该位置没有图片，显示独立的上传区域 -->
        <div v-else class="upload-area">
          <input
            type="file"
            :id="`file-${carousel.type}-${position}`"
            @change="(e) => handleFileSelect(e, position)"
            accept="image/jpeg,image/png,image/gif,image/webp"
            style="display: none"
          >
          <label :for="`file-${carousel.type}-${position}`" class="upload-placeholder">
            <i class="bi bi-cloud-upload"></i>
            <p>点击上传</p>
          </label>
        </div>

        </div>
    </div>

    <!-- 全屏预览 -->
    <div v-if="fullscreenUrl" class="fullscreen-preview" @click="fullscreenUrl = null">
      <div class="preview-content" @click.stop>
        <img :src="fullscreenUrl" :alt="carousel.title">
        <button class="close-btn" @click="fullscreenUrl = null">
          <i class="bi bi-x-lg"></i>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import Swal from 'sweetalert2'

const props = defineProps({
  carousel: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['upload', 'delete'])

const fullscreenUrl = ref(null)

// 获取指定位置的图片
const getImageAtPosition = (position) => {
  return props.carousel.images.find(img => img.position === position)
}

// 根据图片类型返回对应的CSS类
const getGridClass = () => {
  const type = props.carousel.type
  switch (type) {
    case 'about-overview':
      return 'grid-cols-3' // 3列2行
    case 'about-production':
      return 'grid-cols-3' // 3列3行
    case 'about-rd':
      return 'grid-cols-3' // 3列2行
    case 'about-quality':
      return 'grid-cols-3' // 3列布局，与其他图片大小一致
    case 'home-carousel':
    case 'about-carousel':
    case 'about-bottom-fixed':
    default:
      return 'grid-cols-3' // 默认3列布局
  }
}

// 计算空位数量（保留这个计算属性用于显示数量）
const emptySlots = computed(() => {
  return Math.max(0, props.carousel.maxCount - props.carousel.images.length)
})

// 处理文件选择
const handleFileSelect = async (event, position) => {
  const file = event.target.files[0]
  if (!file) return

  // 验证文件类型
  const validTypes = ['image/jpeg', 'image/png', 'image/gif', 'image/webp']
  if (!validTypes.includes(file.type)) {
    Swal.fire({
      title: '温馨提示',
      text: '请选择有效的图片格式 (JPG, PNG, GIF, WebP)',
      icon: 'warning',
      confirmButtonText: '确定',
      confirmButtonColor: '#1976d2'
    })
    return
  }

  // 验证文件大小
  const maxSize = props.carousel.maxSize * 1024 * 1024
  if (file.size > maxSize) {
    Swal.fire({
      title: '温馨提示',
      text: `文件大小不能超过 ${props.carousel.maxSize}MB`,
      icon: 'warning',
      confirmButtonText: '确定',
      confirmButtonColor: '#1976d2'
    })
    return
  }

  // 显示确认对话框，明确告知用户图片将固定在此位置
  const result = await Swal.fire({
    title: '确认上传',
    html: `图片将上传到<strong>位置 ${position}</strong>并固定显示在此位置<br>是否继续？`,
    icon: 'question',
    showCancelButton: true,
    confirmButtonText: '确定上传',
    cancelButtonText: '取消',
    confirmButtonColor: '#1976d2',
    cancelButtonColor: '#6c757d'
  })

  if (!result.isConfirmed) {
    event.target.value = ''
    return
  }

  // 触发上传事件，传递位置信息
  emit('upload', props.carousel.type, file, position)

  // 清空文件输入
  event.target.value = ''
}

// 处理删除图片（基于索引）
const handleDeleteImage = (index) => {
  emit('delete', props.carousel.type, props.carousel.images[index].id, index)
}

// 处理删除指定位置的图片
const handleDeleteImageAtPosition = async (position) => {
  const image = getImageAtPosition(position)
  if (!image) {
    console.warn(`位置 ${position} 没有图片可删除`)
    return
  }

  // 显示确认对话框，明确告知用户正在删除哪个位置的图片
  const result = await Swal.fire({
    title: '确认删除',
    html: `确定要删除<strong>位置 ${position}</strong>的图片吗？<br>此操作不可撤销。`,
    icon: 'warning',
    showCancelButton: true,
    confirmButtonText: '确定删除',
    cancelButtonText: '取消',
    confirmButtonColor: '#dc3545',
    cancelButtonColor: '#6c757d'
  })

  if (result.isConfirmed) {
    emit('delete', props.carousel.type, image.id, position)
  }
}

// 显示全屏图片
const showFullImage = (url) => {
  fullscreenUrl.value = url
}

// 格式化时间
const formatTime = (time) => {
  if (!time) return '未更新'
  const date = new Date(time)
  return date.toLocaleDateString('zh-CN', { month: 'short', day: 'numeric' })
}
</script>

<style scoped>
.carousel-card {
  background: white;
  border: 1px solid #e0e0e0;
  border-radius: 12px;
  padding: 20px;
  transition: all 0.3s;
}

.carousel-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  border-color: #667eea;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.card-header h4 {
  margin: 0;
  font-size: 18px;
  color: #2c3e50;
  font-weight: 600;
}

.count-badge {
  background: #e3f2fd;
  color: #1976d2;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 13px;
  font-weight: 600;
}

.carousel-info {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-bottom: 15px;
  padding: 10px;
  background: #f8f9fa;
  border-radius: 6px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: #666;
}

.info-item i {
  color: #667eea;
}

.images-grid {
  display: grid;
  gap: 15px;
}

/* 不同列数的网格布局 */
.images-grid.grid-cols-2 {
  grid-template-columns: repeat(2, 1fr);
}

.images-grid.grid-cols-3 {
  grid-template-columns: repeat(3, 1fr);
}

.images-grid.grid-cols-4 {
  grid-template-columns: repeat(4, 1fr);
}

.image-slot {
  position: relative;
  aspect-ratio: 16/9;
  border-radius: 8px;
  overflow: hidden;
}

.image-preview {
  width: 100%;
  height: 100%;
  position: relative;
  cursor: pointer;
}

.image-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s;
}

.image-preview:hover img {
  transform: scale(1.05);
}

.image-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s;
}

.image-preview:hover .image-overlay {
  opacity: 1;
}

.btn-overlay {
  background: rgba(255, 255, 255, 0.9);
  border: none;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  cursor: pointer;
  color: #dc3545;
  font-size: 18px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s;
}

.btn-overlay:hover {
  background: #dc3545;
  color: white;
  transform: scale(1.1);
}

.empty-preview {
  width: 100%;
  height: 100%;
  border: 2px dashed #ddd;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: border-color 0.3s;
}

.empty-preview:hover {
  border-color: #667eea;
}

.upload-area {
  width: 100%;
  height: 100%;
  border: 2px dashed #ddd;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: border-color 0.3s;
}

.upload-area:hover {
  border-color: #667eea;
}

.upload-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: #999;
  width: 100%;
  height: 100%;
  transition: color 0.3s;
}

.upload-placeholder:hover {
  color: #667eea;
}

.upload-placeholder i {
  font-size: 36px;
  margin-bottom: 8px;
}

.upload-placeholder p {
  margin: 0;
  font-size: 14px;
}

.fullscreen-preview {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.9);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
  animation: fadeIn 0.3s;
}

.preview-content {
  position: relative;
  max-width: 90%;
  max-height: 90%;
}

.preview-content img {
  max-width: 100%;
  max-height: 90vh;
  object-fit: contain;
}

.close-btn {
  position: absolute;
  top: -50px;
  right: 0;
  background: rgba(255, 255, 255, 0.2);
  border: none;
  color: white;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  cursor: pointer;
  font-size: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background 0.3s;
}

.close-btn:hover {
  background: rgba(255, 255, 255, 0.3);
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

@media (max-width: 1200px) {
  /* 中等屏幕：4列变为2列，3列保持2列 */
  .images-grid.grid-cols-4 {
    grid-template-columns: repeat(2, 1fr) !important;
  }

  .images-grid.grid-cols-3,
  .images-grid.grid-cols-2 {
    grid-template-columns: repeat(2, 1fr) !important;
  }
}

@media (max-width: 768px) {
  /* 小屏幕：所有网格变为单列 */
  .images-grid {
    grid-template-columns: 1fr !important;
  }

  .image-slot {
    aspect-ratio: 16/9;
  }
}
</style>
