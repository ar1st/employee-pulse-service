package gr.uom.employeepulseservice.controller.dto;

import lombok.Data;

@Data
public class DepartmentDto {
    private Integer id;
    private String name;
    private Integer organizationId;
}
