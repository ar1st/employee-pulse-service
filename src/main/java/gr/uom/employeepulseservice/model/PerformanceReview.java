package gr.uom.employeepulseservice.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "performance_reviews")
public class PerformanceReview {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "performance_reviews_seq")
    @SequenceGenerator(name = "performance_reviews_seq", allocationSize = 1)
    private Integer id;
    private String rawText;
    private String comments;
    private Double overallRating;

    //manager that created the performance review
    @ManyToOne
    @JoinColumn(name = "reporter_id")
    private Employee reportedBy;

    //employee that the performance review applies to
    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee refersTo;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    private LocalDate reviewDate;
    private LocalDateTime reviewDateTime;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "performance_review_id")
    private List<SkillEntry> skillEntries;
}
