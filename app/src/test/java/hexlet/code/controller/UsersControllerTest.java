package hexlet.code.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.UserMapper;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import hexlet.code.service.UserService;
import hexlet.code.util.ModelGenerator;
import net.datafaker.Faker;
import org.instancio.Instancio;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Optional;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
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
    private Faker faker;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ModelGenerator modelGenerator;

    @Autowired
    private UserService userService;

    @BeforeEach
    public void setUp() {

        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .apply(springSecurity())
                .build();
        token = jwt().jwt(builder -> builder.subject("hexlet@example.com"));
        testUser = Instancio.of(modelGenerator.getUserModel()).create();
        userRepository.save(testUser);
    }

    @AfterEach
    public void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    public void testIndex() throws Exception {

        mockMvc.perform(get("/api/users").with(token))
                .andExpect(status().isOk());
    }

    @Test
    void testCreate() throws Exception {

        User userNew = Instancio.of(modelGenerator.getUserModel()).create();

        MockHttpServletRequestBuilder request = post("/api/users")
                .with(token)
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
    public void testShow() throws Exception {

        MockHttpServletRequestBuilder request = get("/api/users/{id}",
                testUser.getId())
                .with(token);
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
    void testUpdate() throws Exception {

        HashMap<String, String> data = new HashMap<>();
        data.put("firstName", "Mike");

        MockHttpServletRequestBuilder request = put("/api/users/" + testUser.getId())
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(data));

        mockMvc.perform(request)
                .andExpect(status().isOk());

        testUser = userRepository.findById(testUser.getId()).get();
        assertThat(testUser.getFirstName()).isEqualTo("Mike");
    }

    @Test
    public void destroy() throws Exception {
        userRepository.save(testUser);
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/users/{id}", testUser.getId())
                        .with(SecurityMockMvcRequestPostProcessors.user(testUser.getUsername())))
                .andExpect(status().isNoContent());

        assertThat(userRepository.findById(testUser.getId())).isEmpty();
    }
}
