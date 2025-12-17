/**
 * 颜色对比度工具类
 * 用于根据背景色自动计算最适合的字体颜色，确保文字清晰可读
 */

export class ColorContrastHelper {
  /**
   * 将十六进制颜色转换为 RGB 对象
   * @param {string} hex - 十六进制颜色值 (支持 #RGB, #RRGGBB 格式)
   * @returns {{r: number, g: number, b: number}} RGB 对象
   */
  static hexToRgb(hex) {
    if (!hex) return { r: 0, g: 0, b: 0 };

    // 移除 # 号
    const cleanHex = hex.replace('#', '');

    // 处理简写格式 #RGB
    if (cleanHex.length === 3) {
      return {
        r: parseInt(cleanHex[0] + cleanHex[0], 16),
        g: parseInt(cleanHex[1] + cleanHex[1], 16),
        b: parseInt(cleanHex[2] + cleanHex[2], 16)
      };
    }

    // 处理完整格式 #RRGGBB
    if (cleanHex.length === 6) {
      return {
        r: parseInt(cleanHex.substring(0, 2), 16),
        g: parseInt(cleanHex.substring(2, 4), 16),
        b: parseInt(cleanHex.substring(4, 6), 16)
      };
    }

    return { r: 0, g: 0, b: 0 };
  }

  /**
   * 将 RGB 对象转换为十六进制颜色
   * @param {{r: number, g: number, b: number}} rgb - RGB 对象
   * @returns {string} 十六进制颜色值
   */
  static rgbToHex(rgb) {
    const toHex = (n) => {
      const hex = Math.round(Math.max(0, Math.min(255, n))).toString(16);
      return hex.length === 1 ? '0' + hex : hex;
    };

    return `#${toHex(rgb.r)}${toHex(rgb.g)}${toHex(rgb.b)}`;
  }

  /**
   * 计算 RGB 颜色的相对亮度
   * 基于 WCAG 2.1 标准
   * @param {{r: number, g: number, b: number}} rgb - RGB 对象
   * @returns {number} 相对亮度 (0-1)
   */
  static getLuminance(rgb) {
    const gammaCorrect = (c) => {
      c = c / 255;
      return c <= 0.03928 ? c / 12.92 : Math.pow((c + 0.055) / 1.055, 2.4);
    };

    const r = gammaCorrect(rgb.r);
    const g = gammaCorrect(rgb.g);
    const b = gammaCorrect(rgb.b);

    return 0.2126 * r + 0.7152 * g + 0.0722 * b;
  }

  /**
   * 计算两个颜色之间的对比度
   * 基于 WCAG 2.1 标准
   * @param {string} color1 - 第一个颜色 (十六进制)
   * @param {string} color2 - 第二个颜色 (十六进制)
   * @returns {number} 对比度 (1-21)
   */
  static getContrast(color1, color2) {
    const rgb1 = typeof color1 === 'string' ? this.hexToRgb(color1) : color1;
    const rgb2 = typeof color2 === 'string' ? this.hexToRgb(color2) : color2;

    const lum1 = this.getLuminance(rgb1);
    const lum2 = this.getLuminance(rgb2);

    const brightest = Math.max(lum1, lum2);
    const darkest = Math.min(lum1, lum2);

    return (brightest + 0.05) / (darkest + 0.05);
  }

  /**
   * 根据背景色自动计算最适合的字体颜色
   * @param {string|{startColor: string, endColor: string}} backgroundColor - 背景色，可以是纯色或渐变色
   * @param {Array<string>} presetColors - 预设的字体颜色数组，默认为黑白两色
   * @returns {string} 最适合的字体颜色
   */
  static calculateOptimalTextColor(backgroundColor, presetColors = ['#000000', '#ffffff']) {
    let backgroundRgb;

    // 处理纯色背景
    if (typeof backgroundColor === 'string') {
      backgroundRgb = this.hexToRgb(backgroundColor);
    }
    // 处理渐变背景
    else if (backgroundColor && typeof backgroundColor === 'object') {
      const startRgb = this.hexToRgb(backgroundColor.startColor);
      const endRgb = this.hexToRgb(backgroundColor.endColor);

      // 计算渐变中间色的 RGB 值
      backgroundRgb = {
        r: Math.round((startRgb.r + endRgb.r) / 2),
        g: Math.round((startRgb.g + endRgb.g) / 2),
        b: Math.round((startRgb.b + endRgb.b) / 2)
      };
    } else {
      backgroundRgb = { r: 255, g: 255, b: 255 }; // 默认白色背景
    }

    // 计算背景亮度
    const backgroundLuminance = this.getLuminance(backgroundRgb);

    // 如果有预设颜色，选择对比度最高的
    if (presetColors && presetColors.length > 0) {
      let bestColor = presetColors[0];
      let bestContrast = 0;

      for (const textColor of presetColors) {
        const contrast = this.getContrast(backgroundRgb, textColor);
        if (contrast > bestContrast) {
          bestContrast = contrast;
          bestColor = textColor;
        }
      }

      return bestColor;
    }

    // 默认逻辑：亮背景用深色字，暗背景用浅色字
    return backgroundLuminance > 0.5 ? '#000000' : '#ffffff';
  }

  /**
   * 检查颜色对比度是否符合 WCAG 标准
   * @param {string} textColor - 文字颜色
   * @param {string} backgroundColor - 背景颜色
   * @param {'AA'|'AAA'} level - WCAG 等级
   * @param {'normal'|'large'} textSize - 文字大小
   * @returns {boolean} 是否符合标准
   */
  static meetsWCAGStandards(textColor, backgroundColor, level = 'AA', textSize = 'normal') {
    const contrast = this.getContrast(textColor, backgroundColor);

    const requirements = {
      AA: { normal: 4.5, large: 3.0 },
      AAA: { normal: 7.0, large: 4.5 }
    };

    return contrast >= requirements[level][textSize];
  }

  /**
   * 获取推荐字体颜色集合，按对比度排序
   * @param {string} backgroundColor - 背景颜色
   * @returns {Array<{color: string, contrast: number, isAccessible: boolean}>} 推荐颜色集合
   */
  static getRecommendedTextColors(backgroundColor) {
    const commonTextColors = [
      '#000000', '#ffffff', '#333333', '#666666', '#999999',
      '#ff0000', '#00ff00', '#0000ff', '#ffff00', '#ff00ff',
      '#00ffff', '#ffa500', '#800080', '#008000', '#000080'
    ];

    const recommendations = [];

    for (const textColor of commonTextColors) {
      const contrast = this.getContrast(textColor, backgroundColor);
      recommendations.push({
        color: textColor,
        contrast: Math.round(contrast * 100) / 100,
        isAccessible: this.meetsWCAGStandards(textColor, backgroundColor)
      });
    }

    // 按对比度降序排序
    recommendations.sort((a, b) => b.contrast - a.contrast);

    return recommendations;
  }

  /**
   * 为给定背景生成完整的字体颜色方案
   * @param {string|{startColor: string, endColor: string}} backgroundColor - 背景色
   * @returns {Object} 完整的颜色方案
   */
  static generateColorScheme(backgroundColor) {
    const primaryTextColor = this.calculateOptimalTextColor(backgroundColor);
    const backgroundRgb = typeof backgroundColor === 'string'
      ? this.hexToRgb(backgroundColor)
      : {
          r: Math.round((this.hexToRgb(backgroundColor.startColor).r + this.hexToRgb(backgroundColor.endColor).r) / 2),
          g: Math.round((this.hexToRgb(backgroundColor.startColor).g + this.hexToRgb(backgroundColor.endColor).g) / 2),
          b: Math.round((this.hexToRgb(backgroundColor.startColor).b + this.hexToRgb(backgroundColor.endColor).b) / 2)
        };

    // 生成次要文本颜色（降低对比度但保持可读性）
    const adjustColorForSecondary = (baseColor, factor = 0.7) => {
      const rgb = this.hexToRgb(baseColor);
      const bgLum = this.getLuminance(backgroundRgb);

      if (bgLum > 0.5) {
        // 亮背景，让深色更浅
        return this.rgbToHex({
          r: Math.round(rgb.r + (255 - rgb.r) * (1 - factor)),
          g: Math.round(rgb.g + (255 - rgb.g) * (1 - factor)),
          b: Math.round(rgb.b + (255 - rgb.b) * (1 - factor))
        });
      } else {
        // 暗背景，让浅色更深
        return this.rgbToHex({
          r: Math.round(rgb.r * factor),
          g: Math.round(rgb.g * factor),
          b: Math.round(rgb.b * factor)
        });
      }
    };

    return {
      primary: primaryTextColor,
      secondary: adjustColorForSecondary(primaryTextColor, 0.6),
      tertiary: adjustColorForSecondary(primaryTextColor, 0.4),
      disabled: adjustColorForSecondary(primaryTextColor, 0.3),
      link: primaryTextColor === '#000000' ? '#1976d2' : '#64b5f6',
      success: '#4caf50',
      warning: '#ff9800',
      error: '#f44336',
      info: '#2196f3'
    };
  }
}

export default ColorContrastHelper;