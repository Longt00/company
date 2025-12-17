package com.manage.service;

import com.manage.dto.company.CompanyContactRequest;
import com.manage.dto.company.CompanyContactResponse;
import com.manage.entity.CompanyContact;

/**
 * 公司联系方式服务接口
 *
 * @author System
 * @version 1.0
 */
public interface CompanyContactService {

    /**
     * 获取联系方式（公开接口）
     *
     * @return 联系方式
     */
    CompanyContactResponse getContact();

    /**
     * 获取联系方式（管理接口）
     *
     * @return 联系方式
     */
    CompanyContactResponse getContactForAdmin();

    /**
     * 更新联系方式
     *
     * @param request 更新请求
     * @param userId  操作用户ID
     * @return 更新后的联系方式
     */
    CompanyContactResponse updateContact(CompanyContactRequest request, Long userId);

    /**
     * 根据ID获取联系方式
     *
     * @param id 联系方式ID
     * @return 联系方式
     */
    CompanyContactResponse getContactById(Long id);

    /**
     * 检查联系方式是否存在
     *
     * @return 是否存在
     */
    boolean existsContact();
}