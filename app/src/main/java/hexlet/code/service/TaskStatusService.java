package hexlet.code.service;

import hexlet.code.dto.taskstatus.TaskStatusCreateDTO;
import hexlet.code.dto.taskstatus.TaskStatusDTO;
import hexlet.code.dto.taskstatus.TaskStatusUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.TaskStatusMapper;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskStatusService {

    @Autowired
    private TaskStatusRepository taskStatusRepository;
    @Autowired
    private TaskStatusMapper taskStatusMapper;

    public TaskStatusDTO getTaskStatusById(Long id) {
        TaskStatus taskStatus = taskStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task status with id '%d' not found".formatted(id)));
        return taskStatusMapper.map(taskStatus);
    }

    public List<TaskStatusDTO> getAll() {
        List<TaskStatus> taskStatusList = taskStatusRepository.findAll();
        return taskStatusList.stream()
                .map(taskStatusMapper::map)
                .toList();
    }

    public TaskStatusDTO create(TaskStatusCreateDTO taskStatusCreateDTO) {
        TaskStatus taskStatus = taskStatusMapper.map(taskStatusCreateDTO);
        taskStatus = taskStatusRepository.save(taskStatus);
        return taskStatusMapper.map(taskStatus);
    }

    public TaskStatusDTO update(Long id, TaskStatusUpdateDTO taskStatusUpdateDTO) {
        TaskStatus taskStatus = taskStatusRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task status with id '%d' not found".formatted(id)));
        taskStatusMapper.update(taskStatusUpdateDTO, taskStatus);
        taskStatusRepository.save(taskStatus);

        return taskStatusMapper.map(taskStatus);
    }

    public void deleteById(Long id) {
        taskStatusRepository.deleteById(id);
    }
}
