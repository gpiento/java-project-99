package hexlet.code.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.taskstatus.TaskStatusCreateDTO;
import hexlet.code.dto.taskstatus.TaskStatusUpdateDTO;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.util.ModelGenerator;
import org.instancio.Instancio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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

    private final JwtRequestPostProcessor token = jwt().jwt(builder -> builder.subject("hexlet@example.com"));
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private TaskStatusRepository taskStatusRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ModelGenerator generator;

    private TaskStatus testTaskStatus;

    @Nested
    @DisplayName("GET /api/task_statuses")
    class GetAllTaskStatuses {
        @Test
        @DisplayName("should return all task statuses")
        public void getAllTaskStatuses() throws Exception {
            testTaskStatus = taskStatusRepository.save(Instancio.of(generator.getTaskStatusModel()).create());
            mockMvc.perform(get("/api/task_statuses")
                            .with(token))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$[*].slug", hasItem(testTaskStatus.getSlug())));
        }

        @Test
        @DisplayName("should return task status by id")
        public void getTaskStatusByIdSuccess() throws Exception {
            testTaskStatus = taskStatusRepository.save(Instancio.of(generator.getTaskStatusModel()).create());
            mockMvc.perform(get("/api/task_statuses/{id}", testTaskStatus.getId())
                            .with(token))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(testTaskStatus.getId()))
                    .andExpect(jsonPath("$.name").value(testTaskStatus.getName()))
                    .andExpect(jsonPath("$.slug").value(testTaskStatus.getSlug()))
                    .andExpect(jsonPath("$.createdAt").exists());
        }

        @Test
        @DisplayName("should return 404 if task status not found")
        public void getTaskStatusByIdNotFound() throws Exception {
            mockMvc.perform(get("/api/task_statuses/{id}", 999999999)
                            .with(token))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("POST /api/task_statuses")
    class CreateTaskStatus {
        @Test
        @DisplayName("should create task status")
        public void createTaskStatus() throws Exception {
            TaskStatusCreateDTO createDTO = Instancio.of(generator.getTaskStatusCreateDTOModel()).create();
            mockMvc.perform(post("/api/task_statuses")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createDTO))
                            .with(token))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").exists())
                    .andExpect(jsonPath("$.name").value(createDTO.getName()))
                    .andExpect(jsonPath("$.slug").value(createDTO.getSlug()))
                    .andExpect(jsonPath("$.createdAt").exists());

            TaskStatus actualedTaskStatus = taskStatusRepository.findBySlug(createDTO.getSlug()).get();
            assertNotNull(actualedTaskStatus);
            assertThat(actualedTaskStatus.getSlug()).isEqualTo(createDTO.getSlug());
        }
    }

    @Nested
    @DisplayName("PUT /api/task_statuses/{id}")
    class UpdateTaskStatus {
        @Test
        @DisplayName("should update task status by id")
        public void updateTaskStatusById() throws Exception {
            testTaskStatus = taskStatusRepository.save(Instancio.of(generator.getTaskStatusModel()).create());
            TaskStatusUpdateDTO updateDTO = Instancio.of(generator.getTaskStatusUpdateDTOModel()).create();
            mockMvc.perform(put("/api/task_statuses/{id}", testTaskStatus.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateDTO))
                            .with(token))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(testTaskStatus.getId()))
                    .andExpect(jsonPath("$.name").value(updateDTO.getName().get()))
                    .andExpect(jsonPath("$.slug").value(updateDTO.getSlug().get()))
                    .andExpect(jsonPath("$.createdAt").exists());

            TaskStatus actualedTaskStatus = taskStatusRepository.findBySlug(updateDTO.getSlug().get()).get();
            assertNotNull(actualedTaskStatus);
            assertThat(actualedTaskStatus.getSlug()).isEqualTo(updateDTO.getSlug().get());
        }
    }

    @Nested
    @DisplayName("DELETE /api/task_statuses/{id}")
    class DeleteTaskStatus {
        @Test
        @DisplayName("should delete task status by id")
        public void deleteTaskStatus() throws Exception {
            testTaskStatus = taskStatusRepository.save(Instancio.of(generator.getTaskStatusModel()).create());
            mockMvc.perform(get("/api/task_statuses/{id}", testTaskStatus.getId())
                            .with(token))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.slug").value(testTaskStatus.getSlug()));
            mockMvc.perform(delete("/api/task_statuses/{id}", testTaskStatus.getId())
                            .with(token))
                    .andExpect(status().isNoContent());
            mockMvc.perform(get("/api/task_statuses/{id}", testTaskStatus.getId())
                            .with(token))
                    .andExpect(status().isNotFound());
        }
    }
}
