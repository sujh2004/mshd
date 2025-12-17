package com.mshd.repository;

import com.mshd.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 用户Repository
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 根据用户名查询
     */
    Optional<User> findByUsername(String username);

    /**
     * 根据角色查询
     */
    List<User> findByRole(String role);

    /**
     * 根据状态查询
     */
    List<User> findByStatus(Integer status);

    /**
     * 根据部门查询
     */
    List<User> findByDepartment(String department);

    /**
     * 根据邮箱查询
     */
    Optional<User> findByEmail(String email);

    /**
     * 判断用户名是否存在
     */
    boolean existsByUsername(String username);
}
