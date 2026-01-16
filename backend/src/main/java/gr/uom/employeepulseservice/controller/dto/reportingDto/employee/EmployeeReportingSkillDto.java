package gr.uom.employeepulseservice.controller.dto.reportingDto.employee;

import java.util.List;

// One skill with its periods for employee reporting
public record EmployeeReportingSkillDto(
        String skillName,
        List<EmployeeReportingPeriodDto> periods
) {}