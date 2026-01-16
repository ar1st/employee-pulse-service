package gr.uom.employeepulseservice.service;

import gr.uom.employeepulseservice.controller.dto.*;
import gr.uom.employeepulseservice.llm.ChatGptClient;
import gr.uom.employeepulseservice.llm.GeneratedSkill;
import gr.uom.employeepulseservice.mapper.PerformanceReviewMapper;
import gr.uom.employeepulseservice.model.Employee;
import gr.uom.employeepulseservice.model.PerformanceReview;
import gr.uom.employeepulseservice.model.Skill;
import gr.uom.employeepulseservice.model.SkillEntry;
import gr.uom.employeepulseservice.repository.DepartmentRepository;
import gr.uom.employeepulseservice.repository.EmployeeRepository;
import gr.uom.employeepulseservice.repository.PerformanceReviewRepository;
import gr.uom.employeepulseservice.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class PerformanceReviewService {

    private final PerformanceReviewRepository performanceReviewRepository;
    private final PerformanceReviewMapper performanceReviewMapper;
    private final SkillRepository skillRepository;
    private final EmployeeRepository employeeRepository;
    private final ChatGptClient chatGptClient;
    private final DepartmentRepository departmentRepository;

    @Transactional
    public CreatePerformanceReviewResponseDto createPerformanceReview(CreatePerformanceReviewDto dto) {
        log.info("Creating performance review for {}", dto.employeeId());

        PerformanceReview performanceReview = performanceReviewMapper.toEntity(dto);

        Employee employee = findEmployeeById(dto.employeeId());
        performanceReview.setRefersTo(employee);
        performanceReview.setDepartment(employee.getDepartment());

        Employee reporter = findEmployeeById(dto.reporterId());
        performanceReview.setReportedBy(reporter);

        ensureReporterIsManagerOfEmployee(dto.reporterId(), dto.employeeId());

        LocalDate reviewDate = dto.reviewDate() != null ? dto.reviewDate() : LocalDate.now();
        LocalDateTime reviewDateTime = reviewDate.atStartOfDay();

        performanceReview.setReviewDate(reviewDate);
        performanceReview.setReviewDateTime(reviewDateTime);

        PerformanceReview createdPerformanceReview = performanceReviewRepository.save(performanceReview);

        log.info("Created performance review for {}", dto.employeeId());
        return new CreatePerformanceReviewResponseDto(createdPerformanceReview.getId());
    }

    @Transactional
    public PerformanceReviewDto updatePerformanceReview(Integer reviewId, UpdatePerformanceReviewDto dto) {
        log.info("Updating performance review {}", reviewId);

        PerformanceReview performanceReview = performanceReviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Performance review not found"));

        performanceReview.setRawText(dto.rawText());
        performanceReview.setComments(dto.comments());
        performanceReview.setOverallRating(dto.overallRating());
        
        if (dto.reviewDate() != null) {
            performanceReview.setReviewDate(dto.reviewDate());
            performanceReview.setReviewDateTime(dto.reviewDate().atStartOfDay());
        }

        PerformanceReview updatedPerformanceReview = performanceReviewRepository.save(performanceReview);

        log.info("Updated performance review {}", reviewId);
        return performanceReviewMapper.toDto(updatedPerformanceReview);
    }

    private void ensureReporterIsManagerOfEmployee(Integer reporterId, Integer employeeId) {
        boolean isManager = departmentRepository.isManagerOfEmployee(reporterId, employeeId);
        if (!isManager) {
            throw new RuntimeException("Reporter is not the manager of this employee");
        }
    }

    private Employee findEmployeeById(Integer id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
    }

    @Transactional(readOnly = true)
    public PerformanceReviewDto findById(Integer id) {
        PerformanceReview pr = performanceReviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Performance review not found"));
        return performanceReviewMapper.toDto(pr);
    }

    @Transactional(readOnly = true)
    public List<PerformanceReviewDto> findByDate(LocalDate date) {
        return performanceReviewMapper.toDtos(
                performanceReviewRepository.findAllByReviewDate(date)
        );
    }

    @Transactional(readOnly = true)
    public List<PerformanceReviewDto> findByDateRange(LocalDate from, LocalDate to) {
        return performanceReviewMapper.toDtos(
                performanceReviewRepository.findAllByReviewDateBetween(from, to)
        );
    }

    @Transactional(readOnly = true)
    public List<PerformanceReviewDto> findByEmployee(Integer employeeId) {
        return performanceReviewMapper.toDtos(
                performanceReviewRepository.findAllByRefersToId(employeeId)
        );
    }

    @Transactional(readOnly = true)
    public List<PerformanceReviewDto> findByReviewer(Integer reporterId) {
        return performanceReviewMapper.toDtos(
                performanceReviewRepository.findAllByReportedById(reporterId)
        );
    }

    @Transactional(readOnly = true)
    public List<PerformanceReviewDto> findByDepartment(Integer departmentId) {
        return performanceReviewMapper.toDtos(
                performanceReviewRepository.findAllByDepartmentId(departmentId)
        );
    }

    @Transactional(readOnly = true)
    public List<PerformanceReviewDto> findByOrganization(Integer organizationId) {
        return performanceReviewMapper.toDtos(
                performanceReviewRepository.findAllByDepartmentOrganizationIdOrderByReviewDateTimeDesc(organizationId)
        );
    }

    @Transactional(readOnly = true)
    public List<PerformanceReviewDto> findBySkill(Integer skillId) {
        return performanceReviewMapper.toDtos(
                performanceReviewRepository.findAllBySkillId(skillId)
        );
    }

    @Transactional(readOnly = true)
    public List<PerformanceReviewDto> findByOccupation(Integer occupationId) {
        return performanceReviewMapper.toDtos(
                performanceReviewRepository.findAllByRefersToOccupationId(occupationId)
        );
    }

    @Transactional
    public PerformanceReviewDto addSkillEntryToReview(Integer reviewId, SaveSkillEntryDto dto) {
        PerformanceReview review = performanceReviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Performance review not found"));

        Skill skill = skillRepository.findById(dto.skillId())
                .orElseThrow(() -> new RuntimeException("Skill not found"));

        SkillEntry entry = new SkillEntry();
        entry.setSkill(skill);
        entry.setRating(dto.rating());
        
        LocalDate entryDate = dto.entryDate() != null
            ? dto.entryDate() 
            : (review.getReviewDate() != null ? review.getReviewDate() : LocalDate.now());
        entry.setEntryDate(entryDate);
        entry.setEntryDateTime(entryDate.atStartOfDay());
        entry.setEmployee(review.getRefersTo());

        review.getSkillEntries().add(entry);

        return performanceReviewMapper.toDto(
                performanceReviewRepository.save(review)
        );
    }

    @Transactional
    public PerformanceReviewDto addSkillEntriesToReview(Integer reviewId, List<SaveSkillEntryDto> dtos) {
        PerformanceReview review = performanceReviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Performance review not found"));

        LocalDate defaultEntryDate = review.getReviewDate() != null ? review.getReviewDate() : LocalDate.now();

        for (SaveSkillEntryDto dto : dtos) {
            Skill skill = skillRepository.findById(dto.skillId())
                    .orElseThrow(() -> new RuntimeException("Skill not found with id: " + dto.skillId()));

            SkillEntry entry = new SkillEntry();
            entry.setSkill(skill);
            entry.setRating(dto.rating());
            
            LocalDate entryDate = dto.entryDate() != null ? dto.entryDate() : defaultEntryDate;
            entry.setEntryDate(entryDate);
            entry.setEntryDateTime(entryDate.atStartOfDay());
            entry.setEmployee(review.getRefersTo());

            review.getSkillEntries().add(entry);
        }

        return performanceReviewMapper.toDto(
                performanceReviewRepository.save(review)
        );
    }

    @Transactional
    public PerformanceReviewDto updateSkillEntryInReview(Integer reviewId, Integer entryId, SaveSkillEntryDto dto) {
        PerformanceReview review = performanceReviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Performance review not found"));

        SkillEntry entry = review.getSkillEntries().stream()
                .filter(se -> se.getId().equals(entryId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Skill entry not found for this review"));

        if (dto.skillId() != null && !dto.skillId().equals(entry.getSkill().getId())) {
            Skill skill = skillRepository.findById(dto.skillId())
                    .orElseThrow(() -> new RuntimeException("Skill not found"));
            entry.setSkill(skill);
        }

        if (dto.rating() != null) entry.setRating(dto.rating());
        
        // Use entryDate from DTO if provided, otherwise use review's reviewDate, or fall back to now()
        LocalDate entryDate = dto.entryDate() != null 
            ? dto.entryDate() 
            : (review.getReviewDate() != null ? review.getReviewDate() : LocalDate.now());
        entry.setEntryDate(entryDate);
        entry.setEntryDateTime(entryDate.atStartOfDay());

        return performanceReviewMapper.toDto(review);
    }

    @Transactional
    public PerformanceReviewDto removeSkillEntryFromReview(Integer reviewId, Integer entryId) {
        PerformanceReview review = performanceReviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Performance review not found"));

        boolean removed = review.getSkillEntries().removeIf(se -> se.getId().equals(entryId));

        if (!removed) {
            throw new RuntimeException("Skill entry not found for this review");
        }

        return performanceReviewMapper.toDto(
                performanceReviewRepository.save(review)
        );
    }

    @Transactional
    public void deletePerformanceReview(Integer reviewId) {
        performanceReviewRepository.deleteById(reviewId);
    }

    @Transactional(readOnly = true)
    public List<GeneratedSkillEntryDto> generateSkillEntries(String rawText) {
        log.info("Generating skill entries from performance review text");
        
        List<GeneratedSkill> generatedSkills = chatGptClient.analyzePerformanceReview(rawText);
        
        if (generatedSkills == null || generatedSkills.isEmpty()) {
            log.info("No skills generated from performance review text");
            return Collections.emptyList();
        }
        
        log.info("Generated skills:\n{}", formatGeneratedSkills(generatedSkills));
        
        return generatedSkills.stream()
                .map(generatedSkill -> {
                    Skill skill = skillRepository.findByEscoId(generatedSkill.getEscoSkillId());
                    
                    if (skill == null) {
                        log.warn("Skill not found for escoId: {}", generatedSkill.getEscoSkillId());
                        return null;
                    }
                    
                    return new GeneratedSkillEntryDto(
                            skill.getId(),
                            skill.getName(),
                            generatedSkill.getRating()
                    );
                })
                .filter(Objects::nonNull)
                .toList();
    }

    private String formatGeneratedSkills(List<GeneratedSkill> skills) {
        if (skills == null || skills.isEmpty()) {
            return "[]";
        }
        return "[\n" + skills.stream()
                .map(GeneratedSkill::toString)
                .reduce((a, b) -> a + ",\n" + b)
                .orElse("") + "\n]";
    }

}
