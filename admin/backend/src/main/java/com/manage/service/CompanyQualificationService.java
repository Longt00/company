package com.manage.service;

import com.manage.dto.company.CompanyQualificationRequest;
import com.manage.dto.company.CompanyQualificationResponse;
import com.manage.entity.CompanyQualification;

import java.util.List;

/**
 * 公司资质文件服务接口
 *
 * @author System
 * @version 1.0
 */
public interface CompanyQualificationService {

    /**
     * 获取所有有效的资质文件（公开接口）
     *
     * @return 资质文件列表
     */
    List<CompanyQualificationResponse> getActiveQualifications();

    /**
     * 根据类型获取资质文件（公开接口）
     *
     * @param certificateType 证书类型
     * @return 资质文件列表
     */
    List<CompanyQualificationResponse> getQualificationsByType(String certificateType);

    /**
     * 获取所有资质文件（管理接口）
     *
     * @return 资质文件列表
     */
    List<CompanyQualificationResponse> getAllQualifications();

    /**
     * 根据ID获取资质文件
     *
     * @param id 资质文件ID
     * @return 资质文件
     */
    CompanyQualificationResponse getQualificationById(Long id);

    /**
     * 创建资质文件
     *
     * @param request 创建请求
     * @param userId  操作用户ID
     * @return 创建的资质文件
     */
    CompanyQualificationResponse createQualification(CompanyQualificationRequest request, Long userId);

    /**
     * 更新资质文件
     *
     * @param id      资质文件ID
     * @param request 更新请求
     * @param userId  操作用户ID
     * @return 更新后的资质文件
     */
    CompanyQualificationResponse updateQualification(Long id, CompanyQualificationRequest request, Long userId);

    /**
     * 删除资质文件
     *
     * @param id      资质文件ID
     * @param userId  操作用户ID
     */
    void deleteQualification(Long id, Long userId);

    /**
     * 批量更新排序
     *
     * @param sortRequests 排序请求列表
     * @param userId       操作用户ID
     */
    void updateSortOrder(List<QualificationSortRequest> sortRequests, Long userId);

    /**
     * 检查并更新过期状态
     */
    void checkAndUpdateExpiredStatus();

    /**
     * 排序请求DTO
     */
    class QualificationSortRequest {
        private Long id;
        private Integer sortOrder;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Integer getSortOrder() {
            return sortOrder;
        }

        public void setSortOrder(Integer sortOrder) {
            this.sortOrder = sortOrder;
        }
    }
}