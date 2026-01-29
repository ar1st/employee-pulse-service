package gr.uom.employeepulseservice.repository.impl;

import gr.uom.employeepulseservice.controller.dto.reportingDto.employee.*;
import gr.uom.employeepulseservice.controller.dto.reportingDto.orgdept.*;
import gr.uom.employeepulseservice.model.PeriodType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
class ReportingRepositoryImplTest {

    @Mock
    private NamedParameterJdbcTemplate jdbcTemplate;

    @InjectMocks
    private ReportingRepositoryImpl reportingRepository;

    private LocalDate startDate;
    private LocalDate endDate;

    @BeforeEach
    void setUp() {
        startDate = LocalDate.of(2026, 1, 1);
        endDate = LocalDate.of(2026, 12, 31);
    }

    @Test
    void getReportByOrganizationAndDepartment_WithAllParameters_ShouldReturnGroupedData() {
        // Given
        Integer orgId = 1;
        Integer deptId = 10;
        Integer skillId = 5;
        PeriodType periodType = PeriodType.MONTH;

        List<OrgDeptReportingStatsDto> mockRows = List.of(
                new OrgDeptReportingStatsDto("Org1", "Dept1", "Java", LocalDate.of(2026, 1, 1), 4.5, 3.0, 5.0, 10L, 5L),
                new OrgDeptReportingStatsDto("Org1", "Dept1", "Java", LocalDate.of(2026, 2, 1), 4.0, 3.5, 4.5, 8L, 4L),
                new OrgDeptReportingStatsDto("Org1", "Dept1", "Python", LocalDate.of(2026, 1, 1), 3.5, 2.0, 4.0, 6L, 3L)
        );

        when(jdbcTemplate.query(anyString(), any(MapSqlParameterSource.class), any(RowMapper.class))).thenReturn(mockRows);

        // When
        OrgDeptReportingResponseDto result = reportingRepository.getReportByOrganizationAndDepartment(
                periodType, orgId, deptId, skillId, startDate, endDate);

        // Then
        assertNotNull(result);
        assertEquals(orgId, result.organizationId());
        assertEquals("Org1", result.organizationName());
        assertEquals(deptId, result.departmentId());
        assertEquals("Dept1", result.departmentName());
        assertEquals(2, result.skills().size());

        // Verify Java skill has 2 periods
        OrgDeptReportingSkillDto javaSkill = result.skills().stream()
                .filter(s -> s.skillName().equals("Java"))
                .findFirst()
                .orElseThrow();
        assertEquals(2, javaSkill.periods().size());

        // Verify Python skill has 1 period
        OrgDeptReportingSkillDto pythonSkill = result.skills().stream()
                .filter(s -> s.skillName().equals("Python"))
                .findFirst()
                .orElseThrow();
        assertEquals(1, pythonSkill.periods().size());

        // Verify SQL parameters
        ArgumentCaptor<MapSqlParameterSource> paramsCaptor = ArgumentCaptor.forClass(MapSqlParameterSource.class);
        verify(jdbcTemplate).query(anyString(), paramsCaptor.capture(), any(RowMapper.class));
        MapSqlParameterSource params = paramsCaptor.getValue();
        assertEquals(orgId, params.getValue("orgId"));
        assertEquals(deptId, params.getValue("deptId"));
        assertEquals(skillId, params.getValue("skillId"));
        assertEquals(startDate, params.getValue("startDate"));
        assertEquals(endDate, params.getValue("endDate"));
    }

    @Test
    void getReportByOrganizationAndDepartment_WithoutOptionalParameters_ShouldBuildCorrectSql() {
        // Given
        Integer orgId = 1;
        PeriodType periodType = PeriodType.QUARTER;

        List<OrgDeptReportingStatsDto> mockRows = List.of(
                new OrgDeptReportingStatsDto("Org1", null, "Java", LocalDate.of(2026, 1, 1), 4.5, 3.0, 5.0, 10L, 5L)
        );

        when(jdbcTemplate.query(anyString(), any(MapSqlParameterSource.class), any(RowMapper.class))).thenReturn(mockRows);

        // When
        OrgDeptReportingResponseDto result = reportingRepository.getReportByOrganizationAndDepartment(
                periodType, orgId, null, null, null, null);

        // Then
        assertNotNull(result);
        assertEquals(orgId, result.organizationId());
        assertNull(result.departmentId());
        assertNull(result.departmentName());

        // Verify SQL contains NULL AS department_name and no deptId/skillId filters
        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(jdbcTemplate).query(sqlCaptor.capture(), any(MapSqlParameterSource.class), any(RowMapper.class));
        String sql = sqlCaptor.getValue();
        assertTrue(sql.contains("NULL AS department_name"));
        assertFalse(sql.contains("department_id = :deptId"));
        assertFalse(sql.contains("skill_id = :skillId"));

        // Verify parameters
        ArgumentCaptor<MapSqlParameterSource> paramsCaptor = ArgumentCaptor.forClass(MapSqlParameterSource.class);
        verify(jdbcTemplate).query(anyString(), paramsCaptor.capture(), any(RowMapper.class));
        MapSqlParameterSource params = paramsCaptor.getValue();
        assertEquals(orgId, params.getValue("orgId"));
        assertFalse(params.hasValue("deptId"));
        assertFalse(params.hasValue("skillId"));
        assertFalse(params.hasValue("startDate"));
        assertFalse(params.hasValue("endDate"));
    }

    @Test
    void getReportByOrganizationAndDepartment_WithNullPeriodType_ShouldDefaultToQuarter() {
        // Given
        Integer orgId = 1;
        List<OrgDeptReportingStatsDto> mockRows = List.of(
                new OrgDeptReportingStatsDto("Org1", "Dept1", "Java", LocalDate.of(2026, 1, 1), 4.5, 3.0, 5.0, 10L, 5L)
        );

        when(jdbcTemplate.query(anyString(), any(MapSqlParameterSource.class), any(RowMapper.class))).thenReturn(mockRows);

        // When
        OrgDeptReportingResponseDto result = reportingRepository.getReportByOrganizationAndDepartment(
                null, orgId, null, null, null, null);

        // Then
        assertNotNull(result);

        // Verify SQL uses QUARTER period expression
        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(jdbcTemplate).query(sqlCaptor.capture(), any(MapSqlParameterSource.class), any(RowMapper.class));
        String sql = sqlCaptor.getValue();
        assertTrue(sql.contains("date_trunc('quarter', entry_date)::date"));
    }

    @Test
    void getReportByOrganizationAndDepartment_WithEmptyResults_ShouldReturnNull() {
        // Given
        Integer orgId = 1;
        when(jdbcTemplate.query(anyString(), any(MapSqlParameterSource.class), any(RowMapper.class))).thenReturn(new ArrayList<>());

        // When
        OrgDeptReportingResponseDto result = reportingRepository.getReportByOrganizationAndDepartment(
                PeriodType.MONTH, orgId, null, null, null, null);

        // Then
        assertNull(result);
    }

    @Test
    void getReportByOrganizationAndDepartment_WithDifferentPeriodTypes_ShouldUseCorrectExpression() {
        // Given
        Integer orgId = 1;
        List<OrgDeptReportingStatsDto> mockRows = List.of(
                new OrgDeptReportingStatsDto("Org1", "Dept1", "Java", LocalDate.of(2026, 1, 1), 4.5, 3.0, 5.0, 10L, 5L)
        );

        when(jdbcTemplate.query(anyString(), any(MapSqlParameterSource.class), any(RowMapper.class))).thenReturn(mockRows);

        // Test DAY
        reportingRepository.getReportByOrganizationAndDepartment(PeriodType.DAY, orgId, null, null, null, null);
        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(jdbcTemplate, atLeastOnce()).query(sqlCaptor.capture(), any(MapSqlParameterSource.class), any(RowMapper.class));
        String sql = sqlCaptor.getAllValues().getLast();
        assertTrue(sql.contains("date_trunc('day', entry_date)::date"));

        // Test WEEK
        reportingRepository.getReportByOrganizationAndDepartment(PeriodType.WEEK, orgId, null, null, null, null);
        verify(jdbcTemplate, atLeast(2)).query(anyString(), any(MapSqlParameterSource.class), any(RowMapper.class));

        // Test YEAR
        reportingRepository.getReportByOrganizationAndDepartment(PeriodType.YEAR, orgId, null, null, null, null);
        verify(jdbcTemplate, atLeast(3)).query(anyString(), any(MapSqlParameterSource.class), any(RowMapper.class));
    }

    @Test
    void getReportByEmployee_WithAllParameters_ShouldReturnGroupedData() {
        // Given
        Integer employeeId = 100;
        Integer skillId = 5;
        PeriodType periodType = PeriodType.MONTH;

        List<EmployeeReportingStatsDto> mockRows = List.of(
                new EmployeeReportingStatsDto(employeeId, "John", "Doe", "Java", LocalDate.of(2026, 1, 1), 4.5, 3.0, 5.0),
                new EmployeeReportingStatsDto(employeeId, "John", "Doe", "Java", LocalDate.of(2026, 2, 1), 4.0, 3.5, 4.5),
                new EmployeeReportingStatsDto(employeeId, "John", "Doe", "Python", LocalDate.of(2026, 1, 1), 3.5, 2.0, 4.0)
        );

        when(jdbcTemplate.query(anyString(), any(MapSqlParameterSource.class), any(RowMapper.class))).thenReturn(mockRows);

        // When
        EmployeeReportingResponseDto result = reportingRepository.getReportByEmployee(
                periodType, employeeId, skillId, startDate, endDate);

        // Then
        assertNotNull(result);
        assertEquals(employeeId, result.employeeId());
        assertEquals("John", result.firstName());
        assertEquals("Doe", result.lastName());
        assertEquals(2, result.skills().size());

        // Verify Java skill has 2 periods
        EmployeeReportingSkillDto javaSkill = result.skills().stream()
                .filter(s -> s.skillName().equals("Java"))
                .findFirst()
                .orElseThrow();
        assertEquals(2, javaSkill.periods().size());

        // Verify parameters
        ArgumentCaptor<MapSqlParameterSource> paramsCaptor = ArgumentCaptor.forClass(MapSqlParameterSource.class);
        verify(jdbcTemplate).query(anyString(), paramsCaptor.capture(), any(RowMapper.class));
        MapSqlParameterSource params = paramsCaptor.getValue();
        assertEquals(employeeId, params.getValue("employeeId"));
        assertEquals(skillId, params.getValue("skillId"));
        assertEquals(startDate, params.getValue("startDate"));
        assertEquals(endDate, params.getValue("endDate"));
    }

    @Test
    void getReportByEmployee_WithoutOptionalParameters_ShouldBuildCorrectSql() {
        // Given
        Integer employeeId = 100;
        PeriodType periodType = PeriodType.QUARTER;

        List<EmployeeReportingStatsDto> mockRows = List.of(
                new EmployeeReportingStatsDto(employeeId, "John", "Doe", "Java", LocalDate.of(2026, 1, 1), 4.5, 3.0, 5.0)
        );

        when(jdbcTemplate.query(anyString(), any(MapSqlParameterSource.class), any(RowMapper.class))).thenReturn(mockRows);

        // When
        EmployeeReportingResponseDto result = reportingRepository.getReportByEmployee(
                periodType, employeeId, null, null, null);

        // Then
        assertNotNull(result);

        // Verify SQL doesn't contain skill filter
        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(jdbcTemplate).query(sqlCaptor.capture(), any(MapSqlParameterSource.class), any(RowMapper.class));
        String sql = sqlCaptor.getValue();
        assertFalse(sql.contains("skill_id = :skillId"));

        // Verify parameters
        ArgumentCaptor<MapSqlParameterSource> paramsCaptor = ArgumentCaptor.forClass(MapSqlParameterSource.class);
        verify(jdbcTemplate).query(anyString(), paramsCaptor.capture(), any(RowMapper.class));
        MapSqlParameterSource params = paramsCaptor.getValue();
        assertEquals(employeeId, params.getValue("employeeId"));
        assertFalse(params.hasValue("skillId"));
        assertFalse(params.hasValue("startDate"));
        assertFalse(params.hasValue("endDate"));
    }

    @Test
    void getReportByEmployee_WithNullPeriodType_ShouldDefaultToQuarter() {
        // Given
        Integer employeeId = 100;
        List<EmployeeReportingStatsDto> mockRows = List.of(
                new EmployeeReportingStatsDto(employeeId, "John", "Doe", "Java", LocalDate.of(2026, 1, 1), 4.5, 3.0, 5.0)
        );

        when(jdbcTemplate.query(anyString(), any(MapSqlParameterSource.class), any(RowMapper.class))).thenReturn(mockRows);

        // When
        EmployeeReportingResponseDto result = reportingRepository.getReportByEmployee(
                null, employeeId, null, null, null);

        // Then
        assertNotNull(result);

        // Verify SQL uses QUARTER period expression
        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(jdbcTemplate).query(sqlCaptor.capture(), any(MapSqlParameterSource.class), any(RowMapper.class));
        String sql = sqlCaptor.getValue();
        assertTrue(sql.contains("date_trunc('quarter', entry_date)::date"));
    }

    @Test
    void getReportByEmployee_WithEmptyResults_ShouldReturnNull() {
        // Given
        Integer employeeId = 100;
        when(jdbcTemplate.query(anyString(), any(MapSqlParameterSource.class), any(RowMapper.class))).thenReturn(new ArrayList<>());

        // When
        EmployeeReportingResponseDto result = reportingRepository.getReportByEmployee(
                PeriodType.MONTH, employeeId, null, null, null);

        // Then
        assertNull(result);
    }

    @Test
    void getSkillTimelineByEmployee_WithAllParameters_ShouldReturnGroupedData() {
        // Given
        Integer employeeId = 100;
        Integer skillId = 5;

        List<EmployeeSkillTimelineRowDto> mockRows = List.of(
                new EmployeeSkillTimelineRowDto(employeeId, "John", "Doe", 5, "Java", LocalDate.of(2026, 1, 1), 4.5, 3.0, 5.0, 4.2),
                new EmployeeSkillTimelineRowDto(employeeId, "John", "Doe", 5, "Java", LocalDate.of(2026, 1, 15), 4.0, 3.0, 5.0, 4.2),
                new EmployeeSkillTimelineRowDto(employeeId, "John", "Doe", 6, "Python", LocalDate.of(2026, 1, 1), 3.5, 2.0, 4.0, 3.2)
        );

        when(jdbcTemplate.query(anyString(), any(MapSqlParameterSource.class), any(RowMapper.class))).thenReturn(mockRows);

        // When
        EmployeeSkillTimelineResponseDto result = reportingRepository.getSkillTimelineByEmployee(
                employeeId, skillId, startDate, endDate);

        // Then
        assertNotNull(result);
        assertEquals(employeeId, result.employeeId());
        assertEquals("John", result.firstName());
        assertEquals("Doe", result.lastName());
        assertEquals(2, result.skills().size());

        // Verify Java skill has 2 timeline points
        EmployeeSkillTimelineSkillDto javaSkill = result.skills().stream()
                .filter(s -> s.skillName().equals("Java"))
                .findFirst()
                .orElseThrow();
        assertEquals(2, javaSkill.timeline().size());
        assertEquals(5, javaSkill.skillId());
        assertEquals(3.0, javaSkill.minRating());
        assertEquals(5.0, javaSkill.maxRating());
        assertEquals(4.2, javaSkill.avgRating());

        // Verify parameters
        ArgumentCaptor<MapSqlParameterSource> paramsCaptor = ArgumentCaptor.forClass(MapSqlParameterSource.class);
        verify(jdbcTemplate).query(anyString(), paramsCaptor.capture(), any(RowMapper.class));
        MapSqlParameterSource params = paramsCaptor.getValue();
        assertEquals(employeeId, params.getValue("employeeId"));
        assertEquals(skillId, params.getValue("skillId"));
        assertEquals(startDate, params.getValue("startDate"));
        assertEquals(endDate, params.getValue("endDate"));
    }

    @Test
    void getSkillTimelineByEmployee_WithoutOptionalParameters_ShouldBuildCorrectSql() {
        // Given
        Integer employeeId = 100;

        List<EmployeeSkillTimelineRowDto> mockRows = List.of(
                new EmployeeSkillTimelineRowDto(employeeId, "John", "Doe", 5, "Java", LocalDate.of(2026, 1, 1), 4.5, 3.0, 5.0, 4.2)
        );

        when(jdbcTemplate.query(anyString(), any(MapSqlParameterSource.class), any(RowMapper.class))).thenReturn(mockRows);

        // When
        EmployeeSkillTimelineResponseDto result = reportingRepository.getSkillTimelineByEmployee(
                employeeId, null, null, null);

        // Then
        assertNotNull(result);

        // Verify SQL doesn't contain skill filter or date filters
        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(jdbcTemplate).query(sqlCaptor.capture(), any(MapSqlParameterSource.class), any(RowMapper.class));
        String sql = sqlCaptor.getValue();
        assertFalse(sql.contains("skill_id = :skillId"));
        assertFalse(sql.contains("entry_date >= :startDate"));
        assertFalse(sql.contains("entry_date <= :endDate"));

        // Verify parameters
        ArgumentCaptor<MapSqlParameterSource> paramsCaptor = ArgumentCaptor.forClass(MapSqlParameterSource.class);
        verify(jdbcTemplate).query(anyString(), paramsCaptor.capture(), any(RowMapper.class));
        MapSqlParameterSource params = paramsCaptor.getValue();
        assertEquals(employeeId, params.getValue("employeeId"));
        assertFalse(params.hasValue("skillId"));
        assertFalse(params.hasValue("startDate"));
        assertFalse(params.hasValue("endDate"));
    }

    @Test
    void getSkillTimelineByEmployee_WithEmptyResults_ShouldReturnNull() {
        // Given
        Integer employeeId = 100;
        when(jdbcTemplate.query(anyString(), any(MapSqlParameterSource.class), any(RowMapper.class))).thenReturn(new ArrayList<>());

        // When
        EmployeeSkillTimelineResponseDto result = reportingRepository.getSkillTimelineByEmployee(
                employeeId, null, null, null);

        // Then
        assertNull(result);
    }

    @Test
    void getSkillTimelineByOrganizationAndDepartment_WithAllParameters_ShouldReturnGroupedData() {
        // Given
        Integer orgId = 1;
        Integer deptId = 10;
        Integer skillId = 5;

        List<OrgDeptSkillTimelineRowDto> mockRows = List.of(
                new OrgDeptSkillTimelineRowDto(orgId, "Org1", deptId, "Dept1", 5, "Java", LocalDate.of(2026, 1, 1), 3.0, 5.0, 4.2),
                new OrgDeptSkillTimelineRowDto(orgId, "Org1", deptId, "Dept1", 5, "Java", LocalDate.of(2026, 1, 15), 3.5, 4.5, 4.0),
                new OrgDeptSkillTimelineRowDto(orgId, "Org1", deptId, "Dept1", 6, "Python", LocalDate.of(2026, 1, 1), 2.0, 4.0, 3.2)
        );

        when(jdbcTemplate.query(anyString(), any(MapSqlParameterSource.class), any(RowMapper.class))).thenReturn(mockRows);

        // When
        OrgDeptSkillTimelineResponseDto result = reportingRepository.getSkillTimelineByOrganizationAndDepartment(
                orgId, deptId, skillId, startDate, endDate);

        // Then
        assertNotNull(result);
        assertEquals(orgId, result.organizationId());
        assertEquals("Org1", result.organizationName());
        assertEquals(deptId, result.departmentId());
        assertEquals("Dept1", result.departmentName());
        assertEquals(2, result.skills().size());

        // Verify Java skill has 2 timeline points
        OrgDeptSkillTimelineSkillDto javaSkill = result.skills().stream()
                .filter(s -> s.skillName().equals("Java"))
                .findFirst()
                .orElseThrow();
        assertEquals(2, javaSkill.timeline().size());
        assertEquals(5, javaSkill.skillId());

        // Verify parameters
        ArgumentCaptor<MapSqlParameterSource> paramsCaptor = ArgumentCaptor.forClass(MapSqlParameterSource.class);
        verify(jdbcTemplate).query(anyString(), paramsCaptor.capture(), any(RowMapper.class));
        MapSqlParameterSource params = paramsCaptor.getValue();
        assertEquals(orgId, params.getValue("orgId"));
        assertEquals(deptId, params.getValue("deptId"));
        assertEquals(skillId, params.getValue("skillId"));
        assertEquals(startDate, params.getValue("startDate"));
        assertEquals(endDate, params.getValue("endDate"));
    }

    @Test
    void getSkillTimelineByOrganizationAndDepartment_WithoutOptionalParameters_ShouldBuildCorrectSql() {
        // Given
        Integer orgId = 1;

        List<OrgDeptSkillTimelineRowDto> mockRows = List.of(
                new OrgDeptSkillTimelineRowDto(orgId, "Org1", 10, "Dept1", 5, "Java", LocalDate.of(2026, 1, 1), 3.0, 5.0, 4.2)
        );

        when(jdbcTemplate.query(anyString(), any(MapSqlParameterSource.class), any(RowMapper.class))).thenReturn(mockRows);

        // When
        OrgDeptSkillTimelineResponseDto result = reportingRepository.getSkillTimelineByOrganizationAndDepartment(
                orgId, null, null, null, null);

        // Then
        assertNotNull(result);
        assertEquals(orgId, result.organizationId());
        assertNull(result.departmentId());
        assertNull(result.departmentName());

        // Verify SQL doesn't contain department or skill filters
        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(jdbcTemplate).query(sqlCaptor.capture(), any(MapSqlParameterSource.class), any(RowMapper.class));
        String sql = sqlCaptor.getValue();
        assertFalse(sql.contains("department_id = :deptId"));
        assertFalse(sql.contains("skill_id = :skillId"));

        // Verify parameters
        ArgumentCaptor<MapSqlParameterSource> paramsCaptor = ArgumentCaptor.forClass(MapSqlParameterSource.class);
        verify(jdbcTemplate).query(anyString(), paramsCaptor.capture(), any(RowMapper.class));
        MapSqlParameterSource params = paramsCaptor.getValue();
        assertEquals(orgId, params.getValue("orgId"));
        assertFalse(params.hasValue("deptId"));
        assertFalse(params.hasValue("skillId"));
        assertFalse(params.hasValue("startDate"));
        assertFalse(params.hasValue("endDate"));
    }

    @Test
    void getSkillTimelineByOrganizationAndDepartment_WithEmptyResults_ShouldReturnNull() {
        // Given
        Integer orgId = 1;
        when(jdbcTemplate.query(anyString(), any(MapSqlParameterSource.class), any(RowMapper.class))).thenReturn(new ArrayList<>());

        // When
        OrgDeptSkillTimelineResponseDto result = reportingRepository.getSkillTimelineByOrganizationAndDepartment(
                orgId, null, null, null, null);

        // Then
        assertNull(result);
    }

    @Test
    void getSkillTimelineByOrganizationAndDepartment_WithOnlyStartDate_ShouldIncludeStartDateFilter() {
        // Given
        Integer orgId = 1;
        List<OrgDeptSkillTimelineRowDto> mockRows = List.of(
                new OrgDeptSkillTimelineRowDto(orgId, "Org1", 10, "Dept1", 5, "Java", LocalDate.of(2026, 1, 1), 3.0, 5.0, 4.2)
        );

        when(jdbcTemplate.query(anyString(), any(MapSqlParameterSource.class), any(RowMapper.class))).thenReturn(mockRows);

        // When
        reportingRepository.getSkillTimelineByOrganizationAndDepartment(orgId, null, null, startDate, null);

        // Then
        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(jdbcTemplate).query(sqlCaptor.capture(), any(MapSqlParameterSource.class), any(RowMapper.class));
        String sql = sqlCaptor.getValue();
        assertTrue(sql.contains("entry_date::date >= :startDate"));
        assertFalse(sql.contains("entry_date::date <= :endDate"));

        ArgumentCaptor<MapSqlParameterSource> paramsCaptor = ArgumentCaptor.forClass(MapSqlParameterSource.class);
        verify(jdbcTemplate).query(anyString(), paramsCaptor.capture(), any(RowMapper.class));
        MapSqlParameterSource params = paramsCaptor.getValue();
        assertEquals(startDate, params.getValue("startDate"));
        assertFalse(params.hasValue("endDate"));
    }

    @Test
    void getSkillTimelineByOrganizationAndDepartment_WithOnlyEndDate_ShouldIncludeEndDateFilter() {
        // Given
        Integer orgId = 1;
        List<OrgDeptSkillTimelineRowDto> mockRows = List.of(
                new OrgDeptSkillTimelineRowDto(orgId, "Org1", 10, "Dept1", 5, "Java", LocalDate.of(2026, 1, 1), 3.0, 5.0, 4.2)
        );

        when(jdbcTemplate.query(anyString(), any(MapSqlParameterSource.class), any(RowMapper.class))).thenReturn(mockRows);

        // When
        reportingRepository.getSkillTimelineByOrganizationAndDepartment(orgId, null, null, null, endDate);

        // Then
        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(jdbcTemplate).query(sqlCaptor.capture(), any(MapSqlParameterSource.class), any(RowMapper.class));
        String sql = sqlCaptor.getValue();
        assertFalse(sql.contains("entry_date::date >= :startDate"));
        assertTrue(sql.contains("entry_date::date <= :endDate"));

        ArgumentCaptor<MapSqlParameterSource> paramsCaptor = ArgumentCaptor.forClass(MapSqlParameterSource.class);
        verify(jdbcTemplate).query(anyString(), paramsCaptor.capture(), any(RowMapper.class));
        MapSqlParameterSource params = paramsCaptor.getValue();
        assertFalse(params.hasValue("startDate"));
        assertEquals(endDate, params.getValue("endDate"));
    }

    @Test
    void getReportByOrganizationAndDepartment_WithOnlyStartDate_ShouldIncludeStartDateFilter() {
        // Given
        Integer orgId = 1;
        List<OrgDeptReportingStatsDto> mockRows = List.of(
                new OrgDeptReportingStatsDto("Org1", "Dept1", "Java", LocalDate.of(2026, 1, 1), 4.5, 3.0, 5.0, 10L, 5L)
        );

        when(jdbcTemplate.query(anyString(), any(MapSqlParameterSource.class), any(RowMapper.class))).thenReturn(mockRows);

        // When
        reportingRepository.getReportByOrganizationAndDepartment(PeriodType.MONTH, orgId, null, null, startDate, null);

        // Then
        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(jdbcTemplate).query(sqlCaptor.capture(), any(MapSqlParameterSource.class), any(RowMapper.class));
        String sql = sqlCaptor.getValue();
        assertTrue(sql.contains("entry_date::date >= :startDate"));
        assertFalse(sql.contains("entry_date::date <= :endDate"));

        ArgumentCaptor<MapSqlParameterSource> paramsCaptor = ArgumentCaptor.forClass(MapSqlParameterSource.class);
        verify(jdbcTemplate).query(anyString(), paramsCaptor.capture(), any(RowMapper.class));
        MapSqlParameterSource params = paramsCaptor.getValue();
        assertEquals(startDate, params.getValue("startDate"));
        assertFalse(params.hasValue("endDate"));
    }

    @Test
    void getReportByOrganizationAndDepartment_WithOnlyEndDate_ShouldIncludeEndDateFilter() {
        // Given
        Integer orgId = 1;
        List<OrgDeptReportingStatsDto> mockRows = List.of(
                new OrgDeptReportingStatsDto("Org1", "Dept1", "Java", LocalDate.of(2026, 1, 1), 4.5, 3.0, 5.0, 10L, 5L)
        );

        when(jdbcTemplate.query(anyString(), any(MapSqlParameterSource.class), any(RowMapper.class))).thenReturn(mockRows);

        // When
        reportingRepository.getReportByOrganizationAndDepartment(PeriodType.MONTH, orgId, null, null, null, endDate);

        // Then
        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(jdbcTemplate).query(sqlCaptor.capture(), any(MapSqlParameterSource.class), any(RowMapper.class));
        String sql = sqlCaptor.getValue();
        assertFalse(sql.contains("entry_date::date >= :startDate"));
        assertTrue(sql.contains("entry_date::date <= :endDate"));

        ArgumentCaptor<MapSqlParameterSource> paramsCaptor = ArgumentCaptor.forClass(MapSqlParameterSource.class);
        verify(jdbcTemplate).query(anyString(), paramsCaptor.capture(), any(RowMapper.class));
        MapSqlParameterSource params = paramsCaptor.getValue();
        assertFalse(params.hasValue("startDate"));
        assertEquals(endDate, params.getValue("endDate"));
    }

    @Test
    void getReportByEmployee_WithOnlyStartDate_ShouldIncludeStartDateFilter() {
        // Given
        Integer employeeId = 100;
        List<EmployeeReportingStatsDto> mockRows = List.of(
                new EmployeeReportingStatsDto(employeeId, "John", "Doe", "Java", LocalDate.of(2026, 1, 1), 4.5, 3.0, 5.0)
        );

        when(jdbcTemplate.query(anyString(), any(MapSqlParameterSource.class), any(RowMapper.class))).thenReturn(mockRows);

        // When
        reportingRepository.getReportByEmployee(PeriodType.MONTH, employeeId, null, startDate, null);

        // Then
        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(jdbcTemplate).query(sqlCaptor.capture(), any(MapSqlParameterSource.class), any(RowMapper.class));
        String sql = sqlCaptor.getValue();
        assertTrue(sql.contains("entry_date::date >= :startDate"));
        assertFalse(sql.contains("entry_date::date <= :endDate"));

        ArgumentCaptor<MapSqlParameterSource> paramsCaptor = ArgumentCaptor.forClass(MapSqlParameterSource.class);
        verify(jdbcTemplate).query(anyString(), paramsCaptor.capture(), any(RowMapper.class));
        MapSqlParameterSource params = paramsCaptor.getValue();
        assertEquals(startDate, params.getValue("startDate"));
        assertFalse(params.hasValue("endDate"));
    }

    @Test
    void getSkillTimelineByEmployee_WithOnlyStartDate_ShouldIncludeStartDateFilter() {
        // Given
        Integer employeeId = 100;
        List<EmployeeSkillTimelineRowDto> mockRows = List.of(
                new EmployeeSkillTimelineRowDto(employeeId, "John", "Doe", 5, "Java", LocalDate.of(2026, 1, 1), 4.5, 3.0, 5.0, 4.2)
        );

        when(jdbcTemplate.query(anyString(), any(MapSqlParameterSource.class), any(RowMapper.class))).thenReturn(mockRows);

        // When
        reportingRepository.getSkillTimelineByEmployee(employeeId, null, startDate, null);

        // Then
        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(jdbcTemplate).query(sqlCaptor.capture(), any(MapSqlParameterSource.class), any(RowMapper.class));
        String sql = sqlCaptor.getValue();
        assertTrue(sql.contains("entry_date >= :startDate"));
        assertFalse(sql.contains("entry_date <= :endDate"));

        ArgumentCaptor<MapSqlParameterSource> paramsCaptor = ArgumentCaptor.forClass(MapSqlParameterSource.class);
        verify(jdbcTemplate).query(anyString(), paramsCaptor.capture(), any(RowMapper.class));
        MapSqlParameterSource params = paramsCaptor.getValue();
        assertEquals(startDate, params.getValue("startDate"));
        assertFalse(params.hasValue("endDate"));
    }

    @Test
    void getSkillTimelineByEmployee_WithOnlyEndDate_ShouldIncludeEndDateFilter() {
        // Given
        Integer employeeId = 100;
        List<EmployeeSkillTimelineRowDto> mockRows = List.of(
                new EmployeeSkillTimelineRowDto(employeeId, "John", "Doe", 5, "Java", LocalDate.of(2026, 1, 1), 4.5, 3.0, 5.0, 4.2)
        );

        when(jdbcTemplate.query(anyString(), any(MapSqlParameterSource.class), any(RowMapper.class))).thenReturn(mockRows);

        // When
        reportingRepository.getSkillTimelineByEmployee(employeeId, null, null, endDate);

        // Then
        ArgumentCaptor<String> sqlCaptor = ArgumentCaptor.forClass(String.class);
        verify(jdbcTemplate).query(sqlCaptor.capture(), any(MapSqlParameterSource.class), any(RowMapper.class));
        String sql = sqlCaptor.getValue();
        assertFalse(sql.contains("entry_date >= :startDate"));
        assertTrue(sql.contains("entry_date <= :endDate"));

        ArgumentCaptor<MapSqlParameterSource> paramsCaptor = ArgumentCaptor.forClass(MapSqlParameterSource.class);
        verify(jdbcTemplate).query(anyString(), paramsCaptor.capture(), any(RowMapper.class));
        MapSqlParameterSource params = paramsCaptor.getValue();
        assertFalse(params.hasValue("startDate"));
        assertEquals(endDate, params.getValue("endDate"));
    }
}

