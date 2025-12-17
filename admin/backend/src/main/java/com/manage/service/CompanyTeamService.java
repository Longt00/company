package com.manage.service;

import com.manage.dto.company.CompanyTeamRequest;
import com.manage.dto.company.CompanyTeamResponse;
import com.manage.entity.CompanyTeam;

import java.util.List;

/**
 * 公司核心团队服务接口
 *
 * @author System
 * @version 1.0
 */
public interface CompanyTeamService {

    /**
     * 获取所有启用的团队成员（公开接口）
     *
     * @return 团队成员列表
     */
    List<CompanyTeamResponse> getActiveTeamMembers();

    /**
     * 获取核心成员（公开接口）
     *
     * @return 核心成员列表
     */
    List<CompanyTeamResponse> getCoreMembers();

    /**
     * 根据部门获取团队成员（公开接口）
     *
     * @param department 部门名称
     * @return 团队成员列表
     */
    List<CompanyTeamResponse> getTeamMembersByDepartment(String department);

    /**
     * 获取所有团队成员（管理接口）
     *
     * @return 团队成员列表
     */
    List<CompanyTeamResponse> getAllTeamMembers();

    /**
     * 根据ID获取团队成员
     *
     * @param id 成员ID
     * @return 团队成员
     */
    CompanyTeamResponse getTeamMemberById(Long id);

    /**
     * 创建团队成员
     *
     * @param request 创建请求
     * @param userId  操作用户ID
     * @return 创建的团队成员
     */
    CompanyTeamResponse createTeamMember(CompanyTeamRequest request, Long userId);

    /**
     * 更新团队成员
     *
     * @param id      成员ID
     * @param request 更新请求
     * @param userId  操作用户ID
     * @return 更新后的团队成员
     */
    CompanyTeamResponse updateTeamMember(Long id, CompanyTeamRequest request, Long userId);

    /**
     * 删除团队成员
     *
     * @param id      成员ID
     * @param userId  操作用户ID
     */
    void deleteTeamMember(Long id, Long userId);

    /**
     * 批量更新排序
     *
     * @param sortRequests 排序请求列表
     * @param userId       操作用户ID
     */
    void updateSortOrder(List<TeamSortRequest> sortRequests, Long userId);

    /**
     * 排序请求DTO
     */
    class TeamSortRequest {
        private Long id;
        private Integer sortOrder;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Integer getSortOrder() {
            return sortOrder;
        }

        public void setSortOrder(Integer sortOrder) {
            this.sortOrder = sortOrder;
        }
    }
}