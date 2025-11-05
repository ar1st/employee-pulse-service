package gr.uom.employeepulseservice.repository;

import gr.uom.employeepulseservice.controller.dto.EmployeeReportingStatsDto;
import gr.uom.employeepulseservice.controller.dto.OrgDeptReportingStatsDto;
import gr.uom.employeepulseservice.model.PeriodType;

import java.util.List;

public interface ReportingRepository {

    List<OrgDeptReportingStatsDto> getReportByOrganizationAndDepartment(PeriodType periodType,
                                                                        Integer organizationId,
                                                                        Integer departmentId,
                                                                        Integer periodValue);

    List<EmployeeReportingStatsDto> getReportByEmployee(PeriodType periodType,
                                                        Integer employeeId,
                                                        Integer periodValue);
}
