package gr.uom.employeepulseservice.controller;

import gr.uom.employeepulseservice.controller.dto.EmployeeReportingStatsDto;
import gr.uom.employeepulseservice.controller.dto.OrgDeptReportingStatsDto;
import gr.uom.employeepulseservice.repository.ReportingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportingController {

    private final ReportingRepository reportingRepository;

    @GetMapping("/org/{orgId}/dept/{deptId}")
    public ResponseEntity<List<OrgDeptReportingStatsDto>> getReportByOrganizationAndDepartment(
            @PathVariable Integer orgId,
            @PathVariable Integer deptId,
            @RequestParam(defaultValue = "quarter") String grain,
            @RequestParam(required = false) Integer periodValue
    ) {
        return ResponseEntity.ok(
                reportingRepository.getReportByOrganizationAndDepartment(grain, orgId, deptId, periodValue)
        );
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<EmployeeReportingStatsDto>> getReportByEmployee(
            @PathVariable Integer employeeId,
            @RequestParam(defaultValue = "month") String grain,
            @RequestParam(required = false) Integer periodValue
    ) {
        return ResponseEntity.ok(
                reportingRepository.getReportByEmployee(grain, employeeId, periodValue)
                );
    }
}
