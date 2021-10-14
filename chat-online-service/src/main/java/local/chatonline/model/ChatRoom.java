package local.chatonline.model;

import lombok.Getter;
import lombok.Setter;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class ChatRoom {
    String id;
    String chatId;
    String senderId;
    String recipientId;
}