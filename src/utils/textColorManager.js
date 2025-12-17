/**
 * 简单的文字颜色管理器
 * 为每个文字段落提供独立的颜色控制
 */

export class TextColorManager {
  constructor() {
    this.defaultColors = {
      primary: '#333333',
      secondary: '#666666',
      muted: '#999999',
      link: '#1976d2',
      accent: '#ff6a00',
      success: '#28a745',
      warning: '#ffc107',
      danger: '#dc3545',
      info: '#17a2b8'
    };

    this.presets = [
      { name: '默认', colors: this.defaultColors },
      {
        name: '深色主题',
        colors: {
          primary: '#ffffff',
          secondary: '#cccccc',
          muted: '#888888',
          link: '#64b5f6',
          accent: '#ff9500',
          success: '#66bb6a',
          warning: '#ffca28',
          danger: '#ef5350',
          info: '#29b6f6'
        }
      },
      {
        name: '蓝色系',
        colors: {
          primary: '#1565c0',
          secondary: '#42a5f5',
          muted: '#90caf9',
          link: '#0d47a1',
          accent: '#ff6f00',
          success: '#2e7d32',
          warning: '#f57c00',
          danger: '#c62828',
          info: '#0277bd'
        }
      },
      {
        name: '绿色系',
        colors: {
          primary: '#2e7d32',
          secondary: '#66bb6a',
          muted: '#a5d6a7',
          link: '#1b5e20',
          accent: '#ff8f00',
          success: '#00c853',
          warning: '#ffab00',
          danger: '#d32f2f',
          info: '#00897b'
        }
      }
    ];
  }

  /**
   * 获取默认颜色配置
   */
  getDefaultColors() {
    return { ...this.defaultColors };
  }

  /**
   * 获取所有预设
   */
  getPresets() {
    return [...this.presets];
  }

  /**
   * 应用颜色配置到页面
   */
  applyColors(colors) {
    const root = document.documentElement;

    Object.keys(colors).forEach(key => {
      root.style.setProperty(`--color-${key}`, colors[key]);
    });
  }

  /**
   * 应用预设主题
   */
  applyPreset(presetName) {
    const preset = this.presets.find(p => p.name === presetName);
    if (preset) {
      this.applyColors(preset.colors);
      return preset.colors;
    }
    return null;
  }

  /**
   * 设置单个颜色
   */
  setColor(colorType, colorValue) {
    const root = document.documentElement;
    root.style.setProperty(`--color-${colorType}`, colorValue);
  }

  /**
   * 获取当前颜色
   */
  getCurrentColor(colorType) {
    const root = document.documentElement;
    return getComputedStyle(root).getPropertyValue(`--color-${colorType}`).trim();
  }

  /**
   * 重置为默认颜色
   */
  resetToDefault() {
    this.applyColors(this.defaultColors);
  }

  /**
   * 保存颜色配置到本地存储
   */
  saveColors(colors) {
    try {
      localStorage.setItem('textColors', JSON.stringify(colors));
    } catch (error) {
      console.error('保存颜色配置失败:', error);
    }
  }

  /**
   * 从本地存储加载颜色配置
   */
  loadColors() {
    try {
      const saved = localStorage.getItem('textColors');
      return saved ? JSON.parse(saved) : null;
    } catch (error) {
      console.error('加载颜色配置失败:', error);
      return null;
    }
  }
}

// 创建全局实例
export const textColorManager = new TextColorManager();

export default textColorManager;