package com.example.rbacsystem.model.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "系统配置参数")
public class SystemConfigParam {
    @Schema(description = "配置键", required = true)
    private String configKey;

    @Schema(description = "配置值", required = true)
    private String configValue;

    @Schema(description = "配置描述")
    private String description;
}