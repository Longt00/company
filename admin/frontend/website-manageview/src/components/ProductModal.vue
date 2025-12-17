<template>
  <!-- Cache buster: {{ Date.now() }} -->
  <div class="modal-overlay" @click="$emit('close')">
    <div class="modal-content enhanced" @click.stop>
      <div class="modal-header blue-white-bg">
        <h4>{{ isEdit ? '编辑产品' : '添加产品' }}</h4>
        <button class="btn-close" @click="$emit('close')">
          <i class="bi bi-x-lg"></i>
        </button>
      </div>

      <div class="modal-body">
        <form @submit.prevent="handleSubmit">
          <!-- 产品信息选项卡 -->
          <div class="form-tabs">
            <button
              type="button"
              class="tab-btn"
              :class="{ active: activeTab === 'basic' }"
              @click="activeTab = 'basic'"
            >
              <i class="bi bi-info-circle"></i> 产品基本信息
            </button>
            <button
              type="button"
              class="tab-btn"
              :class="{ active: activeTab === 'category' }"
              @click="activeTab = 'category'"
            >
              <i class="bi bi-tags"></i> 分类与状态
            </button>
            <button
              type="button"
              class="tab-btn"
              :class="{ active: activeTab === 'attributes' }"
              @click="activeTab = 'attributes'"
            >
              <i class="bi bi-box-seam"></i> 产品属性
            </button>
            <button
              type="button"
              class="tab-btn"
              :class="{ active: activeTab === 'media' }"
              @click="activeTab = 'media'"
            >
              <i class="bi bi-camera-video"></i> 媒体资源
            </button>
          </div>

          <!-- 产品基本信息标签页 -->
          <div v-show="activeTab === 'basic'" class="tab-content">
            <!-- 产品名称 -->
            <div class="form-group">
              <label class="form-label required">产品名称</label>
              <input
                type="text"
                class="form-control"
                v-model="form.productName"
                placeholder="请输入产品名称"
                maxlength="200"
                required
              >
              <small class="form-text">{{ form.productName.length }}/200</small>
            </div>

            <!-- 产品描述 -->
            <div class="form-group">
              <label class="form-label required">产品描述</label>
              <textarea
                class="form-control"
                v-model="form.description"
                placeholder="请输入产品描述"
                rows="6"
                maxlength="2000"
                required
              ></textarea>
              <small class="form-text">{{ form.description.length }}/2000</small>
            </div>
          </div>

          <!-- 分类与状态标签页 -->
          <div v-show="activeTab === 'category'" class="tab-content">
            <!-- 产品分类选择 -->
            <div class="form-group">
              <CategorySelectorAdvanced
                v-model="form.categoryPath"
                label="产品分类"
                :required="false"
                help-text="从预设分类中选择或创建自定义分类"
                @change="handleCategoryChange"
              />
            </div>

            <!-- 显示分类路径 -->
            <div v-if="form.categoryPath" class="form-group">
              <label class="form-label">分类路径</label>
              <div class="category-display">
                <i class="bi bi-tags"></i>
                {{ form.categoryPath }}
              </div>
            </div>

            <!-- 产品状态 -->
            <div class="form-group">
              <label class="form-label">产品状态</label>
              <select class="form-control" v-model.number="form.status">
                <option value="2">草稿</option>
                <option value="1">上架</option>
                <option value="0">下架</option>
              </select>
              <small class="form-text text-muted">选择产品发布状态</small>
            </div>
          </div>

          <!-- 产品属性标签页 -->
          <div v-show="activeTab === 'attributes'" class="tab-content">
            <div class="attributes-container">
              <h6 class="section-title">
                <i class="bi bi-box-seam"></i> 产品属性设置
              </h6>

              <!-- 第一行：季节、款号、重量 -->
              <div class="form-row">
                <div class="form-group col-md-4">
                  <label class="form-label">季节</label>
                  <input
                    type="text"
                    class="form-control"
                    v-model="form.season" 
                    placeholder="如：春夏、秋冬、四季"
                    maxlength="100"
                  >
                </div>
                <div class="form-group col-md-4">
                  <label class="form-label">款号</label>
                  <input
                    type="text"
                    class="form-control"
                    v-model="form.material"
                    placeholder="如：NY2024001"
                    maxlength="100"
                  >
                </div>
                <div class="form-group col-md-4">
                  <label class="form-label">重量</label>
                  <input
                    type="text"
                    class="form-control"
                    v-model="form.style"
                    placeholder="如：180g、220g"
                    maxlength="100"
                  >
                </div>
              </div>

              <!-- 第二行：成分、供应类型、原产地 --> <!-- 新增原产地输入框 -->
              <div class="form-row">
                <div class="form-group col-md-4">
                  <label class="form-label">成分</label>
                  <input
                    type="text"
                    class="form-control"
                    v-model="form.fabricType"
                    placeholder="如：牛仔布、棉布、丝绸"
                    maxlength="100"
                  >
                </div>
                <div class="form-group col-md-4">
                  <label class="form-label">Supply Type</label>
                  <select class="form-control" v-model="form.supplyType">
                    <option value="">Select Supply Type</option>
                    <option value="OEM Services">OEM Services</option>
                    <option value="In Stock Supply">In Stock Supply</option>
                    <option value="Custom Supply">Custom Supply</option>
                    <option value="Wholesale Supply">Wholesale Supply</option>
                  </select>
                  
                </div>
              </div>

              <!-- 第三行：印花方法、徽标位置 -->
              <div class="form-row">
                <div class="form-group col-md-6">
                  <label class="form-label">印花方法</label>
                  <input
                    type="text"
                    class="form-control"
                    v-model="form.printingMethod"
                    placeholder="如：数码印花、丝印"
                    maxlength="100"
                  >
                </div>
                <div class="form-group col-md-6">
                  <label class="form-label">徽标位置</label>
                  <input
                    type="text"
                    class="form-control"
                    v-model="form.logoPosition"
                    placeholder="默认: 无"
                    maxlength="100"
                  >
                  <small class="form-text text-muted">留空表示无徽标</small>
                </div>
              </div>

              <!-- 第四行：产品特性、尺寸 -->
              <div class="form-row">
                <div class="form-group col-md-6">
                  <label class="form-label">产品特性</label>
                  <textarea
                    class="form-control"
                    v-model="form.fashionElements"
                    placeholder="如：破洞、刺绣、撞色、磨白等"
                    rows="2"
                    maxlength="200"
                  ></textarea>
                  <small class="form-text">描述产品的特色和卖点</small>
                </div>
                <div class="form-group col-md-6">
                  <label class="form-label">尺寸</label>
                  <input
                    type="text"
                    class="form-control"
                    v-model="form.model"
                    placeholder="如：S/M/L/XL/XXL 或 均码"
                    maxlength="50"
                  >
                  <small class="form-text">可用的尺码规格</small>
                </div>
              </div>
            </div>
          </div>

          <!-- 媒体资源标签页 -->
          <div v-show="activeTab === 'media'" class="tab-content">
            <!-- 产品图片 -->
            <div class="form-group">
              <label class="form-label">产品主图</label>
              <div class="image-upload-area">
                <div v-if="form.mainImage" class="preview-container">
                  <img :src="form.mainImage" alt="主图预览" style="max-height: 200px;">
                  <button
                    type="button"
                    class="btn-remove"
                    @click="form.mainImage = ''"
                  >
                    <i class="bi bi-x-circle-fill"></i>
                  </button>
                </div>
                <div v-else class="upload-placeholder" @click="triggerMainImageInput">
                  <i class="bi bi-image"></i>
                  <p>点击上传主图</p>
                </div>
                <input
                  ref="mainImageInput"
                  type="file"
                  accept="image/*"
                  @change="handleMainImageUpload"
                  style="display: none"
                >
              </div>
            </div>

            <!-- 产品视频 -->
            <div class="form-group">
              <label class="form-label">产品视频</label>
              <div v-if="form.videoPath" class="video-preview">
                <video :src="form.videoPath" controls style="max-width: 400px;">
                  您的浏览器不支持视频播放。
                </video>
                <button
                  type="button"
                  class="btn btn-outline-danger btn-sm mt-2"
                  @click="form.videoPath = ''"
                >
                  <i class="bi bi-trash"></i> 删除视频
                </button>
              </div>
              <div v-else class="video-upload-section">
                <input
                  ref="videoInput"
                  type="file"
                  accept="video/mp4,video/avi,video/mov,video/wmv,video/flv,video/webm,video/mkv,video/3gpp,video/quicktime"
                  @change="handleVideoUpload"
                  style="display: none;"
                >
                <button
                  type="button"
                  class="btn btn-primary"
                  @click="triggerVideoInput"
                  :disabled="videoUploading"
                >
                  <span v-if="videoUploading" class="spinner-border spinner-border-sm me-2"></span>
                  <i v-else class="bi bi-cloud-upload"></i>
                  {{ videoUploading ? '上传中...' : '上传视频' }}
                </button>

                <!-- 上传进度 -->
                <div v-if="videoUploadProgress > 0 && videoUploading" class="upload-progress mt-3">
                  <div class="progress">
                    <div
                      class="progress-bar"
                      :style="{ width: videoUploadProgress + '%' }"
                    >
                      {{ videoUploadProgress }}%
                    </div>
                  </div>
                  <small class="form-text text-muted">正在上传视频，请稍候...</small>
                </div>

                <small class="form-text text-muted">支持 MP4、AVI、MOV、WMV、FLV、WebM、MKV、3GP、QuickTime 格式，最大 100MB</small>
              </div>
            </div>

            <!-- 产品其他图片 -->
            <div class="form-group">
              <label class="form-label">产品其他图片</label>
              <div class="other-images-upload-area">
                <!-- 已上传的其他图片展示 -->
                <div v-if="form.productImages.length > 0" class="uploaded-images-grid mb-3">
                  <div
                    v-for="(image, index) in form.productImages"
                    :key="index"
                    class="image-item"
                  >
                    <img :src="image" :alt="`产品图片${index + 1}`" class="product-image-thumb" />
                    <div class="image-overlay">
                      <button
                        type="button"
                        class="btn btn-sm btn-danger"
                        @click="removeProductImage(index)"
                        title="删除图片"
                      >
                        <i class="bi bi-trash"></i>
                      </button>
                    </div>
                    <div class="image-label">
                      <span class="badge bg-secondary">{{ index + 1 }}</span>
                    </div>
                  </div>
                </div>

                <!-- 添加其他图片按钮 -->
                <div class="add-more-images">
                  <input
                    ref="productImagesInput"
                    type="file"
                    accept="image/*"
                    multiple
                    @change="handleProductImagesUpload"
                    style="display: none;"
                  >
                  <button
                    type="button"
                    class="btn btn-outline-primary"
                    @click="triggerProductImagesInput"
                  >
                    <i class="bi bi-plus-circle"></i>
                    添加产品其他图片
                  </button>
                  <small class="form-text text-muted">
                    可同时上传多张图片，用于展示产品细节、多角度视图等。支持 JPG、PNG、GIF 格式，单张最大5MB
                  </small>
                </div>

                <!-- 图片统计信息 -->
                <div v-if="form.productImages.length > 0" class="image-count-info mt-2">
                  <div class="alert alert-sm alert-info">
                    <i class="bi bi-info-circle"></i>
                    当前已上传 <strong>{{ form.productImages.length }}</strong> 张其他图片
                    <span v-if="form.mainImage" class="ms-2">+ 1 张主图</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </form>
      </div>

      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" @click="$emit('close')">
          取消
        </button>
        <button
          type="submit"
          class="btn btn-primary"
          @click="handleSubmit"
          :disabled="submitting"
        >
          <span v-if="submitting">
            <i class="bi bi-arrow-repeat spin"></i> {{ isEdit ? '更新中...' : '创建中...' }}
          </span>
          <span v-else>
            <i class="bi bi-check-circle"></i> {{ isEdit ? '更新产品' : '创建产品' }}
          </span>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, watch, onMounted } from 'vue'
import { productAPI, uploadAPI } from '@/api'
import CategorySelector from './CategorySelector.vue'
import CategorySelectorAdvanced from './CategorySelectorAdvanced.vue'
import Swal from 'sweetalert2'

// Props 和 Emits
const props = defineProps({
  product: {
    type: Object,
    default: null
  }
})

const emit = defineEmits(['close', 'success'])

// 响应式数据
const activeTab = ref('basic')
const submitting = ref(false)
const categorySelector = ref()
const mainImageInput = ref()
const productImagesInput = ref()
const videoInput = ref()

// 计算属性
const isEdit = computed(() => !!props.product && !!props.product.id)

// 表单数据 - 包含所有42个属性（移除系统字段）
const form = reactive({
  // 基本信息属性
  productName: '',
  productCode: '',
  category: '',
  description: '',
  specifications: '',

  // 三级分类属性
  categoryPath: '',
  categoryLevel1: '',
  categoryLevel2: '',
  categoryLevel3: '',
  isCustomCategory: false,

  // 媒体资源属性
  mainImage: '',
  productImages: [],
  videoPath: '',

  // 价格库存属性
  price: 0,
  marketPrice: 0,
  stockQuantity: 0,

  // 产品基本属性
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
  supplyType: '', // 初始值为空
  origin: '',
  fastSampling: false,

  // 规格包装属性
  model: '',
  season: '',
  salesUnit: '',

  // 其他属性
  status: 1,
  isFeatured: false,
  sortOrder: 0,
  seoKeywords: '',
  seoDescription: ''
})

// 方法
const initForm = () => {
  if (props.product) {
    console.log('🔄 开始填充表单数据，产品数据:', props.product)

    // 字段映射 - 前端显示字段 → 表单字段
    const fieldMapping = {
      'name': 'productName', // 显示用的name → 表单用的productName
      'image': 'mainImage', // 显示用的image → 表单用的mainImage
    }

    // 编辑模式 - 填充现有数据
    Object.keys(form).forEach(key => {
      let value = null

      // 首先检查映射字段 (如: name → productName)
      const displayField = Object.keys(fieldMapping).find(k => fieldMapping[k] === key)
      if (displayField && props.product[displayField] !== undefined && props.product[displayField] !== null) {
        value = props.product[displayField]
      } else if (props.product[key] !== undefined && props.product[key] !== null) {
        // 直接字段匹配
        value = props.product[key]
      }

      // 特别调试tags字段
      if (key === 'tags') {
        console.log('🔍 标签字段初始化检查:', {
          formKey: key,
          displayField: displayField,
          displayFieldValue: displayField ? props.product[displayField] : undefined,
          directFieldValue: props.product[key],
          finalValue: value,
          finalValueType: typeof value,
          isArray: Array.isArray(value)
        })
      }

      // 特殊调试款号相关字段
      if (['material', 'fabricType', 'fabricWeight'].includes(key)) {
        console.log(`🧵 材质字段调试 - ${key}:`, {
          formKey: key,
          displayField: displayField,
          displayFieldValue: displayField ? props.product[displayField] : undefined,
          directFieldValue: props.product[key],
          finalValue: value,
          valueType: typeof value
        })
      }

      // 特殊处理某些字段
      if (value !== null && value !== undefined) {
        if (key === 'tags') {
          // 标签字段处理 - 新的逗号分隔文本格式
          console.log('🏷️ 处理标签字段 - 原始值:', value, '类型:', typeof value, '是否为数组:', Array.isArray(value))
          if (Array.isArray(value)) {
            // 如果后端返回数组，转换为逗号分隔的字符串
            form[key] = value.join(', ')
            console.log('🏷️ 标签从数组转换为逗号分隔字符串:', form[key])
          } else if (typeof value === 'string') {
            // 如果已经是字符串，直接使用
            form[key] = value
            console.log('🏷️ 标签保持字符串格式:', form[key])
          } else if (value === null || value === undefined) {
            form[key] = ''
            console.log('🏷️ 标签设置为空字符串，因为值为null/undefined')
          } else {
            // 其他情况转换为字符串
            form[key] = String(value)
            console.log('🏷️ 标签转换为字符串格式:', form[key])
          }

          // 标签处理完成后的最终调试
          console.log('🏷️ 标签字段处理完成:', {
            originalValue: value,
            finalFormValue: form[key],
            finalFormType: typeof form[key],
            finalFormLength: form[key] ? form[key].length : 0
          })

          // 强制触发响应式更新
          console.log('🔄 强制触发响应式更新，标签值:', form[key])
        } else if (key === 'productImages') {
          // 产品图片处理
          if (Array.isArray(value)) {
            form[key] = value
          } else if (typeof value === 'string' && value) {
            try {
              form[key] = JSON.parse(value)
            } catch (e) {
              form[key] = []
            }
          } else {
            form[key] = []
          }
        } else if (key === 'status') {
          // 状态字段处理
          if (typeof value === 'string') {
            const statusMap = { 'published': 1, 'inactive': 0, 'draft': 2 }
            form[key] = statusMap[value] !== undefined ? statusMap[value] : 1
          } else {
            form[key] = parseInt(value) !== undefined ? parseInt(value) : 1
          }
        } else if (['price', 'marketPrice', 'stockQuantity', 'sortOrder'].includes(key)) {
          // 数字字段处理
          form[key] = parseFloat(value) || 0
        } else if (['isFeatured', 'fastSampling'].includes(key)) {
          // 布尔字段处理
          form[key] = Boolean(value)
        } else {
          // 字符串字段处理
          form[key] = String(value)

          // 材质字段设置后的调试信息
          if (['material', 'fabricType', 'fabricWeight'].includes(key)) {
            console.log(`🧵 材质字段设置完成 - ${key}:`, {
              finalValue: form[key],
              finalType: typeof form[key]
            })
          }
        }
      }
    })

    // 特殊处理分类数据映射
    const categoryValue = props.product.categoryPath || props.product.category || ''
    form.categoryPath = categoryValue
    form.category = categoryValue

    // 解析分类路径到各级分类（用于编辑时回填到分类选择器）
    if (categoryValue) {
      const parts = categoryValue.split(' > ').map(part => part.trim()).filter(part => part)

      if (parts.length > 0) {
        form.categoryLevel1 = parts[0] || ''
        form.categoryLevel2 = parts[1] || ''
        form.categoryLevel3 = parts[2] || ''

        // 检查是否包含自定义分类
        form.isCustomCategory = !["Men", "Women", "Children", "Denim hat/Denim bag", "Customization of denim processing accessories", "Ungrouped"].includes(parts[0])

        console.log('🏷️ 分类数据解析:', {
          original: categoryValue,
          parts,
          level1: form.categoryLevel1,
          level2: form.categoryLevel2,
          level3: form.categoryLevel3,
          isCustom: form.isCustomCategory
        })
      }
    }

    console.log('✅ 表单数据填充完成:', { ...form })
  } else {
    console.log('➕ 新建模式 - 重置表单')
    resetForm()
  }
}

const resetForm = () => {
  Object.keys(form).forEach(key => {
    if (key === 'productImages') {
      form[key] = []
    } else if (key === 'status') {
      form[key] = 1
    } else if (key === 'price' || key === 'marketPrice' || key === 'stockQuantity' || key === 'sortOrder') {
      form[key] = 0
    } else if (key === 'isFeatured' || key === 'fastSampling' || key === 'isCustomCategory') {
      form[key] = false
    } else if (key === 'origin') {
      form[key] = '' // 留空，后端会设置默认值 "Guangdong, China"
    } else if (key === 'supplyType') {
      form[key] = '' // 保持为空，无默认值
    } else if (key === 'logoPosition') {
      form[key] = '' // 留空，后端会设置默认值 "无"
    } else {
      form[key] = ''
    }
  })
}

const handleCategoryChange = (categoryPath) => {
  console.log('🏷️ 分类变化:', categoryPath)

  if (categoryPath) {
    // 更新category字段为拼接后的路径
    form.category = categoryPath
    form.categoryPath = categoryPath

    // 解析分类路径到各级分类
    const parts = categoryPath.split(' > ').map(part => part.trim()).filter(part => part)

    if (parts.length > 0) {
      form.categoryLevel1 = parts[0] || ''
      form.categoryLevel2 = parts[1] || ''
      form.categoryLevel3 = parts[2] || ''

      // 检查是否包含自定义分类
      form.isCustomCategory = !["Men", "Women", "Children", "Denim hat/Denim bag", "Customization of denim processing accessories", "Ungrouped"].includes(parts[0])
    }

    console.log('✅ 分类字段更新:', {
      category: form.category,
      categoryPath: form.categoryPath,
      level1: form.categoryLevel1,
      level2: form.categoryLevel2,
      level3: form.categoryLevel3,
      isCustom: form.isCustomCategory
    })
  }
}

// 触发主图上传
const triggerMainImageInput = () => {
  mainImageInput.value.click()
}

const handleMainImageUpload = async (event) => {
  const file = event.target.files[0]
  if (file) {
    try {
      // 显示加载状态
      Swal.fire({
        title: '正在上传图片...',
        text: '请稍候',
        allowOutsideClick: false,
        didOpen: () => {
          Swal.showLoading()
        }
      })

      // 使用 uploadAPI 上传图片到服务器
      const response = await uploadAPI.uploadImage(file, {
        category: 'product', // 产品图片分类
        description: '产品主图'
      })

      if (response.success && response.data && response.data.url) {
        form.mainImage = response.data.url // 使用服务器返回的URL
        console.log('✅ 主图上传成功:', response.data.url)

        Swal.fire({
          icon: 'success',
          title: '上传成功',
          text: '主图已成功上传',
          timer: 1500,
          showConfirmButton: false
        })
      } else {
        throw new Error('上传失败，未获取到图片URL')
      }
    } catch (error) {
      console.error('❌ 主图上传失败:', error)

      Swal.fire({
        icon: 'error',
        title: '上传失败',
        text: '图片上传失败，请重试',
        confirmButtonText: '确定'
      })

      // 清空输入框
      event.target.value = ''
    }
  }
}

// 触发产品其他图片文件选择
const triggerProductImagesInput = () => {
  productImagesInput.value.click()
}

// 处理产品其他图片上传
const handleProductImagesUpload = async (event) => {
  const files = Array.from(event.target.files)
  if (files.length === 0) return

  try {
    // 显示上传进度提示
    Swal.fire({
      title: '正在上传图片...',
      text: `正在上传 ${files.length} 张图片，请稍候`,
      allowOutsideClick: false,
      didOpen: () => {
        Swal.showLoading()
      }
    })

    // 逐个上传图片
    const uploadPromises = files.map(async (file, index) => {
      try {
        const response = await uploadAPI.uploadImage(file, {
          category: 'product', // 产品图片分类
          description: `产品其他图片 ${index + 1}`
        })

        if (response.success && response.data && response.data.url) {
          return response.data.url
        } else {
          throw new Error(`第 ${index + 1} 张图片上传失败`)
        }
      } catch (error) {
        console.error(`❌ 第 ${index + 1} 张图片上传失败:`, error)
        throw new Error(`第 ${index + 1} 张图片上传失败: ${error.message}`)
      }
    })

    // 等待所有图片上传完成
    const uploadedUrls = await Promise.all(uploadPromises)

    // 将新上传的图片URL添加到现有图片列表中
    form.productImages.push(...uploadedUrls)

    console.log('✅ 产品其他图片上传成功:', uploadedUrls)

    Swal.fire({
      icon: 'success',
      title: '上传成功',
      text: `成功上传 ${uploadedUrls.length} 张图片`,
      timer: 2000,
      showConfirmButton: false
    })
  } catch (error) {
    console.error('❌ 产品其他图片上传失败:', error)

    Swal.fire({
      icon: 'error',
      title: '上传失败',
      text: error.message || '图片上传失败，请重试',
      confirmButtonText: '确定'
    })
  } finally {
    // 清空输入框
    event.target.value = ''
  }
}

// 删除产品其他图片
const removeProductImage = (index) => {
  Swal.fire({
    title: '确认删除',
    text: `确定要删除第 ${index + 1} 张产品其他图片吗？`,
    icon: 'warning',
    showCancelButton: true,
    confirmButtonText: '确定删除',
    cancelButtonText: '取消',
    confirmButtonColor: '#dc3545'
  }).then((result) => {
    if (result.isConfirmed) {
      // 从数组中移除指定索引的图片
      form.productImages.splice(index, 1)

      Swal.fire({
        icon: 'success',
        title: '删除成功',
        text: '图片已删除',
        timer: 1000,
        showConfirmButton: false
      })
    }
  })
}

// 触发视频文件选择
const triggerVideoInput = () => {
  videoInput.value.click()
}

// 视频上传进度
const videoUploadProgress = ref(0)
const videoUploading = ref(false)

// 处理视频上传
const handleVideoUpload = async (event) => {
  const file = event.target.files[0]
  if (!file) return

  // 验证文件类型
  const validTypes = ['video/mp4', 'video/avi', 'video/mov', 'video/wmv', 'video/flv', 'video/webm', 'video/mkv', 'video/3gpp', 'video/quicktime']
  if (!validTypes.includes(file.type)) {
    Swal.fire({
      icon: 'error',
      title: '文件类型错误',
      text: '请选择有效的视频文件',
      confirmButtonText: '确定'
    })
    event.target.value = ''
    return
  }

  // 验证文件大小 (100MB)
  const maxSize = 100 * 1024 * 1024
  if (file.size > maxSize) {
    Swal.fire({
      icon: 'error',
      title: '文件过大',
      text: '视频文件不能超过100MB',
      confirmButtonText: '确定'
    })
    event.target.value = ''
    return
  }

  videoUploading.value = true
  videoUploadProgress.value = 0

  try {
    // 使用 uploadAPI 上传视频到服务器，带进度回调
    const response = await uploadAPI.uploadVideo(file, {
      category: 'product', // 产品视频分类
      description: '产品视频',
      onProgress: (percent) => {
        videoUploadProgress.value = percent
      }
    })

    if (response.success && response.data && response.data.url) {
      form.videoPath = response.data.url // 使用服务器返回的URL
      console.log('✅ 视频上传成功:', response.data.url)
    } else {
      throw new Error('上传失败，未获取到视频URL')
    }
  } catch (error) {
    console.error('❌ 视频上传失败:', error)

    Swal.fire({
      icon: 'error',
      title: '上传失败',
      text: '视频上传失败，请重试',
      confirmButtonText: '确定'
    })
  } finally {
    videoUploading.value = false
    videoUploadProgress.value = 0
    // 清空输入框，允许重复选择同一文件
    event.target.value = ''
  }
}

const uploadVideo = async (file) => {
  try {
    console.log('🎬 开始上传视频文件:', {
      name: file.name,
      size: file.size,
      sizeMB: (file.size / 1024 / 1024).toFixed(2) + ' MB',
      type: file.type,
      lastModified: new Date(file.lastModified).toISOString()
    })

    // 前端最终验证 - 适配后端100MB限制
    if (file.size > 100 * 1024 * 1024) {
      console.error('❌ 文件大小超过100MB限制:', file.size)
      Swal.fire({
        icon: 'error',
        title: '文件过大',
        html: `文件大小: ${(file.size / 1024 / 1024).toFixed(2)} MB<br>最大允许: 100 MB`,
        confirmButtonText: '确定'
      })
      return
    }

    // 验证文件类型 - 后端支持的视频类型
    const allowedMimeTypes = [
      'video/mp4', 'video/avi', 'video/mov', 'video/wmv', 'video/flv', 'video/webm',
      'video/mkv', 'video/3gpp', 'video/quicktime', 'video/x-msvideo', 'video/x-matroska',
      'video/ogg', 'application/octet-stream'
    ]

    const allowedExtensions = ['.mp4', '.avi', '.mov', '.wmv', '.flv', '.webm', '.mkv', '.3gp', '.qt']

    const fileName = file.name.toLowerCase()
    const hasValidMimeType = allowedMimeTypes.includes(file.type)
    const hasValidExtension = allowedExtensions.some(ext => fileName.endsWith(ext))

    console.log('🔍 文件类型验证:', {
      mimeType: file.type,
      fileName: fileName,
      hasValidMimeType,
      hasValidExtension
    })

    if (!hasValidMimeType && !hasValidExtension) {
      console.error('❌ 不支持的文件类型:', { mimeType: file.type, fileName })
      Swal.fire({
        icon: 'error',
        title: '文件类型不支持',
        html: `MIME类型: ${file.type}<br>文件名: ${file.name}<br><br>支持的格式: MP4, AVI, MOV, WMV, FLV, WEBM, MKV, 3GP, QT`,
        confirmButtonText: '确定'
      })
      return
    }

    // 显示上传进度
    Swal.fire({
      title: '正在上传视频...',
      html: '<div class="progress-info">文件名: ' + file.name + '</div><div class="progress-info">文件大小: ' + (file.size / 1024 / 1024).toFixed(2) + ' MB</div><div class="progress-bar"><div class="progress-fill"></div></div>',
      allowOutsideClick: false,
      allowEscapeKey: false,
      showConfirmButton: false,
      didOpen: () => {
        // 模拟上传进度
        let progress = 0
        const progressInterval = setInterval(() => {
          progress += Math.random() * 20
          if (progress > 90) progress = 90

          const progressFill = document.querySelector('.swal2-container .progress-fill')
          if (progressFill) {
            progressFill.style.width = progress + '%'
          }
        }, 200)

        // 保存interval ID以便后续清理
        window.uploadProgressInterval = progressInterval
      }
    })

    const formData = new FormData()
    formData.append('file', file)

    console.log('📤 发送视频上传请求到:', '/api/admin/upload/video')
    console.log('📋 FormData详情:')
    for (let [key, value] of formData.entries()) {
      console.log(`  ${key}:`, {
        name: value.name,
        size: value.size,
        type: value.type,
        lastModified: value.lastModified
      })
    }

    const response = await productAPI.uploadProductVideo(formData)

    console.log('📹 视频上传响应:', response)

    // 完成进度
    if (window.uploadProgressInterval) {
      clearInterval(window.uploadProgressInterval)
      window.uploadProgressInterval = null
    }

    const progressFill = document.querySelector('.swal2-container .progress-fill')
    if (progressFill) {
      progressFill.style.width = '100%'
    }

    // 延迟一下让用户看到100%进度
    await new Promise(resolve => setTimeout(resolve, 300))

    if (response.data && response.data.url) {
      form.videoPath = response.data.url
      console.log('✅ 视频上传成功，URL:', response.data.url)

      // 关闭当前弹窗并显示成功信息
      Swal.close()

      await new Promise(resolve => setTimeout(resolve, 100))

      Swal.fire({
        icon: 'success',
        title: '视频上传成功',
        html: '<div>文件名: ' + file.name + '</div><div>文件大小: ' + (file.size / 1024 / 1024).toFixed(2) + ' MB</div><div>访问链接: ' + response.data.url + '</div>',
        confirmButtonText: '确定',
        confirmButtonColor: '#1976d2'
      })
    } else {
      throw new Error('响应中没有返回视频URL')
    }
  } catch (error) {
    console.error('❌ 视频上传失败:', error)

    // 清理进度定时器
    if (window.uploadProgressInterval) {
      clearInterval(window.uploadProgressInterval)
      window.uploadProgressInterval = null
    }

    // 显示详细错误信息
    let errorMessage = '视频上传失败'
    if (error.response) {
      console.error('❌ 错误响应:', error.response)
      errorMessage = error.response.data?.message || error.response.data || error.message
    } else if (error.request) {
      console.error('❌ 网络请求失败:', error.request)
      errorMessage = '网络连接失败，请检查网络连接'
    } else {
      console.error('❌ 其他错误:', error.message)
      errorMessage = error.message
    }

    // 关闭当前弹窗
    Swal.close()

    // 延迟一下让弹窗关闭动画完成
    await new Promise(resolve => setTimeout(resolve, 100))

    Swal.fire({
      icon: 'error',
      title: '上传失败',
      html: '<div>错误信息: ' + errorMessage + '</div><div>文件名: ' + file.name + '</div>',
      confirmButtonText: '确定',
      confirmButtonColor: '#1976d2'
    })
  }
}

const showVideoUploadModal = () => {
  // 这里可以打开视频上传模态框
  Swal.fire({
    title: '上传产品视频',
    text: '请选择要上传的视频文件',
    input: 'file',
    inputAttributes: {
      'accept': 'video/*',
      'aria-label': '上传产品视频'
    },
    showCancelButton: true,
    confirmButtonText: '上传',
    cancelButtonText: '取消',
    didOpen: () => {
      // 添加文件验证
      const fileInput = Swal.getInput()
      if (fileInput) {
        fileInput.addEventListener('change', (e) => {
          const file = e.target.files[0]
          if (file) {
            // 验证文件大小（适配后端100MB限制）
            if (file.size > 100 * 1024 * 1024) {
              Swal.showValidationMessage('视频文件大小不能超过100MB')
              return
            }

            // 验证文件格式
            const validExtensions = ['.mp4', '.avi', '.mov', '.wmv', '.flv', '.webm', '.mkv', '.m4v']
            const fileName = file.name.toLowerCase()
            const isValidFormat = validExtensions.some(ext => fileName.endsWith(ext))

            if (!isValidFormat) {
              Swal.showValidationMessage('只支持 MP4、AVI、MOV、WMV、FLV、WEBM、MKV、M4V 格式的视频文件')
              return
            }
          }
        })
      }
    }
  }).then((result) => {
    if (result.isConfirmed && result.value) {
      // 处理视频上传
      uploadVideo(result.value)
    }
  })
}

const validateForm = () => {
  if (!form.productName.trim()) {
    Swal.fire('验证失败', '请输入产品名称', 'warning')
    return false
  }
  if (!form.description.trim()) {
    Swal.fire('验证失败', '请输入产品描述', 'warning')
    return false
  }
  return true
}

const handleSubmit = async () => {
  if (!validateForm()) return

  submitting.value = true

  // 生成唯一的产品编号（如果为空的话）
  if (!form.productCode.trim()) {
    const timestamp = Date.now()
    const random = Math.floor(Math.random() * 10000)
    form.productCode = `PRD${timestamp}${random}`
  }

  try {
    console.log('🎯 ========== 开始提交产品数据 ==========')
    console.log('🎯 当前表单完整数据:', JSON.stringify(form, null, 2))

    // 设置默认值 - 核心修改：supplyType 不再设置默认值，保持为空
    const origin = form.origin || 'Guangdong, China'
    const supplyType = form.supplyType || '' // 原代码是 'OEM加工服务'，现在改为空
    const logoPosition = form.logoPosition || '无'

    // 只发送需要的字段，确保字段完整性和数据类型正确
    const productData = {
      // 基本信息属性
      productName: form.productName || '',
      productCode: form.productCode || '',
      category: form.categoryPath || form.category || '', // 使用拼接后的分类路径
      description: form.description || '',
      specifications: form.specifications || '',
      mainImage: form.mainImage || '',
      productImages: Array.isArray(form.productImages) && form.productImages.length > 0
        ? JSON.stringify(form.productImages)
        : null,
      videoPath: form.videoPath || '',

      // 价格库存属性
      price: parseFloat(form.price) || 0,
      marketPrice: parseFloat(form.marketPrice) || 0,
      stockQuantity: parseInt(form.stockQuantity) || 0,

      // 状态属性
      status: parseInt(form.status) !== undefined ? parseInt(form.status) : 1,
      isFeatured: Boolean(form.isFeatured),
      sortOrder: parseInt(form.sortOrder) || 0,

      // SEO属性
      seoKeywords: form.seoKeywords || '',
      seoDescription: form.seoDescription || '',

      // ============ 服装材质属性 ============
      material: form.material || '', // 材质（如：纯棉、涤纶、牛仔布）
      fabricType: form.fabricType || '', // 面料类型（如：梭织布、针织布）
      fabricWeight: form.fabricWeight || '', // 面料克重（如：180g、220g）

      // ============ 服装款式属性 ============
      waistStyle: form.waistStyle || '', // 腰型（如：高腰、中腰、低腰）
      jeansStyle: form.jeansStyle || '', // 牛仔裤款式（如：直筒、紧身、喇叭）
      patternFit: form.patternFit || '', // 板型（如：修身、宽松、标准）
      style: form.style || '', // 样式（如：休闲、商务、运动）

      // ============ 工艺制造属性 ============
      craftsmanship: form.craftsmanship || '', // 工艺（如：水洗、石磨、激光）
      weavingMethod: form.weavingMethod || '', // 编织方法（如：平纹、斜纹、提花）
      washingProcess: form.washingProcess || '', // 洗水工艺（如：普洗、石洗、酵洗）
      needleDetectionProcess: form.needleDetectionProcess || '', // 检针工艺（如：过检、磁检）

      // ============ 图案设计属性 ============
      patternType: form.patternType || '', // 图案类型（如：纯色、条纹、印花）
      printingMethod: form.printingMethod || '', // 印花方法（如：丝印、数码印花）
      fashionElements: form.fashionElements || '', // 流行元素（如：破洞、刺绣、撞色）
      logoPosition: logoPosition, // 徽标位置（如：左胸、后领、袖口）

      // ============ 供应商业属性 ============
      supplyType: supplyType, // 供应类型（空值）
      origin: origin, // 原产地（如：中国、越南、孟加拉）
      fastSampling: Boolean(form.fastSampling), // 7天快速打样：false-否 true-是

      // ============ 规格包装属性 ============
      model: form.model || '', // 型号（如：S/M/L、均码）
      season: form.season || '', // 季节（如：春季、夏季、四季）→ 不再强制填充默认值
      salesUnit: form.salesUnit || '' // 销售单位（如：件、套、打）
    }

    // 数据清理 - 移除null和undefined值，但保留空字符串、0值和false值
    console.log('🧹 数据清理前，productData:', JSON.stringify(productData, null, 2))

    Object.keys(productData).forEach(key => {
      if (productData[key] === null || productData[key] === undefined || productData[key] === 'undefined') {
        delete productData[key]
        console.log(`🗑️ 移除无效字段 ${key}:`, productData[key])
      }
      // 对于空字符串字段，根据业务逻辑决定是否保留
      else if (productData[key] === '' && ['productName', 'description'].includes(key)) {
        // 必填字段不能为空，但由于有前端验证，这里不删除
      }
      // 确保数字字段是有效的数字类型
      else if (['price', 'marketPrice', 'stockQuantity', 'status', 'sortOrder'].includes(key)) {
        if (isNaN(Number(productData[key])) || productData[key] === 'undefined') {
          console.log(`⚠️ 数字字段 ${key} 无效:`, productData[key])
          productData[key] = 0 // 设置默认值
        }
      }
    })

    console.log('🧹 数据清理后，productData:', JSON.stringify(productData, null, 2))

    console.log('📋 最终提交的产品数据:', JSON.stringify(productData, null, 2))

    let response
    console.log('🚀 开始保存产品数据:', {
      isEdit: isEdit.value,
      productId: props.product?.id,
      productData
    })

    if (isEdit.value) {
      // 确保产品ID存在且有效
      const productId = props.product?.id
      if (!productId || productId === 'undefined' || productId === undefined) {
        throw new Error('产品ID无效，无法更新产品')
      }
      console.log('📝 更新产品，ID:', productId)
      response = await productAPI.updateProduct(productId, productData)
    } else {
      console.log('🆕 创建新产品')
      response = await productAPI.createProduct(productData)
    }

    if (response) {
      console.log('✅ 产品保存成功，发送success事件', response)

      // 显示成功提示
      Swal.fire({
        icon: 'success',
        title: isEdit.value ? '产品更新成功' : '产品创建成功',
        showConfirmButton: false,
        timer: 1500
      })

      // 发送成功事件，如果是编辑模式，传递更新后的产品数据
      if (isEdit.value && response.data) {
        emit('success', response.data)
      } else {
        emit('success')
      }

      emit('close')
    }
  } catch (error) {
    console.error('提交失败:', error)
    Swal.fire({
      icon: 'error',
      title: '操作失败',
      text: error.response?.data?.message || error.message || '操作失败，请重试'
    })
  } finally {
    submitting.value = false
  }
}

// 监听产品数据变化 - 处理编辑和新建模式
watch(
  () => props.product,
  (newProduct, oldProduct) => {
    if (newProduct && newProduct !== oldProduct) {
      // 编辑模式 - 有产品数据
      initForm()
      activeTab.value = 'basic'
    } else if (!newProduct && oldProduct) {
      // 切换到新建模式
      resetForm()
      activeTab.value = 'basic'
    }
  },
  { deep: true, immediate: true }
)

// 生命周期
onMounted(() => {
  if (props.product) {
    initForm()
  }
})
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
  z-index: 1050;
  overflow-y: auto;
}

.modal-content {
  background: white;
  border-radius: 12px;
  width: 90%;
  max-width: 1200px;
  max-height: 90vh;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.modal-content.enhanced {
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid #e9ecef;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.modal-header.blue-white-bg {
  background: #e3f2fd;
  color: #1976d2;
}

.modal-header h4 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
}

.btn-close {
  background: none;
  border: none;
  color: white;
  font-size: 24px;
}

.modal-header.blue-white-bg .btn-close {
  color: #1976d2;
}

.btn-close {
  cursor: pointer;
  padding: 0;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: background-color 0.2s;
}

.btn-close:hover {
  background-color: rgba(255, 255, 255, 0.2);
}

.modal-body {
  flex: 1;
  overflow-y: auto;
  padding: 24px;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 20px 24px;
  border-top: 1px solid #e9ecef;
  background: #f8f9fa;
}

/* 表单标签页 */
.form-tabs {
  display: flex;
  gap: 4px;
  margin-bottom: 24px;
  border-bottom: 2px solid #e9ecef;
  overflow-x: auto;
  flex-wrap: wrap;
}

.tab-btn {
  padding: 10px 16px;
  border: none;
  background: none;
  color: #6c757d;
  cursor: pointer;
  border-bottom: 3px solid transparent;
  transition: all 0.2s;
  white-space: nowrap;
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  font-weight: 500;
}

.tab-btn:hover {
  color: #495057;
  background: rgba(0, 0, 0, 0.05);
}

.tab-btn.active {
  color: #667eea;
  border-bottom-color: #667eea;
  background: rgba(102, 126, 234, 0.1);
}

.tab-content {
  animation: fadeIn 0.3s ease-in-out;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

/* 表单样式 */
.form-group {
  margin-bottom: 20px;
}

.form-label {
  display: block;
  margin-bottom: 6px;
  font-weight: 500;
  color: #495057;
  font-size: 14px;
}

.form-label.required::after {
  content: ' *';
  color: #dc3545;
}

.form-control {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #ced4da;
  border-radius: 6px;
  font-size: 14px;
  transition: border-color 0.15s ease-in-out, box-shadow 0.15s ease-in-out;
}

.form-control:focus {
  border-color: #667eea;
  box-shadow: 0 0 0 0.2rem rgba(102, 126, 234, 0.25);
  outline: 0;
}

.form-row {
  display: flex;
  gap: 16px;
  margin-bottom: 20px;
}

.form-row .form-group {
  flex: 1;
  margin-bottom: 0;
}

.form-text {
  font-size: 12px;
  color: #6c757d;
  margin-top: 4px;
  display: block;
}

.text-danger {
  color: #dc3545 !important;
}

.warning-text {
  color: #ffc107 !important;
  font-weight: 500;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #495057;
  margin: 24px 0 16px 0;
  padding-bottom: 8px;
  border-bottom: 2px solid #e9ecef;
  display: flex;
  align-items: center;
  gap: 8px;
}

.section-title:first-child {
  margin-top: 0;
}

/* 标签文本输入 */
.tags-text-input {
  position: relative;
}

.tags-preview {
  margin-top: 8px;
  padding: 12px;
  background: #f8f9fa;
  border: 1px solid #dee2e6;
  border-radius: 6px;
}

.preview-label {
  font-size: 12px;
  color: #6c757d;
  margin-bottom: 6px;
  font-weight: 500;
}

.tags-list {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.tag-item {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  padding: 4px 8px;
  background: #667eea;
  color: white;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
}

/* 分类显示 */
.category-display {
  padding: 12px 16px;
  background: #f8f9fa;
  border: 1px solid #dee2e6;
  border-radius: 6px;
  color: #495057;
  display: flex;
  align-items: center;
  gap: 8px;
  font-family: monospace;
}

/* 图片上传 */
.image-upload-area {
  border: 2px dashed #dee2e6;
  border-radius: 8px;
  padding: 20px;
  text-align: center;
  transition: border-color 0.2s;
  position: relative;
}

.image-upload-area:hover {
  border-color: #667eea;
}

.upload-placeholder {
  cursor: pointer;
  color: #6c757d;
}

.upload-placeholder i {
  font-size: 48px;
  margin-bottom: 12px;
  display: block;
}

.upload-placeholder p {
  margin: 0;
  font-size: 14px;
}

.preview-container {
  position: relative;
  display: inline-block;
}

.preview-container img {
  max-width: 100%;
  border-radius: 6px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.btn-remove {
  position: absolute;
  top: -8px;
  right: -8px;
  background: #dc3545;
  color: white;
  border: none;
  border-radius: 50%;
  width: 24px;
  height: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  font-size: 12px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

/* 视频预览 */
.video-preview {
  text-align: center;
}

.video-preview video {
  border-radius: 6px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.btn-remove-video {
  margin-top: 8px;
  background: #dc3545;
  color: white;
  border: none;
  border-radius: 6px;
  padding: 8px 16px;
  cursor: pointer;
  font-size: 14px;
  display: inline-flex;
  align-items: center;
  gap: 6px;
}

.video-upload-section {
  text-align: center;
  padding: 20px;
  border: 2px dashed #dee2e6;
  border-radius: 8px;
}

/* 产品属性容器 */
.attributes-container {
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  border: 1px solid #dee2e6;
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
}

.attributes-container .section-title {
  background: #e3f2fd;
  color: #1976d2;
  margin: -24px -24px 20px -24px;
  padding: 16px 24px;
  border-radius: 12px 12px 0 0;
  border: none;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.attributes-container .form-group {
  background: white;
  border-radius: 8px;
  padding: 16px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
  border: 1px solid #e9ecef;
  transition: all 0.2s ease;
}

.attributes-container .form-group:hover {
  border-color: #667eea;
  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.1);
  transform: translateY(-1px);
}

.attributes-container .form-label {
  font-weight: 600;
  color: #495057;
  margin-bottom: 8px;
  display: flex;
  align-items: center;
  gap: 6px;
}

.attributes-container .form-control {
  border: 1px solid #ced4da;
  border-radius: 6px;
  transition: all 0.2s ease;
}

.attributes-container .form-control:focus {
  border-color: #667eea;
  box-shadow: 0 0 0 0.2rem rgba(102, 126, 234, 0.25);
}

/* 输入组 */
.input-group {
  position: relative;
  display: flex;
  flex-wrap: wrap;
  align-items: stretch;
  width: 100%;
}

.input-group-text {
  display: flex;
  align-items: center;
  padding: 10px 12px;
  font-size: 14px;
  font-weight: 400;
  line-height: 1.5;
  color: #495057;
  text-align: center;
  white-space: nowrap;
  background-color: #e9ecef;
  border: 1px solid #ced4da;
  border-radius: 6px 0 0 6px;
}

.input-group .form-control {
  border-radius: 0 6px 6px 0;
  border-left: 0;
}

/* 开关 */
.form-switch {
  padding-left: 0;
  margin-bottom: 0;
}

.form-check-input {
  width: 40px;
  height: 20px;
  background-color: #dee2e6;
  border: 1px solid #ced4da;
  cursor: pointer;
}

.form-check-input:checked {
  background-color: #667eea;
  border-color: #667eea;
}

.form-check-label {
  margin-left: 8px;
  color: #495057;
  font-size: 14px;
  cursor: pointer;
}

/* 7天快速打样容器样式 */
.fast-sampling-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 12px;
  background: #f8f9fa;
  border: 1px solid #e9ecef;
  border-radius: 8px;
  min-height: 60px;
  transition: all 0.3s ease;
}

.fast-sampling-container:hover {
  border-color: #667eea;
  background: #f0f8ff;
}

.fast-sampling-container .form-check {
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0;
  padding: 8px 0;
}

.fast-sampling-container .form-check-input {
  margin: 0;
  order: 1;
}

.fast-sampling-container .form-check-label {
  margin: 0 0 0 12px;
  font-weight: 500;
  color: #495057;
  order: 2;
}

.fast-sampling-container .form-text {
  margin-top: 8px;
  text-align: center;
  font-size: 12px;
  color: #6c757d;
}

/* 按钮样式 */
.btn {
  padding: 10px 20px;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  display: inline-flex;
  align-items: center;
  gap: 6px;
}

.btn-primary {
  background: #667eea;
  color: white;
}

.btn-primary:hover:not(:disabled) {
  background: #5a6fd8;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
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
  color: #667eea;
  border: 2px solid #667eea;
}

.btn-outline-primary:hover {
  background: #667eea;
  color: white;
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none !important;
  box-shadow: none !important;
}

/* 加载动画 */
.spin {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

/* 响应式 */
@media (max-width: 768px) {
  .modal-content {
    width: 95%;
    margin: 10px;
  }

  .form-row {
    flex-direction: column;
    gap: 0;
  }

  .form-tabs {
    flex-direction: column;
  }

  .tab-btn {
    border-bottom: 1px solid #dee2e6;
    border-right: 3px solid transparent;
  }

  .tab-btn.active {
    border-right-color: #667eea;
    border-bottom-color: #dee2e6;
  }
}

/* 选择器样式 */
select.form-control {
  cursor: pointer;
  background-image: url("data:image/svg+xml,%3csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 16 16'%3e%3cpath fill='none' stroke='%23343a40' stroke-linecap='round' stroke-linejoin='round' stroke-width='2' d='m1 6 7 7 7-7'/%3e%3c/svg%3e");
  background-repeat: no-repeat;
  background-position: right 12px center;
  background-size: 16px 12px;
  padding-right: 40px;
  appearance: none;
}

/* 数字输入框 */
input[type="number"]::-webkit-inner-spin-button,
input[type="number"]::-webkit-outer-spin-button {
  opacity: 1;
  height: 40px;
}

/* 文本域 */
textarea.form-control {
  resize: vertical;
  min-height: 80px;
}

/* 进度条 */
.upload-progress {
  margin-top: 12px;
}

.progress {
  height: 8px;
  background-color: #e9ecef;
  border-radius: 4px;
  overflow: hidden;
}

.progress-bar {
  height: 100%;
  background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
  transition: width 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 12px;
  font-weight: 500;
}

/* Swal视频上传进度条样式 */
.swal2-container .progress-info {
  margin: 8px 0;
  font-size: 14px;
  color: #666;
}

.swal2-container .progress-bar {
  width: 100%;
  height: 6px;
  background-color: #e9ecef;
  border-radius: 3px;
  overflow: hidden;
  margin: 8px 0;
}

.swal2-container .progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #667eea 0%, #764ba2 100%);
  transition: width 0.3s ease;
  width: 0%;
}

/* 产品视频上传进度条样式 */
.upload-progress {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.upload-progress .progress {
  height: 28px;
  border-radius: 8px;
  overflow: hidden;
  background: #e9ecef;
}

.upload-progress .progress-bar {
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

.video-upload-section .btn {
  min-width: 120px;
}

.video-preview {
  background: #f8f9fa;
  padding: 15px;
  border-radius: 8px;
  text-align: center;
}

.video-preview video {
  border-radius: 4px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

/* 产品其他图片样式 */
.other-images-upload-area {
  border: 1px solid #e9ecef;
  border-radius: 8px;
  padding: 20px;
  background: #fafbfc;
}

.uploaded-images-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
  gap: 15px;
  margin-bottom: 20px;
}

.image-item {
  position: relative;
  border-radius: 8px;
  overflow: hidden;
  background: white;
  border: 2px solid #e9ecef;
  transition: all 0.3s ease;
}

.image-item:hover {
  border-color: #007bff;
  box-shadow: 0 4px 12px rgba(0, 123, 255, 0.15);
  transform: translateY(-2px);
}

.product-image-thumb {
  width: 100%;
  height: 120px;
  object-fit: cover;
  display: block;
}

.image-item .image-overlay {
  position: absolute;
  top: 0;
  right: 0;
  left: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.image-item:hover .image-overlay {
  opacity: 1;
}

.image-item .image-overlay .btn {
  transform: scale(0.9);
  transition: transform 0.2s ease;
}

.image-item:hover .image-overlay .btn {
  transform: scale(1);
}

.image-label {
  position: absolute;
  top: 8px;
  left: 8px;
}

.image-label .badge {
  font-size: 11px;
  padding: 4px 6px;
  border-radius: 10px;
  background: rgba(108, 117, 125, 0.9);
  backdrop-filter: blur(4px);
}

.add-more-images {
  text-align: center;
  padding: 20px;
  border: 2px dashed #dee2e6;
  border-radius: 8px;
  background: white;
  transition: all 0.3s ease;
}

.add-more-images:hover {
  border-color: #007bff;
  background: #f8f9ff;
}

.add-more-images .btn {
  min-width: 180px;
  padding: 12px 20px;
  font-weight: 500;
}

.image-count-info .alert {
  margin-bottom: 0;
  padding: 12px 16px;
  border-radius: 6px;
  font-size: 14px;
}

.image-count-info .alert-info {
  background: #e7f3ff;
  border-color: #b3d7ff;
  color: #0c5460;
}

.image-count-info .alert-info i {
  margin-right: 8px;
  color: #0275d1;
}

.image-count-info strong {
  color: #0275d1;
  font-weight: 600;
}

/* 响应式布局 */
@media (max-width: 768px) {
  .uploaded-images-grid {
    grid-template-columns: repeat(auto-fill, minmax(80px, 1fr));
    gap: 10px;
  }

  .product-image-thumb {
    height: 80px;
  }

  .add-more-images .btn {
    min-width: 140px;
    padding: 10px 16px;
    font-size: 14px;
  }
}

@media (max-width: 480px) {
  .uploaded-images-grid {
    grid-template-columns: repeat(auto-fill, minmax(60px, 1fr));
    gap: 8px;
  }

  .product-image-thumb {
    height: 60px;
  }
}
</style>