package gr.uom.employeepulseservice.controller.dto.reportingDto;

import java.time.LocalDate;

public record SkillTimelinePointDto(
        LocalDate date,
        Double rating
) {}