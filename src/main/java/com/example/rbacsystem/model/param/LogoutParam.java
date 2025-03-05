package com.example.rbacsystem.model.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "登出请求参数")
public class LogoutParam {

    @Schema(description = "会话ID", required = true)
    private String sessionId;
}