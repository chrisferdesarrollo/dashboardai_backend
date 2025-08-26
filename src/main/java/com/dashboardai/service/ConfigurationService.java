package com.dashboardai.service;

import com.dashboardai.dto.request.ConfigurationRequest;
import com.dashboardai.dto.response.ConfigurationResponse;
import com.dashboardai.entity.Configuration;
import com.dashboardai.repository.ConfigurationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ConfigurationService {
    
    @Autowired
    private ConfigurationRepository configurationRepository;
    
    // Constantes para las claves de configuración
    public static final String N8N_WEBHOOK_URL_KEY = "n8n.webhook.url";
    public static final String N8N_API_URL_KEY = "n8n.api.url";
    public static final String N8N_API_TOKEN_KEY = "n8n.api.token";
    
    /**
     * Obtiene todas las configuraciones de un usuario específico
     */
    public List<ConfigurationResponse> getUserConfigurations(Long userId) {
        return configurationRepository.findByUserId(userId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Obtiene una configuración específica de un usuario
     */
    public Optional<ConfigurationResponse> getUserConfiguration(Long userId, String key) {
        return configurationRepository.findByUserIdAndKey(userId, key)
                .map(this::convertToResponse);
    }
    
    /**
     * Obtiene solo el valor de una configuración específica de un usuario
     */
    public Optional<String> getUserConfigurationValue(Long userId, String key) {
        return configurationRepository.findValueByUserIdAndKey(userId, key);
    }
    
    /**
     * Guarda o actualiza una configuración de un usuario
     */
    public ConfigurationResponse saveUserConfiguration(Long userId, ConfigurationRequest request) {
        Optional<Configuration> existingConfig = configurationRepository.findByUserIdAndKey(userId, request.getKey());
        
        Configuration config;
        if (existingConfig.isPresent()) {
            // Actualizar configuración existente
            config = existingConfig.get();
            config.setValue(request.getValue());
            config.setDescription(request.getDescription());
        } else {
            // Crear nueva configuración
            config = new Configuration(userId, request.getKey(), request.getValue(), request.getDescription());
        }
        
        Configuration savedConfig = configurationRepository.save(config);
        return convertToResponse(savedConfig);
    }
    
    /**
     * Elimina una configuración específica de un usuario
     */
    public void deleteUserConfiguration(Long userId, String key) {
        configurationRepository.deleteByUserIdAndKey(userId, key);
    }
    
    /**
     * Elimina todas las configuraciones de un usuario
     */
    public void deleteAllUserConfigurations(Long userId) {
        configurationRepository.deleteByUserId(userId);
    }
    
    // Métodos específicos para configuraciones de n8n por usuario
    
    /**
     * Obtiene la URL del webhook de n8n para un usuario específico
     */
    public String getUserN8nWebhookUrl(Long userId) {
        return getUserConfigurationValue(userId, N8N_WEBHOOK_URL_KEY).orElse("");
    }
    
    /**
     * Establece la URL del webhook de n8n para un usuario específico
     */
    public void setUserN8nWebhookUrl(Long userId, String webhookUrl) {
        ConfigurationRequest request = new ConfigurationRequest(
            N8N_WEBHOOK_URL_KEY, 
            webhookUrl, 
            "URL del webhook de n8n para triggers de workflows"
        );
        saveUserConfiguration(userId, request);
    }
    
    /**
     * Obtiene la URL de la API de n8n para un usuario específico
     */
    public String getUserN8nApiUrl(Long userId) {
        return getUserConfigurationValue(userId, N8N_API_URL_KEY).orElse("");
    }
    
    /**
     * Establece la URL de la API de n8n para un usuario específico
     */
    public void setUserN8nApiUrl(Long userId, String apiUrl) {
        ConfigurationRequest request = new ConfigurationRequest(
            N8N_API_URL_KEY, 
            apiUrl, 
            "URL de la API de n8n para gestión de workflows"
        );
        saveUserConfiguration(userId, request);
    }
    
    /**
     * Obtiene el token de la API de n8n para un usuario específico
     */
    public String getUserN8nApiToken(Long userId) {
        return getUserConfigurationValue(userId, N8N_API_TOKEN_KEY).orElse("");
    }
    
    /**
     * Establece el token de la API de n8n para un usuario específico
     */
    public void setUserN8nApiToken(Long userId, String apiToken) {
        ConfigurationRequest request = new ConfigurationRequest(
            N8N_API_TOKEN_KEY, 
            apiToken, 
            "Token de autenticación para la API de n8n"
        );
        saveUserConfiguration(userId, request);
    }
    
    /**
     * Obtiene todas las configuraciones de n8n para un usuario
     */
    public ConfigurationResponse getUserN8nConfig(Long userId) {
        // Crear un objeto que contenga todas las configuraciones de n8n
        String webhookUrl = getUserN8nWebhookUrl(userId);
        String apiUrl = getUserN8nApiUrl(userId);
        String apiToken = getUserN8nApiToken(userId);
        
        // Retornar como un JSON string con todas las configuraciones
        String configJson = String.format(
            "{\"webhookUrl\":\"%s\",\"apiUrl\":\"%s\",\"apiToken\":\"%s\"}", 
            webhookUrl, apiUrl, apiToken.isEmpty() ? "" : "***"
        );
        
        return new ConfigurationResponse(
            null, 
            "n8n.config", 
            configJson, 
            "Configuración completa de n8n", 
            null, 
            null
        );
    }
    
    /**
     * Inicializa configuraciones por defecto para un nuevo usuario
     */
    public void initializeDefaultConfigurations(Long userId) {
        // Crear configuraciones por defecto si no existen
        if (!configurationRepository.existsByUserIdAndKey(userId, N8N_WEBHOOK_URL_KEY)) {
            setUserN8nWebhookUrl(userId, "");
        }
        if (!configurationRepository.existsByUserIdAndKey(userId, N8N_API_URL_KEY)) {
            setUserN8nApiUrl(userId, "");
        }
        if (!configurationRepository.existsByUserIdAndKey(userId, N8N_API_TOKEN_KEY)) {
            setUserN8nApiToken(userId, "");
        }
    }
    
    private ConfigurationResponse convertToResponse(Configuration config) {
        return new ConfigurationResponse(
            config.getId(),
            config.getKey(),
            config.getValue(),
            config.getDescription(),
            config.getCreatedAt(),
            config.getUpdatedAt()
        );
    }
}
