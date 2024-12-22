package hexlet.code.dto.taskstatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TaskStatusCreateDTO {

    @NotBlank(message = "Name is required")
    @Size(min = 1, message = "Name must be at least 1 character long")
    private String name;

    @NotBlank(message = "Slug is required")
    @Size(min = 1, message = "Slug must be at least 1 character long")
    private String slug;
}
