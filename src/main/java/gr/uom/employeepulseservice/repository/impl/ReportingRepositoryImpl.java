package gr.uom.employeepulseservice.repository.impl;

import gr.uom.employeepulseservice.controller.dto.EmployeeReportingStatsDto;
import gr.uom.employeepulseservice.controller.dto.OrgDeptReportingStatsDto;
import gr.uom.employeepulseservice.repository.ReportingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class ReportingRepositoryImpl implements ReportingRepository {

    private static final Set<String> ALLOWED_GRAINS = Set.of("month", "quarter", "semester", "year");

    private final NamedParameterJdbcTemplate jdbc;

    private String normalizeGrain(String grain) {
        String normalizedGrain = (grain == null) ? "month" : grain.trim().toLowerCase();

        if (!ALLOWED_GRAINS.contains(normalizedGrain)) {
            throw new IllegalArgumentException("Unsupported grain: " + grain + ". Use month|quarter|semester|year");
        }
        return normalizedGrain;
    }

    private String periodStartExpression(String grain) {
        return switch (grain) {
            case "month" -> "date_trunc('month', entry_date)::date";
            case "quarter" -> "date_trunc('quarter', entry_date)::date";
            case "semester" ->
                    "(date_trunc('quarter', entry_date) - ((EXTRACT(quarter FROM entry_date)::int % 2) * interval '3 months'))::date";
            case "year" -> "date_trunc('year', entry_date)::date";
            default -> throw new IllegalStateException("Unexpected grain: " + grain);
        };
    }

    private String periodValuePredicate(String grain, Integer periodValue) {
        if (periodValue == null) return "TRUE";

        return switch (grain) {
            case "month" -> "EXTRACT(month FROM entry_date)::int = :periodValue";
            case "quarter" -> "EXTRACT(quarter FROM entry_date)::int = :periodValue";
            case "semester" -> "(((EXTRACT(quarter FROM entry_date)::int - 1) / 2) + 1) = :periodValue";
            case "year" -> "EXTRACT(year FROM entry_date)::int = :periodValue";
            default -> throw new IllegalStateException("Unexpected grain: " + grain);
        };
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrgDeptReportingStatsDto> getReportByOrganizationAndDepartment(String grain,
                                                                               Integer organizationId,
                                                                               Integer departmentId,
                                                                               Integer periodValue) {
        String normalizedGrain = normalizeGrain(grain);
        String periodStart = periodStartExpression(normalizedGrain);
        String periodValueWhere = periodValuePredicate(normalizedGrain, periodValue);

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
                      AND %s
                    GROUP BY organization_name, department_name, skill_name, period_start
                    ORDER BY period_start DESC;
                """.formatted(periodStart, periodValueWhere);

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("orgId", organizationId)
                .addValue("deptId", departmentId);
        if (periodValue != null) params.addValue("periodValue", periodValue);

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
    public List<EmployeeReportingStatsDto> getReportByEmployee(String grain,
                                                               Integer employeeId,
                                                               Integer periodValue) {
        String normalizedGrain = normalizeGrain(grain);
        String periodStart = periodStartExpression(normalizedGrain);
        String periodValueWhere = periodValuePredicate(normalizedGrain, periodValue);

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
                      AND %s
                    GROUP BY employee_id, first_name, last_name, skill_name, period_start
                    ORDER BY period_start DESC;
                """.formatted(periodStart, periodValueWhere);

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("employeeId", employeeId);
        if (periodValue != null) params.addValue("periodValue", periodValue);

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

