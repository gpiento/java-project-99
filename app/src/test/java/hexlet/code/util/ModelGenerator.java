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
    private Model<TaskStatusCreateDTO> taskStatusCreateDTOModel;
    private Model<TaskStatusUpdateDTO> taskStatusUpdateDTOModel;
    private Model<Task> taskModel;
    private Model<TaskCreateDTO> taskCreateDTOModel;
    private Model<TaskUpdateDTO> taskUpdateDTOModel;
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
                .supply(field(TaskStatus::getName), () -> faker.word().adverb().toLowerCase())
                .supply(field(TaskStatus::getSlug), () -> faker.lorem()
                        .sentence(1)
                        .toLowerCase()
                        .replace(" ", "_")
                        .replaceAll("[^a-z_\\s]", ""))
                .ignore(field(TaskStatus::getCreatedAt))
                .toModel();

        taskStatusCreateDTOModel = Instancio.of(TaskStatusCreateDTO.class)
                .supply(field(TaskStatusCreateDTO::getName), () -> faker.text()
                        .text(3, 7, true))
                .supply(field(TaskStatusCreateDTO::getSlug), () -> faker.lorem()
                        .sentence(3)
                        .toLowerCase()
                        .replace(" ", "_")
                        .replace("-", "_"))
                .toModel();

        taskStatusUpdateDTOModel = Instancio.of(TaskStatusUpdateDTO.class)
                .supply(field(TaskStatusUpdateDTO::getName), () -> JsonNullable.of(faker.text()
                        .text(3, 7, true)))
                .supply(field(TaskStatusUpdateDTO::getSlug), () -> JsonNullable.of(faker.lorem()
                        .sentence(3)
                        .toLowerCase()
                        .replace(" ", "_")
                        .replace("-", "_")))
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
                .supply(field(TaskCreateDTO::getTitle), () -> faker.text().text(3, 7))
                .supply(field(TaskCreateDTO::getIndex), () -> faker.number().numberBetween(1, 100))
                .supply(field(TaskCreateDTO::getContent), () -> faker.text().text(25))
                .ignore(field(TaskCreateDTO::getStatus))
                .ignore(field(TaskCreateDTO::getAssigneeId))
                .ignore(field(TaskCreateDTO::getTaskLabelIds))
                .toModel();

        taskUpdateDTOModel = Instancio.of(TaskUpdateDTO.class)
                .supply(field(TaskUpdateDTO::getTitle), () -> faker.text().text(3, 7))
                .supply(field(TaskUpdateDTO::getIndex), () -> faker.number().numberBetween(1, 100))
                .supply(field(TaskUpdateDTO::getContent), () -> faker.text().text(25))
                .ignore(field(TaskUpdateDTO::getStatus))
                .ignore(field(TaskUpdateDTO::getAssigneeId))
                .ignore(field(TaskUpdateDTO::getTaskLabelIds))
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
