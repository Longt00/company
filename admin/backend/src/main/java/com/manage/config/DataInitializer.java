package com.manage.config;

import com.manage.entity.CompanyContact;
import com.manage.repository.CompanyContactRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 数据初始化器
 * 在应用启动时检查并插入基础的CompanyContact记录
 *
 * @author System
 * @version 1.0
 */
@Slf4j
@Component
@Order(1)
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final CompanyContactRepository companyContactRepository;

    @Override
    public void run(String... args) {
        try {
            initializeCompanyContact();
        } catch (Exception e) {
            log.error("数据初始化失败", e);
        }
    }

    /**
     * 初始化公司联系方式数据
     */
    private void initializeCompanyContact() {
        try {
            // 检查是否已有联系方式记录
            if (companyContactRepository.findFirstByOrderByIdDesc().isPresent()) {
                log.info("CompanyContact记录已存在，跳过初始化");
                return;
            }

            log.info("开始初始化CompanyContact基础数据...");

            // 创建基础联系方式记录
            CompanyContact contact = new CompanyContact();
            contact.setAddressCn("广东省深圳市南山区科技园南区深圳湾科技生态园");
            contact.setAddressEn("Nanshan Science and Technology Park, Southern District, Shenzhen Bay Technology & Ecology Park, Shenzhen");
            contact.setPhone("+86-0755-2660-8888");
            contact.setFax("+86-0755-2660-8889");
            contact.setEmail("contact@company.com");
            contact.setWebsiteUrl("https://www.company.com");
            contact.setContactPersonCn("王经理");
            contact.setContactPersonEn("Manager Wang");
            contact.setWechat("company_wechat_official");
            contact.setWhatsapp("+8613800138000");
            contact.setQq("123456789");
            contact.setStatus(1);
            contact.setCreatedBy(1L);
            contact.setUpdatedBy(1L);
            contact.setCreateTime(LocalDateTime.now());
            contact.setUpdateTime(LocalDateTime.now());

            // 保存记录
            CompanyContact savedContact = companyContactRepository.save(contact);

            log.info("CompanyContact基础数据初始化成功，ID: {}, WhatsApp: {}",
                    savedContact.getId(), savedContact.getWhatsapp());

            // 验证WhatsApp链接生成
            String cleanNumber = savedContact.getWhatsapp().replaceAll("[^0-9]", "");
            String whatsappUrl = "https://wa.me/" + cleanNumber;
            log.info("生成的WhatsApp聊天链接: {}", whatsappUrl);

        } catch (Exception e) {
            log.error("初始化CompanyContact数据时发生错误", e);
            throw new RuntimeException("数据初始化失败: " + e.getMessage(), e);
        }
    }
}