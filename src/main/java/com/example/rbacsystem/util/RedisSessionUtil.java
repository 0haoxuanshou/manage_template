package com.example.rbacsystem.util;

import com.example.rbacsystem.security.CustomUserDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class RedisSessionUtil {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String SESSION_PREFIX = "session:";
    private static final long SESSION_TIMEOUT = 8L;

    public RedisSessionUtil(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public String createSession(CustomUserDetails customUserDetails) {
        try {
            String sessionId = UUID.randomUUID().toString();
            String key = SESSION_PREFIX + sessionId;
            redisTemplate.opsForValue().set(key, customUserDetails, SESSION_TIMEOUT, TimeUnit.HOURS);
            log.info("Created new session for user: {}", customUserDetails.getUsername());
            return sessionId;
        } catch (Exception e) {
            log.error("Error creating session for user: {}", customUserDetails.getUsername(), e);
            throw new RuntimeException("Failed to create session", e);
        }
    }

    public Optional<CustomUserDetails> getSession(String sessionId) {
        try {
            if (sessionId == null || sessionId.trim().isEmpty()) {
                log.warn("Attempted to get session with null or empty sessionId");
                return Optional.empty();
            }

            String key = SESSION_PREFIX + sessionId;
            CustomUserDetails value = (CustomUserDetails) redisTemplate.opsForValue().get(key);

            if (value == null) {
                log.debug("No session found for sessionId: {}", sessionId);
                return Optional.empty();
            }

            return Optional.of(value);
        } catch (Exception e) {
            log.error("Error retrieving session: {}", sessionId, e);
            return Optional.empty();
        }
    }

    public void removeSession(String sessionId) {
        try {
            if (sessionId == null || sessionId.trim().isEmpty()) {
                log.warn("Attempted to remove session with null or empty sessionId");
                return;
            }

            String key = SESSION_PREFIX + sessionId;
            Boolean result = redisTemplate.delete(key);

            if (Boolean.TRUE.equals(result)) {
                log.info("Successfully removed session: {}", sessionId);
            } else {
                log.warn("Failed to remove session: {}", sessionId);
            }
        } catch (Exception e) {
            log.error("Error removing session: {}", sessionId, e);
            throw new RuntimeException("Failed to remove session", e);
        }
    }

    public boolean refreshSession(String sessionId) {
        try {
            if (sessionId == null || sessionId.trim().isEmpty()) {
                log.warn("Attempted to refresh session with null or empty sessionId");
                return false;
            }

            String key = SESSION_PREFIX + sessionId;
            Boolean result = redisTemplate.expire(key, SESSION_TIMEOUT, TimeUnit.HOURS);

            if (Boolean.TRUE.equals(result)) {
                log.debug("Successfully refreshed session: {}", sessionId);
                return true;
            } else {
                log.warn("Failed to refresh session: {}", sessionId);
                return false;
            }
        } catch (Exception e) {
            log.error("Error refreshing session: {}", sessionId, e);
            return false;
        }
    }
}