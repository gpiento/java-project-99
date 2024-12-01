package hexlet.code.service;

import hexlet.code.dto.task.TaskCreateDTO;
import hexlet.code.dto.task.TaskDTO;
import hexlet.code.dto.task.TaskParamsDTO;
import hexlet.code.dto.task.TaskUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.model.Task;
import hexlet.code.repository.TaskRepository;
import hexlet.code.specification.TaskSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskService.class);
    private final TaskMapper taskMapper;
    private final TaskRepository taskRepository;
    private final TaskSpecification taskSpecification;

    @Autowired
    public TaskService(TaskMapper taskMapper, TaskRepository taskRepository, TaskSpecification taskSpecification) {
        this.taskMapper = taskMapper;
        this.taskRepository = taskRepository;
        this.taskSpecification = taskSpecification;
    }

    public List<TaskDTO> getAllTasks(TaskParamsDTO taskParamsDTO) {
        Specification<Task> spec = taskSpecification.build(taskParamsDTO);
        return taskRepository.findAll(spec).stream().map(taskMapper::map).toList();
    }

    public TaskDTO getTaskById(Long id) {
        Task task = taskRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Task with id '%d' not found", id));
        return taskMapper.map(task);
    }

    public TaskDTO createTask(TaskCreateDTO taskCreateDTO) {
        Task task = taskMapper.map(taskCreateDTO);
        task = taskRepository.save(task);
        LOGGER.info("Task created with id: {}", task.getId());
        return taskMapper.map(task);
    }

    public TaskDTO updateTaskById(Long id, TaskUpdateDTO taskUpdateDTO) {
        Task task = taskRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Task with id '%d' not found", id));
        taskMapper.update(taskUpdateDTO, task);
        task = taskRepository.save(task);
        return taskMapper.map(task);
    }

    public void deleteById(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new ResourceNotFoundException("Task with id '%d' not found", id);
        }
        taskRepository.deleteById(id);
    }
}
