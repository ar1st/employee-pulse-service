package gr.uom.employeepulseservice.controller.dto;

import java.time.LocalDate;

public record UpdatePerformanceReviewDto(
        String rawText,
        String comments,
        Double overallRating,
        LocalDate reviewDate
) {
}

