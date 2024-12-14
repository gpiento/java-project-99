package hexlet.code.controller.api;

import hexlet.code.dto.taskstatus.TaskStatusCreateDTO;
import hexlet.code.dto.taskstatus.TaskStatusDTO;
import hexlet.code.dto.taskstatus.TaskStatusUpdateDTO;
import hexlet.code.service.TaskStatusService;
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
@RequestMapping("/api/task_statuses")
@AllArgsConstructor
public class TaskStatusController {

    private final TaskStatusService taskStatusService;

    @GetMapping("")
    public ResponseEntity<List<TaskStatusDTO>> getAllTaskStatuses() {
        List<TaskStatusDTO> taskStatusDTO = taskStatusService.getAllTaskStatuses();
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(taskStatusDTO.size()))
                .body(taskStatusDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskStatusDTO> getTaskStatusById(@PathVariable Long id) {
        TaskStatusDTO taskStatusDTO = taskStatusService.getTaskStatusById(id);
        return ResponseEntity.status(HttpStatus.OK).body(taskStatusDTO);
    }

    @ApiResponse(responseCode = "201", description = "Сreated",
            content = @Content(schema = @Schema(implementation = TaskStatusDTO.class)))
    @PostMapping("")
    public ResponseEntity<TaskStatusDTO> createTaskStatus(@Valid @RequestBody TaskStatusCreateDTO taskStatusCreateDTO) {
        TaskStatusDTO taskStatusDTO = taskStatusService.createTaskStatus(taskStatusCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(taskStatusDTO);
    }

    @ApiResponse(responseCode = "200", description = "OK")
    @PutMapping("/{id}")
    public ResponseEntity<TaskStatusDTO> updateTaskStatusById(
            @PathVariable Long id,
            @Valid @RequestBody TaskStatusUpdateDTO taskStatusUpdateDTO) {
        TaskStatusDTO taskStatusDTO = taskStatusService.updateTaskStatus(id, taskStatusUpdateDTO);
        return ResponseEntity.status(HttpStatus.OK).body(taskStatusDTO);
    }

    @ApiResponse(responseCode = "204", description = "Task status deleted")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTaskStatusById(@PathVariable Long id) {
        taskStatusService.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
