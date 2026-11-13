package com.dandanyun;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * 点单云 启动类
 * 后端最小框架：配置、数据库、鉴权、统一返回、异常处理与测试已就绪。
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class DandanyunApplication {

    public static void main(String[] args) {
        SpringApplication.run(DandanyunApplication.class, args);
        System.out.println(">>> 点单云 已启动，健康检查：/health");
    }
}
