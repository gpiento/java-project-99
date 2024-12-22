package hexlet.code.dto.taskstatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

@Getter
@Setter
public class TaskStatusUpdateDTO {

    @NotBlank(message = "Name is required")
    @Size(min = 1, message = "Name must be at least 1 character long")
    private JsonNullable<String> name;

    @NotBlank(message = "Slug is required")
    @Size(min = 1, message = "Slug must be at least 1 character long")
    private JsonNullable<String> slug;
}
