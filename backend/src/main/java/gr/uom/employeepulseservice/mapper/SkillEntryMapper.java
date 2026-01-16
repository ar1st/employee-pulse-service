package gr.uom.employeepulseservice.mapper;

import gr.uom.employeepulseservice.controller.dto.SkillEntryDto;
import gr.uom.employeepulseservice.model.SkillEntry;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SkillEntryMapper {

    @Mapping(source = "skill.id", target = "skillId")
    @Mapping(source = "skill.name", target = "skillName")
    @Mapping(source = "employee.id", target = "employeeId")
    SkillEntryDto toDto(SkillEntry entity);

    List<SkillEntryDto> toDtos(List<SkillEntry> entities);

}
