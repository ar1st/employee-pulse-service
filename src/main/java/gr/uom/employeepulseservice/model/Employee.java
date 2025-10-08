package gr.uom.employeepulseservice.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
@Entity
@Table(name = "employees")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "employees_seq")
    @SequenceGenerator(name = "employees_seq", allocationSize = 1)
    private Integer id;

    private String firstName;
    private String lastName;

    private String email;

    private LocalDate hireDate;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    @ManyToOne
    @JoinColumn(name = "organization_id")
    private Organization organization;

    @ManyToOne
    @JoinColumn(name = "occupation_id")
    private Occupation occupation;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "employee_id")
    private List<SkillEntry> skillEntries;
    /*
    [
        {
        skill: java
        rating: 4
        date: 20-01-2025
        },
        {
        skill: java
        rating: 5
        date: 20-01-2026
        },
        {
        teamwork
        5
        wed
        }
    ]
     */

//    private Map<Skill, Double> skillToRating;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "employee_id")
    private List<PerformanceReview> performanceReviews;

}
