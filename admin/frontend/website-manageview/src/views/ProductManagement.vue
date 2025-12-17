<template>
  <div class="product-management">
    <div class="page-header">
      <h2>
        <i class="bi bi-box-seam"></i>
        产品管理
        <span class="count-badge">{{ products.length }}</span>
      </h2>
      <div class="header-actions">
        <div class="search-container">
          <div class="search-box">
            <input
              v-model="searchKeyword"
              type="text"
              placeholder="搜索产品名称..."
              class="search-input"
              @keyup.enter="performSearch"
            >
            <button
              v-if="searchKeyword"
              class="btn-clear-search"
              @click="clearSearch"
            >
              <i class="bi bi-x-circle"></i>
            </button>
            <button
              class="btn-search"
              @click="performSearch"
              :disabled="searching"
            >
              <i class="bi bi-search"></i>
            </button>
          </div>
        </div>
        
        <!-- 正常操作按钮 -->
        <div v-if="!showBatchActions" class="normal-actions">
          <button class="btn btn-success" @click="handleExcelBatchUpload">
            <i class="bi bi-file-earmark-excel"></i> Excel批量上传
          </button>
          <button class="btn btn-info" @click="toggleBatchMode">
            <i class="bi bi-check2-square"></i> 批量操作
          </button>
          <button class="btn btn-primary" @click="showAddModal">
            <i class="bi bi-plus-circle"></i> 添加新产品
          </button>
          <button class="btn btn-outline-primary" @click="$router.push('/')">
            <i class="bi bi-house"></i> 返回首页
          </button>
        </div>
        
        <!-- 批量操作按钮 -->
        <div v-else class="batch-actions">
          <div class="batch-select-info">
            <input
              type="checkbox"
              :checked="isAllSelected"
              @change="handleSelectAll"
              class="form-check-input"
            />
            <span>已选择 {{ selectedProducts.length }} 个产品</span>
          </div>
          <button class="btn btn-success" @click="handleBatchActivate" :disabled="selectedProducts.length === 0">
            <i class="bi bi-check-circle"></i> 批量上架
          </button>
          <button class="btn btn-warning" @click="handleBatchDeactivate" :disabled="selectedProducts.length === 0">
            <i class="bi bi-x-circle"></i> 批量下架
          </button>
          <button class="btn btn-info" @click="handleBatchSaveAsDraft" :disabled="selectedProducts.length === 0">
            <i class="bi bi-file-earmark"></i> 批量草稿
          </button>
          <button class="btn btn-primary" @click="handleBatchSetMain" :disabled="selectedProducts.length === 0">
            <i class="bi bi-house"></i> 批量主产品
          </button>
          <button class="btn btn-outline-primary" @click="handleBatchUnsetMain" :disabled="selectedProducts.length === 0">
            <i class="bi bi-house-slash"></i> 取消主产品
          </button>
          <button class="btn btn-success" @click="handleBatchSetNew" :disabled="selectedProducts.length === 0">
            <i class="bi bi-bag"></i> 批量新产品
          </button>
          <button class="btn btn-outline-success" @click="handleBatchUnsetNew" :disabled="selectedProducts.length === 0">
            <i class="bi bi-bag-slash"></i> 取消新产品
          </button>
          <button class="btn btn-warning" @click="handleBatchFeatured" :disabled="selectedProducts.length === 0">
            <i class="bi bi-star"></i> 批量热门
          </button>
          <button class="btn btn-danger" @click="handleBatchDelete" :disabled="selectedProducts.length === 0">
            <i class="bi bi-trash"></i> 批量删除
          </button>
          <button class="btn btn-secondary" @click="toggleBatchMode">
            <i class="bi bi-x-lg"></i> 退出批量
          </button>
        </div>
      </div>
    </div>

    <!-- 主要内容区域 -->
    <div class="main-content">
      <!-- 左侧分类筛选 -->
      <aside class="sidebar">
        <CategoryFilter
          :products="allProducts"
          @filter-change="handleCategoryFilter"
          @category-select="handleCategorySelect"
        />
      </aside>

      <!-- 右侧产品列表 -->
      <div class="content-area">
        <!-- 搜索状态显示 -->
        <div v-if="isSearchMode" class="search-status-bar">
      <div class="search-info">
        <i class="bi bi-search"></i>
        <span class="search-label">搜索结果:</span>
        <span class="search-keyword">"{{ searchKeyword }}"</span>
        <span class="search-count">找到 {{ products.length }} 个产品</span>
        <button @click="clearSearch" class="btn-clear-search-bar">
          <i class="bi bi-x-lg"></i> 清除搜索
        </button>
      </div>
    </div>

    <!-- 产品列表 -->
    <section class="section">
      <h3 class="section-title">
        <i class="bi bi-list-ul"></i>
        {{ isSearchMode ? '搜索结果' : '产品列表' }}
      </h3>

      <div v-if="products.length > 0" class="product-list">
        <ProductCard
          v-for="(product, index) in products"
          :key="product.id"
          :product="product"
          :index="index"
          :selected="selectedProducts.includes(product.id)"
          :showBatchActions="showBatchActions"
          @edit="handleEdit"
          @delete="handleDelete"
          @preview="handlePreviewImage"
          @toggleFeatured="handleToggleFeatured"
          @toggleMain="handleToggleMain"
          @toggleNew="handleToggleNew"
          @selection-change="handleSelectionChange"
        />
      </div>

      <div v-else class="empty-state">
        <i class="bi bi-inbox"></i>
        <p>暂无产品</p>
        <small>点击"添加新产品"按钮开始添加您的产品</small>
      </div>
    </section>

    <!-- 添加/编辑产品弹窗 -->
    <ProductModal
      v-if="showModal"
      :product="editingProduct"
      @success="handleSuccess"
      @close="closeModal"
    />

    <!-- 图片预览弹窗 -->
    <ImagePreview
      v-if="previewImage"
      :image="previewImage"
      @close="previewImage = null"
    />

    <!-- Excel批量上传模态框 -->
    <ExcelBatchUploadModal
v-if="showExcelBatchUploadModal"
      @success="handleExcelBatchUploadSuccess"
      @close="showExcelBatchUploadModal = false"
    />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import ProductCard from '@/components/ProductCard.vue'
import ProductModal from '@/components/ProductModal.vue'
import ImagePreview from '@/components/ImagePreview.vue'
import ExcelBatchUploadModal from '@/components/ExcelBatchUploadModal.vue'
import CategoryFilter from '@/components/CategoryFilter.vue'
import { productAPI } from '@/api'
import Swal from 'sweetalert2'

const products = ref([])
const allProducts = ref([])  // 存储所有产品的原始数据
const showModal = ref(false)
const editingProduct = ref(null)
const previewImage = ref(null)

// 搜索相关状态
const searchKeyword = ref('')
const isSearchMode = ref(false)
const searching = ref(false)

// 批量操作相关状态
const showBatchActions = ref(false)
const selectedProducts = ref([])
const showExcelBatchUploadModal = ref(false)

// 分类筛选相关状态
const selectedCategory = ref('')
const selectedCategoryPath = ref('')

// 状态字符串到整数的映射函数
const getStatusValue = (statusStr) => {
  switch(statusStr) {
    case 'published':
      return 1
    case 'inactive':
      return 2
    case 'draft':
      return 0
    default:
      return 0  // 默认为草稿状态
  }
}

// 将图片数组转换为字符串（后端期望字符串格式）
const convertImagesToString = (images) => {
  if (!images) return ''
  if (Array.isArray(images)) {
    return images.join(',')  // 用逗号分隔图片URL
  }
  return String(images)  // 如果已经是字符串，直接返回
}

// 处理标签字段（后端期望数组格式）
const convertTagsToArray = (tags) => {
  if (!tags) return []
  if (Array.isArray(tags)) {
    return tags.filter(tag => tag && tag.trim())  // 过滤空标签
  }
  if (typeof tags === 'string') {
    if (tags.trim() === '') return []
    return tags.split(',').map(tag => tag.trim()).filter(tag => tag)  // 分割字符串并过滤空值
  }
  return []
}

// 全选状态计算属性
const isAllSelected = computed(() => {
  return products.value.length > 0 && selectedProducts.value.length === products.value.length
})

// 加载数据
onMounted(() => {
  loadProducts()
})

// 加载产品列表
const loadProducts = async () => {
  try {
    console.log('🔄 开始重新加载产品列表...')

    // 强制清除可能的缓存
    const cacheBuster = Math.random().toString(36).substring(7)
    const timestamp = Date.now()

    const response = await productAPI.getProducts({
      page: 0,
      size: 100,  // 获取前100个产品
      timestamp: timestamp,
      cacheBuster: cacheBuster  // 添加随机缓存破坏器
    })

    console.log('📦 API响应数据:', response)
    console.log('🕐 请求时间戳:', timestamp, '缓存破坏器:', cacheBuster)

    if (response.data && response.data.content) {
      // 转换后端数据为前端格式
      const newProducts = response.data.content.map(convertProductFromBackend)
      console.log('🔄 转换后的产品数据:', newProducts)

      // 强制更新响应式数据 - 多重确保
      products.value = []
      allProducts.value = []
      await new Promise(resolve => setTimeout(resolve, 50))  // 增加延迟确保DOM更新
      products.value = newProducts
      allProducts.value = newProducts
      await new Promise(resolve => setTimeout(resolve, 10))  // 再次确保

      console.log('✅ 产品列表更新成功，产品数量:', products.value.length)

      // 验证更新是否成功
      if (newProducts.length > 0 && newProducts[0].tags) {
        console.log('🏷️ 标签数据验证 - 第一个产品标签:', newProducts[0].tags)
      }
    } else {
      console.warn('⚠️ API响应数据格式异常:', response.data)
      products.value = []
      allProducts.value = []
    }
  } catch (error) {
    console.error('❌ 加载产品失败:', error)
    Swal.fire({
      title: '加载失败',
      text: '加载产品列表失败，请刷新重试',
      icon: 'error',
      confirmButtonText: '确定',
      confirmButtonColor: '#1976d2'
    })
  }
}

// 转换后端数据为前端格式 - 包含所有字段
const convertProductFromBackend = (product) => {
  console.log('🔄 转换后端产品数据，原始数据:', product)
  console.log('🏷️ 后端数据中的标签字段:', product.tags, '类型:', typeof product.tags, '是否为数组:', Array.isArray(product.tags))

  const converted = {
    // 基本显示字段
    id: product.id,
    name: product.productName,           // productName → name
    category: product.category || '未分类',
    categoryPath: product.category || '未分类',
    image: product.mainImage || '',      // mainImage → image
    description: product.description,
    status: convertStatusToFrontend(product.status),  // 整数 → 字符串
    createdAt: product.createTime ? new Date(product.createTime) : new Date(),

    // 编辑需要的完整字段 - 直接映射所有后端字段
    productName: product.productName || '',
    productCode: product.productCode || '',
    specifications: product.specifications || '',
    mainImage: product.mainImage || '',
    productImages: product.productImages || [],
    videoPath: product.videoPath || '',
    price: product.price || 0,
    marketPrice: product.marketPrice || 0,
    stockQuantity: product.stockQuantity || 0,
    // 标签处理将在后面统一处理
    isFeatured: product.isFeatured || false,
    sortOrder: product.sortOrder || 0,
    seoKeywords: product.seoKeywords || '',
    seoDescription: product.seoDescription || '',

    // 服装材质属性
    material: product.material || '',
    fabricType: product.fabricType || '',
    fabricWeight: product.fabricWeight || '',

    // 服装款式属性
    waistStyle: product.waistStyle || '',
    jeansStyle: product.jeansStyle || '',
    patternFit: product.patternFit || '',
    style: product.style || '',

    // 工艺制造属性
    craftsmanship: product.craftsmanship || '',
    weavingMethod: product.weavingMethod || '',
    washingProcess: product.washingProcess || '',
    needleDetectionProcess: product.needleDetectionProcess || '',

    // 图案设计属性
    patternType: product.patternType || '',
    printingMethod: product.printingMethod || '',
    fashionElements: product.fashionElements || '',
    logoPosition: product.logoPosition || '',

    // 供应商业属性
    supplyType: product.supplyType || '',
    origin: product.origin || '',
    fastSampling: product.fastSampling || false,

    // 规格包装属性
    model: product.model || '',
    season: product.season || '',
    salesUnit: product.salesUnit || '',

    // 标签处理 - 使用新的转换函数
    tags: convertTagsFromBackend(product.tags)
  }

  console.log('✅ 转换完成，前端数据:', converted)
  console.log('🏷️ 转换后的标签字段:', converted.tags, '类型:', typeof converted.tags, '是否为数组:', Array.isArray(converted.tags))
  return converted
}

// 状态转换：后端整数 → 前端字符串
const convertStatusToFrontend = (status) => {
  const statusMap = {
    0: 'inactive',   // 下架
    1: 'published',  // 上架
    2: 'draft'       // 草稿
  }
  return statusMap[status] || 'draft'
}

// 标签转换：后端格式 → 前端逗号分隔文本格式
const convertTagsFromBackend = (tags) => {
  if (!tags) {
    return ''
  }

  if (Array.isArray(tags)) {
    // 如果后端返回数组，转换为逗号分隔的字符串
    return tags.join(', ')
  } else if (typeof tags === 'string') {
    // 如果已经是字符串，直接使用
    return tags
  } else {
    // 其他情况返回空字符串
    return ''
  }
}

// 显示添加弹窗
const showAddModal = () => {
  editingProduct.value = {
    // 预填充选中的分类
    category: selectedCategoryPath.value || '',
    categoryPath: selectedCategoryPath.value || '',
    // 其他字段保持默认值或为空
    productName: '',
    productCode: '',
    specifications: '',
    mainImage: '',
    productImages: [],
    videoPath: '',
    price: 0,
    marketPrice: 0,
    stockQuantity: 0,
    tags: '',
    isFeatured: false,
    sortOrder: 0,
    seoKeywords: '',
    seoDescription: '',
    // 服装材质属性
    material: '',
    fabricType: '',
    fabricWeight: '',
    // 服装款式属性
    waistStyle: '',
    jeansStyle: '',
    patternFit: '',
    style: '',
    // 工艺制造属性
    craftsmanship: '',
    weavingMethod: '',
    washingProcess: '',
    needleDetectionProcess: '',
    // 图案设计属性
    patternType: '',
    printingMethod: '',
    fashionElements: '',
    logoPosition: '',
    // 供应商业属性
    supplyType: '',
    origin: '',
    fastSampling: false,
    // 规格包装属性
    model: '',
    season: '',
    salesUnit: ''
  }
  showModal.value = true
}

// 处理编辑
const handleEdit = (product) => {
  console.log('🔍 点击编辑按钮，产品数据:', product)
  console.log('🏷️ 编辑时产品的标签字段:', product.tags, '类型:', typeof product.tags, '是否为数组:', Array.isArray(product.tags))

  // 确保传递完整的产品数据，包含所有字段
  const fullProductData = {
    ...product,
    // 确保所有编辑需要的字段都存在
    productName: product.productName || product.name || '',
    productCode: product.productCode || '',
    specifications: product.specifications || '',
    mainImage: product.mainImage || product.image || '',
    productImages: product.productImages || [],
    videoPath: product.videoPath || '',
    price: product.price || 0,
    marketPrice: product.marketPrice || 0,
    stockQuantity: product.stockQuantity || 0,
    tags: product.tags || '',  // 保持当前格式（应该已经是逗号分隔格式）
    isFeatured: product.isFeatured || false,
    sortOrder: product.sortOrder || 0,
    seoKeywords: product.seoKeywords || '',
    seoDescription: product.seoDescription || '',

    // 服装材质属性
    material: product.material || '',
    fabricType: product.fabricType || '',
    fabricWeight: product.fabricWeight || '',

    // 服装款式属性
    waistStyle: product.waistStyle || '',
    jeansStyle: product.jeansStyle || '',
    patternFit: product.patternFit || '',
    style: product.style || '',

    // 工艺制造属性
    craftsmanship: product.craftsmanship || '',
    weavingMethod: product.weavingMethod || '',
    washingProcess: product.washingProcess || '',
    needleDetectionProcess: product.needleDetectionProcess || '',

    // 图案设计属性
    patternType: product.patternType || '',
    printingMethod: product.printingMethod || '',
    fashionElements: product.fashionElements || '',
    logoPosition: product.logoPosition || '',

    // 供应商业属性
    supplyType: product.supplyType || '',
    origin: product.origin || '',
    fastSampling: product.fastSampling || false,

    // 规格包装属性
    model: product.model || '',
    season: product.season || '',
    salesUnit: product.salesUnit || ''
  }

  console.log('📤 传递给ProductModal的完整数据:', fullProductData)
  console.log('🏷️ 传递给ProductModal的标签字段:', fullProductData.tags, '类型:', typeof fullProductData.tags, '是否为数组:', Array.isArray(fullProductData.tags))
  editingProduct.value = fullProductData
  showModal.value = true
}

// 处理删除
const handleDelete = async (id) => {
  const result = await Swal.fire({
    title: '温馨提示',
    text: '确定要删除这个产品吗？删除后无法恢复。',
    icon: 'question',
    showCancelButton: true,
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    confirmButtonColor: '#1976d2',
    cancelButtonColor: '#6c757d'
  })
  
  if (!result.isConfirmed) return

  try {
    await productAPI.deleteProduct(id)
    products.value = products.value.filter(p => p.id !== id)
    Swal.fire({
      title: '删除成功',
      icon: 'success',
      confirmButtonText: '确定',
      confirmButtonColor: '#1976d2',
      timer: 1500
    })
  } catch (error) {
    console.error('删除失败:', error)
    Swal.fire({
      title: '删除失败',
      text: error.response?.data?.message || error.message,
      icon: 'error',
      confirmButtonText: '确定',
      confirmButtonColor: '#1976d2'
    })
  }
}

// 处理热门推荐切换
const handleToggleFeatured = async (productId, isFeatured) => {
  const action = isFeatured ? '设置为' : '取消'
  const result = await Swal.fire({
    title: '确认操作',
    text: `确定要${action}热门推荐吗？`,
    icon: 'question',
    showCancelButton: true,
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    confirmButtonColor: isFeatured ? '#ffc107' : '#6c757d',
    cancelButtonColor: '#6c757d'
  })

  if (!result.isConfirmed) return

  try {
    // 获取当前产品数据，然后更新特定字段
    const currentProduct = products.value.find(p => p.id === productId)
    if (!currentProduct) {
      throw new Error('未找到要更新的产品')
    }

    const updatedProduct = {
      ...currentProduct,
      isFeatured,
      status: typeof currentProduct.status === 'string' ? getStatusValue(currentProduct.status) : currentProduct.status,
      productImages: convertImagesToString(currentProduct.images || currentProduct.productImages),
      tags: convertTagsToArray(currentProduct.tags)
    }
    const response = await productAPI.updateProduct(productId, updatedProduct)
    console.log('✅ 热门推荐设置成功:', response)

    // 更新本地数据
    const productIndex = products.value.findIndex(p => p.id === productId)
    if (productIndex !== -1) {
      products.value[productIndex].isFeatured = isFeatured
    }

    Swal.fire({
      title: '操作成功',
      text: `已${action}热门推荐`,
      icon: 'success',
      confirmButtonText: '确定',
      confirmButtonColor: '#1976d2',
      timer: 1500
    })
  } catch (error) {
    console.error('设置热门推荐失败:', error)
    Swal.fire({
      title: '操作失败',
      text: error.response?.data?.message || error.message,
      icon: 'error',
      confirmButtonText: '确定',
      confirmButtonColor: '#1976d2'
    })
  }
}

// 处理主产品切换
const handleToggleMain = async (productId, isMain) => {
  const action = isMain ? '设置为主产品' : '取消主产品'
  const result = await Swal.fire({
    title: '确认操作',
    text: `确定要${action}吗？`,
    icon: 'question',
    showCancelButton: true,
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    confirmButtonColor: isMain ? '#007bff' : '#6c757d',
    cancelButtonColor: '#6c757d'
  })

  if (!result.isConfirmed) return

  try {
    // 获取当前产品数据，然后更新特定字段
    const currentProduct = products.value.find(p => p.id === productId)
    if (!currentProduct) {
      throw new Error('未找到要更新的产品')
    }

    const updatedProduct = {
      ...currentProduct,
      isMain,
      status: typeof currentProduct.status === 'string' ? getStatusValue(currentProduct.status) : currentProduct.status,
      productImages: convertImagesToString(currentProduct.images || currentProduct.productImages),
      tags: convertTagsToArray(currentProduct.tags)
    }
    const response = await productAPI.updateProduct(productId, updatedProduct)
    console.log('✅ 主产品设置成功:', response)

    // 更新本地数据
    const productIndex = products.value.findIndex(p => p.id === productId)
    if (productIndex !== -1) {
      products.value[productIndex].isMain = isMain
    }

    Swal.fire({
      title: '操作成功',
      text: `已${action}`,
      icon: 'success',
      confirmButtonText: '确定',
      confirmButtonColor: '#1976d2',
      timer: 1500
    })
  } catch (error) {
    console.error('设置主产品失败:', error)
    Swal.fire({
      title: '操作失败',
      text: error.response?.data?.message || error.message,
      icon: 'error',
      confirmButtonText: '确定',
      confirmButtonColor: '#1976d2'
    })
  }
}

// 处理新产品切换
const handleToggleNew = async (productId, isNew) => {
  const action = isNew ? '设置为新产品' : '取消新产品'
  const result = await Swal.fire({
    title: '确认操作',
    text: `确定要${action}吗？`,
    icon: 'question',
    showCancelButton: true,
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    confirmButtonColor: isNew ? '#28a745' : '#6c757d',
    cancelButtonColor: '#6c757d'
  })

  if (!result.isConfirmed) return

  try {
    // 获取当前产品数据，然后更新特定字段
    const currentProduct = products.value.find(p => p.id === productId)
    if (!currentProduct) {
      throw new Error('未找到要更新的产品')
    }

    const updatedProduct = {
      ...currentProduct,
      isNew,
      status: typeof currentProduct.status === 'string' ? getStatusValue(currentProduct.status) : currentProduct.status,
      productImages: convertImagesToString(currentProduct.images || currentProduct.productImages),
      tags: convertTagsToArray(currentProduct.tags)
    }
    const response = await productAPI.updateProduct(productId, updatedProduct)
    console.log('✅ 新产品设置成功:', response)

    // 更新本地数据
    const productIndex = products.value.findIndex(p => p.id === productId)
    if (productIndex !== -1) {
      products.value[productIndex].isNew = isNew
    }

    Swal.fire({
      title: '操作成功',
      text: `已${action}`,
      icon: 'success',
      confirmButtonText: '确定',
      confirmButtonColor: '#1976d2',
      timer: 1500
    })
  } catch (error) {
    console.error('设置新产品失败:', error)
    Swal.fire({
      title: '操作失败',
      text: error.response?.data?.message || error.message,
      icon: 'error',
      confirmButtonText: '确定',
      confirmButtonColor: '#1976d2'
    })
  }
}

// 处理操作成功（从ProductModal传来的成功事件）
const handleSuccess = async (updatedProduct) => {
  console.log('📢 收到ProductModal的成功事件，开始更新数据')
  console.log('📦 收到的更新产品数据:', updatedProduct)

  try {
    // 如果有返回的产品数据，直接更新对应的项
    if (updatedProduct) {
      console.log('🔄 使用返回的产品数据更新本地状态，原始数据:', updatedProduct)
      const index = products.value.findIndex(p => p.id === updatedProduct.id)
      if (index !== -1) {
        const convertedProduct = convertProductFromBackend(updatedProduct)
        products.value[index] = convertedProduct
        console.log('✅ 本地产品数据更新成功，转换后的数据:', convertedProduct)

        // 验证关键字段是否有值
        console.log('🔍 验证关键字段:', {
          price: convertedProduct.price,
          material: convertedProduct.material,
          style: convertedProduct.style,
          craftsmanship: convertedProduct.craftsmanship
        })
        return
      }
    }

    // 如果没有返回数据或找不到对应项，则重新加载列表
    console.log('🔄 重新加载产品列表以获取最新数据')
    await loadProducts()
    console.log('✅ 产品列表重新加载完成，产品数量:', products.value.length)
  } catch (error) {
    console.error('❌ 数据更新失败:', error)
    Swal.fire({
      title: '数据同步失败',
      text: '产品保存成功，但页面数据更新失败，请手动刷新页面',
      icon: 'warning',
      confirmButtonText: '确定',
      confirmButtonColor: '#1976d2'
    })
  }
}

// 处理保存（备用方法，保留以防其他地方使用）
const handleSave = async (productData) => {
  try {
    if (productData.id) {
      // 编辑
      const response = await productAPI.updateProduct(productData.id, productData)

      if (response.data) {
        console.log('✅ 产品更新成功，后端返回数据:', response.data)
        console.log('🏷️ 后端返回的标签字段:', response.data.tags, '类型:', typeof response.data.tags, '是否为数组:', Array.isArray(response.data.tags))

        // 更新本地列表
        const index = products.value.findIndex(p => p.id === productData.id)
        if (index !== -1) {
          const updatedProduct = convertProductFromBackend(response.data)
          console.log('🔄 更新本地产品列表，标签:', updatedProduct.tags)
          products.value[index] = updatedProduct
        }

        // 重新加载整个产品列表以确保数据一致性
        console.log('🔄 重新加载产品列表以确保数据一致性')
        await loadProducts()
        Swal.fire({
          title: '更新成功',
          icon: 'success',
          confirmButtonText: '确定',
          confirmButtonColor: '#1976d2',
          timer: 1500
        })
      }
    } else {
      // 新增
      const response = await productAPI.createProduct(productData)

      if (response.data) {
        // 添加到本地列表
        products.value.push(convertProductFromBackend(response.data))
        Swal.fire({
          title: '添加成功',
          icon: 'success',
          confirmButtonText: '确定',
          confirmButtonColor: '#1976d2',
          timer: 1500
        })
      }
    }

    closeModal()
  } catch (error) {
    console.error('保存失败:', error)
    Swal.fire({
      title: '保存失败',
      text: error.response?.data?.message || error.message,
      icon: 'error',
      confirmButtonText: '确定',
      confirmButtonColor: '#1976d2'
    })
  }
}

// 关闭弹窗
const closeModal = () => {
  console.log('🔒 关闭产品编辑弹窗')
  showModal.value = false
  editingProduct.value = null

  // 如果当前在搜索模式，重新执行搜索以获取最新数据
  if (isSearchMode.value && searchKeyword.value.trim()) {
    console.log('🔍 重新执行搜索以更新搜索结果')
    performSearch()
  }
}

// 预览图片
const handlePreviewImage = (image) => {
  previewImage.value = image
}

// 执行搜索
const performSearch = async () => {
  const keyword = searchKeyword.value.trim()

  if (!keyword) {
    clearSearch()
    return
  }

  searching.value = true

  try {
    // 使用5.12搜索产品接口
    const response = await productAPI.searchProducts(keyword)

    if (response.data) {
      // 处理搜索结果格式（可能是数组或包装对象）
      if (Array.isArray(response.data)) {
        products.value = response.data.map(convertProductFromBackend)
      } else if (response.data.content && Array.isArray(response.data.content)) {
        // 处理分页格式
        products.value = response.data.content.map(convertProductFromBackend)
      } else {
        // 单个产品对象
        products.value = [convertProductFromBackend(response.data)]
      }
      isSearchMode.value = true
    } else {
      products.value = []
      isSearchMode.value = true
    }
  } catch (error) {
    console.error('搜索失败:', error)
    Swal.fire({
      title: '搜索失败',
      text: error.response?.data?.message || error.message,
      icon: 'error',
      confirmButtonText: '确定',
      confirmButtonColor: '#1976d2'
    })
    products.value = []
    isSearchMode.value = true
  } finally {
    searching.value = false
  }
}

// 清除搜索
const clearSearch = () => {
  searchKeyword.value = ''
  isSearchMode.value = false
  searching.value = false
  loadProducts() // 重新加载完整产品列表
}

// ============ 批量操作功能 ============

// 切换批量操作模式
const toggleBatchMode = () => {
  showBatchActions.value = !showBatchActions.value
  if (!showBatchActions.value) {
    selectedProducts.value = []
  }
}

// 全选/取消全选
const handleSelectAll = (event) => {
  if (event.target.checked) {
    selectedProducts.value = products.value.map(p => p.id)
  } else {
    selectedProducts.value = []
  }
}

// 单个产品选择
const handleSelectionChange = (productId, isSelected) => {
  if (isSelected) {
    if (!selectedProducts.value.includes(productId)) {
      selectedProducts.value.push(productId)
    }
  } else {
    const index = selectedProducts.value.indexOf(productId)
    if (index > -1) {
      selectedProducts.value.splice(index, 1)
    }
  }
}

// Excel批量上传
const handleExcelBatchUpload = () => {
  showExcelBatchUploadModal.value = true
}

// Excel批量上传成功
const handleExcelBatchUploadSuccess = async (results) => {
  console.log('🎯🎯🎯 Excel批量上传完成:', results)

  // 先关闭上传窗口
  showExcelBatchUploadModal.value = false

  // 显示成功提示（带确认按钮）
  if (results.successCount > 0) {
    await Swal.fire({
      title: '上传成功',
      html: `成功上传 <strong>${results.successCount}</strong> 个产品！`,
      icon: 'success',
      confirmButtonText: '确定',
      confirmButtonColor: '#1976d2'
    })
  } else {
    await Swal.fire({
      title: '上传完成',
      html: `上传完成，成功 <strong>${results.successCount}</strong> 个，失败 <strong>${results.failureCount}</strong> 个`,
      icon: results.failureCount === 0 ? 'success' : 'warning',
      confirmButtonText: '确定',
      confirmButtonColor: '#1976d2'
    })
  }

  // 用户点击确认按钮后刷新列表
  console.log('🔄 用户点击确认，开始刷新产品列表...')
  await loadProducts()
  console.log('✅ 产品列表刷新完成')
}

// 批量上架
const handleBatchActivate = async () => {
  if (selectedProducts.value.length === 0) {
    Swal.fire({
      title: '提示',
      text: '请先选择要上架的产品',
      icon: 'warning',
      confirmButtonText: '确定',
      confirmButtonColor: '#1976d2'
    })
    return
  }

  const result = await Swal.fire({
    title: '批量上架确认',
    html: `确定要上架选中的 <strong>${selectedProducts.value.length}</strong> 个产品吗？`,
    icon: 'info',
    showCancelButton: true,
    confirmButtonText: '确定上架',
    cancelButtonText: '取消',
    confirmButtonColor: '#28a745'
  })

  if (result.isConfirmed) {
    try {
      await productAPI.batchActivateProducts(selectedProducts.value)

      await Swal.fire({
        title: '上架成功',
        text: `成功上架 ${selectedProducts.value.length} 个产品`,
        icon: 'success',
        confirmButtonText: '确定',
        confirmButtonColor: '#1976d2'
      })

      selectedProducts.value = []
      await loadProducts()
    } catch (error) {
      Swal.fire({
        title: '上架失败',
        text: error.response?.data?.message || error.message,
        icon: 'error',
        confirmButtonText: '确定',
        confirmButtonColor: '#1976d2'
      })
    }
  }
}

// 批量下架
const handleBatchDeactivate = async () => {
  if (selectedProducts.value.length === 0) {
    Swal.fire({
      title: '提示',
      text: '请先选择要下架的产品',
      icon: 'warning',
      confirmButtonText: '确定',
      confirmButtonColor: '#1976d2'
    })
    return
  }

  const result = await Swal.fire({
    title: '批量下架确认',
    html: `确定要下架选中的 <strong>${selectedProducts.value.length}</strong> 个产品吗？`,
    icon: 'warning',
    showCancelButton: true,
    confirmButtonText: '确定下架',
    cancelButtonText: '取消',
    confirmButtonColor: '#ffc107'
  })

  if (result.isConfirmed) {
    try {
      await productAPI.batchDeactivateProducts(selectedProducts.value)

      await Swal.fire({
        title: '下架成功',
        text: `成功下架 ${selectedProducts.value.length} 个产品`,
        icon: 'success',
        confirmButtonText: '确定',
        confirmButtonColor: '#1976d2'
      })

      selectedProducts.value = []
      await loadProducts()
    } catch (error) {
      Swal.fire({
        title: '下架失败',
        text: error.response?.data?.message || error.message,
        icon: 'error',
        confirmButtonText: '确定',
        confirmButtonColor: '#1976d2'
      })
    }
  }
}

// 批量保存为草稿
const handleBatchSaveAsDraft = async () => {
  if (selectedProducts.value.length === 0) {
    Swal.fire({
      title: '提示',
      text: '请先选择要保存为草稿的产品',
      icon: 'warning',
      confirmButtonText: '确定',
      confirmButtonColor: '#1976d2'
    })
    return
  }

  const result = await Swal.fire({
    title: '批量保存草稿确认',
    html: `确定要将选中的 <strong>${selectedProducts.value.length}</strong> 个产品保存为草稿吗？`,
    icon: 'info',
    showCancelButton: true,
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    confirmButtonColor: '#17a2b8'
  })

  if (result.isConfirmed) {
    try {
      await productAPI.batchSaveAsDraft(selectedProducts.value)

      await Swal.fire({
        title: '保存成功',
        text: `成功将 ${selectedProducts.value.length} 个产品保存为草稿`,
        icon: 'success',
        confirmButtonText: '确定',
        confirmButtonColor: '#1976d2'
      })

      selectedProducts.value = []
      await loadProducts()
    } catch (error) {
      Swal.fire({
        title: '保存失败',
        text: error.response?.data?.message || error.message,
        icon: 'error',
        confirmButtonText: '确定',
        confirmButtonColor: '#1976d2'
      })
    }
  }
}

// 批量删除
const handleBatchDelete = async () => {
  if (selectedProducts.value.length === 0) {
    Swal.fire({
      title: '提示',
      text: '请先选择要删除的产品',
      icon: 'warning',
      confirmButtonText: '确定',
      confirmButtonColor: '#1976d2'
    })
    return
  }

  const result = await Swal.fire({
    title: '批量删除确认',
    html: `确定要删除选中的 <strong>${selectedProducts.value.length}</strong> 个产品吗？<br/><span style="color: #dc3545;">此操作不可恢复！</span>`,
    icon: 'warning',
    showCancelButton: true,
    confirmButtonText: '确定删除',
    cancelButtonText: '取消',
    confirmButtonColor: '#dc3545'
  })

  if (result.isConfirmed) {
    try {
      await productAPI.batchDeleteProducts(selectedProducts.value)

      await Swal.fire({
        title: '删除成功',
        text: `成功删除 ${selectedProducts.value.length} 个产品`,
        icon: 'success',
        confirmButtonText: '确定',
        confirmButtonColor: '#1976d2'
      })

      selectedProducts.value = []
      await loadProducts()
    } catch (error) {
      Swal.fire({
        title: '删除失败',
        text: error.response?.data?.message || error.message,
        icon: 'error',
        confirmButtonText: '确定',
        confirmButtonColor: '#1976d2'
      })
    }
  }
}

// ============ 分类筛选功能 ============

// 处理分类筛选变化
const handleCategoryFilter = (categoryPath) => {
  selectedCategoryPath.value = categoryPath

  if (!categoryPath || categoryPath === '') {
    // 如果没有选择分类，显示所有产品
    products.value = [...allProducts.value]
    return
  }

  // 分层级筛选：支持第一级、第二级、第三级分类筛选
  products.value = allProducts.value.filter(product => {
    const productCategory = product.categoryPath || product.category || ''

    // 精确匹配：当选择的是完整分类路径时
    if (productCategory === categoryPath) {
      return true
    }

    // 父分类匹配：当选择的是父分类时，包含所有子分类产品
    // 例如：选择 "Men" 时，包含 "Men > Tops > T-shirt" 等所有Men开头的分类
    if (productCategory.startsWith(categoryPath + ' > ')) {
      return true
    }

    return false
  })

  console.log(`分类筛选: ${categoryPath}, 筛选出 ${products.value.length} 个产品`)
}

// 批量设置主产品
const handleBatchSetMain = async () => {
  const result = await Swal.fire({
    title: '确认批量操作',
    text: `确定要将选中的 ${selectedProducts.value.length} 个产品设置为主产品吗？`,
    icon: 'question',
    showCancelButton: true,
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    confirmButtonColor: '#007bff',
    cancelButtonColor: '#6c757d'
  })

  if (!result.isConfirmed) return

  try {
    // 使用循环调用单个产品设置接口（因为后端没有批量接口）
    const successCount = { count: 0 }
    const errorMessages = []

    for (const productId of selectedProducts.value) {
      try {
        // 获取当前产品数据，然后更新特定字段
        const currentProduct = products.value.find(p => p.id === productId)
        if (!currentProduct) {
          throw new Error(`未找到产品ID ${productId}`)
        }

        const updatedProduct = {
          ...currentProduct,
          isMain: true,
          status: typeof currentProduct.status === 'string' ? getStatusValue(currentProduct.status) : currentProduct.status,
          productImages: convertImagesToString(currentProduct.images || currentProduct.productImages),
          tags: convertTagsToArray(currentProduct.tags)
        }
        await productAPI.updateProduct(productId, updatedProduct)
        successCount.count++
        console.log(`✅ 产品 ${productId} 设置主产品成功`)

        // 更新本地数据
        const productIndex = products.value.findIndex(p => p.id === productId)
        if (productIndex !== -1) {
          products.value[productIndex].isMain = true
        }
      } catch (error) {
        console.error(`❌ 产品 ${productId} 设置主产品失败:`, error)
        errorMessages.push(`产品 ${productId}: ${error.response?.data?.message || error.message}`)
      }
    }

    const total = selectedProducts.value.length
    const failed = total - successCount.count

    if (failed === 0) {
      Swal.fire({
        title: '操作成功',
        text: `已成功将 ${successCount.count} 个产品设置为主产品`,
        icon: 'success',
        confirmButtonText: '确定',
        confirmButtonColor: '#1976d2',
        timer: 2000
      })
    } else {
      Swal.fire({
        title: '部分成功',
        html: `
          <div>成功设置 <strong>${successCount.count}</strong> 个产品为主产品</div>
          <div style="margin-top: 10px; font-size: 12px; color: #666;">
            失败 ${failed} 个：<br>
            ${errorMessages.slice(0, 3).join('<br>')}
            ${errorMessages.length > 3 ? '<br>...' : ''}
          </div>
        `,
        icon: 'warning',
        confirmButtonText: '确定',
        confirmButtonColor: '#1976d2'
      })
    }
  } catch (error) {
    console.error('批量设置主产品失败:', error)
    Swal.fire({
      title: '操作失败',
      text: '批量设置主产品时发生未知错误',
      icon: 'error',
      confirmButtonText: '确定',
      confirmButtonColor: '#1976d2'
    })
  }
}

// 批量设置新产品
const handleBatchSetNew = async () => {
  const result = await Swal.fire({
    title: '确认批量操作',
    text: `确定要将选中的 ${selectedProducts.value.length} 个产品设置为新产品吗？`,
    icon: 'question',
    showCancelButton: true,
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    confirmButtonColor: '#28a745',
    cancelButtonColor: '#6c757d'
  })

  if (!result.isConfirmed) return

  try {
    // 使用循环调用单个产品设置接口
    const successCount = { count: 0 }
    const errorMessages = []

    for (const productId of selectedProducts.value) {
      try {
        // 获取当前产品数据，然后更新特定字段
        const currentProduct = products.value.find(p => p.id === productId)
        if (!currentProduct) {
          throw new Error(`未找到产品ID ${productId}`)
        }

        const updatedProduct = {
          ...currentProduct,
          isNew: true,
          status: typeof currentProduct.status === 'string' ? getStatusValue(currentProduct.status) : currentProduct.status,
          productImages: convertImagesToString(currentProduct.images || currentProduct.productImages),
          tags: convertTagsToArray(currentProduct.tags)
        }
        await productAPI.updateProduct(productId, updatedProduct)
        successCount.count++
        console.log(`✅ 产品 ${productId} 设置新产品成功`)

        // 更新本地数据
        const productIndex = products.value.findIndex(p => p.id === productId)
        if (productIndex !== -1) {
          products.value[productIndex].isNew = true
        }
      } catch (error) {
        console.error(`❌ 产品 ${productId} 设置新产品失败:`, error)
        errorMessages.push(`产品 ${productId}: ${error.response?.data?.message || error.message}`)
      }
    }

    const total = selectedProducts.value.length
    const failed = total - successCount.count

    if (failed === 0) {
      Swal.fire({
        title: '操作成功',
        text: `已成功将 ${successCount.count} 个产品设置为新产品`,
        icon: 'success',
        confirmButtonText: '确定',
        confirmButtonColor: '#1976d2',
        timer: 2000
      })
    } else {
      Swal.fire({
        title: '部分成功',
        html: `
          <div>成功设置 <strong>${successCount.count}</strong> 个产品为新产品</div>
          <div style="margin-top: 10px; font-size: 12px; color: #666;">
            失败 ${failed} 个：<br>
            ${errorMessages.slice(0, 3).join('<br>')}
            ${errorMessages.length > 3 ? '<br>...' : ''}
          </div>
        `,
        icon: 'warning',
        confirmButtonText: '确定',
        confirmButtonColor: '#1976d2'
      })
    }
  } catch (error) {
    console.error('批量设置新产品失败:', error)
    Swal.fire({
      title: '操作失败',
      text: '批量设置新产品时发生未知错误',
      icon: 'error',
      confirmButtonText: '确定',
      confirmButtonColor: '#1976d2'
    })
  }
}

// 批量设置热门产品
const handleBatchFeatured = async () => {
  if (selectedProducts.value.length === 0) {
    Swal.fire({
      title: '提示',
      text: '请选择要设置的热门产品',
      icon: 'warning',
      confirmButtonText: '确定',
      confirmButtonColor: '#1976d2'
    })
    return
  }

  const result = await Swal.fire({
    title: '批量设置热门产品',
    text: `确定要将 ${selectedProducts.value.length} 个产品设置为热门产品吗？`,
    icon: 'question',
    showCancelButton: true,
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    confirmButtonColor: '#ffc107',
    cancelButtonColor: '#6c757d'
  })

  if (!result.isConfirmed) return

  try {
    const results = { successCount: 0, failureCount: 0, errors: [] }

    // 使用单个产品 API 逐个调用
    for (const productId of selectedProducts.value) {
      try {
        // 获取当前产品数据，然后更新特定字段
        const currentProduct = products.value.find(p => p.id === productId)
        if (!currentProduct) {
          throw new Error(`未找到产品ID ${productId}`)
        }

        const updatedProduct = {
          ...currentProduct,
          isFeatured: true,
          status: typeof currentProduct.status === 'string' ? getStatusValue(currentProduct.status) : currentProduct.status,
          productImages: convertImagesToString(currentProduct.images || currentProduct.productImages),
          tags: convertTagsToArray(currentProduct.tags)
        }
        await productAPI.updateProduct(productId, updatedProduct)
        results.successCount++
      } catch (error) {
        results.failureCount++
        const errorMsg = error.response?.data?.message || error.message
        results.errors.push(`产品ID ${productId} 设置热门产品失败: ${errorMsg}`)
        console.error(`设置热门产品失败 - 产品ID ${productId}:`, error)
      }
    }

    // 显示结果
    if (results.successCount > 0 || results.failureCount > 0) {
      let resultText = `<div style="text-align: left;">`

      if (results.successCount > 0) {
        resultText += `<div>✅ 成功设置热门产品: ${results.successCount} 个</div>`
      }

      if (results.failureCount > 0) {
        resultText += `<div style="color: orange;">⚠️ 部分失败: ${results.failureCount} 个</div>`

        // 显示详细错误信息（最多显示 3 个）
        if (results.errors.length > 0) {
          resultText += `<div style="margin-top: 10px; font-size: 12px; color: #666;">详情:`
          const errorShowCount = Math.min(3, results.errors.length)
          for (let i = 0; i < errorShowCount; i++) {
            resultText += `<div>• ${results.errors[i]}</div>`
          }
          if (results.errors.length > 3) {
            resultText += `<div>• 还有 ${results.errors.length - 3} 个错误...</div>`
          }
          resultText += `</div>`
        }
      }

      resultText += `</div>`

      Swal.fire({
        title: '操作完成',
        html: resultText,
        icon: results.failureCount > 0 ? 'warning' : 'success',
        confirmButtonText: '确定',
        confirmButtonColor: '#1976d2'
      })
    }

    // 清空选择并重新加载数据
    selectedProducts.value = []
    await loadProducts()
  } catch (error) {
    console.error('批量设置热门产品失败:', error)
    Swal.fire({
      title: '操作失败',
      text: '批量设置热门产品时发生错误，请稍后重试',
      icon: 'error',
      confirmButtonText: '确定',
      confirmButtonColor: '#1976d2'
    })
  }
}

// 批量取消主产品
const handleBatchUnsetMain = async () => {
  if (selectedProducts.value.length === 0) {
    Swal.fire({
      title: '提示',
      text: '请选择要取消主产品标识的产品',
      icon: 'warning',
      confirmButtonText: '确定',
      confirmButtonColor: '#1976d2'
    })
    return
  }

  const result = await Swal.fire({
    title: '批量取消主产品',
    text: `确定要取消 ${selectedProducts.value.length} 个产品的主产品标识吗？`,
    icon: 'question',
    showCancelButton: true,
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    confirmButtonColor: '#1976d2',
    cancelButtonColor: '#6c757d'
  })

  if (!result.isConfirmed) return

  try {
    const results = { successCount: 0, failureCount: 0, errors: [] }

    // 使用单个产品 API 逐个调用
    for (const productId of selectedProducts.value) {
      try {
        // 获取当前产品数据，然后更新特定字段
        const currentProduct = products.value.find(p => p.id === productId)
        if (!currentProduct) {
          throw new Error(`未找到产品ID ${productId}`)
        }

        const updatedProduct = {
          ...currentProduct,
          isMain: false,
          status: typeof currentProduct.status === 'string' ? getStatusValue(currentProduct.status) : currentProduct.status,
          productImages: convertImagesToString(currentProduct.images || currentProduct.productImages),
          tags: convertTagsToArray(currentProduct.tags)
        }
        await productAPI.updateProduct(productId, updatedProduct)
        results.successCount++
      } catch (error) {
        results.failureCount++
        const errorMsg = error.response?.data?.message || error.message
        results.errors.push(`产品ID ${productId} 取消主产品标识失败: ${errorMsg}`)
        console.error(`取消主产品标识失败 - 产品ID ${productId}:`, error)
      }
    }

    // 显示结果
    if (results.successCount > 0 || results.failureCount > 0) {
      let resultText = `<div style="text-align: left;">`

      if (results.successCount > 0) {
        resultText += `<div>✅ 成功取消主产品标识: ${results.successCount} 个</div>`
      }

      if (results.failureCount > 0) {
        resultText += `<div style="color: orange;">⚠️ 部分失败: ${results.failureCount} 个</div>`

        // 显示详细错误信息（最多显示 3 个）
        if (results.errors.length > 0) {
          resultText += `<div style="margin-top: 10px; font-size: 12px; color: #666;">详情:`
          const errorShowCount = Math.min(3, results.errors.length)
          for (let i = 0; i < errorShowCount; i++) {
            resultText += `<div>• ${results.errors[i]}</div>`
          }
          if (results.errors.length > 3) {
            resultText += `<div>• 还有 ${results.errors.length - 3} 个错误...</div>`
          }
          resultText += `</div>`
        }
      }

      resultText += `</div>`

      Swal.fire({
        title: '操作完成',
        html: resultText,
        icon: results.failureCount > 0 ? 'warning' : 'success',
        confirmButtonText: '确定',
        confirmButtonColor: '#1976d2'
      })
    }

    // 清空选择并重新加载数据
    selectedProducts.value = []
    await loadProducts()
  } catch (error) {
    console.error('批量取消主产品标识失败:', error)
    Swal.fire({
      title: '操作失败',
      text: '批量取消主产品标识时发生错误，请稍后重试',
      icon: 'error',
      confirmButtonText: '确定',
      confirmButtonColor: '#1976d2'
    })
  }
}

// 批量取消新产品
const handleBatchUnsetNew = async () => {
  if (selectedProducts.value.length === 0) {
    Swal.fire({
      title: '提示',
      text: '请选择要取消新产品标识的产品',
      icon: 'warning',
      confirmButtonText: '确定',
      confirmButtonColor: '#1976d2'
    })
    return
  }

  const result = await Swal.fire({
    title: '批量取消新产品',
    text: `确定要取消 ${selectedProducts.value.length} 个产品的新产品标识吗？`,
    icon: 'question',
    showCancelButton: true,
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    confirmButtonColor: '#1976d2',
    cancelButtonColor: '#6c757d'
  })

  if (!result.isConfirmed) return

  try {
    const results = { successCount: 0, failureCount: 0, errors: [] }

    // 使用单个产品 API 逐个调用
    for (const productId of selectedProducts.value) {
      try {
        // 获取当前产品数据，然后更新特定字段
        const currentProduct = products.value.find(p => p.id === productId)
        if (!currentProduct) {
          throw new Error(`未找到产品ID ${productId}`)
        }

        const updatedProduct = {
          ...currentProduct,
          isNew: false,
          status: typeof currentProduct.status === 'string' ? getStatusValue(currentProduct.status) : currentProduct.status,
          productImages: convertImagesToString(currentProduct.images || currentProduct.productImages),
          tags: convertTagsToArray(currentProduct.tags)
        }
        await productAPI.updateProduct(productId, updatedProduct)
        results.successCount++
      } catch (error) {
        results.failureCount++
        const errorMsg = error.response?.data?.message || error.message
        results.errors.push(`产品ID ${productId} 取消新产品标识失败: ${errorMsg}`)
        console.error(`取消新产品标识失败 - 产品ID ${productId}:`, error)
      }
    }

    // 显示结果
    if (results.successCount > 0 || results.failureCount > 0) {
      let resultText = `<div style="text-align: left;">`

      if (results.successCount > 0) {
        resultText += `<div>✅ 成功取消新产品标识: ${results.successCount} 个</div>`
      }

      if (results.failureCount > 0) {
        resultText += `<div style="color: orange;">⚠️ 部分失败: ${results.failureCount} 个</div>`

        // 显示详细错误信息（最多显示 3 个）
        if (results.errors.length > 0) {
          resultText += `<div style="margin-top: 10px; font-size: 12px; color: #666;">详情:`
          const errorShowCount = Math.min(3, results.errors.length)
          for (let i = 0; i < errorShowCount; i++) {
            resultText += `<div>• ${results.errors[i]}</div>`
          }
          if (results.errors.length > 3) {
            resultText += `<div>• 还有 ${results.errors.length - 3} 个错误...</div>`
          }
          resultText += `</div>`
        }
      }

      resultText += `</div>`

      Swal.fire({
        title: '操作完成',
        html: resultText,
        icon: results.failureCount > 0 ? 'warning' : 'success',
        confirmButtonText: '确定',
        confirmButtonColor: '#1976d2'
      })
    }

    // 清空选择并重新加载数据
    selectedProducts.value = []
    await loadProducts()
  } catch (error) {
    console.error('批量取消新产品标识失败:', error)
    Swal.fire({
      title: '操作失败',
      text: '批量取消新产品标识时发生错误，请稍后重试',
      icon: 'error',
      confirmButtonText: '确定',
      confirmButtonColor: '#1976d2'
    })
  }
}

// 处理分类选择
const handleCategorySelect = (categoryPath) => {
  selectedCategoryPath.value = categoryPath

  if (!categoryPath || categoryPath === '') {
    // 如果没有选择分类，显示所有产品
    products.value = [...allProducts.value]
    return
  }

  // 分层级筛选：支持第一级、第二级、第三级分类筛选
  products.value = allProducts.value.filter(product => {
    const productCategory = product.categoryPath || product.category || ''

    // 精确匹配：当选择的是完整分类路径时
    if (productCategory === categoryPath) {
      return true
    }

    // 父分类匹配：当选择的是父分类时，包含所有子分类产品
    // 例如：选择 "Men" 时，包含 "Men > Tops > T-shirt" 等所有Men开头的分类
    if (productCategory.startsWith(categoryPath + ' > ')) {
      return true
    }

    return false
  })

  console.log(`分类选择: ${categoryPath}, 筛选出 ${products.value.length} 个产品`)
}

</script>

<style scoped>
.product-management {
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
  display: flex;
  align-items: center;
  gap: 10px;
}

.page-header h2 i {
  color: #667eea;
}

.count-badge {
  background: #667eea;
  color: white;
  font-size: 16px;
  padding: 4px 12px;
  border-radius: 12px;
  font-weight: 500;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 15px;
  flex-wrap: wrap;
}

/* 批量操作样式 */
.normal-actions,
.batch-actions {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.batch-select-info {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  background: #f5f7ff;
  border: 2px solid #667eea;
  border-radius: 8px;
  font-weight: 500;
  color: #667eea;
}

.batch-select-info .form-check-input {
  width: 18px;
  height: 18px;
  cursor: pointer;
  border: 2px solid #667eea;
}

.batch-select-info .form-check-input:checked {
  background-color: #667eea;
  border-color: #667eea;
}

.batch-actions .btn {
  white-space: nowrap;
}

.batch-actions .btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.search-container {
  flex: 1;
  min-width: 250px;
  max-width: 400px;
}

.search-box {
  position: relative;
  display: flex;
  align-items: center;
}

.search-input {
  width: 100%;
  padding: 8px 40px 8px 15px;
  border: 2px solid #e0e0e0;
  border-radius: 25px;
  font-size: 14px;
  transition: all 0.3s;
  background: white;
}

.search-input:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 0.2rem rgba(102, 126, 234, 0.15);
}

.search-input::placeholder {
  color: #999;
}

.btn-search {
  position: absolute;
  right: 5px;
  background: none;
  border: none;
  color: #667eea;
  cursor: pointer;
  padding: 6px;
  border-radius: 50%;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  justify-content: center;
}

.btn-search:hover:not(:disabled) {
  background: #f0f4ff;
  color: #4a5bc5;
}

.btn-search:disabled {
  color: #ccc;
  cursor: not-allowed;
}

.btn-clear-search {
  position: absolute;
  right: 35px;
  background: none;
  border: none;
  color: #999;
  cursor: pointer;
  padding: 4px;
  border-radius: 50%;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  justify-content: center;
}

.btn-clear-search:hover {
  background: #f5f5f5;
  color: #666;
}

.search-status-bar {
  background: #e3f2fd;
  color: #1976d2;
  padding: 15px 20px;
  border-radius: 12px;
  margin-bottom: 20px;
  animation: slideDown 0.3s ease-in-out;
  border-left: 4px solid #1976d2;
}

.search-info {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.search-info i {
  font-size: 18px;
}

.search-label {
  font-weight: 500;
  opacity: 0.9;
}

.search-keyword {
  background: rgba(255, 255, 255, 0.2);
  padding: 3px 8px;
  border-radius: 4px;
  font-weight: 600;
  font-family: monospace;
}

.search-count {
  margin-left: 8px;
  opacity: 0.8;
  font-size: 14px;
}

.btn-clear-search-bar {
  background: rgba(25, 118, 210, 0.1);
  border: 1px solid rgba(25, 118, 210, 0.3);
  color: #1976d2;
  padding: 5px 12px;
  border-radius: 6px;
  cursor: pointer;
  font-size: 12px;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  gap: 5px;
  margin-left: auto;
}

.btn-clear-search-bar:hover {
  background: rgba(25, 118, 210, 0.2);
  border-color: rgba(25, 118, 210, 0.5);
  transform: translateY(-1px);
  box-shadow: 0 2px 4px rgba(25, 118, 210, 0.2);
}

@keyframes slideDown {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.section {
  background: white;
  border-radius: 12px;
  padding: 25px;
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

.product-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.empty-state {
  text-align: center;
  padding: 80px 20px;
  color: #999;
}

.empty-state i {
  font-size: 80px;
  margin-bottom: 20px;
  display: block;
  color: #ddd;
}

.empty-state p {
  margin: 0 0 10px 0;
  font-size: 20px;
  color: #666;
}

.empty-state small {
  font-size: 14px;
  color: #999;
}

@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    gap: 15px;
    align-items: flex-start;
  }

  .header-actions {
    width: 100%;
    flex-direction: column;
    align-items: stretch;
  }

  .search-container {
    order: -1;
    min-width: auto;
    max-width: none;
  }

  .header-actions .btn {
    width: 100%;
    justify-content: center;
  }

  .search-info {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }

  .btn-clear-search-bar {
    margin-left: 0;
    align-self: flex-end;
  }
}

@media (max-width: 480px) {
  .search-input {
    font-size: 13px;
    padding: 6px 35px 6px 12px;
  }

  .btn-search {
    right: 3px;
    padding: 4px;
  }

  .btn-clear-search {
    right: 30px;
    padding: 3px;
  }

  .search-status-bar {
    padding: 12px 15px;
  }
}

/* 侧边栏布局样式 */
.main-content {
  display: flex;
  gap: 25px;
  align-items: flex-start;
}

.sidebar {
  width: 320px;
  flex-shrink: 0;
}

.content-area {
  flex: 1;
  min-width: 0; /* 防止内容溢出 */
}

/* 侧边栏响应式布局 */
@media (max-width: 1200px) {
  .main-content {
    flex-direction: column;
  }

  .sidebar {
    width: 100%;
  }
}

@media (max-width: 768px) {
  .page-header {
    flex-direction: column;
    gap: 15px;
    align-items: flex-start;
  }

  .header-actions {
    width: 100%;
    flex-direction: column;
    align-items: stretch;
  }

  .search-container {
    order: -1;
    min-width: auto;
    max-width: none;
  }

  .header-actions .btn {
    width: 100%;
    justify-content: center;
  }

  .search-info {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }

  .btn-clear-search-bar {
    margin-left: 0;
    align-self: flex-end;
  }

  .main-content {
    gap: 20px;
  }
}
</style>
