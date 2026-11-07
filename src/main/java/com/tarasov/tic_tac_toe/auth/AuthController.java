package com.tarasov.tic_tac_toe.auth;

import com.tarasov.tic_tac_toe.auth.dto.AuthResponse;
import com.tarasov.tic_tac_toe.auth.dto.RegisterRequest;
import com.tarasov.tic_tac_toe.auth.dto.LoginRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class AuthController {
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/auth/login")
    public AuthResponse login(@RequestBody @Valid LoginRequest request) {
        log.info("LoginRequest = {}", request);

        return authService.login(request);
    }

    @PostMapping("/auth/register")
    public AuthResponse register(@RequestBody @Valid RegisterRequest request) {
        log.info("RegisterRequest = {}", request);

        return authService.register(request);
    }
}