package gr.uom.employee_pulse_service.model;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

//todo
@Data
public class PerformanceReview {

    private Integer id;
    private String rawText;
    private String comments;
    private Double overallRating;

    //manager that created the performance review
    private Employee reportedBy;

    //employee that the performance review applies to
    private Employee refersTo;

    private LocalDate reviewDate;

    private List<SkillEntry> skillEntries;
}
