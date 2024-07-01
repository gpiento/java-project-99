package hexlet.code.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.util.ModelGenerator;
import net.datafaker.Faker;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
class TaskStatusControllerTest {

    private TaskStatus testTaskStatus;
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

    @BeforeEach
    public void setUp() {
        token = jwt().jwt(builder -> builder.subject("hexlet@example.com"));
        testTaskStatus = Instancio.of(modelGenerator.getTaskStatusModel()).create();
    }

    @Test
    public void testIndex() throws Exception {
        mockMvc.perform(get("/api/task_statuses").with(token))
                .andExpect(status().isOk());
    }

    @Test
    void testCreate() throws Exception {
        mockMvc.perform(post("/api/task_statuses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testTaskStatus))
                        .with(token))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value(testTaskStatus.getName()))
                .andExpect(jsonPath("$.slug").value(testTaskStatus.getSlug()))
                .andExpect(jsonPath("$.createdAt").exists());
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
    }

    @Test
    void testUpdate() throws Exception {
        testTaskStatus = taskStatusRepository.save(testTaskStatus);
        testTaskStatus.setName(faker.name().name());
        testTaskStatus.setSlug(faker.name().name().toLowerCase().replace(" ", "-"));
        mockMvc.perform(put("/api/task_statuses/{id}", testTaskStatus.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testTaskStatus))
                        .with(token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testTaskStatus.getId()))
                .andExpect(jsonPath("$.name").value(testTaskStatus.getName()))
                .andExpect(jsonPath("$.slug").value(testTaskStatus.getSlug()))
                .andExpect(jsonPath("$.createdAt").exists());
    }

    @Test
    public void destroy() throws Exception {
        testTaskStatus = taskStatusRepository.save(testTaskStatus);
        mockMvc.perform(delete("/api/task_statuses/{id}", testTaskStatus.getId()).with(token))
                .andExpect(status().isNoContent());
        assertFalse(taskStatusRepository.existsById(testTaskStatus.getId()));
    }
}
