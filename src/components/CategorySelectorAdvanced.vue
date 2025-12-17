<template>
  <div class="category-selector-advanced">
    <div class="selector-container">
      <!-- 一级分类选择 -->
      <div class="category-level">
        <label class="level-label">{{ label || 'Product Category' }}</label>
        <div class="level-select">
          <select
            v-model="selectedLevel1"
            @change="handleLevel1Change"
            class="category-select"
          >
            <option value="">Select Level 1</option>
            <option
              v-for="category in level1Categories"
              :key="category"
              :value="category"
            >
              {{ category }}
            </option>
            <option value="Custom Level 1">+ Custom Level 1</option>
          </select>
        </div>
      </div>

      <!-- 二级分类选择 -->
      <div class="category-level" v-if="showLevel2">
        <div class="level-select">
          <select
            v-model="selectedLevel2"
            @change="handleLevel2Change"
            class="category-select"
          >
            <option value="">Select Level 2</option>
            <option
              v-for="category in level2Categories"
              :key="category"
              :value="category"
            >
              {{ category }}
            </option>
            <option value="Custom Level 2">+ Custom Level 2</option>
          </select>
        </div>
      </div>

      <!-- 三级分类选择 -->
      <div class="category-level" v-if="showLevel3">
        <div class="level-select">
          <select
            v-model="selectedLevel3"
            @change="handleLevel3Change"
            class="category-select"
          >
            <option value="">Select Level 3</option>
            <option
              v-for="category in level3Categories"
              :key="category"
              :value="category"
            >
              {{ category }}
            </option>
            <option value="Custom Level 3">+ Custom Level 3</option>
          </select>
        </div>
      </div>

      <!-- 自定义输入框 -->
      <div class="custom-inputs">
        <input
          v-if="selectedLevel1 === 'Custom Level 1'"
          v-model="customLevel1"
          @input="updateCategoryPath"
          type="text"
          placeholder="Enter custom level 1"
          class="custom-input"
          maxlength="30"
        />
        <input
          v-if="selectedLevel2 === 'Custom Level 2'"
          v-model="customLevel2"
          @input="updateCategoryPath"
          type="text"
          placeholder="Enter custom level 2"
          class="custom-input"
          maxlength="30"
        />
        <input
          v-if="selectedLevel3 === 'Custom Level 3'"
          v-model="customLevel3"
          @input="updateCategoryPath"
          type="text"
          placeholder="Enter custom level 3"
          class="custom-input"
          maxlength="30"
        />
      </div>

      <!-- 帮助文本 -->
      <div v-if="helpText" class="help-text">
        {{ helpText }}
      </div>

      <!-- 必填指示器 -->
      <span v-if="required && !isComplete" class="required-indicator">*</span>
    </div>

    <!-- 当前选择的路径显示 -->
    <div v-if="currentPath" class="current-path">
      <small>Selected: {{ currentPath }}</small>
    </div>
  </div>
</template>

<script>
export default {
  name: 'CategorySelectorAdvanced',
  props: {
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
      default: ''
    }
  },
  emits: ['update:modelValue', 'change'],
  data() {
    return {
      selectedLevel1: '',
      selectedLevel2: '',
      selectedLevel3: '',
      customLevel1: '',
      customLevel2: '',
      customLevel3: '',
      showLevel2: false,
      showLevel3: false,

      // 预设分类数据
      categoryStructure: {
        'Men': {
          'Bottoms': ['Trousers', 'Shorts'],
          'Tops': ['Hoodie', 'T-shirt', 'Vest'],
          'Coat/Shirt': ['Suit', 'Dress Shirt', 'Casual Shirt']
        },
        'Women': {
          'Bottoms': ['Trousers', 'Shorts', 'Skirts'],
          'Tops': ['Hoodie', 'T-shirt', 'Vest'],
          'Coat/Shirt': ['Suit', 'Blouse', 'Casual Shirt'],
          'Maternity clothing': ['Maternity Tops', 'Maternity Bottoms', 'Maternity Dresses']
        },
        'Children': {
          'Girls': ['Trousers', 'Shorts', 'Skirts', 'Tops', 'Hoodie', 'T-shirt', 'Vest', 'Coat/Shirt', 'Suit'],
          'Boys': ['Trousers', 'Shorts', 'Tops', 'Hoodie', 'T-shirt', 'Vest', 'Coat/Shirt', 'Suit']
        },
        'Denim hat/Denim bag': {
          'Denim Products': ['Denim Baseball Cap', 'Denim Bucket Hat', 'Denim Backpack', 'Denim Shoulder Bag']
        },
        'Customization of denim processing accessories': {
          'Customization Services': ['Custom Embroidery', 'Custom Printing', 'Custom Patches']
        },
        'Ungrouped': {
          'Other Products': ['Uncategorized', 'Special Items', 'Miscellaneous']
        }
      }
    }
  },
  computed: {
    level1Categories() {
      return Object.keys(this.categoryStructure)
    },
    level2Categories() {
      if (!this.selectedLevel1 || this.selectedLevel1 === 'Custom Level 1') return []
      return Object.keys(this.categoryStructure[this.selectedLevel1] || {})
    },
    level3Categories() {
      if (!this.selectedLevel1 || !this.selectedLevel2 ||
          this.selectedLevel1 === 'Custom Level 1' ||
          this.selectedLevel2 === 'Custom Level 2') return []
      return this.categoryStructure[this.selectedLevel1]?.[this.selectedLevel2] || []
    },
    currentPath() {
      const parts = []

      if (this.selectedLevel1) {
        parts.push(this.selectedLevel1 === 'Custom Level 1' ? this.customLevel1 : this.selectedLevel1)
      }

      if (this.selectedLevel2) {
        parts.push(this.selectedLevel2 === 'Custom Level 2' ? this.customLevel2 : this.selectedLevel2)
      }

      if (this.selectedLevel3) {
        parts.push(this.selectedLevel3 === 'Custom Level 3' ? this.customLevel3 : this.selectedLevel3)
      }

      return parts.length > 0 ? parts.join(' > ') : ''
    },
    isComplete() {
      return this.currentPath.length > 0
    }
  },
  mounted() {
    this.parseInitialValue()
  },
  methods: {
    parseInitialValue() {
      if (this.modelValue) {
        const parts = this.modelValue.split(' > ').filter(part => part.trim())

        if (parts.length > 0) {
          this.selectedLevel1 = this.level1Categories.includes(parts[0]) ? parts[0] : 'Custom Level 1'
          if (this.selectedLevel1 === 'Custom Level 1') {
            this.customLevel1 = parts[0]
          }
        }

        if (parts.length > 1) {
          this.showLevel2 = true
          this.selectedLevel2 = this.level2Categories.includes(parts[1]) ? parts[1] : 'Custom Level 2'
          if (this.selectedLevel2 === 'Custom Level 2') {
            this.customLevel2 = parts[1]
          }
        }

        if (parts.length > 2) {
          this.showLevel3 = true
          this.selectedLevel3 = this.level3Categories.includes(parts[2]) ? parts[2] : 'Custom Level 3'
          if (this.selectedLevel3 === 'Custom Level 3') {
            this.customLevel3 = parts[2]
          }
        }
      }
    },
    handleLevel1Change() {
      this.selectedLevel2 = ''
      this.selectedLevel3 = ''
      this.customLevel2 = ''
      this.customLevel3 = ''

      if (this.selectedLevel1 && this.selectedLevel1 !== 'Custom Level 1') {
        this.showLevel2 = true
      } else {
        this.showLevel2 = false
        this.showLevel3 = false
      }

      this.updateCategoryPath()
    },
    handleLevel2Change() {
      this.selectedLevel3 = ''
      this.customLevel3 = ''

      if (this.selectedLevel2 && this.selectedLevel2 !== 'Custom Level 2') {
        this.showLevel3 = true
      } else {
        this.showLevel3 = false
      }

      this.updateCategoryPath()
    },
    handleLevel3Change() {
      this.updateCategoryPath()
    },
    updateCategoryPath() {
      const path = this.currentPath

      // 检查字符限制
      if (path.length > 100) {
        this.$emit('change', this.currentPath)
        return // 不更新超出限制的值
      }

      this.$emit('update:modelValue', path)
      this.$emit('change', path)
    }
  }
}
</script>

<style scoped>
.category-selector-advanced {
  width: 100%;
  max-width: 600px;
}

.selector-container {
  display: flex;
  flex-direction: column;
  gap: 12px;
  position: relative;
}

.category-level {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.level-label {
  font-size: 14px;
  font-weight: 500;
  color: #333;
  margin-bottom: 4px;
}

.level-select {
  position: relative;
}

.category-select {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #ddd;
  border-radius: 6px;
  background: #fff;
  font-size: 14px;
  color: #333;
  outline: none;
  transition: all 0.3s ease;
  cursor: pointer;
}

.category-select:focus {
  border-color: #ff6a00;
  box-shadow: 0 0 0 2px rgba(255, 106, 0, 0.1);
}

.custom-inputs {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.custom-input {
  padding: 10px 12px;
  border: 1px solid #ddd;
  border-radius: 6px;
  background: #fff;
  font-size: 14px;
  color: #333;
  outline: none;
  transition: all 0.3s ease;
}

.custom-input:focus {
  border-color: #ff6a00;
  box-shadow: 0 0 0 2px rgba(255, 106, 0, 0.1);
}

.help-text {
  font-size: 12px;
  color: #666;
  margin-top: 4px;
  font-style: italic;
}

.required-indicator {
  position: absolute;
  top: 0;
  right: -12px;
  color: #ff6a00;
  font-weight: bold;
  font-size: 16px;
}

.current-path {
  margin-top: 8px;
  padding: 8px 12px;
  background: #f8f9fa;
  border-radius: 4px;
  border-left: 3px solid #ff6a00;
}

.current-path small {
  color: #666;
  font-size: 12px;
  word-break: break-all;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .category-selector-advanced {
    max-width: 100%;
  }

  .category-select,
  .custom-input {
    font-size: 16px; /* 防止iOS自动放大 */
  }
}

/* 错误状态 */
.category-select.error,
.custom-input.error {
  border-color: #dc3545;
  box-shadow: 0 0 0 2px rgba(220, 53, 69, 0.1);
}

/* 禁用状态 */
.category-select:disabled,
.custom-input:disabled {
  background: #f8f9fa;
  color: #6c757d;
  cursor: not-allowed;
}

/* 加载状态 */
.category-select.loading {
  background-image: url("data:image/svg+xml,%3Csvg width='20' height='20' viewBox='0 0 20 20' fill='none' xmlns='http://www.w3.org/2000/svg'%3E%3Cpath d='M10 3a7 7 0 1 0 0 14 7 7 0 0 0 0-14z' stroke='%23ff6a00' stroke-width='2' stroke-linecap='round' stroke-dasharray='3 3'/%3E%3C/svg%3E");
  background-repeat: no-repeat;
  background-position: right 8px center;
  background-size: 16px;
}
</style>