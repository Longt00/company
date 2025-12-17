<template>
  <div class="category-selector-advanced">
    <label class="form-label" :class="{ 'required': required }">
      {{ label }}
    </label>

    <!-- 三级分类选择器 -->
    <div class="category-levels">
      <!-- 一级分类 -->
      <div class="level-group">
        <div class="combined-input">
          <select
            v-model="selectedLevel1"
            @change="handleLevel1Change"
            @focus="handleLevel1Focus"
            class="form-select"
          >
            <option value="">请选择一级分类</option>
            <option v-for="category in level1Categories" :key="category" :value="category">
              {{ category }}
            </option>
          </select>
          <input
            v-model="inputLevel1"
            type="text"
            class="form-input"
            placeholder="请输入一级分类或从上方选择"
            @input="handleLevel1Input"
            @focus="handleLevel1Focus"
          >
        </div>
      </div>

      <!-- 二级分类 -->
      <div v-if="showLevel2" class="level-group">
        <div class="combined-input">
          <select
            v-model="selectedLevel2"
            @change="handleLevel2Change"
            @focus="handleLevel2Focus"
            class="form-select"
            :disabled="!selectedLevel1 && !inputLevel1"
          >
            <option value="">请选择二级分类</option>
            <option v-for="category in level2Categories" :key="category" :value="category">
              {{ category }}
            </option>
          </select>
          <input
            v-model="inputLevel2"
            type="text"
            class="form-input"
            placeholder="请输入二级分类或从上方选择"
            @input="handleLevel2Input"
            @focus="handleLevel2Focus"
            :disabled="!selectedLevel1 && !inputLevel1"
          >
        </div>
      </div>

      <!-- 三级分类 -->
      <div v-if="showLevel3" class="level-group">
        <div class="combined-input">
          <select
            v-model="selectedLevel3"
            @change="handleLevel3Change"
            @focus="handleLevel3Focus"
            class="form-select"
            :disabled="!selectedLevel2 && !inputLevel2"
          >
            <option value="">请选择三级分类</option>
            <option v-for="category in level3Categories" :key="category" :value="category">
              {{ category }}
            </option>
          </select>
          <input
            v-model="inputLevel3"
            type="text"
            class="form-input"
            placeholder="请输入三级分类或从上方选择"
            @input="handleLevel3Input"
            @focus="handleLevel3Focus"
            :disabled="!selectedLevel2 && !inputLevel2"
          >
        </div>
      </div>
    </div>

    <!-- 分类路径显示 -->
    <div v-if="categoryPath" class="category-path">
      <i class="bi bi-folder2-open"></i>
      <span class="path-text">{{ categoryPath }}</span>
    </div>

    <!-- 帮助文本 -->
    <small v-if="helpText" class="form-text">{{ helpText }}</small>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'

const props = defineProps({
  modelValue: {
    type: String,
    default: ''
  },
  label: {
    type: String,
    default: 'Product Category'
  },
  required: {
    type: Boolean,
    default: false
  },
  helpText: {
    type: String,
    default: 'Select product category from hierarchical structure'
  }
})

const emit = defineEmits(['update:modelValue', 'change'])

// 预设分类数据结构 - 全英文
const presetCategories = ref({
  "Men": {
    "Bottoms": ["Trousers", "Shorts"],
    "Tops": ["Hoodie", "T-shirt", "Vest"],
    "Coat/Shirt": ["Suit", "Dress Shirt", "Casual Shirt"]
  },
  "Women": {
    "Bottoms": ["Trousers", "Shorts", "Skirts"],
    "Tops": ["Hoodie", "T-shirt", "Vest"],
    "Coat/Shirt": ["Suit", "Blouse", "Casual Shirt"],
    "Maternity clothing": ["Maternity Tops", "Maternity Bottoms", "Maternity Dresses"]
  },
  "Children": {
    "Girls": ["Trousers", "Shorts", "Skirts", "Tops", "Hoodie", "T-shirt", "Vest", "Coat/Shirt", "Suit"],
    "Boys": ["Trousers", "Shorts", "Tops", "Hoodie", "T-shirt", "Vest", "Coat/Shirt", "Suit"]
  },
  "Denim hat/Denim bag": {
    "Denim Products": ["Denim Baseball Cap", "Denim Bucket Hat", "Denim Backpack", "Denim Shoulder Bag"]
  },
  "Customization of denim processing accessories": {
    "Customization Services": ["Custom Embroidery", "Custom Printing", "Custom Patches"]
  },
  "Ungrouped": {
    "Other Products": ["Uncategorized", "Special Items", "Miscellaneous"]
  }
})

// 选中的分类
const selectedLevel1 = ref('')
const selectedLevel2 = ref('')
const selectedLevel3 = ref('')

// 输入分类（新的统一输入变量）
const inputLevel1 = ref('')
const inputLevel2 = ref('')
const inputLevel3 = ref('')

// 计算属性
const level1Categories = computed(() => {
  return Object.keys(presetCategories.value)
})

const level2Categories = computed(() => {
  if (!selectedLevel1.value || selectedLevel1.value === 'Custom') {
    return []
  }
  return Object.keys(presetCategories.value[selectedLevel1.value] || {})
})

const level3Categories = computed(() => {
  if (!selectedLevel2.value || selectedLevel2.value === 'Custom') {
    return []
  }
  return presetCategories.value[selectedLevel1.value]?.[selectedLevel2.value] || []
})

const showLevel2 = computed(() => {
  return selectedLevel1.value || inputLevel1.value
})

const showLevel3 = computed(() => {
  return selectedLevel2.value || inputLevel2.value
})

const categoryPath = computed(() => {
  const parts = []

  // 一级分类：优先使用输入框内容
  if (inputLevel1.value) {
    parts.push(inputLevel1.value)
  } else if (selectedLevel1.value) {
    parts.push(selectedLevel1.value)
  }

  // 二级分类：优先使用输入框内容
  if (inputLevel2.value) {
    parts.push(inputLevel2.value)
  } else if (selectedLevel2.value) {
    parts.push(selectedLevel2.value)
  }

  // 三级分类：优先使用输入框内容
  if (inputLevel3.value) {
    parts.push(inputLevel3.value)
  } else if (selectedLevel3.value) {
    parts.push(selectedLevel3.value)
  }

  return parts.join(' > ')
})

// 清空选择和输入
const clearSelection = () => {
  selectedLevel1.value = ''
  selectedLevel2.value = ''
  selectedLevel3.value = ''
  inputLevel1.value = ''
  inputLevel2.value = ''
  inputLevel3.value = ''
}

// 解析分类路径的方法
const parseCategoryPath = (path) => {
  if (!path) {
    clearSelection()
    return
  }

  const parts = path.split(' > ').map(p => p.trim()).filter(p => p)

  clearSelection()

  if (parts.length > 0) {
    // 检查第一级是否在预设选项中
    if (level1Categories.value.includes(parts[0])) {
      selectedLevel1.value = parts[0]
    } else {
      inputLevel1.value = parts[0]
    }
  }

  if (parts.length > 1) {
    // 检查第二级是否在预设选项中
    if (level2Categories.value.includes(parts[1])) {
      selectedLevel2.value = parts[1]
    } else {
      inputLevel2.value = parts[1]
    }
  }

  if (parts.length > 2) {
    // 检查第三级是否在预设选项中
    if (level3Categories.value.includes(parts[2])) {
      selectedLevel3.value = parts[2]
    } else {
      inputLevel3.value = parts[2]
    }
  }
}

// 监听器
watch(categoryPath, (newPath) => {
  emit('update:modelValue', newPath)
  emit('change', newPath)
}, { immediate: true })

// 从父组件传入的值解析分类
watch(() => props.modelValue, (newValue) => {
  if (newValue && newValue !== categoryPath.value) {
    parseCategoryPath(newValue)
  }
}, { immediate: true })

// 其他方法
const handleLevel1Change = () => {
  if (selectedLevel1.value) {
    inputLevel1.value = selectedLevel1.value  // 选择的值同步到输入框
  }
  // 清空下级
  selectedLevel2.value = ''
  selectedLevel3.value = ''
  inputLevel2.value = ''
  inputLevel3.value = ''

  emit('update:modelValue', categoryPath.value)
  emit('change', categoryPath.value)
}

const handleLevel2Change = () => {
  if (selectedLevel2.value) {
    inputLevel2.value = selectedLevel2.value  // 选择的值同步到输入框
  }
  // 清空下级
  selectedLevel3.value = ''
  inputLevel3.value = ''

  emit('update:modelValue', categoryPath.value)
  emit('change', categoryPath.value)
}

const handleLevel3Change = () => {
  if (selectedLevel3.value) {
    inputLevel3.value = selectedLevel3.value  // 选择的值同步到输入框
  }

  emit('update:modelValue', categoryPath.value)
  emit('change', categoryPath.value)
}

const handleLevel1Input = () => {
  // 输入时清空选择框，但不清空其他级别的值
  selectedLevel1.value = ''

  emit('update:modelValue', categoryPath.value)
  emit('change', categoryPath.value)
}

const handleLevel2Input = () => {
  // 输入时清空选择框，但不清空其他级别的值
  selectedLevel2.value = ''

  emit('update:modelValue', categoryPath.value)
  emit('change', categoryPath.value)
}

const handleLevel3Input = () => {
  // 输入时清空选择框
  selectedLevel3.value = ''

  emit('update:modelValue', categoryPath.value)
  emit('change', categoryPath.value)
}

// 处理聚焦事件，用于自动清空选择框
const handleLevel1Focus = () => {
  if (inputLevel1.value) {
    selectedLevel1.value = ''  // 有输入内容时清空选择框
  }
}

const handleLevel2Focus = () => {
  if (inputLevel2.value) {
    selectedLevel2.value = ''  // 有输入内容时清空选择框
  }
}

const handleLevel3Focus = () => {
  if (inputLevel3.value) {
    selectedLevel3.value = ''  // 有输入内容时清空选择框
  }
}

onMounted(() => {
  if (props.modelValue) {
    parseCategoryPath(props.modelValue)
  }
})
</script>

<style scoped>
.category-selector-advanced {
  width: 100%;
}

.form-label {
  display: block;
  font-weight: 500;
  color: #495057;
  margin-bottom: 8px;
  font-size: 14px;
}

.form-label.required::after {
  content: ' *';
  color: #dc3545;
}

.category-levels {
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin-bottom: 12px;
}

.level-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.input-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.combined-input {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.combined-input .form-select {
  order: 1; /* 选择框在上 */
  min-width: 200px;
}

.combined-input .form-input {
  order: 2; /* 输入框在下 */
  border: 2px solid #28a745;
  background: #f8fff9;
}

.form-select {
  width: 100%;
  padding: 10px 15px;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  font-size: 14px;
  background: white;
  cursor: pointer;
  transition: all 0.3s;
  font-family: inherit;
  min-width: 200px;
  flex-shrink: 0;
}

.form-select:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 0.2rem rgba(102, 126, 234, 0.15);
}

.form-select:disabled {
  background: #f8f9fa;
  color: #6c757d;
  cursor: not-allowed;
}

.form-input {
  width: 100%;
  padding: 10px 15px;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  font-size: 14px;
  transition: all 0.3s;
  font-family: inherit;
}

.form-input:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 0.2rem rgba(102, 126, 234, 0.15);
}


.category-path {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 12px;
  background: #f8f9fa;
  border-radius: 6px;
  border-left: 3px solid #667eea;
  margin-bottom: 8px;
}

.category-path i {
  color: #667eea;
  font-size: 14px;
}

.path-text {
  font-size: 13px;
  color: #495057;
  font-weight: 500;
}

.form-text {
  font-size: 12px;
  color: #6c757d;
  margin-top: 5px;
  display: block;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .category-levels {
    gap: 10px;
  }

  .level-group {
    gap: 6px;
  }

  .form-select,
  .form-input {
    padding: 8px 12px;
    font-size: 13px;
  }

  .category-path {
    padding: 6px 10px;
  }

  .path-text {
    font-size: 12px;
  }
}

/* 动画效果 */
.form-select,
.form-input {
  animation: fadeIn 0.3s ease-in-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(-5px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
</style>