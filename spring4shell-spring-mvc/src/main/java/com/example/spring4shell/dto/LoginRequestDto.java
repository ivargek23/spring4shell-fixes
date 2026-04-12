package com.example.spring4shell.dto;

import lombok.Data;

@Data
public class LoginRequestDto {
    private String username;
    private String password;
}
