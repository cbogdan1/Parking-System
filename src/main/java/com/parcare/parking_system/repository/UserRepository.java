package com.parcare.parking_system.repository;

import com.parcare.parking_system.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Spring va genera: SELECT * FROM users WHERE username = ?
    Optional<User> findByUsername(String username);

    // Verifica daca exista deja un email
    boolean existsByEmail(String email);

    // Gaseste toti utilizatorii dupa rol
    List<User> findByRole(String role);
}