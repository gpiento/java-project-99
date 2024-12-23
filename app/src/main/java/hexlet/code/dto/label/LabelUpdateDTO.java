package hexlet.code.dto.label;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

@Getter
@Setter
public class LabelUpdateDTO {

    @Size(min = 3, max = 1000, message = "Must be between 3 and 1000 characters")
    private JsonNullable<String> name;
}
