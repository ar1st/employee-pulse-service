package gr.uom.employeepulseservice.controller.dto;

import java.time.LocalDate;

public record SaveSkillEntryDto(
        Integer skillId,
        Double rating,
        LocalDate entryDate
) {
}
