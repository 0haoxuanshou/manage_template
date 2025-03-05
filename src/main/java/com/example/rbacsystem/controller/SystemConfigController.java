package com.example.rbacsystem.controller;

import com.example.rbacsystem.common.Result;
import com.example.rbacsystem.model.param.SystemConfigParam;
import com.example.rbacsystem.model.vo.SystemConfigVo;
import com.example.rbacsystem.entity.SystemConfig;
import com.example.rbacsystem.service.SystemConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "系统配置管理", description = "系统配置相关接口")
@RestController
@RequestMapping("/api/system/config")
public class SystemConfigController {

    private final SystemConfigService systemConfigService;

    public SystemConfigController(SystemConfigService systemConfigService) {
        this.systemConfigService = systemConfigService;
    }

    @Operation(summary = "获取所有系统配置")
    @GetMapping
    public Result<List<SystemConfigVo>> getAllConfigs() {
        List<SystemConfig> configs = systemConfigService.getAllConfigs();
        List<SystemConfigVo> vos = configs.stream().map(config -> {
            SystemConfigVo vo = new SystemConfigVo();
            BeanUtils.copyProperties(config, vo);
            return vo;
        }).collect(Collectors.toList());
        return Result.success(vos);
    }

    @Operation(summary = "根据配置键获取配置值")
    @GetMapping("/{configKey}")
    public Result<String> getConfigValue(
            @Parameter(description = "配置键") @PathVariable String configKey) {
        return Result.success(systemConfigService.getConfigValue(configKey));
    }

    @Operation(summary = "保存或更新配置")
    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public Result<Void> saveOrUpdateConfig(@RequestBody SystemConfigParam param) {
        SystemConfig config = new SystemConfig();
        BeanUtils.copyProperties(param, config);
        systemConfigService.saveOrUpdateConfig(config);
        return Result.success();
    }

    @Operation(summary = "删除配置")
    @DeleteMapping("/{configKey}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Result<Void> deleteConfig(
            @Parameter(description = "配置键") @PathVariable String configKey) {
        systemConfigService.deleteConfig(configKey);
        return Result.success();
    }

    @Operation(summary = "批量更新配置")
    @PostMapping("/batch")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Result<Void> batchUpdateConfig(@RequestBody List<SystemConfigParam> params) {
        List<SystemConfig> configs = params.stream().map(param -> {
            SystemConfig config = new SystemConfig();
            BeanUtils.copyProperties(param, config);
            return config;
        }).collect(Collectors.toList());
        systemConfigService.batchUpdateConfig(configs);
        return Result.success();
    }

    @Operation(summary = "刷新配置缓存")
    @PostMapping("/refresh")
    @PreAuthorize("hasAuthority('ADMIN')")
    public Result<Void> refreshCache() {
        systemConfigService.refreshCache();
        return Result.success();
    }
}