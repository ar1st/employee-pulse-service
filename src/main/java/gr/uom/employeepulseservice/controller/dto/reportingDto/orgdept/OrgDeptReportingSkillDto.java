package gr.uom.employeepulseservice.controller.dto.reportingDto.orgdept;

import java.util.List;

// One skill with its periods for org/department reporting
public record OrgDeptReportingSkillDto(
        String skillName,
        List<OrgDeptReportingPeriodDto> periods
) {}

