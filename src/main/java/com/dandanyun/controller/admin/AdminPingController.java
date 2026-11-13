package com.dandanyun.controller.admin;

import com.dandanyun.auth.AuthContext;
import com.dandanyun.auth.AuthenticatedUser;
import com.dandanyun.common.BusinessException;
import com.dandanyun.common.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理端示例接口（管理员端 + 商家端共用这套前缀）。
 * /admin/boom 是故意抛异常的，用来验证全局异常处理器是否生效。
 */
@RestController
@RequestMapping("/admin")
public class AdminPingController {

    @GetMapping("/ping")
    public Result<String> ping() {
        return Result.ok("admin ok");
    }

    @GetMapping("/me")
    public Result<AuthenticatedUser> me() {
        return Result.ok(AuthContext.require());
    }

    @GetMapping("/boom")
    public Result<String> boom() {
        throw new BusinessException(4001, "这是一条业务异常，说明全局异常处理器生效了");
    }
}
