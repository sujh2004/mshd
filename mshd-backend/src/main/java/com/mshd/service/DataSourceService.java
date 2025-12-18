package com.mshd.service;

import com.mshd.entity.DataSource;
import com.mshd.repository.DataSourceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 数据源服务类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DataSourceService {

    private final DataSourceRepository dataSourceRepository;

    /**
     * 保存数据源
     */
    @Transactional
    public DataSource save(DataSource dataSource) {
        return dataSourceRepository.save(dataSource);
    }

    /**
     * 根据ID查询
     */
    public Optional<DataSource> findById(Long id) {
        return dataSourceRepository.findById(id);
    }

    /**
     * 根据编码查询
     */
    public Optional<DataSource> findBySourceCode(String sourceCode) {
        return dataSourceRepository.findBySourceCode(sourceCode);
    }

    /**
     * 查询所有数据源
     */
    public List<DataSource> findAll() {
        return dataSourceRepository.findAll();
    }

    /**
     * 根据类型查询
     */
    public List<DataSource> findBySourceType(String sourceType) {
        return dataSourceRepository.findBySourceType(sourceType);
    }

    /**
     * 查询启用的数据源
     */
    public List<DataSource> findEnabled() {
        return dataSourceRepository.findByEnabled(true);
    }

    /**
     * 根据同步状态查询
     */
    public List<DataSource> findBySyncStatus(Integer syncStatus) {
        return dataSourceRepository.findBySyncStatus(syncStatus);
    }

    /**
     * 删除数据源
     */
    @Transactional
    public void delete(Long id) {
        dataSourceRepository.deleteById(id);
    }

    /**
     * 启用/禁用数据源
     */
    @Transactional
    public void toggleEnabled(Long id, Boolean enabled) {
        Optional<DataSource> dataSourceOpt = dataSourceRepository.findById(id);
        if (dataSourceOpt.isPresent()) {
            DataSource dataSource = dataSourceOpt.get();
            dataSource.setEnabled(enabled);
            dataSourceRepository.save(dataSource);
        }
    }

    /**
     * 测试数据源连接
     */
    public boolean testConnection(DataSource dataSource) {
        // TODO: 实现实际的连接测试逻辑
        // 这里简化处理，返回true
        log.info("测试数据源连接: {}", dataSource.getSourceName());
        return true;
    }

    /**
     * 更新同步状态
     */
    @Transactional
    public void updateSyncStatus(Long id, Integer status, String errorMessage) {
        Optional<DataSource> dataSourceOpt = dataSourceRepository.findById(id);
        if (dataSourceOpt.isPresent()) {
            DataSource dataSource = dataSourceOpt.get();
            dataSource.setSyncStatus(status);
            dataSource.setLastSyncTime(LocalDateTime.now());
            if (errorMessage != null) {
                dataSource.setErrorMessage(errorMessage);
            }
            dataSourceRepository.save(dataSource);
        }
    }

    /**
     * 统计各类型数据源数量
     */
    public long countByType(String sourceType) {
        return dataSourceRepository.findBySourceType(sourceType).size();
    }

    /**
     * 统计启用的数据源数量
     */
    public long countEnabled() {
        return dataSourceRepository.findByEnabled(true).size();
    }
}
