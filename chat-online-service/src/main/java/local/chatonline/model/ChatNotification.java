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
public class ChatNotification {
    String id;
    String senderId;
    String senderName;
}
