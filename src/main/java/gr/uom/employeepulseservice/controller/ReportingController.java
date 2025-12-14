package gr.uom.employeepulseservice.controller;

import gr.uom.employeepulseservice.controller.dto.reportingDto.employee.EmployeeReportingResponseDto;
import gr.uom.employeepulseservice.controller.dto.reportingDto.employee.EmployeeSkillTimelineResponseDto;
import gr.uom.employeepulseservice.controller.dto.reportingDto.orgdept.OrgDeptReportingResponseDto;
import gr.uom.employeepulseservice.controller.dto.reportingDto.orgdept.OrgDeptSkillTimelineResponseDto;
import gr.uom.employeepulseservice.model.PeriodType;
import gr.uom.employeepulseservice.repository.ReportingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportingController {

    private final ReportingRepository reportingRepository;

    @GetMapping("/org/{orgId}")
    public ResponseEntity<OrgDeptReportingResponseDto> getReportByOrganizationAndDepartment(
            @PathVariable Integer orgId,
            @RequestParam PeriodType periodType,
            @RequestParam(required = false) Integer deptId,
            @RequestParam(required = false) Integer periodValue,
            @RequestParam(required = false) Integer year
    ) {
        return ResponseEntity.ok(
                reportingRepository.getReportByOrganizationAndDepartment(periodType, orgId, deptId, periodValue, year)
        );
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<EmployeeReportingResponseDto> getReportByEmployee(
            @PathVariable Integer employeeId,
            @RequestParam PeriodType periodType,
            @RequestParam(required = false) Integer periodValue,
            @RequestParam(required = false) Integer year
    ) {
        return ResponseEntity.ok(
                reportingRepository.getReportByEmployee(periodType, employeeId, periodValue, year)
        );
    }

    @GetMapping("/employees/{employeeId}/skills/timeline")
    public EmployeeSkillTimelineResponseDto getSkillTimelineByEmployee(
            @PathVariable Integer employeeId,
            @RequestParam(required = false) Integer skillId
    ) {
        return reportingRepository.getSkillTimelineByEmployee(employeeId, skillId);
    }

    @GetMapping("/organizations/{organizationId}/skills/timeline")
    public OrgDeptSkillTimelineResponseDto getSkillTimelineByOrganizationAndDepartment(
            @PathVariable Integer organizationId,
            @RequestParam(required = false) Integer departmentId,
            @RequestParam(required = false) Integer skillId
    ) {
        return reportingRepository.getSkillTimelineByOrganizationAndDepartment(
                organizationId,
                departmentId,
                skillId
        );
    }

}

