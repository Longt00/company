package com.manage.dto.company;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.validation.constraints.Size;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;

/**
 * 公司信息部分更新请求DTO
 * 用于只更新公司信息的部分字段
 *
 * @author System
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyInfoPartialUpdateRequest {

    /**
     * 公司简称
     */
    @Size(max = 100, message = "公司简称长度不能超过100个字符")
    private String companyShortName;

    /**
     * 公司英文名称
     */
    @Size(max = 200, message = "公司英文名称长度不能超过200个字符")
    private String companyEnglishName;

    /**
     * 公司描述
     */
    @Size(max = 2000, message = "公司描述长度不能超过2000个字符")
    private String companyDescription;

    /**
     * 公司地址
     */
    @Size(max = 500, message = "公司地址长度不能超过500个字符")
    private String address;

    /**
     * 联系电话
     */
    @Pattern(regexp = "^[+]?[0-9\\-\\s()]{7,20}$", message = "联系电话格式不正确")
    private String contactPhone;

    /**
     * 联系邮箱
     */
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
             message = "联系邮箱格式不正确")
    private String contactEmail;

    /**
     * 网站地址
     */
    @Size(max = 200, message = "网站地址长度不能超过200个字符")
    private String website;

    /**
     * 员工人数
     */
    @Size(max = 50, message = "员工人数描述长度不能超过50个字符")
    private String employeeCount;

    /**
     * 年营业额
     */
    @Size(max = 100, message = "年营业额描述长度不能超过100个字符")
    private String annualRevenue;

    /**
     * 主要产品
     */
    @Size(max = 1000, message = "主要产品描述长度不能超过1000个字符")
    private String mainProducts;

    /**
     * 公司简介
     */
    @Size(max = 5000, message = "公司简介长度不能超过5000个字符")
    private String companyProfile;

    /**
     * 业务范围
     */
    @Size(max = 2000, message = "业务范围长度不能超过2000个字符")
    private String businessScope;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 判断是否所有字段都为空
     * @return 如果所有字段都为空返回true
     */
    public boolean isAllFieldsNull() {
        return companyShortName == null &&
               companyEnglishName == null &&
               companyDescription == null &&
               address == null &&
               contactPhone == null &&
               contactEmail == null &&
               website == null &&
               employeeCount == null &&
               annualRevenue == null &&
               mainProducts == null &&
               companyProfile == null &&
               businessScope == null &&
               status == null;
    }
}