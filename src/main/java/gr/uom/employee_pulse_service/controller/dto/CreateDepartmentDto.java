package gr.uom.employee_pulse_service.controller.dto;


public record CreateDepartmentDto(
        String name,
        Integer organizationId
) {
}
