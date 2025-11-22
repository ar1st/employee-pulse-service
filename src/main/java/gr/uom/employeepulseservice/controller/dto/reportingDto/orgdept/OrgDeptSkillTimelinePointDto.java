package gr.uom.employeepulseservice.controller.dto.reportingDto.orgdept;

import java.time.LocalDate;

// One point in time in the org/department skill timeline
public record OrgDeptSkillTimelinePointDto(
        LocalDate date,
        Double minRating,
        Double maxRating,
        Double avgRating
) {}
