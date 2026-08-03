package com.mathshowcase.controller;

import com.mathshowcase.common.R;
import com.mathshowcase.dto.LoginDTO;
import com.mathshowcase.dto.RegisterDTO;
import com.mathshowcase.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public R<?> register(@Valid @RequestBody RegisterDTO dto) {
        try {
            return R.ok(userService.register(dto));
        } catch (RuntimeException e) {
            return R.error(e.getMessage());
        }
    }

    @PostMapping("/login")
    public R<?> login(@Valid @RequestBody LoginDTO dto) {
        try {
            return R.ok(userService.login(dto));
        } catch (RuntimeException e) {
            return R.error(e.getMessage());
        }
    }
}
