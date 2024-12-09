package hexlet.code.service;

import hexlet.code.dto.task.TaskCreateDTO;
import hexlet.code.dto.task.TaskDTO;
import hexlet.code.dto.task.TaskParamsDTO;
import hexlet.code.dto.task.TaskUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.specification.TaskSpecification;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TaskService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskService.class);
    private final TaskMapper taskMapper;
    private final TaskRepository taskRepository;
    private final TaskSpecification taskSpecification;
    private final UserRepository userRepository;
    private final TaskStatusRepository taskStatusRepository;
    private final LabelRepository labelRepository;

    public List<TaskDTO> getAllTasks(TaskParamsDTO taskParamsDTO) {
        Specification<Task> spec = taskSpecification.build(taskParamsDTO);
        return taskRepository.findAll(spec).stream()
                .map(taskMapper::map)
                .toList();
    }

    public TaskDTO getTaskById(Long id) {
        Task task = taskRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Task with id '%d' not found", id));
        return taskMapper.map(task);
    }

    @Transactional
    public TaskDTO create(TaskCreateDTO taskCreateDTO) {
        Task task = taskMapper.map(taskCreateDTO);
        task.setAssignee(userRepository.findById(taskCreateDTO.getAssigneeId()).orElseThrow(() ->
                new ResourceNotFoundException("User with id '%d' not found", taskCreateDTO.getAssigneeId())));
        task.setTaskStatus(taskStatusRepository.findBySlug(taskCreateDTO.getStatus()).orElseThrow(() ->
                new ResourceNotFoundException("Task status '%s' not found", taskCreateDTO.getStatus())));
        task.setLabels(labelRepository.findAllById(taskCreateDTO.getTaskLabelIds()).stream()
                .collect(HashSet::new, Set::add, Set::addAll));
        task = taskRepository.save(task);
        LOGGER.info("Task created with id: {}", task.getId());
        TaskDTO taskDTO = taskMapper.map(task);
        taskDTO.setId(task.getId());
        taskDTO.setStatus(task.getTaskStatus().getSlug());
        taskDTO.setAssignee(task.getAssignee().getId());
        taskDTO.setTaskLabelIds(task.getLabels().stream().map(Label::getId).collect(Collectors.toSet()));
        return taskDTO;
    }

    @Transactional
    public TaskDTO updateById(Long id, TaskUpdateDTO taskUpdateDTO) {
        Task task = taskRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Task with id '%d' not found", id));
        taskMapper.update(taskUpdateDTO, task);
        LOGGER.info("Updated task with id: {}", id);
        task = taskRepository.save(task);
        return taskMapper.map(task);
    }

    @Transactional
    public void deleteById(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new ResourceNotFoundException("Task with id '%d' not found", id);
        }
        taskRepository.deleteById(id);
        LOGGER.info("Deleted task with id: {}", id);
    }
}
