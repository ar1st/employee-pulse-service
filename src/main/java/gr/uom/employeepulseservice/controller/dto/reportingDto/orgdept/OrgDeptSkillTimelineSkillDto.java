package gr.uom.employeepulseservice.controller.dto.reportingDto.orgdept;

import java.util.List;

// Timeline data for a single skill in an organization/department
public record OrgDeptSkillTimelineSkillDto(
        Integer skillId,
        String skillName,
        List<OrgDeptSkillTimelinePointDto> timeline
) {
}
