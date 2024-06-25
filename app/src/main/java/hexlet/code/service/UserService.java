package hexlet.code.service;

import hexlet.code.dto.users.UserCreateDTO;
import hexlet.code.dto.users.UserDTO;
import hexlet.code.dto.users.UserUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.UserMapper;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    public UserDTO getUserById(final Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User " + id + " not found"));

        return userMapper.map(user);
    }

    public UserDTO create(final UserCreateDTO userCreateDTO) {

        User user = userMapper.map(userCreateDTO);
        userRepository.save(user);

        return userMapper.map(user);
    }

    public List<UserDTO> getAll() {

        List<User> users = userRepository.findAll();

        return users.stream()
                .map(userMapper::map)
                .toList();
    }

    public UserDTO update(final Long id, final UserUpdateDTO userUpdateDTO) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User " + id + " not found"));
        userMapper.update(userUpdateDTO, user);
        userRepository.save(user);

        return userMapper.map(user);
    }

    public void delete(final Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User " + id + " not found"));
        userRepository.deleteById(user.getId());
    }
}
