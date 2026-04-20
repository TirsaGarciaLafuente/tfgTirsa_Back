package com.breakroom.Models.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistroDto {
    
	private String nombre;
	private String email;
    private String username;
    private String password;

}