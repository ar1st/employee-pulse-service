package gr.uom.employeepulseservice.repository;

import gr.uom.employeepulseservice.model.PerformanceReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PerformanceReviewRepository extends JpaRepository<PerformanceReview, Integer> {

    // by exact date
    List<PerformanceReview> findAllByReviewDate(LocalDate date);

    // by date range (inclusive)
    List<PerformanceReview> findAllByReviewDateBetween(LocalDate from, LocalDate to);

    // by employee (reviewed person)
    List<PerformanceReview> findAllByRefersToId(Integer employeeId);

    // by reviewer (reporter/manager)
    List<PerformanceReview> findAllByReportedById(Integer reporterId);

    // by department
    List<PerformanceReview> findAllByDepartmentId(Integer departmentId);

    // by occupation (of reviewed employee)
    List<PerformanceReview> findAllByRefersToOccupationId(Integer occupationId);

    // by skill included in the review's skill entries
    @Query("""
        select distinct performanceReview
        from PerformanceReview performanceReview
        join performanceReview.skillEntries skillEntries
        where skillEntries.skill.id = :skillId
    """)
    List<PerformanceReview> findAllBySkillId(Integer skillId);
}
