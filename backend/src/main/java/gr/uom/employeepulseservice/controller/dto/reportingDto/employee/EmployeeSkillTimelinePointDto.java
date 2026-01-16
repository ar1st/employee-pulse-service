package gr.uom.employeepulseservice.controller.dto.reportingDto.employee;

import java.time.LocalDate;

// One point in time in the employee's skill timeline
public record EmployeeSkillTimelinePointDto(
        LocalDate date,
        Double rating
) {}