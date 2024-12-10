package hexlet.code.service;

import hexlet.code.dto.user.UserCreateDTO;
import hexlet.code.dto.user.UserDTO;
import hexlet.code.dto.user.UserUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.exception.UserAlreadyExistsException;
import hexlet.code.mapper.UserMapper;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::map)
                .toList();
    }

    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("User with id '%d' not found", id));
        return userMapper.map(user);
    }

    @Transactional
    public UserDTO createUser(UserCreateDTO userCreateDTO) {
        if (userRepository.existsByEmail(userCreateDTO.getEmail())) {
            LOGGER.info("User with email {} already exits", userCreateDTO.getEmail());
            throw new UserAlreadyExistsException("User with email '%s' already exists", userCreateDTO.getEmail());
        }
        User user = userMapper.map(userCreateDTO);
        user = userRepository.save(user);
        LOGGER.info("Created user with id: {}", user.getId());
        return userMapper.map(user);
    }

    @Transactional
    public UserDTO updateById(Long id, UserUpdateDTO userUpdateDTO) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("User with id '%d' not found", id));
        userMapper.update(userUpdateDTO, user);
        LOGGER.info("Updated user with id: {}", id);
        user = userRepository.save(user);
        return userMapper.map(user);
    }

    @Transactional
    public void deleteById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User with id '%d' not found", id);
        }
        userRepository.deleteById(id);
        LOGGER.info("Deleted user with id: {}", id);
    }
}
