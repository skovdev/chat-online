package local.chatonline.auth.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
public class Address {
    String id;
    String country;
    String city;
    String zipCode;
    String streetName;
    int buildingNumber;
}
