package gr.uom.employeepulseservice.controller.dto;

import java.time.LocalDate;

public record SaveEmployeeDto(
        String firstName,
        String lastName,
        String email,
        LocalDate hireDate,
        Integer organizationId,
        Integer departmentId,
        Integer occupationId
) {
}
