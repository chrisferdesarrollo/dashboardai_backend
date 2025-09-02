package com.dashboardai.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.util.Map;

@RestController
@RequestMapping("/api/n8n/proxy")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"}, maxAge = 3600)
@SuppressWarnings("rawtypes")
public class N8nProxyController {
    
    private static final Logger logger = LoggerFactory.getLogger(N8nProxyController.class);
    private static final String N8N_BASE_URL = "https://n8n-n8n.hrxtio.easypanel.host";
    
    private final RestTemplate restTemplate;
    
    public N8nProxyController() {
        this.restTemplate = new RestTemplate();
    }
    
    /**
     * Proxy para test de conexión a n8n
     */
    @PostMapping("/test-connection")
    public ResponseEntity<?> testConnection(@RequestBody Map<String, Object> payload) {
        try {
            logger.info("Testing n8n connection via proxy");
            
            String url = N8N_BASE_URL + "/webhook/test-connection";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("User-Agent", "DashboardAI-Backend/1.0");
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
            
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
            
            logger.info("n8n connection test successful: {}", response.getStatusCode());
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", response.getBody(),
                "status", response.getStatusCode().value()
            ));
            
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            logger.error("HTTP error testing n8n connection: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            return ResponseEntity.ok(Map.of(
                "success", false,
                "error", "HTTP " + e.getStatusCode() + ": " + e.getResponseBodyAsString(),
                "status", e.getStatusCode().value()
            ));
        } catch (ResourceAccessException e) {
            logger.error("Network error testing n8n connection: {}", e.getMessage());
            return ResponseEntity.ok(Map.of(
                "success", false,
                "error", "No se pudo conectar con n8n: " + e.getMessage()
            ));
        } catch (Exception e) {
            logger.error("Unexpected error testing n8n connection: {}", e.getMessage(), e);
            return ResponseEntity.ok(Map.of(
                "success", false,
                "error", "Error inesperado: " + e.getMessage()
            ));
        }
    }
    
    /**
     * Proxy para obtener workflows de n8n
     */
    @GetMapping("/workflows")
    public ResponseEntity<?> getWorkflows() {
        try {
            logger.info("Getting workflows from n8n via proxy");
            
            String url = N8N_BASE_URL + "/api/v1/workflows";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("User-Agent", "DashboardAI-Backend/1.0");
            // TODO: Agregar token de autenticación cuando esté configurado
            // headers.set("Authorization", "Bearer " + n8nApiToken);
            
            HttpEntity<String> request = new HttpEntity<>(headers);
            
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, request, Map.class);
            
            logger.info("n8n workflows retrieved successfully: {}", response.getStatusCode());
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", response.getBody(),
                "status", response.getStatusCode().value()
            ));
            
        } catch (Exception e) {
            logger.error("Error getting workflows from n8n: {}", e.getMessage(), e);
            return ResponseEntity.ok(Map.of(
                "success", false,
                "error", "Error al obtener workflows: " + e.getMessage()
            ));
        }
    }
    
    /**
     * Proxy para crear sesión de WhatsApp
     */
    @PostMapping("/whatsapp/create-session")
    public ResponseEntity<?> createWhatsAppSession(@RequestBody Map<String, Object> payload) {
        try {
            logger.info("Creating WhatsApp session via proxy");
            
            String url = N8N_BASE_URL + "/webhook/create-whatsapp-session";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("User-Agent", "DashboardAI-Backend/1.0");
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
            
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
            
            logger.info("WhatsApp session created successfully: {}", response.getStatusCode());
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "data", response.getBody(),
                "status", response.getStatusCode().value()
            ));
            
        } catch (Exception e) {
            logger.error("Error creating WhatsApp session: {}", e.getMessage(), e);
            return ResponseEntity.ok(Map.of(
                "success", false,
                "error", "Error al crear sesión de WhatsApp: " + e.getMessage()
            ));
        }
    }
    
    /**
     * Proxy para eliminar sesión de WhatsApp
     */
    @PostMapping("/delete-whatsapp-session")
    public ResponseEntity<?> deleteWhatsAppSession(@RequestBody Map<String, Object> payload) {
        try {
            logger.info("Deleting WhatsApp session via proxy: {}", payload.get("sessionName"));
            
            String url = N8N_BASE_URL + "/webhook/delete-whatsapp-session";
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("User-Agent", "DashboardAI-Backend/1.0");
            headers.set("Access-Control-Allow-Origin", "*");
            headers.set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            headers.set("Access-Control-Allow-Headers", "Content-Type, Authorization");
            
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
            
            logger.info("Sending delete request to n8n: {}", url);
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);
            
            logger.info("WhatsApp session deleted successfully: {}", response.getStatusCode());
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Sesión eliminada correctamente",
                "sessionName", payload.get("sessionName"),
                "timestamp", java.time.Instant.now().toString(),
                "data", response.getBody(),
                "status", response.getStatusCode().value()
            ));
            
        } catch (HttpClientErrorException e) {
            logger.error("HTTP Client Error deleting WhatsApp session: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            return ResponseEntity.ok(Map.of(
                "success", false,
                "error", "Error del cliente HTTP: " + e.getStatusCode() + " - " + e.getResponseBodyAsString()
            ));
        } catch (HttpServerErrorException e) {
            logger.error("HTTP Server Error deleting WhatsApp session: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            return ResponseEntity.ok(Map.of(
                "success", false,
                "error", "Error del servidor HTTP: " + e.getStatusCode() + " - " + e.getResponseBodyAsString()
            ));
        } catch (ResourceAccessException e) {
            logger.error("Network error deleting WhatsApp session: {}", e.getMessage());
            return ResponseEntity.ok(Map.of(
                "success", false,
                "error", "Error de red: " + e.getMessage()
            ));
        } catch (Exception e) {
            logger.error("Unexpected error deleting WhatsApp session: {}", e.getMessage(), e);
            return ResponseEntity.ok(Map.of(
                "success", false,
                "error", "Error inesperado al eliminar sesión de WhatsApp: " + e.getMessage()
            ));
        }
    }
}
