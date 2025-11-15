package gr.uom.employeepulseservice.repository.impl;

import gr.uom.employeepulseservice.controller.dto.reportingDto.*;
import gr.uom.employeepulseservice.model.PeriodType;
import gr.uom.employeepulseservice.repository.ReportingRepository;
import lombok.RequiredArgsConstructor;
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
    private final NamedParameterJdbcTemplate jdbc;

    private String periodStartExpression(PeriodType periodType) {
        return switch (periodType) {
            case DAY -> "date_trunc('day', entry_date)::date";
            case WEEK -> "date_trunc('week', entry_date)::date";
            case MONTH -> "date_trunc('month', entry_date)::date";
            case QUARTER -> "date_trunc('quarter', entry_date)::date";
            case YEAR -> "date_trunc('year', entry_date)::date";
        };
    }

    private String periodValuePredicate(PeriodType periodType, Integer periodValue) {
        if (periodValue == null) return "TRUE";

        return switch (periodType) {
            case DAY -> "EXTRACT(day FROM entry_date)::int = :periodValue";
            case WEEK -> "EXTRACT(week FROM entry_date)::int = :periodValue";
            case MONTH -> "EXTRACT(month FROM entry_date)::int = :periodValue";
            case QUARTER -> "EXTRACT(quarter FROM entry_date)::int = :periodValue";
            case YEAR -> "EXTRACT(year FROM entry_date)::int = :periodValue";
        };
    }

    private String yearPredicate(Integer year) {
        return (year == null) ? "TRUE" : "EXTRACT(year FROM entry_date)::int = :year";
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrgDeptReportingStatsDto> getReportByOrganizationAndDepartment(PeriodType periodType,
                                                                               Integer organizationId,
                                                                               Integer departmentId,
                                                                               Integer periodValue,
                                                                               Integer year) {
        periodType = periodType == null ? PeriodType.QUARTER : periodType;

        String periodStart = periodStartExpression(periodType);
        String periodValueWhere = periodValuePredicate(periodType, periodValue);
        String yearWhere = yearPredicate(year);

        String sql = """
                    SELECT
                        organization_name,
                        department_name,
                        skill_name,
                        %s AS period_start,
                        avg(rating) AS avg_rating,
                        min(rating) AS min_rating,
                        max(rating) AS max_rating,
                        count(*)    AS sample_count
                    FROM v_org_department_skill_period
                    WHERE organization_id = :orgId
                      AND department_id   = :deptId
                      AND %s                -- period value
                      AND %s                -- year filter
                    GROUP BY organization_name, department_name, skill_name, period_start
                    ORDER BY period_start DESC;
                """.formatted(periodStart, periodValueWhere, yearWhere);

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("orgId", organizationId)
                .addValue("deptId", departmentId);

        if (periodValue != null) params.addValue("periodValue", periodValue);
        if (year != null) params.addValue("year", year);

        return jdbc.query(sql, params, (rs, rn) -> new OrgDeptReportingStatsDto(
                rs.getString("organization_name"),
                rs.getString("department_name"),
                rs.getString("skill_name"),
                rs.getObject("period_start", LocalDate.class),
                rs.getDouble("avg_rating"),
                rs.getDouble("min_rating"),
                rs.getDouble("max_rating"),
                rs.getLong("sample_count")
        ));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeReportingStatsDto> getReportByEmployee(PeriodType periodType,
                                                               Integer employeeId,
                                                               Integer periodValue,
                                                               Integer year) {
        periodType = periodType == null ? PeriodType.MONTH : periodType;

        String periodStart = periodStartExpression(periodType);
        String periodValueWhere = periodValuePredicate(periodType, periodValue);
        String yearWhere = yearPredicate(year);

        String sql = """
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
                      AND %s            -- period value
                      AND %s            -- year filter
                    GROUP BY employee_id, first_name, last_name, skill_name, period_start
                    ORDER BY period_start DESC;
                """.formatted(periodStart, periodValueWhere, yearWhere);

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("employeeId", employeeId);

        if (periodValue != null) params.addValue("periodValue", periodValue);
        if (year != null) params.addValue("year", year);

        return jdbc.query(sql, params, (rs, rn) -> new EmployeeReportingStatsDto(
                rs.getInt("employee_id"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("skill_name"),
                rs.getObject("period_start", LocalDate.class),
                rs.getDouble("avg_rating"),
                rs.getDouble("min_rating"),
                rs.getDouble("max_rating")
        ));
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmployeeSkillTimelineStatsDto> getSkillTimelineByEmployee(Integer employeeId, Integer skillId) {

        // Base query: fetch all skill entry rows for an employee
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

        // Dynamically apply skill filter when a specific skillId is requested
        String sql;
        if (skillId != null) {
            sql = baseSql + " AND skill_id = :skillId ORDER BY skill_name, entry_date;";
        } else {
            sql = baseSql + " ORDER BY skill_name, entry_date;";
        }

        // Prepare SQL parameters
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("employeeId", employeeId);
        if (skillId != null) params.addValue("skillId", skillId);

        // Execute the query and map each database row to a simple DTO
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

        // Group timeline rows by skill so each skill produces one aggregated result
        Map<Integer, List<EmployeeSkillTimelineRowDto>> bySkill = new LinkedHashMap<>();
        for (EmployeeSkillTimelineRowDto row : rows) {

            Integer skillIdKey = row.skillId();

            // Create the list for this skill if it doesn't exist yet
            if (!bySkill.containsKey(skillIdKey)) {
                bySkill.put(skillIdKey, new ArrayList<>());
            }

            bySkill.get(skillIdKey).add(row);
        }

        // Convert grouped rows into final response structures
        List<EmployeeSkillTimelineStatsDto> result = new ArrayList<>();

        for (List<EmployeeSkillTimelineRowDto> skillRows : bySkill.values()) {
            if (skillRows.isEmpty()) continue;

            // First row holds shared employee/skill metadata
            EmployeeSkillTimelineRowDto first = skillRows.get(0);

            // Build timeline points for this skill
            List<SkillTimelinePointDto> timeline = new ArrayList<>();
            for (EmployeeSkillTimelineRowDto row : skillRows) {
                SkillTimelinePointDto point = new SkillTimelinePointDto(
                        row.entryDate(),
                        row.rating()
                );
                timeline.add(point);
            }

            // Create the final aggregated result for this skill
            EmployeeSkillTimelineStatsDto stats = new EmployeeSkillTimelineStatsDto(
                    first.employeeId(),
                    first.firstName(),
                    first.lastName(),
                    first.skillId(),
                    first.skillName(),
                    timeline,
                    first.minRating(),
                    first.maxRating(),
                    first.avgRating()
            );

            result.add(stats);

        }

        return result;
    }


}