package gr.uom.employeepulseservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import gr.uom.employeepulseservice.controller.dto.*;
import gr.uom.employeepulseservice.mapper.EmployeeMapper;
import gr.uom.employeepulseservice.mapper.SkillEntryMapper;
import gr.uom.employeepulseservice.model.*;
import gr.uom.employeepulseservice.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private OccupationRepository occupationRepository;

    @Mock
    private OrganizationRepository organizationRepository;

    @Mock
    private SkillEntryRepository skillEntryRepository;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private EmployeeMapper employeeMapper;

    @Mock
    private SkillEntryMapper skillEntryMapper;

    @InjectMocks
    private EmployeeService employeeService;

    private Employee employee;
    private Department department;
    private Organization organization;
    private Occupation occupation;
    private Skill skill;
    private SkillEntry skillEntry;
    private EmployeeDto employeeDto;
    private SaveEmployeeDto saveEmployeeDto;
    private SkillEntryDto skillEntryDto;

    @BeforeEach
    void setUp() {
        organization = new Organization();
        organization.setId(1);
        organization.setName("Test Organization");

        department = new Department();
        department.setId(1);
        department.setName("Test Department");
        department.setOrganization(organization);

        occupation = new Occupation();
        occupation.setId(1);
        occupation.setTitle("Software Engineer");

        employee = new Employee();
        employee.setId(1);
        employee.setFirstName("John");
        employee.setLastName("Doe");
        employee.setEmail("john.doe@test.com");
        employee.setHireDate(LocalDate.of(2020, 1, 1));
        employee.setDepartment(department);
        employee.setOrganization(organization);
        employee.setOccupation(occupation);

        skill = new Skill();
        skill.setId(1);
        skill.setName("Java");

        skillEntry = new SkillEntry();
        skillEntry.setId(1);
        skillEntry.setEmployee(employee);
        skillEntry.setSkill(skill);
        skillEntry.setRating(4.5);
        skillEntry.setEntryDate(LocalDate.now());
        skillEntry.setEntryDateTime(LocalDate.now().atStartOfDay());

        employeeDto = new EmployeeDto(1, "John", "Doe", "john.doe@test.com",
                LocalDate.of(2020, 1, 1), 1, "Test Department", 1, 1, "Software Engineer", null);

        saveEmployeeDto = new SaveEmployeeDto("John", "Doe", "john.doe@test.com",
                LocalDate.of(2020, 1, 1), 1, 1, 1);

        skillEntryDto = new SkillEntryDto(1, 1, "Java", 4.5, LocalDate.now(),
                LocalDate.now().atStartOfDay(), 1);

    }

    @Test
    void findAll_ShouldReturnListOfEmployeeDtos() {
        // Given
        List<Employee> employees = Collections.singletonList(employee);
        List<EmployeeDto> expectedDtos = Collections.singletonList(employeeDto);

        when(employeeRepository.findAll()).thenReturn(employees);
        when(employeeMapper.toDtos(employees)).thenReturn(expectedDtos);

        // When
        List<EmployeeDto> result = employeeService.findAll();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(expectedDtos, result);
        verify(employeeRepository).findAll();
        verify(employeeMapper).toDtos(employees);
    }

    @Test
    void findEmployeeById_WhenEmployeeExists_ShouldReturnEmployeeDto() {
        // Given
        when(employeeRepository.findById(1)).thenReturn(Optional.of(employee));
        when(employeeMapper.toDto(employee)).thenReturn(employeeDto);

        // When
        EmployeeDto result = employeeService.findEmployeeById(1);

        // Then
        assertNotNull(result);
        assertEquals(employeeDto, result);
        verify(employeeRepository).findById(1);
        verify(employeeMapper).toDto(employee);
    }

    @Test
    void findEmployeeById_WhenEmployeeNotFound_ShouldThrowRuntimeException() {
        // Given
        when(employeeRepository.findById(999)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> employeeService.findEmployeeById(999));

        assertEquals("Employee not found", exception.getMessage());
        verify(employeeRepository).findById(999);
    }

    @Test
    void createEmployee_WhenValid_ShouldSaveEmployee() {
        // Given
        Employee newEmployee = new Employee();
        newEmployee.setFirstName("Jane");
        newEmployee.setLastName("Smith");
        newEmployee.setEmail("jane.smith@test.com");

        when(employeeRepository.existsByEmail(saveEmployeeDto.email())).thenReturn(false);
        when(departmentRepository.findById(1)).thenReturn(Optional.of(department));
        when(organizationRepository.findById(1)).thenReturn(Optional.of(organization));
        when(occupationRepository.findById(1)).thenReturn(Optional.of(occupation));
        when(employeeMapper.toEntity(saveEmployeeDto)).thenReturn(newEmployee);
        when(employeeRepository.save(any(Employee.class))).thenReturn(newEmployee);

        // When
        employeeService.createEmployee(saveEmployeeDto);

        // Then
        verify(employeeRepository).existsByEmail(saveEmployeeDto.email());
        verify(departmentRepository).findById(1);
        verify(organizationRepository).findById(1);
        verify(occupationRepository).findById(1);
        verify(employeeMapper).toEntity(saveEmployeeDto);
        verify(employeeRepository).save(any(Employee.class));
    }

    @Test
    void createEmployee_WhenDuplicateEmail_ShouldThrowIllegalArgumentException() {
        // Given
        when(employeeRepository.existsByEmail("john.doe@test.com")).thenReturn(true);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> employeeService.createEmployee(saveEmployeeDto));

        assertEquals("Employee already exists with the provided email", exception.getMessage());
        verify(employeeRepository).existsByEmail("john.doe@test.com");
        verify(employeeRepository, never()).save(any());
    }

    @Test
    void createEmployee_WhenDepartmentNotFound_ShouldThrowRuntimeException() {
        // Given
        when(employeeRepository.existsByEmail(anyString())).thenReturn(false);
        when(departmentRepository.findById(999)).thenReturn(Optional.empty());
        when(employeeMapper.toEntity(any())).thenReturn(new Employee());

        SaveEmployeeDto dto = new SaveEmployeeDto("Jane", "Smith", "jane@test.com",
                LocalDate.now(), 1, 999, 1);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> employeeService.createEmployee(dto));

        assertEquals("Department not found", exception.getMessage());
        verify(departmentRepository).findById(999);
    }

    @Test
    void createEmployee_WhenDepartmentNotBelongsToOrganization_ShouldThrowRuntimeException() {
        // Given
        Organization otherOrg = new Organization();
        otherOrg.setId(2);
        Department otherDept = new Department();
        otherDept.setId(2);
        otherDept.setOrganization(otherOrg);

        when(employeeRepository.existsByEmail(anyString())).thenReturn(false);
        when(departmentRepository.findById(2)).thenReturn(Optional.of(otherDept));
        when(organizationRepository.findById(1)).thenReturn(Optional.of(organization));
        when(occupationRepository.findById(1)).thenReturn(Optional.of(occupation));
        when(employeeMapper.toEntity(any())).thenReturn(new Employee());

        SaveEmployeeDto dto = new SaveEmployeeDto("Jane", "Smith", "jane@test.com",
                LocalDate.now(), 1, 2, 1);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> employeeService.createEmployee(dto));

        assertEquals("Department does not belong to the Organization", exception.getMessage());
        verify(employeeRepository, never()).save(any());
    }

    @Test
    void updateEmployee_WhenValid_ShouldUpdateEmployee() {
        // Given
        SaveEmployeeDto updateDto = new SaveEmployeeDto("John", "Updated", "john.doe@test.com",
                LocalDate.of(2020, 1, 1), 1, 1, 1);

        when(employeeRepository.findById(1)).thenReturn(Optional.of(employee));
        when(departmentRepository.findById(1)).thenReturn(Optional.of(department));
        when(organizationRepository.findById(1)).thenReturn(Optional.of(organization));
        when(occupationRepository.findById(1)).thenReturn(Optional.of(occupation));

        // When
        employeeService.updateEmployee(1, updateDto);

        // Then
        verify(employeeRepository).findById(1);
        verify(employeeMapper).updateFromDto(employee, updateDto);
        verify(departmentRepository).findById(1);
        verify(organizationRepository).findById(1);
        verify(occupationRepository).findById(1);
    }

    @Test
    void updateEmployee_WhenEmailChangedAndDuplicate_ShouldThrowIllegalArgumentException() {
        // Given
        SaveEmployeeDto updateDto = new SaveEmployeeDto("John", "Doe", "new.email@test.com",
                LocalDate.of(2020, 1, 1), 1, 1, 1);

        when(employeeRepository.findById(1)).thenReturn(Optional.of(employee));
        when(employeeRepository.existsByEmail("new.email@test.com")).thenReturn(true);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> employeeService.updateEmployee(1, updateDto));

        assertEquals("Employee already exists with the provided email", exception.getMessage());
        verify(employeeRepository).existsByEmail("new.email@test.com");
    }

    @Test
    void updateEmployee_WhenEmployeeNotFound_ShouldThrowRuntimeException() {
        // Given
        when(employeeRepository.findById(999)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> employeeService.updateEmployee(999, saveEmployeeDto));

        assertEquals("Employee not found", exception.getMessage());
        verify(employeeRepository).findById(999);
    }

    @Test
    void deleteEmployee_ShouldDeleteManagerAndEmployee() {
        // Given
        // When
        employeeService.deleteEmployee(1);

        // Then
        verify(departmentRepository).deleteManagerOfDepartmentsByEmployeeId(1);
        verify(employeeRepository).deleteById(1);
    }

    @Test
    void changeDepartmentOfEmployee_WhenValid_ShouldChangeDepartment() {
        // Given
        Department newDepartment = new Department();
        newDepartment.setId(2);
        newDepartment.setName("New Department");
        newDepartment.setOrganization(organization);

        when(employeeRepository.findById(1)).thenReturn(Optional.of(employee));
        when(departmentRepository.findById(2)).thenReturn(Optional.of(newDepartment));

        // When
        employeeService.changeDepartmentOfEmployee(1, 2);

        // Then
        assertEquals(newDepartment, employee.getDepartment());
        verify(employeeRepository).findById(1);
        verify(departmentRepository).findById(2);
    }

    @Test
    void changeDepartmentOfEmployee_WhenDifferentOrganization_ShouldThrowRuntimeException() {
        // Given
        Organization otherOrg = new Organization();
        otherOrg.setId(2);
        Department otherDept = new Department();
        otherDept.setId(2);
        otherDept.setOrganization(otherOrg);

        when(employeeRepository.findById(1)).thenReturn(Optional.of(employee));
        when(departmentRepository.findById(2)).thenReturn(Optional.of(otherDept));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> employeeService.changeDepartmentOfEmployee(1, 2));

        assertEquals("Target department belongs to a different organization", exception.getMessage());
        verify(employeeRepository).findById(1);
        verify(departmentRepository).findById(2);
    }

    @Test
    void getSkillEntriesOfEmployee_WhenEmployeeExists_ShouldReturnSkillEntries() {
        // Given
        List<SkillEntry> skillEntries = Collections.singletonList(skillEntry);
        List<SkillEntryDto> expectedDtos = Collections.singletonList(skillEntryDto);

        when(employeeRepository.findById(1)).thenReturn(Optional.of(employee));
        when(skillEntryRepository.findAllByEmployeeIdOrderByEntryDateDesc(1)).thenReturn(skillEntries);
        when(skillEntryMapper.toDtos(skillEntries)).thenReturn(expectedDtos);

        // When
        List<SkillEntryDto> result = employeeService.getSkillEntriesOfEmployee(1);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(expectedDtos, result);
        verify(employeeRepository).findById(1);
        verify(skillEntryRepository).findAllByEmployeeIdOrderByEntryDateDesc(1);
        verify(skillEntryMapper).toDtos(skillEntries);
    }

    @Test
    void getSkillEntriesOfEmployee_WhenEmployeeNotFound_ShouldThrowRuntimeException() {
        // Given
        when(employeeRepository.findById(999)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> employeeService.getSkillEntriesOfEmployee(999));

        assertEquals("Employee not found", exception.getMessage());
        verify(employeeRepository).findById(999);
    }

    @Test
    void bulkCreate_WhenValid_ShouldSaveAllEmployees() throws Exception {
        // Given
        SaveEmployeeDto dto1 = new SaveEmployeeDto("John", "Doe", "john@test.com",
                LocalDate.now(), 1, 1, 1);
        SaveEmployeeDto dto2 = new SaveEmployeeDto("Jane", "Smith", "jane@test.com",
                LocalDate.now(), 1, 1, 1);

        ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();
        String json = objectMapper.writeValueAsString(Arrays.asList(dto1, dto2));

        Employee emp1 = new Employee();
        Employee emp2 = new Employee();

        when(employeeMapper.toEntity(dto1)).thenReturn(emp1);
        when(employeeMapper.toEntity(dto2)).thenReturn(emp2);
        when(departmentRepository.findById(1)).thenReturn(Optional.of(department));
        when(organizationRepository.findById(1)).thenReturn(Optional.of(organization));
        when(occupationRepository.findById(1)).thenReturn(Optional.of(occupation));
        when(employeeRepository.saveAll(any())).thenReturn(Arrays.asList(emp1, emp2));

        // When
        employeeService.bulkCreate(json);

        // Then
        verify(employeeMapper, times(2)).toEntity(any());
        verify(employeeRepository).saveAll(any());
    }

    @Test
    void getLatestSkillEntriesOfEmployee_WhenEmployeeExists_ShouldReturnLatestPerSkill() {
        // Given
        Skill skill2 = new Skill();
        skill2.setId(2);
        skill2.setName("Python");

        SkillEntry entry1 = new SkillEntry();
        entry1.setId(1);
        entry1.setSkill(skill);
        entry1.setRating(4.5);
        entry1.setEntryDate(LocalDate.of(2024, 1, 1));

        SkillEntry entry2 = new SkillEntry();
        entry2.setId(2);
        entry2.setSkill(skill);
        entry2.setRating(5.0);
        entry2.setEntryDate(LocalDate.of(2024, 2, 1));

        SkillEntry entry3 = new SkillEntry();
        entry3.setId(3);
        entry3.setSkill(skill2);
        entry3.setRating(3.5);
        entry3.setEntryDate(LocalDate.of(2024, 1, 15));

        List<SkillEntry> allEntries = Arrays.asList(entry2, entry3, entry1);

        when(employeeRepository.findById(1)).thenReturn(Optional.of(employee));
        when(skillEntryRepository.findAllByEmployeeIdOrderByEntryDateDesc(1)).thenReturn(allEntries);

        // When
        List<SkillToRatingDto> result = employeeService.getLatestSkillEntriesOfEmployee(1);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1, result.get(0).skillId());
        assertEquals(5.0, result.get(0).rating()); // Latest entry for skill 1
        assertEquals(2, result.get(1).skillId());
        assertEquals(3.5, result.get(1).rating()); // Latest entry for skill 2
        verify(employeeRepository).findById(1);
        verify(skillEntryRepository).findAllByEmployeeIdOrderByEntryDateDesc(1);
    }

    @Test
    void addSkillEntryToEmployee_WhenValid_ShouldSaveSkillEntry() {
        // Given
        SaveSkillEntryDto dto = new SaveSkillEntryDto(1, 4.5, LocalDate.now());

        when(employeeRepository.findById(1)).thenReturn(Optional.of(employee));
        when(skillRepository.findById(1)).thenReturn(Optional.of(skill));
        when(skillEntryRepository.save(any(SkillEntry.class))).thenReturn(skillEntry);

        // When
        employeeService.addSkillEntryToEmployee(1, dto);

        // Then
        verify(employeeRepository).findById(1);
        verify(skillRepository).findById(1);
        verify(skillEntryRepository).save(any(SkillEntry.class));
    }

    @Test
    void addSkillEntryToEmployee_WhenEntryDateNull_ShouldUseCurrentDate() {
        // Given
        SaveSkillEntryDto dto = new SaveSkillEntryDto(1, 4.5, null);

        when(employeeRepository.findById(1)).thenReturn(Optional.of(employee));
        when(skillRepository.findById(1)).thenReturn(Optional.of(skill));
        when(skillEntryRepository.save(any(SkillEntry.class))).thenAnswer(invocation -> {
            SkillEntry se = invocation.getArgument(0);
            assertNotNull(se.getEntryDate());
            assertEquals(LocalDate.now(), se.getEntryDate());
            return se;
        });

        // When
        employeeService.addSkillEntryToEmployee(1, dto);

        // Then
        verify(skillEntryRepository).save(any(SkillEntry.class));
    }

    @Test
    void addSkillEntryToEmployee_WhenSkillNotFound_ShouldThrowRuntimeException() {
        // Given
        SaveSkillEntryDto dto = new SaveSkillEntryDto(999, 4.5, LocalDate.now());

        when(employeeRepository.findById(1)).thenReturn(Optional.of(employee));
        when(skillRepository.findById(999)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> employeeService.addSkillEntryToEmployee(1, dto));

        assertEquals("Skill not found", exception.getMessage());
        verify(skillRepository).findById(999);
        verify(skillEntryRepository, never()).save(any());
    }

    @Test
    void removeSkillEntryFromEmployee_WhenValid_ShouldDeleteSkillEntry() {
        // Given
        when(skillEntryRepository.existsByIdAndEmployeeId(1, 1)).thenReturn(true);

        // When
        employeeService.removeSkillEntryFromEmployee(1, 1);

        // Then
        verify(skillEntryRepository).existsByIdAndEmployeeId(1, 1);
        verify(skillEntryRepository).deleteById(1);
    }

    @Test
    void removeSkillEntryFromEmployee_WhenNotOwnedByEmployee_ShouldThrowRuntimeException() {
        // Given
        when(skillEntryRepository.existsByIdAndEmployeeId(1, 1)).thenReturn(false);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> employeeService.removeSkillEntryFromEmployee(1, 1));

        assertEquals("Skill entry not found for this employee", exception.getMessage());
        verify(skillEntryRepository).existsByIdAndEmployeeId(1, 1);
        verify(skillEntryRepository, never()).deleteById(anyInt());
    }
}

