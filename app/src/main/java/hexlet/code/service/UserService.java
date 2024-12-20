package hexlet.code.service;

import hexlet.code.dto.user.UserCreateDTO;
import hexlet.code.dto.user.UserDTO;
import hexlet.code.dto.user.UserUpdateDTO;
import hexlet.code.exception.ResourceAlreadyExistsException;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.UserMapper;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    public List<UserDTO> getAll() {
        return userRepository.findAll().stream()
                .map(userMapper::map)
                .toList();
    }

    public UserDTO getById(Long id) {
        User user = findUserById(id);
        return userMapper.map(user);
    }

    @Transactional
    public UserDTO create(UserCreateDTO userCreateDTO) {
        if (userRepository.existsByEmail(userCreateDTO.getEmail())) {
            LOGGER.info("User with email {} already exits", userCreateDTO.getEmail());
            throw new ResourceAlreadyExistsException("User with email '"
                    + userCreateDTO.getEmail() + "' already exists");
        }
        User user = userMapper.map(userCreateDTO);
        user = userRepository.save(user);
        LOGGER.info("Created user with id: {}", user.getId());
        return userMapper.map(user);
    }

    @PreAuthorize("@userUtils.isCurrentUser(#id)")
    @Transactional
    public UserDTO updateById(Long id, UserUpdateDTO userUpdateDTO) {
        User user = findUserById(id);
        userMapper.update(userUpdateDTO, user);
        LOGGER.info("Updated user with id: {}", id);
        user = userRepository.save(user);
        return userMapper.map(user);
    }

    @PreAuthorize("@userUtils.isCurrentUser(#id)")
    @Transactional
    public void deleteById(Long id) {
        findUserById(id);
        userRepository.deleteById(id);
        LOGGER.info("Deleted user with id: {}", id);
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id '"
                        + id + "' not found"));
    }
}
