package gr.uom.employeepulseservice.repository;

import gr.uom.employeepulseservice.model.Occupation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OccupationRepository extends JpaRepository<Occupation, Integer> {
}
