package com.login.signup.loginResgisterAPI.Payload;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class ResetPasswordRequest {
    @NotEmpty(message = "Username is required")
    private String username;
    @NotEmpty(message = "New Password is required")
    private String newPassword;
}

