package com.manage.service.impl;

import com.manage.common.ResultCode;
import com.manage.dto.company.CompanyQualificationRequest;
import com.manage.dto.company.CompanyQualificationResponse;
import com.manage.entity.CompanyQualification;
import com.manage.exception.BusinessException;
import com.manage.repository.CompanyQualificationRepository;
import com.manage.service.CompanyQualificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 公司资质文件服务实现类
 *
 * @author System
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CompanyQualificationServiceImpl implements CompanyQualificationService {

    private final CompanyQualificationRepository qualificationRepository;

    /**
     * 获取所有有效的资质文件（公开接口）
     *
     * @return 资质文件列表
     */
    @Override
    public List<CompanyQualificationResponse> getActiveQualifications() {
        List<CompanyQualification> qualifications = qualificationRepository.findActiveQualifications();
        return qualifications.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * 根据类型获取资质文件（公开接口）
     *
     * @param certificateType 证书类型
     * @return 资质文件列表
     */
    @Override
    public List<CompanyQualificationResponse> getQualificationsByType(String certificateType) {
        List<CompanyQualification> qualifications = qualificationRepository.findByTypeAndStatus(certificateType);
        return qualifications.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * 获取所有资质文件（管理接口）
     *
     * @return 资质文件列表
     */
    @Override
    public List<CompanyQualificationResponse> getAllQualifications() {
        List<CompanyQualification> qualifications = qualificationRepository.findAll();
        return qualifications.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * 根据ID获取资质文件
     *
     * @param id 资质文件ID
     * @return 资质文件
     */
    @Override
    public CompanyQualificationResponse getQualificationById(Long id) {
        CompanyQualification qualification = qualificationRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "资质文件不存在"));
        return convertToResponse(qualification);
    }

    /**
     * 创建资质文件
     *
     * @param request 创建请求
     * @param userId  操作用户ID
     * @return 创建的资质文件
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CompanyQualificationResponse createQualification(CompanyQualificationRequest request, Long userId) {
        log.info("创建资质文件，操作用户ID：{}", userId);

        // 数据校验
        validateQualificationRequest(request);

        // 创建资质文件
        CompanyQualification qualification = new CompanyQualification();
        BeanUtils.copyProperties(request, qualification);
        qualification.setCreatedBy(userId);
        qualification.setCreateTime(LocalDateTime.now());

        // 保存
        CompanyQualification savedQualification = qualificationRepository.save(qualification);
        log.info("资质文件创建成功");

        return convertToResponse(savedQualification);
    }

    /**
     * 更新资质文件
     *
     * @param id      资质文件ID
     * @param request 更新请求
     * @param userId  操作用户ID
     * @return 更新后的资质文件
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CompanyQualificationResponse updateQualification(Long id, CompanyQualificationRequest request, Long userId) {
        log.info("更新资质文件，ID：{}，操作用户ID：{}", id, userId);

        CompanyQualification qualification = qualificationRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "资质文件不存在"));

        // 数据校验
        validateQualificationRequest(request);

        // 更新信息
        BeanUtils.copyProperties(request, qualification);
        qualification.setUpdatedBy(userId);
        qualification.setUpdateTime(LocalDateTime.now());

        // 保存更新
        CompanyQualification savedQualification = qualificationRepository.save(qualification);
        log.info("资质文件更新成功");

        return convertToResponse(savedQualification);
    }

    /**
     * 删除资质文件
     *
     * @param id      资质文件ID
     * @param userId  操作用户ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteQualification(Long id, Long userId) {
        log.info("删除资质文件，ID：{}，操作用户ID：{}", id, userId);

        CompanyQualification qualification = qualificationRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "资质文件不存在"));

        qualificationRepository.delete(qualification);
        log.info("资质文件删除成功");
    }

    /**
     * 批量更新排序
     *
     * @param sortRequests 排序请求列表
     * @param userId       操作用户ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSortOrder(List<QualificationSortRequest> sortRequests, Long userId) {
        log.info("批量更新资质文件排序，操作用户ID：{}", userId);

        for (QualificationSortRequest sortRequest : sortRequests) {
            CompanyQualification qualification = qualificationRepository.findById(sortRequest.getId())
                    .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "资质文件不存在"));

            qualification.setSortOrder(sortRequest.getSortOrder());
            qualification.setUpdatedBy(userId);
            qualification.setUpdateTime(LocalDateTime.now());

            qualificationRepository.save(qualification);
        }

        log.info("资质文件排序更新成功");
    }

    /**
     * 检查并更新过期状态
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void checkAndUpdateExpiredStatus() {
        List<CompanyQualification> expiredQualifications = qualificationRepository.findExpiredQualifications();

        for (CompanyQualification qualification : expiredQualifications) {
            qualification.setStatus(0); // 设置为过期状态
            qualification.setUpdateTime(LocalDateTime.now());
            qualificationRepository.save(qualification);
        }

        if (!expiredQualifications.isEmpty()) {
            log.info("更新了{}个过期资质文件的状态", expiredQualifications.size());
        }
    }

    /**
     * 数据校验
     *
     * @param request 请求数据
     */
    private void validateQualificationRequest(CompanyQualificationRequest request) {
        // 发证日期不能晚于有效期
        if (request.getIssueDate() != null && request.getExpireDate() != null) {
            if (request.getIssueDate().isAfter(request.getExpireDate())) {
                throw new BusinessException("发证日期不能晚于有效期");
            }
        }

        // 有效期不能早于当前时间
        if (request.getExpireDate() != null && request.getExpireDate().isBefore(LocalDate.now())) {
            // 如果已过期，自动设置状态为过期
            request.setStatus(0);
        }
    }

    /**
     * 实体对象转换为响应DTO
     *
     * @param qualification 实体对象
     * @return 响应DTO
     */
    private CompanyQualificationResponse convertToResponse(CompanyQualification qualification) {
        CompanyQualificationResponse response = new CompanyQualificationResponse();
        BeanUtils.copyProperties(qualification, response);

        // 设置证书类型描述
        response.setCertificateTypeDesc(getCertificateTypeDesc(qualification.getCertificateType()));

        // 设置状态描述
        response.setStatusDesc(getStatusDesc(qualification.getStatus()));

        // 检查是否即将过期（30天内）
        if (qualification.getExpireDate() != null) {
            LocalDate thirtyDaysLater = LocalDate.now().plusDays(30);
            response.setIsExpiringSoon(qualification.getExpireDate().isBefore(thirtyDaysLater));
        } else {
            response.setIsExpiringSoon(false);
        }

        return response;
    }

    /**
     * 获取证书类型描述
     *
     * @param type 证书类型
     * @return 类型描述
     */
    private String getCertificateTypeDesc(String type) {
        switch (type) {
            case "business_license":
                return "营业执照";
            case "iso":
                return "ISO认证";
            case "honor":
                return "荣誉证书";
            case "other":
                return "其他";
            default:
                return "未知";
        }
    }

    /**
     * 获取状态描述
     *
     * @param status 状态码
     * @return 状态描述
     */
    private String getStatusDesc(Integer status) {
        switch (status) {
            case 0:
                return "过期";
            case 1:
                return "有效";
            case 2:
                return "禁用";
            default:
                return "未知";
        }
    }
}