<template>
  <div class="video-management">
    <div class="page-header">
      <h2><i class="bi bi-film"></i> 视频管理</h2>
      <button class="btn btn-return" @click="$router.push('/')">
        <i class="bi bi-house"></i> 返回首页
      </button>
    </div>

    <!-- 全局提示 -->
    <div class="global-tips">
      <i class="bi bi-info-circle"></i>
      支持 MP4/AVI/MOV/WMV/FLV/WebM/MKV/3GP 格式，最大 150MB
    </div>

    <!-- 三列布局 -->
    <div class="three-column-layout">
      <!-- 左列：企业宣传视频 -->
      <section class="video-section left-column">
        <div class="section-header">
          <h3 class="section-title">
            <i class="bi bi-camera-video"></i> 企业宣传视频
          </h3>
          <span class="badge-tag">公司介绍页面</span>
        </div>
        <p class="section-desc">用于公司简介页面展示企业形象宣传片</p>

        <VideoCard
          :video="videos.companyIntro"
          @upload="(file) => handleUpload('company-intro', file)"
          @delete="() => handleDelete('company-intro')"
        />
      </section>

      <!-- 中列：企业优势视频 -->
      <section class="video-section middle-column">
        <div class="section-header">
          <h3 class="section-title">
            <i class="bi bi-award"></i> 企业优势视频
          </h3>
          <span class="badge-tag warning">首页展示组件</span>
        </div>
        <p class="section-desc">用于首页企业优势组件展示企业优势视频</p>

        <VideoCard
          :video="videos.companyAdvantage"
          title="企业优势视频"
          @upload="(file) => handleUpload('company-advantage', file)"
          @delete="() => handleDelete('company-advantage')"
        />
      </section>

      <!-- 右列：产品展示视频 -->
      <section class="video-section right-column">
        <div class="section-header">
          <h3 class="section-title">
            <i class="bi bi-collection-play"></i> 产品展示视频
          </h3>
          <span class="badge-tag success">首页展示组件</span>
        </div>
        <p class="section-desc">用于首页产品展示组件（最多1个）</p>

        <VideoCard
          :video="videos.productVideo"
          title="产品展示视频"
          @upload="(file) => handleUpload('product-video', file)"
          @delete="() => handleDelete('product-video')"
        />
      </section>
    </div>

    <!-- 上传进度条 -->
    <div v-if="uploadProgress.show" class="upload-progress-overlay">
      <div class="upload-progress-modal">
        <div class="progress-header">
          <i class="bi bi-cloud-upload"></i>
          <span>上传中...</span>
        </div>
        <div class="progress-file-name">{{ uploadProgress.fileName }}</div>
        <div class="progress-bar-wrapper">
          <div class="progress-bar">
            <div 
              class="progress-bar-fill" 
              :style="{ width: uploadProgress.percent + '%' }"
            >
              <span class="progress-text">{{ uploadProgress.percent }}%</span>
            </div>
          </div>
        </div>
        <div class="progress-tip">
          <i class="bi bi-info-circle"></i>
          请不要关闭页面，正在上传中...
        </div>
      </div>
    </div>

  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import VideoCard from '@/components/VideoCard.vue'
import { uploadAPI } from '@/api'
import fileManager from '@/utils/fileManager'
import { handleFileOperationError, handleFileOperationSuccess, defaultShowMessage } from '@/utils/errorHandler'
import { filterExistingFiles } from '@/utils/fileValidator'
import { checkDatabaseConsistency, generateCleanupReport, displayCleanupReport } from '@/utils/databaseCleaner'
import Swal from 'sweetalert2'

// 视频数据结构
const videos = ref({
  companyIntro: {
    url: '',
    fileName: '',
    fileSize: '',
    duration: '',
    updateTime: '',
    remark: ''
  },
  companyAdvantage: {
    url: '',
    fileName: '',
    fileSize: '',
    duration: '',
    updateTime: '',
    remark: ''
  },
  productVideo: {
    url: '',
    fileName: '',
    fileSize: '',
    duration: '',
    updateTime: '',
    remark: ''
  }
})

// 上传进度状态
const uploadProgress = ref({
  show: false,
  percent: 0,
  fileName: ''
})

// 加载数据
onMounted(() => {
  // 清除缓存，确保显示最新数据
  fileManager.clearAllCache()
  loadVideos()
})

// 加载所有视频
const loadVideos = async () => {
  try {
    // 加载企业宣传视频
    const companyResponse = await uploadAPI.getFilesByCategory('company-intro')
    // 处理不同的响应格式
    let companyFiles = []
    if (Array.isArray(companyResponse?.data)) {
      companyFiles = companyResponse.data
    } else if (companyResponse?.data?.files && Array.isArray(companyResponse.data.files)) {
      companyFiles = companyResponse.data.files
    }

    if (companyFiles?.length > 0) {
      // 按 updateTime 升序排序，确保最新的在最后面
      companyFiles.sort((a, b) => {
        const timeA = new Date(a.updateTime || 0)
        const timeB = new Date(b.updateTime || 0)
        return timeA - timeB
      })

      // 使用文件验证工具过滤存在的文件
      const validFiles = await filterExistingFiles([companyFiles[0]])

      if (validFiles.length > 0) {
        const file = validFiles[0]
        videos.value.companyIntro = {
          url: file.fileUrl,
          fileName: file.originalName,
          fileSize: formatFileSize(file.fileSize),
          updateTime: file.updateTime,
          remark: file.remark || ''
        }
      } else {
        console.warn('企业宣传视频文件不存在，已跳过')
        // 文件不存在，重置状态
        videos.value.companyIntro = {
          url: '',
          fileName: '',
          fileSize: '',
          duration: '',
          updateTime: '',
          remark: ''
        }
      }
    } else {
      // 如果没有数据，重置为企业宣传视频初始状态
      videos.value.companyIntro = {
        url: '',
        fileName: '',
        fileSize: '',
        duration: '',
        updateTime: '',
        remark: ''
      }
    }

    // 加载企业优势视频
    const advantageResponse = await uploadAPI.getFilesByCategory('company-advantage')
    // 处理不同的响应格式
    let advantageFiles = []
    if (Array.isArray(advantageResponse?.data)) {
      advantageFiles = advantageResponse.data
    } else if (advantageResponse?.data?.files && Array.isArray(advantageResponse.data.files)) {
      advantageFiles = advantageResponse.data.files
    }

    if (advantageFiles && advantageFiles.length > 0) {
      // 按 updateTime 升序排序，确保最新的在最后面
      advantageFiles.sort((a, b) => {
        const timeA = new Date(a.updateTime || 0)
        const timeB = new Date(b.updateTime || 0)
        return timeA - timeB
      })

      // 重置企业优势视频
      videos.value.companyAdvantage = {
        url: '',
        fileName: '',
        fileSize: '',
        duration: '',
        updateTime: '',
        remark: ''
      }

      // 使用文件验证工具过滤存在的文件，只取第一个
      const validFiles = await filterExistingFiles(advantageFiles.slice(0, 1))

      if (validFiles.length > 0) {
        const file = validFiles[0]
        videos.value.companyAdvantage = {
          url: file.fileUrl,
          fileName: file.originalName,
          fileSize: formatFileSize(file.fileSize),
          updateTime: file.updateTime,
          remark: file.remark || ''
        }
      } else {
        console.warn('企业优势视频文件不存在，已跳过')
        videos.value.companyAdvantage = {
          url: '',
          fileName: '',
          fileSize: '',
          duration: '',
          updateTime: '',
          remark: ''
        }
      }
    } else {
      // 如果没有数据，重置企业优势视频
      videos.value.companyAdvantage = {
        url: '',
        fileName: '',
        fileSize: '',
        duration: '',
        updateTime: '',
        remark: ''
      }
    }

    // 加载产品视频
    const productResponse = await uploadAPI.getFilesByCategory('product-video')
    // 处理不同的响应格式
    let productFiles = []
    if (Array.isArray(productResponse?.data)) {
      productFiles = productResponse.data
    } else if (productResponse?.data?.files && Array.isArray(productResponse.data.files)) {
      productFiles = productResponse.data.files
    }

    if (productFiles && productFiles.length > 0) {
      // 按 updateTime 升序排序，确保最新的在最后面
      productFiles.sort((a, b) => {
        const timeA = new Date(a.updateTime || 0)
        const timeB = new Date(b.updateTime || 0)
        return timeA - timeB
      })

      // 重置产品视频
      videos.value.productVideo = {
        url: '',
        fileName: '',
        fileSize: '',
        duration: '',
        updateTime: '',
        remark: ''
      }

      // 使用文件验证工具过滤存在的文件，只取第一个
      const validFiles = await filterExistingFiles(productFiles.slice(0, 1))

      if (validFiles.length > 0) {
        const file = validFiles[0]
        videos.value.productVideo = {
          url: file.fileUrl,
          fileName: file.originalName,
          fileSize: formatFileSize(file.fileSize),
          updateTime: file.updateTime,
          remark: file.remark || ''
        }
      } else {
        console.warn('产品展示视频文件不存在，已跳过')
        videos.value.productVideo = {
          url: '',
          fileName: '',
          fileSize: '',
          duration: '',
          updateTime: '',
          remark: ''
        }
      }
    } else {
      // 如果没有数据，重置产品视频
      videos.value.productVideo = {
        url: '',
        fileName: '',
        fileSize: '',
        duration: '',
        updateTime: '',
        remark: ''
      }
    }

  
    console.log('视频数据加载成功')

    // 开发环境下执行数据库一致性检查
    if (import.meta.env.DEV) {
      setTimeout(async () => {
        console.log('🔍 开始执行数据库一致性检查...')

        // 检查企业宣传视频
        if (companyFiles && companyFiles.length > 0) {
          const companyConsistency = await checkDatabaseConsistency(companyFiles.slice(0, 1))
          const companyReport = generateCleanupReport(companyConsistency, '企业宣传视频')
          displayCleanupReport(companyReport)
        }

        // 检查企业优势视频
        if (advantageFiles && advantageFiles.length > 0) {
          const advantageConsistency = await checkDatabaseConsistency(advantageFiles.slice(0, 1))
          const advantageReport = generateCleanupReport(advantageConsistency, '企业优势视频')
          displayCleanupReport(advantageReport)
        }

        // 检查产品视频
        if (productFiles && productFiles.length > 0) {
          const productConsistency = await checkDatabaseConsistency(productFiles.slice(0, 1))
          const productReport = generateCleanupReport(productConsistency, '产品视频')
          displayCleanupReport(productReport)
        }


        console.log('📋 数据库一致性检查完成')
      }, 2000) // 延迟2秒执行，避免影响主流程
    }
  } catch (error) {
    console.error('加载视频失败:', error)
    // 发生错误时重置所有视频数据，避免显示已删除的视频
    videos.value.companyIntro = {
      url: '',
      fileName: '',
      fileSize: '',
      duration: '',
      updateTime: '',
      remark: ''
    }
    videos.value.companyAdvantage = {
      url: '',
      fileName: '',
      fileSize: '',
      duration: '',
      updateTime: '',
      remark: ''
    }
    videos.value.productVideo = {
      url: '',
      fileName: '',
      fileSize: '',
      duration: '',
      updateTime: '',
      remark: ''
    }
  }
}

// 处理上传
const handleUpload = async (category, file, index = null) => {
  try {
    // 验证文件类型
    const validTypes = [
      'video/mp4', 'video/avi', 'video/mov', 'video/wmv', 'video/flv', 'video/webm',
      'video/mkv', 'video/3gpp', 'video/quicktime', 'video/x-msvideo', 'video/x-matroska'
    ]
    if (!validTypes.includes(file.type)) {
      Swal.fire({
        title: '温馨提示',
        text: '请选择有效的视频格式 (MP4, AVI, MOV, WMV, FLV, WebM, MKV, 3GP)',
        icon: 'warning',
        confirmButtonText: '确定',
        confirmButtonColor: '#1976d2'
      })
      return
    }

    // 验证文件大小 (150MB)
    const maxSize = 150 * 1024 * 1024
    if (file.size > maxSize) {
      Swal.fire({
        title: '温馨提示',
        text: '文件大小不能超过 150MB',
        icon: 'warning',
        confirmButtonText: '确定',
        confirmButtonColor: '#1976d2'
      })
      return
    }

    // 对于企业优势视频，检查是否已有视频
    if (category === 'company-advantage') {
      if (videos.value.companyAdvantage && videos.value.companyAdvantage.url) {
        // 如果已有企业优势视频，不允许上传
        Swal.fire({
          title: '温馨提示',
          text: '企业优势视频已存在，请先删除现有视频',
          icon: 'warning',
          confirmButtonText: '确定',
          confirmButtonColor: '#1976d2'
        })
        return
      }
    }

    // 对于产品视频，检查是否已有视频
    if (category === 'product-video') {
      if (videos.value.productVideo && videos.value.productVideo.url) {
        // 如果已有产品视频，不允许上传
        Swal.fire({
          title: '温馨提示',
          text: '产品展示视频已存在，请先删除现有视频',
          icon: 'warning',
          confirmButtonText: '确定',
          confirmButtonColor: '#1976d2'
        })
        return
      }
    }

  
    // 显示上传进度
    uploadProgress.value.show = true
    uploadProgress.value.percent = 0
    uploadProgress.value.fileName = file.name

    // 调用上传API
    const response = await uploadAPI.uploadVideo(file, {
      category: category,
      description: category === 'company-intro' ? '企业宣传视频' :
                   category === 'company-advantage' ? '企业优势视频' : `产品展示视频 ${index + 1}`,
      maxSize: 150 * 1024 * 1024, // 150MB
      thumbnail: true, // 生成缩略图
      onProgress: (percent) => {
        uploadProgress.value.percent = percent
      }
    })

    if (response.data && response.data.url) {
      // 隐藏进度条
      uploadProgress.value.show = false

      // 清除相关缓存
      fileManager.clearCache(category)

      // 对于企业优势视频和产品视频，直接更新数据
      if (category === 'company-advantage') {
        videos.value.companyAdvantage = {
          url: response.data.url,
          fileName: response.data.originalName || file.name,
          fileSize: formatFileSize(response.data.fileSize || file.size),
          updateTime: response.data.updateTime || new Date().toISOString(),
          remark: response.data.remark || ''
        }
      } else if (category === 'product-video') {
        videos.value.productVideo = {
          url: response.data.url,
          fileName: response.data.originalName || file.name,
          fileSize: formatFileSize(response.data.fileSize || file.size),
          updateTime: response.data.updateTime || new Date().toISOString(),
          remark: response.data.remark || ''
        }
      } else {
        // 企业宣传视频重新加载
        await loadVideos()
      }

      Swal.fire({
        title: '上传成功',
        icon: 'success',
        confirmButtonText: '确定',
        confirmButtonColor: '#1976d2',
        timer: 1500
      })
    } else {
      throw new Error('上传响应格式错误，未获取到文件URL')
    }
  } catch (error) {
    // 隐藏进度条
    uploadProgress.value.show = false

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

// 处理删除
const handleDelete = async (category, index = null) => {
  let videoData
  let videoTitle

  if (category === 'company-intro') {
    videoData = videos.value.companyIntro
    videoTitle = '企业宣传视频'
  } else if (category === 'company-advantage') {
    videoData = videos.value.companyAdvantage
    videoTitle = '企业优势视频'
  } else if (category === 'product-video') {
    videoData = videos.value.productVideo
    videoTitle = '产品展示视频'
  }

  if (!videoData || !videoData.url) {
    Swal.fire({
      title: '温馨提示',
      text: '该位置暂无视频',
      icon: 'info',
      confirmButtonText: '确定',
      confirmButtonColor: '#1976d2'
    })
    return
  }

  // 使用实际文件名，如果没有则使用默认标题
  const displayName = videoData.fileName || videoTitle

  const result = await Swal.fire({
    title: '温馨提示',
    html: `确定要删除<br/><strong>${displayName}</strong> 吗？`,
    icon: 'question',
    showCancelButton: true,
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    confirmButtonColor: '#1976d2',
    cancelButtonColor: '#6c757d'
  })

  if (!result.isConfirmed) return

  try {
    // 调用删除API
    const response = await uploadAPI.deleteFile(videoData.url)

    // 检查删除结果
    if (response && response.success && response.data && response.data.deleted) {
      // 清除相关缓存
      if (category === 'company-intro') {
        fileManager.clearCache('company-intro')
      } else if (category === 'company-advantage') {
        fileManager.clearCache('company-advantage')
      } else {
        fileManager.clearCache('product-video')
      }

      // 对于企业优势视频和产品视频，清空数据
      if (category === 'company-advantage') {
        videos.value.companyAdvantage = {
          url: '',
          fileName: '',
          fileSize: '',
          duration: '',
          updateTime: '',
          remark: ''
        }
      } else if (category === 'product-video') {
        videos.value.productVideo = {
          url: '',
          fileName: '',
          fileSize: '',
          duration: '',
          updateTime: '',
          remark: ''
        }
      } else {
        // 企业宣传视频重新刷新
        await refreshVideos()
      }

      // 显示成功消息
      handleFileOperationSuccess('delete', response.data, defaultShowMessage)
    } else {
      throw new Error(response?.message || '删除失败')
    }
  } catch (error) {
    // 使用统一错误处理
    handleFileOperationError(error, 'delete', videoData, defaultShowMessage)

    // 即使删除失败，也尝试重新加载数据以保持界面同步
    try {
      await loadVideos()
    } catch (loadError) {
      console.error('重新加载数据失败:', loadError)
    }
  }
}

// 刷新视频数据（使用专门刷新接口）
const refreshVideos = async () => {
  try {
    // 刷新企业宣传视频
    const companyResponse = await uploadAPI.refreshFileList('company-intro')
    if (companyResponse && companyResponse.success && companyResponse.data && companyResponse.data.files) {
      // 按 updateTime 升序排序，确保最新的在最后面
      companyResponse.data.files.sort((a, b) => {
        const timeA = new Date(a.updateTime || 0)
        const timeB = new Date(b.updateTime || 0)
        return timeA - timeB
      })

      if (companyResponse.data.files[0]) {
        const file = companyResponse.data.files[0]
        videos.value.companyIntro = {
          url: file.fileUrl,
          fileName: file.originalName,
          fileSize: formatFileSize(file.fileSize),
          updateTime: file.updateTime,
          remark: file.remark || ''
        }
      } else {
        // 如果没有数据，重置为企业宣传视频初始状态
        videos.value.companyIntro = {
          url: '',
          fileName: '',
          fileSize: '',
          duration: '',
          updateTime: '',
          remark: ''
        }
      }

      // 更新缓存
      fileManager.cache.set('files_company-intro', companyResponse.data.files)
      fileManager.lastUpdateTime.set('files_company-intro', Date.now())
    } else {
      // 请求失败时重置
      videos.value.companyIntro = {
        url: '',
        fileName: '',
        fileSize: '',
        duration: '',
        updateTime: '',
        remark: ''
      }
      fileManager.clearCache('company-intro')
    }

    // 刷新产品视频
    const productResponse = await uploadAPI.refreshFileList('product-video')
    if (productResponse && productResponse.success && productResponse.data && productResponse.data.files) {
      // 按 updateTime 升序排序，确保最新的在最后面
      productResponse.data.files.sort((a, b) => {
        const timeA = new Date(a.updateTime || 0)
        const timeB = new Date(b.updateTime || 0)
        return timeA - timeB
      })

      // 重置产品视频
      videos.value.productVideo = {
        url: '',
        fileName: '',
        fileSize: '',
        duration: '',
        updateTime: '',
        remark: ''
      }

      // 只填充第一个视频
      if (productResponse.data.files.length > 0) {
        const file = productResponse.data.files[0]
        videos.value.productVideo = {
          url: file.fileUrl,
          fileName: file.originalName,
          fileSize: formatFileSize(file.fileSize),
          updateTime: file.updateTime,
          remark: file.remark || ''
        }
      }

      // 更新缓存
      fileManager.cache.set('files_product-video', productResponse.data.files)
      fileManager.lastUpdateTime.set('files_product-video', Date.now())
    } else {
      // 请求失败时重置产品视频
      videos.value.productVideo = {
        url: '',
        fileName: '',
        fileSize: '',
        duration: '',
        updateTime: '',
        remark: ''
      }
      fileManager.clearCache('product-video')
    }

    // 刷新企业优势视频
    const advantageResponse = await uploadAPI.refreshFileList('company-advantage')
    if (advantageResponse && advantageResponse.success && advantageResponse.data && advantageResponse.data.files) {
      // 按 updateTime 升序排序，确保最新的在最后面
      advantageResponse.data.files.sort((a, b) => {
        const timeA = new Date(a.updateTime || 0)
        const timeB = new Date(b.updateTime || 0)
        return timeA - timeB
      })

      // 重置企业优势视频
      videos.value.companyAdvantage = {
        url: '',
        fileName: '',
        fileSize: '',
        duration: '',
        updateTime: '',
        remark: ''
      }

      // 只填充第一个视频
      if (advantageResponse.data.files.length > 0) {
        const file = advantageResponse.data.files[0]
        videos.value.companyAdvantage = {
          url: file.fileUrl,
          fileName: file.originalName,
          fileSize: formatFileSize(file.fileSize),
          updateTime: file.updateTime,
          remark: file.remark || ''
        }
      }

      // 更新缓存
      fileManager.cache.set('files_company-advantage', advantageResponse.data.files)
      fileManager.lastUpdateTime.set('files_company-advantage', Date.now())
    } else {
      // 请求失败时重置企业优势视频
      videos.value.companyAdvantage = {
        url: '',
        fileName: '',
        fileSize: '',
        duration: '',
        updateTime: '',
        remark: ''
      }
      fileManager.clearCache('company-advantage')
    }

    console.log('视频数据刷新成功')
  } catch (error) {
    console.error('刷新视频失败:', error)
    // 使用统一错误处理
    handleFileOperationError(error, 'refresh', {}, defaultShowMessage)

    // 发生错误时重置所有视频数据，避免显示已删除的视频
    videos.value.companyIntro = {
      url: '',
      fileName: '',
      fileSize: '',
      duration: '',
      updateTime: '',
      remark: ''
    }
    videos.value.companyAdvantage = {
      url: '',
      fileName: '',
      fileSize: '',
      duration: '',
      updateTime: '',
      remark: ''
    }
    videos.value.productVideo = {
      url: '',
      fileName: '',
      fileSize: '',
      duration: '',
      updateTime: '',
      remark: ''
    }

    // 清除所有相关缓存
    fileManager.clearCache('company-intro')
    fileManager.clearCache('company-advantage')
    fileManager.clearCache('product-video')
  }
}

// 格式化文件大小
const formatFileSize = (bytes) => {
  if (!bytes) return '未知'
  if (bytes < 1024) return bytes + ' B'
  if (bytes < 1024 * 1024) return (bytes / 1024).toFixed(2) + ' KB'
  return (bytes / (1024 * 1024)).toFixed(2) + ' MB'
}
</script>

<style scoped>
.video-management {
  padding: 20px;
  max-width: 1600px;
  margin: 0 auto;
  background: #f5f7fa;
  min-height: 100vh;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 25px;
  padding: 0 10px;
}

.page-header h2 {
  margin: 0;
  color: #1976d2;
  font-size: 28px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 10px;
}

.page-header h2 i {
  color: #1976d2;
}

/* 全局提示 */
.global-tips {
  background: linear-gradient(135deg, #e3f2fd 0%, #bbdefb 100%);
  color: #1565c0;
  padding: 16px 24px;
  border-radius: 12px;
  margin-bottom: 25px;
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 14px;
  font-weight: 500;
  border-left: 4px solid #1976d2;
  box-shadow: 0 2px 8px rgba(25, 118, 210, 0.1);
}

.global-tips i {
  font-size: 18px;
  color: #1976d2;
}

.btn-return {
  background: white;
  color: #1976d2;
  border: 2px solid #1976d2;
  padding: 8px 20px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  gap: 6px;
}

.btn-return:hover {
  background: #1976d2;
  color: white;
}

/* 三列布局 */
.three-column-layout {
  display: grid;
  grid-template-columns: 1fr 1fr 1fr;
  gap: 20px;
}

/* 视频区域 */
.video-section {
  background: white;
  border-radius: 12px;
  padding: 25px;
  box-shadow: 0 2px 10px rgba(25, 118, 210, 0.08);
  transition: all 0.3s;
}

.video-section:hover {
  box-shadow: 0 4px 20px rgba(25, 118, 210, 0.15);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
  padding-bottom: 12px;
  border-bottom: 2px solid #e3f2fd;
}

.section-title {
  font-size: 18px;
  color: #1976d2;
  margin: 0;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 8px;
}

.section-title i {
  color: #1976d2;
  font-size: 20px;
}

.badge-tag {
  background: linear-gradient(135deg, #1976d2 0%, #2196f3 100%);
  color: white;
  padding: 6px 14px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
  white-space: nowrap;
}

.badge-tag.warning {
  background: linear-gradient(135deg, #f57c00 0%, #ff9800 100%);
}

.badge-tag.success {
  background: linear-gradient(135deg, #0288d1 0%, #03a9f4 100%);
}

.section-desc {
  color: #666;
  font-size: 13px;
  margin: 12px 0 20px 0;
  line-height: 1.5;
}

/* 视频列表 */
.video-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

/* 响应式 */
@media (max-width: 1200px) {
  .three-column-layout {
    grid-template-columns: 1fr 1fr;
  }
}

@media (max-width: 768px) {
  .three-column-layout {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .video-management {
    padding: 15px;
  }

  .page-header {
    flex-direction: column;
    gap: 15px;
    align-items: flex-start;
  }

  .section-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }

  .video-section {
    padding: 20px;
  }
}

/* 上传进度条样式 */
.upload-progress-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.7);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
  backdrop-filter: blur(4px);
}

.upload-progress-modal {
  background: white;
  border-radius: 16px;
  padding: 40px;
  min-width: 500px;
  max-width: 600px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.3);
  animation: slideUp 0.3s ease;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.progress-header {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 20px;
  font-weight: 600;
  color: #1976d2;
  margin-bottom: 20px;
}

.progress-header i {
  font-size: 24px;
  animation: pulse 1.5s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.1);
  }
}

.progress-file-name {
  color: #666;
  font-size: 14px;
  margin-bottom: 25px;
  word-break: break-all;
  padding: 12px;
  background: #f5f7fa;
  border-radius: 8px;
}

.progress-bar-wrapper {
  margin-bottom: 20px;
}

.progress-bar {
  width: 100%;
  height: 40px;
  background: #e3f2fd;
  border-radius: 20px;
  overflow: hidden;
  position: relative;
  box-shadow: inset 0 2px 4px rgba(0, 0, 0, 0.1);
}

.progress-bar-fill {
  height: 100%;
  background: linear-gradient(90deg, #1976d2 0%, #2196f3 100%);
  border-radius: 20px;
  transition: width 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow: hidden;
}

.progress-bar-fill::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(
    90deg,
    transparent,
    rgba(255, 255, 255, 0.3),
    transparent
  );
  animation: shimmer 2s infinite;
}

@keyframes shimmer {
  0% {
    transform: translateX(-100%);
  }
  100% {
    transform: translateX(100%);
  }
}

.progress-text {
  color: white;
  font-weight: 600;
  font-size: 16px;
  z-index: 1;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.2);
}

.progress-tip {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #666;
  font-size: 13px;
  padding: 12px;
  background: #fff3cd;
  border-radius: 8px;
  border-left: 4px solid #ffc107;
}

.progress-tip i {
  color: #ffc107;
  font-size: 16px;
}

@media (max-width: 768px) {
  .upload-progress-modal {
    min-width: 90%;
    padding: 30px 20px;
  }
  
  .progress-bar {
    height: 32px;
  }
  
  .progress-text {
    font-size: 14px;
  }
}
</style>
