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
        Integer occupationId,
        Integer managerId,
        List<Integer> subordinateIds,
        List<Integer> skillEntryIds
) {
}
