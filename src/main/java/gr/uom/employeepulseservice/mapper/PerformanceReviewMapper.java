package gr.uom.employeepulseservice.mapper;

import gr.uom.employeepulseservice.controller.dto.CreatePerformanceReviewDto;
import gr.uom.employeepulseservice.model.PerformanceReview;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PerformanceReviewMapper {

    @Mapping(target = "reviewDate", ignore = true)
    @Mapping(target = "skillEntries", ignore = true)
    @Mapping(target = "reportedBy", ignore = true)
    @Mapping(target = "refersTo", ignore = true)
    @Mapping(target = "id", ignore = true)
    PerformanceReview toEntity(CreatePerformanceReviewDto dto);
}
