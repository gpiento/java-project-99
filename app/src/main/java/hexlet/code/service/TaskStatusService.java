package hexlet.code.service;

import hexlet.code.dto.taskstatus.TaskStatusCreateDTO;
import hexlet.code.dto.taskstatus.TaskStatusDTO;
import hexlet.code.dto.taskstatus.TaskStatusUpdateDTO;
import hexlet.code.exception.TaskStatusAlreadyExistsException;
import hexlet.code.exception.TaskStatusNotFoundException;
import hexlet.code.mapper.TaskStatusMapper;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class TaskStatusService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskStatusService.class);

    private final TaskStatusRepository taskStatusRepository;

    private final TaskStatusMapper taskStatusMapper;

    @Transactional(readOnly = true)
    public List<TaskStatusDTO> getAll() {
        return taskStatusRepository.findAll().stream()
                .map(taskStatusMapper::map)
                .toList();
    }

    @Transactional(readOnly = true)
    public TaskStatusDTO getById(Long id) {
        return taskStatusRepository.findById(id)
                .map(taskStatusMapper::map)
                .orElseThrow(() -> new TaskStatusNotFoundException(id));
    }

    @PreAuthorize("isAuthenticated()")
    @Transactional
    public TaskStatusDTO create(TaskStatusCreateDTO taskStatusCreateDTO) {
        if (taskStatusRepository.existsBySlug(taskStatusCreateDTO.getSlug())) {
            throw new TaskStatusAlreadyExistsException(taskStatusCreateDTO.getSlug());
        }
        TaskStatus taskStatus = taskStatusMapper.map(taskStatusCreateDTO);
        taskStatus = taskStatusRepository.save(taskStatus);
        LOGGER.info("Created task status with id: {}", taskStatus);
        return taskStatusMapper.map(taskStatus);
    }

    @PreAuthorize("isAuthenticated()")
    @Transactional
    public TaskStatusDTO updateById(Long id, TaskStatusUpdateDTO taskStatusUpdateDTO) {
        return taskStatusRepository.findById(id)
                .map(taskStatus -> {
                            LOGGER.info("Updated task status: {}", taskStatus);
                            taskStatusMapper.update(taskStatusUpdateDTO, taskStatus);
                            return taskStatusMapper.map(taskStatus);
                        }
                )
                .orElseThrow(() -> new TaskStatusNotFoundException(id));
    }

    @PreAuthorize("isAuthenticated()")
    @Transactional
    public void deleteById(Long id) {
        taskStatusRepository.findById(id)
                .ifPresentOrElse(
                        taskStatus -> {
                            taskStatusRepository.deleteById(id);
                            LOGGER.info("Deleted task status with id: {}", id);
                        },
                        () -> {
                            throw new TaskStatusNotFoundException(id);
                        }
                );
    }
}
