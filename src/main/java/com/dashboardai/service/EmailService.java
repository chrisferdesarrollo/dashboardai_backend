package com.dashboardai.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.frontend.url:http://localhost:5173}")
    private String frontendUrl;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendVerificationEmail(String toEmail, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("Verifica tu cuenta - DashboardAI");
        
        String verificationUrl = frontendUrl + "/verify-email?token=" + token;
        
        String emailContent = String.format(
            "¡Hola!\n\n" +
            "Gracias por registrarte en DashboardAI.\n\n" +
            "Para completar tu registro, por favor verifica tu dirección de email haciendo clic en el siguiente enlace:\n\n" +
            "%s\n\n" +
            "Este enlace expirará en 24 horas.\n\n" +
            "Si no creaste esta cuenta, puedes ignorar este email.\n\n" +
            "Saludos,\n" +
            "El equipo de DashboardAI",
            verificationUrl
        );
        
        message.setText(emailContent);
        
        try {
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Error al enviar email de verificación: " + e.getMessage());
        }
    }

    public void sendWelcomeEmail(String toEmail, String username) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("¡Bienvenido a DashboardAI!");
        
        String emailContent = String.format(
            "¡Hola %s!\n\n" +
            "¡Tu email ha sido verificado exitosamente!\n\n" +
            "Ahora puedes acceder a tu cuenta en DashboardAI y comenzar a explorar todas las funcionalidades.\n\n" +
            "Accede aquí: %s\n\n" +
            "Saludos,\n" +
            "El equipo de DashboardAI",
            username,
            frontendUrl
        );
        
        message.setText(emailContent);
        
        try {
            mailSender.send(message);
        } catch (Exception e) {
            // No lanzar excepción para email de bienvenida
            System.err.println("Error al enviar email de bienvenida: " + e.getMessage());
        }
    }
}
