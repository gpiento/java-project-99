package hexlet.code.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.task.TaskCreateDTO;
import hexlet.code.dto.task.TaskUpdateDTO;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.util.ModelGenerator;
import jakarta.transaction.Transactional;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashSet;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class TaskControllerTest {

    private final JwtRequestPostProcessor token = jwt().jwt(builder -> builder.subject("hexlet@example.com"));
    private Task testTask;
    private User testUser;
    private TaskStatus testTaskStatus;
    private Label testLabel;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ModelGenerator generator;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TaskStatusRepository taskStatusRepository;
    @Autowired
    private LabelRepository labelRepository;

    @Test
    public void getAllTasks() throws Exception {
        testTask = Instancio.of(generator.getTaskModel()).create();
        testUser = userRepository.save(Instancio.of(generator.getUserModel()).create());
        testTaskStatus = taskStatusRepository.save(Instancio.of(generator.getTaskStatusModel()).create());
        testLabel = labelRepository.save(Instancio.of(generator.getLabelModel()).create());
        testTask.setAssignee(testUser);
        testTask.setTaskStatus(testTaskStatus);
        testTask.setLabels(new HashSet<>() {{
            add(testLabel);
        }});
        testTask = taskRepository.save(testTask);
        mockMvc.perform(get("/api/tasks")
                        .with(token))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    public void createTask() throws Exception {
        TaskCreateDTO taskCreate = Instancio.of(generator.getTaskCreateDTOModel()).create();
        testUser = userRepository.save(Instancio.of(generator.getUserModel()).create());
        testTaskStatus = taskStatusRepository.save(Instancio.of(generator.getTaskStatusModel()).create());
        testLabel = labelRepository.save(Instancio.of(generator.getLabelModel()).create());
        taskCreate.setAssigneeId(testUser.getId());
        taskCreate.setStatus(testTaskStatus.getSlug());
        taskCreate.setTaskLabelIds(new HashSet<>() {{
            add(testLabel.getId());
        }});

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskCreate))
                        .with(token))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.index").value(taskCreate.getIndex()))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.assignee_id").value(testUser.getId()))
                .andExpect(jsonPath("$.title").value(taskCreate.getTitle()))
                .andExpect(jsonPath("$.content").value(taskCreate.getContent()))
                .andExpect(jsonPath("$.status").value(testTaskStatus.getSlug()));
    }

    @Test
    public void getTaskById() throws Exception {
        testTask = Instancio.of(generator.getTaskModel()).create();
        testUser = userRepository.save(Instancio.of(generator.getUserModel()).create());
        testTaskStatus = taskStatusRepository.save(Instancio.of(generator.getTaskStatusModel()).create());
        testLabel = labelRepository.save(Instancio.of(generator.getLabelModel()).create());
        testTask.setAssignee(testUser);
        testTask.setTaskStatus(testTaskStatus);
        testTask.setLabels(new HashSet<>() {{
            add(testLabel);
        }});
        testTask = taskRepository.save(testTask);
        mockMvc.perform(get("/api/tasks/{id}", testTask.getId())
                        .with(token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.index").value(testTask.getIndex()))
                .andExpect(jsonPath("$.title").value(testTask.getName()))
                .andExpect(jsonPath("$.content").value(testTask.getDescription()))
                .andExpect(jsonPath("$.assignee_id").value(testTask.getAssignee().getId()))
                .andExpect(jsonPath("$.status").value(testTask.getTaskStatus().getSlug()))
                .andExpect(jsonPath("$.taskLabelIds").exists())
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    public void updateTaskById() throws Exception {
        testTask = Instancio.of(generator.getTaskModel()).create();
        testUser = userRepository.save(Instancio.of(generator.getUserModel()).create());
        testTaskStatus = taskStatusRepository.save(Instancio.of(generator.getTaskStatusModel()).create());
        testLabel = labelRepository.save(Instancio.of(generator.getLabelModel()).create());
        testTask.setAssignee(testUser);
        testTask.setTaskStatus(testTaskStatus);
        testTask.setLabels(new HashSet<>() {{
            add(testLabel);
        }});
        testTask = taskRepository.save(testTask);

        TaskUpdateDTO taskUpdateDTO = new TaskUpdateDTO();
        taskUpdateDTO.setTitle(JsonNullable.of("test task"));
        taskUpdateDTO.setIndex(JsonNullable.of(1000));
        taskUpdateDTO.setContent(JsonNullable.of("test content"));

        mockMvc.perform(put("/api/tasks/{id}", testTask.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskUpdateDTO))
                        .with(token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testTask.getId()))
                .andExpect(jsonPath("$.title").value(taskUpdateDTO.getTitle().get()))
                .andExpect(jsonPath("$.index").value(taskUpdateDTO.getIndex().get()))
                .andExpect(jsonPath("$.content").value(taskUpdateDTO.getContent().get()))
                .andExpect(jsonPath("$.createdAt").exists());
        taskRepository.delete(testTask);
    }

    @Test
    public void deleteTask() throws Exception {
        testTask = Instancio.of(generator.getTaskModel()).create();
        testUser = userRepository.save(Instancio.of(generator.getUserModel()).create());
        testTaskStatus = taskStatusRepository.save(Instancio.of(generator.getTaskStatusModel()).create());
        testLabel = labelRepository.save(Instancio.of(generator.getLabelModel()).create());
        testTask.setAssignee(testUser);
        testTask.setTaskStatus(testTaskStatus);
        testTask.setLabels(new HashSet<>() {{
            add(testLabel);
        }});
        testTask = taskRepository.save(testTask);

        mockMvc.perform(get("/api/tasks/{id}", testTask.getId())
                        .with(token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(testTask.getName()));
        mockMvc.perform(delete("/api/tasks/{id}", testTask.getId())
                        .with(token))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/api/tasks/{id}", testTask.getId())
                        .with(token))
                .andExpect(status().isNotFound());
    }
}
