package com.tarasov.tic_tac_toe.lobby;

import com.tarasov.tic_tac_toe.lobby.dto.UserResponse;
import java.util.List;

public interface UserService {

    /** Все зарегистрированные пользователи (из БД). */
    List<UserResponse> getUsers();

    /** Только пользователи с активной WebSocket-сессией. */
    List<UserResponse> getActiveUsers();
}
