package com.parcare.parking_system.config;

import com.parcare.parking_system.model.User;
import com.parcare.parking_system.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Componenta care ruleaza la pornirea aplicatiei si migreaza
 * parolele vechi (plain text) la format BCrypt.
 *
 * Detecteaza parolele care NU incep cu "$2a$" (prefixul BCrypt)
 * si le hash-uieste automat.
 */
@Component
public class PasswordMigrationRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(PasswordMigrationRunner.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public PasswordMigrationRunner(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        List<User> allUsers = userRepository.findAll();
        int migratedCount = 0;

        for (User user : allUsers) {
            String password = user.getPassword();

            // Verificam daca parola NU este deja hash-uita cu BCrypt
            if (password != null && !password.startsWith("$2a$")) {
                log.info("Migrating plain-text password for user: {}", user.getUsername());
                user.setPassword(passwordEncoder.encode(password));
                userRepository.save(user);
                migratedCount++;
            }
        }

        if (migratedCount > 0) {
            log.info("Password migration complete: {} user(s) updated to BCrypt.", migratedCount);
        } else {
            log.info("Password migration: all passwords are already BCrypt-hashed.");
        }
    }
}
