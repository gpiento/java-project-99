package hexlet.code.service;

import hexlet.code.dto.task.TaskCreateDTO;
import hexlet.code.dto.task.TaskDTO;
import hexlet.code.dto.task.TaskUpdateDTO;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.model.Task;
import hexlet.code.repository.TaskRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    private final TaskMapper taskMapper;
    private final TaskRepository taskRepository;

    public TaskService(TaskMapper taskMapper, TaskRepository taskRepository) {

        this.taskMapper = taskMapper;
        this.taskRepository = taskRepository;
    }

    public ResponseEntity<TaskDTO> getTaskById(Long id) {
        Optional<Task> taskOptional = taskRepository.findById(id);
        return taskOptional.map(t -> ResponseEntity.ok(taskMapper.map(t)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    public List<TaskDTO> getAll() {
        List<Task> taskStatusList = taskRepository.findAll();
        return taskStatusList.stream()
                .map(taskMapper::map)
                .toList();
    }

    public ResponseEntity<TaskDTO> create(TaskCreateDTO taskCreateDTO) {
        Optional<Task> taskOptional = taskRepository.findByName(taskCreateDTO.getName());
        if (taskOptional.isPresent()) {
            Task task = taskMapper.map(taskCreateDTO);
            task = taskRepository.save(task);
            return ResponseEntity.ok(taskMapper.map(task));
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    public ResponseEntity<TaskDTO> update(Long id, TaskUpdateDTO taskUpdateDTO) {
        Optional<Task> taskOptional = taskRepository.findById(id);
        if (taskOptional.isPresent()) {
            Task task = taskOptional.get();
            taskMapper.update(taskUpdateDTO, task);
            task = taskRepository.save(task);
            return ResponseEntity.ok(taskMapper.map(task));

        } else {
            return ResponseEntity.notFound().build();
        }
    }

    public ResponseEntity<HttpStatus> delete(Long id) {
        Optional<Task> taskOptional = taskRepository.findById(id);
        if (taskOptional.isPresent()) {
            taskRepository.delete(taskOptional.get());
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
