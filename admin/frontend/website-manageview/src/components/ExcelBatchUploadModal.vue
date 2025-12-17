<template>
  <div class="excel-upload-modal-overlay" @click.self="$emit('close')">
    <div class="excel-upload-modal">
      <div class="modal-header">
        <h3>
          <i class="bi bi-file-earmark-excel"></i>
          Excel批量上传产品
        </h3>
        <button class="btn-close" @click="$emit('close')">
          <i class="bi bi-x-lg"></i>
        </button>
      </div>

      <div class="modal-body">
        <!-- 模板下载区域 -->
        <div class="template-section">
          <div class="template-info">
            <i class="bi bi-info-circle"></i>
            <span>请先下载Excel模板，按照模板格式填写产品信息后再上传。注意：标签字段请使用逗号分隔格式（如：热销,新品），产品图片请使用JSON数组格式。</span>
          </div>
          <button class="btn btn-outline-primary" @click="downloadTemplate">
            <i class="bi bi-download"></i> 下载Excel模板
          </button>
        </div>

        <!-- 文件选择区域 -->
        <div class="file-select-section" :class="{ 'drag-over': isDragOver }">
          <input
            ref="fileInput"
            type="file"
            accept=".xlsx,.xls"
            @change="handleFileSelect"
            class="file-input"
          />
          <div class="upload-area" @click="$refs.fileInput.click()" @dragover.prevent="isDragOver = true" @dragleave="isDragOver = false" @drop.prevent="handleDrop">
            <i class="bi bi-cloud-upload"></i>
            <p v-if="!selectedFile">点击或拖拽Excel文件到此处</p>
            <p v-else class="selected-file">已选择: {{ selectedFile.name }}</p>
            <small>支持 .xlsx 和 .xls 格式，文件大小不超过10MB</small>
          </div>
        </div>

        <!-- 数据预览区域 -->
        <div v-if="parsedData.length > 0" class="data-preview">
          <h4>
            <i class="bi bi-table"></i>
            数据预览 (共 {{ parsedData.length }} 条)
          </h4>
          <div class="preview-table-container">
            <table class="preview-table">
              <thead>
                <tr>
                  <th rowspan="3">序号</th>
                  <th rowspan="3">产品信息</th>
                  <th colspan="4">分类与基本属性</th>
                  <th colspan="3">价格库存</th>
                  <th colspan="5">产品属性</th>
                  <th colspan="3">供应信息</th>
                  <th rowspan="3">状态</th>
                </tr>
                <tr>
                  <th>一级分类</th>
                  <th>二级分类</th>
                  <th>三级分类</th>
                  <th>规格</th>
                  <th>销售价</th>
                  <th>市场价</th>
                  <th>库存</th>
                  <th>材质</th>
                  <th>面料类型</th>
                  <th>风格</th>
                  <th>工艺</th>
                  <th>供应类型</th>
                  <th>产地</th>
                  <th>快速打样</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="(row, index) in parsedData.slice(0, 10)" :key="index">
                  <td class="text-center">{{ index + 1 }}</td>

                  <!-- 产品信息列 -->
                  <td class="product-info-cell">
                    <div class="product-name">
                      <strong>{{ row.productName || '未命名' }}</strong>
                    </div>
                    <div v-if="row.productCode" class="product-code">
                      <small class="text-muted">编码: {{ row.productCode }}</small>
                    </div>
                    <div v-if="row.tags" class="tags-cell">
                      <span class="tag-badge">{{ row.tags }}</span>
                    </div>
                  </td>

                  <!-- 分类与基本属性 -->
                  <td class="category-cell">
                    <div class="category-item">{{ row.categoryLevel1 || '-' }}</div>
                  </td>
                  <td class="category-cell">
                    <div class="category-item">{{ row.categoryLevel2 || '-' }}</div>
                  </td>
                  <td class="category-cell">
                    <div class="category-item">{{ row.categoryLevel3 || '-' }}</div>
                  </td>
                  <td class="spec-cell">
                    <small>{{ row.specifications || '-' }}</small>
                  </td>

                  <!-- 价格库存 -->
                  <td class="price-cell">¥{{ formatNumber(row.price) }}</td>
                  <td class="price-cell muted">¥{{ formatNumber(row.marketPrice) }}</td>
                  <td class="stock-cell">{{ formatNumber(row.stockQuantity) }}</td>

                  <!-- 产品属性 -->
                  <td class="attr-cell">{{ row.material || '-' }}</td>
                  <td class="attr-cell">{{ row.fabricType || '-' }}</td>
                  <td class="attr-cell">{{ row.style || '-' }}</td>
                  <td class="attr-cell">
                    <div class="craft-cell">{{ truncateText(row.craftsmanship, 15) || '-' }}</div>
                  </td>

                  <!-- 供应信息 -->
                  <td class="supply-cell">{{ row.supplyType || '-' }}</td>
                  <td class="supply-cell">
                    <small>{{ truncateText(row.origin, 10) || '-' }}</small>
                  </td>
                  <td class="fast-sampling-cell">
                    <span v-if="row.fastSampling" class="badge bg-success">支持</span>
                    <span v-else class="badge bg-secondary">不支持</span>
                  </td>

                  <!-- 状态 -->
                  <td>
                    <span :class="'status-badge status-' + (row.status || 1)">
                      {{ getStatusText(row.status || 1) }}
                    </span>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
          <small v-if="parsedData.length > 10" class="preview-note">
            * 仅显示前10条数据预览
          </small>
        </div>

        <!-- 上传结果展示 -->
        <div v-if="uploadResult" class="upload-result">
          <div class="result-summary">
            <div class="result-item success">
              <i class="bi bi-check-circle"></i>
              <span>成功: {{ uploadResult.successCount }}</span>
            </div>
            <div class="result-item failure">
              <i class="bi bi-x-circle"></i>
              <span>失败: {{ uploadResult.failureCount }}</span>
            </div>
          </div>
          <div v-if="uploadResult.errors.length > 0" class="error-list">
            <h5>错误详情:</h5>
            <ul>
              <li v-for="(error, index) in uploadResult.errors" :key="index">
                {{ error }}
              </li>
            </ul>
          </div>
        </div>

        <!-- 上传进度 -->
        <div v-if="uploading" class="upload-progress">
          <div class="progress-stats">
            <div class="progress-item">
              <i class="bi bi-check-circle"></i>
              <span> 成功: {{ uploadResult?.successCount || 0 }}</span>
            </div>
            <div class="progress-item">
              <i class="bi bi-x-circle"></i>
              <span> 失败: {{ uploadResult?.failureCount || 0 }}</span>
            </div>
          </div>
          <div class="progress-bar">
            <div class="progress-fill" :style="{ width: uploadProgress + '%' }"></div>
          </div>
          <div class="progress-info">
            <p>正在处理第 {{ currentRowIndex + 1 }} / {{ parsedData.length }} 行数据</p>
            <div v-if="currentProductName" class="current-product">
              <i class="bi bi-box"></i>
              <span>{{ currentProductName }}</span>
            </div>
          </div>
        </div>
      </div>

      <div class="modal-footer">
        <button class="btn btn-secondary" @click="$emit('close')" :disabled="uploading">
          关闭
        </button>
        <button v-if="uploading" class="btn btn-danger" @click="cancelUpload">
          <i class="bi bi-x-circle"></i>
          取消上传
        </button>
        <button
          v-else
          class="btn btn-primary"
          @click="handleUpload"
          :disabled="!selectedFile || parsedData.length === 0"
        >
          <i class="bi bi-upload"></i>
          开始上传
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import * as XLSX from 'xlsx'
import { productAPI } from '@/api'
import Swal from 'sweetalert2'

const emit = defineEmits(['success', 'close'])

const selectedFile = ref(null)
const parsedData = ref([])
const uploading = ref(false)
const uploadProgress = ref(0)
const uploadResult = ref(null)
const isDragOver = ref(false)
const fileInput = ref(null)
const isCancelled = ref(false)
const currentRowIndex = ref(0)
const currentProductName = ref('')

// 获取状态文本
const getStatusText = (status) => {
  const statusMap = {
    0: '下架',
    1: '上架',
    2: '草稿'
  }
  return statusMap[status] || '草稿'
}

// 辅助函数：格式化数字显示
const formatNumber = (value) => {
  if (value === null || value === undefined || value === '') return '0'
  const num = parseFloat(value)
  return isNaN(num) ? '0' : num.toFixed(2)
}

// 辅助函数：截断文本
const truncateText = (text, maxLength = 20) => {
  if (!text) return ''
  return text.length > maxLength ? text.substring(0, maxLength) + '...' : text
}

// 下载Excel模板
const downloadTemplate = () => {
  const templateData = [
    {
      // 基本信息属性
      productName: '男士直筒牛仔裤',
      productCode: 'PROD-001',
      category: 'Men > Bottoms > Trousers',
      categoryLevel1: 'Men',
      categoryLevel2: 'Bottoms',
      categoryLevel3: 'Trousers',
            description: '经典男士直筒牛仔裤，采用优质牛仔布料制作，舒适透气，修身版型设计，展现干练气质。经过精心洗水工艺处理，展现自然的牛仔色彩。',
      specifications: 'S(28-30) M(31-33) L(34-36) XL(37-39) XXL(40-42)',

      // 价格库存属性
      price: 159.99,
      marketPrice: 199.99,
      stockQuantity: 500,

      // 服装材质属性
      material: '98%棉 2%氨纶',
      fabricType: '牛仔布',
      fabricWeight: '14盎司',

      // 服装款式属性
      waistStyle: '中腰',
      jeansStyle: '直筒牛仔裤',
      patternFit: '修身版',
      style: '休闲风格',

      // 工艺制造属性
      craftsmanship: '精密缝制工艺，3线拷克，包边处理',
      washingProcess: '石磨洗水，自然做旧效果',

      // 图案设计属性
      patternType: '纯色设计',
      printingMethod: '无印刷',
      fashionElements: '金属纽扣，皮革标签，撞色缝线',
      logoPosition: '后袋右下角，刺绣工艺',

      // 供应商业属性
      supplyType: 'OEM Services',
      origin: '中国广东东莞',
      fastSampling: true,

      // 规格包装属性
      model: 'S/M/L/XL/XXL',
      season: '四季通用',
      salesUnit: '件',

      // 标签和SEO属性
      tags: '热销,新品,休闲,经典,修身',
      seoKeywords: '牛仔裤,男装,直筒裤,休闲裤,修身版,纯棉',
      seoDescription: '优质男士直筒牛仔裤，纯棉材质，修身版型，舒适透气，时尚百搭',

      // 媒体资源（可选）
      productImages: '["https://example.com/image1.jpg", "https://example.com/image2.jpg"]',  // JSON数组字符串格式

      // 其他属性
      status: 1,
      isFeatured: true,
      isMain: true,   // 设为主产品
      isNew: true,    // 设为新产品
      sortOrder: 10
    },
    {
      // 女装示例
      productName: '女士高腰短裤',
      productCode: 'PROD-002',
      category: 'Women > Bottoms > Shorts',
      categoryLevel1: 'Women',
      categoryLevel2: 'Bottoms',
      categoryLevel3: 'Shorts',
            description: '时尚女士高腰短裤，优质面料，舒适透气，展现女性优美腿部线条。',
      specifications: 'S M L XL',

      // 价格库存属性
      price: 89.99,
      marketPrice: 119.99,
      stockQuantity: 300,

      // 服装材质属性
      material: '95%棉 5%氨纶',
      fabricType: '牛仔布',
      fabricWeight: '12盎司',

      // 服装款式属性
      waistStyle: '高腰',
      jeansStyle: '短裤',
      patternFit: '修身版',
      style: '时尚风格',

      // 工艺制造属性
      craftsmanship: '精密缝制',
      washingProcess: '酵素洗水',

      // 图案设计属性
      patternType: '浅蓝色设计',
      printingMethod: '无印刷',
      fashionElements: '毛边处理，金属装饰',
      logoPosition: '后袋位置',

      // 供应商业属性
      supplyType: 'In Stock Supply',
      origin: '中国广东广州',
      fastSampling: false,

      // 规格包装属性
      model: 'S/M/L/XL',
      season: '春夏',
      salesUnit: '件',

      // 标签和SEO属性
      tags: '时尚,新品,热销,女士',
      seoKeywords: '短裤,女装,高腰,夏季,时尚',
      seoDescription: '时尚女士高腰短裤，优质面料，夏季必备',

      // 其他属性
      status: 1,
      isFeatured: false,
      isMain: false,  // 非主产品
      isNew: true,    // 设为新产品
      sortOrder: 20
    },
    {
      // 童装示例
      productName: '儿童休闲T恤',
      productCode: 'PROD-003',
      category: 'Children > Boys > T-shirt',
      categoryLevel1: 'Children',
      categoryLevel2: 'Boys',
      categoryLevel3: 'T-shirt',
            description: '舒适儿童T恤，纯棉材质，适合日常穿着。',
      specifications: '90/100/110/120/130/140',

      // 价格库存属性
      price: 39.99,
      marketPrice: 59.99,
      stockQuantity: 200,

      // 服装材质属性
      material: '100%纯棉',
      fabricType: '棉质T恤布',
      fabricWeight: '180克',

      // 服装款式属性
      waistStyle: '标准腰',
      jeansStyle: '不适用',
      patternFit: '标准版',
      style: '休闲风格',

      // 工艺制造属性
      craftsmanship: '双针缝制',
      washingProcess: '柔软处理',

      // 图案设计属性
      patternType: '卡通图案',
      printingMethod: '数码印花',
      fashionElements: '印花图案，圆领设计',
      logoPosition: '胸前左上角',

      // 供应商业属性
      supplyType: 'Wholesale Supply',
      origin: '中国浙江',
      fastSampling: true,

      // 规格包装属性
      model: '90/100/110/120/130/140',
      season: '四季通用',
      salesUnit: '件',

      // 标签和SEO属性
      tags: '儿童,舒适,纯棉,卡通',
      seoKeywords: '童装,T恤,儿童服装,纯棉',
      seoDescription: '舒适儿童T恤，纯棉材质，卡通印花',

      // 其他属性
      status: 1,
      isFeatured: true,
      isMain: false,  // 非主产品
      isNew: false,   // 非新产品
      sortOrder: 5
    }
  ]

  // 创建工作表
  const worksheet = XLSX.utils.json_to_sheet(templateData)

  // 设置列宽
  const colWidths = [
    { wch: 20 }, // productName
    { wch: 15 }, // productCode
    { wch: 30 }, // category
    { wch: 15 }, // categoryLevel1
    { wch: 15 }, // categoryLevel2
    { wch: 15 }, // categoryLevel3
    { wch: 40 }, // description
    { wch: 20 }, // specifications
    { wch: 10 }, // price
    { wch: 10 }, // marketPrice
    { wch: 10 }, // stockQuantity
    { wch: 15 }, // material
    { wch: 15 }, // fabricType
    { wch: 15 }, // fabricWeight
    { wch: 15 }, // waistStyle
    { wch: 20 }, // jeansStyle
    { wch: 15 }, // patternFit
    { wch: 15 }, // style
    { wch: 15 }, // craftsmanship
    { wch: 15 }, // washingProcess
    { wch: 15 }, // patternType
    { wch: 15 }, // printingMethod
    { wch: 15 }, // fashionElements
    { wch: 15 }, // logoPosition
    { wch: 15 }, // supplyType
    { wch: 15 }, // origin
    { wch: 15 }, // fastSampling
    { wch: 15 }, // model
    { wch: 15 }, // season
    { wch: 15 }, // salesUnit
    { wch: 25 }, // tags
    { wch: 25 }, // seoKeywords
    { wch: 40 }, // seoDescription
    { wch: 8 },  // status
    { wch: 15 }, // isFeatured
    { wch: 8 }   // sortOrder
  ]
  worksheet['!cols'] = colWidths

  const workbook = XLSX.utils.book_new()
  XLSX.utils.book_append_sheet(workbook, worksheet, '产品批量上传模板')

  // 创建说明工作表
  const instructions = [
    // 基本信息
    { 字段名称: 'productName', 中文名称: '产品名称', 说明: '必填，产品的主要名称，最多200字符', 示例: '男士直筒牛仔裤' },
    { 字段名称: 'productCode', 中文名称: '产品编码', 说明: '可选，产品唯一编码，便于管理', 示例: 'PROD-001' },
    { 字段名称: 'description', 中文名称: '产品描述', 说明: '必填，详细的产品描述，最多2000字符', 示例: '经典男士直筒牛仔裤...' },
    { 字段名称: 'specifications', 中文名称: '规格参数', 说明: '可选，产品规格，如尺码、颜色等', 示例: 'S(28-30) M(31-33) L(34-36)' },

    // 分类信息
    { 字段名称: 'category', 中文名称: '完整分类路径', 说明: '可选，格式：一级 > 二级 > 三级', 示例: 'Men > Bottoms > Trousers' },
    { 字段名称: 'categoryLevel1', 中文名称: '一级分类', 说明: '必填，主分类', 示例: 'Men' },
    { 字段名称: 'categoryLevel2', 中文名称: '二级分类', 说明: '可选，子分类', 示例: 'Bottoms' },
    { 字段名称: 'categoryLevel3', 中文名称: '三级分类', 说明: '可选，具体分类', 示例: 'Trousers' },
    
    // 价格库存
    { 字段名称: 'price', 中文名称: '销售价格', 说明: '必填，产品销售价格', 示例: '159.99' },
    { 字段名称: 'marketPrice', 中文名称: '市场价格', 说明: '可选，产品市场价，用于对比', 示例: '199.99' },
    { 字段名称: 'stockQuantity', 中文名称: '库存数量', 说明: '必填，当前可销售库存', 示例: '500' },

    // 材质属性
    { 字段名称: 'material', 中文名称: '材质成分', 说明: '可选，产品材质成分', 示例: '98%棉 2%氨纶' },
    { 字段名称: 'fabricType', 中文名称: '面料类型', 说明: '可选，面料类型分类', 示例: '牛仔布' },
    { 字段名称: 'fabricWeight', 中文名称: '面料重量', 说明: '可选，面料重量规格', 示例: '14盎司' },

    // 款式属性
    { 字段名称: 'waistStyle', 中文名称: '腰型', 说明: '可选，腰部款式', 示例: '中腰' },
    { 字段名称: 'jeansStyle', 中文名称: '牛仔裤款式', 说明: '可选，牛仔裤具体款式', 示例: '直筒牛仔裤' },
    { 字段名称: 'patternFit', 中文名称: '版型', 说明: '可选，产品版型', 示例: '修身版' },
    { 字段名称: 'style', 中文名称: '风格', 说明: '可选，产品风格', 示例: '休闲风格' },

    // 工艺属性
    { 字段名称: 'craftsmanship', 中文名称: '制作工艺', 说明: '可选，制作工艺描述', 示例: '精密缝制工艺，3线拷克' },
    { 字段名称: 'washingProcess', 中文名称: '洗水工艺', 说明: '可选，洗水处理工艺', 示例: '石磨洗水，自然做旧' },

    // 设计属性
    { 字段名称: 'patternType', 中文名称: '图案类型', 说明: '可选，图案设计类型', 示例: '纯色设计' },
    { 字段名称: 'printingMethod', 中文名称: '印刷方法', 说明: '可选，印刷工艺', 示例: '无印刷' },
    { 字段名称: 'fashionElements', 中文名称: '时尚元素', 说明: '可选，时尚设计元素', 示例: '金属纽扣，皮革标签' },
    { 字段名称: 'logoPosition', 中文名称: '标志位置', 说明: '可选，品牌标志位置', 示例: '后袋右下角，刺绣' },

    // 供应属性
    { 字段名称: 'supplyType', 中文名称: '供应类型', 说明: '可选，供应服务类型', 示例: 'OEM Services' },
    { 字段名称: 'origin', 中文名称: '产地', 说明: '可选，产品产地', 示例: '中国广东东莞' },
    { 字段名称: 'fastSampling', 中文名称: '快速打样', 说明: '可选，是否支持7天快速打样', 示例: 'true' },

    // 规格包装
    { 字段名称: 'model', 中文名称: '型号/尺码', 说明: '可选，产品型号或尺码', 示例: 'S/M/L/XL/XXL' },
    { 字段名称: 'season', 中文名称: '适用季节', 说明: '可选，适用季节', 示例: '四季通用' },
    { 字段名称: 'salesUnit', 中文名称: '销售单位', 说明: '可选，销售单位', 示例: '件' },

    // SEO和标签
    { 字段名称: 'tags', 中文名称: '产品标签', 说明: '可选，产品标签，用逗号分隔', 示例: '热销,新品,休闲,经典' },
    { 字段名称: 'seoKeywords', 中文名称: 'SEO关键词', 说明: '可选，搜索引擎关键词', 示例: '牛仔裤,男装,直筒裤' },
    { 字段名称: 'seoDescription', 中文名称: 'SEO描述', 说明: '可选，搜索引擎描述', 示例: '优质男士直筒牛仔裤...' },

    // 其他属性
    { 字段名称: 'status', 中文名称: '产品状态', 说明: '必填，1=上架，0=下架，2=草稿', 示例: '1' },
    { 字段名称: 'isFeatured', 中文名称: '是否推荐', 说明: '可选，是否为推荐/热门产品', 示例: 'true' },
    { 字段名称: 'isMain', 中文名称: '是否主产品', 说明: '可选，是否为主打产品', 示例: 'true' },
    { 字段名称: 'isNew', 中文名称: '是否新产品', 说明: '可选，是否为新产品', 示例: 'true' },
    { 字段名称: 'sortOrder', 中文名称: '显示排序', 说明: '可选，产品显示排序', 示例: '10' }
  ]

  const instructionSheet = XLSX.utils.json_to_sheet(instructions)
  const instructionColWidths = [
    { wch: 20 }, // 字段名称
    { wch: 15 }, // 中文名称
    { wch: 40 }, // 说明
    { wch: 30 }  // 示例
  ]
  instructionSheet['!cols'] = instructionColWidths

  XLSX.utils.book_append_sheet(workbook, instructionSheet, '字段说明')

  // 创建三级分类体系说明工作表
  const categories = [
    // Men 分类
    { 一级分类: 'Men', 二级分类: 'Bottoms', 三级分类: 'Trousers', 分类描述: '男士长裤类，包括牛仔裤、休闲裤等' },
    { 一级分类: 'Men', 二级分类: 'Bottoms', 三级分类: 'Shorts', 分类描述: '男士短裤类，夏季必备' },
    { 一级分类: 'Men', 二级分类: 'Tops', 三级分类: 'Hoodie', 分类描述: '男士卫衣，休闲保暖' },
    { 一级分类: 'Men', 二级分类: 'Tops', 三级分类: 'T-shirt', 分类描述: '男士T恤，基础款' },
    { 一级分类: 'Men', 二级分类: 'Tops', 三级分类: 'Vest', 分类描述: '男士背心，夏季清爽' },
    { 一级分类: 'Men', 二级分类: 'Coat/Shirt', 三级分类: 'Suit', 分类描述: '男士西装套装' },
    { 一级分类: 'Men', 二级分类: 'Coat/Shirt', 三级分类: 'Dress Shirt', 分类描述: '男士正装衬衫' },
    { 一级分类: 'Men', 二级分类: 'Coat/Shirt', 三级分类: 'Casual Shirt', 分类描述: '男士休闲衬衫' },

    // Women 分类
    { 一级分类: 'Women', 二级分类: 'Bottoms', 三级分类: 'Trousers', 分类描述: '女士长裤类' },
    { 一级分类: 'Women', 二级分类: 'Bottoms', 三级分类: 'Shorts', 分类描述: '女士短裤类' },
    { 一级分类: 'Women', 二级分类: 'Bottoms', 三级分类: 'Skirts', 分类描述: '女士裙子类' },
    { 一级分类: 'Women', 二级分类: 'Tops', 三级分类: 'Hoodie', 分类描述: '女士卫衣' },
    { 一级分类: 'Women', 二级分类: 'Tops', 三级分类: 'T-shirt', 分类描述: '女士T恤' },
    { 一级分类: 'Women', 二级分类: 'Tops', 三级分类: 'Vest', 分类描述: '女士背心' },
    { 一级分类: 'Women', 二级分类: 'Coat/Shirt', 三级分类: 'Suit', 分类描述: '女士西装套装' },
    { 一级分类: 'Women', 二级分类: 'Coat/Shirt', 三级分类: 'Blouse', 分类描述: '女士衬衫' },
    { 一级分类: 'Women', 二级分类: 'Coat/Shirt', 三级分类: 'Casual Shirt', 分类描述: '女士休闲衬衫' },
    { 一级分类: 'Women', 二级分类: 'Maternity clothing', 三级分类: 'Maternity Tops', 分类描述: '孕妇上装' },
    { 一级分类: 'Women', 二级分类: 'Maternity clothing', 三级分类: 'Maternity Bottoms', 分类描述: '孕妇下装' },
    { 一级分类: 'Women', 二级分类: 'Maternity clothing', 三级分类: 'Maternity Dresses', 分类描述: '孕妇连衣裙' },

    // Children 分类
    { 一级分类: 'Children', 二级分类: 'Girls', 三级分类: 'Trousers', 分类描述: '女童长裤' },
    { 一级分类: 'Children', 二级分类: 'Girls', 三级分类: 'Shorts', 分类描述: '女童短裤' },
    { 一级分类: 'Children', 二级分类: 'Girls', 三级分类: 'Skirts', 分类描述: '女童裙子' },
    { 一级分类: 'Children', 二级分类: 'Girls', 三级分类: 'Tops', 分类描述: '女童上装' },
    { 一级分类: 'Children', 二级分类: 'Girls', 三级分类: 'Hoodie', 分类描述: '女童卫衣' },
    { 一级分类: 'Children', 二级分类: 'Girls', 三级分类: 'T-shirt', 分类描述: '女童T恤' },
    { 一级分类: 'Children', 二级分类: 'Girls', 三级分类: 'Vest', 分类描述: '女童背心' },
    { 一级分类: 'Children', 二级分类: 'Girls', 三级分类: 'Coat/Shirt', 分类描述: '女童外套/衬衫' },
    { 一级分类: 'Children', 二级分类: 'Girls', 三级分类: 'Suit', 分类描述: '女童套装' },
    { 一级分类: 'Children', 二级分类: 'Boys', 三级分类: 'Trousers', 分类描述: '男童长裤' },
    { 一级分类: 'Children', 二级分类: 'Boys', 三级分类: 'Shorts', 分类描述: '男童短裤' },
    { 一级分类: 'Children', 二级分类: 'Boys', 三级分类: 'Tops', 分类描述: '男童上装' },
    { 一级分类: 'Children', 二级分类: 'Boys', 三级分类: 'Hoodie', 分类描述: '男童卫衣' },
    { 一级分类: 'Children', 二级分类: 'Boys', 三级分类: 'T-shirt', 分类描述: '男童T恤' },
    { 一级分类: 'Children', 二级分类: 'Boys', 三级分类: 'Vest', 分类描述: '男童背心' },
    { 一级分类: 'Children', 二级分类: 'Boys', 三级分类: 'Coat/Shirt', 分类描述: '男童外套/衬衫' },
    { 一级分类: 'Children', 二级分类: 'Boys', 三级分类: 'Suit', 分类描述: '男童套装' },

    // Denim hat/Denim bag 分类
    { 一级分类: 'Denim hat/Denim bag', 二级分类: 'Denim Products', 三级分类: 'Denim Baseball Cap', 分类描述: '牛仔棒球帽' },
    { 一级分类: 'Denim hat/Denim bag', 二级分类: 'Denim Products', 三级分类: 'Denim Bucket Hat', 分类描述: '牛仔渔夫帽' },
    { 一级分类: 'Denim hat/Denim bag', 二级分类: 'Denim Products', 三级分类: 'Denim Backpack', 分类描述: '牛仔双肩包' },
    { 一级分类: 'Denim hat/Denim bag', 二级分类: 'Denim Products', 三级分类: 'Denim Shoulder Bag', 分类描述: '牛仔单肩包' },

    // Customization of denim processing accessories 分类
    { 一级分类: 'Customization of denim processing accessories', 二级分类: 'Customization Services', 三级分类: 'Custom Embroidery', 分类描述: '牛仔定制刺绣服务' },
    { 一级分类: 'Customization of denim processing accessories', 二级分类: 'Customization Services', 三级分类: 'Custom Printing', 分类描述: '牛仔定制印刷服务' },
    { 一级分类: 'Customization of denim processing accessories', 二级分类: 'Customization Services', 三级分类: 'Custom Patches', 分类描述: '牛仔定制徽章服务' },

    // Ungrouped 分类
    { 一级分类: 'Ungrouped', 二级分类: 'Other Products', 三级分类: 'Uncategorized', 分类描述: '未分类产品' },
    { 一级分类: 'Ungrouped', 二级分类: 'Other Products', 三级分类: 'Special Items', 分类描述: '特殊产品' },
    { 一级分类: 'Ungrouped', 二级分类: 'Other Products', 三级分类: 'Miscellaneous', 分类描述: '杂项产品' }
  ]

  const categorySheet = XLSX.utils.json_to_sheet(categories)
  const categoryColWidths = [
    { wch: 35 }, // 一级分类
    { wch: 15 }, // 二级分类
    { wch: 20 }, // 三级分类
    { wch: 40 }  // 分类描述
  ]
  categorySheet['!cols'] = categoryColWidths

  XLSX.utils.book_append_sheet(workbook, categorySheet, '分类体系说明')
  XLSX.writeFile(workbook, '产品批量上传完整模板.xlsx')
}

// 文件选择处理
const handleFileSelect = async (event) => {
  const file = event.target.files[0]
  if (!file) return
  await processFile(file)
}

// 拖拽放置处理
const handleDrop = async (event) => {
  isDragOver.value = false
  const file = event.dataTransfer.files[0]
  if (!file) return
  await processFile(file)
}

// 处理文件
const processFile = async (file) => {
  // 文件格式验证
  const validTypes = [
    'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
    'application/vnd.ms-excel'
  ]
  if (!validTypes.includes(file.type) && !file.name.match(/\.(xlsx|xls)$/i)) {
    Swal.fire({
      title: '文件格式错误',
      text: '请选择Excel文件（.xlsx或.xls格式）',
      icon: 'error',
      confirmButtonText: '确定',
      confirmButtonColor: '#1976d2'
    })
    return
  }

  // 文件大小验证
  if (file.size > 10 * 1024 * 1024) {
    Swal.fire({
      title: '文件过大',
      text: '文件大小不能超过10MB',
      icon: 'error',
      confirmButtonText: '确定',
      confirmButtonColor: '#1976d2'
    })
    return
  }

  selectedFile.value = file
  await parseExcelFile(file)
}

// Excel文件解析
const parseExcelFile = async (file) => {
  try {
    const data = await file.arrayBuffer()
    const workbook = XLSX.read(data, { type: 'array' })
    const firstSheetName = workbook.SheetNames[0]
    const worksheet = workbook.Sheets[firstSheetName]
    const jsonData = XLSX.utils.sheet_to_json(worksheet)

    if (!Array.isArray(jsonData) || jsonData.length === 0) {
      throw new Error('Excel文件中没有找到有效数据')
    }

    parsedData.value = jsonData
    console.log('解析到的数据:', jsonData)

    Swal.fire({
      title: '解析成功',
      text: `成功解析 ${jsonData.length} 条产品数据`,
      icon: 'success',
      confirmButtonText: '确定',
      confirmButtonColor: '#1976d2',
      timer: 1500
    })
  } catch (error) {
    console.error('Excel解析失败:', error)
    Swal.fire({
      title: '解析失败',
      text: error.message || '无法解析Excel文件',
      icon: 'error',
      confirmButtonText: '确定',
      confirmButtonColor: '#1976d2'
    })
  }
}

// 取消上传
const cancelUpload = async () => {
  const result = await Swal.fire({
    title: '确认取消',
    text: '确定要取消当前的上传操作吗？已上传的数据将被保留。',
    icon: 'question',
    showCancelButton: true,
    confirmButtonText: '确定取消',
    cancelButtonText: '继续上传',
    confirmButtonColor: '#dc3545',
    cancelButtonColor: '#6c757d'
  })

  if (result.isConfirmed) {
    isCancelled.value = true
    uploading.value = false
    Swal.fire({
      title: '已取消',
      text: '上传操作已取消',
      icon: 'info',
      timer: 1500,
      showConfirmButton: false
    })
  }
}

// 批量创建产品
const handleUpload = async () => {
  if (parsedData.value.length === 0) return

  uploading.value = true
  uploadProgress.value = 0
  uploadResult.value = null
  isCancelled.value = false
  currentRowIndex.value = 0
  currentProductName.value = ''

  const results = { successCount: 0, failureCount: 0, errors: [], details: [] }

  try {
    console.log(`开始批量上传 ${parsedData.value.length} 个产品...`)

    for (let i = 0; i < parsedData.value.length; i++) {
      if (isCancelled.value) {
        console.log('上传已被用户取消')
        break
      }

      const excelRow = parsedData.value[i]
      currentRowIndex.value = i
      currentProductName.value = excelRow.productName || `产品${i + 1}`

      try {
        const productData = mapExcelDataToProduct(excelRow, i)
        console.log(`正在创建第${i + 1}个产品 (${i + 2}行): ${productData.productName}`)
        console.log('🖼️ productImages 类型:', typeof productData.productImages, '值:', productData.productImages)
        console.log('🏷️ tags 类型:', typeof productData.tags, '值:', productData.tags)

        await productAPI.createProduct(productData)
        console.log(`第${i + 1}个产品创建成功`)

        results.successCount++
        results.details.push({
          row: i + 2,
          status: 'success',
          productName: productData.productName,
          data: excelRow
        })

        uploadResult.value = { ...results }
      } catch (error) {
        console.error(`第${i + 1}个产品创建失败 (${i + 2}行):`, error)
        results.failureCount++

        let errorMsg = '未知错误'

        if (error.response) {
          const responseData = error.response.data
          errorMsg = responseData?.message || error.message
        } else if (error.request) {
          errorMsg = '网络连接失败，请检查网络设置'
        } else {
          errorMsg = error.message || '处理请求时发生错误'
        }

        results.errors.push(`第${i + 2}行创建失败: ${errorMsg}`)
        results.details.push({
          row: i + 2,
          status: 'failed',
          error: errorMsg,
          data: excelRow.productName || '未命名产品'
        })

        uploadResult.value = { ...results }
      }

      uploadProgress.value = Math.round(((i + 1) / parsedData.value.length) * 100)

      // 添加延迟以避免请求过于频繁
      if (i < parsedData.value.length - 1) {
        await new Promise(resolve => setTimeout(resolve, 500))
      }
    }

    console.log('批量上传完成:', results)
  } catch (error) {
    console.error('批量上传过程中发生严重错误:', error)
    results.errors.push(`批量上传失败: ${error.message}`)
  } finally {
    uploading.value = false
  }

  // 直接触发success事件，关闭窗口，由父组件处理成功提示
  emit('success', results)
}

// 字段映射 - 完整版支持所有产品属性（除媒体资源外）
const mapExcelDataToProduct = (excelRow, index) => {
  // 创建产品对象，包含所有字段
  const product = {
    // ===== 基本信息属性 =====
    productName: excelRow.productName?.toString().trim() || `产品${index + 1}`,
    productCode: excelRow.productCode?.toString().trim() || '',
    description: excelRow.description?.toString().trim() || '',
    specifications: excelRow.specifications?.toString().trim() || '',

    // ===== 三级分类属性 =====
    categoryPath: '',
    categoryLevel1: '',
    categoryLevel2: '',
    categoryLevel3: '',
    
    // ===== 媒体资源属性 - Excel批量上传时跳过媒体资源 =====
    mainImage: '',
    productImages: '[]',  // 初始化为空数组的JSON字符串
    videoPath: '',

    // ===== 价格库存属性 =====
    price: 0,
    marketPrice: 0,
    stockQuantity: 0,

    // ===== 产品标识属性 =====
    isMain: false,  // 是否为主产品
    isNew: false,   // 是否为新产品

    // ===== 服装材质属性 =====
    material: '',
    fabricType: '',
    fabricWeight: '',

    // ===== 服装款式属性 =====
    waistStyle: '',
    jeansStyle: '',
    patternFit: '',
    style: '',

    // ===== 工艺制造属性 =====
    craftsmanship: '',
    washingProcess: '',

    // ===== 图案设计属性 =====
    patternType: '',
    printingMethod: '',
    fashionElements: '',
    logoPosition: '',

    // ===== 供应商业属性 =====
    supplyType: '',
    origin: '',
    fastSampling: false,

    // ===== 规格包装属性 =====
    model: '',
    season: '',
    salesUnit: '',

    // ===== 标签和SEO属性 =====
    tags: '',
    seoKeywords: '',
    seoDescription: '',

    // ===== 其他属性 =====
    status: 1,
    isFeatured: false,
    isMain: false,  // 是否为主产品
    isNew: false,   // 是否为新产品
    sortOrder: 0
  }

  // ===== 分类路径处理 =====
  // 优先使用完整分类路径，然后分别设置各级分类
  if (excelRow.category) {
    product.categoryPath = excelRow.category.toString().trim()
    // 解析完整分类路径到各级分类
    const parts = product.categoryPath.split(' > ').map(part => part.trim()).filter(part => part)
    if (parts.length >= 1) product.categoryLevel1 = parts[0]
    if (parts.length >= 2) product.categoryLevel2 = parts[1]
    if (parts.length >= 3) product.categoryLevel3 = parts[2]
  }

  // 分别处理各级分类（覆盖路径解析的结果）
  if (excelRow.categoryLevel1) product.categoryLevel1 = excelRow.categoryLevel1.toString().trim()
  if (excelRow.categoryLevel2) product.categoryLevel2 = excelRow.categoryLevel2.toString().trim()
  if (excelRow.categoryLevel3) product.categoryLevel3 = excelRow.categoryLevel3.toString().trim()

  // 重建完整分类路径
  const categoryParts = [product.categoryLevel1, product.categoryLevel2, product.categoryLevel3]
    .filter(part => part && part.trim())
  if (categoryParts.length > 0) {
    product.categoryPath = categoryParts.join(' > ')
  }

  // ===== 价格库存处理 =====
  product.price = parseNumberField(excelRow.price, 'price', 159.99)
  product.marketPrice = parseNumberField(excelRow.marketPrice, 'marketPrice', 199.99)
  product.stockQuantity = parseIntegerField(excelRow.stockQuantity, 'stockQuantity', 100)

  // ===== 状态处理 - 支持多种格式 =====
  product.status = parseStatusField(excelRow.status)

  // ===== 基本信息属性处理 =====
  product.description = excelRow.description?.toString().trim() || ''
  product.specifications = excelRow.specifications?.toString().trim() || ''

  // ===== 服装材质属性处理 =====
  product.material = excelRow.material?.toString().trim() || ''
  product.fabricType = excelRow.fabricType?.toString().trim() || ''
  product.fabricWeight = excelRow.fabricWeight?.toString().trim() || ''

  // ===== 服装款式属性处理 =====
  product.waistStyle = excelRow.waistStyle?.toString().trim() || ''
  product.jeansStyle = excelRow.jeansStyle?.toString().trim() || ''
  product.patternFit = excelRow.patternFit?.toString().trim() || ''
  product.style = excelRow.style?.toString().trim() || ''

  // ===== 工艺制造属性处理 =====
  product.craftsmanship = excelRow.craftsmanship?.toString().trim() || ''
  product.washingProcess = excelRow.washingProcess?.toString().trim() || ''

  // ===== 图案设计属性处理 =====
  product.patternType = excelRow.patternType?.toString().trim() || ''
  product.printingMethod = excelRow.printingMethod?.toString().trim() || ''
  product.fashionElements = excelRow.fashionElements?.toString().trim() || ''
  product.logoPosition = excelRow.logoPosition?.toString().trim() || ''

  // ===== 供应商业属性处理 =====
  product.supplyType = excelRow.supplyType?.toString().trim() || ''
  product.origin = excelRow.origin?.toString().trim() || ''
  product.fastSampling = parseBooleanField(excelRow.fastSampling)

  // ===== 规格包装属性处理 =====
  product.model = excelRow.model?.toString().trim() || ''
  product.season = excelRow.season?.toString().trim() || ''
  product.salesUnit = excelRow.salesUnit?.toString().trim() || ''

  // ===== 标签和SEO属性处理 =====
  // tags字段需要数组格式，如果是字符串需要转换
  if (excelRow.tags) {
    if (Array.isArray(excelRow.tags)) {
      product.tags = excelRow.tags
    } else if (typeof excelRow.tags === 'string') {
      // 尝试解析JSON数组字符串
      try {
        const parsedTags = JSON.parse(excelRow.tags)
        product.tags = Array.isArray(parsedTags) ? parsedTags : [excelRow.tags]
      } catch {
        // 如果不是JSON格式，按逗号分割
        product.tags = excelRow.tags.split(',').map(tag => tag.trim()).filter(tag => tag)
      }
    } else {
      product.tags = [String(excelRow.tags)]
    }
  } else {
    product.tags = []
  }

  // productImages需要JSON数组字符串格式
  if (excelRow.productImages) {
    if (Array.isArray(excelRow.productImages)) {
      product.productImages = JSON.stringify(excelRow.productImages)
    } else if (typeof excelRow.productImages === 'string') {
      // 检查是否已经是JSON字符串
      try {
        JSON.parse(excelRow.productImages)
        product.productImages = excelRow.productImages // 已经是JSON字符串
      } catch {
        // 如果不是JSON，尝试转换为JSON数组
        const images = excelRow.productImages.split(',').map(img => img.trim()).filter(img => img)
        product.productImages = JSON.stringify(images)
      }
    } else {
      product.productImages = JSON.stringify([String(excelRow.productImages)])
    }
  } else {
    product.productImages = JSON.stringify([])
  }

  product.seoKeywords = excelRow.seoKeywords?.toString().trim() || ''
  product.seoDescription = excelRow.seoDescription?.toString().trim() || ''

  // ===== 其他属性处理 =====
  product.isFeatured = parseBooleanField(excelRow.isFeatured)
  product.sortOrder = parseIntegerField(excelRow.sortOrder, 'sortOrder', 0)
  
  // ===== 产品标识属性处理 =====
  product.isMain = parseBooleanField(excelRow.isMain)
  product.isNew = parseBooleanField(excelRow.isNew)

  // ===== 数据验证和默认值处理 =====
  // 必填字段验证
  if (!product.productName || product.productName.trim() === '') {
    throw new Error(`第${index + 2}行：产品名称不能为空`)
  }
  if (!product.categoryLevel1 || product.categoryLevel1.trim() === '') {
    throw new Error(`第${index + 2}行：一级分类不能为空`)
  }
  if (product.price <= 0) {
    throw new Error(`第${index + 2}行：销售价格必须大于0`)
  }
  if (product.stockQuantity < 0) {
    throw new Error(`第${index + 2}行：库存数量不能为负数`)
  }

  // ===== 日志记录 =====
  console.log(`📋 Excel行${index + 2}映射完成:`, {
    productName: product.productName,
    categoryPath: product.categoryPath,
    price: product.price,
    stockQuantity: product.stockQuantity,
    status: product.status,
    material: product.material,
    style: product.style,
    fastSampling: product.fastSampling
  })

  return product
}

// 辅助函数：解析数字字段
const parseNumberField = (value, fieldName, defaultValue = 0) => {
  if (value === undefined || value === null || value === '') return defaultValue
  const parsed = parseFloat(value)
  return isNaN(parsed) ? defaultValue : parsed
}

// 辅助函数：解析整数字段
const parseIntegerField = (value, fieldName, defaultValue = 0) => {
  if (value === undefined || value === null || value === '') return defaultValue
  const parsed = parseInt(value)
  return isNaN(parsed) ? defaultValue : parsed
}

// 辅助函数：解析布尔字段
const parseBooleanField = (value) => {
  if (value === undefined || value === null || value === '') return false
  if (typeof value === 'boolean') return value
  if (typeof value === 'number') return value !== 0
  if (typeof value === 'string') {
    const trueValues = ['true', '1', '是', 'yes', 'on', '启用', '支持', '有']
    return trueValues.includes(value.toLowerCase().trim())
  }
  return Boolean(value)
}

// 辅助函数：解析状态字段
const parseStatusField = (value) => {
  if (value === undefined || value === null || value === '') return 1

  if (typeof value === 'number') {
    return [0, 1, 2].includes(value) ? value : 1
  }

  if (typeof value === 'string') {
    const statusMap = {
      // 英文状态
      'published': 1, 'active': 1, 'online': 1,
      'inactive': 0, 'offline': 0, 'disabled': 0,
      'draft': 2,
      // 中文状态
      '上架': 1, '发布': 1, '在线': 1,
      '下架': 0, '离线': 0, '禁用': 0,
      '草稿': 2
    }
    const normalized = value.toLowerCase().trim()
    return statusMap[normalized] !== undefined ? statusMap[normalized] : 1
  }

  return 1
}
</script>

<style scoped>
.excel-upload-modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2000;
}

.excel-upload-modal {
  background: white;
  border-radius: 12px;
  width: 90%;
  max-width: 1000px;
  max-height: 90vh;
  display: flex;
  flex-direction: column;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid #e0e0e0;
}

.modal-header h3 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 10px;
}

.btn-close {
  background: none;
  border: none;
  font-size: 20px;
  cursor: pointer;
  color: #666;
  padding: 5px 10px;
  transition: color 0.2s;
}

.btn-close:hover {
  color: #000;
}

.modal-body {
  padding: 24px;
  overflow-y: auto;
  flex: 1;
}

.template-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  background: #f5f5f5;
  border-radius: 8px;
  margin-bottom: 20px;
}

.template-info {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #666;
  font-size: 14px;
}

.file-select-section {
  margin-bottom: 20px;
}

.file-input {
  display: none;
}

.upload-area {
  border: 2px dashed #ccc;
  border-radius: 8px;
  padding: 40px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
}

.upload-area:hover {
  border-color: #1976d2;
  background: #f5f9ff;
}

.drag-over .upload-area {
  border-color: #1976d2;
  background: #e3f2fd;
}

.upload-area i {
  font-size: 48px;
  color: #1976d2;
  margin-bottom: 16px;
}

.upload-area p {
  margin: 8px 0 4px;
  font-size: 16px;
  color: #333;
}

.selected-file {
  color: #1976d2;
  font-weight: 600;
}

.upload-area small {
  color: #999;
  font-size: 13px;
}

.data-preview {
  margin-top: 20px;
}

.data-preview h4 {
  margin-bottom: 12px;
  font-size: 16px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 8px;
}

.preview-table-container {
  overflow-x: auto;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
}

.preview-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 14px;
}

.preview-table thead {
  background: #f5f5f5;
}

.preview-table th,
.preview-table td {
  padding: 12px;
  text-align: left;
  border-bottom: 1px solid #e0e0e0;
}

.preview-table th {
  font-weight: 600;
  color: #333;
}

.preview-table tbody tr:hover {
  background: #f8f9fa;
}

/* 优化表格行样式 */
.preview-table tbody tr:nth-child(even) {
  background: #fdfdfd;
}

.preview-table tbody tr {
  border-bottom: 1px solid #e9ecef;
  transition: all 0.2s ease;
}

.preview-table tbody tr:hover {
  background: #f0f4f8;
  transform: translateY(1px);
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.status-badge {
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
}

.status-1 {
  background: #e8f5e9;
  color: #2e7d32;
}

.status-0 {
  background: #fff3e0;
  color: #e65100;
}

.status-2 {
  background: #e3f2fd;
  color: #1976d2;
}

.preview-note {
  display: block;
  margin-top: 8px;
  color: #999;
  font-size: 12px;
}

/* 增强的预览表格样式 */
.preview-table thead th[rowspan] {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  font-weight: 600;
  text-align: center;
  vertical-align: middle;
}

.preview-table thead th[colspan] {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  color: white;
  font-weight: 500;
  text-align: center;
  font-size: 13px;
}

.preview-table thead th {
  border-right: 1px solid rgba(255, 255, 255, 0.3);
}

.preview-table thead th:last-child {
  border-right: none;
}

.product-name-cell {
  min-width: 200px;
  max-width: 250px;
}

.product-name-cell strong {
  color: #2c3e50;
  font-weight: 600;
  display: block;
  margin-bottom: 4px;
}

.tags-cell {
  margin-top: 4px;
}

.tag-badge {
  display: inline-block;
  background: #e9ecef;
  color: #495057;
  border: 1px solid #ced4da;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 11px;
  font-weight: 500;
}

.category-cell {
  min-width: 120px;
  line-height: 1.3;
}

.category-cell div {
  font-weight: 500;
  color: #2c3e50;
}

.category-cell .text-muted {
  font-size: 11px;
  color: #6c757d !important;
}

.price-cell {
  font-weight: 600;
  color: #27ae60;
  text-align: right;
  min-width: 80px;
  background: #f8f9fa;
  padding: 6px 8px;
  border: 1px solid #dee2e6;
  border-radius: 4px;
}

.price-cell.muted {
  color: #6c757d;
  font-weight: 500;
  font-size: 13px;
  background: #ffffff;
  border: 1px solid #dee2e6;
}

.stock-cell {
  font-weight: 600;
  text-align: center;
  min-width: 60px;
  color: #495057;
  background: #f8f9fa;
  padding: 6px 8px;
  border: 1px solid #dee2e6;
  border-radius: 4px;
}

/* 增强的表格单元格样式 */
.product-info-cell {
  min-width: 200px;
  max-width: 250px;
  padding: 12px 8px;
}

.product-name {
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 4px;
}

.product-code {
  font-size: 11px;
  color: #6c757d;
  margin-bottom: 6px;
}

.category-cell .category-item {
  font-weight: 500;
  color: #495057;
  font-size: 13px;
  background: #f8f9fa;
  padding: 4px 8px;
  border: 1px solid #dee2e6;
  border-radius: 4px;
  text-align: center;
  margin: 1px 0;
}

.spec-cell {
  font-size: 11px;
  color: #6c757d;
  text-align: center;
  max-width: 100px;
}

.attr-cell {
  font-size: 12px;
  color: #495057;
  text-align: center;
  padding: 8px 4px;
}

.craft-cell {
  text-align: center;
  font-size: 11px;
  color: #495057;
  line-height: 1.3;
}

.supply-cell {
  font-size: 12px;
  color: #495057;
  text-align: center;
  padding: 8px 4px;
}

.fast-sampling-cell {
  text-align: center;
  padding: 6px 4px;
}

.fast-sampling-cell .badge {
  font-size: 10px;
  padding: 4px 10px;
  border-radius: 12px;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  border: none;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.fast-sampling-cell .badge.bg-success {
  background: linear-gradient(135deg, #27ae60 0%, #2ecc71 100%) !important;
  color: white;
}

.fast-sampling-cell .badge.bg-secondary {
  background: linear-gradient(135deg, #7f8c8d 0%, #95a5a6 100%) !important;
  color: white;
}

/* 简洁的表格头部样式 - 实线方框设计 */
.preview-table thead th[rowspan="3"] {
  background: #f8f9fa;
  color: #2c3e50;
  font-weight: 600;
  text-align: center;
  vertical-align: middle;
  font-size: 13px;
  border: 1px solid #dee2e6;
  border-bottom: 2px solid #adb5bd;
}

.preview-table thead th[colspan]:not([colspan="1"]) {
  background: #ffffff;
  color: #495057;
  font-weight: 500;
  text-align: center;
  font-size: 12px;
  padding: 10px 6px;
  border: 1px solid #dee2e6;
  border-bottom: 1px solid #adb5bd;
}

.preview-table thead th:not([rowspan]):not([colspan]) {
  background: #ffffff;
  color: #6c757d;
  font-weight: 500;
  text-align: center;
  font-size: 11px;
  padding: 8px 4px;
  border: 1px solid #dee2e6;
}

/* 最后一列不显示边框 */
.preview-table thead th:last-child {
  border-right: none;
}

/* 响应式表格 */
@media (max-width: 1200px) {
  .preview-table {
    font-size: 12px;
  }

  .product-name-cell {
    min-width: 150px;
    max-width: 180px;
  }
}

@media (max-width: 768px) {
  .preview-table-container {
    overflow-x: auto;
  }

  .preview-table {
    min-width: 800px;
    font-size: 11px;
  }

  .preview-table thead th {
    padding: 8px 6px;
    font-size: 11px;
  }

  .preview-table td {
    padding: 8px 6px;
  }
}

.upload-result {
  margin-top: 20px;
  padding: 16px;
  background: #f5f5f5;
  border-radius: 8px;
}

.result-summary {
  display: flex;
  gap: 20px;
  margin-bottom: 16px;
}

.result-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 600;
}

.result-item.success {
  color: #2e7d32;
}

.result-item.failure {
  color: #c62828;
}

.error-list {
  margin-top: 12px;
}

.error-list h5 {
  font-size: 14px;
  font-weight: 600;
  margin-bottom: 8px;
  color: #c62828;
}

.error-list ul {
  margin: 0;
  padding-left: 20px;
  max-height: 200px;
  overflow-y: auto;
}

.error-list li {
  color: #666;
  font-size: 13px;
  margin-bottom: 4px;
}

.upload-progress {
  margin-top: 20px;
}

.progress-stats {
  display: flex;
  justify-content: space-around;
  margin-bottom: 15px;
}

.progress-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 600;
}

.progress-item i {
  color: #6c757d;
}

.progress-bar {
  width: 100%;
  height: 8px;
  background: #e0e0e0;
  border-radius: 4px;
  overflow: hidden;
  margin-bottom: 15px;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #1976d2, #42a5f5);
  transition: width 0.3s;
}

.progress-info {
  text-align: center;
}

.progress-info p {
  font-size: 14px;
  color: #666;
  margin: 0 0 10px 0;
}

.current-product {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: #495057;
  font-size: 14px;
  padding: 8px;
  background: #f8f9fa;
  border-radius: 4px;
  border: 1px solid #e9ecef;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 16px 24px;
  border-top: 1px solid #e0e0e0;
}
</style>
