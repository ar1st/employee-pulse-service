package gr.uom.employeepulseservice.mapper;

import gr.uom.employeepulseservice.controller.dto.CreatePerformanceReviewDto;
import gr.uom.employeepulseservice.controller.dto.PerformanceReviewDto;
import gr.uom.employeepulseservice.model.PerformanceReview;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = SkillEntryMapper.class)
public interface PerformanceReviewMapper {

    @Mapping(target = "reviewDate", ignore = true)
    @Mapping(target = "skillEntries", ignore = true)
    @Mapping(target = "reportedBy", ignore = true)
    @Mapping(target = "refersTo", ignore = true)
    @Mapping(target = "department", ignore = true)
    @Mapping(target = "id", ignore = true)
    PerformanceReview toEntity(CreatePerformanceReviewDto dto);

    @Mapping(source = "skillEntries", target = "skillEntryDtos")
    PerformanceReviewDto toDto(PerformanceReview entity);

    List<PerformanceReviewDto> toDtos(List<PerformanceReview> entities);

}
