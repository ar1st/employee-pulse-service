package gr.uom.employeepulseservice.repository;

import gr.uom.employeepulseservice.model.Occupation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OccupationRepository extends JpaRepository<Occupation, Integer> {

    @Query("""
        SELECT DISTINCT e.occupation
        FROM Employee e
        WHERE e.organization.id = :organizationId
        AND e.occupation IS NOT NULL
    """)
    List<Occupation> findOccupationsByOrganizationId(@Param("organizationId") Integer organizationId);

    @Query("""
        SELECT o
        FROM Occupation o
        WHERE LOWER(o.title) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
        ORDER BY o.title
    """)
    List<Occupation> searchOccupations(@Param("searchTerm") String searchTerm);
}
