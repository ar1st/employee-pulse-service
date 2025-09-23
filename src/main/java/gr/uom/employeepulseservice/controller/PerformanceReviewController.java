package gr.uom.employeepulseservice.controller;

import gr.uom.employeepulseservice.controller.dto.CreatePerformanceReviewDto;
import gr.uom.employeepulseservice.service.PerformanceReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("performance-reviews")
@RequiredArgsConstructor
public class PerformanceReviewController {

    private final PerformanceReviewService performanceReviewService;

    @PostMapping
    public ResponseEntity<Void> createPerformanceReview(@RequestBody CreatePerformanceReviewDto dto) {
        performanceReviewService.createPerformanceReview(dto);

        return ResponseEntity.ok().build();
    }

    // get by id
    // get by date
    //get by employee
    //get by reviewer
    //get by department
    //get by skills
    //get by occupation
}
