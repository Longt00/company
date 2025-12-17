package com.manage.dto.product;

import org.junit.jupiter.api.Test;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ProductRequest 测试类
 * 测试价格字段的可选性
 */
class ProductRequestTest {

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    @Test
    void testProductRequestWithNullPrice() {
        // 创建一个没有价格的产品请求
        ProductRequest request = new ProductRequest();
        request.setProductName("测试产品");
        request.setProductCode("TEST001");
        request.setCategory("测试分类");
        request.setDescription("测试描述");
        request.setPrice(null); // 价格为null

        // 验证约束违规
        Set<ConstraintViolation<ProductRequest>> violations = validator.validate(request);

        // 由于我们移除了@NotNull注解，应该没有验证错误
        assertTrue(violations.isEmpty(), "价格字段应该可以为null，不应该有验证错误");
    }

    @Test
    void testProductRequestWithZeroPrice() {
        // 创建一个价格为0的产品请求
        ProductRequest request = new ProductRequest();
        request.setProductName("测试产品");
        request.setProductCode("TEST002");
        request.setCategory("测试分类");
        request.setDescription("测试描述");
        request.setPrice(java.math.BigDecimal.ZERO); // 价格为0

        // 验证约束违规
        Set<ConstraintViolation<ProductRequest>> violations = validator.validate(request);

        // 价格为0应该是有效的（因为我们修改了DecimalMin为inclusive=true）
        assertTrue(violations.isEmpty(), "价格字段应该可以为0，不应该有验证错误");
    }

    @Test
    void testProductRequestWithPositivePrice() {
        // 创建一个有正价格的产品请求
        ProductRequest request = new ProductRequest();
        request.setProductName("测试产品");
        request.setProductCode("TEST003");
        request.setCategory("测试分类");
        request.setDescription("测试描述");
        request.setPrice(new java.math.BigDecimal("99.99")); // 正价格

        // 验证约束违规
        Set<ConstraintViolation<ProductRequest>> violations = validator.validate(request);

        // 正价格应该是有效的
        assertTrue(violations.isEmpty(), "正价格应该是有效的");
    }

    @Test
    void testProductRequestWithNegativePrice() {
        // 创建一个负价格的产品请求
        ProductRequest request = new ProductRequest();
        request.setProductName("测试产品");
        request.setProductCode("TEST004");
        request.setCategory("测试分类");
        request.setDescription("测试描述");
        request.setPrice(new java.math.BigDecimal("-10.00")); // 负价格

        // 验证约束违规
        Set<ConstraintViolation<ProductRequest>> violations = validator.validate(request);

        // 负价格应该是无效的
        assertFalse(violations.isEmpty(), "负价格应该是无效的");

        // 检查错误消息是否正确
        boolean hasPriceError = violations.stream()
            .anyMatch(v -> v.getPropertyPath().toString().equals("price"));
        assertTrue(hasPriceError, "应该有关于价格字段的验证错误");
    }
}