package hexlet.code.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.users.UserUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import hexlet.code.util.ModelGenerator;
import org.instancio.Instancio;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UsersControllerTest {

    private User testUser;
    private JwtRequestPostProcessor token;

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ModelGenerator modelGenerator;

    @BeforeEach
    public void setUp() {

        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .apply(springSecurity())
                .build();
//        token = jwt().jwt(builder -> builder.subject("hexlet@example.com"));
        testUser = Instancio.of(modelGenerator.getUserModel()).create();
    }

    @AfterEach
    public void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = "hexlet@example.com", password = "qwerty", roles = "USER")
    public void testIndex() throws Exception {

        MockHttpServletRequestBuilder request = get("/api/users");
//                .with(token);
        mockMvc.perform(request)
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "hexlet@example.com", password = "qwerty", roles = "USER")
    void testCreate() throws Exception {

        User userNew = Instancio.of(modelGenerator.getUserModel()).create();

        MockHttpServletRequestBuilder request = post("/api/users")
//                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userNew));
        mockMvc.perform(request)
                .andExpect(status().isCreated());

        Optional<User> userOptional = userRepository.findByEmail(userNew.getEmail());
        User userFromDB = userOptional
                .orElseThrow(() -> new ResourceNotFoundException("User " + userNew.getEmail() + " not found"));

        assertNotNull(userFromDB);
        assertThat(userFromDB.getEmail()).isEqualTo(userNew.getEmail());
        assertThat(userFromDB.getFirstName()).isEqualTo(userNew.getFirstName());
        assertThat(userFromDB.getLastName()).isEqualTo(userNew.getLastName());
        assertThat(userFromDB.getCreatedAt()).isNotNull();
    }

    @Test
    @WithMockUser(username = "hexlet@example.com", password = "qwerty", roles = "USER")
    public void testShow() throws Exception {

        userRepository.save(testUser);

//        MockHttpServletRequestBuilder request = get("/api/users/{id}", testUser.getId())
//                .with(token);
        MockHttpServletRequestBuilder request = get("/api/users/{id}", testUser.getId());
        MvcResult result = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn();

        String body = result.getResponse().getContentAsString();
        assertThatJson(body).and(
                v -> v.node("id").isEqualTo(testUser.getId()),
                v -> v.node("email").isEqualTo(testUser.getEmail()),
                v -> v.node("firstName").isEqualTo(testUser.getFirstName()),
                v -> v.node("lastName").isEqualTo(testUser.getLastName()),
                v -> v.node("createdAt").isPresent());
    }

    @Test
    @WithMockUser(username = "hexlet@example.com", password = "qwerty", roles = "USER")
    void testUpdate() throws Exception {

        userRepository.save(testUser);

        UserUpdateDTO userUpdateDTO = new UserUpdateDTO();
        userUpdateDTO.setFirstName(JsonNullable.of("NewFirstName"));
        userUpdateDTO.setLastName(JsonNullable.of("NewLastName"));

        MockHttpServletRequestBuilder request = put("/api/users/{id}", testUser.getId())
//                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userUpdateDTO));
        mockMvc.perform(request)
                .andExpect(status().isOk());

        testUser = userRepository.findById(testUser.getId()).get();

        assertThat(testUser.getFirstName()).isEqualTo("NewFirstName");
        assertThat(testUser.getLastName()).isEqualTo("NewLastName");
    }

    @Test
    @WithMockUser(username = "hexlet@example.com", password = "qwerty", roles = "USER")
    public void destroy() throws Exception {

        userRepository.save(testUser);

        MockHttpServletRequestBuilder request = delete("/api/users/{id}", testUser.getId());
//                .with(token);
        mockMvc.perform(request).andExpect(status().isNoContent());

        assertThat(userRepository.existsById(testUser.getId())).isFalse();
    }
}
