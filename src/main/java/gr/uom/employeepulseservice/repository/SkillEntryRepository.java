package gr.uom.employeepulseservice.repository;

import gr.uom.employeepulseservice.model.SkillEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SkillEntryRepository extends JpaRepository<SkillEntry, Integer> {

    @Query("""
        SELECT se
        FROM SkillEntry se
        WHERE se.employee.id = :employeeId
        ORDER BY se.entryDate DESC
    """)
    List<SkillEntry> findAllByEmployeeIdOrderByEntryDateDesc(@Param("employeeId") Integer employeeId);

    @Query("""
        SELECT CASE WHEN COUNT(se) > 0 THEN true ELSE false END
        FROM SkillEntry se
        WHERE se.id = :id AND se.employee.id = :employeeId
    """)
    boolean existsByIdAndEmployeeId(@Param("id") Integer id,
                                    @Param("employeeId") Integer employeeId);
}
