package com.tarasov.tic_tac_toe.webSocket;

import com.tarasov.tic_tac_toe.auth.JwtService;
import org.springframework.http.HttpHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

/**
 * Аутентификация WebSocket-сессии тем же JWT, что и REST.
 * Токен передаётся в заголовке CONNECT-фрейма:
 *   Authorization: Bearer <token>
 * Проверяются только CONNECT-фреймы, остальные пропускаются как есть.
 */
@Component
public class JwtChannelInterceptor implements ChannelInterceptor {

    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtChannelInterceptor(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            String authHeader = accessor.getFirstNativeHeader(HttpHeaders.AUTHORIZATION);

            if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
                throw new IllegalArgumentException("Missing Authorization header in STOMP CONNECT");
            }

            // Если токен невалидный/просроченный — здесь будет исключение,
            // клиент получит ERROR-фрейм, а сессия закроется.
            String username = jwtService.extractUsername(authHeader.substring(BEARER_PREFIX.length()));
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());

            // Именно это делает пользователя "известным" для @SubscribeMapping,
            // @SendToUser и convertAndSendToUser
            accessor.setUser(authentication);
        }

        return message;
    }
}
