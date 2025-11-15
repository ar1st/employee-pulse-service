package gr.uom.employeepulseservice.controller.dto.reportingDto.orgdept;

import java.time.LocalDate;

public record OrgDeptSkillTimelinePointDto(
        LocalDate date,
        Double minRating,
        Double maxRating,
        Double avgRating
) {}
