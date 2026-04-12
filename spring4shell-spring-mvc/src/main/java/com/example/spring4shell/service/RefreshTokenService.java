package com.example.spring4shell.service;

import com.example.spring4shell.dto.AuthResponse;
import com.example.spring4shell.exception.InvalidRefreshTokenException;
import com.example.spring4shell.exception.TokenReuseException;
import com.example.spring4shell.model.RefreshToken;
import com.example.spring4shell.model.User;
import com.example.spring4shell.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final CustomJwtService customJwtService;
    private final Logger logger = Logger.getLogger(RefreshTokenService.class.getName());

    @Value("${app.jwt.refresh-token-expiration}")
    private Long refreshTokenExpiration;

    public RefreshToken generateRefreshToken(User user) {
        RefreshToken refreshToken = RefreshToken.builder()
                .token(generateSecureToken())
                .expiryDate(Instant.now().plusMillis(refreshTokenExpiration))
                .revoked(false)
                .createdAt(Instant.now())
                .user(user)
                .build();

        return refreshTokenRepository.save(refreshToken);
    }

    private String generateSecureToken() {
        byte[] bytes = new byte[64];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    @Transactional(noRollbackFor = TokenReuseException.class)
    public AuthResponse refresh(String token) {
        RefreshToken existingToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidRefreshTokenException("Invalid refresh token"));

        if (existingToken.isRevoked()) {
            logger.warning("SECURITY: Token reuse detected for user with ID: " + existingToken.getUser().getId());
            refreshTokenRepository.revokeAllByUserId(existingToken.getUser().getId());

            throw new TokenReuseException("Refresh token has been revoked due to token reuse");
        }

        if (existingToken.getExpiryDate().isBefore(Instant.now())) {
            existingToken.setRevoked(true);
            refreshTokenRepository.save(existingToken);
            throw new InvalidRefreshTokenException("Refresh token has expired");
        }

        RefreshToken newRefreshToken = generateRefreshToken(existingToken.getUser());

        existingToken.setRevoked(true);
        existingToken.setReplacedByToken(newRefreshToken.getToken());

        refreshTokenRepository.save(existingToken);
        logger.info("SECURITY: Refresh token rotated for user with ID: " + existingToken.getUser().getId());

        String newAccessToken = customJwtService.generateAccessToken(existingToken.getUser().getUsername());
        logger.info("SECURITY: New access token generated for user with ID: " + existingToken.getUser().getId());

        return new AuthResponse(newAccessToken, newRefreshToken.getToken());
    }

    @Transactional
    public void revokeAllUserTokens(Long userId) {
        logger.info("SECURITY: Revoked all refresh tokens for user with ID: " + userId);
        refreshTokenRepository.revokeAllByUserId(userId);
    }

    @Transactional
    public void revokeSingleUserToken(String token) {
        RefreshToken existingToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidRefreshTokenException("Invalid refresh token"));

        existingToken.setRevoked(true);
        logger.info("SECURITY: Revoked refresh token for user with ID: " + existingToken.getUser().getId());
        refreshTokenRepository.save(existingToken);
    }

    @Transactional
    @Scheduled(cron = "0 0 * * * *")
    public void cleanupExpiredTokens() {
        Long deleted = refreshTokenRepository.deleteByExpiryDateBefore(Instant.now());
        logger.info("SECURITY: Cleaned up " + deleted + " expired refresh tokens");
    }
}
