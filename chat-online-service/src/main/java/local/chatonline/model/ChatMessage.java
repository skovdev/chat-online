package local.chatonline.model;

import local.chatonline.common.type.MessageStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class ChatMessage {
    String id;
    String chatId;
    String senderId;
    String recipientId;
    String senderName;
    String recipientName;
    String content;
    LocalDateTime dateTime;
    MessageStatus messageStatus;
}
