package hexlet.code.controller.api;

import hexlet.code.dto.user.UserCreateDTO;
import hexlet.code.dto.user.UserDTO;
import hexlet.code.dto.user.UserUpdateDTO;
import hexlet.code.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
// TODO add security
//@Tag(name = "User Management", description = "Operations pertaining to users")
public class UsersController {

    private final UserService userService;

    public UsersController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> usersDTO = userService.getAllUsers();
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(usersDTO.size()))
                .body(usersDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        UserDTO userDTO = userService.getUserById(id);
        return ResponseEntity.ok(userDTO);
    }

    @PostMapping("")
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserCreateDTO userCreateDTO) {
        UserDTO createUser = userService.createUser(userCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createUser);
    }

    @PutMapping("/{id}")
    @PreAuthorize("@userUtils.isAuthor(#id)")
    public ResponseEntity<UserDTO> updateUserById(@PathVariable Long id,
                                                  @Valid @RequestBody UserUpdateDTO userUpdateDTO) {
        UserDTO updateUser = userService.updateUser(id, userUpdateDTO);
        return ResponseEntity.ok(updateUser);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@userUtils.isAuthor(#id)")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
