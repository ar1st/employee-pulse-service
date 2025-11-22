package gr.uom.employeepulseservice.controller.dto.reportingDto.employee;

import java.util.List;

// Timeline and summary stats for a single employee skill
public record EmployeeSkillTimelineSkillDto(
    Integer skillId,
    String skillName,
    List<EmployeeSkillTimelinePointDto> timeline,
    Double minRating,
    Double maxRating,
    Double avgRating
) { }
