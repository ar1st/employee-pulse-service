package gr.uom.employeepulseservice.controller.dto;

import java.util.List;

public record CreatePerformanceReviewResponseDto(
        Integer performanceReviewId,
        List<SkillEntryDto> generatedSkillEntries
) {
}
