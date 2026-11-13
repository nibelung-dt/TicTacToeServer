package com.tarasov.tic_tac_toe.webSocket;

import com.tarasov.tic_tac_toe.lobby.PresenceService;
import com.tarasov.tic_tac_toe.lobby.dto.UserResponse;
import com.tarasov.tic_tac_toe.webSocket.events.LobbyEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;

/**
 * Связывает lifecycle WebSocket-сессий с состоянием лобби.
 *
 * ВАЖНО: SessionConnectedEvent и SessionDisconnectEvent — это события Spring,
 * а не наш DTO. Не путать с com.tarasov.tic_tac_toe.webSocket.events.*
 */
@Component
@RequiredArgsConstructor
public class PresenceListener {

    private final PresenceService presenceService;
    private final SimpMessagingTemplate messagingTemplate;

    @EventListener
    public void onConnected(SessionConnectedEvent event) {
        Principal principal = event.getUser();
        if (principal == null) {
            return; // CONNECT не аутентифицирован (наш interceptor не пропустил)
        }

        // У SessionConnectedEvent НЕТ метода getSessionId(): id есть только
        // в заголовках фрейма, поэтому достаём его через StompHeaderAccessor.
        String sessionId = StompHeaderAccessor.wrap(event.getMessage()).getSessionId();
        if (sessionId == null) {
            return; // теоретически невозможно: заголовки проставляет StompSubProtocolHandler
        }

        UserResponse user = presenceService.connect(principal.getName(), sessionId);
        if (user != null) {
            // статус изменился только если это была первая сессия пользователя
            messagingTemplate.convertAndSend("/topic/lobby", LobbyEvent.online(user));
        }
    }

    @EventListener
    public void onDisconnected(SessionDisconnectEvent event) {
        Principal principal = event.getUser();
        if (principal == null) {
            return; // сессия не успела аутентифицироваться — в PresenceService её и нет
        }

        // Здесь, в отличие от SessionConnectedEvent, id сессии есть прямо у события.
        UserResponse user = presenceService.disconnect(principal.getName(), event.getSessionId());
        if (user != null) {
            // статус изменился только если закрылась последняя сессия
            messagingTemplate.convertAndSend("/topic/lobby", LobbyEvent.offline(user));
        }
    }
}
