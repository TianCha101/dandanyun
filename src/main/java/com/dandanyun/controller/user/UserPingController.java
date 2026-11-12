package com.dandanyun.controller.user;

import com.dandanyun.common.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户端示例接口（顾客端 H5 调这套前缀）。
 */
@RestController
@RequestMapping("/user")
public class UserPingController {

    @GetMapping("/ping")
    public Result<String> ping() {
        return Result.ok("user ok");
    }
}
