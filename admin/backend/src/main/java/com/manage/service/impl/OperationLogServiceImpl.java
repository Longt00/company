package com.manage.service.impl;

import com.manage.entity.OperationLog;
import com.manage.repository.OperationLogRepository;
import com.manage.service.OperationLogService;
import com.manage.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 操作日志服务实现类
 *
 * @author System
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OperationLogServiceImpl implements OperationLogService {

    private final OperationLogRepository operationLogRepository;
    private final UserService userService;

    @Override
    public void log(String operationType, String operationModule, String operationDesc,
                   String targetType, Long targetId, String targetName) {
        try {
            // 获取当前用户信息
            Long userId = getCurrentUserId();
            String username = getCurrentUsername();

            logWithUser(operationType, operationModule, operationDesc, targetType, targetId, targetName, userId, username);
        } catch (Exception e) {
            log.error("记录操作日志失败: {}", e.getMessage());
        }
    }

    @Override
    public void log(String operationType, String operationModule, String operationDesc,
                   String targetType, Long targetId, String targetName,
                   Long userId, String username) {
        logWithUser(operationType, operationModule, operationDesc, targetType, targetId, targetName, userId, username);
    }

    /**
     * 带用户信息的日志记录方法
     */
    private void logWithUser(String operationType, String operationModule, String operationDesc,
                            String targetType, Long targetId, String targetName,
                            Long userId, String username) {
        try {
            OperationLog operationLog = new OperationLog();
            operationLog.setOperationType(operationType);
            operationLog.setOperationModule(operationModule);
            operationLog.setOperationDesc(operationDesc);
            operationLog.setTargetType(targetType);
            operationLog.setTargetId(targetId);
            operationLog.setTargetName(targetName);

            // 处理用户信息，如果为空则使用默认值
            operationLog.setUserId(userId != null ? userId : 0L);
            operationLog.setUsername(username != null ? username : "system");

            // 获取请求信息
            ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                operationLog.setIpAddress(getClientIpAddress(request));
                operationLog.setUserAgent(request.getHeader("User-Agent"));
            }

            operationLogRepository.save(operationLog);
            log.debug("操作日志记录成功: {} - {}", operationType, operationDesc);
        } catch (Exception e) {
            log.error("记录操作日志失败: {}", e.getMessage(), e);
        }
    }

    @Override
    public List<OperationLog> getRecentLogs(int limit) {
        try {
            return operationLogRepository.findRecentLogs(limit);
        } catch (Exception e) {
            log.error("获取最近日志失败: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public List<OperationLog> getUserLogs(Long userId) {
        try {
            return operationLogRepository.findByUserIdOrderByCreateTimeDesc(userId);
        } catch (Exception e) {
            log.error("获取用户日志失败: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public Page<OperationLog> getLogsPage(Pageable pageable) {
        try {
            return operationLogRepository.findAllByOrderByCreateTimeDesc(pageable);
        } catch (Exception e) {
            log.error("分页查询日志失败: {}", e.getMessage());
            return Page.empty(pageable);
        }
    }

    @Override
    public Page<OperationLog> getLogsByModule(String operationModule, Pageable pageable) {
        try {
            return operationLogRepository.findByOperationModuleOrderByCreateTimeDesc(operationModule, pageable);
        } catch (Exception e) {
            log.error("按模块查询日志失败: {}", e.getMessage());
            return Page.empty(pageable);
        }
    }

    @Override
    public Page<OperationLog> getLogsByType(String operationType, Pageable pageable) {
        try {
            return operationLogRepository.findByOperationTypeOrderByCreateTimeDesc(operationType, pageable);
        } catch (Exception e) {
            log.error("按类型查询日志失败: {}", e.getMessage());
            return Page.empty(pageable);
        }
    }

    @Override
    public Page<OperationLog> getLogsByTimeRange(LocalDateTime startTime, LocalDateTime endTime, Pageable pageable) {
        try {
            return operationLogRepository.findByCreateTimeBetween(startTime, endTime, pageable);
        } catch (Exception e) {
            log.error("按时间范围查询日志失败: {}", e.getMessage());
            return Page.empty(pageable);
        }
    }

    @Override
    public Page<OperationLog> getLogsByUsername(String username, Pageable pageable) {
        try {
            return operationLogRepository.findByUsernameOrderByCreateTimeDesc(username, pageable);
        } catch (Exception e) {
            log.error("按用户名查询日志失败: {}", e.getMessage());
            return Page.empty(pageable);
        }
    }

    @Override
    public Page<OperationLog> searchLogs(String keyword, Pageable pageable) {
        try {
            return operationLogRepository.searchLogs(keyword, pageable);
        } catch (Exception e) {
            log.error("搜索日志失败: {}", e.getMessage());
            return Page.empty(pageable);
        }
    }

    @Override
    public Map<String, Object> getOperationStatistics() {
        try {
            Map<String, Object> statistics = new HashMap<>();

            // 统计各操作类型的数量
            List<Object[]> typeStats = operationLogRepository.countLogsByOperationType();
            Map<String, Long> typeCount = new HashMap<>();
            for (Object[] stat : typeStats) {
                typeCount.put((String) stat[0], (Long) stat[1]);
            }
            statistics.put("operationTypes", typeCount);

            // 统计各操作模块的数量
            List<Object[]> moduleStats = operationLogRepository.countLogsByOperationModule();
            Map<String, Long> moduleCount = new HashMap<>();
            for (Object[] stat : moduleStats) {
                moduleCount.put((String) stat[0], (Long) stat[1]);
            }
            statistics.put("operationModules", moduleCount);

            // 统计最近7天的数量
            LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
            Long recentCount = operationLogRepository.countLogsByTimeRange(sevenDaysAgo, LocalDateTime.now());
            statistics.put("recent7DaysCount", recentCount);

            // 统计最近30天的数量
            LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
            Long monthCount = operationLogRepository.countLogsByTimeRange(thirtyDaysAgo, LocalDateTime.now());
            statistics.put("recent30DaysCount", monthCount);

            return statistics;
        } catch (Exception e) {
            log.error("获取操作统计失败: {}", e.getMessage());
            return new HashMap<>();
        }
    }

    @Override
    public int cleanExpiredLogs(LocalDateTime beforeTime) {
        try {
            List<OperationLog> expiredLogs = operationLogRepository.findByCreateTimeBetween(
                LocalDateTime.of(1970, 1, 1, 0, 0), beforeTime,
                PageRequest.of(0, 1000)).getContent();

            int deletedCount = 0;
            for (OperationLog operationLog : expiredLogs) {
                try {
                    operationLogRepository.delete(operationLog);
                    deletedCount++;
                } catch (Exception e) {
                    log.error("删除过期日志失败: {}", e.getMessage());
                }
            }

            log.info("清理过期日志完成，删除数量: {}", deletedCount);
            return deletedCount;
        } catch (Exception e) {
            log.error("清理过期日志失败: {}", e.getMessage());
            return 0;
        }
    }

    @Override
    public void batchLog(List<OperationLog> logs) {
        try {
            operationLogRepository.saveAll(logs);
            log.debug("批量记录操作日志成功，数量: {}", logs.size());
        } catch (Exception e) {
            log.error("批量记录操作日志失败: {}", e.getMessage());
        }
    }

    @Override
    public Page<OperationLog> getLogsByConditions(String operationType, String operationModule,
                                                 String username, LocalDateTime startTime,
                                                 LocalDateTime endTime, String keyword,
                                                 Pageable pageable) {
        try {
            // 如果有搜索关键词，优先使用搜索
            if (keyword != null && !keyword.trim().isEmpty()) {
                return searchLogs(keyword, pageable);
            }

            // 如果有操作类型过滤
            if (operationType != null && !operationType.trim().isEmpty()) {
                return getLogsByType(operationType, pageable);
            }

            // 如果有操作模块过滤
            if (operationModule != null && !operationModule.trim().isEmpty()) {
                return getLogsByModule(operationModule, pageable);
            }

            // 如果有用户名过滤
            if (username != null && !username.trim().isEmpty()) {
                return getLogsByUsername(username, pageable);
            }

            // 如果有时间范围过滤
            if (startTime != null && endTime != null) {
                return getLogsByTimeRange(startTime, endTime, pageable);
            }

            // 默认返回所有日志
            return getLogsPage(pageable);
        } catch (Exception e) {
            log.error("按条件查询日志失败: {}", e.getMessage());
            return Page.empty(pageable);
        }
    }

    /**
     * 获取当前用户ID
     */
    private Long getCurrentUserId() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
                String username = ((UserDetails) authentication.getPrincipal()).getUsername();
                return userService.findByUsername(username).getId();
            }
        } catch (Exception e) {
            log.warn("获取当前用户ID失败: {}", e.getMessage());
        }
        return null;
    }

    /**
     * 获取当前用户名
     */
    private String getCurrentUsername() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null) {
                return authentication.getName();
            }
        } catch (Exception e) {
            log.warn("获取当前用户名失败: {}", e.getMessage());
        }
        return "system";
    }

    /**
     * 获取客户端真实IP地址
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        // 处理多个IP的情况，取第一个
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }

        return ip;
    }
}