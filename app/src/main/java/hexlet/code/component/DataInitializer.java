package hexlet.code.component;

import hexlet.code.model.Label;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.service.CustomUserDetailsService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
@AllArgsConstructor
public class DataInitializer implements ApplicationRunner {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;
    @Autowired
    private TaskStatusRepository taskStatusRepository;
    @Autowired
    private LabelRepository labelRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DefaultUserProperties defaultUser;

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
