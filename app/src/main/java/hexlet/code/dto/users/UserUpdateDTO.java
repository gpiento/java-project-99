package hexlet.code.dto.users;

import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

@Getter
@Setter
public class UserUpdateDTO {

    @Email
    private JsonNullable<String> email;

    private JsonNullable<String> firstName;

    private JsonNullable<String> lastName;

    private JsonNullable<String> passwordDigest;
}
