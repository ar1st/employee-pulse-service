package gr.uom.employee_pulse_service.model;

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

    private String description;

    private String escoId;
}
