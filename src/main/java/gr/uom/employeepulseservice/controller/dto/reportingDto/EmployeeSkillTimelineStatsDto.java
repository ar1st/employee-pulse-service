package gr.uom.employeepulseservice.controller.dto.reportingDto;

import java.util.List;

public record EmployeeSkillTimelineStatsDto(
        Integer employeeId,
        String firstName,
        String lastName,
        Integer skillId,
        String skillName,
        List<SkillTimelinePointDto> timeline,
        Double minRating,
        Double maxRating,
        Double avgRating
) {}