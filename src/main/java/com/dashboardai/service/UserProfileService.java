package com.dashboardai.service;

import com.dashboardai.dto.request.UpdateEmailRequest;
import com.dashboardai.dto.request.UpdatePasswordRequest;
import com.dashboardai.dto.request.UpdateAvatarRequest;
import com.dashboardai.dto.response.UserProfileResponse;
import com.dashboardai.exception.InvalidPasswordException;
import com.dashboardai.exception.UserProfileException;
import com.dashboardai.model.User;
import com.dashboardai.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@Transactional
public class UserProfileService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final String UPLOAD_DIR = "uploads/avatars/";

    /**
     * Obtener perfil completo del usuario
     */
    public UserProfileResponse getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserProfileException("Usuario no encontrado"));
        
        return new UserProfileResponse(user);
    }

    /**
     * Actualizar email del usuario
     */
    public void updateEmail(Long userId, UpdateEmailRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserProfileException("Usuario no encontrado"));

        // Verificar contraseña actual
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new InvalidPasswordException("La contraseña actual es incorrecta");
        }

        // Verificar que el nuevo email no esté en uso
        if (userRepository.existsByEmail(request.getNewEmail()) && 
            !request.getNewEmail().equals(user.getEmail())) {
            throw new UserProfileException("El email ya está en uso por otro usuario");
        }

        user.setEmail(request.getNewEmail());
        // Mantener el estado de verificación del email
        // Solo actualizar el timestamp si estaba verificado
        if (user.getEmailVerified()) {
            user.setEmailVerifiedAt(java.time.LocalDateTime.now());
        }
        
        userRepository.save(user);
    }

    /**
     * Actualizar contraseña del usuario
     */
    public void updatePassword(Long userId, UpdatePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserProfileException("Usuario no encontrado"));

        // Verificar contraseña actual
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new InvalidPasswordException("La contraseña actual es incorrecta");
        }

        // Verificar que la nueva contraseña sea diferente a la actual
        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new UserProfileException("La nueva contraseña debe ser diferente a la actual");
        }

        // Encriptar y guardar nueva contraseña
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    /**
     * Actualizar avatar del usuario (URL predeterminada)
     */
    public void updateAvatar(Long userId, UpdateAvatarRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserProfileException("Usuario no encontrado"));

        user.setAvatarUrl(request.getAvatarUrl());
        userRepository.save(user);
    }

    /**
     * Subir avatar personalizado del usuario
     */
    public String uploadAvatar(Long userId, MultipartFile file) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserProfileException("Usuario no encontrado"));

        // Validar archivo
        if (file.isEmpty()) {
            throw new UserProfileException("El archivo está vacío");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new UserProfileException("El archivo debe ser una imagen");
        }

        // Tamaño máximo: 5MB
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new UserProfileException("El archivo es demasiado grande. Máximo 5MB");
        }

        // Crear directorio si no existe
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Generar nombre único para el archivo
        String fileExtension = getFileExtension(file.getOriginalFilename());
        String fileName = "avatar_" + userId + "_" + UUID.randomUUID().toString() + fileExtension;
        Path filePath = uploadPath.resolve(fileName);

        // Eliminar avatar anterior si existe
        if (user.getAvatarUrl() != null && user.getAvatarUrl().startsWith("/uploads/avatars/")) {
            String oldFileName = user.getAvatarUrl().substring("/uploads/avatars/".length());
            Path oldFilePath = uploadPath.resolve(oldFileName);
            if (Files.exists(oldFilePath)) {
                try {
                    Files.delete(oldFilePath);
                } catch (IOException e) {
                    // Log error but don't fail the operation
                    System.err.println("No se pudo eliminar el avatar anterior: " + e.getMessage());
                }
            }
        }

        // Guardar archivo
        Files.copy(file.getInputStream(), filePath);

        // Actualizar URL en base de datos
        String avatarUrl = "/uploads/avatars/" + fileName;
        user.setAvatarUrl(avatarUrl);
        userRepository.save(user);

        return avatarUrl;
    }

    /**
     * Eliminar avatar del usuario
     */
    public void removeAvatar(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserProfileException("Usuario no encontrado"));

        // Eliminar archivo físico si es un avatar subido
        if (user.getAvatarUrl() != null && user.getAvatarUrl().startsWith("/uploads/avatars/")) {
            String fileName = user.getAvatarUrl().substring("/uploads/avatars/".length());
            Path filePath = Paths.get(UPLOAD_DIR).resolve(fileName);
            if (Files.exists(filePath)) {
                try {
                    Files.delete(filePath);
                } catch (IOException e) {
                    System.err.println("No se pudo eliminar el archivo de avatar: " + e.getMessage());
                }
            }
        }

        user.setAvatarUrl(null);
        userRepository.save(user);
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.lastIndexOf(".") == -1) {
            return ".jpg"; // Extensión por defecto
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }
}