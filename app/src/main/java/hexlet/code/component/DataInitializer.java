package hexlet.code.component;

import hexlet.code.dto.taskstatus.TaskStatusCreateDTO;
import hexlet.code.mapper.TaskStatusMapper;
import hexlet.code.model.User;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.service.CustomUserDetailsService;
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

    @Autowired
    private CustomUserDetailsService customUserDetailsService;
    @Autowired
    private TaskStatusRepository taskStatusRepository;
    @Autowired
    private TaskStatusMapper taskStatusMapper;

    @Override
    public void run(ApplicationArguments args) {
        User userData = new User();
        userData.setEmail("hexlet@example.com");
        userData.setPasswordDigest("qwerty");
        customUserDetailsService.createUser(userData);

        List<TaskStatusCreateDTO> defaultStatuses = Arrays.asList(
                new TaskStatusCreateDTO("Draft", "draft"),
                new TaskStatusCreateDTO("To Review", "to_review"),
                new TaskStatusCreateDTO("To Be Fixed", "to_be_fixed"),
                new TaskStatusCreateDTO("To Publish", "to_publish"),
                new TaskStatusCreateDTO("Published", "published")
        );

        for (TaskStatusCreateDTO statusDTO : defaultStatuses) {
            if (!taskStatusRepository.findBySlug(statusDTO.getSlug()).isPresent()) {
                taskStatusRepository.save(taskStatusMapper.map(statusDTO));
            }
        }
    }
}
