package hexlet.code.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.user.UserCreateDTO;
import hexlet.code.dto.user.UserUpdateDTO;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import hexlet.code.util.ModelGenerator;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.hasItem;
import static org.instancio.Select.field;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
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
public class UserControllerTest {
    private JwtRequestPostProcessor token;
    private User testUser;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ModelGenerator generator;

    @BeforeEach
    public void setUp() {
        testUser = Instancio.of(generator.getUserModel()).create();
        token = jwt().jwt(builder -> builder.subject(testUser.getEmail()));
    }

    @Test
    public void getAllUsers() throws Exception {
        testUser = userRepository.save(testUser);
        mockMvc.perform(get("/api/users")
                        .with(token))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[*].email", hasItem(testUser.getEmail())));
    }

    @Test
    public void getUserByIdSuccess() throws Exception {
        testUser = userRepository.save(testUser);
        mockMvc.perform(get("/api/users/{id}", testUser.getId())
                        .with(token))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.id").value(testUser.getId()),
                        jsonPath("$.email").value(testUser.getEmail()),
                        jsonPath("$.firstName").value(testUser.getFirstName()),
                        jsonPath("$.lastName").value(testUser.getLastName())
                );
    }

    @Test
    public void getUserByIdNotFound() throws Exception {
        mockMvc.perform(get("/api/users/{id}", 999999999)
                        .with(token))
                .andExpect(status().isNotFound());
    }

    @Test
    public void createUser() throws Exception {
        UserCreateDTO createUser = Instancio.of(generator.getUserCreateDTOModel()).create();
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUser))
                        .with(token))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.email").value(createUser.getEmail()))
                .andExpect(jsonPath("$.firstName").value(createUser.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(createUser.getLastName()));
    }

    @Test
    public void createUserInvalidEmail() throws Exception {
        UserCreateDTO invalidUser = Instancio.of(generator.getUserCreateDTOModel())
                .set(field(UserCreateDTO::getEmail), "invalid-email")
                .create();
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUser))
                        .with(token))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createUserEmptyFields() throws Exception {
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new UserCreateDTO()))
                        .with(token))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateUserById() throws Exception {
        testUser = userRepository.save(testUser);
        UserUpdateDTO userUpdateDTO = Instancio.of(generator.getUserUpdateDTOModel()).create();
        mockMvc.perform(put("/api/users/{id}", testUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userUpdateDTO))
                        .with(token))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testUser.getId()))
                .andExpect(jsonPath("$.email").value(userUpdateDTO.getEmail().get()))
                .andExpect(jsonPath("$.firstName").value(userUpdateDTO.getFirstName().get()))
                .andExpect(jsonPath("$.lastName").value(userUpdateDTO.getLastName().get()));
    }

    @Test
    public void deleteUser() throws Exception {
        testUser = userRepository.save(testUser);
        mockMvc.perform(get("/api/users/{id}", testUser.getId())
                        .with(token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(testUser.getEmail()));
        mockMvc.perform(delete("/api/users/{id}", testUser.getId())
                        .with(token))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/api/users/{id}", testUser.getId())
                        .with(token))
                .andExpect(status().isNotFound());
    }
}
