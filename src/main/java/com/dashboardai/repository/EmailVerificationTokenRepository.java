package com.dashboardai.repository;

import com.dashboardai.model.EmailVerificationToken;
import com.dashboardai.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Long> {
    
    Optional<EmailVerificationToken> findByToken(String token);
    
    Optional<EmailVerificationToken> findByUser(User user);
    
    void deleteByUser(User user);
    
    // Eliminar tokens expirados
    @Modifying
    @Query("DELETE FROM EmailVerificationToken t WHERE t.expiresAt < :now")
    void deleteExpiredTokens(LocalDateTime now);
    
    // Verificar si existe un token válido para un usuario
    @Query("SELECT COUNT(t) > 0 FROM EmailVerificationToken t WHERE t.user = :user AND t.used = false AND t.expiresAt > :now")
    boolean existsValidTokenForUser(User user, LocalDateTime now);
}
