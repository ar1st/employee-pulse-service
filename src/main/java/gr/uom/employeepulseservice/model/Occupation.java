package gr.uom.employeepulseservice.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "occupations")
public class Occupation {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "occupations_seq")
    @SequenceGenerator(name = "occupations_seq", allocationSize = 1)
    private Integer id;

    private String title;

    @Column(name = "description", length = 1000)
    private String description;

    private String escoId;
}
