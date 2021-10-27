package local.chatonline.auth.model.http.response;

import lombok.Data;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
public class ApiResponse {
    Boolean success;
    String message;
}