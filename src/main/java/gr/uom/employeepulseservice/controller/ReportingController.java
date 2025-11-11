package gr.uom.employeepulseservice.controller;

import gr.uom.employeepulseservice.controller.dto.EmployeeReportingStatsDto;
import gr.uom.employeepulseservice.controller.dto.OrgDeptReportingStatsDto;
import gr.uom.employeepulseservice.model.PeriodType;
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
            @RequestParam PeriodType periodType,
            @RequestParam(required = false) Integer periodValue,
            @RequestParam(required = false) Integer year
    ) {
        return ResponseEntity.ok(
                reportingRepository.getReportByOrganizationAndDepartment(periodType, orgId, deptId, periodValue, year)
        );
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<EmployeeReportingStatsDto>> getReportByEmployee(
            @PathVariable Integer employeeId,
            @RequestParam PeriodType periodType,
            @RequestParam(required = false) Integer periodValue,
            @RequestParam(required = false) Integer year
    ) {
        return ResponseEntity.ok(
                reportingRepository.getReportByEmployee(periodType, employeeId, periodValue, year)
        );
    }
}

