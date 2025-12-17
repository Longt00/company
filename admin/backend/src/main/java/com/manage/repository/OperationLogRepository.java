package com.manage.repository;

import com.manage.entity.OperationLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 操作日志数据访问层接口
 *
 * @author System
 * @version 1.0
 */
@Repository
public interface OperationLogRepository extends JpaRepository<OperationLog, Long> {

    /**
     * 查询最近的操作日志（分页）
     *
     * @param pageable 分页参数
     * @return 操作日志分页结果
     */
    Page<OperationLog> findAllByOrderByCreateTimeDesc(Pageable pageable);

    /**
     * 查询指定用户的操作日志
     *
     * @param userId 用户ID
     * @return 操作日志列表
     */
    List<OperationLog> findByUserIdOrderByCreateTimeDesc(Long userId);

    /**
     * 查询最近N条日志
     *
     * @return 操作日志列表
     */
    List<OperationLog> findTop10ByOrderByCreateTimeDesc();

    /**
     * 查询指定数量最近的日志
     *
     * @param limit 限制数量
     * @return 操作日志列表
     */
    @Query(value = "SELECT * FROM operation_log ORDER BY create_time DESC LIMIT :limit", nativeQuery = true)
    List<OperationLog> findRecentLogs(@Param("limit") Integer limit);

    /**
     * 根据操作模块查询日志
     *
     * @param operationModule 操作模块
     * @param pageable        分页参数
     * @return 操作日志分页结果
     */
    Page<OperationLog> findByOperationModuleOrderByCreateTimeDesc(String operationModule, Pageable pageable);

    /**
     * 根据操作类型查询日志
     *
     * @param operationType 操作类型
     * @param pageable      分页参数
     * @return 操作日志分页结果
     */
    Page<OperationLog> findByOperationTypeOrderByCreateTimeDesc(String operationType, Pageable pageable);

    /**
     * 根据时间范围查询日志
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param pageable  分页参数
     * @return 操作日志分页结果
     */
    @Query("SELECT ol FROM OperationLog ol WHERE ol.createTime BETWEEN :startTime AND :endTime ORDER BY ol.createTime DESC")
    Page<OperationLog> findByCreateTimeBetween(@Param("startTime") LocalDateTime startTime,
                                             @Param("endTime") LocalDateTime endTime,
                                             Pageable pageable);

    /**
     * 根据用户名查询日志
     *
     * @param username 用户名
     * @param pageable 分页参数
     * @return 操作日志分页结果
     */
    Page<OperationLog> findByUsernameOrderByCreateTimeDesc(String username, Pageable pageable);

    /**
     * 统计各操作类型的数量
     *
     * @return 统计结果
     */
    @Query("SELECT ol.operationType, COUNT(ol) FROM OperationLog ol GROUP BY ol.operationType")
    List<Object[]> countLogsByOperationType();

    /**
     * 统计各操作模块的数量
     *
     * @return 统计结果
     */
    @Query("SELECT ol.operationModule, COUNT(ol) FROM OperationLog ol GROUP BY ol.operationModule")
    List<Object[]> countLogsByOperationModule();

    /**
     * 统计指定时间范围内的日志数量
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 日志数量
     */
    @Query("SELECT COUNT(ol) FROM OperationLog ol WHERE ol.createTime BETWEEN :startTime AND :endTime")
    Long countLogsByTimeRange(@Param("startTime") LocalDateTime startTime,
                             @Param("endTime") LocalDateTime endTime);

    /**
     * 根据关键词搜索日志
     *
     * @param keyword  关键词
     * @param pageable 分页参数
     * @return 操作日志分页结果
     */
    @Query("SELECT ol FROM OperationLog ol WHERE ol.operationDesc LIKE %:keyword% OR ol.targetName LIKE %:keyword% ORDER BY ol.createTime DESC")
    Page<OperationLog> searchLogs(@Param("keyword") String keyword, Pageable pageable);

    /**
     * 查询指定用户在指定时间范围内的日志
     *
     * @param userId    用户ID
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param pageable  分页参数
     * @return 操作日志分页结果
     */
    @Query("SELECT ol FROM OperationLog ol WHERE ol.userId = :userId AND ol.createTime BETWEEN :startTime AND :endTime ORDER BY ol.createTime DESC")
    Page<OperationLog> findByUserIdAndCreateTimeBetween(@Param("userId") Long userId,
                                                       @Param("startTime") LocalDateTime startTime,
                                                       @Param("endTime") LocalDateTime endTime,
                                                       Pageable pageable);
}