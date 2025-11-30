package gr.uom.employeepulseservice.controller.dto;

import java.time.LocalDateTime;
import java.util.List;

public record PerformanceReviewDto(
        Integer id,
        String rawText,
        String comments,
        Double overallRating,

        LocalDateTime reviewDateTime,
        List<SkillEntryDto> skillEntryDtos,
        String departmentName,
        String reporterName,
        String employeeName
        ) {
}
