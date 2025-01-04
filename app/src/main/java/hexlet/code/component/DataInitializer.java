package hexlet.code.component;

import hexlet.code.dto.label.LabelCreateDTO;
import hexlet.code.dto.taskstatus.TaskStatusCreateDTO;
import hexlet.code.dto.user.UserCreateDTO;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.service.LabelService;
import hexlet.code.service.TaskStatusService;
import hexlet.code.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@AllArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private static final Map<String, String> TASK_STATUSES = Map.of(
            "draft", "Draft",
            "to_review", "To Review",
            "to_be_fixed", "To Be Fixed",
            "to_publish", "To Publish",
            "published", "Published"
    );

    private static final List<String> LABEL_NAMES = List.of(
            "bug",
            "feature");

    private final TaskStatusRepository taskStatusRepository;

    private final LabelRepository labelRepository;

    private final UserRepository userRepository;

    private final TaskStatusService taskStatusService;

    private final LabelService labelService;

//    private final DefaultUserProperties defaultUserProperties;

    private final UserService userService;

    @Override
    public void run(ApplicationArguments args) {

        createUserIfNotExists();
        createTaskStatusesIfNotExists();
        createLabelsIfNotExists();
    }

    private void createUserIfNotExists() {
        // TODO try use DefaultUserProperties
        if (userRepository.findByEmail("hexlet@example.com").isEmpty()) {
            UserCreateDTO userCreateDTO = new UserCreateDTO();
            userCreateDTO.setEmail("hexlet@example.com");
            userCreateDTO.setPassword("qwerty");
            userService.create(userCreateDTO);
        }
    }

    private void createTaskStatusesIfNotExists() {
        TASK_STATUSES.entrySet().stream()
                .filter(entry -> taskStatusRepository.findBySlug(entry.getKey()).isEmpty())
                .forEach(entry -> taskStatusService.create(new TaskStatusCreateDTO(entry.getValue(), entry.getKey())));
    }

    private void createLabelsIfNotExists() {
        LABEL_NAMES.stream()
                .filter(name -> labelRepository.findByName(name).isEmpty())
                .forEach(name -> labelService.create(new LabelCreateDTO(name)));
    }
}
