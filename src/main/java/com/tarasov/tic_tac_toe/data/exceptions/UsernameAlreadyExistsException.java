package com.tarasov.tic_tac_toe.data.exceptions;

public class UsernameAlreadyExistsException extends RuntimeException {

    public UsernameAlreadyExistsException(String username) {
        super("Пользователь '" + username + "' уже существует");
    }
}