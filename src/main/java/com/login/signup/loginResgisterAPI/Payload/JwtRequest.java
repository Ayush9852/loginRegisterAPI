package com.login.signup.loginResgisterAPI.Payload;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class JwtRequest {
    @NotEmpty(message = "Username is Required")
    private String username;
    @NotEmpty(message = "Password is required")
    private String password;
}


