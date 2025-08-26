package com.dashboardai.controller;

import com.dashboardai.dto.request.ConfigurationRequest;
import com.dashboardai.dto.response.ConfigurationResponse;
import com.dashboardai.security.services.UserDetailsImpl;
import com.dashboardai.service.ConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/configuration")
@CrossOrigin(origins = "*")
public class ConfigurationController {
    
    @Autowired
    private ConfigurationService configurationService;
    
    /**
     * Obtiene el ID del usuario autenticado
     */
    private Long getCurrentUserId(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return userDetails.getId();
    }
    
    /**
     * Obtiene todas las configuraciones del usuario autenticado
     */
    @GetMapping
    public ResponseEntity<List<ConfigurationResponse>> getUserConfigurations(Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        List<ConfigurationResponse> configurations = configurationService.getUserConfigurations(userId);
        return ResponseEntity.ok(configurations);
    }
    
    /**
     * Obtiene una configuración específica del usuario autenticado
     */
    @GetMapping("/{key}")
    public ResponseEntity<ConfigurationResponse> getUserConfiguration(
            @PathVariable String key, 
            Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        return configurationService.getUserConfiguration(userId, key)
                .map(config -> ResponseEntity.ok(config))
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Obtiene solo el valor de una configuración específica del usuario autenticado
     */
    @GetMapping("/{key}/value")
    public ResponseEntity<String> getUserConfigurationValue(
            @PathVariable String key, 
            Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        return configurationService.getUserConfigurationValue(userId, key)
                .map(value -> ResponseEntity.ok(value))
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Guarda o actualiza una configuración del usuario autenticado
     */
    @PostMapping
    public ResponseEntity<ConfigurationResponse> saveUserConfiguration(
            @Valid @RequestBody ConfigurationRequest request, 
            Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        ConfigurationResponse response = configurationService.saveUserConfiguration(userId, request);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Actualiza una configuración específica del usuario autenticado
     */
    @PutMapping("/{key}")
    public ResponseEntity<ConfigurationResponse> updateUserConfiguration(
            @PathVariable String key, 
            @Valid @RequestBody ConfigurationRequest request, 
            Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        request.setKey(key);
        ConfigurationResponse response = configurationService.saveUserConfiguration(userId, request);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Elimina una configuración específica del usuario autenticado
     */
    @DeleteMapping("/{key}")
    public ResponseEntity<Void> deleteUserConfiguration(
            @PathVariable String key, 
            Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        configurationService.deleteUserConfiguration(userId, key);
        return ResponseEntity.noContent().build();
    }
    
    // Endpoints específicos para configuraciones de n8n
    
    /**
     * Obtiene la URL del webhook de n8n del usuario autenticado
     */
    @GetMapping("/n8n/webhook-url")
    public ResponseEntity<String> getUserN8nWebhookUrl(Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        String webhookUrl = configurationService.getUserN8nWebhookUrl(userId);
        return ResponseEntity.ok(webhookUrl);
    }
    
    /**
     * Establece la URL del webhook de n8n del usuario autenticado
     */
    @PostMapping("/n8n/webhook-url")
    public ResponseEntity<Void> setUserN8nWebhookUrl(
            @RequestBody Map<String, String> request, 
            Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        String webhookUrl = request.get("webhookUrl");
        configurationService.setUserN8nWebhookUrl(userId, webhookUrl);
        return ResponseEntity.ok().build();
    }
    
    /**
     * Obtiene la URL de la API de n8n del usuario autenticado
     */
    @GetMapping("/n8n/api-url")
    public ResponseEntity<String> getUserN8nApiUrl(Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        String apiUrl = configurationService.getUserN8nApiUrl(userId);
        return ResponseEntity.ok(apiUrl);
    }
    
    /**
     * Establece la URL de la API de n8n del usuario autenticado
     */
    @PostMapping("/n8n/api-url")
    public ResponseEntity<Void> setUserN8nApiUrl(
            @RequestBody Map<String, String> request, 
            Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        String apiUrl = request.get("apiUrl");
        configurationService.setUserN8nApiUrl(userId, apiUrl);
        return ResponseEntity.ok().build();
    }
    
    /**
     * Obtiene el token de la API de n8n del usuario autenticado
     */
    @GetMapping("/n8n/api-token")
    public ResponseEntity<String> getUserN8nApiToken(Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        String apiToken = configurationService.getUserN8nApiToken(userId);
        // No devolver el token completo por seguridad, solo indicar si existe
        return ResponseEntity.ok(apiToken.isEmpty() ? "" : "***");
    }
    
    /**
     * Establece el token de la API de n8n del usuario autenticado
     */
    @PostMapping("/n8n/api-token")
    public ResponseEntity<Void> setUserN8nApiToken(
            @RequestBody Map<String, String> request, 
            Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        String apiToken = request.get("apiToken");
        configurationService.setUserN8nApiToken(userId, apiToken);
        return ResponseEntity.ok().build();
    }
    
    /**
     * Obtiene todas las configuraciones de n8n del usuario autenticado
     */
    @GetMapping("/n8n/config")
    public ResponseEntity<Map<String, String>> getUserN8nConfig(Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        
        Map<String, String> config = Map.of(
            "webhookUrl", configurationService.getUserN8nWebhookUrl(userId),
            "apiUrl", configurationService.getUserN8nApiUrl(userId),
            "apiToken", configurationService.getUserN8nApiToken(userId).isEmpty() ? "" : "***"
        );
        
        return ResponseEntity.ok(config);
    }
    
    /**
     * Guarda todas las configuraciones de n8n del usuario autenticado
     */
    @PostMapping("/n8n/config")
    public ResponseEntity<Void> saveUserN8nConfig(
            @RequestBody Map<String, String> config, 
            Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        
        if (config.containsKey("webhookUrl")) {
            configurationService.setUserN8nWebhookUrl(userId, config.get("webhookUrl"));
        }
        if (config.containsKey("apiUrl")) {
            configurationService.setUserN8nApiUrl(userId, config.get("apiUrl"));
        }
        if (config.containsKey("apiToken") && !config.get("apiToken").equals("***")) {
            configurationService.setUserN8nApiToken(userId, config.get("apiToken"));
        }
        
        return ResponseEntity.ok().build();
    }
    
    /**
     * Inicializa configuraciones por defecto para el usuario autenticado
     */
    @PostMapping("/initialize")
    public ResponseEntity<Void> initializeUserConfigurations(Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        configurationService.initializeDefaultConfigurations(userId);
        return ResponseEntity.ok().build();
    }
}
