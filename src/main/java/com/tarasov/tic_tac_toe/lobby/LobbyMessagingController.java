package com.tarasov.tic_tac_toe.lobby;

import com.tarasov.tic_tac_toe.webSocket.events.LobbyEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

/**
 * STOMP-контроллер лобби.
 *
 * Клиент подписывается на /app/lobby и в ответ на подписку получает снимок
 * списка онлайн-пользователей — только себе, минуя брокер.
 * Дальнейшие изменения приходят в /topic/lobby от PresenceListener.
 */
@Controller
@RequiredArgsConstructor
public class LobbyMessagingController {

    private final PresenceService presenceService;

    @SubscribeMapping("/lobby")
    public LobbyEvent snapshot() {
        return LobbyEvent.snapshot(presenceService.onlineUsers());
    }
}