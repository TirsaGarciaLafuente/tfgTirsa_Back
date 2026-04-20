package com.breakroom.Models.DTO;

import lombok.Data;

@Data
public class CambiarPasswordDto {
    private String email;
    private String username;
    private String password;

}