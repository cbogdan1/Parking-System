package com.parcare.parking_system.mapper;

import com.parcare.parking_system.dto.UserDTO;
import com.parcare.parking_system.model.User;

public class UserMapper {

    // Transforma un obiect din Baza de Date intr-un obiect sigur pentru Frontend
    public static UserDTO toDTO(User user) {
        if (user == null) {
            return null;
        }

        return UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
}