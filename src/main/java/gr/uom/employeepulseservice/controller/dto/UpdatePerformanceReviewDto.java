package gr.uom.employeepulseservice.controller.dto;

public record UpdatePerformanceReviewDto(
        String rawText,
        String comments,
        Double overallRating
) {
}

