package gr.uom.employeepulseservice.controller;

import gr.uom.employeepulseservice.controller.dto.*;
import gr.uom.employeepulseservice.service.DepartmentService;
import gr.uom.employeepulseservice.service.SkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;
    private final SkillService skillService;

    @GetMapping
    public ResponseEntity<List<DepartmentDto>> findAll() {
        return ResponseEntity.ok(departmentService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DepartmentDto> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(departmentService.findDepartmentById(id));
    }

    @PostMapping
    public ResponseEntity<Void> createDepartment(@RequestBody CreateDepartmentDto dto) {
        departmentService.createDepartment(dto);

        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateDepartment(@PathVariable Integer id, @RequestBody UpdateDepartmentDto dto) {
        departmentService.updateDepartment(id, dto);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDepartment(@PathVariable Integer id) {
        departmentService.deleteDepartment(id);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/employees")
    public ResponseEntity<List<EmployeeDto>> findEmployeesById(@PathVariable Integer id) {
        return ResponseEntity.ok(departmentService.findEmployeesById(id));
    }

    @GetMapping("/{id}/skills")
    public ResponseEntity<List<SkillDto>> findSkillsById(@PathVariable Integer id) {
        return ResponseEntity.ok(skillService.findByDepartmentId(id));
    }

}