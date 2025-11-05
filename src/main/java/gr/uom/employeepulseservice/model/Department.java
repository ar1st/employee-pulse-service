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

    //todo Assign manager to department
    //add to create/update department
    //and add separate endpoint to assign employee to department
    @ManyToOne
    @JoinColumn(name = "manager_id")
    private Employee manager;

    @OneToMany(mappedBy = "department", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Employee> employees;

}
