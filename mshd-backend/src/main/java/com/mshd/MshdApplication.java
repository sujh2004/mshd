package com.mshd;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * MSHD多源异构灾情数据管理服务系统 - 主应用类
 *
 * @author MSHD Team
 * @version 2.0
 */
@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
public class MshdApplication {

    public static void main(String[] args) {
        SpringApplication.run(MshdApplication.class, args);
        System.out.println("==============================================");
        System.out.println("MSHD 多源异构灾情数据管理服务系统已启动");
        System.out.println("访问地址: http://localhost:8080/api");
        System.out.println("==============================================");
    }
}
