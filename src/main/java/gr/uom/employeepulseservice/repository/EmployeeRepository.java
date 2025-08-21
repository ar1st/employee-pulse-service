package gr.uom.employeepulseservice.repository;

import gr.uom.employeepulseservice.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

    boolean existsByDepartmentId(Integer departmentId);

    boolean existsByManagerId(Integer managerId);

    List<Employee> findByDepartmentId(Integer departmentId);



}
