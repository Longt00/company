package com.manage.service.impl;

import com.manage.dto.company.CompanyContactRequest;
import com.manage.dto.company.CompanyContactResponse;
import com.manage.entity.CompanyContact;
import com.manage.exception.BusinessException;
import com.manage.repository.CompanyContactRepository;
import com.manage.service.CompanyContactService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 公司联系方式服务实现类
 *
 * @author System
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CompanyContactServiceImpl implements CompanyContactService {

    private final CompanyContactRepository companyContactRepository;

    /**
     * 获取联系方式（公开接口）
     *
     * @return 联系方式
     */
    @Override
    public CompanyContactResponse getContact() {
        CompanyContact contact = companyContactRepository.findActiveContact()
                .orElseThrow(() -> new BusinessException(404, "联系方式不存在"));
        return convertToResponse(contact);
    }

    /**
     * 获取联系方式（管理接口）
     *
     * @return 联系方式
     */
    @Override
    public CompanyContactResponse getContactForAdmin() {
        CompanyContact contact = companyContactRepository.findFirstByOrderByIdDesc()
                .orElseThrow(() -> new BusinessException(404, "联系方式不存在"));
        return convertToResponse(contact);
    }

    /**
     * 更新联系方式
     *
     * @param request 更新请求
     * @param userId  操作用户ID
     * @return 更新后的联系方式
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CompanyContactResponse updateContact(CompanyContactRequest request, Long userId) {
        log.info("更新联系方式，操作用户ID：{}", userId);

        CompanyContact contact = companyContactRepository.findFirstByOrderByIdDesc()
                .orElseThrow(() -> new BusinessException(404, "联系方式不存在"));

        // 更新信息
        BeanUtils.copyProperties(request, contact);
        contact.setUpdatedBy(userId);
        contact.setUpdateTime(LocalDateTime.now());

        // 保存更新
        CompanyContact savedContact = companyContactRepository.save(contact);
        log.info("联系方式更新成功");

        return convertToResponse(savedContact);
    }

    /**
     * 根据ID获取联系方式
     *
     * @param id 联系方式ID
     * @return 联系方式
     */
    @Override
    public CompanyContactResponse getContactById(Long id) {
        CompanyContact contact = companyContactRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "联系方式不存在"));
        return convertToResponse(contact);
    }

    /**
     * 检查联系方式是否存在
     *
     * @return 是否存在
     */
    @Override
    public boolean existsContact() {
        return companyContactRepository.findFirstByOrderByIdDesc().isPresent();
    }

    /**
     * 实体对象转换为响应DTO
     *
     * @param contact 实体对象
     * @return 响应DTO
     */
    private CompanyContactResponse convertToResponse(CompanyContact contact) {
        CompanyContactResponse response = new CompanyContactResponse();
        BeanUtils.copyProperties(contact, response);
        return response;
    }
}