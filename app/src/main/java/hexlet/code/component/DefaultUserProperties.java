package hexlet.code.component;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "user")
@Setter
@Getter
public class DefaultUserProperties {
    private String email;
    private String password;
}
