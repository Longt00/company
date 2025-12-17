package com.manage.repository;

import com.manage.entity.CompanyContact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 公司联系方式数据访问层接口
 *
 * @author System
 * @version 1.0
 */
@Repository
public interface CompanyContactRepository extends JpaRepository<CompanyContact, Long> {

    /**
     * 查找启用的联系方式
     *
     * @return 联系方式
     */
    @Query("SELECT cc FROM CompanyContact cc WHERE cc.status = 1")
    Optional<CompanyContact> findActiveContact();

    /**
     * 根据状态查找联系方式
     *
     * @param status 状态
     * @return 联系方式
     */
    @Query("SELECT cc FROM CompanyContact cc WHERE cc.status = :status")
    Optional<CompanyContact> findByStatus(@Param("status") Integer status);

    /**
     * 查找第一条记录（按ID降序）
     *
     * @return 联系方式
     */
    Optional<CompanyContact> findFirstByOrderByIdDesc();
}