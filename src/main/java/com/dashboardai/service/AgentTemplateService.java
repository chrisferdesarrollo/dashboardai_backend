package com.dashboardai.service;

import com.dashboardai.dto.request.AgentTemplateRequest;
import com.dashboardai.dto.response.AgentTemplateResponse;
import com.dashboardai.entity.AgentTemplate;
import com.dashboardai.model.User;
import com.dashboardai.repository.AgentTemplateRepository;
import com.dashboardai.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AgentTemplateService {
    
    @Autowired
    private AgentTemplateRepository agentTemplateRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * Obtener el usuario autenticado actual
     */
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }
    
    /**
     * Verificar si el usuario actual es ADMIN
     */
    private boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .anyMatch(role -> role.equals("ROLE_ADMIN"));
    }
    
    /**
     * Crear un nuevo template de agente
     */
    @Transactional
    public AgentTemplateResponse createTemplate(AgentTemplateRequest request) {
        User user = getCurrentUser();
        
        AgentTemplate template = new AgentTemplate();
        template.setName(request.getName());
        template.setDescription(request.getDescription());
        template.setSystemPrompt(request.getSystemPrompt());
        template.setKnowledgeBaseId(request.getKnowledgeBaseId());
        template.setKnowledgeBaseName(request.getKnowledgeBaseName());
        template.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
        template.setUser(user);
        
        AgentTemplate saved = agentTemplateRepository.save(template);
        
        System.out.println("✅ AgentTemplateService: Template creado - ID: " + saved.getId());
        
        return new AgentTemplateResponse(saved);
    }
    
    /**
     * Obtener todos los templates del usuario
     * Si es ADMIN, obtiene todos los templates de todos los usuarios
     */
    public List<AgentTemplateResponse> getAllTemplates() {
        User user = getCurrentUser();
        boolean isAdminUser = isAdmin();
        
        List<AgentTemplate> templates;
        
        if (isAdminUser) {
            // ADMIN ve todos los templates de todos los usuarios
            templates = agentTemplateRepository.findAllByOrderByCreatedAtDesc();
            System.out.println("🔑 AgentTemplateService: ADMIN obteniendo todos los templates (todos los usuarios)");
        } else {
            // Usuario normal solo ve sus propios templates
            templates = agentTemplateRepository.findByUserOrderByCreatedAtDesc(user);
            System.out.println("👤 AgentTemplateService: Usuario obteniendo solo sus templates");
        }
        
        return templates.stream()
            .map(AgentTemplateResponse::new)
            .collect(Collectors.toList());
    }
    
    /**
     * Obtener templates activos del usuario
     * Si es ADMIN, obtiene todos los templates activos de todos los usuarios
     */
    public List<AgentTemplateResponse> getActiveTemplates() {
        User user = getCurrentUser();
        boolean isAdminUser = isAdmin();
        
        List<AgentTemplate> templates;
        
        if (isAdminUser) {
            // ADMIN ve todos los templates activos de todos los usuarios
            templates = agentTemplateRepository.findByIsActiveOrderByCreatedAtDesc(true);
            System.out.println("🔑 AgentTemplateService: ADMIN obteniendo templates activos (todos los usuarios)");
        } else {
            // Usuario normal solo ve sus propios templates activos
            templates = agentTemplateRepository.findByUserAndIsActive(user, true);
            System.out.println("👤 AgentTemplateService: Usuario obteniendo sus templates activos");
        }
        
        return templates.stream()
            .map(AgentTemplateResponse::new)
            .collect(Collectors.toList());
    }
    
    /**
     * Obtener un template por ID
     * ADMIN puede acceder a cualquier template, usuarios normales solo a los suyos
     */
    public AgentTemplateResponse getTemplateById(UUID id) {
        User user = getCurrentUser();
        boolean isAdminUser = isAdmin();
        
        AgentTemplate template = agentTemplateRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Template no encontrado"));
        
        // Verificar permisos: ADMIN o propietario del template
        if (!isAdminUser && !template.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("No tiene permisos para acceder a este template");
        }
        
        return new AgentTemplateResponse(template);
    }
    
    /**
     * Actualizar un template
     * ADMIN puede actualizar cualquier template, usuarios normales solo los suyos
     */
    @Transactional
    public AgentTemplateResponse updateTemplate(UUID id, AgentTemplateRequest request) {
        User user = getCurrentUser();
        boolean isAdminUser = isAdmin();
        
        AgentTemplate template = agentTemplateRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Template no encontrado"));
        
        // Verificar permisos: ADMIN o propietario del template
        if (!isAdminUser && !template.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("No tiene permisos para actualizar este template");
        }
        
        template.setName(request.getName());
        template.setDescription(request.getDescription());
        template.setSystemPrompt(request.getSystemPrompt());
        template.setKnowledgeBaseId(request.getKnowledgeBaseId());
        template.setKnowledgeBaseName(request.getKnowledgeBaseName());
        template.setIsActive(request.getIsActive() != null ? request.getIsActive() : template.getIsActive());
        
        AgentTemplate updated = agentTemplateRepository.save(template);
        
        System.out.println("✅ AgentTemplateService: Template actualizado - ID: " + id);
        
        return new AgentTemplateResponse(updated);
    }
    
    /**
     * Eliminar un template
     * ADMIN puede eliminar cualquier template, usuarios normales solo los suyos
     */
    @Transactional
    public void deleteTemplate(UUID id) {
        User user = getCurrentUser();
        boolean isAdminUser = isAdmin();
        
        AgentTemplate template = agentTemplateRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Template no encontrado"));
        
        // Verificar permisos: ADMIN o propietario del template
        if (!isAdminUser && !template.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("No tiene permisos para eliminar este template");
        }
        
        agentTemplateRepository.delete(template);
        
        System.out.println("✅ AgentTemplateService: Template eliminado - ID: " + id);
    }
    
    /**
     * Incrementar el contador de uso de un template
     */
    @Transactional
    public void incrementUsageCount(UUID id) {
        AgentTemplate template = agentTemplateRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Template no encontrado"));
        
        template.incrementUsageCount();
        agentTemplateRepository.save(template);
    }
}
