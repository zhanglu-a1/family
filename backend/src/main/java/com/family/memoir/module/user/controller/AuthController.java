package com.family.memoir.module.user.controller;

import com.family.memoir.auth.LoginUser;
import com.family.memoir.common.result.Result;
import com.family.memoir.module.user.dto.LoginRequest;
import com.family.memoir.module.user.dto.RefreshTokenRequest;
import com.family.memoir.module.user.dto.RegisterRequest;
import com.family.memoir.module.user.service.AuthService;
import com.family.memoir.module.user.vo.LoginVO;
import com.family.memoir.module.user.vo.TokenVO;
import com.family.memoir.module.user.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "认证")
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(summary = "注册并自动登录")
    @PostMapping("/register")
    public Result<LoginVO> register(@Valid @RequestBody RegisterRequest request) {
        return Result.ok(authService.register(request));
    }

    @Operation(summary = "登录")
    @PostMapping("/login")
    public Result<LoginVO> login(@Valid @RequestBody LoginRequest request) {
        return Result.ok(authService.login(request));
    }

    @Operation(summary = "刷新访问令牌")
    @PostMapping("/refresh")
    public Result<TokenVO> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        return Result.ok(authService.refresh(request.refreshToken()));
    }

    @Operation(summary = "当前用户信息")
    @GetMapping("/me")
    public Result<UserVO> me(@AuthenticationPrincipal LoginUser loginUser) {
        return Result.ok(authService.currentUser(loginUser));
    }
}
