package gr.uom.employeepulseservice.repository;

import gr.uom.employeepulseservice.controller.dto.EmployeeSkillPeriodStatsDto;
import gr.uom.employeepulseservice.controller.dto.OrgDeptSkillPeriodStatsDto;

import java.util.List;

public interface ReportingRepository {

    List<OrgDeptSkillPeriodStatsDto> getOrgDeptSkillPeriod(String grain,
                                                           Integer organizationId,
                                                           Integer departmentId);

    List<EmployeeSkillPeriodStatsDto> getEmployeeSkillPeriod(String grain,
                                                             Integer employeeId);
}
