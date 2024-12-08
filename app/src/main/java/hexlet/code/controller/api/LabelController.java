package hexlet.code.controller.api;

import hexlet.code.dto.label.LabelCreateDTO;
import hexlet.code.dto.label.LabelDTO;
import hexlet.code.dto.label.LabelUpdateDTO;
import hexlet.code.exception.LabelAlreadyExistsException;
import hexlet.code.exception.ResourceNotFoundException;
import hexlet.code.service.LabelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "Labels", description = "Operations with labels")
@RestController
@RequestMapping("/api/labels")
@AllArgsConstructor
public class LabelController {

    private final LabelService labelService;

    @Operation(summary = "Get all labels")
    @GetMapping("")
    public ResponseEntity<List<LabelDTO>> getAllLabels() {
        List<LabelDTO> labelDTOS = labelService.getAllLabels();
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(labelDTOS.size()))
                .body(labelDTOS);
    }

    @Operation(summary = "Get label by ID")
    @ApiResponse(responseCode = "200", description = "Label found")
    @ApiResponse(responseCode = "404", description = "Label not found")
    @GetMapping("/{id}")
    public ResponseEntity<LabelDTO> getLabelById(@PathVariable Long id) {
        try {
            LabelDTO labelDTO = labelService.getLabelById(id);
            return ResponseEntity.ok(labelDTO);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Create new label")
    @ApiResponse(responseCode = "201", description = "Label created")
    @ApiResponse(responseCode = "409", description = "Label already exists")
    @PostMapping("")
    public ResponseEntity<?> createLabel(@Valid @RequestBody LabelCreateDTO labelCreateDTO) {
        try {
            LabelDTO labelDTO = labelService.createLabel(labelCreateDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(labelDTO);
        } catch (LabelAlreadyExistsException message) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(message.getMessage());
        }
    }

    @Operation(summary = "Update label")
    @ApiResponse(responseCode = "200", description = "Label updated")
    @ApiResponse(responseCode = "404", description = "Label not found")
    @PutMapping("/{id}")
    public ResponseEntity<LabelDTO> update(@PathVariable Long id,
                                           @Valid @RequestBody LabelUpdateDTO labelUpdateDTO) {
        try {
            LabelDTO labelDTO = labelService.updateLabel(id, labelUpdateDTO);
            return ResponseEntity.ok(labelDTO);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Delete label")
    @ApiResponse(responseCode = "204", description = "Label deleted")
    @ApiResponse(responseCode = "404", description = "Label not found")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLabel(@PathVariable Long id) {
        try {
            labelService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
