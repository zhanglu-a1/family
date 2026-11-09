package com.family.memoir.module.user.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.family.memoir.auth.JwtProvider;
import com.family.memoir.auth.LoginUser;
import com.family.memoir.common.exception.BizException;
import com.family.memoir.common.exception.ErrorCode;
import com.family.memoir.module.user.dto.LoginRequest;
import com.family.memoir.module.user.dto.RegisterRequest;
import com.family.memoir.module.user.entity.SysUser;
import com.family.memoir.module.user.mapper.SysUserMapper;
import com.family.memoir.module.user.vo.LoginVO;
import com.family.memoir.module.user.vo.TokenVO;
import com.family.memoir.module.user.vo.UserVO;
import io.jsonwebtoken.Claims;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class AuthService {

    private static final int STATUS_ENABLED = 1;

    private final SysUserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public AuthService(SysUserMapper userMapper, PasswordEncoder passwordEncoder, JwtProvider jwtProvider) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
    }

    @Transactional
    public LoginVO register(RegisterRequest request) {
        boolean exists = userMapper.exists(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, request.username()));
        if (exists) {
            throw new BizException(ErrorCode.USERNAME_EXISTS);
        }

        SysUser user = new SysUser();
        user.setUsername(request.username());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setNickname(StringUtils.hasText(request.nickname()) ? request.nickname() : request.username());
        user.setStatus(STATUS_ENABLED);
        userMapper.insert(user);

        return buildLoginVO(user);
    }

    public LoginVO login(LoginRequest request) {
        SysUser user = userMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, request.username()));
        if (user == null || !passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BizException(ErrorCode.INVALID_CREDENTIALS);
        }
        assertEnabled(user);
        return buildLoginVO(user);
    }

    public TokenVO refresh(String refreshToken) {
        Claims claims = jwtProvider.parse(refreshToken);
        if (!jwtProvider.isRefreshToken(claims)) {
            throw new BizException(ErrorCode.TOKEN_INVALID);
        }
        Long userId = Long.valueOf(claims.getSubject());
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new BizException(ErrorCode.USER_NOT_FOUND);
        }
        assertEnabled(user);
        return buildTokenVO(user);
    }

    public UserVO currentUser(LoginUser loginUser) {
        SysUser user = userMapper.selectById(loginUser.id());
        if (user == null) {
            throw new BizException(ErrorCode.USER_NOT_FOUND);
        }
        return UserVO.from(user);
    }

    private void assertEnabled(SysUser user) {
        if (user.getStatus() == null || user.getStatus() != STATUS_ENABLED) {
            throw new BizException(ErrorCode.ACCOUNT_DISABLED);
        }
    }

    private LoginVO buildLoginVO(SysUser user) {
        TokenVO token = buildTokenVO(user);
        return new LoginVO(token.accessToken(), token.refreshToken(), token.expiresInSeconds(), UserVO.from(user));
    }

    private TokenVO buildTokenVO(SysUser user) {
        String accessToken = jwtProvider.createAccessToken(user.getId(), user.getUsername());
        String refreshToken = jwtProvider.createRefreshToken(user.getId(), user.getUsername());
        return new TokenVO(accessToken, refreshToken, jwtProvider.accessTokenExpiresInSeconds());
    }
}
