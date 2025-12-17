package com.manage.util;

import com.manage.dto.product.ProductRequest;
import com.manage.entity.Product;

import java.lang.reflect.Field;
import java.math.BigDecimal;

/**
 * 产品字段分析工具类
 * 用于打印和分析产品实体和请求DTO的字段配置
 */
public class ProductFieldsAnalyzer {

    public static void main(String[] args) {
        System.out.println("========== 产品字段配置分析 ==========");
        
        System.out.println("\n【1. Product实体类字段分析】");
        analyzeProductEntity();
        
        System.out.println("\n【2. ProductRequest DTO字段分析】");
        analyzeProductRequest();
        
        System.out.println("\n【3. 前端需要的字段映射】");
        analyzeFrontendMapping();
        
        System.out.println("\n========== 分析完成 ==========");
    }

    private static void analyzeProductEntity() {
        System.out.println("Product.java 实体类字段:");
        System.out.println("===================================");
        
        Field[] fields = Product.class.getDeclaredFields();
        
        for (Field field : fields) {
            String fieldName = field.getName();
            String fieldType = field.getType().getSimpleName();
            Object defaultValue = getDefaultValue(field, fieldType);
            
            System.out.printf("%-25s | %-10s | 默认值: %-10s%n", fieldName, fieldType, defaultValue);
        }
    }

    private static void analyzeProductRequest() {
        System.out.println("ProductRequest.java DTO字段:");
        System.out.println("====================================");
        
        Field[] fields = ProductRequest.class.getDeclaredFields();
        
        for (Field field : fields) {
            String fieldName = field.getName();
            String fieldType = field.getType().getSimpleName();
            Object defaultValue = getDefaultValueForDTO(field, fieldType);
            
            System.out.printf("%-25s | %-10s | 默认值: %-10s%n", fieldName, fieldType, defaultValue);
        }
    }

    private static void analyzeFrontendMapping() {
        System.out.println("前端字段 ↔ 后端字段映射:");
        System.out.println("============================");
        
        String[] frontendFields = {
            "面料类型", "材质", "风格", "原产地", "供应类型", 
            "工艺", "印花方法", "7天快速打样", "徽标位置", "产品特性", "尺寸"
        };
        
        String[] backendFields = {
            "fabricType", "material", "style", "origin", "supplyType",
            "craftsmanship", "printingMethod", "fastSampling", "logoPosition", "fashionElements", "model"
        };
        
        for (int i = 0; i < frontendFields.length; i++) {
            System.out.printf("%-15s ↔ %-20s%n", frontendFields[i], backendFields[i]);
        }
    }

    private static String getDefaultValue(Field field, String fieldType) {
        try {
            // 尝试创建实例来获取默认值
            Product tempProduct = new Product();
            field.setAccessible(true);
            Object value = field.get(tempProduct);
            
            if (value == null) return "null";
            if (value instanceof String && ((String) value).isEmpty()) return "\"\"";
            if (value instanceof Boolean) return value.toString();
            if (value instanceof Integer) return value.toString();
            if (value instanceof BigDecimal) return value.toString();
            
            return value.toString();
        } catch (Exception e) {
            return "无法获取";
        }
    }

    private static String getDefaultValueForDTO(Field field, String fieldType) {
        try {
            // 尝试创建实例来获取默认值
            ProductRequest tempRequest = new ProductRequest();
            field.setAccessible(true);
            Object value = field.get(tempRequest);
            
            if (value == null) return "null";
            if (value instanceof String && ((String) value).isEmpty()) return "\"\"";
            if (value instanceof Boolean) return value.toString();
            if (value instanceof Integer) return value.toString();
            if (value instanceof BigDecimal) return value.toString();
            
            return value.toString();
        } catch (Exception e) {
            return "无法获取";
        }
    }
}