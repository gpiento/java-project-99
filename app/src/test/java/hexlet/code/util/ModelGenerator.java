package hexlet.code.util;

import hexlet.code.dto.label.LabelCreateDTO;
import hexlet.code.dto.label.LabelUpdateDTO;
import hexlet.code.dto.user.UserCreateDTO;
import hexlet.code.dto.user.UserUpdateDTO;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import net.datafaker.Faker;
import org.instancio.Instancio;
import org.instancio.Model;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.instancio.Select.field;

@Getter
@Component
public class ModelGenerator {

    private Model<User> userModel;
    private Model<UserCreateDTO> userCreateDTOModel;
    private Model<UserUpdateDTO> userUpdateDTOModel;
    private Model<TaskStatus> taskStatusModel;
    private Model<Task> taskModel;
    private Model<Label> labelModel;
    private Model<LabelCreateDTO> labelCreateDTOModel;
    private Model<LabelUpdateDTO> labelUpdateDTOModel;

    @Autowired
    private Faker faker;

    @PostConstruct
    private void init() {
        userModel = Instancio.of(User.class)
                .ignore(field(User::getId))
                .supply(field(User::getFirstName), () -> faker.name().firstName())
                .supply(field(User::getLastName), () -> faker.name().lastName())
                .supply(field(User::getEmail), () -> faker.internet().emailAddress())
                .supply(field(User::getPasswordDigest), () -> faker.internet().password())
                .ignore(field(User::getCreatedAt))
                .ignore(field(User::getUpdatedAt))
                .ignore(field(User::getTasks))
                .toModel();

        userCreateDTOModel = Instancio.of(UserCreateDTO.class)
                .supply(field(UserCreateDTO::getEmail), () -> faker.internet().emailAddress())
                .supply(field(UserCreateDTO::getFirstName), () -> faker.name().firstName())
                .supply(field(UserCreateDTO::getLastName), () -> faker.name().lastName())
                .supply(field(UserCreateDTO::getPassword), () -> faker.internet().password())
                .toModel();

        userUpdateDTOModel = Instancio.of(UserUpdateDTO.class)
                .supply(field(UserUpdateDTO::getEmail), () -> JsonNullable.of(faker.internet().emailAddress()))
                .supply(field(UserUpdateDTO::getFirstName), () -> JsonNullable.of(faker.name().firstName()))
                .supply(field(UserUpdateDTO::getLastName), () -> JsonNullable.of(faker.name().lastName()))
                .supply(field(UserUpdateDTO::getPassword), () -> JsonNullable.of(faker.internet().password()))
                .toModel();

        taskStatusModel = Instancio.of(TaskStatus.class)
                .ignore(field(TaskStatus::getId))
                .supply(field(TaskStatus::getName), () -> faker.text()
                        .text(3, 7, true))
                .supply(field(TaskStatus::getSlug), () -> faker.lorem()
                        .sentence(2)
                        .toLowerCase()
                        .replace(" ", "_")
                        .replace("-", "_"))
                .ignore(field(TaskStatus::getCreatedAt))
                .toModel();

        taskModel = Instancio.of(Task.class)
                .ignore(field(Task::getId))
                .supply(field(Task::getIndex), () -> faker.number().numberBetween(1, 100))
                .ignore(field(Task::getAssignee))
                .ignore(field(Task::getTaskStatus))
                .ignore(field(Task::getLabels))
                .supply(field(Task::getName), () -> faker.text()
                        .text(3, 7, true))
                .supply(field(Task::getDescription), () -> faker.text().text(25))
                .ignore(field(Task::getCreatedAt))
                .toModel();

        labelModel = Instancio.of(Label.class)
                .ignore(field(Label::getId))
                .supply(field(Label::getName), () -> faker.lorem()
                        .sentence(1, 3)
                        .toLowerCase())
                .ignore(field(Label::getCreatedAt))
                .toModel();

        labelCreateDTOModel = Instancio.of(LabelCreateDTO.class)
                .supply(field(LabelCreateDTO::getName), () -> faker.lorem()
                        .sentence(1, 3)
                        .toLowerCase())
                .toModel();

        labelUpdateDTOModel = Instancio.of(LabelUpdateDTO.class)
                .supply(field(LabelUpdateDTO::getName), () -> JsonNullable.of(faker.lorem()
                        .sentence(1, 3)
                        .toLowerCase()))
                .toModel();
    }
}
