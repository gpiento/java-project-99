package hexlet.code.service;

import hexlet.code.dto.taskstatus.TaskStatusCreateDTO;
import hexlet.code.dto.taskstatus.TaskStatusDTO;
import hexlet.code.dto.taskstatus.TaskStatusUpdateDTO;
import hexlet.code.exception.ResourceAlreadyExistsException;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.TaskStatusMapper;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class TaskStatusService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskStatusService.class);

    private final TaskStatusRepository taskStatusRepository;

    private final TaskStatusMapper taskStatusMapper;

    public List<TaskStatusDTO> getAll() {
        return taskStatusRepository.findAll().stream()
                .map(taskStatusMapper::map)
                .toList();
    }

    public TaskStatusDTO getById(Long id) {
        TaskStatus taskStatus = findTaskStatusById(id);
        return taskStatusMapper.map(taskStatus);
    }

    @Transactional
    public TaskStatusDTO create(TaskStatusCreateDTO taskStatusCreateDTO) {
        if (taskStatusRepository.existsBySlug(taskStatusCreateDTO.getSlug())) {
            throw new ResourceAlreadyExistsException("Task status with slug '"
                    + taskStatusCreateDTO.getSlug() + "' already exists");
        }
        TaskStatus taskStatus = taskStatusMapper.map(taskStatusCreateDTO);
        taskStatus = taskStatusRepository.save(taskStatus);
        LOGGER.info("Created task status with id: {}", taskStatus.getId());
        return taskStatusMapper.map(taskStatus);
    }

    @Transactional
    public TaskStatusDTO updateById(Long id, TaskStatusUpdateDTO taskStatusUpdateDTO) {
        TaskStatus taskStatus = findTaskStatusById(id);
        taskStatusMapper.update(taskStatusUpdateDTO, taskStatus);
        LOGGER.info("Updated task status with id: {}", id);
        taskStatus = taskStatusRepository.save(taskStatus);
        return taskStatusMapper.map(taskStatus);
    }

    @Transactional
    public void deleteById(Long id) {
        findTaskStatusById(id);
        taskStatusRepository.deleteById(id);
        LOGGER.info("Deleted task status with id: {}", id);
    }

    private TaskStatus findTaskStatusById(Long id) {
        return taskStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task status with id '" + id + "' not found"));
    }
}
