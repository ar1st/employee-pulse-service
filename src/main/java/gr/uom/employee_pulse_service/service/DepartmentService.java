package gr.uom.employee_pulse_service.service;

import gr.uom.employee_pulse_service.controller.dto.DepartmentDto;
import gr.uom.employee_pulse_service.controller.dto.CreateDepartmentDto;
import gr.uom.employee_pulse_service.controller.dto.UpdateDepartmentDto;
import gr.uom.employee_pulse_service.mapper.DepartmentMapper;
import gr.uom.employee_pulse_service.model.Department;
import gr.uom.employee_pulse_service.model.Organization;
import gr.uom.employee_pulse_service.repository.DepartmentRepository;
import gr.uom.employee_pulse_service.repository.EmployeeRepository;
import gr.uom.employee_pulse_service.repository.OrganizationRepository;
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
        boolean hsEmployees = employeeRepository.existsByDepartmentId(id);

        //todo check that it works
        if (hsEmployees) {
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

}
