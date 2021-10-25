package local.chatonline.auth.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import lombok.experimental.FieldDefaults;

import java.util.Date;
import java.util.Set;


@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class Profile {
    String displayName;
    String profilePictureUrl;
    Date birthday;
    Set<Address> addresses;
}
