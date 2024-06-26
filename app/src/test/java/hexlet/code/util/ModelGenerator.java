package hexlet.code.util;

import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import net.datafaker.Faker;
import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Getter
@Component
public class ModelGenerator {

    private Model<User> userModel;
    private Model<TaskStatus> taskStatusModel;

    @Autowired
    private Faker faker;

    @PostConstruct
    private void init() {
        userModel = Instancio.of(User.class)
                .supply(Select.field("email"), () -> faker.internet().emailAddress())
                .supply(Select.field("firstName"), () -> faker.name().firstName())
                .supply(Select.field("lastName"), () -> faker.name().lastName())
                .supply(Select.field("passwordDigest"), () -> faker.internet().password())
                .toModel();

        taskStatusModel = Instancio.of(TaskStatus.class)
                .supply(Select.field("name"), () -> faker.name().firstName())
                .supply(Select.field("slug"), () -> faker.internet().domainWord().toLowerCase().replace("-", "_"))
                .toModel();
    }
}
