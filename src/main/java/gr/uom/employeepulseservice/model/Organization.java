package gr.uom.employeepulseservice.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "organizations")
public class Organization {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "organizations_seq")
    @SequenceGenerator(name = "organizations_seq", allocationSize = 1)
    private Integer id;

    private String name;

    private String location;

    @OneToMany(mappedBy = "organization", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Department> departments = new ArrayList<>();
}
