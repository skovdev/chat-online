package local.chatonline.auth.model.http.request;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SignUpRequest {
    String name;
    String username;
    String email;
    String password;
    String profilePicUrl;
}