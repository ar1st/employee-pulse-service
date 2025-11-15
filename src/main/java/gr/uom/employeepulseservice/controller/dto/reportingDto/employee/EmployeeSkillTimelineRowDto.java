package gr.uom.employeepulseservice.controller.dto.reportingDto.employee;

import java.time.LocalDate;

public record EmployeeSkillTimelineRowDto(
        Integer employeeId,
        String firstName,
        String lastName,
        Integer skillId,
        String skillName,
        LocalDate entryDate,
        Double rating,
        Double minRating,
        Double maxRating,
        Double avgRating
) {}