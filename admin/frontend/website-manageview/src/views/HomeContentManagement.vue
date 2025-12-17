<template>
  <div class="home-content-management">
    <div class="page-header">
      <h2><i class="bi bi-house-fill"></i> 首页内容管理</h2>
      <button class="btn btn-outline-primary" @click="$router.push('/')">
        <i class="bi bi-arrow-left"></i> 返回首页
      </button>
    </div>

    <!-- 加载中提示 -->
    <div v-if="loading" class="loading-container">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">加载中...</span>
      </div>
      <p class="mt-3">正在加载首页内容...</p>
    </div>

    <!-- 内容列表 -->
    <div v-else class="content-list">
      <div 
        v-for="content in contentList" 
        :key="content.type"
        class="content-card"
      >
        <!-- 左侧：当前内容展示 -->
        <div class="current-content">
          <div class="content-header">
            <h3>
              <i :class="content.icon"></i>
              {{ content.title }}
            </h3>
          </div>

          <div class="content-preview">
            <label>当前内容：</label>
            <p v-if="content.data?.content" class="preview-text">
              {{ content.data.content }}
            </p>
            <p v-else class="empty-text">暂无内容</p>
          </div>

          <div class="content-meta" v-if="content.data">
            <span class="meta-item">
              <i class="bi bi-clock"></i>
              更新时间：{{ formatDate(content.data.updateTime) }}
            </span>
          </div>
        </div>

        <!-- 右侧：编辑表单 -->
        <div class="edit-form">
          <div class="form-header">
            <h3><i class="bi bi-pencil-square"></i> 编辑内容</h3>
            <p class="form-hint">{{ content.description }}</p>
          </div>

          <form @submit.prevent="handleSave(content)">
            <div class="form-group">
              <label class="form-label required">内容</label>
              <textarea
                class="form-control"
                v-model="content.editContent"
                placeholder="请输入内容..."
                rows="10"
                maxlength="5000"
                required
                :style="{ color: content.textColor || '#333333' }"
              ></textarea>
              <small class="form-text">{{ (content.editContent || '').length }}/5000 字</small>
            </div>

            <!-- 文字颜色选择器 -->
            <div class="form-group">
              <label class="form-label">文字颜色</label>
              <div class="color-picker-container">
                <!-- 当前颜色预览 -->
                <div class="color-preview">
                  <span
                    class="preview-text"
                    :style="{ color: content.textColor || '#333333' }"
                  >
                    预览效果：这是文字颜色示例
                  </span>
                </div>

                <!-- 颜色选择控制 -->
                <div class="color-controls">
                  <!-- 快速颜色选择 -->
                  <div class="quick-colors">
                    <div class="color-title">快速选择</div>
                    <div class="color-grid">
                      <div
                        v-for="color in presetColors"
                        :key="color"
                        class="color-option"
                        :style="{ backgroundColor: color }"
                        :class="{ active: content.textColor === color }"
                        @click="selectTextColor(content, color)"
                        :title="color"
                      ></div>
                    </div>
                  </div>

                  <!-- 自定义颜色 -->
                  <div class="custom-color-section">
                    <div class="color-title">自定义颜色</div>
                    <div class="custom-color-input">
                      <input
                        type="color"
                        :value="content.textColor || '#333333'"
                        @input="selectTextColor(content, $event.target.value)"
                        class="color-input"
                      >
                      <input
                        type="text"
                        :value="content.textColor || '#333333'"
                        @input="updateTextColorFromHex(content, $event.target.value)"
                        class="color-text-input"
                        placeholder="#000000"
                      >
                      <div
                        class="current-color-box"
                        :style="{ backgroundColor: content.textColor || '#333333' }"
                      ></div>
                    </div>
                  </div>

                  <!-- 常用文字颜色 -->
                  <div class="common-colors">
                    <div class="color-title">常用文字颜色</div>
                    <div class="common-color-list">
                      <div
                        v-for="textColor in textPresets"
                        :key="textColor.name"
                        class="common-color-item"
                        :class="{ active: content.textColor === textColor.color }"
                        @click="selectTextColor(content, textColor.color)"
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

            <div class="button-group">
              <button
                type="button"
                class="btn btn-secondary"
                @click="handleReset(content)"
              >
                <i class="bi bi-arrow-counterclockwise"></i>
                重置
              </button>
              <button
                type="submit"
                class="btn btn-primary"
                :disabled="content.saving"
              >
                <span v-if="content.saving" class="spinner-border spinner-border-sm me-2"></span>
                <i v-else class="bi bi-check-circle"></i>
                {{ content.saving ? '保存中...' : '保存' }}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>

    <!-- 图标管理区 -->
    <div class="icon-management-section">
      <div class="section-header">
        <h3 class="section-title">
          <i class="bi bi-icons"></i> 功能图标管理
        </h3>
        <p class="section-desc">
          <i class="bi bi-info-circle"></i>
          网站功能区域展示图标，支持 JPG/PNG/GIF/SVG 格式，最大 10MB
        </p>
      </div>

      <div class="icons-grid">
        <IconUploadCard
          v-for="iconData in iconImages"
          :key="iconData.type"
          :iconData="iconData"
          @upload="handleIconUpload"
          @delete="handleIconDelete"
          @preview="handlePreviewImage"
        />
      </div>
    </div>

    <!-- 背景图片管理区 -->
    <div class="background-management-section">
      <div class="section-header">
        <h3 class="section-title">
          <i class="bi bi-image"></i> 背景图片管理
        </h3>
        <p class="section-desc">
          <i class="bi bi-info-circle"></i>
          首页展示的背景图片，支持 JPG/PNG/GIF 格式，最大 10MB
        </p>
      </div>

      <div class="background-card">
        <IconUploadCard
          :iconData="backgroundImage"
          @upload="handleBackgroundUpload"
          @delete="handleBackgroundDelete"
          @preview="handlePreviewImage"
        />
      </div>
    </div>

    <!-- 上传进度条组件 -->
    <UploadProgress
      :show="uploadProgress.show"
      :percent="uploadProgress.percent"
      :fileName="uploadProgress.fileName"
      :cancellable="uploadProgress.cancellable"
      @cancel="handleUploadCancel"
    />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import http from '../api/http'
import IconUploadCard from '@/components/IconUploadCard.vue'
import UploadProgress from '@/components/UploadProgress.vue'
import { uploadAPI } from '@/api'
import fileManager from '@/utils/fileManager'
import { handleFileOperationError, handleFileOperationSuccess, defaultShowMessage } from '@/utils/errorHandler'
import { filterExistingFiles } from '@/utils/fileValidator'
import Swal from 'sweetalert2'

const loading = ref(false)

// 图标数据
const iconImages = ref([
  {
    type: 'icon-company',
    title: 'COMPANY图标',
    position: '位置1',
    image: null,  // 存储单个图标对象
    size: '',
    maxSize: 10,
    description: '公司展示区域图标'
  },
  {
    type: 'icon-team',
    title: 'TEAM图标',
    position: '位置2',
    image: null,  // 存储单个图标对象
    size: '',
    maxSize: 10,
    description: '团队展示区域图标'
  },
  {
    type: 'icon-best-prices',
    title: 'BEST PRICES图标',
    position: '位置3',
    image: null,  // 存储单个图标对象
    size: '',
    maxSize: 10,
    description: '最优价格展示区域图标'
  },
  {
    type: 'icon-quality',
    title: 'QUALITY图标',
    position: '位置4',
    image: null,  // 存储单个图标对象
    size: '',
    maxSize: 10,
    description: '质量控制展示区域图标'
  }
])

// 背景图片数据
const backgroundImage = ref({
  type: 'background-image',
  title: '首页背景图',
  image: null,  // 存储背景图片对象
  size: '',
  maxSize: 10,  // 10MB
  description: '首页展示的背景图片'
})

// 上传进度状态
const uploadProgress = ref({
  show: false,        // 是否显示进度条
  percent: 0,         // 上传进度百分比
  fileName: '',       // 上传文件名
  cancellable: false  // 是否可取消
})

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

// 3个内容块的配置
const contentList = ref([
  {
    type: 'home-company-info',
    title: '公司基本信息',
    icon: 'bi bi-building',
    description: '显示在首页顶部的公司基本信息，包括公司名称、类型、年限、地址和主营品类',
    data: null,
    editContent: '',
    textColor: '#333333',
    saving: false
  },
  {
    type: 'home-advantage',
    title: 'OUR ADVANTAGE（我们的优势）',
    icon: 'bi bi-star',
    description: '展示公司的核心优势和特点',
    data: null,
    editContent: '',
    textColor: '#333333',
    saving: false
  },
  {
    type: 'home-profile',
    title: 'COMPANY PROFILE（公司简介）',
    icon: 'bi bi-file-text',
    description: '详细的公司介绍，展示公司的历史、产品、质量控制和经营理念',
    data: null,
    editContent: '',
    textColor: '#333333',
    saving: false
  }
])

// 加载所有内容
onMounted(() => {
  loadAllContent()
  loadIconImages()
  loadBackgroundImage()
})

// 加载所有内容
const loadAllContent = async () => {
  loading.value = true
  try {
    // 依次获取3个内容块
    for (const content of contentList.value) {
      await loadContentByType(content)
    }
  } catch (error) {
    console.error('加载首页内容失败:', error)
    Swal.fire({
      title: '加载失败',
      text: error.message || '加载首页内容失败',
      icon: 'error',
      confirmButtonText: '确定'
    })
  } finally {
    loading.value = false
  }
}

// 根据类型加载单个内容
const loadContentByType = async (content) => {
  try {
    const response = await http.get(`/api/rich-content/type/${content.type}`)

    if (response.data && response.data.length > 0) {
      // 取第一条记录
      content.data = response.data[0]
      content.editContent = response.data[0].content || ''
      // 加载文字颜色，如果没有则使用默认颜色
      content.textColor = response.data[0].textColor || '#333333'
    } else {
      content.data = null
      content.editContent = ''
      content.textColor = '#333333'
    }
  } catch (error) {
    console.error(`加载 ${content.type} 失败:`, error)
    content.data = null
    content.editContent = ''
    content.textColor = '#333333'
  }
}

// 保存内容
const handleSave = async (content) => {
  content.saving = true

  try {
    const requestData = {
      title: content.title,
      contentType: content.type,
      content: content.editContent,
      textColor: content.textColor || '#333333',
      status: 1 // 已发布状态
    }

    let response
    if (content.data?.id) {
      // 更新已有内容
      response = await http.put(`/api/rich-content/${content.data.id}`, requestData)
    } else {
      // 创建新内容
      response = await http.post('/api/rich-content', requestData)
    }

    if (response.code === 200) {
      Swal.fire({
        title: '保存成功',
        icon: 'success',
        timer: 1500,
        showConfirmButton: false
      })

      // 重新加载该内容
      await loadContentByType(content)
    } else {
      throw new Error(response.message || '保存失败')
    }
  } catch (error) {
    console.error('保存失败:', error)
    Swal.fire({
      title: '保存失败',
      text: error.message || '保存内容失败',
      icon: 'error',
      confirmButtonText: '确定'
    })
  } finally {
    content.saving = false
  }
}

// 重置内容
const handleReset = async (content) => {
  const result = await Swal.fire({
    title: '确定要重置吗？',
    text: '将恢复到上次保存的内容',
    icon: 'question',
    showCancelButton: true,
    confirmButtonText: '确定',
    cancelButtonText: '取消'
  })

  if (result.isConfirmed) {
    content.editContent = content.data?.content || ''
    content.textColor = content.data?.textColor || '#333333'
  }
}

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return '未知'
  const date = new Date(dateString)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 加载图标数据
const loadIconImages = async () => {
  try {
    for (const iconData of iconImages.value) {
      // 调用后端接口查询该分类的图片
      const response = await uploadAPI.getFilesByCategory(iconData.type)

      if (response && response.data) {
        // 处理不同的响应格式
        let files = []
        if (Array.isArray(response.data)) {
          // 直接是数组格式
          files = response.data
        } else if (response.data.files && Array.isArray(response.data.files)) {
          // 包装在files字段中的格式
          files = response.data.files
        }

        // 使用文件验证工具过滤存在的文件
        const validFiles = await filterExistingFiles(files)

        // 图标只取第一个文件（每个位置只有一个图标）
        if (validFiles.length > 0) {
          const file = validFiles[0]
          iconData.image = {
            id: file.id,
            url: file.fileUrl,
            updateTime: file.updateTime,
            originalName: file.originalName
          }
        } else {
          iconData.image = null
        }
      } else {
        // 如果没有数据，清空图标
        iconData.image = null
      }
    }
    console.log('图标数据加载成功')
  } catch (error) {
    console.error('加载图标失败:', error)
    // 发生错误时清空所有图标，避免显示已删除的图标
    iconImages.value.forEach(iconData => {
      iconData.image = null
    })
  }
}

// 加载背景图片数据
const loadBackgroundImage = async () => {
  try {
    const response = await uploadAPI.getFilesByCategory(backgroundImage.value.type)

    if (response && response.data) {
      // 处理不同的响应格式
      let files = []
      if (Array.isArray(response.data)) {
        files = response.data
      } else if (response.data.files && Array.isArray(response.data.files)) {
        files = response.data.files
      }

      // 使用文件验证工具过滤存在的文件
      const validFiles = await filterExistingFiles(files)

      // 背景图片只取第一个文件
      if (validFiles.length > 0) {
        const file = validFiles[0]
        backgroundImage.value.image = {
          id: file.id,
          url: file.fileUrl,
          updateTime: file.updateTime,
          originalName: file.originalName
        }
      } else {
        backgroundImage.value.image = null
      }
    } else {
      backgroundImage.value.image = null
    }
    console.log('背景图片数据加载成功')
  } catch (error) {
    console.error('加载背景图片失败:', error)
    backgroundImage.value.image = null
  }
}

// 处理图标上传
const handleIconUpload = async (type, file) => {
  try {
    // 查找对应的图标信息
    const iconInfo = iconImages.value.find(icon => icon.type === type)

    if (!iconInfo) {
      Swal.fire({
        title: '错误',
        text: '未找到对应的图标位置',
        icon: 'error',
        confirmButtonText: '确定',
        confirmButtonColor: '#1976d2'
      })
      return
    }

    // 显示上传进度条
    uploadProgress.value.show = true
    uploadProgress.value.percent = 0
    uploadProgress.value.fileName = file.name
    uploadProgress.value.cancellable = true

    // 准备上传参数
    const uploadParams = {
      category: type,                           // 分类
      description: iconInfo?.description,       // 图标描述
      width: 128,                               // 目标宽度
      height: 128,                              // 目标高度
      maxSize: iconInfo?.maxSize * 1024 * 1024, // 最大文件大小
      thumbnail: false,                         // 图标不需要缩略图
      onProgress: (percent, progressEvent) => { // 进度回调
        // 只更新到95%，保留最后5%给后端处理
        uploadProgress.value.percent = Math.min(percent, 95)

        // 如果有详细信息，更新文件名显示
        if (progressEvent && progressEvent.loaded && progressEvent.total) {
          const loadedMB = (progressEvent.loaded / 1024 / 1024).toFixed(1)
          const totalMB = (progressEvent.total / 1024 / 1024).toFixed(1)
          console.log(`上传进度：${percent}% (${loadedMB}/${totalMB}MB)`)
        }
      }
    }

    // 使用增强图片上传API，带进度回调
    const response = await uploadAPI.uploadImage(file, uploadParams)

    // API调用完成，显示后端处理中状态
    uploadProgress.value.percent = 96
    uploadProgress.value.fileName = '正在处理图标...'

    if (response.data && response.data.url) {
      // 后端处理完成，进度100%
      uploadProgress.value.percent = 100
      uploadProgress.value.fileName = '处理完成！'

      // 短暂延迟让用户看到100%进度
      await new Promise(resolve => setTimeout(resolve, 500))

      // 隐藏进度条
      uploadProgress.value.show = false

      // 显示上传成功消息
      let successMessage = '图标上传成功'
      if (response.data.compressed) {
        successMessage += `，压缩率：${response.data.compressionRatio}`
      }

      Swal.fire({
        title: '上传成功',
        text: successMessage,
        icon: 'success',
        confirmButtonText: '确定',
        confirmButtonColor: '#1976d2',
        timer: 2000
      })

      // 清除相关缓存并重新加载（异步执行，不阻塞用户界面）
      setTimeout(async () => {
        fileManager.clearCache(type)
        await loadIconImages()
      }, 100)
    } else {
      throw new Error('上传响应格式错误，未获取到文件URL')
    }
  } catch (error) {
    // 隐藏进度条
    uploadProgress.value.show = false

    // 检查是否是用户取消的错误
    if (error.name === 'CanceledError' || error.message?.includes('取消')) {
      Swal.fire({
        title: '已取消',
        text: '图标上传已取消',
        icon: 'info',
        confirmButtonText: '确定',
        confirmButtonColor: '#1976d2',
        timer: 1500
      })
      return
    }

    console.error('上传失败:', error)
    const errorMsg = error.response?.data?.message || error.message || '上传失败'
    Swal.fire({
      title: '上传失败',
      text: errorMsg,
      icon: 'error',
      confirmButtonText: '确定',
      confirmButtonColor: '#1976d2'
    })
  }
}

// 处理图标删除
const handleIconDelete = async (type, imageId) => {
  const iconInfo = iconImages.value.find(icon => icon.type === type)

  if (!iconInfo || !iconInfo.image) {
    Swal.fire({
      title: '温馨提示',
      text: '该位置暂无图标',
      icon: 'info',
      confirmButtonText: '确定',
      confirmButtonColor: '#1976d2'
    })
    return
  }

  const result = await Swal.fire({
    title: '温馨提示',
    text: `确定要删除 ${iconInfo.title} 吗？`,
    icon: 'question',
    showCancelButton: true,
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    confirmButtonColor: '#1976d2',
    cancelButtonColor: '#6c757d'
  })

  if (!result.isConfirmed) return

  try {
    // 调用删除API（删除文件）
    const response = await uploadAPI.deleteFile(iconInfo.image.url)

    // 检查删除结果
    if (response && response.success && response.data && response.data.deleted) {
      // 清除相关缓存
      fileManager.clearCache(type)

      // 立即刷新图标列表
      await loadIconImages()

      // 显示成功消息
      handleFileOperationSuccess('delete', response.data, defaultShowMessage)
    } else {
      throw new Error(response?.message || '删除失败')
    }
  } catch (error) {
    // 使用统一错误处理
    handleFileOperationError(error, 'delete', iconInfo.image, defaultShowMessage)

    // 即使删除失败，也尝试重新加载数据以保持界面同步
    try {
      await loadIconImages()
    } catch (loadError) {
      console.error('重新加载数据失败:', loadError)
    }
  }
}

// 处理背景图片上传
const handleBackgroundUpload = async (type, file) => {
  try {
    // 显示上传进度条
    uploadProgress.value.show = true
    uploadProgress.value.percent = 0
    uploadProgress.value.fileName = file.name
    uploadProgress.value.cancellable = true

    // 准备上传参数
    const uploadParams = {
      category: type,
      description: backgroundImage.value.description,
      maxSize: backgroundImage.value.maxSize * 1024 * 1024,
      thumbnail: false,
      onProgress: (percent, progressEvent) => {
        uploadProgress.value.percent = Math.min(percent, 95)

        if (progressEvent && progressEvent.loaded && progressEvent.total) {
          const loadedMB = (progressEvent.loaded / 1024 / 1024).toFixed(1)
          const totalMB = (progressEvent.total / 1024 / 1024).toFixed(1)
          console.log(`上传进度：${percent}% (${loadedMB}/${totalMB}MB)`)
        }
      }
    }

    // 使用增强图片上传API，带进度回调
    const response = await uploadAPI.uploadImage(file, uploadParams)

    // API调用完成，显示后端处理中状态
    uploadProgress.value.percent = 96
    uploadProgress.value.fileName = '正在处理背景图片...'

    if (response.data && response.data.url) {
      uploadProgress.value.percent = 100
      uploadProgress.value.fileName = '处理完成！'

      await new Promise(resolve => setTimeout(resolve, 500))
      uploadProgress.value.show = false

      let successMessage = '背景图片上传成功'
      if (response.data.compressed) {
        successMessage += `，压缩率：${response.data.compressionRatio}`
      }

      Swal.fire({
        title: '上传成功',
        text: successMessage,
        icon: 'success',
        confirmButtonText: '确定',
        confirmButtonColor: '#1976d2',
        timer: 2000
      })

      setTimeout(async () => {
        fileManager.clearCache(type)
        await loadBackgroundImage()
      }, 100)
    } else {
      throw new Error('上传响应格式错误，未获取到文件URL')
    }
  } catch (error) {
    uploadProgress.value.show = false

    if (error.name === 'CanceledError' || error.message?.includes('取消')) {
      Swal.fire({
        title: '已取消',
        text: '背景图片上传已取消',
        icon: 'info',
        confirmButtonText: '确定',
        confirmButtonColor: '#1976d2',
        timer: 1500
      })
      return
    }

    console.error('上传失败:', error)
    const errorMsg = error.response?.data?.message || error.message || '上传失败'
    Swal.fire({
      title: '上传失败',
      text: errorMsg,
      icon: 'error',
      confirmButtonText: '确定',
      confirmButtonColor: '#1976d2'
    })
  }
}

// 处理背景图片删除
const handleBackgroundDelete = async () => {
  if (!backgroundImage.value.image) {
    Swal.fire({
      title: '温馨提示',
      text: '暂无背景图片',
      icon: 'info',
      confirmButtonText: '确定',
      confirmButtonColor: '#1976d2'
    })
    return
  }

  const result = await Swal.fire({
    title: '温馨提示',
    text: '确定要删除背景图片吗？',
    icon: 'question',
    showCancelButton: true,
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    confirmButtonColor: '#1976d2',
    cancelButtonColor: '#6c757d'
  })

  if (!result.isConfirmed) return

  try {
    const response = await uploadAPI.deleteFile(backgroundImage.value.image.url)

    if (response && response.success && response.data && response.data.deleted) {
      fileManager.clearCache(backgroundImage.value.type)
      await loadBackgroundImage()
      handleFileOperationSuccess('delete', response.data, defaultShowMessage)
    } else {
      throw new Error(response?.message || '删除失败')
    }
  } catch (error) {
    handleFileOperationError(error, 'delete', backgroundImage.value.image, defaultShowMessage)

    try {
      await loadBackgroundImage()
    } catch (loadError) {
      console.error('重新加载数据失败:', loadError)
    }
  }
}

// 处理图片预览
const handlePreviewImage = (imageUrl) => {
  // 创建全屏预览
  const preview = document.createElement('div')
  preview.className = 'fullscreen-preview'
  preview.innerHTML = `
    <div class="preview-content" onclick="event.stopPropagation()">
      <img src="${imageUrl}" alt="图标预览">
      <button class="close-btn" onclick="this.closest('.fullscreen-preview').remove()">
        <i class="bi bi-x-lg"></i>
      </button>
    </div>
  `
  preview.onclick = () => preview.remove()
  document.body.appendChild(preview)
}

// 处理上传取消
const handleUploadCancel = () => {
  // 这里可以添加取消上传的逻辑
  uploadProgress.value.show = false
  uploadProgress.value.percent = 0
  uploadProgress.value.fileName = ''
}

// 选择文字颜色
const selectTextColor = (content, color) => {
  content.textColor = color
}

// 从十六进制输入更新颜色
const updateTextColorFromHex = (content, hexValue) => {
  // 验证十六进制颜色格式
  if (/^#[0-9A-F]{6}$/i.test(hexValue) || /^#[0-9A-F]{3}$/i.test(hexValue)) {
    content.textColor = hexValue
  }
}
</script>

<style scoped>
.home-content-management {
  padding: 24px;
  max-width: 1600px;
  margin: 0 auto;
}

/* 页面头部 */
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32px;
  padding-bottom: 16px;
  border-bottom: 2px solid #e9ecef;
}

.page-header h2 {
  margin: 0;
  font-size: 28px;
  font-weight: 600;
  color: #2c3e50;
  display: flex;
  align-items: center;
  gap: 12px;
}

.page-header h2 i {
  color: #1976d2;
}

/* 加载中 */
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 20px;
}

.loading-container p {
  color: #6c757d;
  margin-top: 16px;
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

/* 内容列表 */
.content-list {
  display: flex;
  flex-direction: column;
  gap: 32px;
}

/* 内容卡片 - 左右布局 */
.content-card {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24px;
  background: #f8f9fa;
  border-radius: 12px;
  padding: 24px;
}

/* 左侧：当前内容展示 */
.current-content {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.content-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  background: linear-gradient(135deg, #60a5fa 0%, #3b82f6 100%);
  color: white;
  border-radius: 8px;
  margin-bottom: 20px;
}

.content-header h3 {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 10px;
}

.status-badge {
  padding: 4px 12px;
  border-radius: 16px;
  font-size: 13px;
  font-weight: 500;
}

.status-published {
  background: rgba(255, 255, 255, 0.3);
  color: white;
}

.status-empty {
  background: rgba(255, 193, 7, 0.9);
  color: #000;
}

.content-preview label {
  font-weight: 600;
  color: #495057;
  margin-bottom: 12px;
  display: block;
}

.preview-text {
  background: #f8f9fa;
  padding: 16px;
  border-radius: 8px;
  color: #495057;
  line-height: 1.6;
  margin: 0;
  white-space: pre-wrap;
  word-wrap: break-word;
  max-height: 400px;
  overflow-y: auto;
}

.empty-text {
  color: #6c757d;
  font-style: italic;
  margin: 0;
}

.content-meta {
  display: flex;
  gap: 24px;
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid #e9ecef;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #6c757d;
  font-size: 14px;
}

.meta-item i {
  color: #1976d2;
}

/* 右侧：编辑表单 */
.edit-form {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.form-header {
  margin-bottom: 20px;
  padding-bottom: 12px;
  border-bottom: 2px solid #e8f4f8;
}

.form-header h3 {
  margin: 0 0 8px 0;
  font-size: 18px;
  color: #2c3e50;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 10px;
}

.form-header i {
  color: #1976d2;
}

.form-hint {
  color: #6c757d;
  font-size: 14px;
  margin: 0;
  line-height: 1.5;
}

.form-group {
  margin-bottom: 20px;
}

.form-label {
  display: block;
  font-weight: 600;
  color: #495057;
  margin-bottom: 8px;
  font-size: 14px;
}

.form-label.required::after {
  content: ' *';
  color: #dc3545;
}

.form-control {
  width: 100%;
  padding: 12px;
  border: 2px solid #e0e0e0;
  border-radius: 8px;
  font-size: 14px;
  transition: all 0.3s;
  font-family: inherit;
  resize: vertical;
}

.form-control:focus {
  outline: none;
  border-color: #1976d2;
  box-shadow: 0 0 0 3px rgba(25, 118, 210, 0.1);
}

.form-text {
  display: block;
  margin-top: 6px;
  color: #6c757d;
  font-size: 13px;
}

.button-group {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
  margin-top: 24px;
  padding-top: 20px;
  border-top: 1px solid #e9ecef;
}

/* 按钮样式 */
.btn {
  padding: 10px 20px;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
  display: inline-flex;
  align-items: center;
  gap: 8px;
}

.btn-primary {
  background: #1976d2;
  color: white;
}

.btn-primary:hover:not(:disabled) {
  background: #1565c0;
  transform: translateY(-1px);
}

.btn-primary:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-secondary {
  background: #6c757d;
  color: white;
}

.btn-secondary:hover {
  background: #5a6268;
}

.btn-outline-primary {
  background: transparent;
  color: #1976d2;
  border: 1px solid #1976d2;
}

.btn-outline-primary:hover {
  background: #1976d2;
  color: white;
}

/* 图标管理区域样式 */
.icon-management-section {
  margin-top: 40px;
  padding: 32px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.section-header {
  margin-bottom: 24px;
}

.section-title {
  font-size: 20px;
  color: #2c3e50;
  margin-bottom: 12px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 8px;
}

.section-title i {
  color: #667eea;
}

.section-desc {
  color: #666;
  font-size: 14px;
  padding: 12px 16px;
  background: #f8f9fa;
  border-radius: 6px;
  border-left: 3px solid #4a90e2;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 8px;
}

.section-desc i {
  color: #4a90e2;
}

.icons-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  margin-top: 24px;
}

/* 背景图片管理区域样式 */
.background-management-section {
  margin-top: 40px;
  padding: 32px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.background-card {
  display: flex;
  justify-content: center;
  margin-top: 24px;
  max-width: 600px;
  margin-left: auto;
  margin-right: auto;
}

/* 全屏预览样式 */
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

/* 文字颜色选择器样式 */
.color-picker-container {
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  padding: 16px;
  background: #fafafa;
}

.color-preview {
  margin-bottom: 16px;
  padding: 12px;
  background: white;
  border-radius: 6px;
  border: 1px solid #e0e0e0;
}

.preview-text {
  font-size: 16px;
  font-weight: 500;
}

.color-controls {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 16px;
  margin-bottom: 16px;
}

.color-title {
  font-size: 14px;
  font-weight: 600;
  color: #495057;
  margin-bottom: 8px;
}

/* 快速颜色选择 */
.quick-colors .color-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(32px, 1fr));
  gap: 6px;
}

.color-option {
  width: 32px;
  height: 32px;
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
  width: 40px;
  height: 36px;
  border: 2px solid #e0e0e0;
  border-radius: 4px;
  cursor: pointer;
}

.color-input:hover {
  border-color: #1976d2;
}

.color-text-input {
  flex: 1;
  padding: 8px;
  border: 1px solid #e0e0e0;
  border-radius: 4px;
  font-size: 14px;
  font-family: monospace;
}

.color-text-input:focus {
  outline: none;
  border-color: #1976d2;
}

.current-color-box {
  width: 36px;
  height: 36px;
  border: 2px solid #e0e0e0;
  border-radius: 4px;
}

/* 常用文字颜色 */
.common-colors {
  grid-column: span 2;
}

.common-color-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
  gap: 8px;
}

.common-color-item {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 8px;
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
  font-size: 12px;
  font-weight: 600;
}

/* 响应式 */
@media (max-width: 1200px) {
  .content-card {
    grid-template-columns: 1fr;
  }

  .icons-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .home-content-management {
    padding: 16px;
  }

  .page-header {
    flex-direction: column;
    align-items: stretch;
    gap: 12px;
  }

  .button-group {
    flex-direction: column;
  }

  .btn {
    width: 100%;
    justify-content: center;
  }

  .icon-management-section {
    padding: 20px;
    margin-top: 24px;
  }

  .icons-grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }
}
</style>
