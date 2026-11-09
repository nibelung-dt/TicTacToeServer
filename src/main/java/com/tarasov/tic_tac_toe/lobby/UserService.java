package com.tarasov.tic_tac_toe.lobby;

import com.tarasov.tic_tac_toe.lobby.dto.UserResponse;
import java.util.List;

public interface UserService {
    List<UserResponse> getActiveUsers();
}