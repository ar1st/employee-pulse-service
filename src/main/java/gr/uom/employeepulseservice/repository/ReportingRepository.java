package gr.uom.employeepulseservice.repository;

import gr.uom.employeepulseservice.controller.dto.reportingDto.employee.EmployeeReportingResponseDto;
import gr.uom.employeepulseservice.controller.dto.reportingDto.employee.EmployeeSkillTimelineResponseDto;
import gr.uom.employeepulseservice.controller.dto.reportingDto.orgdept.OrgDeptReportingResponseDto;
import gr.uom.employeepulseservice.controller.dto.reportingDto.orgdept.OrgDeptSkillTimelineResponseDto;
import gr.uom.employeepulseservice.model.PeriodType;

public interface ReportingRepository {

    // Returns aggregated reporting stats for an organization and department
    OrgDeptReportingResponseDto getReportByOrganizationAndDepartment(PeriodType periodType,
                                                                     Integer organizationId,
                                                                     Integer departmentId,
                                                                     Integer periodValue,
                                                                     Integer year);

    // Returns aggregated reporting stats for an employee
    EmployeeReportingResponseDto getReportByEmployee(PeriodType periodType,
                                                     Integer employeeId,
                                                     Integer periodValue,
                                                     Integer year);

    // Returns timeline data for all skills of an employee
    EmployeeSkillTimelineResponseDto getSkillTimelineByEmployee(Integer employeeId, Integer skillId);

    // Returns timeline data for all skills in an organization/department
    OrgDeptSkillTimelineResponseDto getSkillTimelineByOrganizationAndDepartment(
            Integer organizationId,
            Integer departmentId,
            Integer skillId
    );

}

