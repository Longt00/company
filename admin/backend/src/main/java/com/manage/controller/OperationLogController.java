package com.manage.controller;

import com.manage.common.Result;
import com.manage.entity.OperationLog;
import com.manage.entity.User;
import com.manage.service.OperationLogService;
import com.manage.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 操作日志控制器
 *
 * @author System
 * @version 1.0
 */
@Slf4j
@RestController
@RequestMapping("/api/admin/logs")
@RequiredArgsConstructor
public class OperationLogController {

    private final OperationLogService operationLogService;
    private final UserService userService;

    /**
     * 获取最近的操作日志
     *
     * @param limit 限制数量，默认10
     * @return 操作日志列表
     */
    @GetMapping("/recent")
    public Result<List<OperationLog>> getRecentLogs(
            @RequestParam(defaultValue = "10") int limit) {
        try {
            if (limit <= 0 || limit > 100) {
                return Result.error("limit参数必须在1-100之间");
            }

            List<OperationLog> logs = operationLogService.getRecentLogs(limit);
            log.info("获取最近操作日志成功，数量: {}", logs.size());
            return Result.success("查询成功", logs);
        } catch (Exception e) {
            log.error("获取最近操作日志失败: {}", e.getMessage());
            return Result.error("查询失败：" + e.getMessage());
        }
    }

    /**
     * 获取当前用户的操作日志
     *
     * @return 操作日志列表
     */
    @GetMapping("/my")
    public Result<List<OperationLog>> getMyLogs() {
        try {
            Long userId = getCurrentUserId();
            if (userId == null) {
                return Result.error("获取用户信息失败");
            }

            List<OperationLog> logs = operationLogService.getUserLogs(userId);
            log.info("获取用户操作日志成功，用户ID: {}, 数量: {}", userId, logs.size());
            return Result.success("查询成功", logs);
        } catch (Exception e) {
            log.error("获取用户操作日志失败: {}", e.getMessage());
            return Result.error("查询失败：" + e.getMessage());
        }
    }

    /**
     * 分页查询操作日志
     *
     * @param page 页码，从0开始
     * @param size 每页大小
     * @return 操作日志分页结果
     */
    @GetMapping("/page")
    public Result<Map<String, Object>> getLogsPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            if (page < 0) {
                return Result.error("页码不能小于0");
            }
            if (size <= 0 || size > 100) {
                return Result.error("每页大小必须在1-100之间");
            }

            org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);
            org.springframework.data.domain.Page<OperationLog> pageResult = operationLogService.getLogsPage(pageable);

            Map<String, Object> result = new HashMap<>();
            result.put("content", pageResult.getContent());
            result.put("totalElements", pageResult.getTotalElements());
            result.put("totalPages", pageResult.getTotalPages());
            result.put("currentPage", page);
            result.put("size", size);
            result.put("first", pageResult.isFirst());
            result.put("last", pageResult.isLast());

            log.info("分页查询操作日志成功，页码: {}, 大小: {}, 总数: {}", page, size, pageResult.getTotalElements());
            return Result.success("查询成功", result);
        } catch (Exception e) {
            log.error("分页查询操作日志失败: {}", e.getMessage());
            return Result.error("查询失败：" + e.getMessage());
        }
    }

    /**
     * 根据操作模块查询日志
     *
     * @param operationModule 操作模块
     * @param page            页码
     * @param size            每页大小
     * @return 操作日志分页结果
     */
    @GetMapping("/module/{operationModule}")
    public Result<Map<String, Object>> getLogsByModule(
            @PathVariable String operationModule,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            if (operationModule == null || operationModule.trim().isEmpty()) {
                return Result.error("操作模块不能为空");
            }

            org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);
            org.springframework.data.domain.Page<OperationLog> pageResult = operationLogService.getLogsByModule(operationModule, pageable);

            Map<String, Object> result = new HashMap<>();
            result.put("content", pageResult.getContent());
            result.put("totalElements", pageResult.getTotalElements());
            result.put("totalPages", pageResult.getTotalPages());
            result.put("operationModule", operationModule);

            log.info("按模块查询操作日志成功，模块: {}, 数量: {}", operationModule, pageResult.getTotalElements());
            return Result.success("查询成功", result);
        } catch (Exception e) {
            log.error("按模块查询操作日志失败: {}", e.getMessage());
            return Result.error("查询失败：" + e.getMessage());
        }
    }

    /**
     * 根据操作类型查询日志
     *
     * @param operationType 操作类型
     * @param page           页码
     * @param size           每页大小
     * @return 操作日志分页结果
     */
    @GetMapping("/type/{operationType}")
    public Result<Map<String, Object>> getLogsByType(
            @PathVariable String operationType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            if (operationType == null || operationType.trim().isEmpty()) {
                return Result.error("操作类型不能为空");
            }

            org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);
            org.springframework.data.domain.Page<OperationLog> pageResult = operationLogService.getLogsByType(operationType, pageable);

            Map<String, Object> result = new HashMap<>();
            result.put("content", pageResult.getContent());
            result.put("totalElements", pageResult.getTotalElements());
            result.put("totalPages", pageResult.getTotalPages());
            result.put("operationType", operationType);

            log.info("按类型查询操作日志成功，类型: {}, 数量: {}", operationType, pageResult.getTotalElements());
            return Result.success("查询成功", result);
        } catch (Exception e) {
            log.error("按类型查询操作日志失败: {}", e.getMessage());
            return Result.error("查询失败：" + e.getMessage());
        }
    }

    /**
     * 根据用户名查询日志
     *
     * @param username 用户名
     * @param page     页码
     * @param size     每页大小
     * @return 操作日志分页结果
     */
    @GetMapping("/user/{username}")
    public Result<Map<String, Object>> getLogsByUsername(
            @PathVariable String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            if (username == null || username.trim().isEmpty()) {
                return Result.error("用户名不能为空");
            }

            org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);
            org.springframework.data.domain.Page<OperationLog> pageResult = operationLogService.getLogsByUsername(username, pageable);

            Map<String, Object> result = new HashMap<>();
            result.put("content", pageResult.getContent());
            result.put("totalElements", pageResult.getTotalElements());
            result.put("totalPages", pageResult.getTotalPages());
            result.put("username", username);

            log.info("按用户名查询操作日志成功，用户名: {}, 数量: {}", username, pageResult.getTotalElements());
            return Result.success("查询成功", result);
        } catch (Exception e) {
            log.error("按用户名查询操作日志失败: {}", e.getMessage());
            return Result.error("查询失败：" + e.getMessage());
        }
    }

    /**
     * 根据时间范围查询日志
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param page      页码
     * @param size      每页大小
     * @return 操作日志分页结果
     */
    @GetMapping("/time-range")
    public Result<Map<String, Object>> getLogsByTimeRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            if (startTime == null || endTime == null) {
                return Result.error("开始时间和结束时间不能为空");
            }
            if (startTime.isAfter(endTime)) {
                return Result.error("开始时间不能晚于结束时间");
            }

            org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);
            org.springframework.data.domain.Page<OperationLog> pageResult = operationLogService.getLogsByTimeRange(startTime, endTime, pageable);

            Map<String, Object> result = new HashMap<>();
            result.put("content", pageResult.getContent());
            result.put("totalElements", pageResult.getTotalElements());
            result.put("totalPages", pageResult.getTotalPages());
            result.put("startTime", startTime);
            result.put("endTime", endTime);

            log.info("按时间范围查询操作日志成功，时间范围: {} ~ {}, 数量: {}", startTime, endTime, pageResult.getTotalElements());
            return Result.success("查询成功", result);
        } catch (Exception e) {
            log.error("按时间范围查询操作日志失败: {}", e.getMessage());
            return Result.error("查询失败：" + e.getMessage());
        }
    }

    /**
     * 搜索操作日志
     *
     * @param keyword 关键词
     * @param page    页码
     * @param size    每页大小
     * @return 操作日志分页结果
     */
    @GetMapping("/search")
    public Result<Map<String, Object>> searchLogs(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            if (keyword == null || keyword.trim().isEmpty()) {
                return Result.error("搜索关键词不能为空");
            }

            org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);
            org.springframework.data.domain.Page<OperationLog> pageResult = operationLogService.searchLogs(keyword, pageable);

            Map<String, Object> result = new HashMap<>();
            result.put("content", pageResult.getContent());
            result.put("totalElements", pageResult.getTotalElements());
            result.put("totalPages", pageResult.getTotalPages());
            result.put("keyword", keyword);

            log.info("搜索操作日志成功，关键词: {}, 数量: {}", keyword, pageResult.getTotalElements());
            return Result.success("查询成功", result);
        } catch (Exception e) {
            log.error("搜索操作日志失败: {}", e.getMessage());
            return Result.error("查询失败：" + e.getMessage());
        }
    }

    /**
     * 获取操作统计信息
     *
     * @return 统计信息
     */
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getOperationStatistics() {
        try {
            Map<String, Object> statistics = operationLogService.getOperationStatistics();
            log.info("获取操作统计信息成功");
            return Result.success("查询成功", statistics);
        } catch (Exception e) {
            log.error("获取操作统计信息失败: {}", e.getMessage());
            return Result.error("查询失败：" + e.getMessage());
        }
    }

    /**
     * 复合查询操作日志
     *
     * @param operationType  操作类型（可选）
     * @param operationModule 操作模块（可选）
     * @param username       用户名（可选）
     * @param startTime      开始时间（可选）
     * @param endTime        结束时间（可选）
     * @param keyword        关键词（可选）
     * @param page           页码
     * @param size           每页大小
     * @return 操作日志分页结果
     */
    @GetMapping("/filter")
    public Result<Map<String, Object>> getLogsByConditions(
            @RequestParam(required = false) String operationType,
            @RequestParam(required = false) String operationModule,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);
            org.springframework.data.domain.Page<OperationLog> pageResult = operationLogService.getLogsByConditions(
                operationType, operationModule, username, startTime, endTime, keyword, pageable);

            Map<String, Object> result = new HashMap<>();
            result.put("content", pageResult.getContent());
            result.put("totalElements", pageResult.getTotalElements());
            result.put("totalPages", pageResult.getTotalPages());
            Map<String, Object> filters = new HashMap<>();
            filters.put("operationType", operationType);
            filters.put("operationModule", operationModule);
            filters.put("username", username);
            filters.put("startTime", startTime);
            filters.put("endTime", endTime);
            filters.put("keyword", keyword);
            result.put("filters", filters);

            log.info("复合查询操作日志成功，条件: {} {} {} {} {} {}, 数量: {}",
                operationType, operationModule, username, startTime, endTime, keyword,
                pageResult.getTotalElements());
            return Result.success("查询成功", result);
        } catch (Exception e) {
            log.error("复合查询操作日志失败: {}", e.getMessage());
            return Result.error("查询失败：" + e.getMessage());
        }
    }

    /**
     * 清理过期日志
     *
     * @param days 保留天数
     * @return 清理结果
     */
    @DeleteMapping("/cleanup")
    public Result<Map<String, Object>> cleanExpiredLogs(@RequestParam int days) {
        try {
            if (days <= 0) {
                return Result.error("保留天数必须大于0");
            }

            LocalDateTime beforeTime = LocalDateTime.now().minusDays(days);
            int deletedCount = operationLogService.cleanExpiredLogs(beforeTime);

            Map<String, Object> result = new HashMap<>();
            result.put("deletedCount", deletedCount);
            result.put("beforeTime", beforeTime);
            result.put("days", days);

            log.info("清理过期操作日志完成，删除数量: {}, 保留天数: {}", deletedCount, days);
            return Result.success("清理完成", result);
        } catch (Exception e) {
            log.error("清理过期操作日志失败: {}", e.getMessage());
            return Result.error("清理失败：" + e.getMessage());
        }
    }

    /**
     * 获取当前登录用户ID
     */
    private Long getCurrentUserId() {
        try {
            Authentication authentication = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
                String username = ((UserDetails) authentication.getPrincipal()).getUsername();
                User user = userService.findByUsername(username);
                return user.getId();
            }
        } catch (Exception e) {
            log.warn("获取当前用户ID失败: {}", e.getMessage());
        }
        return null;
    }
}