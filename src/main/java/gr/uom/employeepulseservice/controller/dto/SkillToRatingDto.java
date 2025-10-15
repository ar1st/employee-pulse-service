package gr.uom.employeepulseservice.controller.dto;

public record SkillToRatingDto(
    Integer skillId,
    String skillName,
    Double rating
    ) {
}
