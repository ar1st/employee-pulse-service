package gr.uom.employee_pulse_service.mapper;

import gr.uom.employee_pulse_service.controller.dto.OrganizationDto;
import gr.uom.employee_pulse_service.controller.dto.SaveOrganizationDto;
import gr.uom.employee_pulse_service.model.Organization;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrganizationMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "departments", ignore = true)
    Organization toEntity(SaveOrganizationDto dto);

    OrganizationDto toDto(Organization organization);

    List<OrganizationDto> toDtos(List<Organization> organizations);
}
