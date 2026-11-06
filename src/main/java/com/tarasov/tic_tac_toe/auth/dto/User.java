package com.tarasov.tic_tac_toe.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record User(
        @NotBlank(message = "Имя не может быть пустым")
        String username,

        @NotBlank(message = "Пароль не может быть пустым")
        String password
) { }