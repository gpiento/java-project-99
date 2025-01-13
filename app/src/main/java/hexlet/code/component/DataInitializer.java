package hexlet.code.component;

import hexlet.code.dto.user.UserCreateDTO;
import hexlet.code.model.Label;
import hexlet.code.model.TaskStatus;
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

    private static final String DEFAULT_USER = "hexlet@example.com";

    private final TaskStatusRepository taskStatusRepository;

    private final LabelRepository labelRepository;

    private final UserRepository userRepository;

    private final TaskStatusService taskStatusService;

    private final LabelService labelService;

//    private final AdminDefaultProperties adminDefaultProperties;

    private final UserService userService;

    @Override
    public void run(ApplicationArguments args) {

        createUserIfNotExists();
        createTaskStatusesIfNotExists();
        createLabelsIfNotExists();
    }

    private void createUserIfNotExists() {
        // TODO try use DefaultUserProperties
        if (!userRepository.existsByEmail(DEFAULT_USER)) {
            UserCreateDTO userCreateDTO = new UserCreateDTO();
            userCreateDTO.setEmail(DEFAULT_USER);
            userCreateDTO.setPassword("qwerty");
            userCreateDTO.setFirstName("Admin");
            userCreateDTO.setLastName("Adminych");
            userService.create(userCreateDTO);
        }
    }

    private void createTaskStatusesIfNotExists() {
        List<TaskStatus> taskStatusList = TASK_STATUSES.entrySet().stream()
                .filter(entry -> !taskStatusRepository.existsBySlug(entry.getKey()))
                .map(entry -> new TaskStatus(entry.getValue(), entry.getKey()))
                .toList();
        taskStatusRepository.saveAll(taskStatusList);
    }

    private void createLabelsIfNotExists() {
        List<Label> labelList = LABEL_NAMES.stream()
                .filter(name -> !labelRepository.existsByName(name))
                .map(Label::new)
                .toList();
        labelRepository.saveAll(labelList);
    }
}
