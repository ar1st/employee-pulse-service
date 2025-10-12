package gr.uom.employeepulseservice.service;

import gr.uom.employeepulseservice.controller.dto.SaveSkillDto;
import gr.uom.employeepulseservice.controller.dto.SkillDto;
import gr.uom.employeepulseservice.mapper.SkillMapper;
import gr.uom.employeepulseservice.model.Skill;
import gr.uom.employeepulseservice.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillService {

    private final SkillRepository skillRepository;
    private final SkillMapper skillMapper;

    @Transactional(readOnly = true)
    public List<SkillDto> findAll() {
        List<Skill> skills = skillRepository.findAll();

        return skillMapper.toDtos(skills);
    }

    @Transactional(readOnly = true)
    public SkillDto findSkillById(Integer id) {
        Skill skill = findById(id);

        return skillMapper.toDto(skill);
    }

    private Skill findById(Integer id) {
        return skillRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Skill not found"));
    }

    @Transactional
    public void createSkill(SaveSkillDto dto) {
        Skill skill = skillMapper.toEntity(dto);

        skillRepository.save(skill);
    }

    @Transactional
    public void updateSkill(Integer id, SaveSkillDto dto) {
        Skill skill = findById(id);

        skillMapper.updateFromDto(skill, dto);
    }

    @Transactional
    public void deleteSkill(Integer id) {
        skillRepository.deleteById(id);
    }

    @Transactional
    public void bulkCreateSkills(List<SaveSkillDto> dtos) {
        List<Skill> toSave = dtos.stream()
                .map(skillMapper::toEntity)
                .toList();

        skillRepository.saveAll(toSave);
    }

    @Transactional(readOnly = true)
    public List<SkillDto> findByOrganizationId(Integer organizationId) {
        return skillMapper.toDtos(
                skillRepository.findSkillsByOrganizationId(organizationId)
        );
    }

    @Transactional(readOnly = true)
    public List<SkillDto> findByDepartmentId(Integer departmentId) {
        return skillMapper.toDtos(
                skillRepository.findSkillsByDepartmentId(departmentId)
        );
    }


}