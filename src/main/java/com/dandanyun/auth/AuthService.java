package com.dandanyun.auth;

import com.dandanyun.common.AuthenticationException;
import com.dandanyun.common.BusinessException;
import com.dandanyun.system.SystemUser;
import com.dandanyun.system.SystemUserMapper;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final SystemUserMapper userMapper;
    private final PasswordService passwordService;
    private final JwtTokenService tokenService;

    public AuthService(
            SystemUserMapper userMapper,
            PasswordService passwordService,
            JwtTokenService tokenService
    ) {
        this.userMapper = userMapper;
        this.passwordService = passwordService;
        this.tokenService = tokenService;
    }

    public LoginResponse login(LoginRequest request, UserRole expectedRole) {
        validate(request);
        String username = request.username().trim();
        SystemUser user = userMapper.findByUsername(username);
        if (user == null || !passwordService.matches(request.password(), user.getPasswordHash())) {
            throw new AuthenticationException("用户名或密码错误");
        }
        if (user.getStatus() == null || user.getStatus() != 1) {
            throw new AuthenticationException("账号已停用");
        }
        if (user.getRole() != expectedRole) {
            throw new AuthenticationException("账号类型与登录入口不匹配");
        }

        AuthenticatedUser principal = new AuthenticatedUser(
                user.getId(),
                user.getUsername(),
                user.getDisplayName(),
                user.getRole()
        );
        JwtTokenService.IssuedToken token = tokenService.issue(principal);
        return new LoginResponse(
                token.value(),
                "Bearer",
                token.expiresInSeconds(),
                new LoginResponse.LoginUser(
                        user.getId(),
                        user.getUsername(),
                        user.getDisplayName(),
                        user.getRole()
                )
        );
    }

    private void validate(LoginRequest request) {
        if (request == null || request.username() == null || request.username().isBlank()) {
            throw new BusinessException(400, "用户名不能为空");
        }
        if (request.username().length() > 64) {
            throw new BusinessException(400, "用户名过长");
        }
        if (request.password() == null || request.password().isBlank()) {
            throw new BusinessException(400, "密码不能为空");
        }
        if (request.password().length() > 128) {
            throw new BusinessException(400, "密码过长");
        }
    }
}
