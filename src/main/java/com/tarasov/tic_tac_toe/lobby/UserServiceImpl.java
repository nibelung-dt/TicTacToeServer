package com.tarasov.tic_tac_toe.lobby;

import com.tarasov.tic_tac_toe.auth.UserRepository;
import com.tarasov.tic_tac_toe.lobby.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PresenceService presenceService;

    /**
     * Все зарегистрированные пользователи, независимо от того, в сети они или нет.
     * Сортировка по userId — чтобы ответ был детерминированным (findAll() порядок не гарантирует).
     */
    @Override
    public List<UserResponse> getUsers() {
        return userRepository.findAll().stream()
                .map(user -> new UserResponse(user.getId(), user.getUsername()))
                .sorted(Comparator.comparing(UserResponse::userId))
                .toList();
    }

    /**
     * Только пользователи с активной WebSocket-сессией.
     * Источник истины — PresenceService, а не поле online в БД.
     */
    @Override
    public List<UserResponse> getActiveUsers() {
        return presenceService.onlineUsers();
    }
}
