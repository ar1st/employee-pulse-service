package gr.uom.employeepulseservice.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "skills")
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "skills_seq")
    @SequenceGenerator(name = "skills_seq", allocationSize = 1)
    private Integer id;

    private String name;

    private String description;

    private String escoId;

}
