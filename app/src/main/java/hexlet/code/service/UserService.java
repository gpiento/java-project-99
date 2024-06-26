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
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;

    public List<UserDTO> getAll() {

        List<User> users = userRepository.findAll();
        return users.stream()
                .map(userMapper::map)
                .toList();
    }

    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User " + id + " not found"));

        return userMapper.map(user);
    }

    public UserDTO create(UserCreateDTO userCreateDTO) {
        Optional<User> userOptional = userRepository.findByEmail(userCreateDTO.getEmail());
        if (userOptional.isPresent()) {
            throw new ResourceNotFoundException("User with email " + userCreateDTO.getEmail() + " already exists");
        }
        User user = userMapper.map(userCreateDTO);
        user = userRepository.save(user);

        return userMapper.map(user);
    }

    public UserDTO update(Long id, UserUpdateDTO userUpdateDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User " + id + " not found"));
        userMapper.update(userUpdateDTO, user);
        userRepository.save(user);

        return userMapper.map(user);
    }

    public void delete(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User " + id + " not found"));
        userRepository.deleteById(user.getId());
    }
}
