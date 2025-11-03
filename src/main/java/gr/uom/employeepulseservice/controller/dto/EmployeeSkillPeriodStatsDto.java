package gr.uom.employeepulseservice.controller.dto;

import java.time.LocalDate;

public record EmployeeSkillPeriodStatsDto (
        Integer employeeId,
        String firstName,
        String lastName,
        String skillName,
        LocalDate periodStart,
        Double avgRating,
        Double minRating,
        Double maxRating,
        Long sampleCount
) {}