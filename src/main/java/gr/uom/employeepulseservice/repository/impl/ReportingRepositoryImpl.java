package gr.uom.employeepulseservice.repository.impl;

import gr.uom.employeepulseservice.controller.dto.EmployeeReportingStatsDto;
import gr.uom.employeepulseservice.controller.dto.OrgDeptReportingStatsDto;
import gr.uom.employeepulseservice.model.PeriodType;
import gr.uom.employeepulseservice.repository.ReportingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ReportingRepositoryImpl implements ReportingRepository {
    private final NamedParameterJdbcTemplate jdbc;

    private String periodStartExpression(PeriodType periodType) {
        return switch (periodType) {
            case DAY     -> "date_trunc('day', entry_date)::date";
            case WEEK    -> "date_trunc('week', entry_date)::date";
            case MONTH   -> "date_trunc('month', entry_date)::date";
            case QUARTER -> "date_trunc('quarter', entry_date)::date";
            case YEAR    -> "date_trunc('year', entry_date)::date";
        };
    }

    private String periodValuePredicate(PeriodType periodType, Integer periodValue) {
        if (periodValue == null) return "TRUE";

        return switch (periodType) {
            case DAY     -> "EXTRACT(day FROM entry_date)::int = :periodValue";
            case WEEK    -> "EXTRACT(week FROM entry_date)::int = :periodValue";
            case MONTH   -> "EXTRACT(month FROM entry_date)::int = :periodValue";
            case QUARTER -> "EXTRACT(quarter FROM entry_date)::int = :periodValue";
            case YEAR    -> "EXTRACT(year FROM entry_date)::int = :periodValue";
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

        String periodStart     = periodStartExpression(periodType);
        String periodValueWhere= periodValuePredicate(periodType, periodValue);
        String yearWhere       = yearPredicate(year);

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
        if (year != null)        params.addValue("year", year);

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

        String periodStart     = periodStartExpression(periodType);
        String periodValueWhere= periodValuePredicate(periodType, periodValue);
        String yearWhere       = yearPredicate(year);

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
        if (year != null)        params.addValue("year", year);

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
}