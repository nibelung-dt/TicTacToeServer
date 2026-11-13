package com.tarasov.tic_tac_toe.webSocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;


@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtChannelInterceptor jwtChannelInterceptor;

    public WebSocketConfig(JwtChannelInterceptor jwtChannelInterceptor) {
        this.jwtChannelInterceptor = jwtChannelInterceptor;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Куда сервер (брокер) может отправлять сообщения клиентам
        config.enableSimpleBroker("/topic", "/queue");
        // Префикс для @MessageMapping / @SubscribeMapping: клиент шлёт на /app/...
        config.setApplicationDestinationPrefixes("/app");
        // Префикс персональных сообщений: клиент подписывается на /user/queue/...
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Это URL рукопожатия (handshake) — то, к чему подключается клиент: ws://host/ws
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*"); // для разработки; в проде — конкретные origin'ы
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        // Проверяем JWT в STOMP-фрейме CONNECT
        registration.interceptors(jwtChannelInterceptor);
    }
}
