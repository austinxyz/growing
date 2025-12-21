package com.growing.app.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GoogleLoginRequest {
    @NotBlank(message = "Google token 不能为空")
    private String credential; // Google ID token
}
