package hexlet.code.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.label.LabelUpdateDTO;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
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
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertFalse;
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
@ActiveProfiles("development")
public class LabelControllerTest {

    private Label testLabel;
    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor token;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ModelGenerator modelGenerator;
    @Autowired
    private Faker faker;
    @Autowired
    private LabelRepository labelRepository;

    @BeforeEach
    public void setUp() {
        token = jwt().jwt(builder -> builder.subject("hexlet@example.com"));
        testLabel = Instancio.of(modelGenerator.getLabelModel()).create();
    }

    @AfterEach
    public void tearDown() {
//        labelRepository.deleteAll();
    }

    @Test
    public void testIndex() throws Exception {
        mockMvc.perform(get("/api/labels").with(token))
                .andExpect(status().isOk());
    }

    @Test
    public void testCreate() throws Exception {
        mockMvc.perform(post("/api/labels")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testLabel))
                        .with(token))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value(testLabel.getName()))
                .andExpect(jsonPath("$.createdAt").exists());
        labelRepository.delete(testLabel);
    }

    @Test
    public void testShow() throws Exception {
        testLabel = labelRepository.save(testLabel);
        mockMvc.perform(get("/api/labels/{id}", testLabel.getId()).with(token))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(testLabel.getId()))
                .andExpect(jsonPath("$.name").value(testLabel.getName()))
                .andExpect(jsonPath("$.createdAt").exists());
        labelRepository.delete(testLabel);
    }

    @Test
    public void testUpdate() throws Exception {
        testLabel = labelRepository.save(testLabel);
        LabelUpdateDTO labelUpdateDTO = new LabelUpdateDTO();
        labelUpdateDTO.setName(JsonNullable.of(faker.name().firstName()));
        mockMvc.perform(put("/api/labels/{id}", testLabel.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(labelUpdateDTO))
                        .with(token))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.id").value(testLabel.getId()))
                .andExpect(jsonPath("$.name").value(labelUpdateDTO.getName().get()))
                .andExpect(jsonPath("$.createdAt").exists());
        labelRepository.delete(testLabel);
    }

    @Test
    public void destroy() throws Exception {
        testLabel = labelRepository.save(testLabel);
        mockMvc.perform(delete("/api/labels/{id}", testLabel.getId()).with(token))
                .andExpect(status().isNoContent());
        assertFalse(labelRepository.existsById(testLabel.getId()));
    }
}
