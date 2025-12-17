package com.manage.service;

import com.manage.dto.company.CompanyInfoRequest;
import com.manage.dto.company.CompanyInfoResponse;
import com.manage.dto.company.CompanyInfoPartialUpdateRequest;

/**
 * 公司信息服务接口（单一模式）
 *
 * @author System
 * @version 1.0
 */
public interface CompanyInfoService {

    /**
     * 获取公司基本信息（公开接口）
     *
     * @return 公司基本信息
     */
    CompanyInfoResponse getCompanyInfo();

    /**
     * 获取公司基本信息（管理接口）
     *
     * @return 公司基本信息
     */
    CompanyInfoResponse getCompanyInfoForAdmin();

    /**
     * 更新公司基本信息
     *
     * @param request 更新请求
     * @param userId  操作用户ID
     * @return 更新后的公司信息
     */
    CompanyInfoResponse updateCompanyInfo(CompanyInfoRequest request, Long userId);

    /**
     * 检查公司信息是否存在
     *
     * @return 存在返回true，不存在返回false
     */
    boolean existsCompanyInfo();

    /**
     * 初始化公司信息
     *
     * @param request 初始化请求
     * @param userId  操作用户ID
     * @return 创建的公司信息
     */
    CompanyInfoResponse initCompanyInfo(CompanyInfoRequest request, Long userId);

    /**
     * 部分更新公司信息
     * 只更新提供的字段，其他字段保持不变
     *
     * @param request 部分更新请求
     * @param userId  操作用户ID
     * @return 更新后的公司信息
     */
    CompanyInfoResponse partialUpdateCompanyInfo(CompanyInfoPartialUpdateRequest request, Long userId);

    /**
     * 更新公司Logo
     *
     * @param logoUrl Logo文件URL
     * @param userId  操作用户ID
     * @return 更新后的公司信息
     */
    CompanyInfoResponse updateCompanyLogo(String logoUrl, Long userId);
}