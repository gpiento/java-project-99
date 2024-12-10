package hexlet.code.controller.api;

import hexlet.code.dto.task.TaskCreateDTO;
import hexlet.code.dto.task.TaskDTO;
import hexlet.code.dto.task.TaskParamsDTO;
import hexlet.code.dto.task.TaskUpdateDTO;
import hexlet.code.service.TaskService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@AllArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @GetMapping("")
    public ResponseEntity<List<TaskDTO>> getAllTasks(TaskParamsDTO taskParamsDTO,
                                                     @RequestParam(defaultValue = "1") Integer page) {
        List<TaskDTO> taskDTOS = taskService.getAllTasks(taskParamsDTO,
                PageRequest.of(page - 1, 10));
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(taskDTOS.size()))
                .body(taskDTOS);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskDTO> getTaskById(@PathVariable Long id) {
        TaskDTO taskDTO = taskService.getTaskById(id);
        return ResponseEntity.ok(taskDTO);
    }

    @ApiResponse(responseCode = "201", description = "Сreated",
            content = @Content(schema = @Schema(implementation = TaskDTO.class)))
    @PostMapping("")
    public ResponseEntity<TaskDTO> createTask(@Valid @RequestBody TaskCreateDTO taskCreateDTO) {
        TaskDTO taskDTO = taskService.create(taskCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(taskDTO);
    }

    @ApiResponse(responseCode = "200", description = "OK")
    @PutMapping("/{id}")
    @PreAuthorize("@userUtils.isCurrentUser(#id)")
    public ResponseEntity<TaskDTO> updateTaskById(@PathVariable Long id,
                                                  @Valid @RequestBody TaskUpdateDTO taskUpdateDTO) {
        TaskDTO taskDTO = taskService.updateById(id, taskUpdateDTO);
        return ResponseEntity.ok(taskDTO);
    }

    @ApiResponse(responseCode = "204", description = "No Content")
    @DeleteMapping("/{id}")
    @PreAuthorize("@userUtils.isCurrentUser(#id)")
    public ResponseEntity<Void> destroy(@PathVariable Long id) {
        taskService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
