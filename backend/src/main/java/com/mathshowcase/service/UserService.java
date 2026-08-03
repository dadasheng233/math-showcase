package com.mathshowcase.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mathshowcase.entity.User;
import com.mathshowcase.dto.LoginDTO;
import com.mathshowcase.dto.RegisterDTO;

import java.util.Map;

public interface UserService extends IService<User> {
    Map<String, Object> register(RegisterDTO dto);
    Map<String, Object> login(LoginDTO dto);
    User getByUsername(String username);
}
