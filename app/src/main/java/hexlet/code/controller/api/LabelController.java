package hexlet.code.controller.api;

import hexlet.code.dto.label.LabelCreateDTO;
import hexlet.code.dto.label.LabelDTO;
import hexlet.code.dto.label.LabelUpdateDTO;
import hexlet.code.service.LabelService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/labels")
@AllArgsConstructor
public class LabelController {

    private final LabelService labelService;

    @GetMapping("")
    public ResponseEntity<List<LabelDTO>> getAllLabels() {
        List<LabelDTO> labelDTOS = labelService.getAllLabels();
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(labelDTOS.size()))
                .body(labelDTOS);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LabelDTO> getLabelById(@PathVariable Long id) {
        LabelDTO labelDTO = labelService.getLabelById(id);
        return ResponseEntity.ok(labelDTO);
    }

    @ApiResponse(responseCode = "201", description = "Label created")
    @PostMapping("")
    public ResponseEntity<LabelDTO> createLabel(@Valid @RequestBody LabelCreateDTO labelCreateDTO) {
        LabelDTO labelDTO = labelService.createLabel(labelCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(labelDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LabelDTO> update(@PathVariable Long id,
                                           @Valid @RequestBody LabelUpdateDTO labelUpdateDTO) {
        LabelDTO labelDTO = labelService.updateLabel(id, labelUpdateDTO);
        return ResponseEntity.ok(labelDTO);
    }

    @ApiResponse(responseCode = "204", description = "Label deleted")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLabel(@PathVariable Long id) {
        labelService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
