package local.chatonline.auth.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserSummary {
    String id;
    String username;
    String name;
    String profilePicture;
}