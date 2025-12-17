package com.manage.util;

import com.manage.dto.company.CompanyInfoResponse;
import com.manage.entity.CompanyInfo;
import org.springframework.beans.BeanUtils;

/**
 * 公司信息转换工具类
 * 用于在Stream操作中进行CompanyInfo到CompanyInfoResponse的转换
 * 解决lambda表达式中无法引用非静态方法的问题
 *
 * @author System
 * @version 1.0
 */
public class CompanyInfoConverter {

    /**
     * 将CompanyInfo实体转换为CompanyInfoResponse响应对象
     *
     * @param companyInfo 公司信息实体
     * @return 公司信息响应对象
     */
    public static CompanyInfoResponse convertToResponse(CompanyInfo companyInfo) {
        CompanyInfoResponse response = new CompanyInfoResponse();
        BeanUtils.copyProperties(companyInfo, response);
        return response;
    }
}