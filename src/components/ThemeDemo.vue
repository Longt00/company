<template>
  <div class="theme-demo">
    <div class="demo-header">
      <h1 class="text-auto-primary">动态颜色主题演示</h1>
      <p class="text-auto-secondary">更换背景色，观察字体颜色自动调整</p>
    </div>

    <!-- 背景选择器 -->
    <div class="demo-section card-auto p-4 mb-4">
      <h2 class="text-auto-primary mb-3">背景设置</h2>

      <!-- 纯色背景 -->
      <div class="mb-4">
        <h3 class="text-auto-secondary mb-2">纯色背景</h3>
        <div class="color-grid">
          <button
            v-for="color in solidColors"
            :key="color"
            @click="setSolidBackground(color)"
            :style="{ backgroundColor: color }"
            :class="['color-btn', { 'active': currentTheme.backgroundColor === color }]"
            :title="color">
          </button>
        </div>
      </div>

      <!-- 渐变背景 -->
      <div class="mb-4">
        <h3 class="text-auto-secondary mb-2">渐变背景</h3>
        <div class="gradient-grid">
          <button
            v-for="gradient in gradients"
            :key="gradient.name"
            @click="setGradientBackground(gradient.start, gradient.end)"
            :style="{ background: `linear-gradient(45deg, ${gradient.start}, ${gradient.end})` }"
            :class="['gradient-btn', { 'active': isCurrentGradient(gradient) }]"
            :title="gradient.name">
          </button>
        </div>
      </div>

      <!-- 自定义颜色 -->
      <div class="mb-4">
        <h3 class="text-auto-secondary mb-2">自定义颜色</h3>
        <div class="custom-colors">
          <div class="color-input-group">
            <label class="text-auto-secondary">背景颜色:</label>
            <input
              type="color"
              v-model="customBgColor"
              @change="setSolidBackground(customBgColor)"
              class="color-input">
            <span class="text-auto-secondary ml-2">{{ customBgColor }}</span>
          </div>

          <div class="color-input-group mt-2">
            <label class="text-auto-secondary">渐变开始:</label>
            <input
              type="color"
              v-model="customGradientStart"
              @change="updateCustomGradient"
              class="color-input">
            <span class="text-auto-secondary ml-2">{{ customGradientStart }}</span>
          </div>

          <div class="color-input-group mt-2">
            <label class="text-auto-secondary">渐变结束:</label>
            <input
              type="color"
              v-model="customGradientEnd"
              @change="updateCustomGradient"
              class="color-input">
            <span class="text-auto-secondary ml-2">{{ customGradientEnd }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 文本内容演示 -->
    <div class="demo-section card-auto p-4 mb-4">
      <h2 class="text-auto-primary mb-3">文本颜色演示</h2>

      <div class="text-examples">
        <p class="text-auto-primary">这是主要文本颜色 (Primary Text)</p>
        <p class="text-auto-secondary">这是次要文本颜色 (Secondary Text)</p>
        <p class="text-auto-disabled">这是禁用文本颜色 (Disabled Text)</p>
        <p><a href="#" class="text-auto-link">这是链接颜色 (Link Color)</a></p>
        <p class="text-auto-accent">这是强调色 (Accent Color)</p>
      </div>
    </div>

    <!-- UI 组件演示 -->
    <div class="demo-section card-auto p-4 mb-4">
      <h2 class="text-auto-primary mb-3">UI 组件演示</h2>

      <!-- 按钮演示 -->
      <div class="component-group mb-4">
        <h3 class="text-auto-secondary mb-2">按钮</h3>
        <div class="button-demo">
          <button class="btn-auto mr-2">默认按钮</button>
          <button class="btn-auto-primary mr-2">主要按钮</button>
          <button class="btn-auto" disabled>禁用按钮</button>
        </div>
      </div>

      <!-- 表单演示 -->
      <div class="component-group mb-4">
        <h3 class="text-auto-secondary mb-2">表单</h3>
        <div class="form-demo">
          <input
            type="text"
            class="form-control mb-2"
            placeholder="输入框示例">
          <select class="form-control mb-2">
            <option>选择框示例</option>
            <option>选项1</option>
            <option>选项2</option>
          </select>
          <textarea
            class="form-control"
            rows="3"
            placeholder="文本域示例"></textarea>
        </div>
      </div>

      <!-- 表格演示 -->
      <div class="component-group mb-4">
        <h3 class="text-auto-secondary mb-2">表格</h3>
        <div class="table-demo">
          <table class="table-auto">
            <thead>
              <tr>
                <th>姓名</th>
                <th>年龄</th>
                <th>城市</th>
              </tr>
            </thead>
            <tbody>
              <tr>
                <td>张三</td>
                <td>25</td>
                <td>北京</td>
              </tr>
              <tr>
                <td>李四</td>
                <td>30</td>
                <td>上海</td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <!-- 代码演示 -->
      <div class="component-group">
        <h3 class="text-auto-secondary mb-2">代码块</h3>
        <pre class="code-auto p-3 rounded"><code>const theme = {
  primary: getOptimalTextColor(backgroundColor),
  secondary: getOptimalTextColor(backgroundColor, 0.6)
};</code></pre>
      </div>
    </div>

    <!-- 对比度信息 -->
    <div class="demo-section card-auto p-4 mb-4">
      <h2 class="text-auto-primary mb-3">对比度信息</h2>

      <div class="contrast-info">
        <div class="info-item">
          <span class="text-auto-secondary">主要文本对比度:</span>
          <span class="text-auto-primary ml-2">{{ primaryContrast }}:1</span>
          <span :class="['ml-2', wcagStatus(primaryContrast)]">
            {{ wcagBadge(primaryContrast) }}
          </span>
        </div>
        <div class="info-item">
          <span class="text-auto-secondary">次要文本对比度:</span>
          <span class="text-auto-primary ml-2">{{ secondaryContrast }}:1</span>
          <span :class="['ml-2', wcagStatus(secondaryContrast)]">
            {{ wcagBadge(secondaryContrast) }}
          </span>
        </div>
        <div class="info-item">
          <span class="text-auto-secondary">链接对比度:</span>
          <span class="text-auto-primary ml-2">{{ linkContrast }}:1</span>
          <span :class="['ml-2', wcagStatus(linkContrast)]">
            {{ wcagBadge(linkContrast) }}
          </span>
        </div>
      </div>
    </div>

    <!-- 预设主题 -->
    <div class="demo-section card-auto p-4">
      <h2 class="text-auto-primary mb-3">预设主题</h2>

      <div class="preset-themes">
        <button
          v-for="theme in presetThemes"
          :key="theme.name"
          @click="applyPresetTheme(theme.name)"
          class="preset-btn btn-auto mr-2 mb-2">
          {{ theme.label }}
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import { themeManager } from '../utils/themeManager.js';
import { ColorContrastHelper } from '../utils/colorContrastHelper.js';

export default {
  name: 'ThemeDemo',
  data() {
    return {
      currentTheme: themeManager.getCurrentTheme(),
      customBgColor: '#ffffff',
      customGradientStart: '#667eea',
      customGradientEnd: '#764ba2',

      solidColors: [
        '#ffffff', '#f8f9fa', '#e9ecef', '#dee2e6', '#ced4da',
        '#000000', '#212529', '#343a40', '#495057', '#6c757d',
        '#ff6b6b', '#4ecdc4', '#45b7d1', '#96ceb4', '#ffeaa7',
        '#dfe6e9', '#74b9ff', '#a29bfe', '#6c5ce7', '#fd79a8'
      ],

      gradients: [
        { name: '海洋', start: '#667eea', end: '#764ba2' },
        { name: '日落', start: '#ff6a00', end: '#ff9500' },
        { name: '森林', start: '#134e4a', end: '#047857' },
        { name: '火焰', start: '#f12711', end: '#f5af19' },
        { name: '极光', start: '#43e97b', end: '#38f9d7' },
        { name: '深空', start: '#141e30', end: '#243b55' }
      ],

      presetThemes: [
        { name: 'light', label: '浅色主题' },
        { name: 'dark', label: '深色主题' },
        { name: 'ocean', label: '海洋主题' },
        { name: 'sunset', label: '日落主题' },
        { name: 'forest', label: '森林主题' }
      ]
    };
  },

  computed: {
    primaryContrast() {
      return this.calculateContrast(this.currentTheme.textColors.primary);
    },

    secondaryContrast() {
      return this.calculateContrast(this.currentTheme.textColors.secondary);
    },

    linkContrast() {
      return this.calculateContrast(this.currentTheme.textColors.link);
    }
  },

  mounted() {
    // 监听主题变化
    themeManager.addObserver(this.updateTheme);

    // 加载保存的主题
    themeManager.loadFromLocalStorage();

    // 设置初始自定义颜色值
    const theme = themeManager.getCurrentTheme();
    this.customBgColor = theme.backgroundColor;
    this.customGradientStart = theme.gradientStartColor;
    this.customGradientEnd = theme.gradientEndColor;
  },

  beforeDestroy() {
    themeManager.removeObserver(this.updateTheme);
  },

  methods: {
    updateTheme(theme) {
      this.currentTheme = theme;
    },

    setSolidBackground(color) {
      themeManager.setSolidBackground(color);
      themeManager.saveToLocalStorage();
    },

    setGradientBackground(startColor, endColor, direction = '45deg') {
      themeManager.setGradientBackground(startColor, endColor, direction);
      themeManager.saveToLocalStorage();
    },

    updateCustomGradient() {
      this.setGradientBackground(this.customGradientStart, this.customGradientEnd);
    },

    applyPresetTheme(themeName) {
      themeManager.applyPresetTheme(themeName);
      themeManager.saveToLocalStorage();

      // 更新自定义颜色输入框
      const theme = themeManager.getCurrentTheme();
      this.customBgColor = theme.backgroundColor;
      this.customGradientStart = theme.gradientStartColor;
      this.customGradientEnd = theme.gradientEndColor;
    },

    isCurrentGradient(gradient) {
      const theme = this.currentTheme;
      return theme.backgroundType === 'gradient' &&
             theme.gradientStartColor === gradient.start &&
             theme.gradientEndColor === gradient.end;
    },

    calculateContrast(textColor) {
      let backgroundColor;

      if (this.currentTheme.backgroundType === 'gradient') {
        backgroundColor = ColorContrastHelper.hexToRgb(
          ColorContrastHelper.calculateOptimalTextColor({
            startColor: this.currentTheme.gradientStartColor,
            endColor: this.currentTheme.gradientEndColor
          })
        );
      } else {
        backgroundColor = this.currentTheme.backgroundColor;
      }

      const contrast = ColorContrastHelper.getContrast(textColor, backgroundColor);
      return Math.round(contrast * 100) / 100;
    },

    wcagStatus(contrast) {
      if (contrast >= 7) return 'text-success';
      if (contrast >= 4.5) return 'text-warning';
      return 'text-danger';
    },

    wcagBadge(contrast) {
      if (contrast >= 7) return 'AAA ✅';
      if (contrast >= 4.5) return 'AA ✅';
      return '不合格 ❌';
    }
  }
};
</script>

<style scoped>
.theme-demo {
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
}

.demo-header {
  text-align: center;
  margin-bottom: 40px;
}

.demo-section {
  margin-bottom: 30px;
}

.color-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(40px, 1fr));
  gap: 8px;
}

.color-btn {
  width: 40px;
  height: 40px;
  border: 2px solid transparent;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.color-btn:hover {
  transform: scale(1.1);
}

.color-btn.active {
  border-color: var(--color-text-auto-accent);
  box-shadow: 0 0 0 2px rgba(var(--color-text-auto-accent), 0.3);
}

.gradient-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(80px, 1fr));
  gap: 12px;
}

.gradient-btn {
  width: 80px;
  height: 40px;
  border: 2px solid transparent;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.gradient-btn:hover {
  transform: scale(1.05);
}

.gradient-btn.active {
  border-color: var(--color-text-auto-accent);
  box-shadow: 0 0 0 2px rgba(var(--color-text-auto-accent), 0.3);
}

.custom-colors {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.color-input-group {
  display: flex;
  align-items: center;
  gap: 12px;
}

.color-input {
  width: 60px;
  height: 36px;
  border: 1px solid var(--color-text-auto-disabled);
  border-radius: 6px;
  cursor: pointer;
}

.text-examples {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.component-group {
  margin-bottom: 24px;
}

.button-demo {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.form-demo {
  max-width: 400px;
}

.form-control {
  width: 100%;
  padding: 8px 12px;
  border: 1px solid var(--color-text-auto-disabled);
  border-radius: 6px;
  background-color: rgba(255, 255, 255, 0.1);
  color: var(--color-text-auto-primary);
}

.form-control:focus {
  outline: none;
  border-color: var(--color-text-auto-accent);
  box-shadow: 0 0 0 2px rgba(var(--color-text-auto-accent), 0.2);
}

.table-demo {
  width: 100%;
  max-width: 400px;
}

.table-auto {
  width: 100%;
  border-collapse: collapse;
}

.table-auto th,
.table-auto td {
  padding: 8px 12px;
  text-align: left;
  border: 1px solid var(--color-text-auto-disabled);
}

.code-auto {
  font-family: 'Courier New', monospace;
  font-size: 14px;
  white-space: pre-wrap;
  overflow-x: auto;
}

.contrast-info {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.info-item {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px;
}

.preset-themes {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.preset-btn {
  padding: 8px 16px;
  border: 1px solid var(--color-text-auto-secondary);
  border-radius: 6px;
  background-color: rgba(255, 255, 255, 0.1);
  color: var(--color-text-auto-primary);
  cursor: pointer;
  transition: all 0.3s ease;
}

.preset-btn:hover {
  background-color: rgba(255, 255, 255, 0.2);
  border-color: var(--color-text-auto-primary);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .theme-demo {
    padding: 16px;
  }

  .color-grid {
    grid-template-columns: repeat(auto-fill, minmax(35px, 1fr));
    gap: 6px;
  }

  .color-btn {
    width: 35px;
    height: 35px;
  }

  .gradient-grid {
    grid-template-columns: repeat(auto-fill, minmax(70px, 1fr));
    gap: 8px;
  }

  .gradient-btn {
    width: 70px;
    height: 35px;
  }

  .button-demo {
    flex-direction: column;
    align-items: flex-start;
  }

  .contrast-info {
    gap: 8px;
  }

  .info-item {
    flex-direction: column;
    align-items: flex-start;
  }
}

@media (max-width: 480px) {
  .demo-header h1 {
    font-size: 24px;
  }

  .demo-header p {
    font-size: 16px;
  }

  .color-input-group {
    flex-wrap: wrap;
    align-items: flex-start;
  }
}
</style>