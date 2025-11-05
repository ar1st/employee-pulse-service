package gr.uom.employeepulseservice.repository;

import gr.uom.employeepulseservice.controller.dto.EmployeeReportingStatsDto;
import gr.uom.employeepulseservice.controller.dto.OrgDeptReportingStatsDto;

import java.util.List;

public interface ReportingRepository {

    List<OrgDeptReportingStatsDto> getReportByOrganizationAndDepartment(String grain,
                                                                        Integer organizationId,
                                                                        Integer departmentId,
                                                                        Integer periodValue);

    List<EmployeeReportingStatsDto> getReportByEmployee(String grain,
                                                        Integer employeeId,
                                                        Integer periodValue);
}
