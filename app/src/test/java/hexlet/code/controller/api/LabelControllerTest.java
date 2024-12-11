package hexlet.code.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.label.LabelCreateDTO;
import hexlet.code.dto.label.LabelUpdateDTO;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
import hexlet.code.util.ModelGenerator;
import jakarta.transaction.Transactional;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasItem;
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

    private final JwtRequestPostProcessor token = jwt().jwt(builder -> builder.subject("hexlet@example.com"));
    private Label testLabel;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ModelGenerator generator;
    @Autowired
    private LabelRepository labelRepository;

    @Test
    public void getAllLabels() throws Exception {
        testLabel = labelRepository.save(Instancio.of(generator.getLabelModel()).create());
        mockMvc.perform(get("/api/labels")
                        .with(token))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[*].name", hasItem(testLabel.getName())));
    }

    @Test
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
    public void getLabelByIdNotFound() throws Exception {
        mockMvc.perform(get("/api/labels/{id}", 999999999)
                        .with(token))
                .andExpect(status().isNotFound());
    }

    @Test
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
    }

    @Test
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
    }

    @Test
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

    @Test
    @WithMockUser
    public void getAllLabelsAuthenticated() throws Exception {
        mockMvc.perform(get("/api/labels"))
                .andExpect(status().isOk());
    }

    @Test
    public void getAllLabelsUnauthenticated() throws Exception {
        mockMvc.perform(get("/api/labels"))
                .andExpect(status().isUnauthorized());
    }
}
