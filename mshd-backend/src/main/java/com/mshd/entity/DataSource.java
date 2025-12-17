package com.mshd.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 数据源配置实体
 */
@Data
@Entity
@Table(name = "data_source")
@EntityListeners(AuditingEntityListener.class)
public class DataSource {

    /**
     * 主键ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 数据源名称
     */
    @Column(name = "source_name", nullable = false, length = 100)
    private String sourceName;

    /**
     * 数据源编码
     */
    @Column(name = "source_code", nullable = false, length = 3, unique = true)
    private String sourceCode;

    /**
     * 数据源类型
     * business: 业务报送数据
     * ubiquitous: 泛在感知数据
     * public_opinion: 舆情感知数据
     * infrastructure: 承载体基础数据
     * other: 其他
     */
    @Column(name = "source_type", nullable = false, length = 50)
    private String sourceType;

    /**
     * 数据源URL
     */
    @Column(name = "source_url", length = 500)
    private String sourceUrl;

    /**
     * 接口协议
     * HTTP, HTTPS, MQTT, WebSocket, FTP
     */
    @Column(name = "protocol", length = 20)
    private String protocol;

    /**
     * 认证方式
     * none, basic, token, oauth2
     */
    @Column(name = "auth_type", length = 20)
    private String authType;

    /**
     * 认证凭证（加密存储）
     */
    @Column(name = "credentials", length = 500)
    private String credentials;

    /**
     * 数据格式
     * JSON, XML, CSV, Binary
     */
    @Column(name = "data_format", length = 20)
    private String dataFormat;

    /**
     * 是否启用
     */
    @Column(name = "enabled")
    private Boolean enabled = true;

    /**
     * 轮询间隔（秒）
     */
    @Column(name = "poll_interval")
    private Integer pollInterval;

    /**
     * 最后同步时间
     */
    @Column(name = "last_sync_time")
    private LocalDateTime lastSyncTime;

    /**
     * 同步状态
     * 0: 未同步, 1: 同步中, 2: 同步成功, 3: 同步失败
     */
    @Column(name = "sync_status")
    private Integer syncStatus;

    /**
     * 错误信息
     */
    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

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
