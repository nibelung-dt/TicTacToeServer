package com.tarasov.tic_tac_toe.lobby;

import com.tarasov.tic_tac_toe.auth.UserRepository;
import com.tarasov.tic_tac_toe.data.UserEntity;
import com.tarasov.tic_tac_toe.lobby.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Кто сейчас в сети.
 *
 * Источник истины — активные WebSocket-сессии, а не поле online в БД:
 * у одного пользователя их может быть несколько (телефон + эмулятор + браузер).
 * Пользователь считается онлайн, пока у него есть хотя бы одна сессия.
 */
@Service
@RequiredArgsConstructor
public class PresenceService {

    private final UserRepository userRepository;

    /** username -> id активных сессий */
    private final Map<String, Set<String>> sessions = new ConcurrentHashMap<>();

    /** username -> данные пользователя, пока он онлайн */
    private final Map<String, UserResponse> online = new ConcurrentHashMap<>();

    /**
     * Регистрирует новую сессию пользователя.
     *
     * @return пользователь, если он только что стал онлайн (это первая его сессия),
     *         иначе null — статус в лобби не изменился
     */
    public synchronized UserResponse connect(String username, String sessionId) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        UserResponse dto = new UserResponse(user.getId(), user.getUsername());
        online.put(username, dto);

        Set<String> userSessions = sessions.computeIfAbsent(
                username, key -> ConcurrentHashMap.newKeySet());
        boolean wasOffline = userSessions.isEmpty();
        userSessions.add(sessionId);

        return wasOffline ? dto : null;
    }

    /**
     * Удаляет сессию пользователя.
     *
     * @return пользователь, если он только что стал офлайн (закрылась последняя сессия),
     *         иначе null
     */
    public synchronized UserResponse disconnect(String username, String sessionId) {
        Set<String> userSessions = sessions.get(username);
        if (userSessions == null) {
            // сессии тут не было: например, CONNECT не прошёл аутентификацию
            // или SessionDisconnectEvent пришёл повторно
            return null;
        }

        userSessions.remove(sessionId);
        if (!userSessions.isEmpty()) {
            return null; // у пользователя ещё есть другие сессии
        }

        sessions.remove(username);
        return online.remove(username);
    }

    /** Текущий снимок «кто онлайн». Отсортирован по userId — ответ детерминированный. */
    public List<UserResponse> onlineUsers() {
        return online.values().stream()
                .sorted(Comparator.comparing(UserResponse::userId))
                .toList();
    }
}
