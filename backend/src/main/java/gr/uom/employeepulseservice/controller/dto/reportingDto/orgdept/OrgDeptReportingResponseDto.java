package gr.uom.employeepulseservice.controller.dto.reportingDto.orgdept;

import java.util.List;

// Top-level response for org/department reporting
public record OrgDeptReportingResponseDto(
        Integer organizationId,
        String organizationName,
        Integer departmentId,
        String departmentName,
        List<OrgDeptReportingSkillDto> skills,
        List<OrgDeptOverallRatingPeriodDto> overallRatings
) {
}
