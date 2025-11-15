package gr.uom.employeepulseservice.repository;

import gr.uom.employeepulseservice.controller.dto.reportingDto.EmployeeReportingStatsDto;
import gr.uom.employeepulseservice.controller.dto.reportingDto.EmployeeSkillTimelineStatsDto;
import gr.uom.employeepulseservice.controller.dto.reportingDto.OrgDeptReportingStatsDto;
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

}

