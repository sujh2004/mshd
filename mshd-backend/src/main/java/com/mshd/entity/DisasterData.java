package com.mshd.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 灾情数据实体
 */
@Data
@Entity
@Table(name = "disaster_data")
@EntityListeners(AuditingEntityListener.class)
public class DisasterData {

    /**
     * 主键ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 一体化编码ID (36位)
     * 格式: 震情码(26位) + 来源码(3位) + 载体码(1位) + 灾情码(6位)
     */
    @Column(name = "encoded_id", nullable = false, unique = true, length = 36)
    private String encodedId;

    /**
     * 震情码 (26位)
     * 格式: 地理信息码(12位) + 时间码(14位)
     */
    @Column(name = "earthquake_code", length = 26)
    private String earthquakeCode;

    /**
     * 地理信息码 (12位)
     */
    @Column(name = "geographic_code", length = 12)
    private String geographicCode;

    /**
     * 地理位置描述
     */
    @Column(name = "location", length = 200)
    private String location;

    /**
     * 经度
     */
    @Column(name = "longitude")
    private Double longitude;

    /**
     * 纬度
     */
    @Column(name = "latitude")
    private Double latitude;

    /**
     * 时间码 (14位)
     * 格式: YYYYMMDDHHmmss
     */
    @Column(name = "time_code", length = 14)
    private String timeCode;

    /**
     * 地震发生时间
     */
    @Column(name = "earthquake_time")
    private LocalDateTime earthquakeTime;

    /**
     * 来源码 (3位)
     * 1XX: 业务报送数据
     * 2XX: 泛在感知数据
     * 3XX: 其他数据
     */
    @Column(name = "source_code", length = 3)
    private String sourceCode;

    /**
     * 数据来源类型
     */
    @Column(name = "source_type", length = 50)
    private String sourceType;

    /**
     * 载体码 (1位)
     * 0: 文字, 1: 图像, 2: 音频, 3: 视频, 4: 其他
     */
    @Column(name = "carrier_code", length = 1)
    private String carrierCode;

    /**
     * 载体类型
     */
    @Column(name = "carrier_type", length = 20)
    private String carrierType;

    /**
     * 灾情码 (6位)
     * 格式: 灾害大类(1位) + 灾害子类(2位) + 灾情指标(3位)
     */
    @Column(name = "disaster_code", length = 6)
    private String disasterCode;

    /**
     * 灾害大类代码
     * 1: 震情, 2: 人员伤亡及失踪, 3: 房屋破坏, 4: 生命线工程灾情, 5: 次生灾害
     */
    @Column(name = "disaster_category", length = 1)
    private String disasterCategory;

    /**
     * 灾害子类代码
     */
    @Column(name = "disaster_subcategory", length = 2)
    private String disasterSubcategory;

    /**
     * 灾情指标代码
     */
    @Column(name = "indicator_code", length = 3)
    private String indicatorCode;

    /**
     * 灾情数据内容（文本数据）
     */
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    /**
     * 多媒体文件路径
     */
    @Column(name = "file_path", length = 500)
    private String filePath;

    /**
     * 文件大小（字节）
     */
    @Column(name = "file_size")
    private Long fileSize;

    /**
     * 数据有效期（小时）
     */
    @Column(name = "ttl")
    private Integer ttl;

    /**
     * 数据状态
     * 0: 待处理, 1: 已编码, 2: 已存储, 3: 已过期, 4: 已删除
     */
    @Column(name = "status")
    private Integer status;

    /**
     * 创建时间
     */
    @CreatedDate
    @Column(name = "create_time", updatable = false)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @LastModifiedDate
    @Column(name = "update_time")
    private LocalDateTime updateTime;

    /**
     * 备注
     */
    @Column(name = "remark", length = 500)
    private String remark;
}
