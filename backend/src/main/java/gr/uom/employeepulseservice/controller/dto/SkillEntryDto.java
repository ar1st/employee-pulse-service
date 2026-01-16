package gr.uom.employeepulseservice.controller.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record SkillEntryDto(
    Integer id,
    Integer skillId,
    String skillName,
    Double rating,
    LocalDate entryDate,
    LocalDateTime entryDateTime,
    Integer employeeId
    ) {
}
