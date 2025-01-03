package hexlet.code.service;

import hexlet.code.dto.task.TaskCreateDTO;
import hexlet.code.dto.task.TaskDTO;
import hexlet.code.dto.task.TaskParamsDTO;
import hexlet.code.dto.task.TaskUpdateDTO;
import hexlet.code.exception.TaskAlreadyExistsException;
import hexlet.code.exception.TaskNotFoundException;
import hexlet.code.exception.TaskStatusNotFoundException;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.model.Task;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.specification.TaskSpecification;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class TaskService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskService.class);

    private final TaskMapper taskMapper;

    private final TaskRepository taskRepository;

    private final TaskSpecification taskSpecification;

    private final TaskStatusRepository taskStatusRepository;

    @Transactional(readOnly = true)
    public List<TaskDTO> getAll(TaskParamsDTO taskParamsDTO, PageRequest pageRequest) {
        Specification<Task> spec = taskSpecification.build(taskParamsDTO);
        return taskRepository.findAll(spec, pageRequest).stream()
                .map(taskMapper::map)
                .toList();
    }

    @Transactional(readOnly = true)
    public TaskDTO getById(Long id) {
        return taskRepository.findById(id)
                .map(taskMapper::map)
                .orElseThrow(() -> new TaskNotFoundException(id));
    }

    @Transactional
    public TaskDTO create(TaskCreateDTO taskCreateDTO) {
        if (taskRepository.existsByName(taskCreateDTO.getTitle())) {
            throw new TaskAlreadyExistsException(taskCreateDTO.getTitle());
        }
        Task task = taskMapper.map(taskCreateDTO);
        if (taskCreateDTO.getStatus() == null || !taskCreateDTO.getStatus().isPresent()) {
            task.setTaskStatus(taskStatusRepository.findBySlug("draft")
                    .orElseThrow(() -> new TaskStatusNotFoundException("draft")));
        }
        task = taskRepository.save(task);
        LOGGER.info("Task created: {}", task);
        return taskMapper.map(task);
    }

    @Transactional
    public TaskDTO updateById(Long id, TaskUpdateDTO taskUpdateDTO) {
        return taskRepository.findById(id)
                .map(task -> {
                    LOGGER.info("Updated task: {}", task);
                    taskMapper.update(taskUpdateDTO, task);
                    return taskMapper.map(task);
                })
                .orElseThrow(() -> new TaskNotFoundException(id));
    }

    @Modifying
    @Transactional
    public void deleteById(Long id) {
        taskRepository.findById(id)
                .ifPresentOrElse(
                        task -> {
                            taskRepository.delete(task);
                            LOGGER.info("Deleted task with id: {}", id);
                        },
                        () -> {
                            throw new TaskNotFoundException(id);
                        }
                );
    }
}
