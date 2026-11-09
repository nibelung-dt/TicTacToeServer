package com.tarasov.tic_tac_toe.lobby;

import com.tarasov.tic_tac_toe.lobby.dto.UserItemList;
import com.tarasov.tic_tac_toe.lobby.dto.UserResponse;
import java.util.ArrayList;


public interface UserService {
    UserResponse getUsersList(ArrayList<UserItemList> list);
}