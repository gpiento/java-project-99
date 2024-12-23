package hexlet.code.dto.label;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LabelCreateDTO {

    @Size(min = 3, max = 1000, message = "Must be between 3 and 1000 characters")
    private String name;
}
