package gr.uom.employeepulseservice.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import gr.uom.employeepulseservice.controller.dto.*;
import gr.uom.employeepulseservice.mapper.EmployeeMapper;
import gr.uom.employeepulseservice.mapper.SkillEntryMapper;
import gr.uom.employeepulseservice.model.*;
import gr.uom.employeepulseservice.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final OccupationRepository occupationRepository;
    private final OrganizationRepository organizationRepository;
    private final SkillEntryRepository skillEntryRepository;
    private final SkillRepository skillRepository;

    private final EmployeeMapper employeeMapper;
    private final SkillEntryMapper skillEntryMapper;

    private final ObjectMapper objectMapper = new ObjectMapper();

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
        checkIfDuplicateEmailExists(dto.email());

        Employee employee = employeeMapper.toEntity(dto);

        setEmployeeRelations(dto, employee);

        employeeRepository.save(employee);
    }

    @Transactional
    public void updateEmployee(Integer id, SaveEmployeeDto dto) {
        checkIfDuplicateEmailExists(dto.email());

        Employee employee = findById(id);

        employeeMapper.updateFromDto(employee, dto);
        setEmployeeRelations(dto, employee);

    }

    @Transactional
    public void deleteEmployee(Integer id) {
        employeeRepository.deleteById(id);
    }

    @Transactional
    public void changeDepartmentOfEmployee(Integer employeeId, Integer departmentId) {
        Employee employee = findById(employeeId);
        Department target = findDepartmentById(departmentId);

        if (!Objects.equals(target.getOrganization().getId(), employee.getOrganization().getId())) {
            throw new RuntimeException("Target department belongs to a different organization");
        }
        employee.setDepartment(target);
    }

    @Transactional(readOnly = true)
    public List<SkillEntryDto> getSkillEntriesOfEmployee(Integer employeeId) {
        ensureEmployeeExists(employeeId);
        return skillEntryMapper.toDtos(
                skillEntryRepository.findAllByEmployeeIdOrderByEntryDateDesc(employeeId)
        );
    }

    @SneakyThrows
    @Transactional
    public void bulkCreate(String json) {
        List<SaveEmployeeDto> dtos = objectMapper.readValue(json, new TypeReference<>() {});

        List<Employee> entities = new ArrayList<>(dtos.size());
        for (SaveEmployeeDto dto : dtos) {
            Employee e = employeeMapper.toEntity(dto);
            setEmployeeRelations(dto, e);
            entities.add(e);
        }
        employeeRepository.saveAll(entities);
    }

    @Transactional(readOnly = true)
    public List<SkillToRatingDto> getLatestSkillEntriesOfEmployee(Integer employeeId) {
        ensureEmployeeExists(employeeId);

        List<SkillEntry> all = skillEntryRepository.findAllByEmployeeIdOrderByEntryDateDesc(employeeId);

        Map<Integer, SkillEntry> latestPerSkill = new LinkedHashMap<>();
        for (SkillEntry se : all) {
            Integer skillId = se.getSkill().getId();
            latestPerSkill.putIfAbsent(skillId, se);
        }

        return latestPerSkill.values().stream()
                .map(it -> new SkillToRatingDto(it.getId(), it.getSkill().getName(), it.getRating()))
                .toList();
    }

    @Transactional
    public void addSkillEntryToEmployee(Integer employeeId, SaveSkillEntryDto dto) {
        Employee employee = findById(employeeId);
        Skill skill = skillRepository.findById(dto.skillId())
                .orElseThrow(() -> new RuntimeException("Skill not found"));

        SkillEntry se = new SkillEntry();
        se.setEmployee(employee);
        se.setSkill(skill);
        se.setRating(dto.rating());
        se.setEntryDate(LocalDate.now());
        se.setEntryDateTime(LocalDateTime.now());
        skillEntryRepository.save(se);
    }

    @Transactional
    public void removeSkillEntryFromEmployee(Integer employeeId, Integer skillEntryId) {
        boolean owns = skillEntryRepository.existsByIdAndEmployeeId(skillEntryId, employeeId);
        if (!owns) {
            throw new RuntimeException("Skill entry not found for this employee");
        }
        skillEntryRepository.deleteById(skillEntryId);
    }

    private void setEmployeeRelations(SaveEmployeeDto dto, Employee employee) {
        Department department = findDepartmentById(dto.departmentId());
        Organization organization = findOrganizationById(dto.organizationId());
        Occupation occupation = findOccupationById(dto.occupationId());

        if (!department.getOrganization().getId().equals(organization.getId())) {
            throw new RuntimeException("Department does not belong to the Organization");
        }

        employee.setDepartment(department);

        employee.setOccupation(occupation);

        employee.setOrganization(organization);
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

    private void ensureEmployeeExists(Integer id) {
        findById(id);
    }

    private void checkIfDuplicateEmailExists(String email) {
        if (employeeRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Employee already exists with the provided email");
        }
    }
}
