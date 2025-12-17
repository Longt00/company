<template>
  <div class="modal-overlay" @click="$emit('close')">
    <div class="modal-content" @click.stop>
      <div class="modal-header">
        <h4>{{ isEdit ? '编辑轮播图' : '添加轮播图' }}</h4>
        <button class="btn-close" @click="$emit('close')">
          <i class="bi bi-x-lg"></i>
        </button>
      </div>

      <div class="modal-body">
        <form @submit.prevent="handleSubmit">
          <div class="form-group mb-3">
            <label class="form-label">轮播图标题 *</label>
            <input 
              type="text" 
              class="form-control"
              v-model="form.title"
              placeholder="请输入轮播图标题"
              required
            >
          </div>

          <div class="form-group mb-3">
            <label class="form-label">描述</label>
            <textarea 
              class="form-control"
              v-model="form.description"
              placeholder="请输入轮播图描述"
              rows="3"
            ></textarea>
          </div>

          <div class="form-group mb-3">
            <label class="form-label">链接地址</label>
            <input 
              type="url" 
              class="form-control"
              v-model="form.link"
              placeholder="https://example.com"
            >
            <small class="form-text text-muted">点击轮播图时跳转的链接（选填）</small>
          </div>

          <div class="form-group mb-3">
            <label class="form-label">轮播图图片 *</label>
            <div class="image-upload-area">
              <div v-if="previewUrl" class="preview-container">
                <img :src="previewUrl" alt="预览">
                <button 
                  type="button" 
                  class="btn-remove"
                  @click="removeImage"
                >
                  <i class="bi bi-x-circle-fill"></i>
                </button>
              </div>
              <div v-else class="upload-placeholder" @click="triggerFileInput">
                <i class="bi bi-cloud-upload"></i>
                <p>点击或拖拽图片到此处上传</p>
                <small>推荐尺寸 1920x800，支持 JPG、PNG、GIF，最大 5MB</small>
              </div>
              <input 
                ref="fileInput"
                type="file" 
                accept="image/jpeg,image/png,image/gif,image/webp"
                @change="handleFileSelect"
                style="display: none"
              >
            </div>
          </div>

          <div class="form-group mb-3">
            <div class="form-check form-switch">
              <input 
                class="form-check-input" 
                type="checkbox" 
                id="enabledSwitch"
                v-model="form.enabled"
              >
              <label class="form-check-label" for="enabledSwitch">
                启用此轮播图
              </label>
            </div>
          </div>

          <div class="modal-footer">
            <button 
              type="button" 
              class="btn btn-secondary"
              @click="$emit('close')"
            >
              取消
            </button>
            <button 
              type="submit" 
              class="btn btn-primary"
              :disabled="!canSubmit"
            >
              <i class="bi bi-check-lg"></i>
              保存
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import Swal from 'sweetalert2'

const props = defineProps({
  carousel: {
    type: Object,
    default: null
  }
})

const emit = defineEmits(['save', 'close'])

const fileInput = ref(null)
const previewUrl = ref('')
const selectedFile = ref(null)

const form = ref({
  id: null,
  title: '',
  description: '',
  link: '',
  url: '',
  enabled: true
})

const isEdit = computed(() => !!props.carousel?.id)

const canSubmit = computed(() => {
  return form.value.title.trim() && (previewUrl.value || form.value.url)
})

onMounted(() => {
  if (props.carousel) {
    form.value = { ...props.carousel }
    if (props.carousel.url) {
      previewUrl.value = props.carousel.url
    }
  }
})

const triggerFileInput = () => {
  fileInput.value?.click()
}

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

  // 验证文件大小 (5MB)
  const maxSize = 5 * 1024 * 1024
  if (file.size > maxSize) {
    Swal.fire({
      title: '温馨提示',
      text: '文件大小不能超过 5MB',
      icon: 'warning',
      confirmButtonText: '确定',
      confirmButtonColor: '#1976d2'
    })
    return
  }

  selectedFile.value = file

  // 生成预览
  const reader = new FileReader()
  reader.onload = (e) => {
    previewUrl.value = e.target.result
  }
  reader.readAsDataURL(file)
}

const removeImage = () => {
  previewUrl.value = ''
  selectedFile.value = null
  form.value.url = ''
  if (fileInput.value) {
    fileInput.value.value = ''
  }
}

const handleSubmit = async () => {
  if (!canSubmit.value) return

  try {
    // 如果有新上传的文件，这里应该先上传文件获取URL
    if (selectedFile.value) {
      // TODO: 上传文件到服务器
      // const formData = new FormData()
      // formData.append('file', selectedFile.value)
      // const response = await axios.post('/api/images/upload', formData)
      // form.value.url = response.data.url
      
      // 临时使用预览URL
      form.value.url = previewUrl.value
    }

    emit('save', { ...form.value })
  } catch (error) {
    console.error('保存失败:', error)
    Swal.fire({
      title: '保存失败',
      text: '请重试',
      icon: 'error',
      confirmButtonText: '确定',
      confirmButtonColor: '#1976d2'
    })
  }
}
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  animation: fadeIn 0.3s;
}

.modal-content {
  background: white;
  border-radius: 12px;
  width: 90%;
  max-width: 600px;
  max-height: 90vh;
  overflow: hidden;
  display: flex;
  flex-direction: column;
  animation: slideUp 0.3s;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 25px;
  border-bottom: 1px solid #e0e0e0;
}

.modal-header h4 {
  margin: 0;
  font-size: 20px;
  color: #2c3e50;
  font-weight: 600;
}

.btn-close {
  background: none;
  border: none;
  font-size: 18px;
  color: #999;
  cursor: pointer;
  padding: 0;
  width: 30px;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: all 0.3s;
}

.btn-close:hover {
  background: #f0f0f0;
  color: #333;
}

.modal-body {
  padding: 25px;
  overflow-y: auto;
}

.form-label {
  font-weight: 500;
  color: #495057;
  margin-bottom: 8px;
  display: block;
}

.form-control {
  width: 100%;
  padding: 10px 15px;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  font-size: 14px;
  transition: all 0.3s;
}

.form-control:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 0.2rem rgba(102, 126, 234, 0.15);
}

.form-text {
  font-size: 12px;
  margin-top: 5px;
  display: block;
}

.image-upload-area {
  border: 2px dashed #ddd;
  border-radius: 8px;
  overflow: hidden;
  transition: border-color 0.3s;
}

.image-upload-area:hover {
  border-color: #667eea;
}

.preview-container {
  position: relative;
  width: 100%;
  height: 250px;
}

.preview-container img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.btn-remove {
  position: absolute;
  top: 10px;
  right: 10px;
  background: rgba(255, 255, 255, 0.9);
  border: none;
  color: #dc3545;
  font-size: 24px;
  cursor: pointer;
  border-radius: 50%;
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s;
}

.btn-remove:hover {
  background: white;
  transform: scale(1.1);
}

.upload-placeholder {
  padding: 60px 20px;
  text-align: center;
  color: #999;
  cursor: pointer;
  transition: background 0.3s;
}

.upload-placeholder:hover {
  background: #f8f9fa;
}

.upload-placeholder i {
  font-size: 48px;
  margin-bottom: 15px;
  display: block;
  color: #667eea;
}

.upload-placeholder p {
  margin: 0 0 5px 0;
  font-size: 16px;
  color: #666;
}

.upload-placeholder small {
  font-size: 12px;
  color: #999;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding-top: 20px;
  border-top: 1px solid #e0e0e0;
  margin-top: 20px;
}

.btn {
  padding: 10px 20px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
  border: none;
  display: inline-flex;
  align-items: center;
  gap: 6px;
}

.btn-secondary {
  background: #6c757d;
  color: white;
}

.btn-secondary:hover {
  background: #5a6268;
}

.btn-primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.btn-primary:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.btn-primary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

@keyframes slideUp {
  from {
    transform: translateY(30px);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}
</style>
