package gr.uom.employeepulseservice.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "skill_entries")
public class SkillEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "skill_entries_seq")
    @SequenceGenerator(name = "skill_entries_seq", allocationSize = 1)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "skill_id")
    private Skill skill;

    private Double rating;

    private LocalDate entryDate;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

}
