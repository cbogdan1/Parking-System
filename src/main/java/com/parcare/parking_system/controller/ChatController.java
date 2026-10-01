package com.parcare.parking_system.controller;

import com.parcare.parking_system.dto.ChatMessageDTO;
import com.parcare.parking_system.dto.UserDTO;
import com.parcare.parking_system.mapper.UserMapper;
import com.parcare.parking_system.model.User;
import com.parcare.parking_system.repository.UserRepository;
import com.parcare.parking_system.service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "http://localhost:3000")
public class ChatController {

    private final ChatService chatService;
    private final UserRepository userRepository;

    public ChatController(ChatService chatService, UserRepository userRepository) {
        this.chatService = chatService;
        this.userRepository = userRepository;
    }

    // Preluam istoricul dintre 2 utilizatori
    @GetMapping("/history/{user1Id}/{user2Id}")
    public ResponseEntity<List<ChatMessageDTO>> getHistory(@PathVariable Long user1Id, @PathVariable Long user2Id) {
        return ResponseEntity.ok(chatService.getConversationHistory(user1Id, user2Id));
    }

    // Trimitem un mesaj (REST + WebSocket broadcast)
    @PostMapping("/send")
    public ResponseEntity<ChatMessageDTO> sendMessage(@RequestBody ChatMessageDTO messageDTO) {
        ChatMessageDTO sentMsg = chatService.sendMessage(
                messageDTO.getSenderId(), 
                messageDTO.getReceiverId(), 
                messageDTO.getContent()
        );
        return ResponseEntity.ok(sentMsg);
    }

    // Returnam ID-ul Administratorului (pentru ca clientul sa stie cui sa scrie)
    @GetMapping("/admin")
    public ResponseEntity<UserDTO> getAdmin() {
        List<User> admins = userRepository.findByRole("ROLE_ADMIN");
        if (admins.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(UserMapper.toDTO(admins.get(0)));
    }

    // Returnam lista de Clienti (pentru ca admin-ul sa poata alege cu cine vorbeste)
    @GetMapping("/clients")
    public ResponseEntity<List<UserDTO>> getClients() {
        List<UserDTO> clients = userRepository.findByRole("ROLE_CLIENT").stream()
                .map(UserMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(clients);
    }
}
