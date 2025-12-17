package com.manage.repository;

import com.manage.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 用户数据访问层接口
 *
 * @author System
 * @version 1.0
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 根据用户名查找用户
     *
     * @param username 用户名
     * @return 用户信息
     */
    Optional<User> findByUsername(String username);

    /**
     * 根据邮箱查找用户
     *
     * @param email 邮箱
     * @return 用户信息
     */
    Optional<User> findByEmail(String email);

    /**
     * 检查用户名是否存在
     *
     * @param username 用户名
     * @return 存在返回true，不存在返回false
     */
    boolean existsByUsername(String username);

    /**
     * 检查邮箱是否存在
     *
     * @param email 邮箱
     * @return 存在返回true，不存在返回false
     */
    boolean existsByEmail(String email);

    /**
     * 根据用户名和状态查找用户
     *
     * @param username 用户名
     * @param status   状态
     * @return 用户信息
     */
    @Query("SELECT u FROM User u WHERE u.username = :username AND u.status = :status")
    Optional<User> findByUsernameAndStatus(@Param("username") String username, @Param("status") Integer status);

    /**
     * 根据邮箱和状态查找用户
     *
     * @param email  邮箱
     * @param status 状态
     * @return 用户信息
     */
    @Query("SELECT u FROM User u WHERE u.email = :email AND u.status = :status")
    Optional<User> findByEmailAndStatus(@Param("email") String email, @Param("status") Integer status);
}