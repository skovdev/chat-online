package local.chatonline.controller;

import local.chatonline.model.ChatMessage;

import local.chatonline.model.ChatNotification;
import local.chatonline.service.ChatMessageService;

import local.chatonline.service.ChatRoomService;
import lombok.Getter;
import lombok.Setter;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import lombok.experimental.FieldDefaults;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;

import org.springframework.messaging.simp.SimpMessagingTemplate;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class ChatController {

    SimpMessagingTemplate simpMessagingTemplate;
    ChatMessageService chatMessageService;
    ChatRoomService chatRoomService;

    @MessageMapping("/chat")
    public void processMessage(@Payload ChatMessage chatMessage) {

        var chatId = chatRoomService.getChatId(chatMessage.getSenderId(), chatMessage.getRecipientId(), true);

        chatMessage.setChatId(chatId.orElse(null));

        ChatMessage saved = chatMessageService.save(chatMessage);

        simpMessagingTemplate.convertAndSendToUser(
                chatMessage.getRecipientId(),
                "/queue/messages",
                new ChatNotification(
                        saved.getId(),
                        saved.getSenderId(),
                        saved.getSenderName()
                )
        );
    }
}