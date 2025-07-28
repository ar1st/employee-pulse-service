package gr.uom.employeepulseservice.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;


@Data
@Entity
@Table(name = "departments")
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "departments_seq")
    @SequenceGenerator(name = "departments_seq", allocationSize = 1)
    private Integer id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "organization_id")
    private Organization organization;

    @OneToMany(mappedBy = "department", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Employee> employees;


}
