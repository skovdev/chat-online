package local.chatonline.service.impl;

import local.chatonline.model.ChatRoom;

import local.chatonline.repository.ChatRoomRepository;

import local.chatonline.service.ChatRoomService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import lombok.experimental.FieldDefaults;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService {

    ChatRoomRepository chatRoomRepository;

    @Transactional
    @Override
    public Optional<String> getChatId(String senderId, String recipientId, boolean createIfNotExist) {
        return chatRoomRepository.findBySenderIdAndRecipientId(senderId, recipientId)
                .map(ChatRoom::getChatId)
                .or(() -> {

                    if (!createIfNotExist) {
                        return Optional.empty();
                    }

                    var chatId = String.format("%s-%s", senderId, recipientId);

                    ChatRoom senderRecipient = new ChatRoom();

                    senderRecipient.setChatId(chatId);
                    senderRecipient.setSenderId(senderId);
                    senderRecipient.setRecipientId(recipientId);

                    ChatRoom recipientSender = new ChatRoom();

                    recipientSender.setChatId(chatId);
                    recipientSender.setSenderId(recipientId);
                    recipientSender.setRecipientId(senderId);

                    chatRoomRepository.save(senderRecipient);
                    chatRoomRepository.save(recipientSender);

                    return Optional.of(chatId);

                });
    }
}
