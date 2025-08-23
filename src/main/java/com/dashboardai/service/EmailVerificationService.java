package com.dashboardai.service;

import com.dashboardai.model.EmailVerificationToken;
import com.dashboardai.model.User;
import com.dashboardai.repository.EmailVerificationTokenRepository;
import com.dashboardai.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class EmailVerificationService {

    @Autowired
    private EmailVerificationTokenRepository tokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Transactional
    public void createVerificationToken(User user) {
        // Eliminar tokens existentes para este usuario
        tokenRepository.deleteByUser(user);

        // Crear nuevo token
        String token = UUID.randomUUID().toString();
        EmailVerificationToken verificationToken = new EmailVerificationToken(token, user);
        tokenRepository.save(verificationToken);

        // Enviar email
        emailService.sendVerificationEmail(user.getEmail(), token);
    }

    @Transactional
    public boolean verifyEmail(String token) {
        Optional<EmailVerificationToken> tokenOpt = tokenRepository.findByToken(token);
        
        if (tokenOpt.isEmpty()) {
            return false;
        }

        EmailVerificationToken verificationToken = tokenOpt.get();
        
        // Verificar si el token ha expirado
        if (verificationToken.isExpired()) {
            return false;
        }

        // Verificar si ya fue usado
        if (verificationToken.getUsed()) {
            return false;
        }

        // Marcar token como usado
        verificationToken.markAsUsed();
        tokenRepository.save(verificationToken);

        // Actualizar usuario como verificado
        User user = verificationToken.getUser();
        user.setEmailVerified(true);
        user.setEmailVerifiedAt(LocalDateTime.now());
        userRepository.save(user);

        // Enviar email de bienvenida
        emailService.sendWelcomeEmail(user.getEmail(), user.getUsername());

        return true;
    }

    @Transactional
    public boolean resendVerificationEmail(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        
        if (userOpt.isEmpty()) {
            return false;
        }

        User user = userOpt.get();
        
        // Verificar si el usuario ya está verificado
        if (user.getEmailVerified()) {
            return false;
        }

        // Verificar si ya existe un token válido (para evitar spam)
        if (tokenRepository.existsValidTokenForUser(user, LocalDateTime.now())) {
            return false;
        }

        // Crear nuevo token de verificación
        createVerificationToken(user);
        return true;
    }

    // Limpiar tokens expirados (método para ejecutar periódicamente)
    @Transactional
    public void cleanupExpiredTokens() {
        tokenRepository.deleteExpiredTokens(LocalDateTime.now());
    }
}
