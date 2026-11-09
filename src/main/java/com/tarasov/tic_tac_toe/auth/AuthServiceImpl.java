package com.tarasov.tic_tac_toe.auth;

import com.tarasov.tic_tac_toe.auth.dto.AuthResponse;
import com.tarasov.tic_tac_toe.auth.dto.LoginRequest;
import com.tarasov.tic_tac_toe.auth.dto.RegisterRequest;
import com.tarasov.tic_tac_toe.data.UserEntity;
import com.tarasov.tic_tac_toe.data.exceptions.InvalidCredentialsException;
import com.tarasov.tic_tac_toe.data.exceptions.UsernameAlreadyExistsException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           JwtService jwtService,
                           AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new UsernameAlreadyExistsException(request.username());
        }

        UserEntity user = new UserEntity();
        user.setUsername(request.username());
        user.setPassword(passwordEncoder.encode(request.password()));

        userRepository.save(user);
        String token = jwtService.generateToken(user.getUsername());
        return new AuthResponse(token);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.username(), request.password())
            );
        } catch (AuthenticationException e) {
            throw new InvalidCredentialsException();
        }

        UserEntity user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new UsernameNotFoundException(request.username()));

        String token = jwtService.generateToken(user.getUsername());
        return new AuthResponse(token);
    }
}