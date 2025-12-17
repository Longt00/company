package com.manage.util;

/**
 * 颜色对比度工具类
 * 用于根据背景色自动计算最适合的字体颜色，确保文字清晰可读
 *
 * @author System
 * @version 1.0
 */
public class ColorContrastUtil {

    /**
     * 将十六进制颜色转换为 RGB 值
     * @param hex 十六进制颜色值（支持 #RGB, #RRGGBB 格式）
     * @return RGB 数组 [r, g, b]
     */
    public static int[] hexToRgb(String hex) {
        if (hex == null || hex.isEmpty()) {
            return new int[]{0, 0, 0};
        }

        // 移除 # 号
        String cleanHex = hex.replace("#", "");

        // 处理简写格式 #RGB
        if (cleanHex.length() == 3) {
            return new int[]{
                Integer.parseInt(cleanHex.substring(0, 1) + cleanHex.substring(0, 1), 16),
                Integer.parseInt(cleanHex.substring(1, 2) + cleanHex.substring(1, 2), 16),
                Integer.parseInt(cleanHex.substring(2, 3) + cleanHex.substring(2, 3), 16)
            };
        }

        // 处理完整格式 #RRGGBB
        if (cleanHex.length() == 6) {
            return new int[]{
                Integer.parseInt(cleanHex.substring(0, 2), 16),
                Integer.parseInt(cleanHex.substring(2, 4), 16),
                Integer.parseInt(cleanHex.substring(4, 6), 16)
            };
        }

        return new int[]{0, 0, 0};
    }

    /**
     * 将 RGB 值转换为十六进制颜色
     * @param r 红色分量 (0-255)
     * @param g 绿色分量 (0-255)
     * @param b 蓝色分量 (0-255)
     * @return 十六进制颜色值
     */
    public static String rgbToHex(int r, int g, int b) {
        return String.format("#%02x%02x%02x",
            Math.max(0, Math.min(255, r)),
            Math.max(0, Math.min(255, g)),
            Math.max(0, Math.min(255, b))
        );
    }

    /**
     * 计算 RGB 颜色的相对亮度
     * 基于 WCAG 2.1 标准
     * @param rgb RGB 数组 [r, g, b]
     * @return 相对亮度 (0.0-1.0)
     */
    public static double getLuminance(int[] rgb) {
        double r = gammaCorrect(rgb[0] / 255.0);
        double g = gammaCorrect(rgb[1] / 255.0);
        double b = gammaCorrect(rgb[2] / 255.0);

        return 0.2126 * r + 0.7152 * g + 0.0722 * b;
    }

    /**
     * Gamma 校正函数
     * @param value 输入值 (0.0-1.0)
     * @return 校正后的值
     */
    private static double gammaCorrect(double value) {
        return value <= 0.03928 ? value / 12.92 : Math.pow((value + 0.055) / 1.055, 2.4);
    }

    /**
     * 计算两个颜色之间的对比度
     * 基于 WCAG 2.1 标准
     * @param color1 第一个颜色（十六进制）
     * @param color2 第二个颜色（十六进制）
     * @return 对比度 (1.0-21.0)
     */
    public static double getContrast(String color1, String color2) {
        int[] rgb1 = hexToRgb(color1);
        int[] rgb2 = hexToRgb(color2);

        double lum1 = getLuminance(rgb1);
        double lum2 = getLuminance(rgb2);

        double brightest = Math.max(lum1, lum2);
        double darkest = Math.min(lum1, lum2);

        return (brightest + 0.05) / (darkest + 0.05);
    }

    /**
     * 根据背景色自动计算最适合的字体颜色
     * @param backgroundColor 背景颜色（十六进制）
     * @return 最适合的字体颜色
     */
    public static String calculateOptimalTextColor(String backgroundColor) {
        int[] rgb = hexToRgb(backgroundColor);
        double luminance = getLuminance(rgb);

        // 亮背景使用深色字体，暗背景使用浅色字体
        return luminance > 0.5 ? "#000000" : "#ffffff";
    }

    /**
     * 计算渐变背景的中间色
     * @param startColor 渐变开始颜色（十六进制）
     * @param endColor 渐变结束颜色（十六进制）
     * @return 渐变中间色（十六进制）
     */
    public static String calculateGradientMidColor(String startColor, String endColor) {
        int[] startRgb = hexToRgb(startColor);
        int[] endRgb = hexToRgb(endColor);

        int midR = (startRgb[0] + endRgb[0]) / 2;
        int midG = (startRgb[1] + endRgb[1]) / 2;
        int midB = (startRgb[2] + endRgb[2]) / 2;

        return rgbToHex(midR, midG, midB);
    }

    /**
     * 生成完整的字体颜色方案
     * @param backgroundColor 背景颜色（十六进制）
     * @return 字体颜色方案对象
     */
    public static TextColorScheme generateColorScheme(String backgroundColor) {
        String primaryTextColor = calculateOptimalTextColor(backgroundColor);
        int[] backgroundRgb = hexToRgb(backgroundColor);
        double backgroundLuminance = getLuminance(backgroundRgb);

        return new TextColorScheme(
            primaryTextColor,
            adjustColorForSecondary(primaryTextColor, backgroundLuminance, 0.6),
            adjustColorForSecondary(primaryTextColor, backgroundLuminance, 0.3),
            primaryTextColor.equals("#000000") ? "#1976d2" : "#64b5f6",
            "#ff6a00", // 保持品牌橙色作为强调色
            "#4caf50",
            "#ff9800",
            "#f44336",
            "#2196f3"
        );
    }

    /**
     * 为给定的主要颜色生成次要颜色
     * @param primaryColor 主要颜色
     * @param backgroundLuminance 背景亮度
     * @param factor 调整因子 (0.0-1.0)
     * @return 调整后的颜色
     */
    private static String adjustColorForSecondary(String primaryColor, double backgroundLuminance, double factor) {
        int[] rgb = hexToRgb(primaryColor);

        if (backgroundLuminance > 0.5) {
            // 亮背景，让深色更浅
            return rgbToHex(
                Math.round(rgb[0] + (255 - rgb[0]) * (1 - factor)),
                Math.round(rgb[1] + (255 - rgb[1]) * (1 - factor)),
                Math.round(rgb[2] + (255 - rgb[2]) * (1 - factor))
            );
        } else {
            // 暗背景，让浅色更深
            return rgbToHex(
                Math.round(rgb[0] * factor),
                Math.round(rgb[1] * factor),
                Math.round(rgb[2] * factor)
            );
        }
    }

    /**
     * 检查颜色对比度是否符合 WCAG 标准
     * @param textColor 文字颜色
     * @param backgroundColor 背景颜色
     * @param level WCAG 等级 ("AA" 或 "AAA")
     * @param textSize 文字大小 ("normal" 或 "large")
     * @return 是否符合标准
     */
    public static boolean meetsWCAGStandards(String textColor, String backgroundColor,
                                           String level, String textSize) {
        double contrast = getContrast(textColor, backgroundColor);

        if ("AA".equalsIgnoreCase(level)) {
            return "large".equalsIgnoreCase(textSize) ? contrast >= 3.0 : contrast >= 4.5;
        } else if ("AAA".equalsIgnoreCase(level)) {
            return "large".equalsIgnoreCase(textSize) ? contrast >= 4.5 : contrast >= 7.0;
        }

        return false;
    }

    /**
     * 验证十六进制颜色格式
     * @param color 颜色字符串
     * @return 是否为有效的十六进制颜色
     */
    public static boolean isValidHexColor(String color) {
        if (color == null) return false;
        String pattern = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$";
        return color.matches(pattern);
    }

    /**
     * 字体颜色方案数据类
     */
    public static class TextColorScheme {
        private String primary;        // 主要文本颜色
        private String secondary;      // 次要文本颜色
        private String disabled;       // 禁用文本颜色
        private String link;           // 链接颜色
        private String accent;         // 强调色
        private String success;        // 成功色
        private String warning;        // 警告色
        private String error;          // 错误色
        private String info;           // 信息色

        public TextColorScheme() {}

        public TextColorScheme(String primary, String secondary, String disabled,
                             String link, String accent, String success,
                             String warning, String error, String info) {
            this.primary = primary;
            this.secondary = secondary;
            this.disabled = disabled;
            this.link = link;
            this.accent = accent;
            this.success = success;
            this.warning = warning;
            this.error = error;
            this.info = info;
        }

        // Getters and Setters
        public String getPrimary() { return primary; }
        public void setPrimary(String primary) { this.primary = primary; }

        public String getSecondary() { return secondary; }
        public void setSecondary(String secondary) { this.secondary = secondary; }

        public String getDisabled() { return disabled; }
        public void setDisabled(String disabled) { this.disabled = disabled; }

        public String getLink() { return link; }
        public void setLink(String link) { this.link = link; }

        public String getAccent() { return accent; }
        public void setAccent(String accent) { this.accent = accent; }

        public String getSuccess() { return success; }
        public void setSuccess(String success) { this.success = success; }

        public String getWarning() { return warning; }
        public void setWarning(String warning) { this.warning = warning; }

        public String getError() { return error; }
        public void setError(String error) { this.error = error; }

        public String getInfo() { return info; }
        public void setInfo(String info) { this.info = info; }
    }
}