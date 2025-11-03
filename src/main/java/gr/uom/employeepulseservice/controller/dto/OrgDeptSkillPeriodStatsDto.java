package gr.uom.employeepulseservice.controller.dto;

import java.time.LocalDate;

public record OrgDeptSkillPeriodStatsDto(
        String organizationName,
        String departmentName,
        String skillName,
        LocalDate periodStart,
        Double avgRating,
        Double minRating,
        Double maxRating,
        Long sampleCount) {
}
