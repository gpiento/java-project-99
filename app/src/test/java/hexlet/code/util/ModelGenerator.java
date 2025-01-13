package hexlet.code.util;

import hexlet.code.dto.label.LabelCreateDTO;
import hexlet.code.dto.label.LabelUpdateDTO;
import hexlet.code.dto.task.TaskCreateDTO;
import hexlet.code.dto.task.TaskUpdateDTO;
import hexlet.code.dto.taskstatus.TaskStatusCreateDTO;
import hexlet.code.dto.taskstatus.TaskStatusUpdateDTO;
import hexlet.code.dto.user.UserCreateDTO;
import hexlet.code.dto.user.UserUpdateDTO;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import net.datafaker.Faker;
import net.datafaker.providers.base.Text;
import org.instancio.Instancio;
import org.instancio.Model;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.stereotype.Component;

import static net.datafaker.providers.base.Text.DIGITS;
import static net.datafaker.providers.base.Text.EN_LOWERCASE;
import static net.datafaker.providers.base.Text.EN_UPPERCASE;
import static org.instancio.Select.field;

@Getter
@Component
public class ModelGenerator {

    private Model<User> userModel;

    private Model<UserCreateDTO> userCreateDTOModel;

    private Model<UserUpdateDTO> userUpdateDTOModel;

    private Model<TaskStatus> taskStatusModel;

    private Model<TaskStatusCreateDTO> taskStatusCreateDTOModel;

    private Model<TaskStatusUpdateDTO> taskStatusUpdateDTOModel;

    private Model<Task> taskModel;

    private Model<TaskCreateDTO> taskCreateDTOModel;

    private Model<TaskUpdateDTO> taskUpdateDTOModel;

    private Model<Label> labelModel;

    private Model<LabelCreateDTO> labelCreateDTOModel;

    private Model<LabelUpdateDTO> labelUpdateDTOModel;

    private static String getName(Faker faker) {
        return faker.lorem()
                .sentence(3, 1);
    }

    private static String getSlug(Faker faker) {
        return faker.lorem()
                .sentence(2, 1)
                .toLowerCase()
                .replace(" ", "_")
                .replaceAll("[^a-z_\\s]", "");
    }

    private static String getPassword(Faker faker) {
        return faker.text().text(Text.TextSymbolsBuilder.builder()
                .len(8)
                .with(EN_LOWERCASE, 1)
                .with(EN_UPPERCASE, 1)
                .with(DIGITS, 1)
                .build());
    }

    @PostConstruct
    private void init() {

        Faker faker = new Faker();

        userModel = Instancio.of(User.class)
                .ignore(field(User::getId))
                .supply(field(User::getFirstName), () -> faker.name().firstName())
                .supply(field(User::getLastName), () -> faker.name().lastName())
                .supply(field(User::getEmail), () -> faker.internet().emailAddress())
                .supply(field(User::getPasswordDigest), () -> getPassword(faker))
                .ignore(field(User::getCreatedAt))
                .ignore(field(User::getUpdatedAt))
                .toModel();

        userCreateDTOModel = Instancio.of(UserCreateDTO.class)
                .supply(field(UserCreateDTO::getEmail), () -> faker.internet().emailAddress())
                .supply(field(UserCreateDTO::getFirstName), () -> faker.name().firstName())
                .supply(field(UserCreateDTO::getLastName), () -> faker.name().lastName())
                .supply(field(UserCreateDTO::getPassword), () -> getPassword(faker))
                .toModel();

        userUpdateDTOModel = Instancio.of(UserUpdateDTO.class)
                .supply(field(UserUpdateDTO::getEmail), () -> JsonNullable.of(faker.internet().emailAddress()))
                .supply(field(UserUpdateDTO::getFirstName), () -> JsonNullable.of(faker.name().firstName()))
                .supply(field(UserUpdateDTO::getLastName), () -> JsonNullable.of(faker.name().lastName()))
                .supply(field(UserUpdateDTO::getPassword), () -> JsonNullable.of(getPassword(faker)))
                .toModel();

        taskStatusModel = Instancio.of(TaskStatus.class)
                .ignore(field(TaskStatus::getId))
                .supply(field(TaskStatus::getName), () -> getName(faker))
                .supply(field(TaskStatus::getSlug), () -> getSlug(faker))
                .ignore(field(TaskStatus::getCreatedAt))
                .toModel();

        taskStatusCreateDTOModel = Instancio.of(TaskStatusCreateDTO.class)
                .supply(field(TaskStatusCreateDTO::getName), () -> getName(faker))
                .supply(field(TaskStatusCreateDTO::getSlug), () -> getSlug(faker))
                .toModel();

        taskStatusUpdateDTOModel = Instancio.of(TaskStatusUpdateDTO.class)
                .supply(field(TaskStatusUpdateDTO::getName), () -> JsonNullable.of(getName(faker)))
                .supply(field(TaskStatusUpdateDTO::getSlug), () -> JsonNullable.of(getSlug(faker)))
                .toModel();

        taskModel = Instancio.of(Task.class)
                .ignore(field(Task::getId))
                .supply(field(Task::getName), () -> faker.text().text(3, 7))
                .supply(field(Task::getIndex), () -> faker.number().numberBetween(1, 100))
                .supply(field(Task::getDescription), () -> faker.text().text(25))
                .ignore(field(Task::getTaskStatus))
                .ignore(field(Task::getAssignee))
                .ignore(field(Task::getLabels))
                .ignore(field(Task::getCreatedAt))
                .toModel();

        taskCreateDTOModel = Instancio.of(TaskCreateDTO.class)
                .supply(field(TaskCreateDTO::getTitle), () -> getName(faker))
                .supply(field(TaskCreateDTO::getIndex), () -> JsonNullable.of(faker.number().numberBetween(1, 100)))
                .supply(field(TaskCreateDTO::getContent), () -> faker.text().text(25))
                .ignore(field(TaskCreateDTO::getStatus))
                .ignore(field(TaskCreateDTO::getAssigneeId))
                .ignore(field(TaskCreateDTO::getTaskLabelIds))
                .toModel();

        taskUpdateDTOModel = Instancio.of(TaskUpdateDTO.class)
                .supply(field(TaskUpdateDTO::getTitle), () -> getName(faker))
                .supply(field(TaskUpdateDTO::getIndex), () -> faker.number().numberBetween(1, 100))
                .supply(field(TaskUpdateDTO::getContent), () -> faker.text().text(25))
                .ignore(field(TaskUpdateDTO::getStatus))
                .ignore(field(TaskUpdateDTO::getAssigneeId))
                .ignore(field(TaskUpdateDTO::getTaskLabelIds))
                .toModel();

        labelModel = Instancio.of(Label.class)
                .ignore(field(Label::getId))
                .supply(field(Label::getName), () -> getName(faker))
                .ignore(field(Label::getCreatedAt))
                .toModel();

        labelCreateDTOModel = Instancio.of(LabelCreateDTO.class)
                .supply(field(LabelCreateDTO::getName), () -> getName(faker))
                .toModel();

        labelUpdateDTOModel = Instancio.of(LabelUpdateDTO.class)
                .supply(field(LabelUpdateDTO::getName), () -> JsonNullable.of(getName(faker)))
                .toModel();
    }
}
