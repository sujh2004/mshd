package com.mshd.controller;

import com.mshd.common.Result;
import com.mshd.entity.DisasterData;
import com.mshd.service.DisasterDataService;
import com.mshd.util.EncodingUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 灾情数据控制器
 */
@Slf4j
@RestController
@RequestMapping("/disaster")
@RequiredArgsConstructor
@CrossOrigin
public class DisasterDataController {

    private final DisasterDataService disasterDataService;

    /**
     * 创建灾情数据
     */
    @PostMapping("/create")
    public Result<DisasterData> create(@RequestBody DisasterData disasterData) {
        try {
            DisasterData saved = disasterDataService.save(disasterData);
            return Result.success("创建成功", saved);
        } catch (Exception e) {
            log.error("创建灾情数据失败", e);
            return Result.error("创建失败: " + e.getMessage());
        }
    }

    /**
     * 根据ID查询
     */
    @GetMapping("/{id}")
    public Result<DisasterData> getById(@PathVariable Long id) {
        return disasterDataService.findById(id)
            .map(data -> Result.success(data))
            .orElse(Result.error("数据不存在"));
    }

    /**
     * 根据编码ID查询
     */
    @GetMapping("/code/{encodedId}")
    public Result<DisasterData> getByEncodedId(@PathVariable String encodedId) {
        return disasterDataService.findByEncodedId(encodedId)
            .map(data -> Result.success(data))
            .orElse(Result.error("数据不存在"));
    }

    /**
     * 查询所有
     */
    @GetMapping("/list")
    public Result<List<DisasterData>> list() {
        List<DisasterData> list = disasterDataService.findAll();
        return Result.success(list);
    }

    /**
     * 根据来源查询
     */
    @GetMapping("/source/{sourceCode}")
    public Result<List<DisasterData>> listBySource(@PathVariable String sourceCode) {
        List<DisasterData> list = disasterDataService.findBySourceCode(sourceCode);
        return Result.success(list);
    }

    /**
     * 根据载体类型查询
     */
    @GetMapping("/carrier/{carrierType}")
    public Result<List<DisasterData>> listByCarrier(@PathVariable String carrierType) {
        List<DisasterData> list = disasterDataService.findByCarrierType(carrierType);
        return Result.success(list);
    }

    /**
     * 根据灾害类别查询
     */
    @GetMapping("/category/{category}")
    public Result<List<DisasterData>> listByCategory(@PathVariable String category) {
        List<DisasterData> list = disasterDataService.findByDisasterCategory(category);
        return Result.success(list);
    }

    /**
     * 根据时间范围查询
     */
    @GetMapping("/time-range")
    public Result<List<DisasterData>> listByTimeRange(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        List<DisasterData> list = disasterDataService.findByTimeRange(startTime, endTime);
        return Result.success(list);
    }

    /**
     * 根据地理位置范围查询
     */
    @GetMapping("/location-range")
    public Result<List<DisasterData>> listByLocationRange(
            @RequestParam Double minLng,
            @RequestParam Double maxLng,
            @RequestParam Double minLat,
            @RequestParam Double maxLat) {
        List<DisasterData> list = disasterDataService.findByLocationRange(
            minLng, maxLng, minLat, maxLat
        );
        return Result.success(list);
    }

    /**
     * 统计各类型数据数量
     */
    @GetMapping("/stats/category")
    public Result<Map<String, Long>> countByCategory() {
        List<Object[]> stats = disasterDataService.countByCategory();
        Map<String, Long> result = new HashMap<>();
        for (Object[] stat : stats) {
            result.put((String) stat[0], (Long) stat[1]);
        }
        return Result.success(result);
    }

    /**
     * 统计各来源数据数量
     */
    @GetMapping("/stats/source")
    public Result<Map<String, Long>> countBySource() {
        List<Object[]> stats = disasterDataService.countBySource();
        Map<String, Long> result = new HashMap<>();
        for (Object[] stat : stats) {
            result.put((String) stat[0], (Long) stat[1]);
        }
        return Result.success(result);
    }

    /**
     * 解析编码ID
     */
    @GetMapping("/parse/{encodedId}")
    public Result<EncodingUtil.EncodingResult> parseEncodedId(@PathVariable String encodedId) {
        try {
            EncodingUtil.EncodingResult result = disasterDataService.parseEncodedId(encodedId);
            return Result.success(result);
        } catch (Exception e) {
            log.error("解析编码ID失败", e);
            return Result.error("解析失败: " + e.getMessage());
        }
    }

    /**
     * 删除过期数据
     */
    @DeleteMapping("/cleanup")
    public Result<Integer> cleanup(@RequestParam(defaultValue = "72") int hours) {
        int count = disasterDataService.deleteExpiredData(hours);
        return Result.success("清理完成", count);
    }

    /**
     * 删除
     */
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        try {
            disasterDataService.delete(id);
            return Result.success("删除成功");
        } catch (Exception e) {
            log.error("删除灾情数据失败", e);
            return Result.error("删除失败: " + e.getMessage());
        }
    }
}
