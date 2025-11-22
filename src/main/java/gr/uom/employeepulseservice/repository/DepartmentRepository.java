package gr.uom.employeepulseservice.repository;

import gr.uom.employeepulseservice.model.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Integer> {

    List<Department> findByOrganizationId(Integer organizationId);

    boolean existsByOrganizationId(Integer organizationId);

    @Query(value = """
        select case when count(*) > 0 then true else false end
        from departments d
        join employees e on e.department_id = d.id
        where e.id = :employeeId
          and d.manager_id = :reporterId
        """, nativeQuery = true)
    boolean isManagerOfEmployee(@Param("reporterId") Integer reporterId,
                                @Param("employeeId") Integer employeeId);
}
