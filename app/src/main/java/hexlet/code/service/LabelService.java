package hexlet.code.service;

import hexlet.code.dto.label.LabelCreateDTO;
import hexlet.code.dto.label.LabelDTO;
import hexlet.code.dto.label.LabelUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.LabelMapper;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LabelService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
    private final LabelRepository labelRepository;
    private final LabelMapper labelMapper;

    @Autowired
    public LabelService(LabelRepository labelRepository, LabelMapper labelMapper) {
        this.labelRepository = labelRepository;
        this.labelMapper = labelMapper;
    }

    public List<LabelDTO> getAllLabels() {
        return labelRepository.findAll().stream()
                .map(labelMapper::map)
                .toList();
    }

    public LabelDTO getLabelById(Long id) {
        Label label = labelRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Label with id '%d' not found", id));
        return labelMapper.map(label);
    }

    @Transactional
    public LabelDTO createLabel(LabelCreateDTO labelCreateDTO) {
        Label label = labelMapper.map(labelCreateDTO);
        label = labelRepository.save(label);
        LOGGER.info("Created label with id: {}", label.getId());
        return labelMapper.map(label);
    }

    @Transactional
    public LabelDTO updateLabel(Long id, LabelUpdateDTO labelUpdateDTO) {
        Label label = labelRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Label with id '%d' not found", id));
        labelMapper.update(labelUpdateDTO, label);
        LOGGER.info("Updated label with id: {}", label.getId());
        return labelMapper.map(labelRepository.save(label));
    }

    @Transactional
    public void deleteById(Long id) {
        if (!labelRepository.existsById(id)) {
            throw new ResourceNotFoundException("Label with id '%d' not found", id);
        }
        labelRepository.deleteById(id);
        LOGGER.info("Deleted label with id: {}", id);
    }
}
