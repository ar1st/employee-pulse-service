package gr.uom.employeepulseservice.controller;

import gr.uom.employeepulseservice.controller.dto.SaveSkillDto;
import gr.uom.employeepulseservice.controller.dto.SkillDto;
import gr.uom.employeepulseservice.service.SkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("skills")
@RequiredArgsConstructor
public class SkillController {

    private final SkillService skillService;

    @GetMapping
    public ResponseEntity<List<SkillDto>> findAll() {
        return ResponseEntity.ok(skillService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SkillDto> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(skillService.findSkillById(id));
    }

    @PostMapping
    public ResponseEntity<Void> createSkill(@RequestBody SaveSkillDto dto) {
        skillService.createSkill(dto);

        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateSkill(@PathVariable Integer id, @RequestBody SaveSkillDto dto) {
        skillService.updateSkill(id, dto);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSkill(@PathVariable Integer id) {
        skillService.deleteSkill(id);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/bulk")
    public ResponseEntity<Void> bulkCreateSkills(@RequestBody String json) {
        skillService.bulkCreateSkills(json);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/organization/{organizationId}")
    public ResponseEntity<List<SkillDto>> findByOrganizationId(@PathVariable Integer organizationId) {
        return ResponseEntity.ok(skillService.findByOrganizationId(organizationId));
    }

    @GetMapping("/department/{departmentId}")
    public ResponseEntity<List<SkillDto>> findByDepartmentId(@PathVariable Integer departmentId) {
        return ResponseEntity.ok(skillService.findByDepartmentId(departmentId));
    }
}