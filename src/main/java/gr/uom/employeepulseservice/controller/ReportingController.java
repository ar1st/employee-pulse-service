package gr.uom.employeepulseservice.controller;

import gr.uom.employeepulseservice.controller.dto.EmployeeSkillPeriodStatsDto;
import gr.uom.employeepulseservice.controller.dto.OrgDeptSkillPeriodStatsDto;
import gr.uom.employeepulseservice.repository.impl.ReportingRepositoryImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportingController {

    private final ReportingRepositoryImpl reportingRepositoryImpl;

    @GetMapping("/org/{orgId}/dept/{deptId}")
    public ResponseEntity<List<OrgDeptSkillPeriodStatsDto>> getOrgDeptSkill(
            @PathVariable Integer orgId,
            @PathVariable Integer deptId,
            @RequestParam(defaultValue = "quarter") String grain
    ) {
        return ResponseEntity.ok(
                reportingRepositoryImpl.getOrgDeptSkillPeriod(grain, orgId, deptId)
        );
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<EmployeeSkillPeriodStatsDto>> getEmployeeSkill(
            @PathVariable Integer employeeId,
            @RequestParam(defaultValue = "month") String grain
    ) {
        return ResponseEntity.ok(
                reportingRepositoryImpl.getEmployeeSkillPeriod(grain, employeeId)
        );
    }

}