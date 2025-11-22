package gr.uom.employeepulseservice.controller.dto.reportingDto.employee;

import java.util.List;

// Top-level timeline response containing employee info and all skills
public record EmployeeSkillTimelineResponseDto(Integer employeeId,
                                               String firstName,
                                               String lastName,
                                               List<EmployeeSkillTimelineSkillDto> skills
) {}
