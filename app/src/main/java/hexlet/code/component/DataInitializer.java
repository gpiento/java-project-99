package hexlet.code.component;

import hexlet.code.mapper.UserMapper;
import hexlet.code.model.User;
import hexlet.code.service.CustomUserDetailsService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class DataInitializer implements ApplicationRunner {

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private UserMapper userMapper;

    @Override
    public void run(ApplicationArguments args) {
        User userData = new User();
        userData.setEmail("hexlet@example.com");
        userData.setPasswordDigest("qwerty");
        customUserDetailsService.createUser(userData);
    }
}
