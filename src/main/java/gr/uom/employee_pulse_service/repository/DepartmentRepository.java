package gr.uom.employee_pulse_service.repository;

import gr.uom.employee_pulse_service.model.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Integer> {
}
