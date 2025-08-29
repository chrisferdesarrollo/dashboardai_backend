package com.dashboardai.controller;

import com.dashboardai.dto.request.N8nConfigurationRequest;
import com.dashboardai.dto.request.N8nTestConnectionRequest;
import com.dashboardai.dto.response.N8nConfigurationResponse;
import com.dashboardai.service.N8nConfigurationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/n8n")
@CrossOrigin(origins = "*")
public class N8nConfigurationController {
    
    private static final Logger logger = LoggerFactory.getLogger(N8nConfigurationController.class);
    
    @Autowired
    private N8nConfigurationService n8nConfigurationService;
    
    /**
     * Save N8n configuration for the current user
     */
    @PostMapping("/config")
    public ResponseEntity<?> saveConfiguration(@Valid @RequestBody N8nConfigurationRequest request) {
        try {
            Long userId = getCurrentUserId();
            N8nConfigurationResponse response = n8nConfigurationService.saveConfiguration(userId, request);
            
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "N8n configuration saved successfully",
                "data", response
            ));
        } catch (Exception e) {
            logger.error("Error saving N8n configuration: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "status", "error",
                    "message", "Failed to save N8n configuration: " + e.getMessage()
                ));
        }
    }
    
    /**
     * Get active N8n configuration for the current user
     */
    @GetMapping("/config")
    public ResponseEntity<?> getConfiguration() {
        try {
            Long userId = getCurrentUserId();
            Optional<N8nConfigurationResponse> config = n8nConfigurationService.getActiveConfiguration(userId);
            
            if (config.isPresent()) {
                return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "data", config.get()
                ));
            } else {
                return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "No N8n configuration found",
                    "data", null
                ));
            }
        } catch (Exception e) {
            logger.error("Error getting N8n configuration: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "status", "error",
                    "message", "Failed to get N8n configuration: " + e.getMessage()
                ));
        }
    }
    
    /**
     * Get all N8n configurations for the current user
     */
    @GetMapping("/config/all")
    public ResponseEntity<?> getAllConfigurations() {
        try {
            Long userId = getCurrentUserId();
            List<N8nConfigurationResponse> configs = n8nConfigurationService.getAllConfigurations(userId);
            
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "data", configs
            ));
        } catch (Exception e) {
            logger.error("Error getting all N8n configurations: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "status", "error",
                    "message", "Failed to get N8n configurations: " + e.getMessage()
                ));
        }
    }
    
    /**
     * Test connection to N8n instance
     */
    @PostMapping("/test-connection")
    public ResponseEntity<?> testConnection(@Valid @RequestBody N8nTestConnectionRequest request) {
        try {
            Long userId = getCurrentUserId();
            Map<String, Object> result = n8nConfigurationService.testConnection(userId, request);
            
            if ("success".equals(result.get("status"))) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
            }
        } catch (Exception e) {
            logger.error("Error testing N8n connection: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "status", "error",
                    "message", "Failed to test N8n connection: " + e.getMessage()
                ));
        }
    }
    
    /**
     * Delete N8n configuration
     */
    @DeleteMapping("/config/{configId}")
    public ResponseEntity<?> deleteConfiguration(@PathVariable Long configId) {
        try {
            Long userId = getCurrentUserId();
            boolean deleted = n8nConfigurationService.deleteConfiguration(userId, configId);
            
            if (deleted) {
                return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "N8n configuration deleted successfully"
                ));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                        "status", "error",
                        "message", "N8n configuration not found"
                    ));
            }
        } catch (Exception e) {
            logger.error("Error deleting N8n configuration: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "status", "error",
                    "message", "Failed to delete N8n configuration: " + e.getMessage()
                ));
        }
    }
    
    /**
     * Activate a specific N8n configuration
     */
    @PutMapping("/config/{configId}/activate")
    public ResponseEntity<?> activateConfiguration(@PathVariable Long configId) {
        try {
            Long userId = getCurrentUserId();
            Optional<N8nConfigurationResponse> config = n8nConfigurationService.activateConfiguration(userId, configId);
            
            if (config.isPresent()) {
                return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "N8n configuration activated successfully",
                    "data", config.get()
                ));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                        "status", "error",
                        "message", "N8n configuration not found"
                    ));
            }
        } catch (Exception e) {
            logger.error("Error activating N8n configuration: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                    "status", "error",
                    "message", "Failed to activate N8n configuration: " + e.getMessage()
                ));
        }
    }
    
    /**
     * Get current user ID from security context
     */
    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof org.springframework.security.core.userdetails.User) {
            String username = auth.getName();
            // Parse username to get user ID (assuming username is the user ID)
            try {
                return Long.parseLong(username);
            } catch (NumberFormatException e) {
                logger.error("Unable to parse user ID from username: {}", username);
                throw new RuntimeException("Invalid user ID");
            }
        }
        throw new RuntimeException("User not authenticated");
    }
}
