package gr.uom.employeepulseservice.repository;

import gr.uom.employeepulseservice.controller.dto.reportingDto.employee.EmployeeReportingStatsDto;
import gr.uom.employeepulseservice.controller.dto.reportingDto.employee.EmployeeSkillTimelineStatsDto;
import gr.uom.employeepulseservice.controller.dto.reportingDto.orgdept.OrgDeptReportingStatsDto;
import gr.uom.employeepulseservice.controller.dto.reportingDto.orgdept.OrgDeptSkillTimelineStatsDto;
import gr.uom.employeepulseservice.model.PeriodType;

import java.util.List;

public interface ReportingRepository {

    List<OrgDeptReportingStatsDto> getReportByOrganizationAndDepartment(PeriodType periodType,
                                                                        Integer organizationId,
                                                                        Integer departmentId,
                                                                        Integer periodValue,
                                                                        Integer year);

    List<EmployeeReportingStatsDto> getReportByEmployee(PeriodType periodType,
                                                        Integer employeeId,
                                                        Integer periodValue,
                                                        Integer year);

    List<EmployeeSkillTimelineStatsDto> getSkillTimelineByEmployee(Integer employeeId,
                                                                   Integer skillId);

    List<OrgDeptSkillTimelineStatsDto> getSkillTimelineByOrganizationAndDepartment(
            Integer organizationId,
            Integer departmentId,
            Integer skillId
    );
}

