package gr.uom.employeepulseservice.controller.dto.reportingDto.employee;

import java.time.LocalDate;

// One period entry for an employee skill
public record EmployeeReportingPeriodDto(
        LocalDate periodStart,
        Double avgRating,
        Double minRating,
        Double maxRating
) {}

