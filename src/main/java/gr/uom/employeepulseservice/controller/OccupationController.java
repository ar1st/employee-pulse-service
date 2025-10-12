package gr.uom.employeepulseservice.controller;

import gr.uom.employeepulseservice.controller.dto.OccupationDto;
import gr.uom.employeepulseservice.controller.dto.SaveOccupationDto;
import gr.uom.employeepulseservice.service.OccupationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("occupations")
@RequiredArgsConstructor
public class OccupationController {

    private final OccupationService occupationService;

    @GetMapping
    public ResponseEntity<List<OccupationDto>> findAll() {
        return ResponseEntity.ok(occupationService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OccupationDto> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(occupationService.findOccupationById(id));
    }

    @PostMapping
    public ResponseEntity<Void> createOccupation(@RequestBody SaveOccupationDto dto) {
        occupationService.createOccupation(dto);

        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateOccupation(@PathVariable Integer id, @RequestBody SaveOccupationDto dto) {
        occupationService.updateOccupation(id, dto);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOccupation(@PathVariable Integer id) {
        occupationService.deleteOccupation(id);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/bulk")
    public ResponseEntity<Void> bulkCreateOccupations(@RequestBody List<SaveOccupationDto> dtos) {
        occupationService.bulkCreateOccupations(dtos);
        return ResponseEntity.ok().build();
    }

}