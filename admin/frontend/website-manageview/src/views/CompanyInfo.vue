<template>
  <div class="company-info">
    <div class="page-header">
      <h2><i class="bi bi-building"></i> 公司信息管理</h2>
      <button class="btn btn-outline-primary" @click="$router.push('/')">
        <i class="bi bi-house"></i> 返回首页
      </button>
    </div>

    <!-- 加载中提示 -->
    <div v-if="loading" class="loading-container">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">加载中...</span>
      </div>
      <p class="mt-3">正在加载公司信息...</p>
    </div>

    <!-- 左右对照布局 -->
    <div v-else class="content-layout">
      <!-- 左侧：当前信息展示 -->
      <div class="current-info">
        <div class="info-header">
          <h3><i class="bi bi-info-circle"></i> 当前信息</h3>
        </div>

        <!-- 1. 公司Logo - 对应右侧第一个section -->
        <div class="info-section">
          <h4><i class="bi bi-star"></i> 公司Logo</h4>
          <div class="info-item logo-info-item">
            <div v-if="currentLogo" class="current-logo-display">
              <img :src="currentLogo.url" :alt="currentLogo.originalName" class="current-logo-img" />
              <div class="logo-details">
                <p class="logo-filename">{{ currentLogo.originalName }}</p>
                <p class="logo-meta">{{ formatFileSize(currentLogo.fileSize) }} • {{ formatTime(currentLogo.updateTime || currentLogo.createTime) }}</p>
              </div>
            </div>
            <div v-else class="no-logo-placeholder">
              <i class="bi bi-image"></i>
              <span>暂未设置公司Logo</span>
            </div>
          </div>
        </div>

        <!-- 2. 基本信息 - 对应右侧第二个section -->
        <div class="info-section">
          <h4><i class="bi bi-building"></i> 基本信息</h4>
          <div class="info-item">
            <label>公司名称：</label>
            <span>{{ originalData?.companyName || '未设置' }}</span>
          </div>
          <div class="info-item">
            <label>公司简介：</label>
            <span class="text-content">{{ originalData?.companyDescription || '未设置' }}</span>
          </div>
        </div>

        <!-- 3. 联系方式 - 对应右侧第三个section -->
        <div class="info-section">
          <h4><i class="bi bi-telephone"></i> 联系方式</h4>
          <div class="info-item">
            <label>公司电话：</label>
            <span>{{ originalData?.companyPhone || '未设置' }}</span>
          </div>
          <div class="info-item">
            <label>公司邮箱：</label>
            <span>{{ originalData?.companyEmail || '未设置' }}</span>
          </div>
        </div>

        <!-- 4. 公司网站 - 对应右侧第四个section -->
        <div class="info-section">
          <h4><i class="bi bi-globe"></i> 公司网站</h4>
          <div class="info-item">
            <label>网站地址：</label>
            <span>{{ originalData?.companyWebsite || '未设置' }}</span>
          </div>
        </div>

        <!-- 5. 公司地址 - 对应右侧第五个section -->
        <div class="info-section">
          <h4><i class="bi bi-geo-alt"></i> 公司地址</h4>
          <div class="info-item">
            <label>详细地址：</label>
            <span class="text-content">{{ originalData?.companyAddress || '未设置' }}</span>
          </div>
        </div>

        <!-- 6. WhatsApp联系方式 - 对应右侧第六个section -->
        <div class="info-section">
          <h4><i class="bi bi-whatsapp"></i> WhatsApp联系方式</h4>
          <div class="info-item">
            <label>WhatsApp：</label>
            <span>{{ originalData?.companyPhone || '未设置' }}</span>
          </div>
        </div>
      </div>

      <!-- 右侧：编辑表单 -->
      <div class="edit-form">
        <div class="form-header">
          <h3><i class="bi bi-pencil-square"></i> 编辑信息</h3>
        </div>

        <form @submit.prevent="handleSubmit">
      <!-- 更换公司Logo -->
      <section class="section">
        <h3 class="section-title">
          <i class="bi bi-star"></i> 更换公司Logo
        </h3>

        <!-- Logo上传 -->
        <div class="logo-upload">
          <div class="upload-area" :class="{ 'has-logo': currentLogo }">
            <input
              type="file"
              id="logo-upload"
              accept="image/*"
              @change="handleLogoUpload"
              ref="logoInput"
              style="display: none"
            >
            <label for="logo-upload" class="upload-label">
              <div class="upload-icon">
                <i class="bi bi-cloud-upload"></i>
              </div>
              <div class="upload-text">
                <p class="upload-title">更换公司Logo</p>
                <p class="upload-desc">支持 JPG、PNG、GIF 格式，建议尺寸 400x200，最大 5MB</p>
              </div>
            </label>
          </div>

          <!-- 删除按钮 -->
          <button
            v-if="currentLogo"
            type="button"
            class="btn btn-danger delete-btn"
            @click="handleLogoDelete"
            :disabled="logoUploading"
          >
            <i class="bi bi-trash"></i>
            删除Logo
          </button>
        </div>

        <!-- 上传进度 -->
        <div v-if="logoUploading" class="upload-progress">
          <div class="progress-info">
            <i class="bi bi-hourglass-split"></i>
            <span>正在上传：{{ logoProgress.fileName }}</span>
          </div>
          <div class="progress-bar">
            <div
              class="progress-fill"
              :style="{ width: logoProgress.percent + '%' }"
            ></div>
          </div>
          <div class="progress-percent">{{ logoProgress.percent }}%</div>
        </div>
      </section>

    <!-- 基本信息 -->
      <section class="section">
        <h3 class="section-title">
          <i class="bi bi-building"></i> 基本信息
        </h3>

        <div class="form-group">
          <label class="form-label required">公司名称</label>
          <input
            type="text"
            class="form-control"
            v-model="form.companyName"
            placeholder="请输入公司名称"
            maxlength="200"
            required
          >
        </div>

        <div class="form-group">
          <label class="form-label required">公司简介</label>
          <textarea
            class="form-control"
            v-model="form.companyDescription"
            placeholder="请输入公司简介，建议200-500字"
            rows="6"
            maxlength="2000"
            required
            :style="{ color: form.descriptionTextColor || '#333333' }"
          ></textarea>
          <small class="form-text">{{ form.companyDescription.length }}/2000 字</small>

          <!-- 公司简介文字颜色选择器 -->
          <div class="color-picker-container">
            <div class="color-preview">
              <span
                class="preview-text"
                :style="{ color: form.descriptionTextColor || '#333333' }"
              >
                预览效果：公司简介文字颜色
              </span>
            </div>

            <div class="color-controls">
              <div class="quick-colors">
                <div class="color-title">快速选择</div>
                <div class="color-grid">
                  <div
                    v-for="color in presetColors"
                    :key="color"
                    class="color-option"
                    :style="{ backgroundColor: color }"
                    :class="{ active: form.descriptionTextColor === color }"
                    @click="selectDescriptionTextColor(color)"
                    :title="color"
                  ></div>
                </div>
              </div>

              <div class="custom-color-section">
                <div class="color-title">自定义颜色</div>
                <div class="custom-color-input">
                  <input
                    type="color"
                    :value="form.descriptionTextColor || '#333333'"
                    @input="selectDescriptionTextColor($event.target.value)"
                    class="color-input"
                  >
                  <input
                    type="text"
                    :value="form.descriptionTextColor || '#333333'"
                    @input="updateDescriptionTextColorFromHex($event.target.value)"
                    class="color-text-input"
                    placeholder="#000000"
                  >
                  <div
                    class="current-color-box"
                    :style="{ backgroundColor: form.descriptionTextColor || '#333333' }"
                  ></div>
                </div>
              </div>

              <div class="common-colors">
                <div class="color-title">常用文字颜色</div>
                <div class="common-color-list">
                  <div
                    v-for="textColor in textPresets"
                    :key="textColor.name"
                    class="common-color-item"
                    :class="{ active: form.descriptionTextColor === textColor.color }"
                    @click="selectDescriptionTextColor(textColor.color)"
                  >
                    <span
                      class="common-color-preview"
                      :style="{ color: textColor.color }"
                    >
                      {{ textColor.name }}
                    </span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- 联系方式 -->
      <section class="section">
        <h3 class="section-title">
          <i class="bi bi-telephone"></i> 联系方式
        </h3>

        <div class="form-row">
          <div class="form-group">
            <label class="form-label">公司电话</label>
            <input 
              type="tel" 
              class="form-control"
              v-model="form.companyPhone"
              placeholder="如：020-12345678"
              maxlength="50"
            >
          </div>

          <div class="form-group">
            <label class="form-label">公司邮箱</label>
            <input 
              type="email" 
              class="form-control"
              v-model="form.companyEmail"
              placeholder="如：contact@company.com"
              maxlength="100"
            >
          </div>
        </div>
      </section>

      <!-- 公司网站 -->
      <section class="section">
        <h3 class="section-title">
          <i class="bi bi-globe"></i> 公司网站
        </h3>

        <div class="form-group">
          <label class="form-label">网站地址</label>
          <input 
            type="url" 
            class="form-control"
            v-model="form.companyWebsite"
            placeholder="如：https://www.example.com"
            maxlength="200"
          >
        </div>
      </section>

      <!-- 公司地址 -->
      <section class="section">
        <h3 class="section-title">
          <i class="bi bi-geo-alt"></i> 公司地址
        </h3>

        <div class="form-group">
          <label class="form-label required">详细地址</label>
          <textarea
            class="form-control"
            v-model="form.companyAddress"
            placeholder="请输入公司详细地址"
            rows="3"
            maxlength="500"
            required
            :style="{ color: form.addressTextColor || '#333333' }"
          ></textarea>
          <small class="form-text">{{ form.companyAddress.length }}/500 字</small>

          <!-- 地址文字颜色选择器 -->
          <div class="color-picker-container">
            <div class="color-preview">
              <span
                class="preview-text"
                :style="{ color: form.addressTextColor || '#333333' }"
              >
                预览效果：公司地址文字颜色
              </span>
            </div>

            <div class="color-controls">
              <div class="quick-colors">
                <div class="color-title">快速选择</div>
                <div class="color-grid">
                  <div
                    v-for="color in presetColors"
                    :key="color"
                    class="color-option"
                    :style="{ backgroundColor: color }"
                    :class="{ active: form.addressTextColor === color }"
                    @click="selectAddressTextColor(color)"
                    :title="color"
                  ></div>
                </div>
              </div>

              <div class="custom-color-section">
                <div class="color-title">自定义颜色</div>
                <div class="custom-color-input">
                  <input
                    type="color"
                    :value="form.addressTextColor || '#333333'"
                    @input="selectAddressTextColor($event.target.value)"
                    class="color-input"
                  >
                  <input
                    type="text"
                    :value="form.addressTextColor || '#333333'"
                    @input="updateAddressTextColorFromHex($event.target.value)"
                    class="color-text-input"
                    placeholder="#000000"
                  >
                  <div
                    class="current-color-box"
                    :style="{ backgroundColor: form.addressTextColor || '#333333' }"
                  ></div>
                </div>
              </div>

              <div class="common-colors">
                <div class="color-title">常用文字颜色</div>
                <div class="common-color-list">
                  <div
                    v-for="textColor in textPresets"
                    :key="textColor.name"
                    class="common-color-item"
                    :class="{ active: form.addressTextColor === textColor.color }"
                    @click="selectAddressTextColor(textColor.color)"
                  >
                    <span
                      class="common-color-preview"
                      :style="{ color: textColor.color }"
                    >
                      {{ textColor.name }}
                    </span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- WhatsApp联系方式 -->
      <section class="section">
        <h3 class="section-title">
          <i class="bi bi-whatsapp"></i> WhatsApp联系方式
        </h3>

        <div class="form-group">
          <label class="form-label">WhatsApp</label>
          <input
            type="tel"
            class="form-control"
            v-model="form.companyPhone"
            placeholder="如：+86 138-0000-0000"
            maxlength="50"
          >
        </div>
      </section>


      <!-- 按钮组 -->
      <div class="button-group">
        <button 
          type="button" 
          class="btn btn-secondary"
          @click="handleReset"
        >
          <i class="bi bi-arrow-counterclockwise"></i>
          重置
        </button>
        <button 
          type="submit" 
          class="btn btn-primary"
          :disabled="submitting"
        >
          <span v-if="submitting" class="spinner-border spinner-border-sm me-2"></span>
          <i v-else class="bi bi-check-circle"></i>
          {{ submitting ? '保存中...' : '保存修改' }}
        </button>
      </div>
    </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { companyAPI, uploadAPI } from '../api'
import { showInfo, showSuccess, showError, showConfirm } from '../utils/dialog'
import fileManager from '../utils/fileManager'
import { filterExistingFiles } from '../utils/fileValidator'

const submitting = ref(false)
const loading = ref(false)

// Logo相关状态
const currentLogo = ref(null)
const logoUploading = ref(false)
const logoProgress = ref({
  fileName: '',
  percent: 0
})
const logoInput = ref(null)

// 预设颜色
const presetColors = ref([
  '#000000', '#ffffff', '#ff0000', '#00ff00', '#0000ff',
  '#ffff00', '#ff00ff', '#00ffff', '#ff6b6b', '#4ecdc4',
  '#45b7d1', '#96ceb4', '#ffeaa7', '#dfe6e9', '#74b9ff',
  '#a29bfe', '#6c5ce7', '#fd79a8', '#fdcb6e', '#e17055',
  '#2d3436', '#636e72', '#b2bec3', '#00b894', '#00cec9',
  '#0984e3', '#6c5ce7', '#a29bfe', '#fd79a8', '#fdcb6e'
])

// 常用文字颜色
const textPresets = ref([
  { name: '默认黑', color: '#333333' },
  { name: '深灰', color: '#666666' },
  { name: '浅灰', color: '#999999' },
  { name: '蓝色', color: '#1976d2' },
  { name: '红色', color: '#dc3545' },
  { name: '绿色', color: '#28a745' },
  { name: '橙色', color: '#ff6a00' },
  { name: '紫色', color: '#6c5ce7' }
])

const form = ref({
  companyName: '',
  companyDescription: '',
  descriptionTextColor: '#333333',
  companyPhone: '',
  companyEmail: '',
  companyWebsite: '',
  companyAddress: '',
  addressTextColor: '#333333'
})

// 备份原始数据用于重置
const originalData = ref(null)

// 加载数据
onMounted(() => {
  loadCompanyInfo()
  loadLogo()
})

// 加载公司信息
const loadCompanyInfo = async () => {
  loading.value = true
  try {
    // 调用API获取公司信息
    const response = await companyAPI.getAdminInfo()
    
    if (response.data) {
      const hasData = Object.values(response.data).some(value => 
        value && typeof value === 'string' && value.trim()
      )
      
      form.value = {
        companyName: response.data.companyName || '',
        companyDescription: response.data.companyDescription || '',
        descriptionTextColor: response.data.descriptionTextColor || '#333333',
        companyPhone: response.data.companyPhone || '',
        companyEmail: response.data.companyEmail || '',
        companyWebsite: response.data.companyWebsite || '',
        companyAddress: response.data.companyAddress || '',
        addressTextColor: response.data.addressTextColor || '#333333'
      }
      originalData.value = JSON.parse(JSON.stringify(form.value))
      
      // 如果所有字段都为空，提示用户
      if (!hasData) {
        showInfo('公司信息为空，请填写公司信息')
      }
    } else {
      // 没有返回数据，视为空信息
      showInfo('未查询到公司信息，请填写公司信息')
    }
  } catch (error) {
    console.error('加载公司信息失败:', error)
    
    // 检查是否是公司信息不存在的业务错误
    const errorMsg = error.message || error.response?.data?.message || ''
    if (error.businessError && errorMsg.includes('公司信息不存在')) {
      showInfo('公司信息不存在，请填写公司信息')
    } else if (error.response?.status === 404) {
      showInfo('公司信息不存在，请填写公司信息')
    } else {
      // 其他错误才提示加载失败
      showError('加载失败：' + (error.response?.data?.message || '请检查网络连接'))
    }
  } finally {
    loading.value = false
  }
}

// 提交表单
const handleSubmit = async () => {
  submitting.value = true

  try {
    // 先检查公司信息是否存在
    const checkResponse = await companyAPI.checkInfo()
    const exists = checkResponse.data?.exists
    
    let response
    if (exists) {
      // 如果存在，调用更新接口
      console.log('公司信息已存在，调用更新接口')
      response = await companyAPI.updateInfo(form.value)
    } else {
      // 如果不存在，调用初始化接口
      console.log('公司信息不存在，调用初始化接口')
      response = await companyAPI.initInfo(form.value)
    }
    
    if (response.code === 200) {
      showSuccess('公司信息已保存')
      originalData.value = JSON.parse(JSON.stringify(form.value))
    } else {
      showError(response.message || '未知错误')
    }
  } catch (error) {
    console.error('保存失败:', error)
    showError(error.response?.data?.message || error.message || '请检查网络连接')
  } finally {
    submitting.value = false
  }
}

// 重置表单
const handleReset = async () => {
  const confirmed = await showConfirm('确定要重置所有修改吗？')
  if (!confirmed) return

  if (originalData.value) {
    form.value = JSON.parse(JSON.stringify(originalData.value))
  }
}

// 加载Logo
const loadLogo = async () => {
  try {
    console.log('🔍 开始加载Logo数据...')
    const response = await uploadAPI.getFilesByCategory('logo')
    console.log('📋 Logo接口响应:', response)

    if (response && response.data) {
      // 处理不同的响应格式，与ImageManagement.vue保持一致
      let files = []
      if (Array.isArray(response.data)) {
        // 直接是数组格式
        files = response.data
        console.log('📁 直接数组格式，文件数量:', files.length)
      } else if (response.data.files && Array.isArray(response.data.files)) {
        // 包装在files字段中的格式
        files = response.data.files
        console.log('📁 包装格式，文件数量:', files.length)
      }

      console.log('📄 原始文件列表:', files)

      const validFiles = await filterExistingFiles(files)
      console.log('✅ 过滤后有效文件:', validFiles)

      // 按创建时间排序，最新的在前面
      if (validFiles.length > 0) {
        validFiles.sort((a, b) => {
          const timeA = new Date(a.createTime || a.updateTime || 0)
          const timeB = new Date(b.createTime || b.updateTime || 0)
          return timeB - timeA
        })

        // 构建logo数据对象，与ImageManagement.vue保持一致
        const logoFile = validFiles[0]
        currentLogo.value = {
          id: logoFile.id,
          originalName: logoFile.originalName,
          fileUrl: logoFile.fileUrl,
          fileSize: logoFile.fileSize,
          createTime: logoFile.createTime,
          updateTime: logoFile.updateTime,
          url: logoFile.fileUrl // 为了模板中的显示方便
        }
        console.log('🎯 设置当前Logo:', currentLogo.value)
      } else {
        currentLogo.value = null
        console.log('❌ 没有找到有效的Logo文件')
      }
    } else {
      currentLogo.value = null
      console.log('❌ 接口响应为空')
    }
  } catch (error) {
    console.error('💥 加载Logo失败:', error)
    currentLogo.value = null
  }
}

// 处理Logo上传
const handleLogoUpload = async (event) => {
  const file = event.target.files[0]
  if (!file) return

  // 验证文件
  if (!file.type.startsWith('image/')) {
    showError('请选择图片文件')
    return
  }

  if (file.size > 5 * 1024 * 1024) {
    showError('图片大小不能超过5MB')
    return
  }

  logoUploading.value = true
  logoProgress.value = {
    fileName: file.name,
    percent: 0
  }

  try {
    const uploadParams = {
      category: 'logo',
      description: '公司官方Logo',
      maxSize: 5 * 1024 * 1024,
      thumbnail: true,
      onProgress: (percent) => {
        logoProgress.value.percent = Math.min(percent, 95)
      }
    }

    const response = await uploadAPI.uploadImage(file, uploadParams)

    if (response.data && response.data.url) {
      logoProgress.value.percent = 100
      logoProgress.value.fileName = '处理完成！'

      // 短暂延迟让用户看到100%进度
      await new Promise(resolve => setTimeout(resolve, 500))

      showSuccess('Logo上传成功')

      // 清除缓存并重新加载
      fileManager.clearCache('logo')
      await loadLogo()

      // 清空文件输入
      if (logoInput.value) {
        logoInput.value.value = ''
      }
    } else {
      throw new Error('上传响应格式错误')
    }
  } catch (error) {
    console.error('Logo上传失败:', error)
    showError('Logo上传失败：' + (error.response?.data?.message || error.message))
  } finally {
    logoUploading.value = false
    logoProgress.value = { fileName: '', percent: 0 }
  }
}

// 处理Logo删除
const handleLogoDelete = async () => {
  const confirmed = await showConfirm('确定要删除公司Logo吗？')
  if (!confirmed) return

  if (!currentLogo.value?.url) {
    showError('未找到要删除的Logo')
    return
  }

  try {
    const response = await uploadAPI.deleteFile(currentLogo.value.url)

    if (response && response.success && response.data && response.data.deleted) {
      showSuccess('Logo删除成功')

      // 清除缓存并重新加载
      fileManager.clearCache('logo')
      await loadLogo()
    } else {
      throw new Error(response?.message || '删除失败')
    }
  } catch (error) {
    console.error('Logo删除失败:', error)
    showError('Logo删除失败：' + (error.response?.data?.message || error.message))
  }
}

// 格式化文件大小
const formatFileSize = (bytes) => {
  if (!bytes) return '未知大小'
  const sizes = ['B', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(1024))
  return Math.round(bytes / Math.pow(1024, i) * 100) / 100 + ' ' + sizes[i]
}

// 格式化时间
const formatTime = (time) => {
  if (!time) return '未知时间'
  const date = new Date(time)
  return date.toLocaleString('zh-CN')
}

// 选择公司简介文字颜色
const selectDescriptionTextColor = (color) => {
  form.value.descriptionTextColor = color
}

// 从十六进制输入更新公司简介颜色
const updateDescriptionTextColorFromHex = (hexValue) => {
  if (/^#[0-9A-F]{6}$/i.test(hexValue) || /^#[0-9A-F]{3}$/i.test(hexValue)) {
    form.value.descriptionTextColor = hexValue
  }
}

// 选择公司地址文字颜色
const selectAddressTextColor = (color) => {
  form.value.addressTextColor = color
}

// 从十六进制输入更新公司地址颜色
const updateAddressTextColorFromHex = (hexValue) => {
  if (/^#[0-9A-F]{6}$/i.test(hexValue) || /^#[0-9A-F]{3}$/i.test(hexValue)) {
    form.value.addressTextColor = hexValue
  }
}
</script>

<style scoped>
.company-info {
  padding: 20px;
  max-width: 1600px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  padding-bottom: 15px;
  border-bottom: 2px solid #e0e0e0;
}

.page-header h2 {
  margin: 0;
  color: #2c3e50;
  font-size: 28px;
}

.page-header h2 i {
  margin-right: 10px;
  color: #1976d2;
}

/* 左右对照布局 */
.content-layout {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  align-items: start;
}

/* 左侧当前信息 */
.current-info {
  background: white;
  border-radius: 12px;
  padding: 25px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  position: sticky;
  top: 20px;
  max-height: calc(100vh - 100px);
  overflow-y: auto;
}

.info-header {
  margin-bottom: 20px;
  padding-bottom: 12px;
  border-bottom: 2px solid #e8f4f8;
}

.info-header h3 {
  margin: 0;
  font-size: 20px;
  color: #2c3e50;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 8px;
}

.info-header i {
  color: #1976d2;
}

.info-section {
  margin-bottom: 25px;
}

.info-section h4 {
  font-size: 16px;
  color: #495057;
  margin-bottom: 15px;
  font-weight: 600;
  padding-left: 10px;
  border-left: 3px solid #1976d2;
  display: flex;
  align-items: center;
  gap: 8px;
}

.info-section h4 i {
  color: #1976d2;
  font-size: 14px;
}

.info-item {
  padding: 10px 0;
  border-bottom: 1px solid #f0f0f0;
}

.info-item:last-child {
  border-bottom: none;
}

.info-item label {
  display: inline-block;
  font-weight: 500;
  color: #666;
  margin-right: 10px;
  min-width: 80px;
}

.info-item span {
  color: #2c3e50;
}

.info-item .text-content {
  display: block;
  margin-top: 5px;
  line-height: 1.6;
  white-space: pre-wrap;
}

/* Logo展示样式 */
.info-item.logo-info-item {
  border: none;
  padding: 0;
}

.current-logo-display {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #e0e0e0;
}

.current-logo-img {
  max-width: 120px;
  max-height: 80px;
  border-radius: 6px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.1);
  object-fit: contain;
}

.logo-details {
  flex: 1;
}

.logo-details p {
  margin: 0 0 5px 0;
  font-size: 13px;
}

.logo-filename {
  font-weight: 600;
  color: #2c3e50;
}

.logo-meta {
  color: #666;
  font-size: 12px;
}

.no-logo-placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  padding: 30px;
  background: #f8f9fa;
  border-radius: 8px;
  border: 2px dashed #d0d7de;
  color: #999;
  font-size: 14px;
}

.no-logo-placeholder i {
  font-size: 24px;
}

/* 右侧编辑表单 */
.edit-form {
  background: white;
  border-radius: 12px;
  padding: 25px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.form-header {
  margin-bottom: 20px;
  padding-bottom: 12px;
  border-bottom: 2px solid #e8f4f8;
}

.form-header h3 {
  margin: 0;
  font-size: 20px;
  color: #2c3e50;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 8px;
}

.form-header i {
  color: #1976d2;
}

.section {
  background: white;
  border-radius: 12px;
  padding: 25px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.section-title {
  font-size: 18px;
  color: #2c3e50;
  margin-bottom: 20px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 8px;
  padding-bottom: 12px;
  border-bottom: 2px solid #e8f4f8;
}

.section-title i {
  color: #1976d2;
  font-size: 20px;
}

.form-group {
  margin-bottom: 20px;
}

.form-row {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

.form-label {
  font-weight: 500;
  color: #495057;
  margin-bottom: 8px;
  display: block;
  font-size: 14px;
}

.form-label.required::after {
  content: ' *';
  color: #dc3545;
}

.form-control {
  width: 100%;
  padding: 10px 15px;
  border: 2px solid #e0e0e0;
  border-radius: 8px;
  font-size: 14px;
  transition: all 0.3s;
  font-family: inherit;
}

.form-control:focus {
  outline: none;
  border-color: #1976d2;
  box-shadow: 0 0 0 0.2rem rgba(25, 118, 210, 0.1);
}

textarea.form-control {
  resize: vertical;
  min-height: 100px;
}

.form-text {
  font-size: 12px;
  color: #6c757d;
  margin-top: 5px;
  display: block;
}

.button-group {
  display: flex;
  gap: 15px;
  justify-content: center;
  padding: 30px 0;
}

.btn {
  padding: 12px 30px;
  border-radius: 8px;
  font-size: 15px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
  border: none;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  min-width: 140px;
  justify-content: center;
}

.btn-secondary {
  background: #6c757d;
  color: white;
}

.btn-secondary:hover {
  background: #5a6268;
  transform: translateY(-2px);
}

.btn-primary {
  background: #1976d2;
  color: white;
}

.btn-primary:hover:not(:disabled) {
  background: #1565c0;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(25, 118, 210, 0.3);
}

.btn-primary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

@media (max-width: 1200px) {
  .content-layout {
    grid-template-columns: 1fr;
  }

  .current-info {
    position: static;
    max-height: none;
    margin-bottom: 20px;
  }
}

@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    gap: 15px;
    align-items: flex-start;
  }

  .form-row {
    grid-template-columns: 1fr;
  }

  .button-group {
    flex-direction: column;
  }

  .btn {
    width: 100%;
  }
}

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  color: #6c757d;
}

.visually-hidden {
  position: absolute;
  width: 1px;
  height: 1px;
  padding: 0;
  margin: -1px;
  overflow: hidden;
  clip: rect(0, 0, 0, 0);
  white-space: nowrap;
  border-width: 0;
}

/* Logo管理样式 */

.logo-upload {
  display: flex;
  align-items: flex-start;
  gap: 20px;
  margin-bottom: 20px;
}

.upload-area {
  flex: 1;
}

.upload-area.has-logo {
  max-width: 400px;
}

.upload-label {
  display: block;
  padding: 30px;
  background: white;
  border: 2px dashed #d0d7de;
  border-radius: 8px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
}

.upload-label:hover {
  border-color: #1976d2;
  background: #f8f9ff;
}

.upload-icon {
  margin-bottom: 15px;
}

.upload-icon i {
  font-size: 48px;
  color: #6c757d;
}

.upload-text p {
  margin: 0;
}

.upload-title {
  font-size: 16px;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 8px;
}

.upload-desc {
  font-size: 14px;
  color: #666;
}

.delete-btn {
  background: #dc3545;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  gap: 6px;
}

.delete-btn:hover:not(:disabled) {
  background: #c82333;
  transform: translateY(-2px);
}

.delete-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.upload-progress {
  margin-top: 20px;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
  border: 1px solid #e0e0e0;
}

.progress-info {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 15px;
  font-size: 14px;
  color: #666;
}

.progress-bar {
  width: 100%;
  height: 8px;
  background: #e9ecef;
  border-radius: 4px;
  overflow: hidden;
  margin-bottom: 10px;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #1976d2, #42a5f5);
  transition: width 0.3s;
  border-radius: 4px;
}

.progress-percent {
  text-align: center;
  font-size: 14px;
  color: #1976d2;
  font-weight: 600;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .logo-preview {
    flex-direction: column;
    text-align: center;
  }

  .current-logo-display {
    flex-direction: column;
    text-align: center;
    gap: 10px;
  }

  .current-logo-img {
    max-width: 150px;
    max-height: 100px;
  }

  .no-logo-placeholder {
    flex-direction: column;
    gap: 8px;
    padding: 20px;
  }

  .logo-upload {
    flex-direction: column;
  }

  .upload-area.has-logo {
    max-width: 100%;
  }

  .upload-label {
    padding: 20px;
  }

  .upload-icon i {
    font-size: 36px;
  }

  .upload-title {
    font-size: 14px;
  }

  .upload-desc {
    font-size: 12px;
  }
}

/* 文字颜色选择器样式 */
.color-picker-container {
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  padding: 16px;
  background: #fafafa;
  margin-top: 12px;
}

.color-preview {
  margin-bottom: 16px;
  padding: 12px;
  background: white;
  border-radius: 6px;
  border: 1px solid #e0e0e0;
}

.preview-text {
  font-size: 14px;
  font-weight: 500;
}

.color-controls {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
  margin-bottom: 16px;
}

.color-title {
  font-size: 13px;
  font-weight: 600;
  color: #495057;
  margin-bottom: 8px;
}

/* 快速颜色选择 */
.quick-colors .color-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(28px, 1fr));
  gap: 6px;
}

.color-option {
  width: 28px;
  height: 28px;
  border: 2px solid transparent;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.color-option:hover {
  transform: scale(1.1);
  border-color: #1976d2;
}

.color-option.active {
  border-color: #1976d2;
  box-shadow: 0 0 0 2px rgba(25, 118, 210, 0.3);
}

/* 自定义颜色 */
.custom-color-input {
  display: flex;
  align-items: center;
  gap: 8px;
}

.color-input {
  width: 36px;
  height: 32px;
  border: 2px solid #e0e0e0;
  border-radius: 4px;
  cursor: pointer;
}

.color-input:hover {
  border-color: #1976d2;
}

.color-text-input {
  flex: 1;
  padding: 6px;
  border: 1px solid #e0e0e0;
  border-radius: 4px;
  font-size: 12px;
  font-family: monospace;
}

.color-text-input:focus {
  outline: none;
  border-color: #1976d2;
}

.current-color-box {
  width: 32px;
  height: 32px;
  border: 2px solid #e0e0e0;
  border-radius: 4px;
}

/* 常用文字颜色 */
.common-colors {
  grid-column: span 2;
}

.common-color-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(100px, 1fr));
  gap: 6px;
}

.common-color-item {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 6px;
  background: white;
  border: 2px solid #e0e0e0;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.common-color-item:hover {
  border-color: #1976d2;
  transform: translateY(-1px);
}

.common-color-item.active {
  border-color: #1976d2;
  background: #f3f8ff;
}

.common-color-preview {
  font-size: 11px;
  font-weight: 600;
}
</style>
