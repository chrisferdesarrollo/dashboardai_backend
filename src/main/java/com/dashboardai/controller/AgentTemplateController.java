package com.dashboardai.controller;

import com.dashboardai.dto.request.AgentTemplateRequest;
import com.dashboardai.dto.response.AgentTemplateResponse;
import com.dashboardai.dto.response.MessageResponse;
import com.dashboardai.service.AgentTemplateService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/agent-templates")
@PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
public class AgentTemplateController {
    
    @Autowired
    private AgentTemplateService agentTemplateService;
    
    /**
     * Crear un nuevo template de agente
     */
    @PostMapping
    public ResponseEntity<?> createTemplate(@Valid @RequestBody AgentTemplateRequest request) {
        try {
            System.out.println("🔵 AgentTemplateController: Creando template - " + request.getName());
            
            AgentTemplateResponse response = agentTemplateService.createTemplate(request);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            System.err.println("❌ AgentTemplateController: Error al crear template: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new MessageResponse("Error al crear template: " + e.getMessage()));
        }
    }
    
    /**
     * Obtener todos los templates del usuario
     */
    @GetMapping
    public ResponseEntity<?> getAllTemplates() {
        try {
            System.out.println("🔵 AgentTemplateController: Obteniendo todos los templates");
            
            List<AgentTemplateResponse> templates = agentTemplateService.getAllTemplates();
            
            return ResponseEntity.ok(templates);
        } catch (Exception e) {
            System.err.println("❌ AgentTemplateController: Error al obtener templates: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new MessageResponse("Error al obtener templates: " + e.getMessage()));
        }
    }
    
    /**
     * Obtener templates activos
     */
    @GetMapping("/active")
    public ResponseEntity<?> getActiveTemplates() {
        try {
            System.out.println("🔵 AgentTemplateController: Obteniendo templates activos");
            
            List<AgentTemplateResponse> templates = agentTemplateService.getActiveTemplates();
            
            return ResponseEntity.ok(templates);
        } catch (Exception e) {
            System.err.println("❌ AgentTemplateController: Error al obtener templates activos: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new MessageResponse("Error al obtener templates activos: " + e.getMessage()));
        }
    }
    
    /**
     * Obtener un template por ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getTemplateById(@PathVariable UUID id) {
        try {
            System.out.println("🔵 AgentTemplateController: Obteniendo template - ID: " + id);
            
            AgentTemplateResponse template = agentTemplateService.getTemplateById(id);
            
            return ResponseEntity.ok(template);
        } catch (RuntimeException e) {
            System.err.println("❌ AgentTemplateController: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new MessageResponse(e.getMessage()));
        } catch (Exception e) {
            System.err.println("❌ AgentTemplateController: Error al obtener template: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new MessageResponse("Error al obtener template: " + e.getMessage()));
        }
    }
    
    /**
     * Actualizar un template (solo ADMIN)
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateTemplate(@PathVariable UUID id, @Valid @RequestBody AgentTemplateRequest request) {
        try {
            System.out.println("🔵 AgentTemplateController: Actualizando template - ID: " + id);
            
            AgentTemplateResponse response = agentTemplateService.updateTemplate(id, request);
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            System.err.println("❌ AgentTemplateController: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new MessageResponse(e.getMessage()));
        } catch (Exception e) {
            System.err.println("❌ AgentTemplateController: Error al actualizar template: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new MessageResponse("Error al actualizar template: " + e.getMessage()));
        }
    }
    
    /**
     * Eliminar un template (solo ADMIN)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteTemplate(@PathVariable UUID id) {
        try {
            System.out.println("🔵 AgentTemplateController: Eliminando template - ID: " + id);
            
            agentTemplateService.deleteTemplate(id);
            
            return ResponseEntity.ok(new MessageResponse("Template eliminado exitosamente"));
        } catch (RuntimeException e) {
            System.err.println("❌ AgentTemplateController: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new MessageResponse(e.getMessage()));
        } catch (Exception e) {
            System.err.println("❌ AgentTemplateController: Error al eliminar template: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new MessageResponse("Error al eliminar template: " + e.getMessage()));
        }
    }
}
