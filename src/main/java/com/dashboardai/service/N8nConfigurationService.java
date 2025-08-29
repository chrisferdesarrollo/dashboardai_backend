package com.dashboardai.service;

import com.dashboardai.dto.request.N8nConfigurationRequest;
import com.dashboardai.dto.request.N8nTestConnectionRequest;
import com.dashboardai.dto.response.N8nConfigurationResponse;
import com.dashboardai.entity.N8nConfiguration;
import com.dashboardai.repository.N8nConfigurationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class N8nConfigurationService {
    
    private static final Logger logger = LoggerFactory.getLogger(N8nConfigurationService.class);
    
    @Autowired
    private N8nConfigurationRepository n8nConfigurationRepository;
    
    @Autowired
    private RestTemplate restTemplate;
    
    /**
     * Save or update N8n configuration for a user
     */
    @Transactional
    public N8nConfigurationResponse saveConfiguration(Long userId, N8nConfigurationRequest request) {
        logger.info("Saving N8n configuration for user: {}", userId);
        
        // Deactivate existing configurations for this user
        Optional<N8nConfiguration> existingConfig = n8nConfigurationRepository.findActiveByUserId(userId);
        if (existingConfig.isPresent()) {
            existingConfig.get().setIsActive(false);
            n8nConfigurationRepository.save(existingConfig.get());
        }
        
        // Create new configuration
        N8nConfiguration config = new N8nConfiguration(userId, request.getN8nUrl());
        config.setApiKey(request.getApiKey());
        
        config = n8nConfigurationRepository.save(config);
        
        return mapToResponse(config);
    }
    
    /**
     * Get active N8n configuration for a user
     */
    public Optional<N8nConfigurationResponse> getActiveConfiguration(Long userId) {
        logger.info("Getting active N8n configuration for user: {}", userId);
        
        return n8nConfigurationRepository.findActiveByUserId(userId)
                .map(this::mapToResponse);
    }
    
    /**
     * Get all N8n configurations for a user
     */
    public List<N8nConfigurationResponse> getAllConfigurations(Long userId) {
        logger.info("Getting all N8n configurations for user: {}", userId);
        
        return n8nConfigurationRepository.findAllByUserId(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    /**
     * Test connection to N8n instance
     */
    public Map<String, Object> testConnection(Long userId, N8nTestConnectionRequest request) {
        logger.info("Testing N8n connection for user: {} to URL: {}", userId, request.getN8nUrl());
        
        try {
            // Prepare the webhook URL
            String webhookUrl = request.getN8nUrl() + "/webhook/test-connection";
            
            // Prepare headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            if (request.getApiKey() != null && !request.getApiKey().isEmpty()) {
                headers.set("Authorization", "Bearer " + request.getApiKey());
            }
            
            // Prepare test payload
            Map<String, Object> payload = Map.of(
                "test", true,
                "timestamp", LocalDateTime.now().toString(),
                "userId", userId
            );
            
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);
            
            // Make the request
            ResponseEntity<String> response = restTemplate.exchange(
                webhookUrl,
                HttpMethod.POST,
                entity,
                String.class
            );
            
            // Update configuration if it exists
            Optional<N8nConfiguration> config = n8nConfigurationRepository.findActiveByUserId(userId);
            if (config.isPresent()) {
                config.get().setLastTestDate(LocalDateTime.now());
                config.get().setLastTestStatus("success");
                n8nConfigurationRepository.save(config.get());
            }
            
            logger.info("N8n connection test successful for user: {}", userId);
            
            return Map.of(
                "status", "success",
                "message", "Connection successful",
                "server", request.getN8nUrl(),
                "timestamp", LocalDateTime.now().toString(),
                "response", response.getBody()
            );
            
        } catch (Exception e) {
            logger.error("N8n connection test failed for user: {} - Error: {}", userId, e.getMessage());
            
            // Update configuration if it exists
            Optional<N8nConfiguration> config = n8nConfigurationRepository.findActiveByUserId(userId);
            if (config.isPresent()) {
                config.get().setLastTestDate(LocalDateTime.now());
                config.get().setLastTestStatus("failed");
                n8nConfigurationRepository.save(config.get());
            }
            
            return Map.of(
                "status", "error",
                "message", "Connection failed: " + e.getMessage(),
                "server", request.getN8nUrl(),
                "timestamp", LocalDateTime.now().toString()
            );
        }
    }
    
    /**
     * Delete N8n configuration
     */
    @Transactional
    public boolean deleteConfiguration(Long userId, Long configId) {
        logger.info("Deleting N8n configuration {} for user: {}", configId, userId);
        
        Optional<N8nConfiguration> config = n8nConfigurationRepository.findById(configId);
        if (config.isPresent() && config.get().getUserId().equals(userId)) {
            n8nConfigurationRepository.delete(config.get());
            return true;
        }
        
        return false;
    }
    
    /**
     * Activate a specific configuration for a user
     */
    @Transactional
    public Optional<N8nConfigurationResponse> activateConfiguration(Long userId, Long configId) {
        logger.info("Activating N8n configuration {} for user: {}", configId, userId);
        
        // First, deactivate all configurations for this user
        List<N8nConfiguration> userConfigs = n8nConfigurationRepository.findAllByUserId(userId);
        userConfigs.forEach(config -> {
            config.setIsActive(false);
            n8nConfigurationRepository.save(config);
        });
        
        // Then activate the specified configuration
        Optional<N8nConfiguration> configToActivate = n8nConfigurationRepository.findById(configId);
        if (configToActivate.isPresent() && configToActivate.get().getUserId().equals(userId)) {
            configToActivate.get().setIsActive(true);
            N8nConfiguration savedConfig = n8nConfigurationRepository.save(configToActivate.get());
            return Optional.of(mapToResponse(savedConfig));
        }
        
        return Optional.empty();
    }
    
    /**
     * Get user's N8n webhook base URL
     */
    public Optional<String> getUserN8nWebhookBase(Long userId) {
        return n8nConfigurationRepository.findActiveByUserId(userId)
                .map(N8nConfiguration::getWebhookBase);
    }
    
    /**
     * Get user's N8n API base URL
     */
    public Optional<String> getUserN8nApiBase(Long userId) {
        return n8nConfigurationRepository.findActiveByUserId(userId)
                .map(N8nConfiguration::getApiBase);
    }
    
    /**
     * Map entity to response DTO
     */
    private N8nConfigurationResponse mapToResponse(N8nConfiguration config) {
        return new N8nConfigurationResponse(
            config.getId(),
            config.getN8nUrl(),
            config.getWebhookBase(),
            config.getApiBase(),
            config.getIsActive(),
            config.getLastTestDate(),
            config.getLastTestStatus(),
            config.getCreatedAt(),
            config.getUpdatedAt()
        );
    }
}
