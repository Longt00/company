<template>
  <div class="image-card">
    <div class="card-header">
      <h4>{{ image.title }}</h4>
      <span class="size-badge">{{ image.size }}</span>
    </div>

    <div class="image-preview" @click="showFullImage = true">
      <img 
        v-if="previewUrl || image.url" 
        :src="previewUrl || image.url || defaultImage" 
        :alt="image.title"
      >
      <div v-else class="placeholder">
        <i class="bi bi-image"></i>
        <p>暂无图片</p>
      </div>
    </div>

    <div class="image-info">
      <div v-if="image.updateTime" class="info-item">
        <i class="bi bi-clock"></i>
        <span>{{ formatTime(image.updateTime) }}</span>
      </div>
      <div v-if="fileSize" class="info-item">
        <i class="bi bi-file-earmark"></i>
        <span>{{ fileSize }}</span>
      </div>
      <div class="info-item">
        <i class="bi bi-info-circle"></i>
        <span>最大 {{ image.maxSize }}MB</span>
      </div>
    </div>

    <div class="upload-section">
      <input 
        type="file" 
        :id="`file-${image.type}`"
        @change="handleFileSelect"
        accept="image/jpeg,image/png,image/gif,image/webp"
        style="display: none"
      >
      
      <label 
        :for="`file-${image.type}`" 
        class="btn btn-outline-primary btn-sm"
      >
        <i class="bi bi-folder-open"></i>
        选择文件
      </label>

      <button 
        class="btn btn-primary btn-sm"
        @click="handleUpload"
        :disabled="!selectedFile || uploading"
      >
        <span v-if="uploading" class="spinner-border spinner-border-sm me-1"></span>
        <i v-else class="bi bi-upload"></i>
        {{ uploading ? '上传中...' : '上传' }}
      </button>
    </div>

    <div v-if="uploadProgress > 0 && uploading" class="progress mt-2">
      <div 
        class="progress-bar" 
        :style="{ width: uploadProgress + '%' }"
      >
        {{ uploadProgress }}%
      </div>
    </div>

    <div v-if="selectedFile" class="selected-file">
      <i class="bi bi-file-earmark-image"></i>
      <span>{{ selectedFile.name }}</span>
      <button class="btn-close-sm" @click="clearSelection">
        <i class="bi bi-x"></i>
      </button>
    </div>

    <div class="card-actions">
      <button 
        class="btn btn-outline-danger btn-sm"
        @click="handleDelete"
        :disabled="!image.url"
      >
        <i class="bi bi-trash"></i>
        删除图片
      </button>
    </div>

    <!-- 全屏预览 -->
    <div v-if="showFullImage" class="fullscreen-preview" @click="showFullImage = false">
      <div class="preview-content" @click.stop>
        <img :src="previewUrl || image.url" :alt="image.title">
        <button class="close-btn" @click="showFullImage = false">
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
  image: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['upload', 'delete'])

const selectedFile = ref(null)
const previewUrl = ref('')
const uploading = ref(false)
const uploadProgress = ref(0)
const showFullImage = ref(false)
const fileSize = ref('')

const defaultImage = '/placeholder.png'

// 处理文件选择
const handleFileSelect = (event) => {
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
  const maxSize = props.image.maxSize * 1024 * 1024 // 转换为字节
  if (file.size > maxSize) {
    Swal.fire({
      title: '温馨提示',
      text: `文件大小不能超过 ${props.image.maxSize}MB`,
      icon: 'warning',
      confirmButtonText: '确定',
      confirmButtonColor: '#1976d2'
    })
    return
  }

  selectedFile.value = file
  fileSize.value = formatFileSize(file.size)

  // 生成预览
  const reader = new FileReader()
  reader.onload = (e) => {
    previewUrl.value = e.target.result
  }
  reader.readAsDataURL(file)
}

// 处理上传
const handleUpload = async () => {
  if (!selectedFile.value) return

  uploading.value = true
  uploadProgress.value = 0

  // 模拟上传进度
  const interval = setInterval(() => {
    if (uploadProgress.value < 90) {
      uploadProgress.value += 10
    }
  }, 200)

  try {
    emit('upload', props.image.type, selectedFile.value)
    
    // 模拟上传完成
    setTimeout(() => {
      clearInterval(interval)
      uploadProgress.value = 100
      
      setTimeout(() => {
        uploading.value = false
        uploadProgress.value = 0
        clearSelection()
      }, 500)
    }, 2000)
  } catch (error) {
    clearInterval(interval)
    uploading.value = false
    uploadProgress.value = 0
    console.error('上传失败:', error)
  }
}

// 处理删除
const handleDelete = () => {
  emit('delete', props.image.type)
}

// 清除选择
const clearSelection = () => {
  selectedFile.value = null
  previewUrl.value = ''
  fileSize.value = ''
  const input = document.getElementById(`file-${props.image.type}`)
  if (input) input.value = ''
}

// 格式化文件大小
const formatFileSize = (bytes) => {
  if (bytes < 1024) return bytes + ' B'
  if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(2) + ' KB'
  return (bytes / (1024 * 1024)).toFixed(2) + ' MB'
}

// 格式化时间
const formatTime = (time) => {
  if (!time) return '未更新'
  const date = new Date(time)
  return date.toLocaleDateString('zh-CN')
}
</script>

<style scoped>
.image-card {
  background: white;
  border: 1px solid #e0e0e0;
  border-radius: 12px;
  padding: 20px;
  transition: all 0.3s;
}

.image-card:hover {
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

.size-badge {
  background: #e3f2fd;
  color: #1976d2;
  padding: 4px 10px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 500;
}

.image-preview {
  width: 100%;
  height: 200px;
  border: 2px dashed #ddd;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 15px;
  overflow: hidden;
  cursor: pointer;
  transition: border-color 0.3s;
}

.image-preview:hover {
  border-color: #667eea;
}

.image-preview img {
  max-width: 100%;
  max-height: 100%;
  object-fit: contain;
}

.placeholder {
  text-align: center;
  color: #999;
}

.placeholder i {
  font-size: 48px;
  margin-bottom: 10px;
  display: block;
}

.placeholder p {
  margin: 0;
  font-size: 14px;
}

.image-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 15px;
  padding: 10px;
  background: #f8f9fa;
  border-radius: 6px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 13px;
  color: #666;
}

.info-item i {
  color: #667eea;
}

.upload-section {
  display: flex;
  gap: 10px;
  margin-bottom: 10px;
}

.upload-section .btn {
  flex: 1;
}

.progress {
  height: 25px;
  border-radius: 6px;
  overflow: hidden;
  background: #e9ecef;
}

.progress-bar {
  height: 100%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 600;
  transition: width 0.3s;
}

.selected-file {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px;
  background: #e3f2fd;
  border-radius: 6px;
  margin-bottom: 10px;
  font-size: 14px;
  color: #1976d2;
}

.selected-file i {
  font-size: 18px;
}

.selected-file span {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.btn-close-sm {
  background: none;
  border: none;
  color: #1976d2;
  cursor: pointer;
  padding: 0;
  width: 20px;
  height: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: background 0.3s;
}

.btn-close-sm:hover {
  background: rgba(25, 118, 210, 0.1);
}

.card-actions {
  display: flex;
  gap: 10px;
}

.card-actions .btn {
  flex: 1;
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
  top: -40px;
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
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}
</style>
