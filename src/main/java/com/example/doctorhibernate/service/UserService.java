package com.example.doctorhibernate.service;

import com.example.doctorhibernate.dto.UserDto;

public interface UserService {

    void seedAdminUser();
    UserDto login(String username, String plainPassword) throws Exception;

}
