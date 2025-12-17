<template>
  <div class="text-color-panel">
    <div class="panel-header">
      <h3>文字颜色设置</h3>
      <button @click="togglePanel" class="toggle-btn">
        {{ isOpen ? '收起' : '展开' }}
      </button>
    </div>

    <div v-if="isOpen" class="panel-content">
      <!-- 预设主题 -->
      <div class="section">
        <h4>预设主题</h4>
        <div class="preset-buttons">
          <button
            v-for="preset in presets"
            :key="preset.name"
            @click="applyPreset(preset.name)"
            :class="['preset-btn', { active: currentPreset === preset.name }]"
          >
            {{ preset.name }}
          </button>
        </div>
      </div>

      <!-- 自定义颜色 -->
      <div class="section">
        <h4>自定义颜色</h4>
        <div class="color-controls">
          <div
            v-for="(color, type) in colorTypes"
            :key="type"
            class="color-control"
          >
            <label :for="type">{{ color.label }}</label>
            <div class="color-input-wrapper">
              <input
                :id="type"
                type="color"
                :value="currentColor[type]"
                @input="updateColor(type, $event.target.value)"
                class="color-input"
              >
              <input
                type="text"
                :value="currentColor[type]"
                @input="updateColor(type, $event.target.value)"
                class="color-text"
                placeholder="#000000"
              >
            </div>
          </div>
        </div>
      </div>

      <!-- 操作按钮 -->
      <div class="actions">
        <button @click="resetToDefault" class="action-btn secondary">重置默认</button>
        <button @click="saveColors" class="action-btn primary">保存配置</button>
      </div>

      <!-- 快速颜色选择 -->
      <div class="section">
        <h4>快速颜色</h4>
        <div class="quick-colors">
          <button
            v-for="color in quickColors"
            :key="color"
            @click="applyQuickColor(color)"
            :style="{ backgroundColor: color }"
            class="quick-color-btn"
            :title="color"
          >
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { textColorManager } from '../utils/textColorManager.js';

export default {
  name: 'TextColorPanel',
  data() {
    return {
      isOpen: true,
      currentPreset: '默认',
      currentColor: {},
      presets: [],

      colorTypes: {
        primary: { label: '主要文字', default: '#333333' },
        secondary: { label: '次要文字', default: '#666666' },
        muted: { label: '弱化文字', default: '#999999' },
        link: { label: '链接颜色', default: '#1976d2' },
        accent: { label: '强调颜色', default: '#ff6a00' },
        success: { label: '成功颜色', default: '#28a745' },
        warning: { label: '警告颜色', default: '#ffc107' },
        danger: { label: '危险颜色', default: '#dc3545' },
        info: { label: '信息颜色', default: '#17a2b8' }
      },

      quickColors: [
        '#000000', '#ffffff', '#ff0000', '#00ff00', '#0000ff',
        '#ffff00', '#ff00ff', '#00ffff', '#ff6b6b', '#4ecdc4',
        '#45b7d1', '#96ceb4', '#ffeaa7', '#dfe6e9', '#74b9ff',
        '#a29bfe', '#6c5ce7', '#fd79a8', '#fdcb6e', '#e17055'
      ]
    };
  },

  mounted() {
    this.initColors();
    this.loadSavedColors();
  },

  methods: {
    initColors() {
      this.presets = textColorManager.getPresets();
      this.currentColor = textColorManager.getDefaultColors();
    },

    loadSavedColors() {
      const savedColors = textColorManager.loadColors();
      if (savedColors) {
        this.currentColor = { ...this.currentColor, ...savedColors };
        textColorManager.applyColors(savedColors);
      }
    },

    togglePanel() {
      this.isOpen = !this.isOpen;
    },

    updateColor(type, color) {
      // 验证颜色格式
      if (/^#[0-9A-F]{6}$/i.test(color) || /^#[0-9A-F]{3}$/i.test(color)) {
        this.currentColor[type] = color;
        textColorManager.setColor(type, color);
        this.currentPreset = '自定义';
      }
    },

    applyPreset(presetName) {
      const colors = textColorManager.applyPreset(presetName);
      if (colors) {
        this.currentColor = colors;
        this.currentPreset = presetName;
      }
    },

    applyQuickColor(color) {
      // 将快速颜色应用到当前选中的文字类型（默认为主要文字）
      this.updateColor('primary', color);
    },

    resetToDefault() {
      this.currentColor = textColorManager.getDefaultColors();
      textColorManager.resetToDefault();
      this.currentPreset = '默认';
    },

    saveColors() {
      textColorManager.saveColors(this.currentColor);
      this.$emit('colors-saved', this.currentColor);
    },

    getCurrentColors() {
      return { ...this.currentColor };
    }
  }
};
</script>

<style scoped>
.text-color-panel {
  background: #ffffff;
  border: 1px solid #e0e0e0;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  transition: all 0.3s ease;
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  background: #f8f9fa;
  border-bottom: 1px solid #e0e0e0;
}

.panel-header h3 {
  margin: 0;
  color: #333;
  font-size: 18px;
  font-weight: 600;
}

.toggle-btn {
  padding: 6px 12px;
  background: #007bff;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  transition: background 0.3s ease;
}

.toggle-btn:hover {
  background: #0056b3;
}

.panel-content {
  padding: 20px;
}

.section {
  margin-bottom: 24px;
}

.section h4 {
  margin: 0 0 12px 0;
  color: #333;
  font-size: 16px;
  font-weight: 500;
}

.preset-buttons {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.preset-btn {
  padding: 8px 16px;
  background: #f8f9fa;
  border: 1px solid #dee2e6;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s ease;
}

.preset-btn:hover {
  background: #e9ecef;
  border-color: #adb5bd;
}

.preset-btn.active {
  background: #007bff;
  color: white;
  border-color: #007bff;
}

.color-controls {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 16px;
}

.color-control {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.color-control label {
  font-size: 14px;
  color: #495057;
  font-weight: 500;
}

.color-input-wrapper {
  display: flex;
  align-items: center;
  gap: 8px;
}

.color-input {
  width: 40px;
  height: 36px;
  border: 2px solid #dee2e6;
  border-radius: 6px;
  cursor: pointer;
  transition: border-color 0.3s ease;
}

.color-input:hover {
  border-color: #007bff;
}

.color-text {
  flex: 1;
  padding: 8px 12px;
  border: 1px solid #dee2e6;
  border-radius: 6px;
  font-size: 14px;
  font-family: monospace;
}

.color-text:focus {
  outline: none;
  border-color: #007bff;
  box-shadow: 0 0 0 2px rgba(0, 123, 255, 0.25);
}

.actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
  padding-top: 16px;
  border-top: 1px solid #e9ecef;
}

.action-btn {
  padding: 10px 20px;
  border: 1px solid transparent;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.action-btn.primary {
  background: #007bff;
  color: white;
  border-color: #007bff;
}

.action-btn.primary:hover {
  background: #0056b3;
}

.action-btn.secondary {
  background: #6c757d;
  color: white;
  border-color: #6c757d;
}

.action-btn.secondary:hover {
  background: #545b62;
}

.quick-colors {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(32px, 1fr));
  gap: 8px;
}

.quick-color-btn {
  width: 32px;
  height: 32px;
  border: 2px solid #dee2e6;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
}

.quick-color-btn:hover {
  transform: scale(1.1);
  border-color: #007bff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .text-color-panel {
    margin: 10px;
  }

  .panel-header {
    padding: 12px 16px;
  }

  .panel-content {
    padding: 16px;
  }

  .color-controls {
    grid-template-columns: 1fr;
  }

  .actions {
    flex-direction: column;
  }

  .quick-colors {
    grid-template-columns: repeat(auto-fill, minmax(28px, 1fr));
  }

  .quick-color-btn {
    width: 28px;
    height: 28px;
  }
}
</style>