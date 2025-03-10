package hexlet.code.mapper;

import hexlet.code.dto.taskstatus.TaskStatusCreateDTO;
import hexlet.code.dto.taskstatus.TaskStatusDTO;
import hexlet.code.dto.taskstatus.TaskStatusUpdateDTO;
import hexlet.code.model.TaskStatus;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = {JsonNullableMapper.class,
                ReferenceMapper.class})
public abstract class TaskStatusMapper {

    public abstract TaskStatusDTO map(TaskStatus model);

    public abstract TaskStatus map(TaskStatusCreateDTO dto);

    public abstract void update(TaskStatusUpdateDTO dto, @MappingTarget TaskStatus model);
}
