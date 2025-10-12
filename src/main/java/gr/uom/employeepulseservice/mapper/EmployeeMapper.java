package gr.uom.employeepulseservice.mapper;

import gr.uom.employeepulseservice.controller.dto.EmployeeDto;
import gr.uom.employeepulseservice.controller.dto.SaveEmployeeDto;
import gr.uom.employeepulseservice.model.Employee;
import gr.uom.employeepulseservice.model.SkillEntry;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "skillEntries", ignore = true)
    @Mapping(target = "occupation", ignore = true)
    @Mapping(target = "department", ignore = true)
    Employee toEntity(SaveEmployeeDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "skillEntries", ignore = true)
    @Mapping(target = "occupation", ignore = true)
    @Mapping(target = "department", ignore = true)
    void updateFromDto(@MappingTarget Employee entity, SaveEmployeeDto dto);

    @Mapping(target = "skillEntryIds", source = "skillEntries")
    @Mapping(target = "occupationId", source = "occupation.id")
    @Mapping(target = "departmentId", source = "department.id")
    @Mapping(target = "organizationId", source = "organization.id")
    EmployeeDto toDto(Employee employee);

    List<EmployeeDto> toDtos(List<Employee> employees);

    default List<Integer> mapSkillEntries(List<SkillEntry> skillEntries) {
        if (skillEntries == null) {
            return null;
        }
        return skillEntries.stream()
                .map(SkillEntry::getId)
                .toList();
    }

}
