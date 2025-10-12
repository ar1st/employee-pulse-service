package gr.uom.employeepulseservice.controller.dto;

import java.time.LocalDate;
import java.util.List;

public record PerformanceReviewDto(
        Integer id,
        String rawText,
        String comments,
        Double overallRating,

        LocalDate reviewDate,
        List<SkillEntryDto> skillEntryDtos
        ) {
}
