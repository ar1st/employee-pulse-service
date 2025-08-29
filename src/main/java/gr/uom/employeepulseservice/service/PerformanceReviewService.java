package gr.uom.employeepulseservice.service;

import gr.uom.employeepulseservice.controller.dto.CreatePerformanceReviewDto;
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

}
