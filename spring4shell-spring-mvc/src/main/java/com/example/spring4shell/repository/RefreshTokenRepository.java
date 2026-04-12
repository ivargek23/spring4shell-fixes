package com.example.spring4shell.repository;

import com.example.spring4shell.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    Optional<RefreshToken> findByTokenAndRevokedFalse(String token);
    Long deleteByExpiryDateBefore(Instant now);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
        update RefreshToken rt
                set rt.revoked=true
                        where rt.user.id = :userId
        """)
    int revokeAllByUserId(@Param("userId") Long userId);
}
