package gr.uom.employeepulseservice.controller.dto.reportingDto.orgdept;

import java.time.LocalDate;

// Raw database row representing a skill entry for org/department
public record OrgDeptSkillTimelineRowDto(
        Integer organizationId,
        String organizationName,
        Integer departmentId,
        String departmentName,
        Integer skillId,
        String skillName,
        LocalDate date,
        Double minRating,
        Double maxRating,
        Double avgRating
) {}
