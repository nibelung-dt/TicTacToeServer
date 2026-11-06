package com.tarasov.tic_tac_toe.auth;

import com.tarasov.tic_tac_toe.auth.dto.AuthResponse;
import com.tarasov.tic_tac_toe.auth.dto.LoginRequest;
import com.tarasov.tic_tac_toe.auth.dto.RegisterRequest;

public interface AuthService {
    /**
     * Регистрирует нового пользователя и сразу возвращает JWT.
     */
    AuthResponse register(RegisterRequest request);

    /**
     * Проверяет учётные данные и возвращает JWT.
     */
    AuthResponse login(LoginRequest request);
}
