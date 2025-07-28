package gr.uom.employee_pulse_service.controller;

import gr.uom.employee_pulse_service.controller.dto.CreateDepartmentDto;
import gr.uom.employee_pulse_service.controller.dto.DepartmentDto;
import gr.uom.employee_pulse_service.controller.dto.UpdateDepartmentDto;
import gr.uom.employee_pulse_service.service.DepartmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    @GetMapping
    public ResponseEntity<List<DepartmentDto>> findAll() {
        return ResponseEntity.ok(departmentService.findAll());
    }

    //todo find departments by organization id
    @GetMapping("/{id}")
    public ResponseEntity<DepartmentDto> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(departmentService.findDepartmentById(id));
    }

    @PostMapping
    public ResponseEntity<Void> createOrganization(@RequestBody CreateDepartmentDto dto) {
        departmentService.createDepartment(dto);

        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateOrganization(@PathVariable Integer id, @RequestBody UpdateDepartmentDto dto) {
        departmentService.updateDepartment(id, dto);

        return ResponseEntity.ok().build();
    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteOrganization(@PathVariable Integer id) {
//        organizationService.deleteOrganization(id);
//
//        return ResponseEntity.ok().build();
//    }
}