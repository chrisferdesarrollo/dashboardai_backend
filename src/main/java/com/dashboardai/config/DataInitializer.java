package com.dashboardai.config;

import com.dashboardai.model.ERole;
import com.dashboardai.model.Role;
import com.dashboardai.model.User;
import com.dashboardai.repository.RoleRepository;
import com.dashboardai.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Crear roles por defecto si no existen
        Role userRole;
        Role modRole;
        Role adminRole;

        if (roleRepository.findByName(ERole.ROLE_USER).isEmpty()) {
            userRole = new Role(ERole.ROLE_USER);
            roleRepository.save(userRole);
            System.out.println("✅ DataInitializer: Rol ROLE_USER creado");
        } else {
            userRole = roleRepository.findByName(ERole.ROLE_USER).get();
        }

        if (roleRepository.findByName(ERole.ROLE_MODERATOR).isEmpty()) {
            modRole = new Role(ERole.ROLE_MODERATOR);
            roleRepository.save(modRole);
            System.out.println("✅ DataInitializer: Rol ROLE_MODERATOR creado");
        } else {
            modRole = roleRepository.findByName(ERole.ROLE_MODERATOR).get();
        }

        if (roleRepository.findByName(ERole.ROLE_ADMIN).isEmpty()) {
            adminRole = new Role(ERole.ROLE_ADMIN);
            roleRepository.save(adminRole);
            System.out.println("✅ DataInitializer: Rol ROLE_ADMIN creado");
        } else {
            adminRole = roleRepository.findByName(ERole.ROLE_ADMIN).get();
        }

        // Crear usuario demo si no existe
        if (userRepository.findByUsername("demo").isEmpty()) {
            User demoUser = new User();
            demoUser.setUsername("demo");
            demoUser.setEmail("demo@topias.app");
            demoUser.setPassword(passwordEncoder.encode("demo123")); // Contraseña demo
            demoUser.setEmailVerified(true); // Email verificado automáticamente
            
            // Asignar rol de usuario
            Set<Role> roles = new HashSet<>();
            roles.add(userRole);
            demoUser.setRoles(roles);
            
            userRepository.save(demoUser);
            System.out.println("✅ DataInitializer: Usuario DEMO creado (username: demo, password: demo123)");
        } else {
            System.out.println("ℹ️ DataInitializer: Usuario DEMO ya existe");
        }
    }
}
