package hexlet.code.service;

import hexlet.code.dto.user.UserCreateDTO;
import hexlet.code.dto.user.UserDTO;
import hexlet.code.dto.user.UserUpdateDTO;
import hexlet.code.exception.UserAlreadyExistsException;
import hexlet.code.exception.UserNotFoundException;
import hexlet.code.mapper.UserMapper;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class UserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public List<UserDTO> getAll() {
        return userRepository.findAll().stream()
                .map(userMapper::map)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserDTO getById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::map)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    @Transactional
    public UserDTO create(UserCreateDTO userCreateDTO) {
        if (userRepository.existsByEmail(userCreateDTO.getEmail())) {
            LOGGER.info("User with email {} already exits", userCreateDTO.getEmail());
            throw new UserAlreadyExistsException(userCreateDTO.getEmail());
        }
        User user = userMapper.map(userCreateDTO);
        user = userRepository.save(user);
        LOGGER.info("Created user: {}", user);
        return userMapper.map(user);
    }

    @Transactional
    @PreAuthorize("@userUtils.isCurrentUser(#id)")
    public UserDTO updateById(Long id, UserUpdateDTO userUpdateDTO) {
        return userRepository.findById(id)
                .map(user -> {
                    LOGGER.info("Updated user: {}", user);
                    userMapper.update(userUpdateDTO, user);
                    return userMapper.map(user);
                })
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    @PreAuthorize("@userUtils.isCurrentUser(#id)")
    @Transactional
    public void deleteById(Long id) {
        userRepository.findById(id)
                .ifPresentOrElse(
                        user -> {
                            userRepository.deleteById(id);
                            LOGGER.info("Deleted user with id: {}", id);
                        },
                        () -> {
                            throw new UserNotFoundException(id);
                        });
    }
}
