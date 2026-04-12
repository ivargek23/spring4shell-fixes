package com.example.spring4shell.controller;

import com.example.spring4shell.dto.AuthResponse;
import com.example.spring4shell.dto.LoginRequestDto;
import com.example.spring4shell.dto.RefreshTokenRequest;
import com.example.spring4shell.model.User;
import com.example.spring4shell.service.CustomJwtService;
import com.example.spring4shell.service.RefreshTokenService;
import com.example.spring4shell.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.logging.Logger;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final CustomJwtService customJwtService;
    private final RefreshTokenService refreshTokenService;
    private final UserService userService;
    private final Logger logger = Logger.getLogger(AuthController.class.getName());

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequestDto loginRequestDto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequestDto.getUsername(),
                        loginRequestDto.getPassword()
                )
        );

        Optional<User> user = userService.findByUsername(loginRequestDto.getUsername());
        if (user.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        String accessToken = customJwtService.generateAccessToken(user.get().getUsername());
        String refreshToken = refreshTokenService.generateRefreshToken(user.get()).getToken();
        logger.info("SECURITY: User logged in successfully: " + user.get().getUsername());

        return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken, "Bearer"));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(refreshTokenService.refresh(request.getRefreshToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(Authentication authentication) {
        Optional<User> user = userService.findByUsername(authentication.getName());
        if (user.isEmpty()) {
            throw new RuntimeException("User not found");
        }
        refreshTokenService.revokeAllUserTokens(user.get().getId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/revoke")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> revokeUserTokens(@RequestParam Long userId) {
        refreshTokenService.revokeAllUserTokens(userId);
        return ResponseEntity.noContent().build();
    }
}
