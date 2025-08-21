package gr.uom.employeepulseservice.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

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

    @ManyToOne
    @JoinColumn(name = "manager_id")
    private Employee manager;

    @OneToMany(mappedBy = "manager", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Employee> subordinates;

    @OneToMany(orphanRemoval = true, cascade = CascadeType.ALL)
    @JoinColumn(name = "employee_id")
    private List<SkillEntry> skillEntries;
//
//    private List<PerformanceReview> performanceReviews;

}
