package gr.uom.employeepulseservice.controller.dto;

import java.time.LocalDate;

public record CreatePerformanceReviewDto(
        String rawText,
        String comments,
        Double overallRating,
        Integer reporterId,
        Integer employeeId,
        LocalDate reviewDate
) {
}
