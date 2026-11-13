package com.tarasov.tic_tac_toe.lobby.dto;

/**
 * Пользователь в лобби.
 *
 * userId — стабильный ключ: PK из БД, никогда не меняется.
 *          Именно по нему Android-клиент делает выбор противника.
 * username — отображаемое имя (в теории может быть переименовано).
 */
public record UserResponse(Long userId, String username) { }
