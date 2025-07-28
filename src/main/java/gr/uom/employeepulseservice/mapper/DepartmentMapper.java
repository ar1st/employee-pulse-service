package gr.uom.employeepulseservice.mapper;

import gr.uom.employeepulseservice.controller.dto.DepartmentDto;
import gr.uom.employeepulseservice.controller.dto.CreateDepartmentDto;
import gr.uom.employeepulseservice.model.Department;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DepartmentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "employees", ignore = true)
    @Mapping(target = "organization", ignore = true)
    Department toEntity(CreateDepartmentDto dto);

    @Mapping(target = "organizationId", source = "organization.id")
    DepartmentDto toDto(Department organization);

    List<DepartmentDto> toDtos(List<Department> organizations);
}
