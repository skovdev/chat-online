package local.chatonline.service.impl;

import local.chatonline.common.type.MessageStatus;

import local.chatonline.exception.ResourceNotFoundException;

import local.chatonline.model.ChatMessage;

import local.chatonline.repository.ChatMessageRepository;

import local.chatonline.service.ChatMessageService;
import local.chatonline.service.ChatRoomService;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import lombok.experimental.FieldDefaults;

import org.springframework.data.mongodb.core.MongoOperations;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class ChatMessageServiceImpl implements ChatMessageService {

    ChatMessageRepository chatMessageRepository;
    ChatRoomService chatRoomService;
    MongoOperations mongoOperations;

    @Override
    public ChatMessage save(ChatMessage chatMessage) {
        chatMessage.setMessageStatus(MessageStatus.RECEIVED);
        chatMessageRepository.save(chatMessage);
        return chatMessage;
    }

    @Override
    public long countNewMessage(String senderId, String recipientId) {
        return chatMessageRepository.countBySenderIdAndRecipientIdAndMessageStatus(senderId, recipientId, MessageStatus.RECEIVED);
    }

    @Override
    public List<ChatMessage> findChatMessage(String senderId, String recipientId) {

        var chatId = chatRoomService.getChatId(senderId, recipientId, false);

        var messages = chatId.map(cId -> chatMessageRepository.findByChatId(cId))
                .orElse(new ArrayList<>());

        if (messages.size() > 0) {
            updateStatuses(senderId, recipientId, MessageStatus.DELIVERED);
        }

        return messages;

    }

    @Override
    public ChatMessage findById(String id) {
        return chatMessageRepository.findById(id).map(chatMessage -> {
            chatMessage.setMessageStatus(MessageStatus.DELIVERED);
            return chatMessageRepository.save(chatMessage);
        }).orElseThrow(() -> new ResourceNotFoundException(String.format("Can't find message: %s", id)));
    }

    @Override
    public void updateStatuses(String senderId, String recipientId, MessageStatus status) {

        Query query = new Query(
                Criteria.where("senderId").is(senderId)
                        .and("recipientId").is(recipientId)
        );

        Update update = Update.update("status", status);

        mongoOperations.updateMulti(query, update, ChatMessage.class);

    }
}
