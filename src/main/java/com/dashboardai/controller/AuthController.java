package com.dashboardai.controller;

import com.dashboardai.dto.request.EmailVerificationRequest;
import com.dashboardai.dto.request.LoginRequest;
import com.dashboardai.dto.request.ResendVerificationRequest;
import com.dashboardai.dto.request.SignupRequest;
import com.dashboardai.dto.response.EmailVerificationResponse;
import com.dashboardai.dto.response.JwtResponse;
import com.dashboardai.dto.response.MessageResponse;
import com.dashboardai.dto.response.SignupResponse;
import com.dashboardai.model.ERole;
import com.dashboardai.model.Role;
import com.dashboardai.model.User;
import com.dashboardai.repository.RoleRepository;
import com.dashboardai.repository.UserRepository;
import com.dashboardai.security.jwt.JwtUtils;
import com.dashboardai.security.services.UserDetailsImpl;
import com.dashboardai.service.EmailVerificationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    EmailVerificationService emailVerificationService;

    @Value("${app.testing.skip-email-verification:false}")
    private boolean skipEmailVerification;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        
        System.out.println("🔵 AuthController: Request recibido");
        System.out.println("🔵 AuthController: Username: '" + loginRequest.getUsername() + "'");
        System.out.println("🔵 AuthController: Password length: " + (loginRequest.getPassword() != null ? loginRequest.getPassword().length() : "null"));
        
        // Verificar si el usuario existe y si su email está verificado
        Optional<User> userOpt = userRepository.findByUsername(loginRequest.getUsername());
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (!user.getEmailVerified()) {
                return ResponseEntity.status(403)
                    .body(new MessageResponse("Debe verificar su email antes de iniciar sesión"));
            }
        }
        
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

        System.out.println("✅ AuthController: Login exitoso para usuario: " + userDetails.getUsername());
        
        return ResponseEntity.ok(new JwtResponse(jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: El nombre de usuario ya está en uso!"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Error: El email ya está en uso!"));
        }

        // Create new user's account
        User user = new User(signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()));

        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);
                        break;
                    case "mod":
                        Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(modRole);
                        break;
                    default:
                        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                }
            });
        }

        user.setRoles(roles);
        user.setEmailVerified(skipEmailVerification); // Verificado automáticamente si está en modo testing
        User savedUser = userRepository.save(user);

        if (skipEmailVerification) {
            return ResponseEntity.ok(new SignupResponse(
                "Usuario registrado exitosamente. [MODO TESTING - Email auto-verificado]", 
                false, 
                null
            ));
        }

        // Crear y enviar token de verificación
        try {
            emailVerificationService.createVerificationToken(savedUser);
            return ResponseEntity.ok(new SignupResponse(
                "Usuario registrado exitosamente. Revisa tu email para verificar tu cuenta.", 
                true, 
                savedUser.getEmail()
            ));
        } catch (Exception e) {
            // Si falla el envío del email, aún así permitir el registro
            return ResponseEntity.ok(new SignupResponse(
                "Usuario registrado exitosamente, pero hubo un problema al enviar el email de verificación. Contacta al administrador.", 
                false, 
                null
            ));
        }
    }

    @PostMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@Valid @RequestBody EmailVerificationRequest request) {
        boolean verified = emailVerificationService.verifyEmail(request.getToken());
        
        if (verified) {
            return ResponseEntity.ok(new EmailVerificationResponse(
                "Email verificado exitosamente. Ya puedes iniciar sesión.", 
                true
            ));
        } else {
            return ResponseEntity.badRequest().body(new EmailVerificationResponse(
                "Token de verificación inválido o expirado.", 
                false
            ));
        }
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<?> resendVerification(@Valid @RequestBody ResendVerificationRequest request) {
        boolean sent = emailVerificationService.resendVerificationEmail(request.getEmail());
        
        if (sent) {
            return ResponseEntity.ok(new MessageResponse(
                "Email de verificación reenviado exitosamente."
            ));
        } else {
            return ResponseEntity.badRequest().body(new MessageResponse(
                "No se pudo reenviar el email. Verifica que el email sea correcto y que no esté ya verificado."
            ));
        }
    }

    @PostMapping("/demo")
    public ResponseEntity<?> demoLogin() {
        System.out.println("🔵 AuthController: Demo login request recibido");
        
        try {
            // Autenticar al usuario demo usando el sistema de autenticación normal
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken("demo", "demo123"));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            List<String> roles = userDetails.getAuthorities().stream()
                .map(item -> item.getAuthority())
                .collect(Collectors.toList());

            System.out.println("✅ AuthController: Demo login exitoso para usuario: " + userDetails.getUsername());
            
            return ResponseEntity.ok(new JwtResponse(
                jwt,
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles
            ));
        } catch (Exception e) {
            System.err.println("❌ AuthController: Error en demo login: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                .body(new MessageResponse("Error al generar sesión demo: " + e.getMessage()));
        }
    }
}
