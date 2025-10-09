package com.dashboardai.controller;

import com.dashboardai.dto.request.UpdateEmailRequest;
import com.dashboardai.dto.request.UpdatePasswordRequest;
import com.dashboardai.dto.request.UpdateAvatarRequest;
import com.dashboardai.dto.response.UserProfileResponse;
import com.dashboardai.exception.InvalidPasswordException;
import com.dashboardai.exception.UserProfileException;
import com.dashboardai.security.services.UserDetailsImpl;
import com.dashboardai.service.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserProfileController {

    @Autowired
    private UserProfileService userProfileService;

    /**
     * Obtener perfil completo del usuario autenticado
     */
    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile(Authentication authentication) {
        try {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            Long userId = userDetails.getId();

            UserProfileResponse profile = userProfileService.getUserProfile(userId);
            return ResponseEntity.ok(profile);
        } catch (UserProfileException e) {
            return ResponseEntity.status(400)
                    .body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(createErrorResponse("Error interno del servidor"));
        }
    }

    /**
     * Actualizar email del usuario
     */
    @PutMapping("/update-email")
    public ResponseEntity<?> updateEmail(@Valid @RequestBody UpdateEmailRequest request,
                                       Authentication authentication) {
        try {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            Long userId = userDetails.getId();

            userProfileService.updateEmail(userId, request);
            return ResponseEntity.ok(createSuccessResponse("Email actualizado correctamente"));
        } catch (InvalidPasswordException e) {
            return ResponseEntity.status(400)
                    .body(createErrorResponse(e.getMessage()));
        } catch (UserProfileException e) {
            return ResponseEntity.status(400)
                    .body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(createErrorResponse("Error interno del servidor"));
        }
    }

    /**
     * Actualizar contraseña del usuario
     */
    @PutMapping("/update-password")
    public ResponseEntity<?> updatePassword(@Valid @RequestBody UpdatePasswordRequest request,
                                          Authentication authentication) {
        try {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            Long userId = userDetails.getId();

            userProfileService.updatePassword(userId, request);
            return ResponseEntity.ok(createSuccessResponse("Contraseña actualizada correctamente"));
        } catch (InvalidPasswordException e) {
            return ResponseEntity.status(400)
                    .body(createErrorResponse(e.getMessage()));
        } catch (UserProfileException e) {
            return ResponseEntity.status(400)
                    .body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(createErrorResponse("Error interno del servidor"));
        }
    }

    /**
     * Actualizar avatar con URL predeterminada
     */
    @PutMapping("/update-avatar")
    public ResponseEntity<?> updateAvatar(@Valid @RequestBody UpdateAvatarRequest request,
                                        Authentication authentication) {
        try {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            Long userId = userDetails.getId();

            userProfileService.updateAvatar(userId, request);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Avatar actualizado correctamente");
            response.put("avatarUrl", request.getAvatarUrl());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(createErrorResponse("Error al actualizar avatar: " + e.getMessage()));
        }
    }

    /**
     * Subir avatar personalizado
     */
    @PostMapping("/upload-avatar")
    public ResponseEntity<?> uploadAvatar(@RequestParam("avatar") MultipartFile file,
                                        Authentication authentication) {
        try {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            Long userId = userDetails.getId();

            String avatarUrl = userProfileService.uploadAvatar(userId, file);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Avatar subido correctamente");
            response.put("avatarUrl", avatarUrl);
            
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            return ResponseEntity.badRequest()
                    .body(createErrorResponse("Error al subir archivo: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(createErrorResponse("Error al actualizar avatar: " + e.getMessage()));
        }
    }

    /**
     * Eliminar avatar del usuario
     */
    @DeleteMapping("/remove-avatar")
    public ResponseEntity<?> removeAvatar(Authentication authentication) {
        try {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            Long userId = userDetails.getId();

            userProfileService.removeAvatar(userId);
            return ResponseEntity.ok(createSuccessResponse("Avatar eliminado correctamente"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(createErrorResponse("Error al eliminar avatar: " + e.getMessage()));
        }
    }

    private Map<String, String> createSuccessResponse(String message) {
        Map<String, String> response = new HashMap<>();
        response.put("message", message);
        return response;
    }

    private Map<String, String> createErrorResponse(String message) {
        Map<String, String> response = new HashMap<>();
        response.put("error", message);
        return response;
    }
}