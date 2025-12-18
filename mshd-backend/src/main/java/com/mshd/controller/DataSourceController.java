package com.mshd.controller;

import com.mshd.common.Result;
import com.mshd.entity.DataSource;
import com.mshd.service.DataSourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据源管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/datasource")
@RequiredArgsConstructor
@CrossOrigin
public class DataSourceController {

    private final DataSourceService dataSourceService;

    /**
     * 创建数据源
     */
    @PostMapping("/create")
    public Result<DataSource> create(@RequestBody DataSource dataSource) {
        try {
            // 检查编码是否已存在
            if (dataSourceService.findBySourceCode(dataSource.getSourceCode()).isPresent()) {
                return Result.error("数据源编码已存在");
            }

            DataSource saved = dataSourceService.save(dataSource);
            return Result.success("创建成功", saved);
        } catch (Exception e) {
            log.error("创建数据源失败", e);
            return Result.error("创建失败: " + e.getMessage());
        }
    }

    /**
     * 更新数据源
     */
    @PutMapping("/update/{id}")
    public Result<DataSource> update(@PathVariable Long id, @RequestBody DataSource dataSource) {
        try {
            // 检查数据源是否存在
            if (!dataSourceService.findById(id).isPresent()) {
                return Result.error("数据源不存在");
            }

            dataSource.setId(id);
            DataSource updated = dataSourceService.save(dataSource);
            return Result.success("更新成功", updated);
        } catch (Exception e) {
            log.error("更新数据源失败", e);
            return Result.error("更新失败: " + e.getMessage());
        }
    }

    /**
     * 根据ID查询
     */
    @GetMapping("/{id}")
    public Result<DataSource> getById(@PathVariable Long id) {
        return dataSourceService.findById(id)
                .map(Result::success)
                .orElse(Result.error("数据源不存在"));
    }

    /**
     * 查询所有数据源
     */
    @GetMapping("/list")
    public Result<List<DataSource>> list() {
        List<DataSource> list = dataSourceService.findAll();
        return Result.success(list);
    }

    /**
     * 根据类型查询
     */
    @GetMapping("/type/{sourceType}")
    public Result<List<DataSource>> listByType(@PathVariable String sourceType) {
        List<DataSource> list = dataSourceService.findBySourceType(sourceType);
        return Result.success(list);
    }

    /**
     * 查询启用的数据源
     */
    @GetMapping("/enabled")
    public Result<List<DataSource>> listEnabled() {
        List<DataSource> list = dataSourceService.findEnabled();
        return Result.success(list);
    }

    /**
     * 启用/禁用数据源
     */
    @PutMapping("/toggle/{id}")
    public Result<String> toggleEnabled(@PathVariable Long id, @RequestParam Boolean enabled) {
        try {
            dataSourceService.toggleEnabled(id, enabled);
            String message = enabled ? "已启用" : "已禁用";
            return Result.success(message);
        } catch (Exception e) {
            log.error("切换数据源状态失败", e);
            return Result.error("操作失败: " + e.getMessage());
        }
    }

    /**
     * 删除数据源
     */
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        try {
            dataSourceService.delete(id);
            return Result.success("删除成功");
        } catch (Exception e) {
            log.error("删除数据源失败", e);
            return Result.error("删除失败: " + e.getMessage());
        }
    }

    /**
     * 测试数据源连接
     */
    @PostMapping("/test")
    public Result<String> testConnection(@RequestBody DataSource dataSource) {
        try {
            boolean success = dataSourceService.testConnection(dataSource);
            if (success) {
                return Result.success("连接测试成功");
            } else {
                return Result.error("连接测试失败");
            }
        } catch (Exception e) {
            log.error("测试连接失败", e);
            return Result.error("测试失败: " + e.getMessage());
        }
    }

    /**
     * 手动同步数据源
     */
    @PostMapping("/sync/{id}")
    public Result<String> syncDataSource(@PathVariable Long id) {
        try {
            // TODO: 实现实际的同步逻辑
            dataSourceService.updateSyncStatus(id, 2, null);
            return Result.success("同步成功");
        } catch (Exception e) {
            log.error("同步数据源失败", e);
            dataSourceService.updateSyncStatus(id, 3, e.getMessage());
            return Result.error("同步失败: " + e.getMessage());
        }
    }

    /**
     * 统计数据源
     */
    @GetMapping("/stats")
    public Result<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", dataSourceService.findAll().size());
        stats.put("enabled", dataSourceService.countEnabled());
        stats.put("business", dataSourceService.countByType("business"));
        stats.put("ubiquitous", dataSourceService.countByType("ubiquitous"));
        stats.put("publicOpinion", dataSourceService.countByType("public_opinion"));
        return Result.success(stats);
    }
}
