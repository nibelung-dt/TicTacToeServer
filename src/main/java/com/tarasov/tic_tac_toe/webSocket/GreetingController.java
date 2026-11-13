package com.tarasov.tic_tac_toe.webSocket;

import com.tarasov.tic_tac_toe.webSocket.events.Greeting;
import com.tarasov.tic_tac_toe.webSocket.events.HelloMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;

import java.security.Principal;


@Controller
public class GreetingController {

    /**
     * Приветствие сразу после подключения.
     * Клиент подписывается на /app/greeting — и прямо в ответ на подписку
     * получает сообщение только в свою сессию (через брокер не идёт).
     * Principal здесь — пользователь, которого мы выставили из JWT в CONNECT.
     */
    @SubscribeMapping("/greeting")
    public Greeting greeting(Principal principal) {
        return new Greeting("Hello, " + HtmlUtils.htmlEscape(principal.getName()) + "!");
    }

    /**
     * Демо отправки сообщения клиентом: publish на /app/hello,
     * ответ уходит всем подписчикам /topic/greetings.
     */
    @MessageMapping("/hello")
    @SendTo("/topic/greetings")
    public Greeting hello(HelloMessage message) {
        return new Greeting("Hello, " + HtmlUtils.htmlEscape(message.getName()) + "!");
    }
}