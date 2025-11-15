package gr.uom.employeepulseservice.controller.dto.reportingDto.orgdept;

import java.util.List;

public record OrgDeptSkillTimelineStatsDto(
        Integer organizationId,
        String organizationName,
        Integer departmentId,
        String departmentName,
        Integer skillId,
        String skillName,
        List<OrgDeptSkillTimelinePointDto> timeline
) {}
