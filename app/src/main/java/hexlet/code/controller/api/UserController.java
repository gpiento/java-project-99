package hexlet.code.controller.api;

import hexlet.code.dto.user.UserCreateDTO;
import hexlet.code.dto.user.UserDTO;
import hexlet.code.dto.user.UserUpdateDTO;
import hexlet.code.service.UserService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> userDTOS = userService.getAll();
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(userDTOS.size()))
                .body(userDTOS);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        UserDTO userDTO = userService.getById(id);
        return ResponseEntity.status(HttpStatus.OK).body(userDTO);
    }

    @ApiResponse(responseCode = "201", description = "Created",
            content = @Content(schema = @Schema(implementation = UserDTO.class)))
    @PostMapping("")
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserCreateDTO userCreateDTO) {
        UserDTO createUser = userService.create(userCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createUser);
    }

    @ApiResponse(responseCode = "200", description = "OK")
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUserById(@PathVariable Long id,
                                                  @Valid @RequestBody UserUpdateDTO userUpdateDTO) {
        UserDTO updatedUser = userService.updateById(id, userUpdateDTO);
        return ResponseEntity.status(HttpStatus.OK).body(updatedUser);
    }

    @ApiResponse(responseCode = "204", description = "No Content")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserById(@PathVariable Long id) {
        userService.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
