package com.tarasov.tic_tac_toe.webSocket.events;

import com.tarasov.tic_tac_toe.lobby.dto.UserResponse;

import java.util.List;

/**
 * Сообщение лобби.
 *
 * SNAPSHOT     — полный список онлайн-пользователей (ответ на подписку /app/lobby)
 * USER_ONLINE  — один пользователь зашёл; заполнено поле user, users = null
 * USER_OFFLINE — один пользователь вышел;  заполнено поле user, users = null
 */
public record LobbyEvent(Type type, UserResponse user, List<UserResponse> users) {

    public enum Type { SNAPSHOT, USER_ONLINE, USER_OFFLINE }

    public static LobbyEvent snapshot(List<UserResponse> users) {
        return new LobbyEvent(Type.SNAPSHOT, null, List.copyOf(users));
    }

    public static LobbyEvent online(UserResponse user) {
        return new LobbyEvent(Type.USER_ONLINE, user, null);
    }

    public static LobbyEvent offline(UserResponse user) {
        return new LobbyEvent(Type.USER_OFFLINE, user, null);
    }
}
