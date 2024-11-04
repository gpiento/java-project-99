package hexlet.code.mapper;

import hexlet.code.dto.label.LabelCreateDTO;
import hexlet.code.dto.label.LabelDTO;
import hexlet.code.dto.label.LabelUpdateDTO;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(
        uses = {
                JsonNullableMapper.class,
                ReferenceMapper.class,
                TaskMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public abstract class LabelMapper {

    @Autowired
    private LabelRepository labelRepository;

    public abstract LabelDTO map(Label model);

    public abstract Label map(LabelCreateDTO dto);

    public abstract void update(LabelUpdateDTO dto, @MappingTarget Label model);

    public Set<Long> toIds(Set<Label> labels) {
        return labels.stream().map(Label::getId).collect(Collectors.toSet());
    }

    public Set<Label> toEntity(Set<Long> listId) {
        return new HashSet<>(labelRepository.findAllById(listId));
    }
}
