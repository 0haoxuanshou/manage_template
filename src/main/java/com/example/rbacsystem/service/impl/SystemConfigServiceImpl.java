package com.example.rbacsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.rbacsystem.entity.SystemConfig;
import com.example.rbacsystem.mapper.SystemConfigMapper;
import com.example.rbacsystem.service.SystemConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class SystemConfigServiceImpl extends ServiceImpl<SystemConfigMapper, SystemConfig> implements SystemConfigService {

    private static final String CACHE_KEY_PREFIX = "system:config:";
    private static final String CACHE_KEY_ALL = CACHE_KEY_PREFIX + "all";
    private static final long CACHE_TIMEOUT = 24;

    private final RedisTemplate<String, Object> redisTemplate;

    public SystemConfigServiceImpl(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public List<SystemConfig> getAllConfigs() {
        // 先从缓存中获取
        List<SystemConfig> configs = (List<SystemConfig>) redisTemplate.opsForValue().get(CACHE_KEY_ALL);
        if (!CollectionUtils.isEmpty(configs)) {
            return configs;
        }

        // 缓存未命中，从数据库查询
        LambdaQueryWrapper<SystemConfig> wrapper = new LambdaQueryWrapper<SystemConfig>()
                .eq(SystemConfig::getIsDeleted, false);
        configs = baseMapper.selectList(wrapper);

        // 放入缓存
        if (!CollectionUtils.isEmpty(configs)) {
            redisTemplate.opsForValue().set(CACHE_KEY_ALL, configs, CACHE_TIMEOUT, TimeUnit.HOURS);
        }

        return configs;
    }

    @Override
    public String getConfigValue(String configKey) {
        String cacheKey = CACHE_KEY_PREFIX + configKey;

        // 先从缓存中获取
        Object value = redisTemplate.opsForValue().get(cacheKey);
        if (value != null) {
            return (String) value;
        }

        // 缓存未命中，从数据库查询
        LambdaQueryWrapper<SystemConfig> wrapper = new LambdaQueryWrapper<SystemConfig>()
                .eq(SystemConfig::getConfigKey, configKey)
                .eq(SystemConfig::getIsDeleted, false);
        SystemConfig config = baseMapper.selectOne(wrapper);

        // 放入缓存
        if (config != null) {
            redisTemplate.opsForValue().set(cacheKey, config.getConfigValue(), CACHE_TIMEOUT, TimeUnit.HOURS);
            return config.getConfigValue();
        }

        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdateConfig(SystemConfig config) {
        // 更新数据库
        LambdaQueryWrapper<SystemConfig> wrapper = new LambdaQueryWrapper<SystemConfig>()
                .eq(SystemConfig::getConfigKey, config.getConfigKey());
        SystemConfig existConfig = baseMapper.selectOne(wrapper);

        if (existConfig != null) {
            config.setId(existConfig.getId());
            baseMapper.updateById(config);
        } else {
            baseMapper.insert(config);
        }

        // 更新缓存
        String cacheKey = CACHE_KEY_PREFIX + config.getConfigKey();
        redisTemplate.opsForValue().set(cacheKey, config.getConfigValue(), CACHE_TIMEOUT, TimeUnit.HOURS);
        redisTemplate.delete(CACHE_KEY_ALL);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteConfig(String configKey) {
        // 逻辑删除数据库记录
        LambdaQueryWrapper<SystemConfig> wrapper = new LambdaQueryWrapper<SystemConfig>()
                .eq(SystemConfig::getConfigKey, configKey);
        SystemConfig config = new SystemConfig();
        config.setIsDeleted(true);
        baseMapper.update(config, wrapper);

        // 删除缓存
        String cacheKey = CACHE_KEY_PREFIX + configKey;
        redisTemplate.delete(cacheKey);
        redisTemplate.delete(CACHE_KEY_ALL);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchUpdateConfig(List<SystemConfig> configs) {
        if (CollectionUtils.isEmpty(configs)) {
            return;
        }

        // 批量更新数据库
        for (SystemConfig config : configs) {
            saveOrUpdateConfig(config);
        }

        // 清除所有配置的缓存
        refreshCache();
    }

    @Override
    public void refreshCache() {
        log.info("开始刷新系统配置缓存");
        // 删除所有配置的缓存
        redisTemplate.delete(CACHE_KEY_ALL);

        // 重新加载所有配置到缓存
        List<SystemConfig> configs = getAllConfigs();
        if (!CollectionUtils.isEmpty(configs)) {
            for (SystemConfig config : configs) {
                String cacheKey = CACHE_KEY_PREFIX + config.getConfigKey();
                redisTemplate.opsForValue().set(cacheKey, config.getConfigValue(), CACHE_TIMEOUT, TimeUnit.HOURS);
            }
        }
        log.info("系统配置缓存刷新完成");
    }
}