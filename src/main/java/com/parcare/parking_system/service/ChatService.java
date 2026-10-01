package com.parcare.parking_system.service;

import com.parcare.parking_system.dto.ChatMessageDTO;
import com.parcare.parking_system.model.ChatMessage;
import com.parcare.parking_system.model.User;
import com.parcare.parking_system.repository.ChatMessageRepository;
import com.parcare.parking_system.repository.UserRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatService(ChatMessageRepository chatMessageRepository, 
                       UserRepository userRepository, 
                       SimpMessagingTemplate messagingTemplate) {
        this.chatMessageRepository = chatMessageRepository;
        this.userRepository = userRepository;
        this.messagingTemplate = messagingTemplate;
    }

    public List<ChatMessageDTO> getConversationHistory(Long user1Id, Long user2Id) {
        List<ChatMessage> messages = chatMessageRepository.findConversation(user1Id, user2Id);
        return messages.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Transactional
    public ChatMessageDTO sendMessage(Long senderId, Long receiverId, String content) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("Expeditor invalid"));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new IllegalArgumentException("Destinatar invalid"));

        ChatMessage message = new ChatMessage();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(content);
        
        ChatMessage savedMessage = chatMessageRepository.save(message);
        ChatMessageDTO dto = mapToDTO(savedMessage);

        // Expediaza mesajul live catre destinatar (pe topicul lui privat)
        messagingTemplate.convertAndSend("/topic/chat/" + receiverId, dto);

        return dto;
    }

    private ChatMessageDTO mapToDTO(ChatMessage msg) {
        return new ChatMessageDTO(
                msg.getId(),
                msg.getSender().getId(),
                msg.getSender().getUsername(),
                msg.getReceiver().getId(),
                msg.getReceiver().getUsername(),
                msg.getContent(),
                msg.getTimestamp()
        );
    }
}
