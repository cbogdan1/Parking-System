package com.parcare.parking_system.controller;

import com.parcare.parking_system.dto.UserDTO;
import com.parcare.parking_system.mapper.UserMapper;
import com.parcare.parking_system.model.User;
import com.parcare.parking_system.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*") // Permite apelurile dinspre React
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User loginRequest) {
        Optional<User> userOpt = userRepository.findByUsername(loginRequest.getUsername());

        // Verificam parola folosind BCrypt matches() in loc de equals()
        if (userOpt.isPresent() && passwordEncoder.matches(loginRequest.getPassword(), userOpt.get().getPassword())) {
            // Logare cu succes -> returnam DTO-ul, FARA parola
            UserDTO userResponse = UserMapper.toDTO(userOpt.get());
            return ResponseEntity.ok(userResponse);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Username sau parola incorecte!");
    }

    @PostMapping({"/register", "/signup"})
    public ResponseEntity<?> register(@RequestBody User newUser) {
        // Validari baza de date
        if (userRepository.findByUsername(newUser.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Username-ul exista deja!");
        }

        if (userRepository.existsByEmail(newUser.getEmail())) {
            return ResponseEntity.badRequest().body("Acest email este deja folosit!");
        }

        // Setam rolul implicit pentru un cont nou
        newUser.setRole("ROLE_CLIENT");

        // Criptam parola cu BCrypt inainte de a salva in baza de date
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));

        // Salvam in baza de date
        User savedUser = userRepository.save(newUser);

        return ResponseEntity.ok(UserMapper.toDTO(savedUser));
    }
}