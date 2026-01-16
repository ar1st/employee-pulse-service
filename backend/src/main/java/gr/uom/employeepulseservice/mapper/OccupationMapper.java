package gr.uom.employeepulseservice.mapper;

import gr.uom.employeepulseservice.controller.dto.OccupationDto;
import gr.uom.employeepulseservice.controller.dto.SaveOccupationDto;
import gr.uom.employeepulseservice.model.Occupation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OccupationMapper {

    @Mapping(target = "id", ignore = true)
    Occupation toEntity(SaveOccupationDto dto);

    @Mapping(target = "id", ignore = true)
    void updateFromDto(@MappingTarget Occupation entity, SaveOccupationDto dto);

    OccupationDto toDto(Occupation occupation);

    List<OccupationDto> toDtos(List<Occupation> occupations);
}
