package gr.uom.employeepulseservice.controller.dto;

public record CreateDepartmentDto(
        String name,
        Integer organizationId
) {
}
