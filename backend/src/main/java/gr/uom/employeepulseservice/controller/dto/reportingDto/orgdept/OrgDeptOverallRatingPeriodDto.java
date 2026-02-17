package gr.uom.employeepulseservice.controller.dto.reportingDto.orgdept;

import java.time.LocalDate;

// One period entry for overall rating timeline at organization/department level
public record OrgDeptOverallRatingPeriodDto(
        LocalDate periodStart,
        Double avgOverallRating
) {}


