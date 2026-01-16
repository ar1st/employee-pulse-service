package gr.uom.employeepulseservice.controller.dto.reportingDto.orgdept;

import java.util.List;

// Top-level timeline response containing org/dept info and all skills
public record OrgDeptSkillTimelineResponseDto(
        Integer organizationId,
        String organizationName,
        Integer departmentId,
        String departmentName,
        List<OrgDeptSkillTimelineSkillDto> skills
) {
}
