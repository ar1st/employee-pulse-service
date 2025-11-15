package gr.uom.employeepulseservice.controller.dto.reportingDto;

import java.time.LocalDate;

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