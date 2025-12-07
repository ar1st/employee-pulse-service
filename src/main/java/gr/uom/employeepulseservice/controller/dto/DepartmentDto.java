package gr.uom.employeepulseservice.controller.dto;


public record DepartmentDto(
        Integer id,
        String name,
        Integer organizationId,
        Integer managerId
) {

}
