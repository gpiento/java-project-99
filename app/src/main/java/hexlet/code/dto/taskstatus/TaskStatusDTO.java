package hexlet.code.dto.taskstatus;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@JsonPropertyOrder({"id", "name", "slug", "createdAt"})
public class TaskStatusDTO {

    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String slug;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate createdAt;
}
