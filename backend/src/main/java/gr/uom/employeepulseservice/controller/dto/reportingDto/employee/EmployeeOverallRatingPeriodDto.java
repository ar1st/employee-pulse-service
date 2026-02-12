package gr.uom.employeepulseservice.controller.dto.reportingDto.employee;

import java.time.LocalDate;

public record EmployeeOverallRatingPeriodDto(
        LocalDate periodStart,
        Double overallRating
) {}


