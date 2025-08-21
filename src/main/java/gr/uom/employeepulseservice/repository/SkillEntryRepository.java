package gr.uom.employeepulseservice.repository;

import gr.uom.employeepulseservice.model.SkillEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SkillEntryRepository extends JpaRepository<SkillEntry, Integer> {
}
