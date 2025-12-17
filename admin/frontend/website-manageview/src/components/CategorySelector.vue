<template>
  <div class="category-selector">
    <label class="form-label" :class="{ 'required': required }">
      {{ label }}
    </label>

    <!-- 产品分类输入框 -->
    <div class="category-input-group">
      <input
        v-model="categoryValue"
        type="text"
        class="form-input"
        :placeholder="placeholder"
        :maxlength="maxLength"
        @input="handleInputChange"
        @blur="handleBlur"
      />
      <small v-if="helpText" class="form-text">{{ helpText }}</small>
      <small v-if="remainingChars >= 0" class="char-counter">
        {{ remainingChars }}/{{ maxLength }} 字符
      </small>
    </div>
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
    default: '产品分类'
  },
  required: {
    type: Boolean,
    default: false
  },
  helpText: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['update:modelValue', 'change'])

// 根据完整接口文档(3)定义：产品分类为String类型，最大100字符
const categoryValue = ref('')
const maxLength = 100

// 计算属性
const remainingChars = computed(() => {
  return maxLength - (categoryValue.value?.length || 0)
})

const placeholder = computed(() => {
  if (props.required) {
    return '请输入产品分类（必填）'
  }
  return '请输入产品分类（可选，如：服装、软件产品）'
})

// 监听器
watch(categoryValue, (newValue) => {
  emit('update:modelValue', newValue)
})

watch(() => props.modelValue, (newValue) => {
  if (newValue !== categoryValue.value) {
    categoryValue.value = newValue || ''
  }
}, { immediate: true })

// 方法
const handleInputChange = () => {
  emit('change', categoryValue.value)
}

const handleBlur = () => {
  // 失去焦点时触发change事件
  emit('change', categoryValue.value)
}

onMounted(() => {
  categoryValue.value = props.modelValue || ''
})
</script>

<style scoped>
.category-selector {
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

.category-input-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
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

.form-text {
  font-size: 12px;
  color: #6c757d;
  display: block;
}

.char-counter {
  font-size: 11px;
  color: #999;
  text-align: right;
  display: block;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .form-input {
    padding: 8px 12px;
    font-size: 13px;
  }
}

/* 动画效果 */
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