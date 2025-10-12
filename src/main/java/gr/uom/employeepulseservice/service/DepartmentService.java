package gr.uom.employeepulseservice.service;

import gr.uom.employeepulseservice.controller.dto.DepartmentDto;
import gr.uom.employeepulseservice.controller.dto.CreateDepartmentDto;
import gr.uom.employeepulseservice.controller.dto.EmployeeDto;
import gr.uom.employeepulseservice.controller.dto.UpdateDepartmentDto;
import gr.uom.employeepulseservice.mapper.DepartmentMapper;
import gr.uom.employeepulseservice.mapper.EmployeeMapper;
import gr.uom.employeepulseservice.model.Department;
import gr.uom.employeepulseservice.model.Employee;
import gr.uom.employeepulseservice.model.Organization;
import gr.uom.employeepulseservice.repository.DepartmentRepository;
import gr.uom.employeepulseservice.repository.EmployeeRepository;
import gr.uom.employeepulseservice.repository.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final OrganizationRepository organizationRepository;
    private final EmployeeRepository employeeRepository;
    private final DepartmentMapper departmentMapper;
    private final EmployeeMapper employeeMapper;

    @Transactional(readOnly = true)
    public List<DepartmentDto> findAll() {
        List<Department> departments = departmentRepository.findAll();

        return departmentMapper.toDtos(departments);
    }

    @Transactional(readOnly = true)
    public DepartmentDto findDepartmentById(Integer id) {
        Department department = findById(id);

        return departmentMapper.toDto(department);
    }

    @Transactional
    public void createDepartment(CreateDepartmentDto dto) {
        Department department = departmentMapper.toEntity(dto);

        Organization organization = findOrganizationById(dto.organizationId());
        department.setOrganization(organization);

        departmentRepository.save(department);
    }

    @Transactional
    public void updateDepartment(Integer id, UpdateDepartmentDto dto) {
        Department department = findById(id);

        department.setName(dto.name());
    }

    @Transactional
    public void deleteDepartment(Integer id) {
        boolean hasEmployees = employeeRepository.existsByDepartmentId(id);

        //todo check that it works
        if (hasEmployees) {
            throw new RuntimeException("Cannot deleted department %d because employees are assigned to it".formatted(id));
        }

        departmentRepository.deleteById(id);
    }

    private Department findById(Integer id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found"));
    }

    private Organization findOrganizationById(Integer id) {
        return organizationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Organization not found"));
    }

    @Transactional(readOnly = true)
    public List<EmployeeDto> findEmployeesById(Integer id) {
        List<Employee> employees = employeeRepository.findByDepartmentId(id);

        return employeeMapper.toDtos(employees);
    }

}
