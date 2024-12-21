package hexlet.code.service;

import hexlet.code.dto.label.LabelCreateDTO;
import hexlet.code.dto.label.LabelDTO;
import hexlet.code.dto.label.LabelUpdateDTO;
import hexlet.code.exception.LabelAlreadyExistsException;
import hexlet.code.exception.LabelNotFoundException;
import hexlet.code.mapper.LabelMapper;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class LabelService {
    private static final Logger LOGGER = LoggerFactory.getLogger(LabelService.class);

    private final LabelRepository labelRepository;

    private final LabelMapper labelMapper;

    @Transactional(readOnly = true)
    public List<LabelDTO> getAll() {
        return labelRepository.findAll().stream()
                .map(labelMapper::map)
                .toList();
    }

    @Transactional(readOnly = true)
    public LabelDTO getById(Long id) {
        Label label = labelRepository.findById(id)
                .orElseThrow(() -> new LabelNotFoundException(id));
        return labelMapper.map(label);
    }

    @Transactional
    public LabelDTO create(LabelCreateDTO labelCreateDTO) {
        if (labelRepository.existsByName(labelCreateDTO.getName())) {
            throw new LabelAlreadyExistsException(labelCreateDTO.getName());
        }
        Label label = labelMapper.map(labelCreateDTO);
        label = labelRepository.save(label);
        LOGGER.info("Created label with id: {}", label.getId());
        return labelMapper.map(label);
    }

    @Transactional
    public LabelDTO updateById(Long id, LabelUpdateDTO labelUpdateDTO) {
        return labelRepository.findById(id)
                .map(label -> {
                            LOGGER.info("Updated label with id: {}", label);
                            labelMapper.update(labelUpdateDTO, label);
                            return labelMapper.map(label);
                        }
                )
                .orElseThrow(() -> new LabelNotFoundException(id));
    }

    @Transactional
    public void deleteById(Long id) {
        labelRepository.findById(id)
                .ifPresentOrElse(
                        label -> {
                            labelRepository.deleteById(id);
                            LOGGER.info("Deleted label with id: {}", id);
                        },
                        () -> {
                            throw new LabelNotFoundException(id);
                        }
                );
    }
}
