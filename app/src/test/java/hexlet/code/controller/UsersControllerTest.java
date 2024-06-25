package hexlet.code.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.dto.users.UserDTO;
import hexlet.code.dto.users.UserUpdateDTO;
import hexlet.code.mapper.UserMapper;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import hexlet.code.service.UserService;
import hexlet.code.util.ModelGenerator;
import net.datafaker.Faker;
import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullable;
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

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.springframework.mock.http.server.reactive.MockServerHttpRequest.post;
import static org.springframework.mock.http.server.reactive.MockServerHttpRequest.put;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
//                .apply(springSecurity())
                .build();
        token = jwt().jwt(builder -> builder.subject("hexlet@example.com"));
        testUser = Instancio.of(modelGenerator.getUserModel()).create();
        userRepository.save(testUser);
    }

    @Test
    public void testIndex() throws Exception {

        mockMvc.perform(get("/api/users").with(jwt()))
                .andExpect(status().isOk());
    }

    @Test
    void testCreate() throws Exception {

        UserDTO dto = userMapper.map(testUser);

        MockHttpServletRequestBuilder request = post("/api/posts")
                .with(token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto));

        mockMvc.perform(request)
                .andExpect(status().isCreated());

        User user = userRepository.findByEmail(dto.getEmail()).get();

        assertNotNull(user);
        assertThat(user.getEmail()).isEqualTo(testUser.getEmail());
        assertThat(user.getFirstName()).isEqualTo(testUser.getFirstName());
        assertThat(user.getLastName()).isEqualTo(testUser.getLastName());
        assertThat(user.getCreatedAt()).isNotNull();
    }

    @Test
    public void testShow() throws Exception {

        MockHttpServletRequestBuilder request = get("/api/posts"
                + testUser.getId()).with(token);
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
    void update() throws Exception {
        userRepository.save(testUser);
        UserUpdateDTO userUpdateDTO = new UserUpdateDTO();
        userUpdateDTO.setFirstName(JsonNullable.of(faker.name().firstName()));
        userUpdateDTO.setLastName(JsonNullable.of(faker.name().lastName()));
        userUpdateDTO.setEmail(JsonNullable.of(faker.internet().emailAddress()));
        userUpdateDTO.setPassword(JsonNullable.of(faker.internet().password(3, 30)));

        MockHttpServletRequestBuilder request = put("/api/users/{id}", testUser.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userUpdateDTO));

        mockMvc.perform(request)
                .andExpect(status().isOk());

        User userModified = userRepository.findById(testUser.getId()).get();

        assertThat(userModified.getFirstName()).isEqualTo(userUpdateDTO.getFirstName());
        assertThat(userModified.getLastName()).isEqualTo(userUpdateDTO.getLastName());
        assertThat(userModified.getEmail()).isEqualTo(userUpdateDTO.getEmail());
        assertThat(userModified.getPasswordDigest()).isEqualTo(userUpdateDTO.getPassword());
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