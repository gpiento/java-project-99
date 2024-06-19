package hexlet.code.controller;

import hexlet.code.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class UsersController {

    @Autowired // or @Inject
    private UserRepository userRepository;


}
