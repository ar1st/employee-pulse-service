package gr.uom.employeepulseservice.controller.dto;

import java.time.LocalDate;
import java.util.List;

public record EmployeeDto(
        Integer id,
        String firstName,
        String lastName,
        String email,
        LocalDate hireDate,
        Integer departmentId,
        String departmentName,
        Integer organizationId,
        Integer occupationId,
        String occupationTitle,
        List<Integer> skillEntryIds
) {
}
