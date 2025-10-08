package gr.uom.employeepulseservice.controller;

import gr.uom.employeepulseservice.controller.dto.EmployeeDto;
import gr.uom.employeepulseservice.controller.dto.SaveEmployeeDto;
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
    public ResponseEntity<Void> deleteDepartment(@PathVariable Integer id) {
        employeeService.deleteEmployee(id);

        return ResponseEntity.ok().build();
    }
    // add employees in bulk (input: json string)
    // get skills
    // add skill to employee
    // remove skill from employee


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
                MONTHLY REPORT
                avg, mean, min, max, percentage
    We will create a view that will hold all skill entries

    fields [id, skill_entry_id, employee_id, entry_date, department_id, organization_id]


    FOR EMPLOYEE ->
        FOR EACH SKILL ->
            MONTHLY REPORT
            avg, mean, min, max, percentage


     */
}
