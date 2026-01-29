package gr.uom.employeepulseservice.service;

import gr.uom.employeepulseservice.controller.dto.*;
import gr.uom.employeepulseservice.mapper.DepartmentMapper;
import gr.uom.employeepulseservice.mapper.EmployeeMapper;
import gr.uom.employeepulseservice.mapper.OrganizationMapper;
import gr.uom.employeepulseservice.mapper.SkillMapper;
import gr.uom.employeepulseservice.model.Department;
import gr.uom.employeepulseservice.model.Employee;
import gr.uom.employeepulseservice.model.Organization;
import gr.uom.employeepulseservice.model.Skill;
import gr.uom.employeepulseservice.repository.DepartmentRepository;
import gr.uom.employeepulseservice.repository.EmployeeRepository;
import gr.uom.employeepulseservice.repository.OrganizationRepository;
import gr.uom.employeepulseservice.repository.SkillRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrganizationServiceTest {

    @Mock
    private OrganizationRepository organizationRepository;

    @Mock
    private OrganizationMapper organizationMapper;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private EmployeeMapper employeeMapper;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private DepartmentMapper departmentMapper;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private SkillMapper skillMapper;

    @InjectMocks
    private OrganizationService organizationService;

    private Organization organization;
    private OrganizationDto organizationDto;
    private SaveOrganizationDto saveOrganizationDto;

    private Employee employee;
    private EmployeeDto employeeDto;

    private Department department;
    private DepartmentDto departmentDto;

    private Skill skill;
    private SkillDto skillDto;

    @BeforeEach
    void setUp() {
        organization = new Organization();
        organization.setId(1);
        organization.setName("Test Organization");
        organization.setLocation("Test Location");

        organizationDto = new OrganizationDto(1, "Test Organization", "Test Location");
        saveOrganizationDto = new SaveOrganizationDto("New Organization", "New Location");

        department = new Department();
        department.setId(10);
        department.setName("Test Department");
        department.setOrganization(organization);

        departmentDto = new DepartmentDto(10, "Test Department", 1, null, null, null);

        employee = new Employee();
        employee.setId(100);
        employee.setFirstName("John");
        employee.setLastName("Doe");
        employee.setOrganization(organization);
        employee.setDepartment(department);

        employeeDto = new EmployeeDto(
                100,
                "John",
                "Doe",
                null,
                null,
                10,
                "Test Department",
                1,
                null,
                null,
                null
        );

        skill = new Skill();
        skill.setId(200);
        skill.setName("Java");
        skill.setDescription("Programming language");
        skill.setEscoId("ESCO-1");

        skillDto = new SkillDto(200, "Java", "Programming language", "ESCO-1");
    }

    @Test
    void findAll_ShouldReturnListOfOrganizationDtos() {
        // Given
        List<Organization> organizations = Collections.singletonList(organization);
        List<OrganizationDto> expectedDtos = Collections.singletonList(organizationDto);

        when(organizationRepository.findAll()).thenReturn(organizations);
        when(organizationMapper.toDtos(organizations)).thenReturn(expectedDtos);

        // When
        List<OrganizationDto> result = organizationService.findAll();

        // Then
        assertNotNull(result);
        assertEquals(expectedDtos, result);
        verify(organizationRepository).findAll();
        verify(organizationMapper).toDtos(organizations);
    }

    @Test
    void findOrganizationById_WhenOrganizationExists_ShouldReturnOrganizationDto() {
        // Given
        when(organizationRepository.findById(1)).thenReturn(Optional.of(organization));
        when(organizationMapper.toDto(organization)).thenReturn(organizationDto);

        // When
        OrganizationDto result = organizationService.findOrganizationById(1);

        // Then
        assertNotNull(result);
        assertEquals(organizationDto, result);
        verify(organizationRepository).findById(1);
        verify(organizationMapper).toDto(organization);
    }

    @Test
    void findOrganizationById_WhenOrganizationNotFound_ShouldThrowRuntimeException() {
        // Given
        when(organizationRepository.findById(999)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> organizationService.findOrganizationById(999));
        assertEquals("Organization not found", exception.getMessage());

        verify(organizationRepository).findById(999);
        verify(organizationMapper, never()).toDto(any());
    }

    @Test
    void createOrganization_WhenValid_ShouldSaveOrganization() {
        // Given
        Organization newOrganization = new Organization();
        newOrganization.setName("New Organization");
        newOrganization.setLocation("New Location");

        when(organizationMapper.toEntity(saveOrganizationDto)).thenReturn(newOrganization);
        when(organizationRepository.save(any(Organization.class))).thenReturn(newOrganization);

        // When
        organizationService.createOrganization(saveOrganizationDto);

        // Then
        verify(organizationMapper).toEntity(saveOrganizationDto);
        verify(organizationRepository).save(newOrganization);
    }

    @Test
    void updateOrganization_WhenOrganizationExists_ShouldUpdateNameAndLocation() {
        // Given
        SaveOrganizationDto dto = new SaveOrganizationDto("Updated Organization", "Updated Location");
        when(organizationRepository.findById(1)).thenReturn(Optional.of(organization));

        // When
        organizationService.updateOrganization(1, dto);

        // Then
        assertEquals("Updated Organization", organization.getName());
        assertEquals("Updated Location", organization.getLocation());
        verify(organizationRepository).findById(1);
        verify(organizationRepository, never()).save(any());
    }

    @Test
    void updateOrganization_WhenOrganizationNotFound_ShouldThrowRuntimeException() {
        // Given
        SaveOrganizationDto dto = new SaveOrganizationDto("Updated Organization", "Updated Location");
        when(organizationRepository.findById(999)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> organizationService.updateOrganization(999, dto));
        assertEquals("Organization not found", exception.getMessage());
        verify(organizationRepository).findById(999);
    }

    @Test
    void deleteOrganization_ShouldDeleteOrganization() {
        // When
        organizationService.deleteOrganization(1);

        // Then
        verify(organizationRepository).deleteById(1);
    }

    @Test
    void findEmployeesById_ShouldReturnListOfEmployeeDtos() {
        // Given
        List<Employee> employees = Collections.singletonList(employee);
        List<EmployeeDto> expectedDtos = Collections.singletonList(employeeDto);

        when(employeeRepository.findByOrganizationIdOrderByHireDateDesc(1)).thenReturn(employees);
        when(employeeMapper.toDtos(employees)).thenReturn(expectedDtos);

        // When
        List<EmployeeDto> result = organizationService.findEmployeesById(1);

        // Then
        assertNotNull(result);
        assertEquals(expectedDtos, result);
        verify(employeeRepository).findByOrganizationIdOrderByHireDateDesc(1);
        verify(employeeMapper).toDtos(employees);
    }

    @Test
    void findDepartmentsById_WhenOrganizationExists_ShouldReturnListOfDepartmentDtos() {
        // Given
        List<Department> departments = Collections.singletonList(department);
        List<DepartmentDto> expectedDtos = Collections.singletonList(departmentDto);

        when(organizationRepository.findById(1)).thenReturn(Optional.of(organization));
        when(departmentRepository.findByOrganizationId(1)).thenReturn(departments);
        when(departmentMapper.toDtos(departments)).thenReturn(expectedDtos);

        // When
        List<DepartmentDto> result = organizationService.findDepartmentsById(1);

        // Then
        assertNotNull(result);
        assertEquals(expectedDtos, result);
        verify(organizationRepository).findById(1);
        verify(departmentRepository).findByOrganizationId(1);
        verify(departmentMapper).toDtos(departments);
    }

    @Test
    void findDepartmentsById_WhenOrganizationNotFound_ShouldThrowRuntimeException() {
        // Given
        when(organizationRepository.findById(999)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> organizationService.findDepartmentsById(999));
        assertEquals("Organization not found", exception.getMessage());

        verify(organizationRepository).findById(999);
        verify(departmentRepository, never()).findByOrganizationId(anyInt());
        verify(departmentMapper, never()).toDtos(any());
    }

    @Test
    void findSkillsById_WhenOrganizationExists_ShouldReturnListOfSkillDtos() {
        // Given
        List<Skill> skills = Collections.singletonList(skill);
        List<SkillDto> expectedDtos = Collections.singletonList(skillDto);

        when(organizationRepository.findById(1)).thenReturn(Optional.of(organization));
        when(skillRepository.findSkillsByOrganizationId(1)).thenReturn(skills);
        when(skillMapper.toDtos(skills)).thenReturn(expectedDtos);

        // When
        List<SkillDto> result = organizationService.findSkillsById(1);

        // Then
        assertNotNull(result);
        assertEquals(expectedDtos, result);
        verify(organizationRepository).findById(1);
        verify(skillRepository).findSkillsByOrganizationId(1);
        verify(skillMapper).toDtos(skills);
    }

    @Test
    void findSkillsById_WhenOrganizationNotFound_ShouldThrowRuntimeException() {
        // Given
        when(organizationRepository.findById(999)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> organizationService.findSkillsById(999));
        assertEquals("Organization not found", exception.getMessage());

        verify(organizationRepository).findById(999);
        verify(skillRepository, never()).findSkillsByOrganizationId(anyInt());
        verify(skillMapper, never()).toDtos(any());
    }
}


