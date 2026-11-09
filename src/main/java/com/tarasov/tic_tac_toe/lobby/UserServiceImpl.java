package com.tarasov.tic_tac_toe.lobby;

import com.tarasov.tic_tac_toe.auth.UserRepository;
import com.tarasov.tic_tac_toe.data.UserEntity;
import com.tarasov.tic_tac_toe.lobby.dto.UserResponse;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<UserResponse> getActiveUsers() {
        return userRepository.findAll().stream()
                .map(UserEntity::getUsername)
                .map(UserResponse::new)
                .toList();
    }
}