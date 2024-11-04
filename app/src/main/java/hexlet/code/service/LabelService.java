package hexlet.code.service;

import hexlet.code.dto.label.LabelCreateDTO;
import hexlet.code.dto.label.LabelDTO;
import hexlet.code.dto.label.LabelUpdateDTO;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.mapper.LabelMapper;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LabelService {

    @Autowired
    private LabelRepository labelRepository;
    @Autowired
    private LabelMapper labelMapper;

    public LabelDTO getLabelById(Long id) {
        Label label = labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Label '" + id + "' not found"));
        return labelMapper.map(label);
    }

    public LabelDTO findByName(String name) {
        Optional<Label> labelOptional = labelRepository.findByName(name);
        if (labelOptional.isEmpty()) {
            throw new ResourceNotFoundException("Label with name '" + name + "' not found");
        }
        return labelMapper.map(labelOptional.get());
    }

    public List<LabelDTO> getAll() {
        List<Label> labelList = labelRepository.findAll();
        return labelList.stream()
                .map(labelMapper::map)
                .toList();
    }

    public LabelDTO create(LabelCreateDTO labelCreateDTO) {
        Optional<Label> labelOptional = labelRepository.findByName(labelCreateDTO.getName());
        if (labelOptional.isPresent()) {
            throw new ResourceNotFoundException("Label with name " + labelCreateDTO.getName() + " already exists");
        }
        Label label = labelMapper.map(labelCreateDTO);
        label = labelRepository.save(label);
        return labelMapper.map(label);
    }

    public LabelDTO update(Long id, LabelUpdateDTO labelUpdateDTO) {
        Label label = labelRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Label " + id + " not found"));
        labelMapper.update(labelUpdateDTO, label);
        label = labelRepository.save(label);
        return labelMapper.map(label);
    }

    public void delete(Long id) {
        labelRepository.deleteById(id);
    }
}
