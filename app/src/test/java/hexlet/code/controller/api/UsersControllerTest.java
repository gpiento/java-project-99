package hexlet.code.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.user.UserUpdateDTO;
import hexlet.code.model.Task;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import hexlet.code.util.ModelGenerator;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

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
//@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UsersControllerTest {
    private final JwtRequestPostProcessor token = jwt().jwt(builder -> builder.subject("test@example.com"));
    private User testUser;
    private Task testTask;
//    private JwtRequestPostProcessor token;

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private WebApplicationContext wac;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ModelGenerator modelGenerator;
    @Autowired
    private Faker faker;


    @BeforeEach
    public void beforeEachSetup() {
        testTask = Instancio.of(modelGenerator.getTaskModel()).create();
        testUser = Instancio.of(modelGenerator.getUserModel()).create();
        // TODO: add assignee
//        testUser.addTask(testTask);
    }

    @AfterEach
    public void afterEachSetup() {
        userRepository.delete(testUser);
    }

    @Test
    public void getAllUsers() throws Exception {
        mockMvc.perform(get("/api/users")
                        .with(token))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void getUserByIdSuccess() throws Exception {
        testUser = userRepository.save(testUser);
        mockMvc.perform(get("/api/users/{id}", testUser.getId())
                        .with(token))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpectAll(
                        jsonPath("$.id").value(testUser.getId()),
                        jsonPath("$.email").value(testUser.getEmail()),
                        jsonPath("$.firstName").value(testUser.getFirstName()),
                        jsonPath("$.lastName").value(testUser.getLastName())
                );
    }

    @Test
    public void getUserByIdNotFound() throws Exception {
        testUser = userRepository.save(testUser);
        mockMvc.perform(get("/api/users/{id}", 999999999)
                        .with(token))
                .andExpect(status().isNotFound());
    }

    @Test
    public void createUser() throws Exception {
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUser))
                        .with(token))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value(testUser.getEmail()))
                .andExpect(jsonPath("$.firstName").value(testUser.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(testUser.getLastName()));
    }

    @Test
    public void createUserInvalidEmail() throws Exception {
        User invalidUser = User.builder()
                .email("invalid-email")
                .lastName("Doe")
                .passwordDigest("qwerty")
                .build();
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUser))
                        .with(token))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createUserDuplicateEmail() throws Exception {
        User existingUser = userRepository.save(User.builder()
                .email("existing@example.com")
                .firstName("Existing")
                .lastName("User")
                .passwordDigest("qwerty")
                .build());
        existingUser = userRepository.save(existingUser);
        User newUser = User.builder()
                .email("existing@example.com")
                .firstName("New")
                .lastName("User")
                .passwordDigest("qwerty")
                .build();
        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newUser))
                        .with(token))
                .andExpect(status().isConflict());
    }

    @Test
    void createUserEmptyFields() throws Exception {
        User emptyUser = User.builder().build();

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emptyUser))
                        .with(token))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateUserById() throws Exception {
        testUser = userRepository.save(testUser);

        UserUpdateDTO userUpdateDTO = new UserUpdateDTO();
        userUpdateDTO.setEmail(JsonNullable.of(faker.internet().emailAddress()));

        mockMvc.perform(put("/api/users/{id}", testUser.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userUpdateDTO))
                        .with(token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testUser.getId()))
                .andExpect(jsonPath("$.email").value(userUpdateDTO.getEmail().get()))
                .andExpect(jsonPath("$.firstName").value(testUser.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(testUser.getLastName()));
    }

    @Test
    public void deleteUser() throws Exception {
        testUser = userRepository.save(testUser);
        mockMvc.perform(delete("/api/users/{id}", testUser.getId())
                        .with(token))
                .andExpect(status().isNoContent());
    }
}
