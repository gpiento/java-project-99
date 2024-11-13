package hexlet.code.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.component.DefaultUserProperties;
import hexlet.code.dto.taskstatus.TaskStatusUpdateDTO;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.util.ModelGenerator;
import jakarta.transaction.Transactional;
import net.datafaker.Faker;
import org.instancio.Instancio;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class TaskStatusControllerTest {

    private TaskStatus testTaskStatus;
    private Task testTask;
    private JwtRequestPostProcessor token;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private TaskStatusRepository taskStatusRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ModelGenerator modelGenerator;
    @Autowired
    private Faker faker;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private DefaultUserProperties defaultUser;


    @BeforeEach
    public void setUp() {
        taskStatusRepository.deleteAll();
        token = jwt().jwt(builder -> builder.subject(defaultUser.getEmail()));
        testTaskStatus = Instancio.of(modelGenerator.getTaskStatusModel()).create();
        testTask = Instancio.of(modelGenerator.getTaskModel()).create();
//        testTaskStatus.addTask(testTask);
    }

    @AfterEach
    public void tearDown() {
        taskStatusRepository.deleteAll();
    }

    @Test
    public void testIndex() throws Exception {
        mockMvc.perform(get("/api/task_statuses").with(token))
                .andExpect(status().isOk());
    }

    @Test
    public void testCreate() throws Exception {
        mockMvc.perform(post("/api/task_statuses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testTaskStatus))
                        .with(token))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value(testTaskStatus.getName()))
                .andExpect(jsonPath("$.slug").value(testTaskStatus.getSlug()))
                .andExpect(jsonPath("$.createdAt").exists());
        taskStatusRepository.delete(testTaskStatus);
    }

    @Test
    public void testShow() throws Exception {
        testTaskStatus = taskStatusRepository.save(testTaskStatus);
        mockMvc.perform(get("/api/task_statuses/{id}", testTaskStatus.getId()).with(token))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(testTaskStatus.getId()))
                .andExpect(jsonPath("$.name").value(testTaskStatus.getName()))
                .andExpect(jsonPath("$.slug").value(testTaskStatus.getSlug()))
                .andExpect(jsonPath("$.createdAt").exists());
        taskStatusRepository.delete(testTaskStatus);
    }

    @Test
    public void testUpdate() throws Exception {
        testTaskStatus = taskStatusRepository.save(testTaskStatus);
        TaskStatusUpdateDTO taskStatusUpdateDTO = new TaskStatusUpdateDTO(
                JsonNullable.of(faker.name().firstName()),
                JsonNullable.of(faker.internet().domainWord().toLowerCase().replace("-", "_"))
        );
        mockMvc.perform(put("/api/task_statuses/{id}", testTaskStatus.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(taskStatusUpdateDTO))
                        .with(token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testTaskStatus.getId()))
                .andExpect(jsonPath("$.name").value(taskStatusUpdateDTO.getName().get()))
                .andExpect(jsonPath("$.slug").value(taskStatusUpdateDTO.getSlug().get()))
                .andExpect(jsonPath("$.name").value(testTaskStatus.getName()))
                .andExpect(jsonPath("$.slug").value(testTaskStatus.getSlug()))
                .andExpect(jsonPath("$.createdAt").exists());
        taskStatusRepository.delete(testTaskStatus);
    }

    @Test
    public void destroy() throws Exception {
        testTaskStatus = taskStatusRepository.save(testTaskStatus);
        mockMvc.perform(delete("/api/task_statuses/{id}", testTaskStatus.getId()).with(token))
                .andExpect(status().isNoContent());
        assertFalse(taskStatusRepository.existsById(testTaskStatus.getId()));
    }
}
