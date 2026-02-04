package gr.uom.employeepulseservice.controller;

import gr.uom.employeepulseservice.controller.dto.OccupationDto;
import gr.uom.employeepulseservice.controller.dto.SaveOccupationDto;
import gr.uom.employeepulseservice.service.OccupationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("occupations")
@RequiredArgsConstructor
public class OccupationController {

    private final OccupationService occupationService;

    @GetMapping
    public ResponseEntity<Page<OccupationDto>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(occupationService.findAll(pageable));
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
    public ResponseEntity<Void> bulkCreateOccupations(@RequestBody String json) {
        occupationService.bulkCreateOccupations(json);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/organization/{organizationId}")
    public ResponseEntity<List<OccupationDto>> findByOrganizationId(
            @PathVariable Integer organizationId,
            @RequestHeader(value = "X-Organization-Id", required = false) Integer headerOrgId
    ) {
        HttpUtils.validateOrganizationHeader(organizationId, headerOrgId);
        return ResponseEntity.ok(occupationService.findByOrganizationId(organizationId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<OccupationDto>> searchOccupations(
            @RequestParam String q
    ) {
        return ResponseEntity.ok(occupationService.searchOccupations(q));
    }

}