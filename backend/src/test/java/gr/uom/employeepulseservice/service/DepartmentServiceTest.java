package gr.uom.employeepulseservice.service;

import gr.uom.employeepulseservice.controller.dto.CreateDepartmentDto;
import gr.uom.employeepulseservice.controller.dto.DepartmentDto;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DepartmentServiceTest {

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private OrganizationRepository organizationRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private DepartmentMapper departmentMapper;

    @Mock
    private EmployeeMapper employeeMapper;

    @InjectMocks
    private DepartmentService departmentService;

    private Department department;
    private Organization organization;
    private Employee employee;
    private DepartmentDto departmentDto;
    private EmployeeDto employeeDto;

    @BeforeEach
    void setUp() {
        organization = new Organization();
        organization.setId(1);
        organization.setName("Test Organization");

        department = new Department();
        department.setId(1);
        department.setName("Test Department");
        department.setOrganization(organization);

        employee = new Employee();
        employee.setId(1);
        employee.setFirstName("John");
        employee.setLastName("Doe");
        employee.setDepartment(department);

        departmentDto = new DepartmentDto(1, "Test Department", 1, null, null, null);
        employeeDto = new EmployeeDto(1, "John", "Doe", null, null, 1, "Test Department", 1, null, null, null);
    }

    @Test
    void findAll_ShouldReturnListOfDepartmentDtos() {
        // Given
        List<Department> departments = Collections.singletonList(department);
        List<DepartmentDto> expectedDtos = Collections.singletonList(departmentDto);

        when(departmentRepository.findAll()).thenReturn(departments);
        when(departmentMapper.toDtos(departments)).thenReturn(expectedDtos);

        // When
        List<DepartmentDto> result = departmentService.findAll();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(expectedDtos, result);
        verify(departmentRepository).findAll();
        verify(departmentMapper).toDtos(departments);
    }

    @Test
    void findDepartmentById_WhenDepartmentExists_ShouldReturnDepartmentDto() {
        // Given
        when(departmentRepository.findById(1)).thenReturn(Optional.of(department));
        when(departmentMapper.toDto(department)).thenReturn(departmentDto);

        // When
        DepartmentDto result = departmentService.findDepartmentById(1);

        // Then
        assertNotNull(result);
        assertEquals(departmentDto, result);
        verify(departmentRepository).findById(1);
        verify(departmentMapper).toDto(department);
    }

    @Test
    void findDepartmentById_WhenDepartmentNotFound_ShouldThrowRuntimeException() {
        // Given
        when(departmentRepository.findById(999)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> departmentService.findDepartmentById(999));

        assertEquals("Department not found", exception.getMessage());
        verify(departmentRepository).findById(999);
        verify(departmentMapper, never()).toDto(any());
    }

    @Test
    void createDepartment_WhenValid_ShouldSaveDepartment() {
        // Given
        CreateDepartmentDto dto = new CreateDepartmentDto("New Department", 1);
        Department newDepartment = new Department();
        newDepartment.setName("New Department");

        when(organizationRepository.findById(1)).thenReturn(Optional.of(organization));
        when(departmentMapper.toEntity(dto)).thenReturn(newDepartment);
        when(departmentRepository.save(any(Department.class))).thenReturn(newDepartment);

        // When
        departmentService.createDepartment(dto);

        // Then
        verify(organizationRepository).findById(1);
        verify(departmentMapper).toEntity(dto);
        verify(departmentRepository).save(any(Department.class));
        assertEquals(organization, newDepartment.getOrganization());
    }

    @Test
    void createDepartment_WhenOrganizationNotFound_ShouldThrowRuntimeException() {
        // Given
        CreateDepartmentDto dto = new CreateDepartmentDto("New Department", 999);
        Department newDepartment = new Department();

        when(organizationRepository.findById(999)).thenReturn(Optional.empty());
        when(departmentMapper.toEntity(dto)).thenReturn(newDepartment);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> departmentService.createDepartment(dto));

        assertEquals("Organization not found", exception.getMessage());
        verify(organizationRepository).findById(999);
        verify(departmentRepository, never()).save(any());
    }

    @Test
    void updateDepartment_WhenDepartmentExists_ShouldUpdateName() {
        // Given
        UpdateDepartmentDto dto = new UpdateDepartmentDto("Updated Department Name");

        when(departmentRepository.findById(1)).thenReturn(Optional.of(department));

        // When
        departmentService.updateDepartment(1, dto);

        // Then
        assertEquals("Updated Department Name", department.getName());
        verify(departmentRepository).findById(1);
    }

    @Test
    void updateDepartment_WhenDepartmentNotFound_ShouldThrowRuntimeException() {
        // Given
        UpdateDepartmentDto dto = new UpdateDepartmentDto("Updated Department Name");

        when(departmentRepository.findById(999)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> departmentService.updateDepartment(999, dto));

        assertEquals("Department not found", exception.getMessage());
        verify(departmentRepository).findById(999);
    }

    @Test
    void deleteDepartment_WhenNoEmployees_ShouldDeleteDepartment() {
        // Given
        when(employeeRepository.existsByDepartmentId(1)).thenReturn(false);

        // When
        departmentService.deleteDepartment(1);

        // Then
        verify(employeeRepository).existsByDepartmentId(1);
        verify(departmentRepository).deleteById(1);
    }

    @Test
    void deleteDepartment_WhenHasEmployees_ShouldThrowRuntimeException() {
        // Given
        when(employeeRepository.existsByDepartmentId(1)).thenReturn(true);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> departmentService.deleteDepartment(1));

        assertEquals("Cannot deleted department 1 because employees are assigned to it", exception.getMessage());
        verify(employeeRepository).existsByDepartmentId(1);
        verify(departmentRepository, never()).deleteById(anyInt());
    }

    @Test
    void findEmployeesById_WhenDepartmentExists_ShouldReturnListOfEmployeeDtos() {
        // Given
        List<Employee> employees = Collections.singletonList(employee);
        List<EmployeeDto> expectedDtos = Collections.singletonList(employeeDto);

        when(employeeRepository.findByDepartmentId(1)).thenReturn(employees);
        when(employeeMapper.toDtos(employees)).thenReturn(expectedDtos);

        // When
        List<EmployeeDto> result = departmentService.findEmployeesById(1);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(expectedDtos, result);
        verify(employeeRepository).findByDepartmentId(1);
        verify(employeeMapper).toDtos(employees);
    }

    @Test
    void findEmployeesById_WhenDepartmentNotFound_ShouldThrowRuntimeException() {
        // Given
        when(employeeRepository.findByDepartmentId(1)).thenReturn(Collections.emptyList());

        // When & Then
        List<EmployeeDto> result = departmentService.findEmployeesById(1);

        verify(employeeRepository).findByDepartmentId(1);
        assertEquals(0, result.size());
    }

    @Test
    void assignManagerToDepartment_WhenValid_ShouldAssignManager() {
        // Given
        Employee manager = new Employee();
        manager.setId(2);
        manager.setFirstName("Jane");
        manager.setLastName("Smith");
        manager.setDepartment(department);

        when(departmentRepository.findById(1)).thenReturn(Optional.of(department));
        when(employeeRepository.findById(2)).thenReturn(Optional.of(manager));

        // When
        departmentService.assignManagerToDepartment(1, 2);

        // Then
        assertEquals(manager, department.getManager());
        verify(departmentRepository).findById(1);
        verify(employeeRepository).findById(2);
    }

    @Test
    void assignManagerToDepartment_WhenDepartmentNotFound_ShouldThrowRuntimeException() {
        // Given
        when(departmentRepository.findById(999)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> departmentService.assignManagerToDepartment(999, 1));

        assertEquals("Department not found", exception.getMessage());
        verify(departmentRepository).findById(999);
        verify(employeeRepository, never()).findById(anyInt());
    }

    @Test
    void assignManagerToDepartment_WhenEmployeeNotFound_ShouldThrowRuntimeException() {
        // Given
        when(departmentRepository.findById(1)).thenReturn(Optional.of(department));
        when(employeeRepository.findById(999)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> departmentService.assignManagerToDepartment(1, 999));

        assertEquals("Employee not found", exception.getMessage());
        verify(departmentRepository).findById(1);
        verify(employeeRepository).findById(999);
    }

    @Test
    void assignManagerToDepartment_WhenManagerNotInDepartment_ShouldThrowIllegalArgumentException() {
        // Given
        Department otherDepartment = new Department();
        otherDepartment.setId(2);
        otherDepartment.setName("Other Department");

        Employee manager = new Employee();
        manager.setId(2);
        manager.setDepartment(otherDepartment);

        when(departmentRepository.findById(1)).thenReturn(Optional.of(department));
        when(employeeRepository.findById(2)).thenReturn(Optional.of(manager));

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> departmentService.assignManagerToDepartment(1, 2));

        assertEquals("Provided manager is not part the department", exception.getMessage());
        verify(departmentRepository).findById(1);
        verify(employeeRepository).findById(2);
        assertNull(department.getManager());
    }
}

