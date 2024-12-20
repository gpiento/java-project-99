package hexlet.code.service;

import hexlet.code.dto.task.TaskCreateDTO;
import hexlet.code.dto.task.TaskDTO;
import hexlet.code.dto.task.TaskParamsDTO;
import hexlet.code.dto.task.TaskUpdateDTO;
import hexlet.code.exception.ResourceAlreadyExistsException;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.model.Task;
import hexlet.code.repository.TaskRepository;
import hexlet.code.specification.TaskSpecification;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class TaskService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskService.class);

    private final TaskMapper taskMapper;

    private final TaskRepository taskRepository;

    private final TaskSpecification taskSpecification;

    public List<TaskDTO> getAll(TaskParamsDTO taskParamsDTO, PageRequest pageRequest) {
        Specification<Task> spec = taskSpecification.build(taskParamsDTO);
        return taskRepository.findAll(spec, pageRequest).stream()
                .map(taskMapper::map)
                .toList();
    }

    public TaskDTO getById(Long id) {
        Task task = taskRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Task with id '" + id + "' not found"));
        return taskMapper.map(task);
    }

    @Transactional
    public TaskDTO create(TaskCreateDTO taskCreateDTO) {
        if (taskRepository.existsByName(taskCreateDTO.getTitle())) {
            throw new ResourceAlreadyExistsException("Task with name '"
                    + taskCreateDTO.getTitle() + "' already exists");
        }
        Task task = taskMapper.map(taskCreateDTO);
        task = taskRepository.save(task);
        LOGGER.info("Task created with id: {}", task.getId());
        return taskMapper.map(task);
    }

    @Transactional
    public TaskDTO updateById(Long id, TaskUpdateDTO taskUpdateDTO) {
        Task task = taskRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Task with id '" + id + "' not found"));
        taskMapper.update(taskUpdateDTO, task);
        LOGGER.info("Updated task with id: {}", id);
        task = taskRepository.save(task);
        return taskMapper.map(task);
    }

    @Transactional
    public void deleteById(Long id) {
        taskRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Task with id '" + id + "' not found"));
        taskRepository.deleteById(id);
        LOGGER.info("Deleted task with id: {}", id);
    }
}
