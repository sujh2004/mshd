package com.mshd.repository;

import com.mshd.entity.DisasterData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 灾情数据Repository
 */
@Repository
public interface DisasterDataRepository extends JpaRepository<DisasterData, Long> {

    /**
     * 根据编码ID查询
     */
    Optional<DisasterData> findByEncodedId(String encodedId);

    /**
     * 根据来源码查询
     */
    List<DisasterData> findBySourceCode(String sourceCode);

    /**
     * 根据载体类型查询
     */
    List<DisasterData> findByCarrierType(String carrierType);

    /**
     * 根据灾害类别查询
     */
    List<DisasterData> findByDisasterCategory(String disasterCategory);

    /**
     * 根据状态查询
     */
    List<DisasterData> findByStatus(Integer status);

    /**
     * 根据时间范围查询
     */
    @Query("SELECT d FROM DisasterData d WHERE d.earthquakeTime BETWEEN :startTime AND :endTime")
    List<DisasterData> findByTimeRange(@Param("startTime") LocalDateTime startTime,
                                       @Param("endTime") LocalDateTime endTime);

    /**
     * 根据地理位置范围查询
     */
    @Query("SELECT d FROM DisasterData d WHERE d.longitude BETWEEN :minLng AND :maxLng " +
           "AND d.latitude BETWEEN :minLat AND :maxLat")
    List<DisasterData> findByLocationRange(@Param("minLng") Double minLng,
                                           @Param("maxLng") Double maxLng,
                                           @Param("minLat") Double minLat,
                                           @Param("maxLat") Double maxLat);

    /**
     * 查询过期数据
     */
    @Query("SELECT d FROM DisasterData d WHERE d.createTime < :expiryTime AND d.status != 3")
    List<DisasterData> findExpiredData(@Param("expiryTime") LocalDateTime expiryTime);

    /**
     * 统计各类型数据数量
     */
    @Query("SELECT d.disasterCategory, COUNT(d) FROM DisasterData d GROUP BY d.disasterCategory")
    List<Object[]> countByCategory();

    /**
     * 统计各来源数据数量
     */
    @Query("SELECT d.sourceCode, COUNT(d) FROM DisasterData d GROUP BY d.sourceCode")
    List<Object[]> countBySource();
}
