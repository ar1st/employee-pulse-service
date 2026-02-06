package gr.uom.employeepulseservice.controller;

import gr.uom.employeepulseservice.controller.dto.*;
import gr.uom.employeepulseservice.service.OrganizationService;
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

    @GetMapping("/{id}")
    public ResponseEntity<OrganizationDto> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(organizationService.findOrganizationById(id));
    }

    @PostMapping
    public ResponseEntity<Void> createOrganization(@RequestBody SaveOrganizationDto dto) {
        organizationService.createOrganization(dto);

        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateOrganization(@PathVariable Integer id, @RequestBody SaveOrganizationDto dto) {
        organizationService.updateOrganization(id, dto);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrganization(@PathVariable Integer id) {
        organizationService.deleteOrganization(id);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/employees")
    public ResponseEntity<List<EmployeeDto>> findEmployeesById(
            @PathVariable Integer id,
            @RequestHeader(value = "X-Organization-Name", required = false) String headerOrgName
    ) {
        String orgName = organizationService.findOrganizationNameById(id);
        HttpUtils.validateOrganizationHeader(orgName, headerOrgName);

        return ResponseEntity.ok(organizationService.findEmployeesById(id));
    }

    @GetMapping("/{id}/departments")
    public ResponseEntity<List<DepartmentDto>> findDepartmentsById(
            @PathVariable Integer id,
            @RequestHeader(value = "X-Organization-Name", required = false) String headerOrgName
    ) {
        String orgName = organizationService.findOrganizationNameById(id);
        HttpUtils.validateOrganizationHeader(orgName, headerOrgName);

        return ResponseEntity.ok(organizationService.findDepartmentsById(id));
    }

    @GetMapping("/{id}/skills")
    public ResponseEntity<List<SkillDto>> findSkillsById(
            @PathVariable Integer id,
            @RequestHeader(value = "X-Organization-Name", required = false) String headerOrgName
    ) {
        String orgName = organizationService.findOrganizationNameById(id);
        HttpUtils.validateOrganizationHeader(orgName, headerOrgName);

        return ResponseEntity.ok(organizationService.findSkillsById(id));
    }
}