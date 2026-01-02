package com.example.doctorhibernate.dto;

public class UserDto {
    private final String username;
    private final String role;

    public UserDto(String username, String role) {
        this.username = username;
        this.role = role;
    }

    public String getUsername() { return username; }
    public String getRole() { return role; }
}
