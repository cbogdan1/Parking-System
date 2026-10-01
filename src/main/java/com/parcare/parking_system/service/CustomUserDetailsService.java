package com.parcare.parking_system.service;

import com.parcare.parking_system.model.User;
import com.parcare.parking_system.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Implementare UserDetailsService necesara pentru ca Spring Security
 * sa poata valida credentialele HTTP Basic Auth din header.
 *
 * Spring Security apeleaza automat loadUserByUsername() cand primeste
 * un request cu header-ul Authorization: Basic ...
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Utilizatorul '" + username + "' nu a fost gasit in baza de date!"));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword()) // Parola BCrypt hash-uita din DB
                .roles(user.getRole().replace("ROLE_", "")) // "ROLE_ADMIN" -> "ADMIN"
                .build();
    }
}
