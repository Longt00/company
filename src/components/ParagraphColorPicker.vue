<template>
  <div class="paragraph-color-picker">
    <!-- 段落编辑区 -->
    <div class="paragraph-editor">
      <textarea
        v-model="content"
        :style="{ color: selectedColor }"
        placeholder="在这里输入文字..."
        class="text-input"
        rows="4"
      ></textarea>
    </div>

    <!-- 颜色选择器 -->
    <div class="color-selector">
      <div class="color-controls">
        <!-- 预设颜色 -->
        <div class="preset-colors">
          <div class="section-title">快速选择</div>
          <div class="color-grid">
            <button
              v-for="color in presetColors"
              :key="color"
              @click="selectColor(color)"
              :style="{ backgroundColor: color }"
              :class="['color-btn', { active: selectedColor === color }]"
              :title="color"
            ></button>
          </div>
        </div>

        <!-- 自定义颜色 -->
        <div class="custom-color">
          <div class="section-title">自定义颜色</div>
          <div class="custom-controls">
            <input
              type="color"
              v-model="customColor"
              @input="selectColor(customColor)"
              class="color-input"
            >
            <input
              type="text"
              v-model="colorHex"
              @input="updateColorFromHex"
              placeholder="#000000"
              class="color-text"
            >
            <span class="current-color" :style="{ backgroundColor: selectedColor }"></span>
          </div>
        </div>
      </div>

      <!-- 常用文字颜色 -->
      <div class="text-colors">
        <div class="section-title">常用文字颜色</div>
        <div class="text-color-grid">
          <button
            v-for="textColor in textColorPresets"
            :key="textColor.name"
            @click="selectColor(textColor.color)"
            :style="{ backgroundColor: textColor.color }"
            :class="['text-color-btn', { active: selectedColor === textColor.color }"
            :title="textColor.name"
          >
            <span class="color-preview" :style="{ color: textColor.color }">{{ textColor.name }}</span>
          </button>
        </div>
      </div>
    </div>

    <!-- 操作按钮 -->
    <div class="actions">
      <button @click="resetColor" class="action-btn secondary">重置颜色</button>
      <button @click="applyToAll" class="action-btn primary">应用到所有段落</button>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ParagraphColorPicker',
  props: {
    initialContent: {
      type: String,
      default: ''
    },
    initialColor: {
      type: String,
      default: '#333333'
    },
    placeholder: {
      type: String,
      default: '在这里输入文字...'
    }
  },

  data() {
    return {
      content: this.initialContent,
      selectedColor: this.initialColor,
      customColor: this.initialColor,
      colorHex: this.initialColor,

      presetColors: [
        '#000000', '#ffffff', '#ff0000', '#00ff00', '#0000ff',
        '#ffff00', '#ff00ff', '#00ffff', '#ff6b6b', '#4ecdc4',
        '#45b7d1', '#96ceb4', '#ffeaa7', '#dfe6e9', '#74b9ff',
        '#a29bfe', '#6c5ce7', '#fd79a8', '#fdcb6e', '#e17055',
        '#2d3436', '#636e72', '#b2bec3', '#00b894', '#00cec9',
        '#0984e3', '#6c5ce7', '#a29bfe', '#fd79a8', '#fdcb6e',
        '#e17055', '#d63031', '#74b9ff', '#a29bfe', '#fd79a8'
      ],

      textColorPresets: [
        { name: '默认黑', color: '#333333' },
        { name: '深灰', color: '#666666' },
        { name: '浅灰', color: '#999999' },
        { name: '蓝色', color: '#1976d2' },
        { name: '红色', color: '#dc3545' },
        { name: '绿色', color: '#28a745' },
        { name: '橙色', color: '#ff6a00' },
        { name: '紫色', color: '#6c5ce7' }
      ]
    };
  },

  watch: {
    content(newContent) {
      this.$emit('content-change', {
        content: newContent,
        color: this.selectedColor
      });
    },

    selectedColor(newColor) {
      this.customColor = newColor;
      this.colorHex = newColor;
      this.$emit('color-change', newColor);
    },

    initialContent(newContent) {
      this.content = newContent;
    },

    initialColor(newColor) {
      this.selectedColor = newColor;
      this.customColor = newColor;
      this.colorHex = newColor;
    }
  },

  methods: {
    selectColor(color) {
      this.selectedColor = color;
    },

    updateColorFromHex() {
      // 验证十六进制颜色格式
      if (/^#[0-9A-F]{6}$/i.test(this.colorHex) || /^#[0-9A-F]{3}$/i.test(this.colorHex)) {
        this.selectedColor = this.colorHex;
        this.customColor = this.colorHex;
      }
    },

    resetColor() {
      this.selectedColor = '#333333';
      this.customColor = '#333333';
      this.colorHex = '#333333';
    },

    applyToAll() {
      this.$emit('apply-to-all', this.selectedColor);
    },

    getParagraphData() {
      return {
        content: this.content,
        color: this.selectedColor
      };
    },

    setParagraphData(data) {
      this.content = data.content || '';
      this.selectedColor = data.color || '#333333';
      this.customColor = this.selectedColor;
      this.colorHex = this.selectedColor;
    }
  }
};
</script>

<style scoped>
.paragraph-color-picker {
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  overflow: hidden;
  background: white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.paragraph-editor {
  padding: 16px;
  border-bottom: 1px solid #e0e0e0;
}

.text-input {
  width: 100%;
  padding: 12px;
  border: 1px solid #e0e0e0;
  border-radius: 6px;
  font-size: 16px;
  line-height: 1.5;
  resize: vertical;
  min-height: 80px;
  font-family: inherit;
}

.text-input:focus {
  outline: none;
  border-color: #007bff;
  box-shadow: 0 0 0 2px rgba(0, 123, 255, 0.25);
}

.color-selector {
  padding: 16px;
  background: #f8f9fa;
}

.color-controls {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  margin-bottom: 20px;
}

.section-title {
  font-size: 14px;
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
}

.preset-colors .color-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(30px, 1fr));
  gap: 6px;
}

.color-btn {
  width: 30px;
  height: 30px;
  border: 2px solid transparent;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.color-btn:hover {
  transform: scale(1.1);
  border-color: #007bff;
}

.color-btn.active {
  border-color: #007bff;
  box-shadow: 0 0 0 2px rgba(0, 123, 255, 0.3);
}

.custom-color .custom-controls {
  display: flex;
  align-items: center;
  gap: 10px;
}

.color-input {
  width: 40px;
  height: 36px;
  border: 2px solid #e0e0e0;
  border-radius: 4px;
  cursor: pointer;
}

.color-input:hover {
  border-color: #007bff;
}

.color-text {
  flex: 1;
  padding: 8px;
  border: 1px solid #e0e0e0;
  border-radius: 4px;
  font-size: 14px;
  font-family: monospace;
}

.color-text:focus {
  outline: none;
  border-color: #007bff;
}

.current-color {
  width: 36px;
  height: 36px;
  border: 2px solid #e0e0e0;
  border-radius: 4px;
  transition: border-color 0.2s ease;
}

.current-color:hover {
  border-color: #007bff;
}

.text-colors .text-color-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(100px, 1fr));
  gap: 8px;
}

.text-color-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 8px;
  background: white;
  border: 2px solid #e0e0e0;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.2s ease;
  min-height: 36px;
}

.text-color-btn:hover {
  border-color: #007bff;
  transform: translateY(-1px);
}

.text-color-btn.active {
  border-color: #007bff;
  background: #f8f9ff;
}

.color-preview {
  font-size: 12px;
  font-weight: 600;
}

.actions {
  display: flex;
  gap: 12px;
  padding: 16px;
  background: white;
  border-top: 1px solid #e0e0e0;
}

.action-btn {
  flex: 1;
  padding: 10px 16px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.2s ease;
}

.action-btn.primary {
  background: #007bff;
  color: white;
}

.action-btn.primary:hover {
  background: #0056b3;
}

.action-btn.secondary {
  background: #6c757d;
  color: white;
}

.action-btn.secondary:hover {
  background: #545b62;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .color-controls {
    grid-template-columns: 1fr;
  }

  .preset-colors .color-grid {
    grid-template-columns: repeat(auto-fill, minmax(35px, 1fr));
  }

  .color-btn {
    width: 35px;
    height: 35px;
  }

  .text-colors .text-color-grid {
    grid-template-columns: repeat(auto-fill, minmax(80px, 1fr));
  }

  .custom-color .custom-controls {
    flex-wrap: wrap;
    gap: 8px;
  }

  .actions {
    flex-direction: column;
  }
}
</style>