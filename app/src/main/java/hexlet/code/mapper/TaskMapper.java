package hexlet.code.mapper;

import hexlet.code.dto.task.TaskCreateDTO;
import hexlet.code.dto.task.TaskDTO;
import hexlet.code.dto.task.TaskUpdateDTO;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskStatusRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(
        uses = {JsonNullableMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class TaskMapper {

    @Autowired
    private LabelRepository labelRepository;
    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Mapping(target = "name", source = "title")
    @Mapping(target = "description", source = "content")
    @Mapping(target = "taskStatus", source = "status", qualifiedByName = "statusToTaskStatus")
    @Mapping(target = "assignee", source = "assignee", qualifiedByName = "userToAssignee")
    @Mapping(target = "labels", source = "labelIds", qualifiedByName = "labelIdsToLabels")
    public abstract Task map(TaskDTO dto);

    @Mapping(target = "title", source = "name")
    @Mapping(target = "content", source = "description")
    @Mapping(target = "status", source = "taskStatus.slug")
    @Mapping(target = "assignee", source = "assignee.id")
    @Mapping(target = "labelIds", source = "labels", qualifiedByName = "labelsToLabelIds")
    public abstract TaskDTO map(Task model);

    @Mapping(target = "name", source = "title")
    @Mapping(target = "description", source = "content")
    @Mapping(target = "taskStatus", source = "status", qualifiedByName = "statusToTaskStatus")
    @Mapping(target = "assignee", source = "assigneeId", qualifiedByName = "userToAssignee")
    @Mapping(target = "labels", source = "labelIds", qualifiedByName = "labelIdsToLabels")
    public abstract Task map(TaskCreateDTO dto);


    @Mapping(target = "name", source = "title")
    @Mapping(target = "description", source = "content")
    @Mapping(target = "taskStatus", source = "status", qualifiedByName = "statusToTaskStatus")
    @Mapping(target = "labels", source = "labelIds", qualifiedByName = "labelIdsToLabels")
    @Mapping(target = "assignee", source = "assigneeId")
    public abstract void update(TaskUpdateDTO dto, @MappingTarget Task model);

    public abstract List<TaskDTO> map(List<Task> modelList);

    @Named("userToAssignee")
    public User userToAssignee(Long userId) {
        return userId == null ? null : User.builder().id(userId).build();
    }

    @Named("statusToTaskStatus")
    public TaskStatus statusToTaskStatus(JsonNullable<String> status) {
        return taskStatusRepository.findBySlug(status.get()).get();
    }

    @Named("labelIdsToLabels")
    public Set<Label> taskLabelIdsToLabels(JsonNullable<Set<Long>> labelIds) {
        return labelIds.get() == null ? new HashSet<>() : labelRepository.findByIdIn(labelIds.get());
    }

    @Named("slugToTaskStatus")
    public TaskStatus slugToTaskStatus(String slug) {
        return taskStatusRepository.findBySlug(slug).orElseThrow();
    }

    @Named("labelIdsToLabels")
    public Set<Label> labelIdsToLabels(Set<Long> labelIds) {
        return labelIds == null ? new HashSet<>() : labelRepository.findByIdIn(labelIds);
    }

    @Named("labelsToLabelIds")
    public Set<Long> labelsToLabelIds(Set<Label> labels) {
        return labels.isEmpty() ? new HashSet<>() : labels.stream()
                .map(Label::getId)
                .collect(Collectors.toSet());
    }
}
