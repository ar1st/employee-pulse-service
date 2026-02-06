package gr.uom.employeepulseservice.service;

import gr.uom.employeepulseservice.controller.dto.*;
import gr.uom.employeepulseservice.mapper.DepartmentMapper;
import gr.uom.employeepulseservice.mapper.EmployeeMapper;
import gr.uom.employeepulseservice.mapper.OrganizationMapper;
import gr.uom.employeepulseservice.mapper.SkillMapper;
import gr.uom.employeepulseservice.model.Employee;
import gr.uom.employeepulseservice.model.Organization;
import gr.uom.employeepulseservice.model.Skill;
import gr.uom.employeepulseservice.repository.DepartmentRepository;
import gr.uom.employeepulseservice.repository.EmployeeRepository;
import gr.uom.employeepulseservice.repository.OrganizationRepository;
import gr.uom.employeepulseservice.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final OrganizationMapper organizationMapper;
    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;
    private final DepartmentRepository departmentRepository;
    private final DepartmentMapper departmentMapper;
    private final SkillRepository skillRepository;
    private final SkillMapper skillMapper;

    @Transactional(readOnly = true)
    public List<OrganizationDto> findAll() {
        List<Organization> organizations = organizationRepository.findAll();

        return organizationMapper.toDtos(organizations);
    }

    @Transactional(readOnly = true)
    public OrganizationDto findOrganizationById(Integer id) {
        Organization organization = findById(id);

        return organizationMapper.toDto(organization);
    }

    public String findOrganizationNameById(Integer id) {
        Organization organization = findById(id);

        return organization.getName();
    }

    private Organization findById(Integer id) {
        return organizationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Organization not found"));
    }

    @Transactional
    public void createOrganization(SaveOrganizationDto dto) {
        Organization organization = organizationMapper.toEntity(dto);

        organizationRepository.save(organization);
    }

    @Transactional
    public void updateOrganization(Integer id, SaveOrganizationDto dto) {
        Organization organization = findById(id);

        organization.setName(dto.name());
        organization.setLocation(dto.location());
    }

    @Transactional
    public void deleteOrganization(Integer id) {
        organizationRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<EmployeeDto> findEmployeesById(Integer id) {
        List<Employee> employees = employeeRepository.findByOrganizationIdOrderByHireDateDesc(id);

        return employeeMapper.toDtos(employees);
    }

    @Transactional(readOnly = true)
    public List<DepartmentDto> findDepartmentsById(Integer id) {
        organizationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Organization not found"));

        return departmentMapper.toDtos(
                departmentRepository.findByOrganizationId(id)
        );
    }

    @Transactional(readOnly = true)
    public List<SkillDto> findSkillsById(Integer id) {
        organizationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Organization not found"));

        List<Skill> skills = skillRepository.findSkillsByOrganizationId(id);
        return skillMapper.toDtos(skills);
    }
}