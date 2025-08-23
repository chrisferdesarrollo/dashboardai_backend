package com.dashboardai.controller;

import com.dashboardai.model.ERole;
import com.dashboardai.model.Role;
import com.dashboardai.model.User;
import com.dashboardai.repository.RoleRepository;
import com.dashboardai.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Set;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/debug")
public class DebugController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @PostMapping("/create-test-user")
    public ResponseEntity<?> createTestUser() {
        try {
            System.out.println("🔧 Debug: Creando usuario de prueba...");
            
            // Verificar si ya existe
            if (userRepository.existsByUsername("testuser")) {
                User existingUser = userRepository.findByUsername("testuser").orElse(null);
                if (existingUser != null) {
                    userRepository.delete(existingUser);
                    System.out.println("🔧 Debug: Usuario existente eliminado");
                }
            }

            // Crear nuevo usuario
            User user = new User("testuser", "test@test.com", encoder.encode("123456"));
            
            // Asignar rol USER
            Set<Role> roles = new HashSet<>();
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
            user.setRoles(roles);

            userRepository.save(user);

            System.out.println("✅ Debug: Usuario de prueba creado:");
            System.out.println("   Username: testuser");
            System.out.println("   Password: 123456");
            System.out.println("   Email: test@test.com");

            return ResponseEntity.ok().body("Usuario de prueba creado: testuser / 123456");
            
        } catch (Exception e) {
            System.err.println("❌ Debug: Error creando usuario: " + e.getMessage());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/list-users")
    public ResponseEntity<?> listUsers() {
        try {
            var users = userRepository.findAll();
            System.out.println("🔧 Debug: Usuarios en la base de datos:");
            
            users.forEach(user -> {
                System.out.println("   ID: " + user.getId() + 
                                 ", Username: '" + user.getUsername() + 
                                 "', Email: '" + user.getEmail() + "'");
            });
            
            return ResponseEntity.ok().body("Ver logs del backend para la lista de usuarios");
            
        } catch (Exception e) {
            System.err.println("❌ Debug: Error listando usuarios: " + e.getMessage());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}
