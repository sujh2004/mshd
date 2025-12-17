package com.mshd.service;

import com.mshd.entity.DisasterData;
import com.mshd.repository.DisasterDataRepository;
import com.mshd.util.EncodingUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 灾情数据服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DisasterDataService {

    private final DisasterDataRepository disasterDataRepository;

    /**
     * 保存灾情数据
     */
    @Transactional
    public DisasterData save(DisasterData disasterData) {
        // 如果没有编码ID，则生成
        if (disasterData.getEncodedId() == null) {
            String encodedId = EncodingUtil.generateEncodedId(
                disasterData.getGeographicCode(),
                disasterData.getEarthquakeTime(),
                disasterData.getSourceCode(),
                disasterData.getCarrierCode(),
                disasterData.getDisasterCode()
            );
            disasterData.setEncodedId(encodedId);
        }

        // 生成震情码
        String earthquakeCode = EncodingUtil.generateEarthquakeCode(
            disasterData.getGeographicCode(),
            disasterData.getEarthquakeTime()
        );
        disasterData.setEarthquakeCode(earthquakeCode);

        // 生成时间码
        String timeCode = EncodingUtil.generateTimeCode(disasterData.getEarthquakeTime());
        disasterData.setTimeCode(timeCode);

        // 设置状态为已编码
        if (disasterData.getStatus() == null) {
            disasterData.setStatus(1);
        }

        log.info("保存灾情数据，编码ID: {}", disasterData.getEncodedId());
        return disasterDataRepository.save(disasterData);
    }

    /**
     * 根据ID查询
     */
    public Optional<DisasterData> findById(Long id) {
        return disasterDataRepository.findById(id);
    }

    /**
     * 根据编码ID查询
     */
    public Optional<DisasterData> findByEncodedId(String encodedId) {
        return disasterDataRepository.findByEncodedId(encodedId);
    }

    /**
     * 查询所有
     */
    public List<DisasterData> findAll() {
        return disasterDataRepository.findAll();
    }

    /**
     * 根据来源查询
     */
    public List<DisasterData> findBySourceCode(String sourceCode) {
        return disasterDataRepository.findBySourceCode(sourceCode);
    }

    /**
     * 根据载体类型查询
     */
    public List<DisasterData> findByCarrierType(String carrierType) {
        return disasterDataRepository.findByCarrierType(carrierType);
    }

    /**
     * 根据灾害类别查询
     */
    public List<DisasterData> findByDisasterCategory(String disasterCategory) {
        return disasterDataRepository.findByDisasterCategory(disasterCategory);
    }

    /**
     * 根据时间范围查询
     */
    public List<DisasterData> findByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        return disasterDataRepository.findByTimeRange(startTime, endTime);
    }

    /**
     * 根据地理位置范围查询
     */
    public List<DisasterData> findByLocationRange(Double minLng, Double maxLng,
                                                   Double minLat, Double maxLat) {
        return disasterDataRepository.findByLocationRange(minLng, maxLng, minLat, maxLat);
    }

    /**
     * 统计各类型数据数量
     */
    public List<Object[]> countByCategory() {
        return disasterDataRepository.countByCategory();
    }

    /**
     * 统计各来源数据数量
     */
    public List<Object[]> countBySource() {
        return disasterDataRepository.countBySource();
    }

    /**
     * 删除过期数据
     */
    @Transactional
    public int deleteExpiredData(int hours) {
        LocalDateTime expiryTime = LocalDateTime.now().minusHours(hours);
        List<DisasterData> expiredData = disasterDataRepository.findExpiredData(expiryTime);

        for (DisasterData data : expiredData) {
            data.setStatus(3); // 标记为已过期
        }

        disasterDataRepository.saveAll(expiredData);
        log.info("标记过期数据数量: {}", expiredData.size());

        return expiredData.size();
    }

    /**
     * 删除
     */
    @Transactional
    public void delete(Long id) {
        disasterDataRepository.deleteById(id);
        log.info("删除灾情数据，ID: {}", id);
    }

    /**
     * 解析编码ID
     */
    public EncodingUtil.EncodingResult parseEncodedId(String encodedId) {
        return EncodingUtil.parseEncodedId(encodedId);
    }
}
