package gr.uom.employeepulseservice.mapper;

import gr.uom.employeepulseservice.controller.dto.SaveSkillDto;
import gr.uom.employeepulseservice.controller.dto.SkillDto;
import gr.uom.employeepulseservice.model.Skill;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SkillMapper {

    @Mapping(target = "id", ignore = true)
    Skill toEntity(SaveSkillDto dto);

    @Mapping(target = "id", ignore = true)
    void updateFromDto(@MappingTarget Skill entity, SaveSkillDto dto);

    SkillDto toDto(Skill skill);

    List<SkillDto> toDtos(List<Skill> skills);
}
