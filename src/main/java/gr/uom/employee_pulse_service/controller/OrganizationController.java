package gr.uom.employee_pulse_service.controller;

import gr.uom.employee_pulse_service.controller.dto.OrganizationDto;
import gr.uom.employee_pulse_service.controller.dto.SaveOrganizationDto;
import gr.uom.employee_pulse_service.service.OrganizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("organizations")
@RequiredArgsConstructor
public class OrganizationController {

    private final OrganizationService organizationService;

    @GetMapping
    public ResponseEntity<List<OrganizationDto>> findAll() {
        return ResponseEntity.ok(organizationService.findAll());
    }

    //todo find departments by organization id

    @GetMapping("/{id}")
    public ResponseEntity<OrganizationDto> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(organizationService.findOrganizationById(id));
    }

    @PostMapping
    public ResponseEntity<Object> createOrganization(@RequestBody SaveOrganizationDto dto) {
        organizationService.createOrganization(dto);

        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrganizationDto> updateOrganization(@PathVariable Integer id, @RequestBody SaveOrganizationDto dto) {
        organizationService.updateOrganization(id, dto);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrganization(@PathVariable Integer id) {
        organizationService.deleteOrganization(id);

        return ResponseEntity.ok().build();
    }
}