package gr.uom.employeepulseservice.controller;

import gr.uom.employeepulseservice.controller.dto.EmployeeDto;
import gr.uom.employeepulseservice.controller.dto.SaveEmployeeDto;
import gr.uom.employeepulseservice.controller.dto.SaveSkillEntryDto;
import gr.uom.employeepulseservice.controller.dto.SkillEntryDto;
import gr.uom.employeepulseservice.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping
    public ResponseEntity<List<EmployeeDto>> findAll() {
        return ResponseEntity.ok(employeeService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDto> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(employeeService.findEmployeeById(id));
    }

    @PostMapping
    public ResponseEntity<Void> createEmployee(@RequestBody SaveEmployeeDto dto) {
        employeeService.createEmployee(dto);

        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateEmployee(@PathVariable Integer id, @RequestBody SaveEmployeeDto dto) {
        employeeService.updateEmployee(id, dto);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Integer id) {
        employeeService.deleteEmployee(id);

        return ResponseEntity.ok().build();
    }

    @PutMapping("/{employeeId}/departments/{departmentId}")
    public ResponseEntity<Void> changeDepartmentOfEmployee(@PathVariable Integer employeeId,
                                                 @PathVariable Integer departmentId) {
        employeeService.changeDepartmentOfEmployee(employeeId, departmentId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/bulk")
    public ResponseEntity<Void> bulkCreate(@RequestBody String json) {
        employeeService.bulkCreate(json);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{employeeId}/skill-entries")
    public ResponseEntity<List<SkillEntryDto>> getSkillEntriesOfEmployee(@PathVariable Integer employeeId) {
        return ResponseEntity.ok(employeeService.getSkillEntriesOfEmployee(employeeId));
    }

    @GetMapping("/{employeeId}/skill-entries/latest")
    public ResponseEntity<List<SkillEntryDto>> getLatestSkillEntriesOfEmployee(@PathVariable Integer employeeId) {
        return ResponseEntity.ok(employeeService.getLatestSkillEntriesOfEmployee(employeeId));
    }

    @PostMapping("/{employeeId}/skill-entries")
    public ResponseEntity<Void> addSkillEntryToEmployee(@PathVariable Integer employeeId,
                                              @RequestBody SaveSkillEntryDto dto) {
        employeeService.addSkillEntryToEmployee(employeeId, dto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{employeeId}/skill-entries/{skillEntryId}")
    public ResponseEntity<Void> removeSkillEntryFromEmployee(@PathVariable Integer employeeId,
                                                 @PathVariable Integer skillEntryId) {
        employeeService.removeSkillEntryFromEmployee(employeeId, skillEntryId);
        return ResponseEntity.ok().build();
    }






    // get employee skill distribution within department
    // per skill -> avg, mean, min, max, percentage

    // get monthly/historical skill report trends by Department, Organization, Employee
    // how does employee percentage change by date
    // how avg, mean, min, max, percentage change by date

    /*
    Java




    FOR EACH ORGANIZATION ->
        FOR EACH DEPARTMENT ->
            FOR EACH SKILL ->
                QUARTERLY REPORT
                avg, mean, min, max, percentage
    We will create a view that will hold all skill entries

    fields [id, skill_entry_id, employee_id, entry_date, department_id, organization_id]


    FOR EMPLOYEE ->
        FOR EACH SKILL ->
            MONTHLY REPORT
            avg, mean, min, max, percentage


     */
}
