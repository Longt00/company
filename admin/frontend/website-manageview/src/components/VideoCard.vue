<template>
  <div class="video-card">
    <!-- 视频预览区 -->
    <div class="video-preview">
      <div v-if="video.url" class="video-player">
        <video
          ref="videoPlayer"
          :src="video.url"
          controls
          @loadedmetadata="handleVideoLoaded"
        >
          <source :src="video.url" type="video/mp4">
          您的浏览器不支持视频播放
        </video>
      </div>
      <div v-else class="video-placeholder">
        <i class="bi bi-film"></i>
        <p>暂无视频</p>
        <small>{{ title || '点击下方按钮上传视频' }}</small>
      </div>
    </div>

    <!-- 视频信息 -->
    <div class="video-info">
      <div v-if="video.url" class="info-content">
        <div class="info-row">
          <span class="info-label"><i class="bi bi-file-earmark-text"></i> 文件名:</span>
          <span class="info-value">{{ video.fileName || '未知' }}</span>
        </div>
        <div class="info-row">
          <span class="info-label"><i class="bi bi-hdd"></i> 大小:</span>
          <span class="info-value">{{ video.fileSize || '未知' }}</span>
        </div>
        <div class="info-row">
          <span class="info-label"><i class="bi bi-clock"></i> 时长:</span>
          <span class="info-value">{{ videoDuration || '未知' }}</span>
        </div>
        <div class="info-row">
          <span class="info-label"><i class="bi bi-calendar"></i> 更新:</span>
          <span class="info-value">{{ formatTime(video.updateTime) }}</span>
        </div>
      </div>
      <div v-else class="info-empty">
        <p>暂无视频信息</p>
      </div>
    </div>

    <!-- 操作按钮 -->
    <div class="video-actions">
      <input
        ref="fileInput"
        type="file"
        accept="video/mp4,video/avi,video/mov,video/wmv,video/flv,video/webm,video/mkv,video/3gpp,video/quicktime"
        @change="handleFileSelect"
        style="display: none"
      >
      
      <button 
        class="btn btn-primary"
        @click="$refs.fileInput.click()"
        :disabled="uploading"
      >
        <span v-if="uploading" class="spinner-border spinner-border-sm me-2"></span>
        <i v-else class="bi bi-cloud-upload"></i>
        {{ uploading ? '上传中...' : (video.url ? '更换视频' : '上传视频') }}
      </button>

      <button 
        v-if="video.url"
        class="btn btn-outline-danger"
        @click="$emit('delete')"
        :disabled="uploading"
      >
        <i class="bi bi-trash"></i>
        删除
      </button>
    </div>

    <!-- 上传进度 -->
    <div v-if="uploadProgress > 0 && uploading" class="upload-progress">
      <div class="progress">
        <div 
          class="progress-bar" 
          :style="{ width: uploadProgress + '%' }"
        >
          {{ uploadProgress }}%
        </div>
      </div>
      <small class="text-muted">正在上传视频，请稍候...</small>
    </div>
  </div>
</template>
<script setup>
import { ref, computed } from 'vue'

const props = defineProps({
  video: {
    type: Object,
    required: true
  },
  title: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['upload', 'delete'])

const fileInput = ref(null)
const videoPlayer = ref(null)
const uploading = ref(false)
const uploadProgress = ref(0)
const videoDuration = ref('')

// 处理文件选择
const handleFileSelect = (event) => {
  const file = event.target.files[0]
  if (!file) return

  // 获取视频时长
  const video = document.createElement('video')
  video.preload = 'metadata'
  video.onloadedmetadata = () => {
    window.URL.revokeObjectURL(video.src)
    const duration = Math.floor(video.duration)
    const minutes = Math.floor(duration / 60)
    const seconds = duration % 60
    videoDuration.value = `${minutes}:${seconds.toString().padStart(2, '0')}`
  }
  video.src = URL.createObjectURL(file)

  // 触发上传
  emit('upload', file)
  
  // 清空输入框，允许重复选择同一文件
  if (fileInput.value) {
    fileInput.value.value = ''
  }
}

// 视频加载完成
const handleVideoLoaded = () => {
  if (videoPlayer.value) {
    const duration = Math.floor(videoPlayer.value.duration)
    const minutes = Math.floor(duration / 60)
    const seconds = duration % 60
    videoDuration.value = `${minutes}:${seconds.toString().padStart(2, '0')}`
  }
}

// 格式化时间
const formatTime = (time) => {
  if (!time) return '未知'
  const date = new Date(time)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}
</script>

<style scoped>
.video-card {
  background: #f8f9fa;
  border-radius: 12px;
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

/* 视频预览 */
.video-preview {
  width: 100%;
  border-radius: 10px;
  overflow: hidden;
  background: #000;
}

.video-player {
  width: 100%;
  aspect-ratio: 16 / 9;
}

.video-player video {
  width: 100%;
  height: 100%;
  display: block;
}

.video-placeholder {
  width: 100%;
  aspect-ratio: 16 / 9;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #bbdefb 0%, #e3f2fd 100%);
  color: #1976d2;
}

.video-placeholder i {
  font-size: 60px;
  margin-bottom: 10px;
  opacity: 0.7;
}

.video-placeholder p {
  margin: 0 0 5px 0;
  font-size: 18px;
  font-weight: 500;
  color: #1565c0;
}

.video-placeholder small {
  font-size: 13px;
  opacity: 0.7;
  color: #1976d2;
}

/* 视频信息 */
.video-info {
  background: white;
  padding: 15px;
  border-radius: 8px;
}

.info-content {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.info-row {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 14px;
}

.info-label {
  color: #666;
  font-weight: 500;
  min-width: 80px;
  display: flex;
  align-items: center;
  gap: 5px;
}

.info-label i {
  font-size: 16px;
  color: #1976d2;
}

.info-value {
  color: #2c3e50;
  flex: 1;
  word-break: break-all;
}

.info-empty {
  text-align: center;
  padding: 15px;
  color: #999;
}

.info-empty p {
  margin: 0;
  font-size: 14px;
}

/* 操作按钮 */
.video-actions {
  display: flex;
  gap: 10px;
}

.video-actions .btn {
  flex: 1;
  padding: 12px 20px;
  font-size: 15px;
  font-weight: 500;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.video-actions .btn i {
  font-size: 18px;
}

/* 上传进度 */
.upload-progress {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.progress {
  height: 28px;
  border-radius: 8px;
  overflow: hidden;
  background: #e9ecef;
}

.progress-bar {
  height: 100%;
  background: linear-gradient(135deg, #1976d2 0%, #2196f3 100%);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 13px;
  font-weight: 600;
  transition: width 0.3s;
}

.upload-progress small {
  text-align: center;
}
</style>