package hexlet.code.controller;

import hexlet.code.model.TaskStatus;
import hexlet.code.service.TaskStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TaskStatusController {

    @Autowired
    private TaskStatusService taskStatusService;

    @GetMapping("/task_statuses")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<TaskStatus> index() {

        TaskStatus taskStatus = taskStatusService.creeate();
        return ResponseEntity.ok().body(taskStatus);
    }
}
