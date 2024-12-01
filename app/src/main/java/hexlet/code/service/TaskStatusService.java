package hexlet.code.service;

import hexlet.code.dto.taskstatus.TaskStatusCreateDTO;
import hexlet.code.dto.taskstatus.TaskStatusDTO;
import hexlet.code.dto.taskstatus.TaskStatusUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.TaskStatusMapper;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskStatusService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskService.class);
    private final TaskStatusRepository taskStatusRepository;
    private final TaskStatusMapper taskStatusMapper;

    @Autowired
    public TaskStatusService(TaskStatusRepository taskStatusRepository, TaskStatusMapper taskStatusMapper) {
        this.taskStatusRepository = taskStatusRepository;
        this.taskStatusMapper = taskStatusMapper;
    }

    public List<TaskStatusDTO> getAllTaskStatuses() {
        return taskStatusRepository.findAll().stream()
                .map(taskStatusMapper::map)
                .toList();
    }

    public TaskStatusDTO getTaskStatusById(Long id) {
        TaskStatus taskStatus = taskStatusRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Task status with id '%d' not found", id));
        return taskStatusMapper.map(taskStatus);
    }

    @Transactional
    public TaskStatusDTO createTaskStatus(TaskStatusCreateDTO taskStatusCreateDTO) {
        TaskStatus taskStatus = taskStatusMapper.map(taskStatusCreateDTO);
        taskStatus = taskStatusRepository.save(taskStatus);
        LOGGER.info("Created task status with id: {}", taskStatus.getId());
        return taskStatusMapper.map(taskStatus);
    }

    @Transactional
    public TaskStatusDTO updateTaskStatus(Long id, TaskStatusUpdateDTO taskStatusUpdateDTO) {
        TaskStatus taskStatus = taskStatusRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Task status with id '%d' not found", id));
        taskStatusMapper.update(taskStatusUpdateDTO, taskStatus);
        LOGGER.info("Updated task status with id: {}", id);
        taskStatusRepository.save(taskStatus);
        return taskStatusMapper.map(taskStatus);
    }

    @Transactional
    public void deleteById(Long id) {
        if (!taskStatusRepository.existsById(id)) {
            throw new ResourceNotFoundException("Task status with id '%d' not found", id);
        }
        taskStatusRepository.deleteById(id);
        LOGGER.info("Deleted task status with id: {}", id);
    }
}
