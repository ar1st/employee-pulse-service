package gr.uom.employeepulseservice.llm;

import lombok.Data;

@Data
public class GeneratedSkill {
    private String skillName;
    private String escoSkillId;
    private Double rating;
}
