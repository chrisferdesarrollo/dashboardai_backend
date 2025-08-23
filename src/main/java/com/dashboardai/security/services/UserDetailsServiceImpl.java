package com.dashboardai.security.services;

import com.dashboardai.model.User;
import com.dashboardai.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("🔵 UserDetailsService: Buscando usuario: '" + username + "'");
        
        User user = userRepository.findByUsernameOrEmail(username, username)
                .orElseThrow(() -> {
                    System.err.println("❌ UserDetailsService: Usuario no encontrado: " + username);
                    return new UsernameNotFoundException("User Not Found: " + username);
                });
        
        System.out.println("✅ UserDetailsService: Usuario encontrado: " + user.getUsername() + " (ID: " + user.getId() + ")");
        return UserDetailsImpl.build(user);
    }
}
