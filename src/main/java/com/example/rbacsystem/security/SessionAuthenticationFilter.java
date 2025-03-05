package com.example.rbacsystem.security;

import com.example.rbacsystem.util.RedisSessionUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
public class SessionAuthenticationFilter extends OncePerRequestFilter {

    private final RedisSessionUtil redisSessionUtil;

    public SessionAuthenticationFilter(RedisSessionUtil redisSessionUtil) {
        this.redisSessionUtil = redisSessionUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String sessionId = request.getHeader("Authorization");

        if (sessionId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            Optional<CustomUserDetails> userInfo = redisSessionUtil.getSession(sessionId);

            if (userInfo.isPresent()) {
                CustomUserDetails customUserDetails = userInfo.get();
                redisSessionUtil.refreshSession(sessionId);

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }
}