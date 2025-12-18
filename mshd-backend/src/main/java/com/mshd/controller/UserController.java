package com.mshd.controller;

import com.mshd.common.Result;
import com.mshd.entity.User;
import com.mshd.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 用户控制器
 */
@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@CrossOrigin
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> loginData) {
        String username = loginData.get("username");
        String password = loginData.get("password");

        if (username == null || username.trim().isEmpty() ||
            password == null || password.trim().isEmpty()) {
            return Result.error("用户名和密码不能为空");
        }

        try {
            // 查找用户
            Optional<User> userOpt = userRepository.findByUsername(username);
            if (!userOpt.isPresent()) {
                return Result.error("用户名或密码错误");
            }

            User user = userOpt.get();

            // 检查用户状态
            if (user.getStatus() == 0) {
                return Result.error("账号已被禁用");
            }

            // 验证密码
            if (!passwordEncoder.matches(password, user.getPassword())) {
                return Result.error("用户名或密码错误");
            }

            // 更新最后登录时间
            user.setLastLoginTime(LocalDateTime.now());
            userRepository.save(user);

            // 返回用户信息（不包含密码）
            Map<String, Object> data = new HashMap<>();
            data.put("id", user.getId());
            data.put("username", user.getUsername());
            data.put("realName", user.getRealName());
            data.put("email", user.getEmail());
            data.put("phone", user.getPhone());
            data.put("role", user.getRole());
            data.put("department", user.getDepartment());
            // 生成简单的token（实际项目中应使用JWT）
            data.put("token", "mshd-" + user.getId() + "-" + System.currentTimeMillis());

            log.info("用户登录成功: {}", username);
            return Result.success("登录成功", data);

        } catch (Exception e) {
            log.error("登录失败", e);
            return Result.error("登录失败: " + e.getMessage());
        }
    }

    /**
     * 用户注册（仅用于测试）
     */
    @PostMapping("/register")
    public Result<User> register(@RequestBody User user) {
        try {
            // 检查用户名是否已存在
            if (userRepository.findByUsername(user.getUsername()).isPresent()) {
                return Result.error("用户名已存在");
            }

            // 加密密码
            user.setPassword(passwordEncoder.encode(user.getPassword()));

            // 设置默认值
            if (user.getRole() == null || user.getRole().isEmpty()) {
                user.setRole("operator");
            }
            if (user.getStatus() == null) {
                user.setStatus(1);
            }

            User saved = userRepository.save(user);
            // 不返回密码
            saved.setPassword(null);

            log.info("用户注册成功: {}", user.getUsername());
            return Result.success("注册成功", saved);

        } catch (Exception e) {
            log.error("注册失败", e);
            return Result.error("注册失败: " + e.getMessage());
        }
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/info")
    public Result<User> getUserInfo(@RequestParam Long userId) {
        try {
            Optional<User> userOpt = userRepository.findById(userId);
            if (!userOpt.isPresent()) {
                return Result.error("用户不存在");
            }

            User user = userOpt.get();
            // 不返回密码
            user.setPassword(null);

            return Result.success(user);
        } catch (Exception e) {
            log.error("获取用户信息失败", e);
            return Result.error("获取用户信息失败: " + e.getMessage());
        }
    }

    /**
     * 用户登出
     */
    @PostMapping("/logout")
    public Result<String> logout() {
        // 简单返回成功（实际项目中需要清除token等）
        return Result.success("登出成功");
    }
}
