package com.breakroom.Models.DTO;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
}