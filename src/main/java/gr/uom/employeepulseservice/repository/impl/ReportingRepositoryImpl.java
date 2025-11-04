package gr.uom.employeepulseservice.repository.impl;

import gr.uom.employeepulseservice.controller.dto.EmployeeSkillPeriodStatsDto;
import gr.uom.employeepulseservice.controller.dto.OrgDeptSkillPeriodStatsDto;
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
        String g = (grain == null) ? "month" : grain.trim().toLowerCase();
        if (!ALLOWED_GRAINS.contains(g)) {
            throw new IllegalArgumentException("Unsupported grain: " + grain + " (use month|quarter|semester|year)");
        }
        return g;
    }

    private String periodStartExpr(String grain) {
        return switch (grain) {
            case "month" -> "date_trunc('month', entry_date)::date";
            case "quarter" -> "date_trunc('quarter', entry_date)::date";
            case "semester" ->
                    "(date_trunc('quarter', entry_date) - ((EXTRACT(quarter FROM entry_date)::int % 2) * interval '3 months'))::date";
            case "year" -> "date_trunc('year', entry_date)::date";
            default -> throw new IllegalStateException("Unexpected grain: " + grain);
        };
    }

    private String numberPredicate(String grain, Integer number) {
        if (number == null) return "TRUE";
        return switch (grain) {
            case "month" -> "EXTRACT(month   FROM entry_date)::int = :number";
            case "quarter" -> "EXTRACT(quarter FROM entry_date)::int = :number";
            case "semester" -> "(((EXTRACT(quarter FROM entry_date)::int - 1) / 2) + 1) = :number";
            case "year" -> "EXTRACT(year    FROM entry_date)::int = :number";
            default -> throw new IllegalStateException("Unexpected grain: " + grain);
        };
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrgDeptSkillPeriodStatsDto> getOrgDeptSkillPeriod(String grain,
                                                                  Integer organizationId,
                                                                  Integer departmentId,
                                                                  Integer number) {
        String g = normalizeGrain(grain);
        String periodStart = periodStartExpr(g);
        String numberWhere = numberPredicate(g, number);

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
                """.formatted(periodStart, numberWhere);

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("orgId", organizationId)
                .addValue("deptId", departmentId);
        if (number != null) params.addValue("number", number);

        return jdbc.query(sql, params, (rs, rn) -> new OrgDeptSkillPeriodStatsDto(
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
    public List<EmployeeSkillPeriodStatsDto> getEmployeeSkillPeriod(String grain,
                                                                    Integer employeeId,
                                                                    Integer number) {
        String g = normalizeGrain(grain);
        String periodStart = periodStartExpr(g);
        String numberWhere = numberPredicate(g, number);

        String sql = """
                    SELECT
                        employee_id,
                        first_name,
                        last_name,
                        skill_name,
                        %s AS period_start,
                        avg(rating) AS avg_rating,
                        min(rating) AS min_rating,
                        max(rating) AS max_rating,
                        count(*)    AS sample_count
                    FROM v_employee_skill_period
                    WHERE employee_id = :employeeId
                      AND %s
                    GROUP BY employee_id, first_name, last_name, skill_name, period_start
                    ORDER BY period_start DESC;
                """.formatted(periodStart, numberWhere);

        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("employeeId", employeeId);
        if (number != null) params.addValue("number", number);

        return jdbc.query(sql, params, (rs, rn) -> new EmployeeSkillPeriodStatsDto(
                rs.getInt("employee_id"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("skill_name"),
                rs.getObject("period_start", LocalDate.class),
                rs.getDouble("avg_rating"),
                rs.getDouble("min_rating"),
                rs.getDouble("max_rating"),
                rs.getLong("sample_count")
        ));
    }
}

