package com.manage.repository;

import com.manage.entity.CompanyQualification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 公司资质文件数据访问层接口
 *
 * @author System
 * @version 1.0
 */
@Repository
public interface CompanyQualificationRepository extends JpaRepository<CompanyQualification, Long> {

    /**
     * 根据类型查找有效的资质文件
     *
     * @param certificateType 证书类型
     * @return 资质文件列表
     */
    @Query("SELECT cq FROM CompanyQualification cq WHERE cq.certificateType = :certificateType AND cq.status = 1 ORDER BY cq.sortOrder ASC")
    List<CompanyQualification> findByTypeAndStatus(@Param("certificateType") String certificateType);

    /**
     * 查找所有有效的资质文件
     *
     * @return 资质文件列表
     */
    @Query("SELECT cq FROM CompanyQualification cq WHERE cq.status = 1 ORDER BY cq.sortOrder ASC")
    List<CompanyQualification> findActiveQualifications();

    /**
     * 根据状态查找资质文件
     *
     * @param status 状态
     * @return 资质文件列表
     */
    @Query("SELECT cq FROM CompanyQualification cq WHERE cq.status = :status ORDER BY cq.sortOrder ASC")
    List<CompanyQualification> findByStatus(@Param("status") Integer status);

    /**
     * 查找过期的资质文件
     *
     * @return 过期的资质文件列表
     */
    @Query("SELECT cq FROM CompanyQualification cq WHERE cq.expireDate < CURRENT_DATE AND cq.status = 1")
    List<CompanyQualification> findExpiredQualifications();

    /**
     * 根据证书类型和状态查找资质文件
     *
     * @param certificateType 证书类型
     * @param status          状态
     * @return 资质文件列表
     */
    @Query("SELECT cq FROM CompanyQualification cq WHERE cq.certificateType = :certificateType AND cq.status = :status ORDER BY cq.sortOrder ASC")
    List<CompanyQualification> findByTypeAndStatusOrderBySortOrder(@Param("certificateType") String certificateType, @Param("status") Integer status);

    /**
     * 查找核心成员资质
     *
     * @return 资质文件列表
     */
    @Query("SELECT cq FROM CompanyQualification cq WHERE cq.status = 1 ORDER BY cq.sortOrder ASC")
    List<CompanyQualification> findCoreQualifications();
}