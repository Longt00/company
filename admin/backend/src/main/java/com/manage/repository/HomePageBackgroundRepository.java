package com.manage.repository;

import com.manage.entity.HomePageBackground;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 主页背景数据访问层
 *
 * @author System
 * @version 1.0
 */
@Repository
public interface HomePageBackgroundRepository extends JpaRepository<HomePageBackground, Long> {

    /**
     * 查找当前启用的背景
     *
     * @return 当前启用的背景
     */
    @Query("SELECT h FROM HomePageBackground h WHERE h.isActive = true ORDER BY h.updateTime DESC")
    Optional<HomePageBackground> findActiveBackground();

    /**
     * 查找所有启用的背景
     *
     * @return 所有启用的背景列表
     */
    @Query("SELECT h FROM HomePageBackground h WHERE h.isActive = true ORDER BY h.updateTime DESC")
    List<HomePageBackground> findAllActiveBackgrounds();

    /**
     * 根据背景类型查找启用的背景
     *
     * @param backgroundType 背景类型
     * @return 指定类型的启用背景
     */
    @Query("SELECT h FROM HomePageBackground h WHERE h.backgroundType = :backgroundType AND h.isActive = true ORDER BY h.updateTime DESC")
    List<HomePageBackground> findActiveBackgroundsByType(@Param("backgroundType") String backgroundType);

    /**
     * 查找指定ID的背景
     *
     * @param id 背景ID
     * @return 背景信息
     */
    @Query("SELECT h FROM HomePageBackground h WHERE h.id = :id")
    Optional<HomePageBackground> findBackgroundById(@Param("id") Long id);

    /**
     * 统计指定类型的背景数量
     *
     * @param backgroundType 背景类型
     * @return 数量
     */
    @Query("SELECT COUNT(h) FROM HomePageBackground h WHERE h.backgroundType = :backgroundType")
    Long countByBackgroundType(@Param("backgroundType") String backgroundType);

    /**
     * 统计启用的背景数量
     *
     * @return 启用的背景数量
     */
    @Query("SELECT COUNT(h) FROM HomePageBackground h WHERE h.isActive = true")
    Long countActiveBackgrounds();
}