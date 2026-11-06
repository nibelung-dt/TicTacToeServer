package com.tarasov.tic_tac_toe.auth;

import com.tarasov.tic_tac_toe.auth.dto.User;
import com.tarasov.tic_tac_toe.data.UserEntity;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class AuthController {
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final UserRepository userRepository;

    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/auth/login")
    public User login(@RequestBody @Valid User user) {
        log.info("user = {}", user);

        UserEntity entity = new UserEntity();
        // entity.setId(user.id());
        entity.setUsername(user.username());

        UserEntity saved = userRepository.save(entity);

        return new User(saved.getUsername(), saved.getPassword());
    }

    @PostMapping("/auth/register")
    public User register(@RequestBody @Valid User user) {

    }

}
