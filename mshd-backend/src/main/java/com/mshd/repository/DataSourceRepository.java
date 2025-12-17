package com.mshd.repository;

import com.mshd.entity.DataSource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 数据源Repository
 */
@Repository
public interface DataSourceRepository extends JpaRepository<DataSource, Long> {

    /**
     * 根据数据源编码查询
     */
    Optional<DataSource> findBySourceCode(String sourceCode);

    /**
     * 根据数据源类型查询
     */
    List<DataSource> findBySourceType(String sourceType);

    /**
     * 查询启用的数据源
     */
    List<DataSource> findByEnabled(Boolean enabled);

    /**
     * 查询同步状态数据源
     */
    List<DataSource> findBySyncStatus(Integer syncStatus);
}
