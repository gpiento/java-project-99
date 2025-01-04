package hexlet.code.component;

import hexlet.code.dto.label.LabelCreateDTO;
import hexlet.code.dto.taskstatus.TaskStatusCreateDTO;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.service.CustomUserDetailsService;
import hexlet.code.service.LabelService;
import hexlet.code.service.TaskStatusService;
import lombok.AllArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
@AllArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final CustomUserDetailsService customUserDetailsService;

    private final TaskStatusRepository taskStatusRepository;

    private final LabelRepository labelRepository;

    private final UserRepository userRepository;

    private final TaskStatusService taskStatusService;

    private final LabelService labelService;

    private final DefaultUserProperties defaultUserProperties;

    @Override
    public void run(ApplicationArguments args) {
        if (!userRepository.existsByEmail("hexlet@example.com")) {
            User user = new User();
            user.setEmail("hexlet@example.com");
            user.setPasswordDigest("qwerty");
            customUserDetailsService.createUser(user);
        }

        Stream.of(
                        new TaskStatusCreateDTO("Draft", "draft"),
                        new TaskStatusCreateDTO("To Review", "to_review"),
                        new TaskStatusCreateDTO("To Be Fixed", "to_be_fixed"),
                        new TaskStatusCreateDTO("To Publish", "to_publish"),
                        new TaskStatusCreateDTO("Published", "published")
                )
                .filter(taskStatus -> !taskStatusRepository.existsBySlug(taskStatus.getSlug()))
                .forEach(taskStatusService::create);

        Stream.of("bug", "feature")
                .map(LabelCreateDTO::new)
                .filter(label -> !labelRepository.existsByName(label.getName()))
                .forEach(labelService::create);
    }
}
