package com.dashboardai.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.nio.file.Paths;

@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(@NonNull ResourceHandlerRegistry registry) {
        // Configurar para servir archivos de avatares
        // Usar ruta absoluta basada en el directorio actual
        String uploadPath = Paths.get("uploads/avatars/").toAbsolutePath().toUri().toString();
        
        registry.addResourceHandler("/uploads/avatars/**")
                .addResourceLocations(uploadPath);
                
        System.out.println("✅ Configuración de recursos estáticos - Avatar path: " + uploadPath);
    }
}