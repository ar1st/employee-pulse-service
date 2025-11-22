package gr.uom.employeepulseservice.controller.dto.reportingDto.employee;

import java.util.List;

public record EmployeeReportingResponseDto(Integer employeeId,
                                           String firstName,
                                           String lastName,
                                           List<EmployeeReportingSkillDto> skills
) {
}
