package hexlet.code.service;

import hexlet.code.dto.label.LabelCreateDTO;
import hexlet.code.dto.label.LabelDTO;
import hexlet.code.dto.label.LabelUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.LabelMapper;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LabelService {

    @Autowired
    private LabelRepository labelRepository;
    @Autowired
    private LabelMapper labelMapper;

    public List<LabelDTO> getAll() {
        return labelRepository.findAllDto();
    }

    public LabelDTO findById(Long id) {
        Label label = labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Label with id '%d' not found", id));
        return labelMapper.map(label);
    }

    public LabelDTO findByName(String name) {
        Label label = labelRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Label with id '%d' not found", name));
        return labelMapper.map(label);
    }

    @Transactional
    public LabelDTO create(LabelCreateDTO labelCreateDTO) {
        return labelMapper.map(labelRepository.save(labelMapper.map(labelCreateDTO)));
    }

    @Transactional
    public LabelDTO update(Long id, LabelUpdateDTO labelUpdateDTO) {
        Label label = labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Label with id '%d' not found", id));
        labelMapper.update(labelUpdateDTO, label);
        return labelMapper.map(labelRepository.save(label));
    }

    @Transactional
    public void deleteById(Long id) {
        if (!labelRepository.existsById(id)) {
            throw new ResourceNotFoundException("Label with id '%d' not found", id);
        }
        labelRepository.deleteById(id);
    }
}
