package com.manage.service;

import com.manage.entity.OperationLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 操作日志服务接口
 *
 * @author System
 * @version 1.0
 */
public interface OperationLogService {

    /**
     * 记录操作日志
     *
     * @param operationType  操作类型：UPLOAD/UPDATE/DELETE/CREATE
     * @param operationModule 操作模块：IMAGE/VIDEO/PRODUCT/COMPANY
     * @param operationDesc  操作描述
     * @param targetType      目标类型
     * @param targetId       目标ID
     * @param targetName     目标名称
     */
    void log(String operationType, String operationModule, String operationDesc,
             String targetType, Long targetId, String targetName);

    /**
     * 记录操作日志（带用户信息）
     *
     * @param operationType  操作类型
     * @param operationModule 操作模块
     * @param operationDesc  操作描述
     * @param targetType      目标类型
     * @param targetId       目标ID
     * @param targetName     目标名称
     * @param userId         用户ID
     * @param username       用户名
     */
    void log(String operationType, String operationModule, String operationDesc,
             String targetType, Long targetId, String targetName,
             Long userId, String username);

    /**
     * 获取最近的操作日志
     *
     * @param limit 限制数量
     * @return 操作日志列表
     */
    List<OperationLog> getRecentLogs(int limit);

    /**
     * 获取指定用户的操作日志
     *
     * @param userId 用户ID
     * @return 操作日志列表
     */
    List<OperationLog> getUserLogs(Long userId);

    /**
     * 分页查询操作日志
     *
     * @param pageable 分页参数
     * @return 操作日志分页结果
     */
    Page<OperationLog> getLogsPage(Pageable pageable);

    /**
     * 根据操作模块查询日志
     *
     * @param operationModule 操作模块
     * @param pageable        分页参数
     * @return 操作日志分页结果
     */
    Page<OperationLog> getLogsByModule(String operationModule, Pageable pageable);

    /**
     * 根据操作类型查询日志
     *
     * @param operationType 操作类型
     * @param pageable      分页参数
     * @return 操作日志分页结果
     */
    Page<OperationLog> getLogsByType(String operationType, Pageable pageable);

    /**
     * 根据时间范围查询日志
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param pageable  分页参数
     * @return 操作日志分页结果
     */
    Page<OperationLog> getLogsByTimeRange(LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);

    /**
     * 根据用户名查询日志
     *
     * @param username 用户名
     * @param pageable 分页参数
     * @return 操作日志分页结果
     */
    Page<OperationLog> getLogsByUsername(String username, Pageable pageable);

    /**
     * 搜索日志
     *
     * @param keyword  关键词
     * @param pageable 分页参数
     * @return 操作日志分页结果
     */
    Page<OperationLog> searchLogs(String keyword, Pageable pageable);

    /**
     * 获取操作统计信息
     *
     * @return 统计信息
     */
    Map<String, Object> getOperationStatistics();

    /**
     * 清理过期日志
     *
     * @param beforeTime 清理此时间之前的日志
     * @return 清理的记录数
     */
    int cleanExpiredLogs(LocalDateTime beforeTime);

    /**
     * 批量记录操作日志
     *
     * @param logs 操作日志列表
     */
    void batchLog(List<OperationLog> logs);

    /**
     * 根据条件查询日志
     *
     * @param operationType  操作类型（可选）
     * @param operationModule 操作模块（可选）
     * @param username       用户名（可选）
     * @param startTime      开始时间（可选）
     * @param endTime        结束时间（可选）
     * @param keyword        关键词（可选）
     * @param pageable       分页参数
     * @return 操作日志分页结果
     */
    Page<OperationLog> getLogsByConditions(String operationType, String operationModule,
                                          String username, LocalDateTime startTime,
                                          LocalDateTime endTime, String keyword,
                                          Pageable pageable);
}