package gr.uom.employeepulseservice.controller.dto.reportingDto.orgdept;

import java.time.LocalDate;

// One period entry for an org/department skill
public record OrgDeptReportingPeriodDto(
        LocalDate periodStart,
        Double avgRating,
        Double minRating,
        Double maxRating,
        Long sampleCount,
        Long employeeCount
) {
}
