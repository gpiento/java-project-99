package hexlet.code.component;

import hexlet.code.model.Label;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.service.CustomUserDetailsService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class DataInitializer implements ApplicationRunner {

    private final CustomUserDetailsService customUserDetailsService;
    private final TaskStatusRepository taskStatusRepository;
    private final LabelRepository labelRepository;
    private final UserRepository userRepository;
    private final DefaultUserProperties defaultUser;

    public DataInitializer(CustomUserDetailsService customUserDetailsService,
                           TaskStatusRepository taskStatusRepository,
                           LabelRepository labelRepository,
                           UserRepository userRepository,
                           DefaultUserProperties defaultUser) {
        this.customUserDetailsService = customUserDetailsService;
        this.taskStatusRepository = taskStatusRepository;
        this.labelRepository = labelRepository;
        this.userRepository = userRepository;
        this.defaultUser = defaultUser;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (!userRepository.existsByEmail(defaultUser.getEmail())) {
            User user = new User();
            user.setEmail(defaultUser.getEmail());
            user.setPasswordDigest(defaultUser.getPassword());
            customUserDetailsService.createUser(user);
        }

        Stream.of(
                        new TaskStatus("Draft", "draft"),
                        new TaskStatus("To Review", "to_review"),
                        new TaskStatus("To Be Fixed", "to_be_fixed"),
                        new TaskStatus("To Publish", "to_publish"),
                        new TaskStatus("Published", "published")
                )
                .filter(taskStatus -> !taskStatusRepository.existsBySlug(taskStatus.getSlug()))
                .forEach(taskStatusRepository::save);

        Stream.of("bug", "feature")
                .map(Label::new)
                .filter(label -> !labelRepository.existsByName(label.getName()))
                .forEach(labelRepository::save);
    }
}
