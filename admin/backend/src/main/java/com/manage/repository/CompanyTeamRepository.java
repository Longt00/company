package com.manage.repository;

import com.manage.entity.CompanyTeam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 公司核心团队数据访问层接口
 *
 * @author System
 * @version 1.0
 */
@Repository
public interface CompanyTeamRepository extends JpaRepository<CompanyTeam, Long> {

    /**
     * 查找所有启用的团队成员
     *
     * @return 团队成员列表
     */
    @Query("SELECT ct FROM CompanyTeam ct WHERE ct.status = 1 ORDER BY ct.sortOrder ASC")
    List<CompanyTeam> findActiveTeamMembers();

    /**
     * 查找启用的核心成员
     *
     * @return 核心成员列表
     */
    @Query("SELECT ct FROM CompanyTeam ct WHERE ct.status = 1 AND ct.isCore = true ORDER BY ct.sortOrder ASC")
    List<CompanyTeam> findActiveCoreMembers();

    /**
     * 根据部门查找团队成员
     *
     * @param department 部门名称
     * @return 团队成员列表
     */
    @Query("SELECT ct FROM CompanyTeam ct WHERE ct.department = :department AND ct.status = 1 ORDER BY ct.sortOrder ASC")
    List<CompanyTeam> findByDepartmentAndStatus(@Param("department") String department);

    /**
     * 根据状态查找团队成员
     *
     * @param status 状态
     * @return 团队成员列表
     */
    @Query("SELECT ct FROM CompanyTeam ct WHERE ct.status = :status ORDER BY ct.sortOrder ASC")
    List<CompanyTeam> findByStatus(@Param("status") Integer status);

    /**
     * 根据ID查找团队成员
     *
     * @param id 成员ID
     * @return 团队成员
     */
    @Query("SELECT ct FROM CompanyTeam ct WHERE ct.id = :id AND ct.status = 1")
    Optional<CompanyTeam> findActiveById(@Param("id") Long id);

    /**
     * 查找指定部门的核心成员
     *
     * @param department 部门名称
     * @return 核心成员列表
     */
    @Query("SELECT ct FROM CompanyTeam ct WHERE ct.department = :department AND ct.status = 1 AND ct.isCore = true ORDER BY ct.sortOrder ASC")
    List<CompanyTeam> findCoreMembersByDepartment(@Param("department") String department);

    /**
     * 统计部门成员数量
     *
     * @param department 部门名称
     * @return 成员数量
     */
    @Query("SELECT COUNT(ct) FROM CompanyTeam ct WHERE ct.department = :department AND ct.status = 1")
    Long countByDepartmentAndStatus(@Param("department") String department);
}