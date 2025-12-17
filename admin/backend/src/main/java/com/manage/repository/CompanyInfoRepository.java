package com.manage.repository;

import com.manage.entity.CompanyInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 公司基本信息数据访问层接口
 *
 * @author System
 * @version 1.0
 */
@Repository
public interface CompanyInfoRepository extends JpaRepository<CompanyInfo, Long> {

    /**
     * 查找启用的公司信息
     *
     * @return 公司信息
     */
    @Query("SELECT ci FROM CompanyInfo ci WHERE ci.status = 1")
    Optional<CompanyInfo> findActiveCompanyInfo();

    /**
     * 根据状态查找公司信息
     *
     * @param status 状态
     * @return 公司信息
     */
    @Query("SELECT ci FROM CompanyInfo ci WHERE ci.status = :status")
    Optional<CompanyInfo> findByStatus(@Param("status") Integer status);

    /**
     * 查找第一个公司信息（按ID降序）
     *
     * @return 公司信息
     */
    Optional<CompanyInfo> findFirstByOrderByIdDesc();

    /**
     * 检查公司信息是否存在
     *
     * @return 存在返回true，不存在返回false
     */
    boolean existsById(Long id);

    /**
     * 检查公司信息是否存在（任意记录）
     *
     * @return 存在返回true，不存在返回false
     */
    boolean existsBy();

    /**
     * 获取所有公司信息（分页）
     *
     * @param pageable 分页参数
     * @return 分页结果
     */
    Page<CompanyInfo> findAll(Pageable pageable);

    /**
     * 根据状态获取公司信息列表
     *
     * @param status 状态
     * @return 公司信息列表
     */
    List<CompanyInfo> findAllByStatus(Integer status);

    /**
     * 根据公司名称模糊查询
     *
     * @param companyName 公司名称
     * @param pageable 分页参数
     * @return 分页结果
     */
    @Query("SELECT ci FROM CompanyInfo ci WHERE ci.companyName LIKE %:companyName%")
    Page<CompanyInfo> findByCompanyNameContaining(@Param("companyName") String companyName, Pageable pageable);

    /**
     * 根据状态和公司名称模糊查询
     *
     * @param status 状态
     * @param companyName 公司名称
     * @param pageable 分页参数
     * @return 分页结果
     */
    @Query("SELECT ci FROM CompanyInfo ci WHERE ci.status = :status AND ci.companyName LIKE %:companyName%")
    Page<CompanyInfo> findByStatusAndCompanyNameContaining(@Param("status") Integer status, @Param("companyName") String companyName, Pageable pageable);
}