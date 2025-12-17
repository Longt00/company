package com.manage.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.manage.common.ResultCode;
import com.manage.dto.product.*;
import com.manage.entity.Product;
import com.manage.exception.BusinessException;
import com.manage.repository.MediaFileRepository;
import com.manage.repository.ProductRepository;
import com.manage.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 产品信息服务实现类
 *
 * @author System
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final MediaFileRepository mediaFileRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProductResponse createProduct(ProductRequest request, Long userId) {
        log.info("创建新产品，产品名称：{}，操作用户ID：{}", request.getProductName(), userId);

        // 检查产品编码是否已存在
        if (StringUtils.hasText(request.getProductCode()) &&
            productRepository.existsByProductCode(request.getProductCode())) {
            throw new BusinessException(ResultCode.CONFLICT, "产品编码已存在");
        }

        Product product = new Product();
        BeanUtils.copyProperties(request, product);
        product.setCreatedBy(userId);

        Product savedProduct = productRepository.save(product);
        log.info("产品创建成功，产品ID：{}", savedProduct.getId());

        return convertToResponse(savedProduct);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProductResponse updateProduct(Long id, ProductRequest request, Long userId) {
        log.info("更新产品信息，产品ID：{}，操作用户ID：{}", id, userId);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "产品不存在"));

        // 检查产品编码是否已被其他产品使用
        if (StringUtils.hasText(request.getProductCode()) &&
            !request.getProductCode().equals(product.getProductCode()) &&
            productRepository.existsByProductCode(request.getProductCode())) {
            throw new BusinessException(ResultCode.CONFLICT, "产品编码已存在");
        }

        BeanUtils.copyProperties(request, product);
        product.setUpdatedBy(userId);

        Product savedProduct = productRepository.save(product);
        log.info("产品更新成功，产品ID：{}", savedProduct.getId());

        return convertToResponse(savedProduct);
    }

    @Override
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "产品不存在"));
        return convertToResponse(product);
    }

    @Override
    public ProductResponse getProductByCode(String productCode) {
        Product product = productRepository.findByProductCode(productCode)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "产品不存在"));
        return convertToResponse(product);
    }

    @Override
    public Page<ProductResponse> getProducts(ProductQueryRequest queryRequest) {
        log.info("分页查询产品列表，查询条件：{}", queryRequest);

        // 构建分页和排序参数
        Sort.Direction direction = "ASC".equalsIgnoreCase(queryRequest.getDirection()) ?
                Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(queryRequest.getPage(), queryRequest.getSize(),
                Sort.by(direction, queryRequest.getSort()));

        Page<Product> productPage;

        // 根据查询条件选择不同的查询方法
        if (StringUtils.hasText(queryRequest.getProductName())) {
            productPage = productRepository.findByProductNameAndStatus(
                    queryRequest.getProductName(),
                    queryRequest.getStatus() != null ? queryRequest.getStatus() : 1,
                    pageable);
        } else if (StringUtils.hasText(queryRequest.getCategory())) {
            productPage = productRepository.findByCategoryAndStatus(
                    queryRequest.getCategory(),
                    queryRequest.getStatus() != null ? queryRequest.getStatus() : 1,
                    pageable);
        } else if (queryRequest.getStatus() != null) {
            productPage = productRepository.findByStatus(queryRequest.getStatus(), pageable);
        } else {
            productPage = productRepository.findAll(pageable);
        }

        return productPage.map(this::convertToResponse);
    }

    @Override
    public List<ProductResponse> getActiveProducts() {
        List<Product> products = productRepository.findActiveProducts();
        return products.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductResponse> getFeaturedProducts() {
        List<Product> products = productRepository.findFeaturedProducts();
        return products.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductResponse> getProductsByCategory(String category) {
        List<Product> products = productRepository.findByCategory(category);
        return products.stream()
                .filter(product -> product.getStatus() == 1)
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductResponse> searchProducts(String keyword) {
        List<Product> products = productRepository.findByProductNameContaining(keyword);
        return products.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProductResponse activateProduct(Long id, Long userId) {
        log.info("上架产品，产品ID：{}，操作用户ID：{}", id, userId);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "产品不存在"));

        product.setStatus(1);
        product.setUpdatedBy(userId);

        Product savedProduct = productRepository.save(product);
        log.info("产品上架成功，产品ID：{}", savedProduct.getId());

        return convertToResponse(savedProduct);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProductResponse deactivateProduct(Long id, Long userId) {
        log.info("下架产品，产品ID：{}，操作用户ID：{}", id, userId);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "产品不存在"));

        product.setStatus(0);
        product.setUpdatedBy(userId);

        Product savedProduct = productRepository.save(product);
        log.info("产品下架成功，产品ID：{}", savedProduct.getId());

        return convertToResponse(savedProduct);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteProduct(Long id, Long userId) {
        log.info("删除产品，产品ID：{}，操作用户ID：{}", id, userId);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "产品不存在"));

        // 处理产品关联的媒体文件状态
        updateMediaFilesStatus(product);

        productRepository.delete(product);
        log.info("产品删除成功，产品ID：{}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteProducts(List<Long> ids, Long userId) {
        log.info("批量删除产品，产品ID列表：{}，操作用户ID：{}", ids, userId);

        List<Product> products = productRepository.findAllById(ids);
        if (products.size() != ids.size()) {
            throw new BusinessException(ResultCode.NOT_FOUND, "部分产品不存在");
        }

        // 处理所有产品关联的媒体文件状态
        for (Product product : products) {
            updateMediaFilesStatus(product);
        }

        productRepository.deleteAll(products);
        log.info("批量删除产品成功，删除数量：{}", products.size());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchActivateProducts(List<Long> ids, Long userId) {
        log.info("批量上架产品，产品ID列表：{}，操作用户ID：{}", ids, userId);

        List<Product> products = productRepository.findAllById(ids);
        if (products.size() != ids.size()) {
            throw new BusinessException(ResultCode.NOT_FOUND, "部分产品不存在");
        }

        products.forEach(product -> {
            product.setStatus(1);
            product.setUpdatedBy(userId);
        });

        productRepository.saveAll(products);
        log.info("批量上架产品成功，上架数量：{}", products.size());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDeactivateProducts(List<Long> ids, Long userId) {
        log.info("批量下架产品，产品ID列表：{}，操作用户ID：{}", ids, userId);

        List<Product> products = productRepository.findAllById(ids);
        if (products.size() != ids.size()) {
            throw new BusinessException(ResultCode.NOT_FOUND, "部分产品不存在");
        }

        products.forEach(product -> {
            product.setStatus(0);
            product.setUpdatedBy(userId);
        });

        productRepository.saveAll(products);
        log.info("批量下架产品成功，下架数量：{}", products.size());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProductResponse updateStock(Long id, Integer quantity, Long userId) {
        log.info("更新产品库存，产品ID：{}，新库存：{}，操作用户ID：{}", id, quantity, userId);

        if (quantity < 0) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "库存数量不能为负数");
        }

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "产品不存在"));

        product.setStockQuantity(quantity);
        product.setUpdatedBy(userId);

        Product savedProduct = productRepository.save(product);
        log.info("产品库存更新成功，产品ID：{}", savedProduct.getId());

        return convertToResponse(savedProduct);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProductResponse setAsFeatured(Long id, Long userId) {
        log.info("设置产品为推荐，产品ID：{}，操作用户ID：{}", id, userId);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "产品不存在"));

        product.setIsFeatured(true);
        product.setUpdatedBy(userId);

        Product savedProduct = productRepository.save(product);
        log.info("产品设置为推荐成功，产品ID：{}", savedProduct.getId());

        return convertToResponse(savedProduct);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProductResponse unsetAsFeatured(Long id, Long userId) {
        log.info("取消产品推荐，产品ID：{}，操作用户ID：{}", id, userId);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "产品不存在"));

        product.setIsFeatured(false);
        product.setUpdatedBy(userId);

        Product savedProduct = productRepository.save(product);
        log.info("取消产品推荐成功，产品ID：{}", savedProduct.getId());

        return convertToResponse(savedProduct);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProductResponse uploadProductImages(ProductImageUploadRequest request, Long userId) {
        log.info("上传产品图片，产品ID：{}，图片数量：{}，操作用户ID：{}",
                request.getProductId(), request.getImageUrls().size(), userId);

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "产品不存在"));

        try {
            // 获取现有图片列表
            List<String> existingImages = new ArrayList<>();
            if (StringUtils.hasText(product.getProductImages())) {
                existingImages = objectMapper.readValue(product.getProductImages(),
                        new TypeReference<List<String>>() {});
            }

            // 添加新图片
            existingImages.addAll(request.getImageUrls());

            // 转换为JSON字符串
            String imagesJson = objectMapper.writeValueAsString(existingImages);
            product.setProductImages(imagesJson);

            // 如果需要设置主图
            if (request.getSetAsMainImage() && !request.getImageUrls().isEmpty()) {
                int index = Math.min(request.getMainImageIndex(), request.getImageUrls().size() - 1);
                product.setMainImage(request.getImageUrls().get(index));
            } else if (!StringUtils.hasText(product.getMainImage()) && !request.getImageUrls().isEmpty()) {
                // 如果没有主图，设置第一张为主图
                product.setMainImage(request.getImageUrls().get(0));
            }

            product.setUpdatedBy(userId);
            Product savedProduct = productRepository.save(product);

            log.info("产品图片上传成功，产品ID：{}", savedProduct.getId());
            return convertToResponse(savedProduct);

        } catch (JsonProcessingException e) {
            log.error("处理产品图片JSON失败", e);
            throw new BusinessException(ResultCode.INTERNAL_SERVER_ERROR, "图片处理失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProductResponse deleteProductImage(Long productId, String imageUrl, Long userId) {
        log.info("删除产品图片，产品ID：{}，图片URL：{}，操作用户ID：{}", productId, imageUrl, userId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "产品不存在"));

        try {
            // 获取现有图片列表
            List<String> existingImages = new ArrayList<>();
            if (StringUtils.hasText(product.getProductImages())) {
                existingImages = objectMapper.readValue(product.getProductImages(),
                        new TypeReference<List<String>>() {});
            }

            // 删除指定图片
            boolean removed = existingImages.remove(imageUrl);
            if (!removed) {
                throw new BusinessException(ResultCode.NOT_FOUND, "图片不存在");
            }

            // 更新图片列表
            String imagesJson = objectMapper.writeValueAsString(existingImages);
            product.setProductImages(imagesJson);

            // 如果删除的是主图，重新设置主图
            if (imageUrl.equals(product.getMainImage()) && !existingImages.isEmpty()) {
                product.setMainImage(existingImages.get(0));
            } else if (imageUrl.equals(product.getMainImage()) && existingImages.isEmpty()) {
                product.setMainImage(null);
            }

            product.setUpdatedBy(userId);
            Product savedProduct = productRepository.save(product);

            log.info("产品图片删除成功，产品ID：{}", savedProduct.getId());
            return convertToResponse(savedProduct);

        } catch (JsonProcessingException e) {
            log.error("处理产品图片JSON失败", e);
            throw new BusinessException(ResultCode.INTERNAL_SERVER_ERROR, "图片处理失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProductResponse setMainImage(Long productId, String imageUrl, Long userId) {
        log.info("设置产品主图，产品ID：{}，图片URL：{}，操作用户ID：{}", productId, imageUrl, userId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "产品不存在"));

        // 验证图片是否在产品图片列表中
        try {
            if (StringUtils.hasText(product.getProductImages())) {
                List<String> existingImages = objectMapper.readValue(product.getProductImages(),
                        new TypeReference<List<String>>() {});
                if (!existingImages.contains(imageUrl)) {
                    throw new BusinessException(ResultCode.BAD_REQUEST, "指定图片不在产品图片列表中");
                }
            }
        } catch (JsonProcessingException e) {
            log.error("处理产品图片JSON失败", e);
            throw new BusinessException(ResultCode.INTERNAL_SERVER_ERROR, "图片处理失败");
        }

        product.setMainImage(imageUrl);
        product.setUpdatedBy(userId);

        Product savedProduct = productRepository.save(product);
        log.info("产品主图设置成功，产品ID：{}", savedProduct.getId());

        return convertToResponse(savedProduct);
    }

    @Override
    public boolean existsByProductCode(String productCode) {
        return productRepository.existsByProductCode(productCode);
    }

    @Override
    public List<ProductResponse> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        List<Product> products = productRepository.findByPriceRange(minPrice, maxPrice, 1);
        return products.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductResponse> getProductsByTag(String tag) {
        List<Product> products = productRepository.findByTag(tag);
        return products.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public Object getProductStatistics() {
        List<Object[]> results = productRepository.countProductsByStatus();
        Map<String, Object> statistics = new HashMap<>();

        for (Object[] result : results) {
            Integer status = (Integer) result[0];
            Long count = (Long) result[1];
            String statusText = getStatusText(status);
            statistics.put(statusText, count);
        }

        return statistics;
    }

    /**
     * 转换为响应DTO
     */
    private ProductResponse convertToResponse(Product product) {
        ProductResponse response = new ProductResponse();
        BeanUtils.copyProperties(product, response);

        // 设置状态文本
        response.setStatusText(getStatusText(product.getStatus()));

        // 处理标签字符串转换
        if (StringUtils.hasText(product.getTags())) {
            response.setTags(Arrays.asList(product.getTags().split(",")));
        }

        // 处理产品图片列表
        try {
            if (StringUtils.hasText(product.getProductImages())) {
                List<String> images = objectMapper.readValue(product.getProductImages(),
                        new TypeReference<List<String>>() {});
                response.setProductImages(images);
            }
        } catch (JsonProcessingException e) {
            log.warn("解析产品图片列表失败，产品ID：{}", product.getId(), e);
            response.setProductImages(new ArrayList<>());
        }

        return response;
    }

    @Override
    public ProductResponse saveAsDraft(Long id, Long userId) {
        log.info("保存产品为草稿，产品ID：{}，操作用户ID：{}", id, userId);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "产品不存在"));

        product.setStatus(2); // 设置为草稿状态
        product.setUpdatedBy(userId);

        Product savedProduct = productRepository.save(product);
        log.info("产品保存为草稿成功，产品ID：{}", savedProduct.getId());

        return convertToResponse(savedProduct);
    }

    @Override
    public ProductResponse publishFromDraft(Long id, Long userId) {
        log.info("从草稿发布产品，产品ID：{}，操作用户ID：{}", id, userId);

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "产品不存在"));

        // 检查产品当前状态是否为草稿
        if (product.getStatus() != 2) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "只有草稿状态的产品才能发布");
        }

        // 验证发布产品的必要字段
        validateProductForPublish(product);

        product.setStatus(1); // 设置为上架状态
        product.setUpdatedBy(userId);

        Product savedProduct = productRepository.save(product);
        log.info("产品从草稿发布成功，产品ID：{}", savedProduct.getId());

        return convertToResponse(savedProduct);
    }

    @Override
    public List<ProductResponse> getDraftProducts() {
        log.info("获取草稿产品列表");

        List<Product> draftProducts = productRepository.findByStatus(2);
        return draftProducts.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchSaveAsDraft(List<Long> ids, Long userId) {
        log.info("批量保存为草稿，产品ID列表：{}，操作用户ID：{}", ids, userId);

        List<Product> products = productRepository.findAllById(ids);
        if (products.size() != ids.size()) {
            throw new BusinessException(ResultCode.NOT_FOUND, "部分产品不存在");
        }

        products.forEach(product -> {
            product.setStatus(2); // 设置为草稿状态
            product.setUpdatedBy(userId);
        });

        productRepository.saveAll(products);
        log.info("批量保存为草稿成功，处理数量：{}", products.size());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchPublishFromDraft(List<Long> ids, Long userId) {
        log.info("批量从草稿发布，产品ID列表：{}，操作用户ID：{}", ids, userId);

        List<Product> products = productRepository.findAllById(ids);
        if (products.size() != ids.size()) {
            throw new BusinessException(ResultCode.NOT_FOUND, "部分产品不存在");
        }

        // 验证所有产品都是草稿状态且满足发布条件
        for (Product product : products) {
            if (product.getStatus() != 2) {
                throw new BusinessException(ResultCode.BAD_REQUEST,
                    "产品 " + product.getProductName() + " 不是草稿状态，无法发布");
            }
            validateProductForPublish(product);
        }

        products.forEach(product -> {
            product.setStatus(1); // 设置为上架状态
            product.setUpdatedBy(userId);
        });

        productRepository.saveAll(products);
        log.info("批量从草稿发布成功，处理数量：{}", products.size());
    }

    /**
     * 验证产品是否满足发布条件
     */
    private void validateProductForPublish(Product product) {
        if (!StringUtils.hasText(product.getProductName())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "产品名称不能为空");
        }
        if (product.getPrice() == null || product.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "产品价格不能为空或负数");
        }
        // 可以根据业务需要添加更多验证规则
        log.debug("产品发布验证通过，产品ID：{}", product.getId());
    }

    /**
     * 获取状态文本
     */
    private String getStatusText(Integer status) {
        switch (status) {
            case 0:
                return "已下架";
            case 1:
                return "已上架";
            case 2:
                return "草稿";
            default:
                return "未知";
        }
    }

    // ============ 公开产品查询方法实现 ============

    @Override
    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    @Override
    public Page<Product> getProductsByCategory(String category, Pageable pageable) {
        return productRepository.findByCategory(category, pageable);
    }

    @Override
    public Page<Product> getProductsByCategoryContaining(String category, Pageable pageable) {
        return productRepository.findByCategoryContaining(category, pageable);
    }

    @Override
    public Page<Product> getActiveProducts(Pageable pageable) {
        return productRepository.findByStatus(1, pageable);
    }

    @Override
    public Page<Product> getActiveProductsByCategory(String category, Pageable pageable) {
        return productRepository.findByCategoryAndStatus(category, 1, pageable);
    }

    @Override
    public List<String> getActiveProductCategories() {
        List<Object[]> results = productRepository.findCategoriesByStatus(1);
        return results.stream()
                .map(result -> (String) result[0])
                .filter(Objects::nonNull)
                .sorted()
                .collect(Collectors.toList());
    }

    @Override
    public Page<Product> searchActiveProducts(String keyword, Pageable pageable) {
        return productRepository.findByProductNameContainingIgnoreCaseAndStatus(keyword, 1, pageable);
    }

    @Override
    public Product getActiveProductById(Long id) {
        Optional<Product> product = productRepository.findByIdAndStatus(id, 1);
        return product.orElse(null);
    }

    /**
     * 更新产品关联的媒体文件状态为已删除（状态码：3）
     *
     * @param product 产品实体
     */
    private void updateMediaFilesStatus(Product product) {
        log.info("开始更新产品关联的媒体文件状态，产品ID：{}", product.getId());

        int updatedCount = 0;

        // 1. 处理主图
        if (StringUtils.hasText(product.getMainImage())) {
            try {
                int result = mediaFileRepository.softDeleteByUrl(product.getMainImage());
                updatedCount += result;
                log.debug("主图媒体文件状态更新成功，URL：{}，更新数量：{}", product.getMainImage(), result);
            } catch (Exception e) {
                log.warn("更新主图媒体文件状态失败，URL：{}，错误：{}", product.getMainImage(), e.getMessage());
            }
        }

        // 2. 处理产品图片列表
        if (StringUtils.hasText(product.getProductImages())) {
            try {
                // 解析产品图片列表（可能是JSON格式或逗号分隔格式）
                List<String> imageUrls = parseProductImagesFromJson(product.getProductImages());
                for (String imageUrl : imageUrls) {
                    if (StringUtils.hasText(imageUrl)) {
                        try {
                            int result = mediaFileRepository.softDeleteByUrl(imageUrl.trim());
                            updatedCount += result;
                            log.debug("产品图片媒体文件状态更新成功，URL：{}，更新数量：{}", imageUrl.trim(), result);
                        } catch (Exception e) {
                            log.warn("更新产品图片媒体文件状态失败，URL：{}，错误：{}", imageUrl.trim(), e.getMessage());
                        }
                    }
                }
            } catch (Exception e) {
                log.warn("解析产品图片列表失败，产品ID：{}，错误：{}", product.getId(), e.getMessage());
            }
        }

        // 3. 处理产品视频
        if (StringUtils.hasText(product.getVideoPath())) {
            try {
                int result = mediaFileRepository.softDeleteByUrl(product.getVideoPath());
                updatedCount += result;
                log.debug("产品视频媒体文件状态更新成功，URL：{}，更新数量：{}", product.getVideoPath(), result);
            } catch (Exception e) {
                log.warn("更新产品视频媒体文件状态失败，URL：{}，错误：{}", product.getVideoPath(), e.getMessage());
            }
        }

        log.info("产品关联的媒体文件状态更新完成，产品ID：{}，总共更新数量：{}", product.getId(), updatedCount);
    }

    /**
     * 解析产品图片列表（支持JSON格式和逗号分隔格式）
     *
     * @param productImages 产品图片JSON字符串或逗号分隔字符串
     * @return 图片URL列表
     */
    private List<String> parseProductImagesFromJson(String productImages) {
        List<String> imageUrls = new ArrayList<>();

        if (!StringUtils.hasText(productImages)) {
            return imageUrls;
        }

        try {
            // 尝试按JSON格式解析
            if (productImages.startsWith("[") && productImages.endsWith("]")) {
                imageUrls = objectMapper.readValue(productImages, new TypeReference<List<String>>() {});
            } else {
                // 按逗号分隔格式解析
                String[] urls = productImages.split(",");
                imageUrls = Arrays.asList(urls);
            }
        } catch (Exception e) {
            log.warn("解析产品图片列表失败：{}", e.getMessage());
            // 降级处理：按逗号分隔
            try {
                String[] urls = productImages.split(",");
                imageUrls = Arrays.asList(urls);
            } catch (Exception ex) {
                log.error("降级解析产品图片列表也失败：{}", ex.getMessage());
            }
        }

        return imageUrls.stream()
                .filter(StringUtils::hasText)
                .map(String::trim)
                .collect(Collectors.toList());
    }

    @Override
    public List<com.manage.dto.product.ProductResponse> getNewProducts() {
        List<Product> products = productRepository.findNewProducts();
        return products.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<com.manage.dto.product.ProductResponse> getMainProducts() {
        List<Product> products = productRepository.findMainProducts();
        return products.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<com.manage.dto.product.ProductResponse> getActiveNewProducts() {
        List<Product> products = productRepository.findActiveNewProducts();
        return products.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<com.manage.dto.product.ProductResponse> getActiveMainProducts() {
        List<Product> products = productRepository.findActiveMainProducts();
        return products.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<com.manage.dto.product.ProductResponse> getActiveFeaturedProducts() {
        List<Product> products = productRepository.findActiveFeaturedProducts();
        return products.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ProductResponse setAsMain(Long id, Long userId) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "产品不存在"));

        product.setIsMain(true);
        product.setUpdatedBy(userId);
  
        Product savedProduct = productRepository.save(product);
        log.info("设置产品为主要产品成功，产品ID：{}", id);

        return convertToResponse(savedProduct);
    }

    @Override
    public ProductResponse unsetAsMain(Long id, Long userId) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "产品不存在"));

        product.setIsMain(false);
        product.setUpdatedBy(userId);
  
        Product savedProduct = productRepository.save(product);
        log.info("取消产品主要产品标识成功，产品ID：{}", id);

        return convertToResponse(savedProduct);
    }

    @Override
    public void batchSetAsMain(List<Long> ids, Long userId) {
        List<Product> products = productRepository.findAllById(ids);

        products.forEach(product -> {
            product.setIsMain(true);
            product.setUpdatedBy(userId);
              });

        productRepository.saveAll(products);
        log.info("批量设置产品为主要产品成功，处理数量：{}", products.size());
    }

    @Override
    public void batchUnsetAsMain(List<Long> ids, Long userId) {
        List<Product> products = productRepository.findAllById(ids);

        products.forEach(product -> {
            product.setIsMain(false);
            product.setUpdatedBy(userId);
              });

        productRepository.saveAll(products);
        log.info("批量取消产品主要产品标识成功，处理数量：{}", products.size());
    }

    @Override
    public ProductResponse setAsNew(Long id, Long userId) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "产品不存在"));

        product.setIsNew(true);
        product.setUpdatedBy(userId);
  
        Product savedProduct = productRepository.save(product);
        log.info("设置产品为新品成功，产品ID：{}", id);

        return convertToResponse(savedProduct);
    }

    @Override
    public ProductResponse unsetAsNew(Long id, Long userId) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "产品不存在"));

        product.setIsNew(false);
        product.setUpdatedBy(userId);
  
        Product savedProduct = productRepository.save(product);
        log.info("取消产品新品标识成功，产品ID：{}", id);

        return convertToResponse(savedProduct);
    }

    @Override
    public void batchSetAsNew(List<Long> ids, Long userId) {
        List<Product> products = productRepository.findAllById(ids);

        products.forEach(product -> {
            product.setIsNew(true);
            product.setUpdatedBy(userId);
              });

        productRepository.saveAll(products);
        log.info("批量设置产品为新品成功，处理数量：{}", products.size());
    }

    @Override
    public void batchUnsetAsNew(List<Long> ids, Long userId) {
        List<Product> products = productRepository.findAllById(ids);

        products.forEach(product -> {
            product.setIsNew(false);
            product.setUpdatedBy(userId);
              });

        productRepository.saveAll(products);
        log.info("批量取消产品新品标识成功，处理数量：{}", products.size());
    }
}