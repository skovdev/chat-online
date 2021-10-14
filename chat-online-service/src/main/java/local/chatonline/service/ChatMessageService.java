package local.chatonline.service;

import local.chatonline.common.type.MessageStatus;

import local.chatonline.model.ChatMessage;

import java.util.List;

public interface ChatMessageService {
    ChatMessage save(ChatMessage chatMessage);
    long countNewMessage(String senderId, String recipientId);
    List<ChatMessage> findChatMessage(String senderId, String recipientId);
    ChatMessage findById(String id);
    void updateStatuses(String senderId, String recipientId, MessageStatus status);
}
