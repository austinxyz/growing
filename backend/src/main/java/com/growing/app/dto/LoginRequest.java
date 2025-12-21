package com.growing.app.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "用户名或邮箱不能为空")
    private String username; // 可以是 username 或 email

    @NotBlank(message = "密码不能为空")
    private String password;
}
