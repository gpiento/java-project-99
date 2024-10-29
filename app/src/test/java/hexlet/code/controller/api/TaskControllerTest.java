package hexlet.code.controller.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.task.TaskCreateDTO;
import hexlet.code.dto.task.TaskDTO;
import hexlet.code.dto.task.TaskUpdateDTO;
import hexlet.code.mapper.TaskMapper;
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
import net.datafaker.Faker;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("development")
public class TaskControllerTest {

    private Task testTask;
    private User testUser;
    private TaskStatus testTaskStatus;
    private Label testLabel;
    private JwtRequestPostProcessor token;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ModelGenerator modelGenerator;
    @Autowired
    private Faker faker;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private TaskMapper taskMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TaskStatusRepository taskStatusRepository;
    @Autowired
    private LabelRepository labelRepository;

    @BeforeEach
    public void setup() {
        testUser = Instancio.of(modelGenerator.getUserModel()).create();
        token = jwt().jwt(builder -> builder.subject(testUser.getUsername()));
        testTask = Instancio.of(modelGenerator.getTaskModel()).create();
        testTaskStatus = Instancio.of(modelGenerator.getTaskStatusModel()).create();
        testLabel = Instancio.of(modelGenerator.getLabelModel()).create();
        // TODO: add assignee
//        testTask.setAssignee(testUser);
    }

    @Test
    public void getAllTasks() throws Exception {
//        mockMvc.perform(get("/api/tasks")
//                        .with(token))
//                .andExpect(status().isOk());
        MvcResult result = mockMvc
                .perform(get("/api/tasks")
                        .with(token))
                .andExpect(status().isOk())
                .andReturn();

        List<TaskDTO> taskDTOS = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                new TypeReference<>() {
                });
        List<Task> actual = taskDTOS.stream().map(taskMapper::map).toList();
        List<Task> expected = taskRepository.findAll();

        assertEquals(expected.size(), actual.size());
        assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    public void createTask() throws Exception {
        testUser = userRepository.save(testUser);
        testTaskStatus = taskStatusRepository.save(testTaskStatus);
        testLabel = labelRepository.save(testLabel);
        testTask.setAssignee(testUser);
        testTask.setTaskStatus(testTaskStatus);
        testTask.setLabels(new HashSet<>() {{
            add(testLabel);
        }});
        testTask = taskRepository.save(testTask);

        TaskCreateDTO taskCreateDTO = new TaskCreateDTO();
        taskCreateDTO.setTitle(testTask.getName());
        taskCreateDTO.setIndex(testTask.getIndex());
        taskCreateDTO.setContent(testTask.getDescription());
        taskCreateDTO.setStatus(testTaskStatus.getSlug());
        taskCreateDTO.setAssigneeId(testUser.getId());
        taskCreateDTO.setLabelIds(Set.of(testLabel.getId()));

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskCreateDTO))
                        .with(token))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.index").value(testTask.getIndex()))
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.assignee_id").value(testUser.getId()))
                .andExpect(jsonPath("$.title").value(testTask.getName()))
                .andExpect(jsonPath("$.content").value(testTask.getDescription()))
                .andExpect(jsonPath("$.status").value(testTaskStatus.getSlug()));
    }

    @Test
    public void getTaskById() throws Exception {
        testTask = taskRepository.save(testTask);
        mockMvc.perform(get("/api/tasks/{id}", testTask.getId())
                        .with(token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testTask.getId()))
                .andExpect(jsonPath("$.title").value(testTask.getName()))
                .andExpect(jsonPath("$.content").value(testTask.getDescription()))
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    public void updateTask() throws Exception {
        testUser = userRepository.save(testUser);
        testTaskStatus = taskStatusRepository.save(testTaskStatus);
        testLabel = labelRepository.save(testLabel);
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
        testTask = taskRepository.save(testTask);
        mockMvc.perform(delete("/api/tasks/{id}", testTask.getId())
                        .with(token))
                .andExpect(status().isNoContent());
        assertFalse(taskRepository.existsById(testTask.getId()));
    }
}
