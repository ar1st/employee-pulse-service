package gr.uom.employeepulseservice.controller.dto;

public record CreatePerformanceReviewDto(
        String rawText,
        String comments,
        Double overallRating,
        Integer reporterId,
        Integer employeeId
) {
}
