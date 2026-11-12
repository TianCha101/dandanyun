package com.dandanyun;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 点单云 启动类
 * 最小运行框架：只依赖 spring-boot-starter-web，不连数据库、不连 Redis。
 */
@SpringBootApplication
public class DandanyunApplication {

    public static void main(String[] args) {
        SpringApplication.run(DandanyunApplication.class, args);
        System.out.println(">>> 点单云 已启动：http://localhost:8080/health");
    }
}
