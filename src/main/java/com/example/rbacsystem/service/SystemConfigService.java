package com.example.rbacsystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.rbacsystem.entity.SystemConfig;

import java.util.List;

public interface SystemConfigService extends IService<SystemConfig> {
    /**
     * 获取所有系统配置
     */
    List<SystemConfig> getAllConfigs();

    /**
     * 根据配置键获取配置值
     */
    String getConfigValue(String configKey);

    /**
     * 更新或创建配置
     */
    void saveOrUpdateConfig(SystemConfig config);

    /**
     * 删除配置
     */
    void deleteConfig(String configKey);

    /**
     * 批量更新配置
     */
    void batchUpdateConfig(List<SystemConfig> configs);

    /**
     * 刷新配置缓存
     */
    void refreshCache();
}