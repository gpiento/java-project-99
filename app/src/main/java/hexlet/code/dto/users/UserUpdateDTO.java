package hexlet.code.dto.users;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

@Getter
@Setter
public class UserUpdateDTO {

    @Email
    @NotNull
    private JsonNullable<String> email;

    @NotNull
    private JsonNullable<String> firstName;

    @NotNull
    private JsonNullable<String> lastName;

    @Size(min = 3)
    @NotNull
    private JsonNullable<String> password;
}
