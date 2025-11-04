package gr.uom.employeepulseservice.controller;

import gr.uom.employeepulseservice.controller.dto.EmployeeSkillPeriodStatsDto;
import gr.uom.employeepulseservice.controller.dto.OrgDeptSkillPeriodStatsDto;
import gr.uom.employeepulseservice.repository.ReportingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportingController {

    private final ReportingRepository reportingRepository;

    @GetMapping("/org/{orgId}/dept/{deptId}")
    public ResponseEntity<List<OrgDeptSkillPeriodStatsDto>> getOrgDeptSkill(
            @PathVariable Integer orgId,
            @PathVariable Integer deptId,
            @RequestParam(defaultValue = "quarter") String grain,
            @RequestParam(required = false) Optional<Integer> number
    ) {
        return ResponseEntity.ok(
                reportingRepository.getOrgDeptSkillPeriod(grain, orgId, deptId, number.orElse(null))
        );
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<EmployeeSkillPeriodStatsDto>> getEmployeeSkill(
            @PathVariable Integer employeeId,
            @RequestParam(defaultValue = "month") String grain,
            @RequestParam(required = false) Optional<Integer> number
    ) {
        return ResponseEntity.ok(
                reportingRepository.getEmployeeSkillPeriod(grain, employeeId, number.orElse(null))
        );
    }
}
