package hexlet.code.mapper;

import hexlet.code.dto.task.TaskCreateDTO;
import hexlet.code.dto.task.TaskDTO;
import hexlet.code.dto.task.TaskUpdateDTO;
import hexlet.code.exception.TaskStatusNotFoundException;
import hexlet.code.model.Label;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
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

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = {JsonNullableMapper.class,
                ReferenceMapper.class})
public abstract class TaskMapper {
    @Autowired
    private TaskStatusRepository taskStatusRepository;
    @Autowired
    private LabelRepository labelRepository;

    @Named("toLongIds")
    public Set<Long> toLongIds(Set<Label> labels) {
        return labels.stream().map(Label::getId).collect(Collectors.toSet());
    }

    @Named("getLabelIds")
    public Set<Label> getLabelIds(Set<Long> labels) {
        return labelRepository.findAllById(labels).stream()
                .collect(LinkedHashSet::new, Set::add, Set::addAll);
    }

    @Named("getLabelsByIds")
    public Set<Label> getLabelsByIds(JsonNullable<Set<Long>> ids) {
        if (ids.isPresent() && ids.get() != null) {
            return getLabelIds(ids.get());
        }
        return null;
    }

    @Named("taskStatusFromSlug")
    public TaskStatus taskStatusFromSlug(String slug) {
        return taskStatusRepository.findBySlug(slug)
                .orElseThrow(() -> new TaskStatusNotFoundException(slug));
    }

    @Mapping(target = "title", source = "name")
    @Mapping(target = "content", source = "description")
    @Mapping(target = "status", source = "taskStatus.slug")
    @Mapping(target = "assignee", source = "assignee.id")
    @Mapping(target = "taskLabelIds", source = "labels", qualifiedByName = "toLongIds")
    public abstract TaskDTO map(Task model);

    @Mapping(target = "name", source = "title")
    @Mapping(target = "description", source = "content")
    @Mapping(target = "taskStatus", source = "status", qualifiedByName = "taskStatusFromSlug")
    @Mapping(target = "assignee", source = "assigneeId")
    @Mapping(target = "labels", source = "taskLabelIds", qualifiedByName = "getLabelsByIds")
    public abstract Task map(TaskCreateDTO dto);

    @Mapping(target = "name", source = "title")
    @Mapping(target = "description", source = "content")
    @Mapping(target = "taskStatus", source = "status", qualifiedByName = "taskStatusFromSlug")
    @Mapping(target = "labels", source = "taskLabelIds", qualifiedByName = "getLabelIds")
    @Mapping(target = "assignee", source = "assigneeId")
    public abstract void update(TaskUpdateDTO dto, @MappingTarget Task model);
}
