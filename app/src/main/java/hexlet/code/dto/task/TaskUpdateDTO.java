package hexlet.code.dto.task;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

import java.util.Set;

@Getter
@Setter
public class TaskUpdateDTO {

    @Size(min = 1, message = "Must be at least 1 character long")
    @NotBlank
    private JsonNullable<String> title;

    private JsonNullable<String> content;

    private JsonNullable<Integer> index;

    private JsonNullable<String> status;

    private JsonNullable<Set<Long>> taskLabelIds;

    @JsonProperty("assignee_id")
    private JsonNullable<Long> assigneeId;
}
