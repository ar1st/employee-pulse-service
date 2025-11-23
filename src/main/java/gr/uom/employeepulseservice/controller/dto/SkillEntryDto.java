package gr.uom.employeepulseservice.controller.dto;

import java.time.LocalDate;

public record SkillEntryDto(
    Integer id,
    Integer skillId,
    String skillName,
    Double rating,
    LocalDate entryDate,
    Integer employeeId
    ) {
}
