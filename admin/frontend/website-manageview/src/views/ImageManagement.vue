<template>
  <div class="image-management">
    <div class="page-header">
      <h2><i class="bi bi-images"></i> 图片管理</h2>
      <button class="btn btn-outline-primary" @click="$router.push('/')">
        <i class="bi bi-house"></i> 返回首页
      </button>
    </div>

    <!-- 轮播图管理区 -->
    <section class="section">
      <h3 class="section-title">
        <i class="bi bi-images"></i> 轮播图管理
      </h3>
      <p class="section-desc">
        <i class="bi bi-info-circle"></i>
        支持 JPG/PNG/GIF 格式，最大 10MB
      </p>

      <div class="image-grid">
        <CarouselImageCard
          v-for="carousel in carouselImages"
          :key="carousel.type"
          :carousel="carousel"
          @upload="handleUpload"
          @delete="handleDelete"
          @refresh="refreshCarouselImages"
        />
      </div>
    </section>

    <!-- 服务图片管理区 -->
    <section class="section">
      <h3 class="section-title">
        <i class="bi bi-images"></i> 服务图片管理
      </h3>
      <p class="section-desc">
        <i class="bi bi-info-circle"></i>
        首页服务流程展示图片，支持 JPG/PNG/GIF 格式，最大 10MB，最多可上传10张
      </p>

      <div class="image-grid">
        <CarouselImageCard
          v-for="serviceImage in serviceImages"
          :key="serviceImage.type"
          :carousel="serviceImage"
          @upload="handleUpload"
          @delete="handleDelete"
          @refresh="refreshServiceImages"
        />
      </div>
    </section>

    <!-- 公司简介页面图片管理区 -->
    <section class="section">
      <h3 class="section-title">
        <i class="bi bi-building"></i> 公司简介页面图片
      </h3>
      <p class="section-desc">
        <i class="bi bi-info-circle"></i>
        公司简介页面各区域展示图片，支持 JPG/PNG/GIF 格式，最大 10MB
      </p>

      <!-- 企业概况区域图片 -->
      <div v-for="aboutImage in aboutSectionImages" :key="aboutImage.type" class="about-section-item">
        <CarouselImageCard
          :carousel="aboutImage"
          @upload="handleUpload"
          @delete="handleDelete"
          @refresh="refreshCarouselImages"
        />
      </div>
    </section>

  
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
import CarouselImageCard from '@/components/CarouselImageCard.vue'
import UploadProgress from '@/components/UploadProgress.vue'
import { uploadAPI } from '@/api'
import fileManager from '@/utils/fileManager'
import { handleFileOperationError, handleFileOperationSuccess, defaultShowMessage } from '@/utils/errorHandler'
import { filterExistingFiles } from '@/utils/fileValidator'

// 轮播图数据
const carouselImages = ref([
  {
    type: 'home-carousel',
    title: '首页顶部轮播图',
    images: [],  // 存储最多5张图片的数组
    size: '',
    maxSize: 10,
    maxCount: 5,  // 最多允许5张轮播图
    description: '用于首页顶部展示'
  },
  {
    type: 'about-carousel',
    title: '公司简介右边轮播图',
    images: [],  // 存储最多5张图片的数组
    size: '',
    maxSize: 10,
    maxCount: 5,  // 最多允许5张轮播图
    description: '用于公司简介页面右边展示'
  },
  {
    type: 'about-bottom-fixed',
    title: '公司简介底部固定图片',
    images: [],  // 存储最多3张图片的数组
    size: '',
    maxSize: 10,
    maxCount: 3,  // 最多允许3张图片
    description: '用于公司简介页面底部固定位置展示'
  }
])

// 服务图片数据
const serviceImages = ref([
  {
    type: 'service-images',
    title: '首页服务流程图片',
    images: [],  // 存储服务图片的数组
    size: '',
    maxSize: 10,
    maxCount: 10,  // 最多允许10张图片
    description: '用于首页服务流程展示区域',
    category: 'service-images'
  }
])

// 公司简介页面各区域图片数据
const aboutSectionImages = ref([
  {
    type: 'about-overview',
    title: '企业概况区域图片',
    images: [],  // 存储最多6张图片的数组
    size: '',
    maxSize: 10,
    maxCount: 6,  // 最多允许6张图片
    description: '用于Overview区域展示'
  },
  {
    type: 'about-production',
    title: '生产区域图片',
    images: [],  // 存储最多8张图片的数组
    size: '',
    maxSize: 10,
    maxCount: 8,  // 最多允许8张图片
    description: '用于Production区域展示'
  },
  {
    type: 'about-rd',
    title: '研发区域图片',
    images: [],  // 存储最多5张图片的数组
    size: '',
    maxSize: 10,
    maxCount: 5,  // 最多允许5张图片
    description: '用于R&D区域展示'
  },
  {
    type: 'about-quality',
    title: '质量控制区域图片',
    images: [],  // 存储最多5张图片的数组
    size: '',
    maxSize: 10,
    maxCount: 5,  // 最多允许5张图片
    description: '用于Quality Control区域展示'
  }
])


// 上传进度状态
const uploadProgress = ref({
  show: false,        // 是否显示进度条
  percent: 0,         // 上传进度百分比
  fileName: '',       // 上传文件名
  cancellable: false  // 是否可取消
})

// 上传请求的取消控制器
let uploadController = null

// 加载数据
onMounted(() => {
  // 清除缓存，确保显示最新数据
  fileManager.clearAllCache()
  loadAllImages()
})

// 位置映射缓存：基于文件名的localStorage持久化存储
const POSITION_CACHE_KEY = 'imagePositionCache'

// 从localStorage加载位置缓存
const loadPositionCache = () => {
  try {
    const cached = localStorage.getItem(POSITION_CACHE_KEY)
    if (cached) {
      const data = JSON.parse(cached)
      console.log('从localStorage加载位置缓存:', data)
      return data // 直接返回JSON对象
    }
  } catch (error) {
    console.error('加载位置缓存失败:', error)
  }
  return {}
}

// 保存位置缓存到localStorage
const savePositionCacheToStorage = (cache) => {
  try {
    localStorage.setItem(POSITION_CACHE_KEY, JSON.stringify(cache))
    console.log('保存位置缓存到localStorage:', cache)
  } catch (error) {
    console.error('保存位置缓存失败:', error)
  }
}

const positionCache = ref(loadPositionCache())

// 保存图片位置信息到缓存（基于文件名）
const saveImagePosition = (category, fileName, position) => {
  if (!positionCache.value[category]) {
    positionCache.value[category] = {}
  }
  positionCache.value[category][fileName] = position
  console.log(`保存位置映射: ${category}, 文件名: ${fileName}, 位置: ${position}`)

  // 立即保存到localStorage
  savePositionCacheToStorage(positionCache.value)
}

// 为指定位置保存图片信息
const saveImageToPosition = (category, position, imageData) => {
  if (!positionCache.value[category]) {
    positionCache.value[category] = {}
  }
  positionCache.value[category][position] = imageData
  savePositionCacheToStorage(positionCache.value)
  console.log(`保存图片到位置: ${category}, 位置: ${position}`)
}

// 从指定位置获取图片信息
const getImageFromPosition = (category, position) => {
  return positionCache.value[category]?.[position] || null
}

// 从指定位置删除图片信息
const removeImageFromPosition = (category, position) => {
  if (positionCache.value[category] && positionCache.value[category][position]) {
    delete positionCache.value[category][position]
    savePositionCacheToStorage(positionCache.value)
    console.log(`从位置删除图片: ${category}, 位置: ${position}`)
  }
}

// 加载所有图片数据
const loadAllImages = async () => {
  // 加载轮播图（现在包含公司简介底部固定图片）
  await loadCarouselImages()
  // 加载服务图片
  await refreshServiceImages()
  // 加载公司简介页面图片
  await loadAboutSectionImages()
}

// 加载轮播图数据（简化版本）
const loadCarouselImages = async () => {
  try {
    for (const carousel of carouselImages.value) {
      // 调用后端接口查询该分类的所有图片
      const response = await uploadAPI.getFilesByCategory(carousel.type)

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

        console.log(`分类 ${carousel.type} 从后端获取的原始文件数据:`, files)

        // 使用文件验证工具过滤存在的文件
        const validFiles = await filterExistingFiles(files)

        // 初始化图片数组
        carousel.images = []

        console.log(`分类 ${carousel.type} 有效文件:`, validFiles.map(f => ({
          id: f.id,
          originalName: f.originalName,
          position: f.position,
          createTime: f.createTime
        })))

        // 从文件名中提取位置信息并分配位置
        const positionMap = new Map() // 存储位置到文件的映射

        // 第一遍：处理包含位置信息的文件名
        validFiles.forEach(file => {
          // 匹配文件名开头的数字，如 "1_xxx.jpg", "2_xxx.png" 等
          const positionMatch = file.originalName.match(/^(\d+)_(.+)$/)
          if (positionMatch) {
            const extractedPosition = parseInt(positionMatch[1])
            // 确保位置在有效范围内
            if (extractedPosition >= 1 && extractedPosition <= carousel.maxCount) {
              positionMap.set(extractedPosition, file)
              console.log(`🎯 从文件名识别位置 ${extractedPosition}: ${file.originalName}`)
            }
          }
        })

        // 第二遍：处理没有位置信息的文件，按时间顺序分配到空余位置
        const filesWithoutPosition = validFiles.filter(file =>
          !Array.from(positionMap.values()).includes(file)
        )

        filesWithoutPosition.sort((a, b) => new Date(a.createTime || 0) - new Date(b.createTime || 0))

        // 为没有位置信息的文件分配空余位置
        let currentPosition = 1
        filesWithoutPosition.forEach(file => {
          // 找到下一个空余位置
          while (positionMap.has(currentPosition) && currentPosition <= carousel.maxCount) {
            currentPosition++
          }

          if (currentPosition <= carousel.maxCount) {
            positionMap.set(currentPosition, file)
            console.log(`📍 为文件分配空余位置 ${currentPosition}: ${file.originalName}`)
          }
        })

        // 构建最终图片数组
        for (let position = 1; position <= carousel.maxCount; position++) {
          const file = positionMap.get(position)
          if (file) {
            carousel.images.push({
              id: file.id,
              url: file.fileUrl,
              updateTime: file.updateTime || file.createTime,
              originalName: file.originalName,
              position: position
            })
          }
        }

        console.log(`🎯 分类 ${carousel.type} 最终图片位置分配:`, carousel.images.map(img => ({
          position: img.position,
          originalName: img.originalName
        })))

      } else {
        // 如果没有数据，清空图片数组
        carousel.images = []
      }
    }
    console.log('轮播图数据加载成功')
  } catch (error) {
    console.error('加载轮播图失败:', error)
    // 发生错误时清空所有图片数组，避免显示已删除的图片
    carouselImages.value.forEach(carousel => {
      carousel.images = []
    })
  }
}


// 加载公司简介页面图片数据（简化版本）
const loadAboutSectionImages = async () => {
  try {
    for (const aboutImage of aboutSectionImages.value) {
      // 调用后端接口查询该分类的所有图片
      const response = await uploadAPI.getFilesByCategory(aboutImage.type)

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

        // 初始化图片数组
        aboutImage.images = []

        console.log(`分类 ${aboutImage.type} 有效文件:`, validFiles.map(f => ({
          id: f.id,
          originalName: f.originalName,
          position: f.position,
          createTime: f.createTime
        })))

        // 从文件名中提取位置信息并分配位置
        const positionMap = new Map() // 存储位置到文件的映射

        // 第一遍：处理包含位置信息的文件名
        validFiles.forEach(file => {
          // 匹配文件名开头的数字，如 "1_xxx.jpg", "2_xxx.png" 等
          const positionMatch = file.originalName.match(/^(\d+)_(.+)$/)
          if (positionMatch) {
            const extractedPosition = parseInt(positionMatch[1])
            // 确保位置在有效范围内
            if (extractedPosition >= 1 && extractedPosition <= aboutImage.maxCount) {
              positionMap.set(extractedPosition, file)
              console.log(`🎯 从文件名识别位置 ${extractedPosition}: ${file.originalName}`)
            }
          }
        })

        // 第二遍：处理没有位置信息的文件，按时间顺序分配到空余位置
        const filesWithoutPosition = validFiles.filter(file =>
          !Array.from(positionMap.values()).includes(file)
        )

        filesWithoutPosition.sort((a, b) => new Date(a.createTime || 0) - new Date(b.createTime || 0))

        // 为没有位置信息的文件分配空余位置
        let currentPosition = 1
        filesWithoutPosition.forEach(file => {
          // 找到下一个空余位置
          while (positionMap.has(currentPosition) && currentPosition <= aboutImage.maxCount) {
            currentPosition++
          }

          if (currentPosition <= aboutImage.maxCount) {
            positionMap.set(currentPosition, file)
            console.log(`📍 为文件分配空余位置 ${currentPosition}: ${file.originalName}`)
          }
        })

        // 构建最终图片数组
        for (let position = 1; position <= aboutImage.maxCount; position++) {
          const file = positionMap.get(position)
          if (file) {
            aboutImage.images.push({
              id: file.id,
              url: file.fileUrl,
              updateTime: file.updateTime || file.createTime,
              originalName: file.originalName,
              position: position
            })
          }
        }

        console.log(`🎯 分类 ${aboutImage.type} 最终图片位置分配:`, aboutImage.images.map(img => ({
          position: img.position,
          originalName: img.originalName
        })))

      } else {
        // 如果没有数据，清空图片数组
        aboutImage.images = []
      }
    }
    console.log('公司简介页面图片数据加载成功')
  } catch (error) {
    console.error('加载公司简介页面图片失败:', error)
    // 发生错误时清空所有图片数组，避免显示已删除的图片
    aboutSectionImages.value.forEach(aboutImage => {
      aboutImage.images = []
    })
  }
}



// 处理上传
const handleUpload = async (type, file, position = null) => {
  try {
    // 查找对应的图片位置信息
    const allImageGroups = [...carouselImages.value, ...serviceImages.value, ...aboutSectionImages.value]
    const imageInfo = allImageGroups.find(img => img.type === type)

    if (!imageInfo) {
      console.error('未找到对应的图片位置')
      return
    }

    // 显示上传进度条
    uploadProgress.value.show = true
    uploadProgress.value.percent = 0
    uploadProgress.value.fileName = file.name
    uploadProgress.value.cancellable = true

    // 为文件添加位置前缀，确保前端能够识别位置
    const fileNameWithPosition = position ? `${position}_${file.name}` : file.name

    // 创建新的File对象，文件名包含位置信息
    const fileWithPosition = new File([file], fileNameWithPosition, {
      type: file.type,
      lastModified: file.lastModified
    })

    // 准备上传参数
    const uploadParams = {
      category: type,  // 分类，直接使用type
      description: imageInfo?.description,      // 图片描述
      width: 1920,                              // 目标宽度
      height: 800,                              // 目标高度
      maxSize: imageInfo?.maxSize * 1024 * 1024, // 最大文件大小
      thumbnail: true,                          // 生成缩略图
      position: position,                       // 位置信息（传递给后端）
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

    // 调试：检查上传参数
    console.log(`🚀 开始上传: 原文件=${file.name}, 位置文件=${fileNameWithPosition}, 位置=${position}, 类型=${type}`)
    console.log(`📤 上传参数:`, uploadParams)

    // 使用图片上传API，传递包含位置信息的文件名
    const response = await uploadAPI.uploadImage(fileWithPosition, uploadParams)

    // API调用完成，显示后端处理中状态
    uploadProgress.value.percent = 96
    uploadProgress.value.fileName = '正在处理图片...'

    if (response.data && response.data.url) {
      // 后端处理完成，进度100%
      uploadProgress.value.percent = 100
      uploadProgress.value.fileName = `位置 ${position} 上传完成！`

      // 短暂延迟让用户看到100%进度
      await new Promise(resolve => setTimeout(resolve, 500))

      // 隐藏进度条
      uploadProgress.value.show = false

      // 显示上传成功消息，包含位置信息
      handleFileOperationSuccess('upload', {
        ...response.data,
        position: position,
        fileName: fileNameWithPosition,
        message: `图片已成功上传到位置 ${position}`
      }, defaultShowMessage)

      // 清除相关缓存并重新加载
      fileManager.clearCache(type)
      await loadAllImages()
    } else {
      throw new Error('上传响应格式错误，未获取到文件URL')
    }
  } catch (error) {
    // 隐藏进度条
    uploadProgress.value.show = false

    // 使用统一错误处理
    handleFileOperationError(error, 'upload', file, defaultShowMessage)
  }
}


// 处理上传取消
const handleUploadCancel = () => {
  if (uploadController) {
    uploadController.abort()
    uploadController = null
  }
}

// 处理删除
const handleDelete = async (type, imageId = null, position = null) => {
  const allImageGroups = [...carouselImages.value, ...serviceImages.value, ...aboutSectionImages.value]
  const imageInfo = allImageGroups.find(img => img.type === type)

  if (!imageInfo || imageInfo.images.length === 0) {
    console.warn('该位置暂无图片')
    return
  }

  // 确定要删除的图片，优先使用位置信息
  let targetImage
  if (position !== null) {
    targetImage = imageInfo.images.find(img => img.position === position)
  } else if (imageId) {
    targetImage = imageInfo.images.find(img => img.id === imageId)
  } else {
    targetImage = imageInfo.images[0]  // 默认删除第一张
  }

  if (!targetImage || !targetImage.url) {
    console.warn('未找到要删除的图片')
    return
  }

  try {
    // 调用删除API
    const response = await uploadAPI.deleteFile(targetImage.url)

    // 检查删除结果
    if (response && response.success && response.data && response.data.deleted) {
      // 无需清理localStorage，系统会自动重新分配位置

      // 清除相关缓存
      fileManager.clearCache(type)

      // 立即刷新文件列表
      await loadAllImages()

      // 显示成功消息，包含位置信息
      const deletedPosition = targetImage.position || position
      handleFileOperationSuccess('delete', {
        ...response.data,
        position: deletedPosition,
        message: `位置 ${deletedPosition} 的图片已成功删除`
      }, defaultShowMessage)
    } else {
      throw new Error(response?.message || '删除失败')
    }
  } catch (error) {
    // 使用统一错误处理
    handleFileOperationError(error, 'delete', targetImage, defaultShowMessage)

    // 即使删除失败，也尝试重新加载数据以保持界面同步
    try {
      await loadAllImages()
    } catch (loadError) {
      console.error('重新加载数据失败:', loadError)
    }
  }
}

// 刷新轮播图数据（使用专门刷新接口）
const refreshCarouselImages = async () => {
  try {
    for (const carousel of carouselImages.value) {
      // 调用刷新接口获取最新数据
      const response = await uploadAPI.refreshFileList(carousel.type)

      if (response && response.success && response.data && response.data.files) {
        // 先取前3张图片并按时间升序排序，确保最新的在最后面
        const sortedFiles = response.data.files.slice(0, 3).sort((a, b) => {
          const timeA = new Date(a.updateTime || 0)
          const timeB = new Date(b.updateTime || 0)
          return timeA - timeB
        })

        carousel.images = sortedFiles.map(file => ({
          id: file.id,
          url: file.fileUrl,
          updateTime: file.updateTime,
          originalName: file.originalName
        }))

        // 更新缓存
        fileManager.cache.set(`files_${carousel.type}`, response.data.files)
        fileManager.lastUpdateTime.set(`files_${carousel.type}`, Date.now())
      } else {
        // 如果没有数据，清空图片数组
        carousel.images = []
        // 清除缓存
        fileManager.clearCache(carousel.type)
      }
    }
    console.log('轮播图数据刷新成功')
  } catch (error) {
    console.error('刷新轮播图失败:', error)
    // 使用统一错误处理
    handleFileOperationError(error, 'refresh', {}, defaultShowMessage)

    // 发生错误时清空所有图片数组，避免显示已删除的图片
    carouselImages.value.forEach(carousel => {
      carousel.images = []
      fileManager.clearCache(carousel.type)
    })
  }
}

// 刷新服务图片
const refreshServiceImages = async () => {
  try {
    for (const serviceImage of serviceImages.value) {
      // 使用getFilesByCategory获取服务图片，使用不带斜杠的分类名
      const response = await uploadAPI.getFilesByCategory('service-images')

      if (response && response.data && response.data.list) {
        // 获取所有服务图片，按创建时间倒序排列
        const sortedFiles = response.data.list.sort((a, b) => {
          const timeA = new Date(a.createTime || 0)
          const timeB = new Date(b.createTime || 0)
          return timeA - timeB // 升序排列，最新的在后面
        })

        // 更新图片数组
        serviceImage.images = sortedFiles.map(file => ({
          id: file.id,
          url: file.fileUrl,
          name: file.originalName,
          size: file.fileSize,
          createTime: file.createTime,
          updateTime: file.updateTime
        }))

        // 更新缓存
        fileManager.cache.set('service_images', response.data.list)
        fileManager.lastUpdateTime.set('service_images', Date.now())
        console.log('服务图片数据刷新成功，找到', sortedFiles.length, '张图片')
      } else {
        // 如果没有数据，清空图片数组
        serviceImage.images = []
        // 清除缓存
        fileManager.clearCache('service_images')
        console.log('服务图片数据为空，已清空显示')
      }
    }
  } catch (error) {
    console.error('刷新服务图片失败:', error)
    // 不要无限重试，只显示错误信息，不清空数组
    // 避免无限循环的错误处理
    serviceImages.value.forEach(serviceImage => {
      if (serviceImage.images.length === 0) {
        // 只有在数组为空时才清空缓存
        fileManager.clearCache('service_images')
      }
    })
  }
}

// 格式化时间
const formatTime = (time) => {
  const date = new Date(time)
  return date.toLocaleString('zh-CN')
}
</script>

<style scoped>
.image-management {
  padding: 20px;
  max-width: 1400px;
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
  color: #667eea;
}

.section {
  background: white;
  border-radius: 12px;
  padding: 25px;
  margin-bottom: 25px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.section-title {
  font-size: 20px;
  color: #2c3e50;
  margin-bottom: 20px;
  font-weight: 600;
}

.section-title i {
  margin-right: 8px;
  color: #667eea;
}

.section-desc {
  color: #666;
  font-size: 14px;
  margin-bottom: 20px;
  padding: 10px 15px;
  background: #f8f9fa;
  border-radius: 6px;
  border-left: 3px solid #4a90e2;
}

.section-desc i {
  color: #4a90e2;
  margin-right: 6px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-header .section-title {
  margin-bottom: 0;
}

.image-grid {
  display: grid;
  grid-template-columns: 1fr;
  gap: 25px;
}

/* 公司简介页面每个区域单独占一行 */
.about-section-item {
  margin-bottom: 30px;
}




@media (max-width: 768px) {
  .image-grid {
    grid-template-columns: 1fr;
  }

  .page-header {
    flex-direction: column;
    gap: 15px;
    align-items: flex-start;
  }

}
</style>
