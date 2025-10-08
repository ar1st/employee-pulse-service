package gr.uom.employeepulseservice.service;

import gr.uom.employeepulseservice.controller.dto.EmployeeDto;
import gr.uom.employeepulseservice.controller.dto.SaveEmployeeDto;
import gr.uom.employeepulseservice.mapper.EmployeeMapper;
import gr.uom.employeepulseservice.model.Department;
import gr.uom.employeepulseservice.model.Employee;
import gr.uom.employeepulseservice.model.Occupation;
import gr.uom.employeepulseservice.model.Organization;
import gr.uom.employeepulseservice.repository.DepartmentRepository;
import gr.uom.employeepulseservice.repository.EmployeeRepository;
import gr.uom.employeepulseservice.repository.OccupationRepository;
import gr.uom.employeepulseservice.repository.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final OccupationRepository occupationRepository;
    private final OrganizationRepository organizationRepository;
    private final EmployeeMapper employeeMapper;

    @Transactional(readOnly = true)
    public List<EmployeeDto> findAll() {
        List<Employee> employees = employeeRepository.findAll();

        return employeeMapper.toDtos(employees);
    }

    @Transactional
    public EmployeeDto findEmployeeById(Integer id) {
        Employee employee = findById(id);

        return employeeMapper.toDto(employee);
    }

    @Transactional
    public void createEmployee(SaveEmployeeDto dto) {
        Employee employee = employeeMapper.toEntity(dto);

        setEmployeeRelations(dto, employee);

        employeeRepository.save(employee);
    }

    private void setEmployeeRelations(SaveEmployeeDto dto, Employee employee) {
        Department department = findDepartmentById(dto.departmentId());
        Organization organization = findOrganizationById(dto.organizationId());
        Employee manager = findById(dto.managerId());
        Occupation occupation = findOccupationById(dto.occupationId());

        if (!department.getOrganization().getId().equals(organization.getId())) {
            throw new RuntimeException("Department does not belong to the Organization");
        }

        if (!manager.getOrganization().getId().equals(employee.getOrganization().getId())) {
            throw new RuntimeException("Employee and Manager do not belong to the same Organization");
        }

        employee.setDepartment(department);

        employee.setOccupation(occupation);


        employee.setOrganization(organization);
    }

    @Transactional
    public void updateEmployee(Integer id, SaveEmployeeDto dto) {
        Employee employee = findById(id);

        employeeMapper.updateFromDto(employee, dto);
        setEmployeeRelations(dto, employee);

    }

    private Employee findById(Integer id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
    }

    private Department findDepartmentById(Integer id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found"));
    }

    private Occupation findOccupationById(Integer id) {
        return occupationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Occupation not found"));
    }


    private Organization findOrganizationById(Integer id) {
        return organizationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Organization not found"));
    }

    @Transactional
    public void deleteEmployee(Integer id) {
        employeeRepository.deleteById(id);
    }
}
