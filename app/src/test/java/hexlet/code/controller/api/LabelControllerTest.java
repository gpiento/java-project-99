package hexlet.code.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.label.LabelCreateDTO;
import hexlet.code.dto.label.LabelUpdateDTO;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
import hexlet.code.util.ModelGenerator;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
public class LabelControllerTest {

    private JwtRequestPostProcessor token;

    private Label testLabel;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ModelGenerator generator;
    @Autowired
    private LabelRepository labelRepository;

    @BeforeEach
    public void setUp() {
        testLabel = Instancio.of(generator.getLabelModel()).create();
        token = jwt().jwt(builder -> builder.subject("hexlet@example.com"));
    }

    @Nested
    @DisplayName("GET /api/labels")
    class GetAllLabelsTest {
        @Test
        @DisplayName("should return all labels")
        public void getAllLabels() throws Exception {
            testLabel = labelRepository.save(testLabel);
            mockMvc.perform(get("/api/labels")
                            .with(token))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$[*].name", hasItem(testLabel.getName())));
        }

        @Test
        @DisplayName("should return unauthorized")
        public void getAllLabelsUnauthenticated() throws Exception {
            mockMvc.perform(get("/api/labels"))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @WithMockUser
        @DisplayName("should return all labels")
        public void getAllLabelsAuthenticated() throws Exception {
            mockMvc.perform(get("/api/labels"))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("GET /api/labels/{id}")
    class GetLabelByIdTest {
        @Test
        @DisplayName("should return label by id")
        public void getLabelByIdSuccess() throws Exception {
            testLabel = labelRepository.save(Instancio.of(generator.getLabelModel()).create());
            mockMvc.perform(get("/api/labels/{id}", testLabel.getId())
                            .with(token))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(testLabel.getId()))
                    .andExpect(jsonPath("$.name").value(testLabel.getName()))
                    .andExpect(jsonPath("$.createdAt").exists());
        }

        @Test
        @DisplayName("should return not found")
        public void getLabelByIdNotFound() throws Exception {
            mockMvc.perform(get("/api/labels/{id}", 999999999)
                            .with(token))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("POST /api/labels")
    class CreateLabelTest {
        @Test
        @DisplayName("should create label")
        public void createLabel() throws Exception {
            LabelCreateDTO createLabel = Instancio.of(generator.getLabelCreateDTOModel()).create();
            mockMvc.perform(post("/api/labels")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createLabel))
                            .with(token))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").exists())
                    .andExpect(jsonPath("$.name").value(createLabel.getName()))
                    .andExpect(jsonPath("$.createdAt").exists());

            Label actualedLabel = labelRepository.findByName(createLabel.getName()).get();
            assertNotNull(actualedLabel);
            assertThat(actualedLabel.getName()).isEqualTo(createLabel.getName());
        }
    }

    @Nested
    @DisplayName("PUT /api/labels/{id}")
    class UpdateLabelTest {
        @Test
        @DisplayName("should update label")
        public void updateLabelById() throws Exception {
            testLabel = labelRepository.save(Instancio.of(generator.getLabelModel()).create());
            LabelUpdateDTO labelUpdateDTO = Instancio.of(generator.getLabelUpdateDTOModel()).create();
            mockMvc.perform(put("/api/labels/{id}", testLabel.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(labelUpdateDTO))
                            .with(token))
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andDo(print())
                    .andExpect(jsonPath("$.id").value(testLabel.getId()))
                    .andExpect(jsonPath("$.name").value(labelUpdateDTO.getName().get()))
                    .andExpect(jsonPath("$.createdAt").exists());

            Label actualedLabel = labelRepository.findByName(labelUpdateDTO.getName().get()).get();
            assertNotNull(actualedLabel);
            assertThat(actualedLabel.getName()).isEqualTo(labelUpdateDTO.getName().get());
        }
    }

    @Nested
    @DisplayName("DELETE /api/labels/{id}")
    class DeleteLabelTest {
        @Test
        @DisplayName("should delete label")
        public void destroy() throws Exception {
            testLabel = labelRepository.save(Instancio.of(generator.getLabelModel()).create());
            mockMvc.perform(get("/api/labels/{id}", testLabel.getId())
                            .with(token))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value(testLabel.getName()));
            mockMvc.perform(delete("/api/labels/{id}", testLabel.getId())
                            .with(token))
                    .andExpect(status().isNoContent());
            mockMvc.perform(get("/api/labels/{id}", testLabel.getId())
                            .with(token))
                    .andExpect(status().isNotFound());
        }
    }
}
