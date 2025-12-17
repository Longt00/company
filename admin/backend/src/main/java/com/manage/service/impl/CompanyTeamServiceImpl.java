package com.manage.service.impl;

import com.manage.dto.company.CompanyTeamRequest;
import com.manage.dto.company.CompanyTeamResponse;
import com.manage.entity.CompanyTeam;
import com.manage.exception.BusinessException;
import com.manage.repository.CompanyTeamRepository;
import com.manage.service.CompanyTeamService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 公司核心团队服务实现类
 *
 * @author System
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CompanyTeamServiceImpl implements CompanyTeamService {

    private final CompanyTeamRepository teamRepository;

    /**
     * 获取所有启用的团队成员（公开接口）
     *
     * @return 团队成员列表
     */
    @Override
    public List<CompanyTeamResponse> getActiveTeamMembers() {
        List<CompanyTeam> teamMembers = teamRepository.findActiveTeamMembers();
        return teamMembers.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * 获取核心成员（公开接口）
     *
     * @return 核心成员列表
     */
    @Override
    public List<CompanyTeamResponse> getCoreMembers() {
        List<CompanyTeam> coreMembers = teamRepository.findActiveCoreMembers();
        return coreMembers.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * 根据部门获取团队成员（公开接口）
     *
     * @param department 部门名称
     * @return 团队成员列表
     */
    @Override
    public List<CompanyTeamResponse> getTeamMembersByDepartment(String department) {
        List<CompanyTeam> teamMembers = teamRepository.findByDepartmentAndStatus(department);
        return teamMembers.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * 获取所有团队成员（管理接口）
     *
     * @return 团队成员列表
     */
    @Override
    public List<CompanyTeamResponse> getAllTeamMembers() {
        List<CompanyTeam> teamMembers = teamRepository.findAll();
        return teamMembers.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * 根据ID获取团队成员
     *
     * @param id 成员ID
     * @return 团队成员
     */
    @Override
    public CompanyTeamResponse getTeamMemberById(Long id) {
        CompanyTeam teamMember = teamRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "团队成员不存在"));
        return convertToResponse(teamMember);
    }

    /**
     * 创建团队成员
     *
     * @param request 创建请求
     * @param userId  操作用户ID
     * @return 创建的团队成员
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CompanyTeamResponse createTeamMember(CompanyTeamRequest request, Long userId) {
        log.info("创建团队成员，操作用户ID：{}", userId);

        // 创建团队成员
        CompanyTeam teamMember = new CompanyTeam();
        BeanUtils.copyProperties(request, teamMember);
        teamMember.setCreatedBy(userId);
        teamMember.setCreateTime(LocalDateTime.now());

        // 保存
        CompanyTeam savedTeamMember = teamRepository.save(teamMember);
        log.info("团队成员创建成功");

        return convertToResponse(savedTeamMember);
    }

    /**
     * 更新团队成员
     *
     * @param id      成员ID
     * @param request 更新请求
     * @param userId  操作用户ID
     * @return 更新后的团队成员
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CompanyTeamResponse updateTeamMember(Long id, CompanyTeamRequest request, Long userId) {
        log.info("更新团队成员，ID：{}，操作用户ID：{}", id, userId);

        CompanyTeam teamMember = teamRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "团队成员不存在"));

        // 更新信息
        BeanUtils.copyProperties(request, teamMember);
        teamMember.setUpdatedBy(userId);
        teamMember.setUpdateTime(LocalDateTime.now());

        // 保存更新
        CompanyTeam savedTeamMember = teamRepository.save(teamMember);
        log.info("团队成员更新成功");

        return convertToResponse(savedTeamMember);
    }

    /**
     * 删除团队成员
     *
     * @param id      成员ID
     * @param userId  操作用户ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTeamMember(Long id, Long userId) {
        log.info("删除团队成员，ID：{}，操作用户ID：{}", id, userId);

        CompanyTeam teamMember = teamRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "团队成员不存在"));

        teamRepository.delete(teamMember);
        log.info("团队成员删除成功");
    }

    /**
     * 批量更新排序
     *
     * @param sortRequests 排序请求列表
     * @param userId       操作用户ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSortOrder(List<TeamSortRequest> sortRequests, Long userId) {
        log.info("批量更新团队成员排序，操作用户ID：{}", userId);

        for (TeamSortRequest sortRequest : sortRequests) {
            CompanyTeam teamMember = teamRepository.findById(sortRequest.getId())
                    .orElseThrow(() -> new BusinessException(404, "团队成员不存在"));

            teamMember.setSortOrder(sortRequest.getSortOrder());
            teamMember.setUpdatedBy(userId);
            teamMember.setUpdateTime(LocalDateTime.now());

            teamRepository.save(teamMember);
        }

        log.info("团队成员排序更新成功");
    }

    /**
     * 实体对象转换为响应DTO
     *
     * @param teamMember 实体对象
     * @return 响应DTO
     */
    private CompanyTeamResponse convertToResponse(CompanyTeam teamMember) {
        CompanyTeamResponse response = new CompanyTeamResponse();
        BeanUtils.copyProperties(teamMember, response);
        return response;
    }
}