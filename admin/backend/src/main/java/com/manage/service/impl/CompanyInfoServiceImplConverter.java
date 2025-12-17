package com.manage.service.impl;

import com.manage.dto.company.CompanyInfoResponse;
import com.manage.entity.CompanyInfo;
import com.manage.util.CompanyInfoConverter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 公司信息转换工具类（Service层专用）
 * 解决Stream.map()中的lambda表达式方法引用问题
 */
@Service
@Slf4j
public class CompanyInfoServiceImplConverter {

    /**
     * 将CompanyInfo实体转换为CompanyInfoResponse响应对象（用于Service层）
     *
     * @param companyPage 分页的CompanyInfo对象
     * @return 转换后的响应对象列表
     */
    public static Page<CompanyInfoResponse> convertToPageResponse(Page<CompanyInfo> companyPage) {
        log.info("转换公司信息分页响应，记录数：{}", companyPage.getTotalElements());

        return companyPage.map(company -> {
            CompanyInfoResponse response = new CompanyInfoResponse();
            BeanUtils.copyProperties(company, response);
            return response;
        });
    }

    /**
     * 将CompanyInfo实体列表转换为响应对象列表（用于Stream操作）
     *
     * @param companyList CompanyInfo实体列表
     * @return 响应对象列表
     */
    public static List<CompanyInfoResponse> convertToListResponse(List<CompanyInfo> companyList) {
        log.info("转换公司信息列表，记录数：{}", companyList.size());

        return companyList.stream()
                .map(CompanyInfoConverter::convertToResponse)
                .collect(Collectors.toList());
    }
}