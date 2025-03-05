package com.example.rbacsystem.model.param;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "用户密码重置请求参数")
public class UserPasswordParam {

    @Schema(description = "新密码", required = true)
    private String newPassword;
}