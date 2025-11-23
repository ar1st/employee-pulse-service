package gr.uom.employeepulseservice.controller;

import gr.uom.employeepulseservice.controller.dto.CreatePerformanceReviewDto;
import gr.uom.employeepulseservice.controller.dto.PerformanceReviewDto;
import gr.uom.employeepulseservice.controller.dto.SaveSkillEntryDto;
import gr.uom.employeepulseservice.controller.dto.SkillEntryDto;
import gr.uom.employeepulseservice.service.PerformanceReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("performance-reviews")
@RequiredArgsConstructor
public class PerformanceReviewController {

    private final PerformanceReviewService performanceReviewService;

    @PostMapping
    public ResponseEntity<List<SkillEntryDto>> createPerformanceReview(@RequestBody CreatePerformanceReviewDto dto) {
        List<SkillEntryDto> skillEntries = performanceReviewService.createPerformanceReview(dto);

        return ResponseEntity.ok(skillEntries);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PerformanceReviewDto> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(performanceReviewService.findById(id));
    }

    @GetMapping("/date")
    public ResponseEntity<List<PerformanceReviewDto>> getByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(performanceReviewService.findByDate(date));
    }

    @GetMapping(value = "/date-range")
    public ResponseEntity<List<PerformanceReviewDto>> getByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(performanceReviewService.findByDateRange(from, to));
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<PerformanceReviewDto>> getByEmployee(@PathVariable Integer employeeId) {
        return ResponseEntity.ok(performanceReviewService.findByEmployee(employeeId));
    }

    @GetMapping("/reviewer/{reporterId}")
    public ResponseEntity<List<PerformanceReviewDto>> getByReviewer(@PathVariable Integer reporterId) {
        return ResponseEntity.ok(performanceReviewService.findByReviewer(reporterId));
    }

    @GetMapping("/department/{departmentId}")
    public ResponseEntity<List<PerformanceReviewDto>> getByDepartment(@PathVariable Integer departmentId) {
        return ResponseEntity.ok(performanceReviewService.findByDepartment(departmentId));
    }

    @GetMapping("/skill/{skillId}")
    public ResponseEntity<List<PerformanceReviewDto>> getBySkill(@PathVariable Integer skillId) {
        return ResponseEntity.ok(performanceReviewService.findBySkill(skillId));
    }

    @GetMapping("/occupation/{occupationId}")
    public ResponseEntity<List<PerformanceReviewDto>> getByOccupation(@PathVariable Integer occupationId) {
        return ResponseEntity.ok(performanceReviewService.findByOccupation(occupationId));
    }

    @PostMapping("/{reviewId}/skill-entries")
    public ResponseEntity<PerformanceReviewDto> addSkillEntry(
            @PathVariable Integer reviewId,
            @RequestBody SaveSkillEntryDto dto
    ) {
        return ResponseEntity.ok(
                performanceReviewService.addSkillEntryToReview(reviewId, dto)
        );
    }

    @PutMapping("/{reviewId}/skill-entries/{entryId}")
    public ResponseEntity<PerformanceReviewDto> updateSkillEntry(
            @PathVariable Integer reviewId,
            @PathVariable Integer entryId,
            @RequestBody SaveSkillEntryDto dto
    ) {
        return ResponseEntity.ok(
                performanceReviewService.updateSkillEntryInReview(reviewId, entryId, dto)
        );
    }

    @DeleteMapping("/{reviewId}/skill-entries/{entryId}")
    public ResponseEntity<PerformanceReviewDto> removeSkillEntry(
            @PathVariable Integer reviewId,
            @PathVariable Integer entryId
    ) {
        return ResponseEntity.ok(
                performanceReviewService.removeSkillEntryFromReview(reviewId, entryId)
        );
    }

}
