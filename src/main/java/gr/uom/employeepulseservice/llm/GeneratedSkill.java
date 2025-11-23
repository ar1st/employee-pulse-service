package gr.uom.employeepulseservice.llm;

import lombok.Data;

@Data
public class GeneratedSkill {
    private String skillName;
    private String escoSkillId;
    private Double rating;

    @Override
    public String toString() {
        return "GeneratedSkill{skillName='" + skillName + "', escoSkillId='" + escoSkillId + "', rating=" + rating + "}";
    }
}
