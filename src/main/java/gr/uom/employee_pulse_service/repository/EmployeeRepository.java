package gr.uom.employee_pulse_service.repository;

import gr.uom.employee_pulse_service.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

    boolean existsByDepartmentId(Integer departmentId);
}
