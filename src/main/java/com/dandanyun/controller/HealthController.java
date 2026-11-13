package com.dandanyun.controller;

import com.dandanyun.common.Result;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 健康检查。不需要数据库、不需要登录，用来确认框架起来了。
 */
@RestController
public class HealthController {

    private final JdbcTemplate jdbcTemplate;

    public HealthController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/health")
    public Result<Map<String, Object>> health() {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("app", "点单云");
        data.put("status", "UP");
        data.put("java", System.getProperty("java.version"));
        data.put("time", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        return Result.ok(data);
    }

    @GetMapping("/health/ready")
    public ResponseEntity<Result<Map<String, Object>>> ready() {
        Map<String, Object> data = new LinkedHashMap<>();
        try {
            Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            data.put("status", "READY");
            data.put("database", result != null && result == 1 ? "UP" : "UNKNOWN");
            return ResponseEntity.ok(Result.ok(data));
        } catch (DataAccessException e) {
            data.put("status", "NOT_READY");
            data.put("database", "DOWN");
            data.put("message", "数据库连接失败");
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(Result.fail(503, "数据库连接失败"));
        }
    }
}
