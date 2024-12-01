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
            User user = User.builder()
                    .email(defaultUser.getEmail())
                    .passwordDigest(defaultUser.getPassword())
                    .build();
            customUserDetailsService.createUser(user);
        }

        Stream.of(
                        TaskStatus.builder().name("Draft").slug("draft").build(),
                        TaskStatus.builder().name("To Review").slug("to_review").build(),
                        TaskStatus.builder().name("To Be Fixed").slug("to_be_fixed").build(),
                        TaskStatus.builder().name("To Publish").slug("to_publish").build(),
                        TaskStatus.builder().name("Published").slug("published").build()
                )
                .filter(taskStatus -> !taskStatusRepository.existsBySlug(taskStatus.getSlug()))
                .forEach(taskStatusRepository::save);

        Stream.of("bug", "feature")
                .map(Label::new)
                .filter(label -> !labelRepository.existsByName(label.getName()))
                .forEach(labelRepository::save);
    }
}
