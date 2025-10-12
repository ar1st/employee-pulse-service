package gr.uom.employeepulseservice.repository;

import gr.uom.employeepulseservice.model.Skill;
import gr.uom.employeepulseservice.model.SkillEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Integer> {

    @Query("""
        SELECT DISTINCT se.skill
        FROM SkillEntry se
        JOIN se.employee e
        WHERE e.organization.id = :organizationId
    """)
    List<Skill> findSkillsByOrganizationId(Integer organizationId);

    @Query("""
        SELECT DISTINCT se.skill
        FROM SkillEntry se
        JOIN se.employee e
        WHERE e.department.id = :departmentId
    """)
    List<Skill> findSkillsByDepartmentId(Integer departmentId);

}
