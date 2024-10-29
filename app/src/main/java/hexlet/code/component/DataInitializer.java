package hexlet.code.component;

import hexlet.code.dto.user.UserCreateDTO;
import hexlet.code.model.Label;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.service.CustomUserDetailsService;
import hexlet.code.service.LabelService;
import hexlet.code.service.TaskStatusService;
import hexlet.code.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@AllArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private static final String ADMIN_EMAIL = "hexlet@example.com";

    @Autowired
    private CustomUserDetailsService customUserDetailsService;
    @Autowired
    private TaskStatusRepository taskStatusRepository;
    @Autowired
    private LabelRepository labelRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private TaskStatusService taskStatusService;
    @Autowired
    private LabelService labelService;

    @Override
    public void run(ApplicationArguments args) {
        if (!userRepository.existsByEmail(ADMIN_EMAIL)) {
            UserCreateDTO userCreateDTO = new UserCreateDTO();
            userCreateDTO.setEmail(ADMIN_EMAIL);
            userCreateDTO.setFirstName("Admin");
            userCreateDTO.setLastName("Rutovich");
            userCreateDTO.setPassword("qwerty");
            userService.createUser(userCreateDTO);
        }

        List<TaskStatus> defaultTaskStatuses = Arrays.asList(
                new TaskStatus("Draft", "draft"),
                new TaskStatus("To Review", "to_review"),
                new TaskStatus("To Be Fixed", "to_be_fixed"),
                new TaskStatus("To Publish", "to_publish"),
                new TaskStatus("Published", "published")
        );

        for (TaskStatus taskStatus : defaultTaskStatuses) {
            if (!taskStatusRepository.existsBySlug(taskStatus.getSlug())) {
                taskStatusRepository.save(taskStatus);
            }
        }

        List<Label> defaultLabels = Arrays.asList(
                new Label("bug"),
                new Label("feature")
        );

        for (Label defaultLabelName : defaultLabels) {
            if (!labelRepository.existsByName(defaultLabelName.getName())) {
                labelRepository.save(defaultLabelName);
            }
        }
    }
}
