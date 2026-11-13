package com.dandanyun.auth;

import com.dandanyun.common.Result;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/admin/login")
    public Result<LoginResponse> adminLogin(@RequestBody LoginRequest request) {
        return Result.ok(authService.login(request, UserRole.ADMIN));
    }

    @PostMapping("/user/login")
    public Result<LoginResponse> userLogin(@RequestBody LoginRequest request) {
        return Result.ok(authService.login(request, UserRole.USER));
    }
}
