package com.tarasov.tic_tac_toe.lobby;

import com.tarasov.tic_tac_toe.auth.AuthService;
import com.tarasov.tic_tac_toe.auth.dto.AuthResponse;
import com.tarasov.tic_tac_toe.auth.dto.LoginRequest;
import com.tarasov.tic_tac_toe.lobby.dto.UserItemList;
import com.tarasov.tic_tac_toe.lobby.dto.UserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.ArrayList;


@RestController
@RequiredArgsConstructor
public class LobbyController {
    private static final Logger log = LoggerFactory.getLogger(LobbyController.class);

    private final UserService userService;

    public LobbyController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/lobby/getAllUsers")
    public UserResponse login(@RequestBody @Valid ArrayList<UserItemList> list) {
        log.info("LoginRequest = {}", list);

        return userService.getUsersList(list);
    }
}