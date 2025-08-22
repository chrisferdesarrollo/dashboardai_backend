package com.dashboardai.config;

import com.dashboardai.model.ERole;
import com.dashboardai.model.Role;
import com.dashboardai.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        // Crear roles por defecto si no existen
        if (roleRepository.findByName(ERole.ROLE_USER).isEmpty()) {
            Role userRole = new Role(ERole.ROLE_USER);
            roleRepository.save(userRole);
        }

        if (roleRepository.findByName(ERole.ROLE_MODERATOR).isEmpty()) {
            Role modRole = new Role(ERole.ROLE_MODERATOR);
            roleRepository.save(modRole);
        }

        if (roleRepository.findByName(ERole.ROLE_ADMIN).isEmpty()) {
            Role adminRole = new Role(ERole.ROLE_ADMIN);
            roleRepository.save(adminRole);
        }
    }
}
