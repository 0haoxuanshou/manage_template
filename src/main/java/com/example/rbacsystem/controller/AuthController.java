package com.example.rbacsystem.controller;

import com.example.rbacsystem.common.Result;
import com.example.rbacsystem.model.param.LoginParam;
import com.example.rbacsystem.model.param.LogoutParam;
import com.example.rbacsystem.security.CustomUserDetails;
import com.example.rbacsystem.util.RedisSessionUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final RedisSessionUtil redisSessionUtil;

    public AuthController(AuthenticationManager authenticationManager, RedisSessionUtil redisSessionUtil) {
        this.authenticationManager = authenticationManager;
        this.redisSessionUtil = redisSessionUtil;
    }

    @PostMapping("/login")
    public Result<String> login(@RequestBody LoginParam loginParam) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginParam.getUsername(), loginParam.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String sessionId = redisSessionUtil.createSession((CustomUserDetails) authentication.getPrincipal());

        return Result.success(sessionId);
    }

    @PostMapping("/logout")
    public Result<Void> logout(@RequestBody LogoutParam logoutParam) {
        redisSessionUtil.removeSession(logoutParam.getSessionId());
        SecurityContextHolder.clearContext();
        return Result.success();
    }
}