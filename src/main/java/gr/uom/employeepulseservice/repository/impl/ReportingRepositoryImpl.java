package gr.uom.employeepulseservice.repository.impl;

import gr.uom.employeepulseservice.controller.dto.reportingDto.employee.*;
import gr.uom.employeepulseservice.controller.dto.reportingDto.orgdept.*;
import gr.uom.employeepulseservice.model.PeriodType;
import gr.uom.employeepulseservice.repository.ReportingRepository;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class ReportingRepositoryImpl implements ReportingRepository {

    // JDBC template for executing parameterized SQL queries
    private final NamedParameterJdbcTemplate jdbc;

    // Returns SQL expression that calculates the start date of the given period type
    private String periodStartExpression(PeriodType periodType) {
        return switch (periodType) {
            case DAY -> "date_trunc('day', entry_date)::date";
            case WEEK -> "date_trunc('week', entry_date)::date";
            case MONTH -> "date_trunc('month', entry_date)::date";
            case QUARTER -> "date_trunc('quarter', entry_date)::date";
            case YEAR -> "date_trunc('year', entry_date)::date";
        };
    }

    // Returns SQL predicate for filtering by a specific period value (e.g. month = 5)
    private String periodValuePredicate(PeriodType periodType, Integer periodValue) {
        if (periodValue == null) return "TRUE"; // no filter when period value is not provided

        return switch (periodType) {
            case DAY -> "EXTRACT(day FROM entry_date)::int = :periodValue";
            case WEEK -> "EXTRACT(week FROM entry_date)::int = :periodValue";
            case MONTH -> "EXTRACT(month FROM entry_date)::int = :periodValue";
            case QUARTER -> "EXTRACT(quarter FROM entry_date)::int = :periodValue";
            case YEAR -> "EXTRACT(year FROM entry_date)::int = :periodValue";
        };
    }

    // Returns SQL predicate for filtering by year
    private String yearPredicate(Integer year) {
        return (year == null) ? "TRUE" : "EXTRACT(year FROM entry_date)::int = :year ";
    }

    @Override
    @Transactional(readOnly = true)
    public OrgDeptReportingResponseDto getReportByOrganizationAndDepartment(
            PeriodType periodType,
            Integer organizationId,
            Integer departmentId,
            Integer skillId,
            Integer periodValue,
            Integer year
    ) {
        // Default period type if missing
        periodType = periodType == null ? PeriodType.QUARTER : periodType;

        String periodStart = periodStartExpression(periodType);
        String periodValueWhere = periodValuePredicate(periodType, periodValue);
        String yearWhere = yearPredicate(year);

        // Build SELECT clause - conditionally include department_name
        StringBuilder selectBuilder = new StringBuilder("""
                SELECT
                    organization_name,
                """);
        if (departmentId != null) {
            selectBuilder.append("department_name,\n");
        } else {
            selectBuilder.append("NULL AS department_name,\n");
        }
        selectBuilder.append(String.format("""
                    skill_name,
                    %s AS period_start,
                    avg(rating) AS avg_rating,
                    min(rating) AS min_rating,
                    max(rating) AS max_rating,
                    count(*)                        AS sample_count,
                    COUNT(DISTINCT employee_id)     AS employee_count
                FROM v_org_department_skill_period
                WHERE organization_id = :orgId
                """, periodStart));

        // Build SQL with conditional filters
        StringBuilder sqlBuilder = new StringBuilder(selectBuilder);

        // Add department filter only when departmentId is provided
        if (departmentId != null) {
            sqlBuilder.append(" AND department_id = :deptId");
        }

        // Add skill filter only when skillId is provided
        if (skillId != null) {
            sqlBuilder.append(" AND skill_id = :skillId");
        }

        sqlBuilder.append(" AND ").append(periodValueWhere);
        sqlBuilder.append(" AND ").append(yearWhere);
        
        // Build GROUP BY clause - conditionally include department_name
        sqlBuilder.append(" GROUP BY organization_name");
        if (departmentId != null) {
            sqlBuilder.append(", department_name");
        }
        sqlBuilder.append(", skill_name, period_start");
        sqlBuilder.append(" ORDER BY skill_name, period_start DESC;");

        String sql = sqlBuilder.toString();

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("orgId", organizationId);

        if (departmentId != null) params.addValue("deptId", departmentId);
        if (skillId != null) params.addValue("skillId", skillId);
        if (periodValue != null) params.addValue("periodValue", periodValue);
        if (year != null) params.addValue("year", year);

        // Execute SQL & map each row to a statistics DTO
        List<OrgDeptReportingStatsDto> rows = jdbc.query(sql, params, (rs, rn) ->
                new OrgDeptReportingStatsDto(
                        rs.getString("organization_name"),
                        rs.getString("department_name"),
                        rs.getString("skill_name"),
                        rs.getObject("period_start", LocalDate.class),
                        rs.getDouble("avg_rating"),
                        rs.getDouble("min_rating"),
                        rs.getDouble("max_rating"),
                        rs.getLong("sample_count"),
                        rs.getLong("employee_count")
                )
        );

        if (rows.isEmpty()) {
            return null;
        }

        // Group all rows by skillName
        Map<String, List<OrgDeptReportingStatsDto>> bySkill = new LinkedHashMap<>();
        for (OrgDeptReportingStatsDto row : rows) {
            bySkill.computeIfAbsent(row.skillName(), k -> new ArrayList<>()).add(row);
        }

        // Convert grouped rows into skill-level structures
        List<OrgDeptReportingSkillDto> skills = new ArrayList<>();

        for (Map.Entry<String, List<OrgDeptReportingStatsDto>> entry : bySkill.entrySet()) {
            String skillName = entry.getKey();
            List<OrgDeptReportingStatsDto> skillRows = entry.getValue();

            // Convert each flat row into a lean period DTO
            List<OrgDeptReportingPeriodDto> periods = new ArrayList<>();
            for (OrgDeptReportingStatsDto r : skillRows) {
                periods.add(new OrgDeptReportingPeriodDto(
                        r.periodStart(),
                        r.avgRating(),
                        r.minRating(),
                        r.maxRating(),
                        r.sampleCount(),
                        r.employeeCount()
                ));
            }

            skills.add(new OrgDeptReportingSkillDto(skillName, periods));
        }

        // Extract parent org/dept info from first row
        OrgDeptReportingStatsDto first = rows.getFirst();

        return new OrgDeptReportingResponseDto(
                organizationId,
                first.organizationName(),
                departmentId,
                (departmentId != null) ? first.departmentName() : null,
                skills
        );
    }

    @NotNull
    private String constructSqlStatement(PeriodType periodType, Integer periodValue, Integer year, String x) {
        String periodStart = periodStartExpression(periodType);
        // SQL predicate for filtering by period number
        String periodValueWhere = periodValuePredicate(periodType, periodValue);
        // SQL predicate for filtering by year
        String yearWhere = yearPredicate(year);

        // Query that returns a flat list: org/dept + skill + period stats
        return x.formatted(periodStart, periodValueWhere, yearWhere);
    }

    @Override
    @Transactional(readOnly = true)
    public EmployeeReportingResponseDto getReportByEmployee(
            PeriodType periodType,
            Integer employeeId,
            Integer periodValue,
            Integer year
    ) {
        // Default period type if missing
        periodType = periodType == null ? PeriodType.MONTH : periodType;

        // SQL expression for period grouping (month, quarter, etc.)
        String sql = constructSqlStatement(periodType, periodValue, year, """
                SELECT
                    employee_id,
                    first_name,
                    last_name,
                    skill_name,
                    %s AS period_start,
                    avg(rating) AS avg_rating,
                    min(rating) AS min_rating,
                    max(rating) AS max_rating
                FROM v_employee_skill_period
                WHERE employee_id = :employeeId
                  AND %s          -- period filter
                  AND %s          -- year filter
                GROUP BY employee_id, first_name, last_name, skill_name, period_start
                ORDER BY skill_name, period_start DESC;
                """);

        // Mandatory employee parameter
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("employeeId", employeeId);

        // Optional parameters
        if (periodValue != null) params.addValue("periodValue", periodValue);
        if (year != null) params.addValue("year", year);

        // Execute SQL & map each row to a statistics DTO
        List<EmployeeReportingStatsDto> rows = jdbc.query(sql, params, (rs, rn) ->
                new EmployeeReportingStatsDto(
                        rs.getInt("employee_id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("skill_name"),
                        rs.getObject("period_start", LocalDate.class),
                        rs.getDouble("avg_rating"),
                        rs.getDouble("min_rating"),
                        rs.getDouble("max_rating")
                )
        );

        // No results found
        if (rows.isEmpty()) {
            return null;
        }

        // Group all rows by skillName
        Map<String, List<EmployeeReportingStatsDto>> bySkill = new LinkedHashMap<>();
        for (EmployeeReportingStatsDto row : rows) {
            bySkill.computeIfAbsent(row.skillName(), k -> new ArrayList<>()).add(row);
        }

        // Convert grouped rows into skill-level structures
        List<EmployeeReportingSkillDto> skills = new ArrayList<>();

        for (Map.Entry<String, List<EmployeeReportingStatsDto>> entry : bySkill.entrySet()) {
            String skillName = entry.getKey();
            List<EmployeeReportingStatsDto> skillRows = entry.getValue();

            // Convert each flat row into a lean period DTO
            List<EmployeeReportingPeriodDto> periods = new ArrayList<>();
            for (EmployeeReportingStatsDto r : skillRows) {
                periods.add(new EmployeeReportingPeriodDto(
                        r.periodStart(),
                        r.avgRating(),
                        r.minRating(),
                        r.maxRating()
                ));
            }

            // Add final skill entry
            skills.add(new EmployeeReportingSkillDto(skillName, periods));
        }

        // Extract parent employee info from first row
        EmployeeReportingStatsDto first = rows.getFirst();

        // Build the final parent DTO
        return new EmployeeReportingResponseDto(
                employeeId,
                first.firstName(),
                first.lastName(),
                skills
        );
    }


    @Override
    @Transactional(readOnly = true)
    public EmployeeSkillTimelineResponseDto getSkillTimelineByEmployee(Integer employeeId, Integer skillId) {

        // Base SQL selecting all skill entries for the employee with window-based min/max/avg
        String baseSql = """
                SELECT
                    employee_id,
                    first_name,
                    last_name,
                    skill_id,
                    skill_name,
                    entry_date,
                    rating,
                    MIN(rating) OVER (PARTITION BY employee_id, skill_id) AS min_rating,
                    MAX(rating) OVER (PARTITION BY employee_id, skill_id) AS max_rating,
                    AVG(rating) OVER (PARTITION BY employee_id, skill_id) AS avg_rating
                FROM v_employee_skill_period
                WHERE employee_id = :employeeId
                """;

        // Add skill filter only when a specific skillId is requested
        String sql = (skillId != null)
                ? baseSql + " AND skill_id = :skillId ORDER BY skill_name, entry_date"
                : baseSql + " ORDER BY skill_name, entry_date";

        // Set mandatory employeeId parameter
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("employeeId", employeeId);
        // Optionally set skillId parameter
        if (skillId != null) params.addValue("skillId", skillId);

        // Execute query and map each row to a flat timeline row DTO
        List<EmployeeSkillTimelineRowDto> rows = jdbc.query(sql, params, (rs, rn) ->
                new EmployeeSkillTimelineRowDto(
                        rs.getInt("employee_id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getInt("skill_id"),
                        rs.getString("skill_name"),
                        rs.getObject("entry_date", LocalDate.class),
                        rs.getDouble("rating"),
                        rs.getDouble("min_rating"),
                        rs.getDouble("max_rating"),
                        rs.getDouble("avg_rating")
                )
        );

        // When no data exists return null (or could return an empty response)
        if (rows.isEmpty()) {
            return null;
        }

        // Group all rows by skillId
        Map<Integer, List<EmployeeSkillTimelineRowDto>> bySkill = new LinkedHashMap<>();
        for (EmployeeSkillTimelineRowDto row : rows) {
            bySkill.computeIfAbsent(row.skillId(), k -> new ArrayList<>()).add(row);
        }

        // Use the first row to extract common employee info
        EmployeeSkillTimelineRowDto first = rows.getFirst();
        List<EmployeeSkillTimelineSkillDto> skills = new ArrayList<>();

        // Convert each group (per skill) into a skill DTO with its timeline
        for (List<EmployeeSkillTimelineRowDto> skillRows : bySkill.values()) {
            EmployeeSkillTimelineRowDto srFirst = skillRows.getFirst();

            // Build the ordered timeline of points for that skill
            List<EmployeeSkillTimelinePointDto> timeline = new ArrayList<>();
            for (EmployeeSkillTimelineRowDto row : skillRows) {
                timeline.add(new EmployeeSkillTimelinePointDto(
                        row.entryDate(),
                        row.rating()
                ));
            }

            // Add aggregated skill-level timeline to the list
            skills.add(new EmployeeSkillTimelineSkillDto(
                    srFirst.skillId(),
                    srFirst.skillName(),
                    timeline,
                    srFirst.minRating(),
                    srFirst.maxRating(),
                    srFirst.avgRating()
            ));
        }

        // Return top-level response containing employee info and all skill timelines
        return new EmployeeSkillTimelineResponseDto(
                first.employeeId(),
                first.firstName(),
                first.lastName(),
                skills
        );
    }


    @Override
    @Transactional(readOnly = true)
    public OrgDeptSkillTimelineResponseDto getSkillTimelineByOrganizationAndDepartment(
            Integer organizationId,
            Integer departmentId,
            Integer skillId,
            LocalDate startDate,
            LocalDate endDate
    ) {
        // Base SQL selecting aggregated ratings per day for org/department
        String baseSql = """
                SELECT
                    organization_id,
                    organization_name,
                    department_id,
                    department_name,
                    skill_id,
                    skill_name,
                    entry_date::date AS date,
                    MIN(rating) AS min_rating,
                    MAX(rating) AS max_rating,
                    AVG(rating) AS avg_rating
                FROM v_org_department_skill_period
                WHERE organization_id = :orgId
                """;

        StringBuilder sqlBuilder = new StringBuilder(baseSql);

        if (startDate != null) {
            sqlBuilder.append(" AND entry_date::date >= :startDate ");
        }
        if (endDate != null) {
            sqlBuilder.append(" AND entry_date::date <= :endDate ");
        }

        if (departmentId != null) {
            sqlBuilder.append(" AND department_id = :deptId ");
        }
        // Add skill filter when provided
        if (skillId != null) {
            sqlBuilder.append(" AND skill_id = :skillId ");
        }

        // Group by org/dept/skill/date and order by skill then date
        sqlBuilder.append("""
                                   GROUP BY organization_id, organization_name,
                                           department_id, department_name,
                                           skill_id, skill_name, date
                                  ORDER BY skill_name, date
                                  """);

        // Final SQL string
        String sql = sqlBuilder.toString();

        // Set mandatory organizationId parameter
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("orgId", organizationId);

        if (departmentId != null) params.addValue("deptId", departmentId);
        if (skillId != null) params.addValue("skillId", skillId);
        if (startDate != null) params.addValue("startDate", startDate);
        if (endDate != null) params.addValue("endDate", endDate);

        // Execute query and map each row to a flat org/dept timeline row DTO
        List<OrgDeptSkillTimelineRowDto> rows = jdbc.query(sql, params, (rs, rn) ->
                new OrgDeptSkillTimelineRowDto(
                        rs.getInt("organization_id"),
                        rs.getString("organization_name"),
                        rs.getInt("department_id"),
                        rs.getString("department_name"),
                        rs.getInt("skill_id"),
                        rs.getString("skill_name"),
                        rs.getObject("date", LocalDate.class),
                        rs.getDouble("min_rating"),
                        rs.getDouble("max_rating"),
                        rs.getDouble("avg_rating")
                )
        );

        // When no data exists return null (or could return an empty response)
        if (rows.isEmpty()) {
            return null;
        }

        // Group all rows by skillId
        Map<Integer, List<OrgDeptSkillTimelineRowDto>> bySkill = new LinkedHashMap<>();
        for (OrgDeptSkillTimelineRowDto row : rows) {
            bySkill.computeIfAbsent(row.skillId(), k -> new ArrayList<>()).add(row);
        }

        // Use the first row to extract org/dept info
        OrgDeptSkillTimelineRowDto first = rows.getFirst();

        // When department is not filtered, do not expose department details in the response
        Integer deptIdForResponse = (departmentId != null) ? first.departmentId() : null;
        String deptNameForResponse = (departmentId != null) ? first.departmentName() : null;

        List<OrgDeptSkillTimelineSkillDto> skills = new ArrayList<>();

        // Convert each group (per skill) into a skill DTO with its timeline
        for (List<OrgDeptSkillTimelineRowDto> skillRows : bySkill.values()) {
            OrgDeptSkillTimelineRowDto srFirst = skillRows.getFirst();

            // Build the ordered timeline of points for that skill
            List<OrgDeptSkillTimelinePointDto> timeline = new ArrayList<>();
            for (OrgDeptSkillTimelineRowDto row : skillRows) {
                timeline.add(new OrgDeptSkillTimelinePointDto(
                        row.date(),
                        row.minRating(),
                        row.maxRating(),
                        row.avgRating()
                ));
            }

            // Add skill-level timeline to the list
            skills.add(new OrgDeptSkillTimelineSkillDto(
                    srFirst.skillId(),
                    srFirst.skillName(),
                    timeline
            ));
        }

        // Return top-level response containing org/dept info and all skill timelines
        return new OrgDeptSkillTimelineResponseDto(
                first.organizationId(),
                first.organizationName(),
                deptIdForResponse,
                deptNameForResponse,
                skills
        );
    }
}
