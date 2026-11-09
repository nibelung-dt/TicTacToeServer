package com.tarasov.tic_tac_toe.lobby;

import com.tarasov.tic_tac_toe.lobby.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class LobbyController {
    private static final Logger log = LoggerFactory.getLogger(LobbyController.class);

    private final UserService userService;

    @GetMapping("/lobby/getActiveUsers")
    public List<UserResponse> getActiveUsers() {
        log.info("getActiveUsers requested");
        return userService.getActiveUsers();
    }
}