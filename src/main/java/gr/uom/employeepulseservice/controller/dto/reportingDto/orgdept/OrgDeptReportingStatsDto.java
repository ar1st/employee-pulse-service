package gr.uom.employeepulseservice.controller.dto.reportingDto.orgdept;

import java.time.LocalDate;

public record OrgDeptReportingStatsDto(
        String organizationName,
        String departmentName,
        String skillName,
        LocalDate periodStart,
        Double avgRating,
        Double minRating,
        Double maxRating,
        Long sampleCount) {
}
