package hexlet.code.component;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "admin")
@Setter
@Getter
public class AdminDefaultProperties {

    @Email
    private String email;

    @NotBlank
    private String password;
}
