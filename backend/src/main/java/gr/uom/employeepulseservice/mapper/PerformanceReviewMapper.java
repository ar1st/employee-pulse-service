package gr.uom.employeepulseservice.mapper;

import gr.uom.employeepulseservice.controller.dto.CreatePerformanceReviewDto;
import gr.uom.employeepulseservice.controller.dto.PerformanceReviewDto;
import gr.uom.employeepulseservice.model.PerformanceReview;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = SkillEntryMapper.class)
public interface PerformanceReviewMapper {

    PerformanceReview toEntity(CreatePerformanceReviewDto dto);

    @Mapping(source = "skillEntries", target = "skillEntryDtos")
    @Mapping(source = "department.name", target = "departmentName")
    @Mapping(source = "reportedBy", target = "reporterName", qualifiedByName = "employeeToFullName")
    @Mapping(source = "refersTo", target = "employeeName", qualifiedByName = "employeeToFullName")
    PerformanceReviewDto toDto(PerformanceReview entity);

    @Named("employeeToFullName")
    default String employeeToFullName(gr.uom.employeepulseservice.model.Employee employee) {
        if (employee == null) {
            return null;
        }
        String firstName = employee.getFirstName() != null ? employee.getFirstName() : "";
        String lastName = employee.getLastName() != null ? employee.getLastName() : "";
        return (firstName + " " + lastName).trim();
    }

    List<PerformanceReviewDto> toDtos(List<PerformanceReview> entities);

}
