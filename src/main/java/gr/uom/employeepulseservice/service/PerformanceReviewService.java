package gr.uom.employeepulseservice.service;

import gr.uom.employeepulseservice.controller.dto.CreatePerformanceReviewDto;
import gr.uom.employeepulseservice.controller.dto.PerformanceReviewDto;
import gr.uom.employeepulseservice.mapper.PerformanceReviewMapper;
import gr.uom.employeepulseservice.model.Employee;
import gr.uom.employeepulseservice.model.PerformanceReview;
import gr.uom.employeepulseservice.model.SkillEntry;
import gr.uom.employeepulseservice.repository.EmployeeRepository;
import gr.uom.employeepulseservice.repository.PerformanceReviewRepository;
import gr.uom.employeepulseservice.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PerformanceReviewService {

    private final PerformanceReviewRepository performanceReviewRepository;
    private final PerformanceReviewMapper performanceReviewMapper;
    private final SkillRepository skillRepository;
    private final EmployeeRepository employeeRepository;

    @Transactional
    public void createPerformanceReview(CreatePerformanceReviewDto dto) {
        LocalDate now = LocalDate.now();
        PerformanceReview performanceReview = performanceReviewMapper.toEntity(dto);

        //todo make sure reporter is manager of employee
        Employee employee = findEmployeeById(dto.employeeId());
        performanceReview.setRefersTo(employee);

        Employee reporter = findEmployeeById(dto.reporterId());
        performanceReview.setReportedBy(reporter);

        //todo replace by NLP service
        performanceReview.setSkillEntries(mockSkillEntries(employee, now));

        performanceReview.setReviewDate(now);

        performanceReviewRepository.save(performanceReview);
    }

    private List<SkillEntry> mockSkillEntries(Employee employee, LocalDate now) {
        SkillEntry skillEntry1 = new SkillEntry();
        skillEntry1.setEntryDate(now);
        skillEntry1.setRating(5.0);
        skillEntry1.setSkill(skillRepository.findById(1).orElseThrow());
        skillEntry1.setEmployee(employee);
        return List.of(skillEntry1);
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
                performanceReviewRepository.findAllByRefersToDepartmentId(departmentId)
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

}
