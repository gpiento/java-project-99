package hexlet.code.controller.api;

import hexlet.code.dto.taskstatus.TaskStatusCreateDTO;
import hexlet.code.dto.taskstatus.TaskStatusDTO;
import hexlet.code.dto.taskstatus.TaskStatusUpdateDTO;
import hexlet.code.service.TaskStatusService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/api/task_statuses")
public class TaskStatusController {

    @Autowired
    private TaskStatusService taskStatusService;

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
        return ResponseEntity.ok(taskStatusDTO);
    }

    @PostMapping("")
    public ResponseEntity<TaskStatusDTO> createTaskStatus(
            @Valid @RequestBody TaskStatusCreateDTO taskStatusCreateDTO) {
        TaskStatusDTO taskStatusDTO = taskStatusService.createTaskStatus(taskStatusCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(taskStatusDTO);
    }

    @PutMapping("/{id}")
    @PreAuthorize("@userUtils.isAuthor(#id)")
    public ResponseEntity<TaskStatusDTO> updateTaskStatusById(
            @PathVariable Long id,
            @Valid @RequestBody TaskStatusUpdateDTO taskStatusUpdateDTO) {
        TaskStatusDTO taskStatusDTO = taskStatusService.updateTaskStatus(id, taskStatusUpdateDTO);
        return ResponseEntity.ok(taskStatusDTO);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@userUtils.isAuthor(#id)")
    public ResponseEntity<Void> deleteTaskStatus(@PathVariable Long id) {
        taskStatusService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
