package com.manage.service.impl;

import com.manage.common.ResultCode;
import com.manage.dto.company.CompanyInfoRequest;
import com.manage.dto.company.CompanyInfoResponse;
import com.manage.dto.company.CompanyInfoPartialUpdateRequest;
import com.manage.entity.CompanyInfo;
import com.manage.exception.BusinessException;
import com.manage.repository.CompanyInfoRepository;
import com.manage.service.CompanyInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompanyInfoServiceImpl implements CompanyInfoService {

    private final CompanyInfoRepository companyInfoRepository;

    @Override
    public CompanyInfoResponse getCompanyInfo() {
        CompanyInfo companyInfo = companyInfoRepository.findActiveCompanyInfo()
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "公司信息不存在"));
        return convertToResponse(companyInfo);
    }

    @Override
    public CompanyInfoResponse getCompanyInfoForAdmin() {
        CompanyInfo companyInfo = companyInfoRepository.findFirstByOrderByIdDesc()
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "公司信息不存在"));
        return convertToResponse(companyInfo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CompanyInfoResponse updateCompanyInfo(CompanyInfoRequest request, Long userId) {
        log.info("更新公司基本信息，操作用户ID：{}", userId);

        CompanyInfo companyInfo = companyInfoRepository.findFirstByOrderByIdDesc()
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "公司信息不存在"));

        // 数据校验 (保留自定义校验逻辑)
        validateCompanyInfoRequest(request);

        // 手动复制属性，避免null值覆盖非null字段
        updateCompanyInfoFromRequest(companyInfo, request);

        // 手动设置审计字段
        companyInfo.setUpdatedBy(userId);
        companyInfo.setUpdateTime(LocalDateTime.now());

        // 保存更新
        CompanyInfo savedInfo = companyInfoRepository.save(companyInfo);
        log.info("公司基本信息更新成功");

        return convertToResponse(savedInfo);
    }

    @Override
    public boolean existsCompanyInfo() {
        return companyInfoRepository.existsBy();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CompanyInfoResponse initCompanyInfo(CompanyInfoRequest request, Long userId) {
        log.info("初始化公司基本信息，操作用户ID：{}", userId);

        if (existsCompanyInfo()) {
            throw new BusinessException(ResultCode.ERROR, "公司信息已存在，不能重复创建");
        }

        validateCompanyInfoRequest(request);

        CompanyInfo companyInfo = new CompanyInfo();

        // 使用 BeanUtils 复制属性 (替换手动复制)
        BeanUtils.copyProperties(request, companyInfo);

        // 手动设置审计字段
        companyInfo.setCreatedBy(userId);
        // createTime 由 @CreationTimestamp 自动设置，不需要手动设置
        // status 字段会通过 BeanUtils 从 request 复制过来 (request DTO 中有默认值1)

        CompanyInfo savedInfo = companyInfoRepository.save(companyInfo);
        log.info("公司基本信息初始化成功");

        return convertToResponse(savedInfo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CompanyInfoResponse partialUpdateCompanyInfo(CompanyInfoPartialUpdateRequest request, Long userId) {
        log.info("部分更新公司信息，操作用户ID：{}", userId);

        CompanyInfo companyInfo = companyInfoRepository.findFirstByOrderByIdDesc()
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "公司信息不存在"));

        // 手动复制非空属性，避免null值覆盖非null字段
        updateCompanyInfoFromPartialRequest(companyInfo, request);

        // 手动设置审计字段
        companyInfo.setUpdatedBy(userId);
        companyInfo.setUpdateTime(LocalDateTime.now());

        // 保存更新
        CompanyInfo savedInfo = companyInfoRepository.save(companyInfo);
        log.info("公司信息部分更新成功");

        return convertToResponse(savedInfo);
    }

    private void validateCompanyInfoRequest(CompanyInfoRequest request) {
        // 成立日期校验
        if (request.getEstablishDate() != null && !request.getEstablishDate().trim().isEmpty()) {
            try {
                // Pattern annotation on DTO should handle format, but year check is custom
                String[] dateParts = request.getEstablishDate().split("-");
                int year = Integer.parseInt(dateParts[0]);
                if (year > LocalDateTime.now().getYear()) {
                    throw new BusinessException(ResultCode.ERROR, "成立年份不能晚于当前年份");
                }
            } catch (Exception e) {
                 // Let pattern validation handle format errors, catch parsing errors
                log.warn("成立日期解析或年份校验失败: {}", e.getMessage()); // Log warning
                 // Re-throw or rely on @Pattern for format validation.
                 // If year check is critical, rethrow specific exception:
                 if (!(e instanceof BusinessException)) { // Avoid wrapping BusinessException
                    throw new BusinessException(ResultCode.ERROR, "成立日期格式不正确或年份无效");
                 } else {
                     throw e; // Re-throw the original BusinessException
                 }
            }
        }

        // 注册资本校验（如果包含数字）
        if (request.getRegisteredCapital() != null && !request.getRegisteredCapital().trim().isEmpty()) {
            String capital = request.getRegisteredCapital().replaceAll("[^0-9.]", "");
            if (!capital.isEmpty()) {
                try {
                    double value = Double.parseDouble(capital);
                    if (value < 0) {
                        throw new BusinessException(ResultCode.ERROR, "注册资本不能为负数");
                    }
                } catch (NumberFormatException e) {
                    log.warn("注册资本 '{}' 无法解析为数字，跳过负数校验", request.getRegisteredCapital());
                    // 如果转换失败，跳过数字校验 (Allow text like "1000万人民币")
                }
            }
        }
    }

    /**
     * 从完整的更新请求中复制属性到实体类
     * 避免null值覆盖数据库中的非null字段
     */
    private void updateCompanyInfoFromRequest(CompanyInfo companyInfo, CompanyInfoRequest request) {
        if (request.getCompanyName() != null) {
            companyInfo.setCompanyName(request.getCompanyName());
        }
        if (request.getCompanyShortName() != null) {
            companyInfo.setCompanyShortName(request.getCompanyShortName());
        }
        if (request.getCompanyDescription() != null) {
            companyInfo.setCompanyDescription(request.getCompanyDescription());
        }
        if (request.getCompanyLogo() != null) {
            companyInfo.setCompanyLogo(request.getCompanyLogo());
        }
        if (request.getCompanyVideo() != null) {
            companyInfo.setCompanyVideo(request.getCompanyVideo());
        }
        if (request.getEstablishDate() != null) {
            companyInfo.setEstablishDate(request.getEstablishDate());
        }
        if (request.getRegisteredCapital() != null) {
            companyInfo.setRegisteredCapital(request.getRegisteredCapital());
        }
        if (request.getLegalRepresentative() != null) {
            companyInfo.setLegalRepresentative(request.getLegalRepresentative());
        }
        if (request.getBusinessScope() != null) {
            companyInfo.setBusinessScope(request.getBusinessScope());
        }
        if (request.getCompanyAddress() != null) {
            companyInfo.setCompanyAddress(request.getCompanyAddress());
        }
        if (request.getCompanyWebsite() != null) {
            companyInfo.setCompanyWebsite(request.getCompanyWebsite());
        }
        if (request.getCompanyPhone() != null) {
            companyInfo.setCompanyPhone(request.getCompanyPhone());
        }
        if (request.getCompanyEmail() != null) {
            companyInfo.setCompanyEmail(request.getCompanyEmail());
        }
        if (request.getStatus() != null) {
            companyInfo.setStatus(request.getStatus());
        }
    }

    /**
     * 从部分更新请求中复制属性到实体类
     * 只更新非null的字段
     */
    private void updateCompanyInfoFromPartialRequest(CompanyInfo companyInfo, CompanyInfoPartialUpdateRequest request) {
        if (request.getCompanyShortName() != null) {
            companyInfo.setCompanyShortName(request.getCompanyShortName());
        }
        if (request.getCompanyDescription() != null) {
            companyInfo.setCompanyDescription(request.getCompanyDescription());
        }
        if (request.getAddress() != null) {
            companyInfo.setCompanyAddress(request.getAddress());
        }
        if (request.getContactPhone() != null) {
            companyInfo.setCompanyPhone(request.getContactPhone());
        }
        if (request.getContactEmail() != null) {
            companyInfo.setCompanyEmail(request.getContactEmail());
        }
        if (request.getWebsite() != null) {
            companyInfo.setCompanyWebsite(request.getWebsite());
        }
        if (request.getBusinessScope() != null) {
            companyInfo.setBusinessScope(request.getBusinessScope());
        }
        if (request.getStatus() != null) {
            companyInfo.setStatus(request.getStatus());
        }
    }

  @Override
    @Transactional(rollbackFor = Exception.class)
    public CompanyInfoResponse updateCompanyLogo(String logoUrl, Long userId) {
        log.info("更新公司Logo，操作用户ID：{}，Logo URL：{}", userId, logoUrl);

        if (logoUrl == null || logoUrl.trim().isEmpty()) {
            throw new BusinessException(ResultCode.ERROR, "Logo URL不能为空");
        }

        CompanyInfo companyInfo = companyInfoRepository.findFirstByOrderByIdDesc()
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "公司信息不存在"));

        // 更新Logo URL
        companyInfo.setCompanyLogo(logoUrl.trim());
        companyInfo.setUpdatedBy(userId);

        // 保存更新
        CompanyInfo savedCompanyInfo = companyInfoRepository.save(companyInfo);
        log.info("公司Logo更新成功，新的Logo URL：{}", logoUrl);

        return convertToResponse(savedCompanyInfo);
    }

    private CompanyInfoResponse convertToResponse(CompanyInfo companyInfo) {
        CompanyInfoResponse response = new CompanyInfoResponse();
        BeanUtils.copyProperties(companyInfo, response);
        return response;
    }
}