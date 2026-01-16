package gr.uom.employeepulseservice.controller.dto.reportingDto.employee;

import java.time.LocalDate;

// Aggregated employee stats for a selected reporting period
public record EmployeeReportingStatsDto(
        Integer employeeId,
        String firstName,
        String lastName,
        String skillName,
        LocalDate periodStart,
        Double avgRating,
        Double minRating,
        Double maxRating
) {}