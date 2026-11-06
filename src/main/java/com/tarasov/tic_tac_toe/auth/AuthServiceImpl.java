package com.tarasov.tic_tac_toe.auth;

import com.tarasov.tic_tac_toe.auth.dto.AuthResponse;
import com.tarasov.tic_tac_toe.auth.dto.LoginRequest;
import com.tarasov.tic_tac_toe.auth.dto.RegisterRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;

public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;      // интерфейс, не JPA-реализация
    private final PasswordEncoder passwordEncoder;    // бин BCryptPasswordEncoder
    private final JwtService jwtService;              // отдельный сервис для работы с токенами
    private final AuthenticationManager authenticationManager; // для проверки логина/пароля

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new UsernameAlreadyExistsException(request.username());
        }

        User user = User.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .role("ROLE_USER")
                .build();

        userRepository.save(user);
        String token = jwtService.generateToken(user.getUsername());
        return new AuthResponse(token);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        return null;
    }
}