package gr.uom.employeepulseservice.repository.impl;

import gr.uom.employeepulseservice.controller.dto.EmployeeSkillPeriodStatsDto;
import gr.uom.employeepulseservice.controller.dto.OrgDeptSkillPeriodStatsDto;
import gr.uom.employeepulseservice.repository.ReportingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class ReportingRepositoryImpl implements ReportingRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public List<OrgDeptSkillPeriodStatsDto> getOrgDeptSkillPeriod(String grain,
                                                             Integer organizationId,
                                                             Integer departmentId) {
        String sql = """
            SELECT
                organization_name,
                department_name,
                skill_name,
                date_trunc(:grain, entry_date) AS period_start,
                avg(rating) AS avg_rating,
                min(rating) AS min_rating,
                max(rating) AS max_rating,
                count(*) AS sample_count
            FROM v_org_department_skill_period
            WHERE organization_id = :orgId
              AND department_id = :deptId
            GROUP BY organization_name, department_name, skill_name, period_start
            ORDER BY period_start DESC;
        """;

        Map<String, Object> params = Map.of(
                "grain", grain,
                "orgId", organizationId,
                "deptId", departmentId
        );

        return jdbcTemplate.query(sql, params, (rs, rn) -> new OrgDeptSkillPeriodStatsDto(
                rs.getString("organization_name"),
                rs.getString("department_name"),
                rs.getString("skill_name"),
                rs.getDate("period_start").toLocalDate(),
                rs.getDouble("avg_rating"),
                rs.getDouble("min_rating"),
                rs.getDouble("max_rating"),
                rs.getLong("sample_count")
        ));
    }

    public List<EmployeeSkillPeriodStatsDto> getEmployeeSkillPeriod(String grain,
                                                               Integer employeeId) {

        String sql = """
        SELECT
            employee_id,
            first_name,
            last_name,
            skill_name,
            date_trunc(:grain, entry_date) AS period_start,
            avg(rating) AS avg_rating,
            min(rating) AS min_rating,
            max(rating) AS max_rating,
            count(*) AS sample_count
        FROM v_employee_skill_period
        WHERE employee_id = :employeeId
        GROUP BY employee_id, first_name, last_name, skill_name, period_start
        ORDER BY period_start DESC;
    """;

        Map<String,Object> params = Map.of(
                "grain", grain,
                "employeeId", employeeId
        );

        return jdbcTemplate.query(sql, params, (rs, rn) -> new EmployeeSkillPeriodStatsDto(
                rs.getInt("employee_id"),
                rs.getString("first_name"),
                rs.getString("last_name"),
                rs.getString("skill_name"),
                rs.getDate("period_start").toLocalDate(),
                rs.getDouble("avg_rating"),
                rs.getDouble("min_rating"),
                rs.getDouble("max_rating"),
                rs.getLong("sample_count")
        ));
    }

}

