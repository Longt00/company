/**
 * 主题管理器
 * 负责动态调整页面颜色主题，支持根据背景色自动调整字体颜色
 */

import { ColorContrastHelper } from './colorContrastHelper.js';

export class ThemeManager {
  constructor() {
    this.currentTheme = {
      backgroundType: 'solid',
      backgroundColor: '#ffffff',
      gradientStartColor: '#ffffff',
      gradientEndColor: '#f8f9fa',
      gradientDirection: '45deg',
      textColors: {},
      customTextColors: null // 用户自定义的字体颜色
    };

    this.observers = new Set(); // 主题变化观察者
    this.init();
  }

  /**
   * 初始化主题管理器
   */
  init() {
    // 从 CSS 变量加载初始设置
    this.loadFromCSSVariables();

    // 监听系统主题变化
    if (window.matchMedia) {
      const darkModeQuery = window.matchMedia('(prefers-color-scheme: dark)');
      darkModeQuery.addEventListener('change', this.handleSystemThemeChange.bind(this));
    }

    // 初始化字体颜色
    this.updateTextColors();
  }

  /**
   * 从 CSS 变量加载当前主题设置
   */
  loadFromCSSVariables() {
    const root = document.documentElement;
    const computedStyle = getComputedStyle(root);

    this.currentTheme.backgroundColor = computedStyle.getPropertyValue('--background-color').trim() || '#ffffff';
    this.currentTheme.gradientStartColor = computedStyle.getPropertyValue('--background-gradient-start').trim() || '#ffffff';
    this.currentTheme.gradientEndColor = computedStyle.getPropertyValue('--background-gradient-end').trim() || '#f8f9fa';
    this.currentTheme.gradientDirection = computedStyle.getPropertyValue('--background-gradient-direction').trim() || '45deg';
    this.currentTheme.backgroundType = computedStyle.getPropertyValue('--background-type').trim() || 'solid';
  }

  /**
   * 处理系统主题变化
   */
  handleSystemThemeChange(e) {
    // 可以根据系统主题自动调整
    console.log('系统主题变化:', e.matches ? 'dark' : 'light');
  }

  /**
   * 更新字体颜色
   * @param {boolean} forceRecalculate - 是否强制重新计算
   */
  updateTextColors(forceRecalculate = false) {
    let backgroundColor;

    if (this.currentTheme.backgroundType === 'gradient') {
      backgroundColor = {
        startColor: this.currentTheme.gradientStartColor,
        endColor: this.currentTheme.gradientEndColor
      };
    } else {
      backgroundColor = this.currentTheme.backgroundColor;
    }

    // 如果有自定义字体颜色，使用自定义颜色
    if (this.currentTheme.customTextColors) {
      this.currentTheme.textColors = {
        ...ColorContrastHelper.generateColorScheme(backgroundColor),
        ...this.currentTheme.customTextColors
      };
    } else {
      this.currentTheme.textColors = ColorContrastHelper.generateColorScheme(backgroundColor);
    }

    this.applyTextColors();
    this.notifyObservers();
  }

  /**
   * 应用字体颜色到 CSS 变量
   */
  applyTextColors() {
    const root = document.documentElement;

    root.style.setProperty('--color-text-auto-primary', this.currentTheme.textColors.primary);
    root.style.setProperty('--color-text-auto-secondary', this.currentTheme.textColors.secondary);
    root.style.setProperty('--color-text-auto-disabled', this.currentTheme.textColors.disabled);
    root.style.setProperty('--color-text-auto-link', this.currentTheme.textColors.link);
    root.style.setProperty('--color-text-auto-accent', this.currentTheme.textColors.accent);
  }

  /**
   * 设置纯色背景
   * @param {string} color - 背景颜色
   */
  setSolidBackground(color) {
    this.currentTheme.backgroundType = 'solid';
    this.currentTheme.backgroundColor = color;

    const root = document.documentElement;
    root.style.setProperty('--background-type', 'solid');
    root.style.setProperty('--background-color', color);

    // 移除渐变背景
    document.body.style.background = color;

    this.updateTextColors();
  }

  /**
   * 设置渐变背景
   * @param {string} startColor - 渐变开始颜色
   * @param {string} endColor - 渐变结束颜色
   * @param {string} direction - 渐变方向
   */
  setGradientBackground(startColor, endColor, direction = '45deg') {
    this.currentTheme.backgroundType = 'gradient';
    this.currentTheme.gradientStartColor = startColor;
    this.currentTheme.gradientEndColor = endColor;
    this.currentTheme.gradientDirection = direction;

    const root = document.documentElement;
    root.style.setProperty('--background-type', 'gradient');
    root.style.setProperty('--background-gradient-start', startColor);
    root.style.setProperty('--background-gradient-end', endColor);
    root.style.setProperty('--background-gradient-direction', direction);

    // 应用渐变背景
    const gradient = `linear-gradient(${direction}, ${startColor}, ${endColor})`;
    document.body.style.background = gradient;

    this.updateTextColors();
  }

  /**
   * 设置背景图片
   * @param {string} imageUrl - 图片URL
   * @param {Object} options - 背景图片选项
   */
  setImageBackground(imageUrl, options = {}) {
    this.currentTheme.backgroundType = 'image';

    const root = document.documentElement;
    root.style.setProperty('--background-type', 'image');

    // 应用背景图片
    document.body.style.background = `url(${imageUrl})`;
    document.body.style.backgroundSize = options.size || 'cover';
    document.body.style.backgroundPosition = options.position || 'center';
    document.body.style.backgroundRepeat = options.repeat || 'no-repeat';

    // 对于图片背景，通常建议使用半透明背景和固定字体颜色
    if (options.overlayColor) {
      document.body.style.backgroundColor = options.overlayColor;
    }

    this.updateTextColors();
  }

  /**
   * 设置自定义字体颜色
   * @param {Object} textColors - 自定义字体颜色对象
   */
  setCustomTextColors(textColors) {
    this.currentTheme.customTextColors = { ...textColors };
    this.updateTextColors();
  }

  /**
   * 重置为默认主题
   */
  resetToDefault() {
    this.currentTheme.customTextColors = null;
    this.setSolidBackground('#ffffff');
  }

  /**
   * 获取当前主题信息
   * @returns {Object} 当前主题对象
   */
  getCurrentTheme() {
    return { ...this.currentTheme };
  }

  /**
   * 获取推荐的字体颜色列表
   * @returns {Array} 推荐的颜色列表
   */
  getRecommendedTextColors() {
    let backgroundColor;

    if (this.currentTheme.backgroundType === 'gradient') {
      backgroundColor = {
        startColor: this.currentTheme.gradientStartColor,
        endColor: this.currentTheme.gradientEndColor
      };
    } else {
      backgroundColor = this.currentTheme.backgroundColor;
    }

    return ColorContrastHelper.getRecommendedTextColors(backgroundColor);
  }

  /**
   * 检查当前主题的 WCAG 合规性
   * @param {string} level - WCAG 等级 ('AA' 或 'AAA')
   * @returns {Object} 合规性检查结果
   */
  checkWCAGCompliance(level = 'AA') {
    const results = {
      primary: false,
      secondary: false,
      link: false,
      overall: false
    };

    let backgroundColor;

    if (this.currentTheme.backgroundType === 'gradient') {
      backgroundColor = this.currentTheme.backgroundColor; // 使用渐变中间色
    } else {
      backgroundColor = this.currentTheme.backgroundColor;
    }

    results.primary = ColorContrastHelper.meetsWCAGStandards(
      this.currentTheme.textColors.primary,
      backgroundColor,
      level
    );

    results.secondary = ColorContrastHelper.meetsWCAGStandards(
      this.currentTheme.textColors.secondary,
      backgroundColor,
      level
    );

    results.link = ColorContrastHelper.meetsWCAGStandards(
      this.currentTheme.textColors.link,
      backgroundColor,
      level
    );

    results.overall = results.primary && results.secondary && results.link;

    return results;
  }

  /**
   * 添加主题变化观察者
   * @param {Function} callback - 回调函数
   */
  addObserver(callback) {
    this.observers.add(callback);
  }

  /**
   * 移除主题变化观察者
   * @param {Function} callback - 回调函数
   */
  removeObserver(callback) {
    this.observers.delete(callback);
  }

  /**
   * 通知所有观察者
   */
  notifyObservers() {
    for (const callback of this.observers) {
      try {
        callback(this.getCurrentTheme());
      } catch (error) {
        console.error('主题观察者回调出错:', error);
      }
    }
  }

  /**
   * 保存主题到本地存储
   */
  saveToLocalStorage() {
    try {
      localStorage.setItem('themeConfig', JSON.stringify(this.getCurrentTheme()));
    } catch (error) {
      console.error('保存主题配置失败:', error);
    }
  }

  /**
   * 从本地存储加载主题
   * @returns {boolean} 是否成功加载
   */
  loadFromLocalStorage() {
    try {
      const savedTheme = localStorage.getItem('themeConfig');
      if (savedTheme) {
        const theme = JSON.parse(savedTheme);

        if (theme.backgroundType === 'gradient') {
          this.setGradientBackground(
            theme.gradientStartColor,
            theme.gradientEndColor,
            theme.gradientDirection
          );
        } else if (theme.backgroundType === 'solid') {
          this.setSolidBackground(theme.backgroundColor);
        }

        if (theme.customTextColors) {
          this.setCustomTextColors(theme.customTextColors);
        }

        return true;
      }
    } catch (error) {
      console.error('加载主题配置失败:', error);
    }
    return false;
  }

  /**
   * 应用预设主题
   * @param {string} themeName - 主题名称
   */
  applyPresetTheme(themeName) {
    const themes = {
      'light': {
        backgroundType: 'solid',
        backgroundColor: '#ffffff'
      },
      'dark': {
        backgroundType: 'solid',
        backgroundColor: '#1a1a1a'
      },
      'ocean': {
        backgroundType: 'gradient',
        gradientStartColor: '#667eea',
        gradientEndColor: '#764ba2'
      },
      'sunset': {
        backgroundType: 'gradient',
        gradientStartColor: '#ff6a00',
        gradientEndColor: '#ff9500'
      },
      'forest': {
        backgroundType: 'gradient',
        gradientStartColor: '#134e4a',
        gradientEndColor: '#047857'
      }
    };

    const theme = themes[themeName];
    if (theme) {
      if (theme.backgroundType === 'gradient') {
        this.setGradientBackground(
          theme.gradientStartColor,
          theme.gradientEndColor
        );
      } else {
        this.setSolidBackground(theme.backgroundColor);
      }
    }
  }
}

// 创建全局实例
export const themeManager = new ThemeManager();

export default themeManager;