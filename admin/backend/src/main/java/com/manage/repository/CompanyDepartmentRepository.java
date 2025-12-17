package com.manage.repository;

import com.manage.entity.CompanyDepartment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 公司部门架构数据访问层接口
 *
 * @author System
 * @version 1.0
 */
@Repository
public interface CompanyDepartmentRepository extends JpaRepository<CompanyDepartment, Long> {

    /**
     * 查找所有启用的部门
     *
     * @return 部门列表
     */
    @Query("SELECT cd FROM CompanyDepartment cd WHERE cd.status = 1 ORDER BY cd.sortOrder ASC")
    List<CompanyDepartment> findActiveDepartments();

    /**
     * 根据父部门ID查找子部门
     *
     * @param parentId 父部门ID
     * @return 子部门列表
     */
    @Query("SELECT cd FROM CompanyDepartment cd WHERE cd.parentId = :parentId AND cd.status = 1 ORDER BY cd.sortOrder ASC")
    List<CompanyDepartment> findChildDepartments(@Param("parentId") Long parentId);

    /**
     * 查找顶级部门
     *
     * @return 顶级部门列表
     */
    @Query("SELECT cd FROM CompanyDepartment cd WHERE cd.parentId = 0 AND cd.status = 1 ORDER BY cd.sortOrder ASC")
    List<CompanyDepartment> findTopLevelDepartments();

    /**
     * 根据层级查找部门
     *
     * @param level 部门层级
     * @return 部门列表
     */
    @Query("SELECT cd FROM CompanyDepartment cd WHERE cd.departmentLevel = :level AND cd.status = 1 ORDER BY cd.sortOrder ASC")
    List<CompanyDepartment> findByLevelAndStatus(@Param("level") Integer level);

    /**
     * 根据状态查找部门
     *
     * @param status 状态
     * @return 部门列表
     */
    @Query("SELECT cd FROM CompanyDepartment cd WHERE cd.status = :status ORDER BY cd.sortOrder ASC")
    List<CompanyDepartment> findByStatus(@Param("status") Integer status);

    /**
     * 根据部门名称查找部门
     *
     * @param departmentNameCn 部门名称（中文）
     * @return 部门信息
     */
    @Query("SELECT cd FROM CompanyDepartment cd WHERE cd.departmentNameCn = :departmentNameCn AND cd.status = 1")
    Optional<CompanyDepartment> findByNameAndStatus(@Param("departmentNameCn") String departmentNameCn);

    /**
     * 根据ID查找部门
     *
     * @param id 部门ID
     * @return 部门信息
     */
    @Query("SELECT cd FROM CompanyDepartment cd WHERE cd.id = :id AND cd.status = 1")
    Optional<CompanyDepartment> findActiveById(@Param("id") Long id);

    /**
     * 检查部门名称是否存在
     *
     * @param departmentNameCn 部门名称（中文）
     * @return 存在返回true，不存在返回false
     */
    boolean existsByDepartmentNameCnAndStatus(String departmentNameCn, Integer status);

    /**
     * 统计指定父部门的子部门数量
     *
     * @param parentId 父部门ID
     * @return 子部门数量
     */
    @Query("SELECT COUNT(cd) FROM CompanyDepartment cd WHERE cd.parentId = :parentId AND cd.status = 1")
    Long countChildDepartments(@Param("parentId") Long parentId);
}