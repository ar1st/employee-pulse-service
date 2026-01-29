package gr.uom.employeepulseservice.service;

import gr.uom.employeepulseservice.controller.dto.*;
import gr.uom.employeepulseservice.llm.ChatGptClient;
import gr.uom.employeepulseservice.llm.GeneratedSkill;
import gr.uom.employeepulseservice.mapper.PerformanceReviewMapper;
import gr.uom.employeepulseservice.model.Department;
import gr.uom.employeepulseservice.model.Employee;
import gr.uom.employeepulseservice.model.PerformanceReview;
import gr.uom.employeepulseservice.model.Skill;
import gr.uom.employeepulseservice.model.SkillEntry;
import gr.uom.employeepulseservice.repository.DepartmentRepository;
import gr.uom.employeepulseservice.repository.EmployeeRepository;
import gr.uom.employeepulseservice.repository.PerformanceReviewRepository;
import gr.uom.employeepulseservice.repository.SkillRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PerformanceReviewServiceTest {

    @Mock
    private PerformanceReviewRepository performanceReviewRepository;

    @Mock
    private PerformanceReviewMapper performanceReviewMapper;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private ChatGptClient chatGptClient;

    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private PerformanceReviewService performanceReviewService;

    private Department department;
    private Employee employee;
    private Employee reporter;
    private Skill skill1;
    private Skill skill2;

    @BeforeEach
    void setUp() {
        department = new Department();
        department.setId(10);
        department.setName("Engineering");

        employee = new Employee();
        employee.setId(100);
        employee.setFirstName("John");
        employee.setLastName("Doe");
        employee.setDepartment(department);

        reporter = new Employee();
        reporter.setId(200);
        reporter.setFirstName("Jane");
        reporter.setLastName("Smith");

        skill1 = new Skill();
        skill1.setId(1);
        skill1.setName("Communication");
        skill1.setEscoId("ESCO-1");

        skill2 = new Skill();
        skill2.setId(2);
        skill2.setName("Leadership");
        skill2.setEscoId("ESCO-2");
    }

    @Test
    void createPerformanceReview_WhenReporterIsManager_ShouldSetRelationsAndDatesAndReturnId() {
        // Given
        LocalDate reviewDate = LocalDate.of(2026, 1, 10);
        CreatePerformanceReviewDto dto = new CreatePerformanceReviewDto(
                "raw",
                "comments",
                4.5,
                reporter.getId(),
                employee.getId(),
                reviewDate
        );

        PerformanceReview mapped = new PerformanceReview();
        when(performanceReviewMapper.toEntity(dto)).thenReturn(mapped);
        when(employeeRepository.findById(employee.getId())).thenReturn(Optional.of(employee));
        when(employeeRepository.findById(reporter.getId())).thenReturn(Optional.of(reporter));
        when(departmentRepository.isManagerOfEmployee(reporter.getId(), employee.getId())).thenReturn(true);

        PerformanceReview saved = new PerformanceReview();
        saved.setId(999);
        when(performanceReviewRepository.save(any(PerformanceReview.class))).thenReturn(saved);

        // When
        CreatePerformanceReviewResponseDto result = performanceReviewService.createPerformanceReview(dto);

        // Then
        assertNotNull(result);
        assertEquals(999, result.performanceReviewId());

        ArgumentCaptor<PerformanceReview> captor = ArgumentCaptor.forClass(PerformanceReview.class);
        verify(performanceReviewRepository).save(captor.capture());
        PerformanceReview toSave = captor.getValue();
        assertEquals(employee, toSave.getRefersTo());
        assertEquals(department, toSave.getDepartment());
        assertEquals(reporter, toSave.getReportedBy());
        assertEquals(reviewDate, toSave.getReviewDate());
        assertEquals(reviewDate.atStartOfDay(), toSave.getReviewDateTime());
    }

    @Test
    void createPerformanceReview_WhenReporterNotManager_ShouldThrowAndNotSave() {
        // Given
        LocalDate reviewDate = LocalDate.of(2026, 1, 10);
        CreatePerformanceReviewDto dto = new CreatePerformanceReviewDto(
                "raw",
                "comments",
                4.5,
                reporter.getId(),
                employee.getId(),
                reviewDate
        );

        when(performanceReviewMapper.toEntity(dto)).thenReturn(new PerformanceReview());
        when(employeeRepository.findById(employee.getId())).thenReturn(Optional.of(employee));
        when(employeeRepository.findById(reporter.getId())).thenReturn(Optional.of(reporter));
        when(departmentRepository.isManagerOfEmployee(reporter.getId(), employee.getId())).thenReturn(false);

        // When & Then
        RuntimeException ex = assertThrows(RuntimeException.class, () -> performanceReviewService.createPerformanceReview(dto));
        assertEquals("Reporter is not the manager of this employee", ex.getMessage());
        verify(performanceReviewRepository, never()).save(any());
    }

    @Test
    void createPerformanceReview_WhenReviewDateNull_ShouldUseCurrentDate() {
        // Given
        CreatePerformanceReviewDto dto = new CreatePerformanceReviewDto(
                "raw",
                "comments",
                4.5,
                reporter.getId(),
                employee.getId(),
                null
        );

        PerformanceReview mapped = new PerformanceReview();
        when(performanceReviewMapper.toEntity(dto)).thenReturn(mapped);
        when(employeeRepository.findById(employee.getId())).thenReturn(Optional.of(employee));
        when(employeeRepository.findById(reporter.getId())).thenReturn(Optional.of(reporter));
        when(departmentRepository.isManagerOfEmployee(reporter.getId(), employee.getId())).thenReturn(true);

        PerformanceReview saved = new PerformanceReview();
        saved.setId(999);
        when(performanceReviewRepository.save(any(PerformanceReview.class))).thenReturn(saved);

        // When
        performanceReviewService.createPerformanceReview(dto);

        // Then
        ArgumentCaptor<PerformanceReview> captor = ArgumentCaptor.forClass(PerformanceReview.class);
        verify(performanceReviewRepository).save(captor.capture());
        PerformanceReview toSave = captor.getValue();
        assertNotNull(toSave.getReviewDate());
        assertNotNull(toSave.getReviewDateTime());
        assertEquals(toSave.getReviewDate().atStartOfDay(), toSave.getReviewDateTime());
    }

    @Test
    void updatePerformanceReview_WhenExists_ShouldUpdateFieldsAndSave() {
        // Given
        Integer reviewId = 55;
        PerformanceReview existing = new PerformanceReview();
        existing.setId(reviewId);
        existing.setRawText("old");
        existing.setComments("old");
        existing.setOverallRating(1.0);
        existing.setReviewDate(LocalDate.of(2026, 1, 1));
        existing.setReviewDateTime(LocalDate.of(2026, 1, 1).atStartOfDay());

        UpdatePerformanceReviewDto dto = new UpdatePerformanceReviewDto(
                "new raw",
                "new comments",
                4.0,
                LocalDate.of(2026, 1, 20)
        );

        PerformanceReviewDto mappedDto = new PerformanceReviewDto(
                reviewId,
                dto.rawText(),
                dto.comments(),
                dto.overallRating(),
                dto.reviewDate(),
                dto.reviewDate().atStartOfDay(),
                List.of(),
                null,
                null,
                null
        );

        when(performanceReviewRepository.findById(reviewId)).thenReturn(Optional.of(existing));
        when(performanceReviewRepository.save(existing)).thenReturn(existing);
        when(performanceReviewMapper.toDto(existing)).thenReturn(mappedDto);

        // When
        PerformanceReviewDto result = performanceReviewService.updatePerformanceReview(reviewId, dto);

        // Then
        assertEquals(mappedDto, result);
        assertEquals("new raw", existing.getRawText());
        assertEquals("new comments", existing.getComments());
        assertEquals(4.0, existing.getOverallRating());
        assertEquals(dto.reviewDate(), existing.getReviewDate());
        assertEquals(dto.reviewDate().atStartOfDay(), existing.getReviewDateTime());
        verify(performanceReviewRepository).save(existing);
        verify(performanceReviewMapper).toDto(existing);
    }

    @Test
    void updatePerformanceReview_WhenNotFound_ShouldThrow() {
        // Given
        Integer reviewId = 999;
        UpdatePerformanceReviewDto dto = new UpdatePerformanceReviewDto("raw", "c", 1.0, LocalDate.of(2026, 1, 1));
        when(performanceReviewRepository.findById(reviewId)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException ex = assertThrows(RuntimeException.class, () -> performanceReviewService.updatePerformanceReview(reviewId, dto));
        assertEquals("Performance review not found", ex.getMessage());
        verify(performanceReviewRepository, never()).save(any());
        verify(performanceReviewMapper, never()).toDto(any());
    }

    @Test
    void updatePerformanceReview_WhenReviewDateNull_ShouldNotUpdateDate() {
        // Given
        Integer reviewId = 55;
        LocalDate originalDate = LocalDate.of(2026, 1, 1);
        PerformanceReview existing = new PerformanceReview();
        existing.setId(reviewId);
        existing.setRawText("old");
        existing.setComments("old");
        existing.setOverallRating(1.0);
        existing.setReviewDate(originalDate);
        existing.setReviewDateTime(originalDate.atStartOfDay());

        UpdatePerformanceReviewDto dto = new UpdatePerformanceReviewDto(
                "new raw",
                "new comments",
                4.0,
                null
        );

        PerformanceReviewDto mappedDto = new PerformanceReviewDto(
                reviewId,
                dto.rawText(),
                dto.comments(),
                dto.overallRating(),
                originalDate,
                originalDate.atStartOfDay(),
                List.of(),
                null,
                null,
                null
        );

        when(performanceReviewRepository.findById(reviewId)).thenReturn(Optional.of(existing));
        when(performanceReviewRepository.save(existing)).thenReturn(existing);
        when(performanceReviewMapper.toDto(existing)).thenReturn(mappedDto);

        // When
        performanceReviewService.updatePerformanceReview(reviewId, dto);

        // Then
        assertEquals("new raw", existing.getRawText());
        assertEquals("new comments", existing.getComments());
        assertEquals(4.0, existing.getOverallRating());
        assertEquals(originalDate, existing.getReviewDate());
        assertEquals(originalDate.atStartOfDay(), existing.getReviewDateTime());
        verify(performanceReviewRepository).save(existing);
    }

    @Test
    void findById_WhenExists_ShouldReturnDto() {
        // Given
        Integer reviewId = 1;
        PerformanceReview entity = new PerformanceReview();
        entity.setId(reviewId);
        PerformanceReviewDto dto = new PerformanceReviewDto(reviewId, null, null, null, null, null, List.of(), null, null, null);
        when(performanceReviewRepository.findById(reviewId)).thenReturn(Optional.of(entity));
        when(performanceReviewMapper.toDto(entity)).thenReturn(dto);

        // When
        PerformanceReviewDto result = performanceReviewService.findById(reviewId);

        // Then
        assertEquals(dto, result);
        verify(performanceReviewRepository).findById(reviewId);
        verify(performanceReviewMapper).toDto(entity);
    }

    @Test
    void findById_WhenNotFound_ShouldThrow() {
        // Given
        Integer reviewId = 999;
        when(performanceReviewRepository.findById(reviewId)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException ex = assertThrows(RuntimeException.class, () -> performanceReviewService.findById(reviewId));
        assertEquals("Performance review not found", ex.getMessage());
        verify(performanceReviewRepository).findById(reviewId);
        verify(performanceReviewMapper, never()).toDto(any());
    }

    @Test
    void findByDate_ShouldReturnListOfDtos() {
        // Given
        LocalDate date = LocalDate.of(2026, 1, 15);
        PerformanceReview review1 = new PerformanceReview();
        review1.setId(1);
        review1.setReviewDate(date);
        PerformanceReview review2 = new PerformanceReview();
        review2.setId(2);
        review2.setReviewDate(date);

        List<PerformanceReview> reviews = List.of(review1, review2);
        PerformanceReviewDto dto1 = new PerformanceReviewDto(1, null, null, null, date, date.atStartOfDay(), List.of(), null, null, null);
        PerformanceReviewDto dto2 = new PerformanceReviewDto(2, null, null, null, date, date.atStartOfDay(), List.of(), null, null, null);
        List<PerformanceReviewDto> expectedDtos = List.of(dto1, dto2);

        when(performanceReviewRepository.findAllByReviewDate(date)).thenReturn(reviews);
        when(performanceReviewMapper.toDtos(reviews)).thenReturn(expectedDtos);

        // When
        List<PerformanceReviewDto> result = performanceReviewService.findByDate(date);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedDtos, result);
        verify(performanceReviewRepository).findAllByReviewDate(date);
        verify(performanceReviewMapper).toDtos(reviews);
    }

    @Test
    void findByDateRange_ShouldReturnListOfDtos() {
        // Given
        LocalDate from = LocalDate.of(2026, 1, 1);
        LocalDate to = LocalDate.of(2026, 1, 31);
        PerformanceReview review1 = new PerformanceReview();
        review1.setId(1);
        review1.setReviewDate(LocalDate.of(2026, 1, 15));
        PerformanceReview review2 = new PerformanceReview();
        review2.setId(2);
        review2.setReviewDate(LocalDate.of(2026, 1, 20));

        List<PerformanceReview> reviews = List.of(review1, review2);
        PerformanceReviewDto dto1 = new PerformanceReviewDto(1, null, null, null, LocalDate.of(2026, 1, 15), LocalDate.of(2026, 1, 15).atStartOfDay(), List.of(), null, null, null);
        PerformanceReviewDto dto2 = new PerformanceReviewDto(2, null, null, null, LocalDate.of(2026, 1, 20), LocalDate.of(2026, 1, 20).atStartOfDay(), List.of(), null, null, null);
        List<PerformanceReviewDto> expectedDtos = List.of(dto1, dto2);

        when(performanceReviewRepository.findAllByReviewDateBetween(from, to)).thenReturn(reviews);
        when(performanceReviewMapper.toDtos(reviews)).thenReturn(expectedDtos);

        // When
        List<PerformanceReviewDto> result = performanceReviewService.findByDateRange(from, to);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedDtos, result);
        verify(performanceReviewRepository).findAllByReviewDateBetween(from, to);
        verify(performanceReviewMapper).toDtos(reviews);
    }

    @Test
    void findByEmployee_ShouldReturnListOfDtos() {
        // Given
        Integer employeeId = 100;
        PerformanceReview review1 = new PerformanceReview();
        review1.setId(1);
        review1.setRefersTo(employee);
        PerformanceReview review2 = new PerformanceReview();
        review2.setId(2);
        review2.setRefersTo(employee);

        List<PerformanceReview> reviews = List.of(review1, review2);
        PerformanceReviewDto dto1 = new PerformanceReviewDto(1, null, null, null, null, null, List.of(), null, null, null);
        PerformanceReviewDto dto2 = new PerformanceReviewDto(2, null, null, null, null, null, List.of(), null, null, null);
        List<PerformanceReviewDto> expectedDtos = List.of(dto1, dto2);

        when(performanceReviewRepository.findAllByRefersToId(employeeId)).thenReturn(reviews);
        when(performanceReviewMapper.toDtos(reviews)).thenReturn(expectedDtos);

        // When
        List<PerformanceReviewDto> result = performanceReviewService.findByEmployee(employeeId);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedDtos, result);
        verify(performanceReviewRepository).findAllByRefersToId(employeeId);
        verify(performanceReviewMapper).toDtos(reviews);
    }

    @Test
    void findByReviewer_ShouldReturnListOfDtos() {
        // Given
        Integer reporterId = 200;
        PerformanceReview review1 = new PerformanceReview();
        review1.setId(1);
        review1.setReportedBy(reporter);
        PerformanceReview review2 = new PerformanceReview();
        review2.setId(2);
        review2.setReportedBy(reporter);

        List<PerformanceReview> reviews = List.of(review1, review2);
        PerformanceReviewDto dto1 = new PerformanceReviewDto(1, null, null, null, null, null, List.of(), null, null, null);
        PerformanceReviewDto dto2 = new PerformanceReviewDto(2, null, null, null, null, null, List.of(), null, null, null);
        List<PerformanceReviewDto> expectedDtos = List.of(dto1, dto2);

        when(performanceReviewRepository.findAllByReportedById(reporterId)).thenReturn(reviews);
        when(performanceReviewMapper.toDtos(reviews)).thenReturn(expectedDtos);

        // When
        List<PerformanceReviewDto> result = performanceReviewService.findByReviewer(reporterId);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedDtos, result);
        verify(performanceReviewRepository).findAllByReportedById(reporterId);
        verify(performanceReviewMapper).toDtos(reviews);
    }

    @Test
    void findByDepartment_ShouldReturnListOfDtos() {
        // Given
        Integer departmentId = 10;
        PerformanceReview review1 = new PerformanceReview();
        review1.setId(1);
        review1.setDepartment(department);
        PerformanceReview review2 = new PerformanceReview();
        review2.setId(2);
        review2.setDepartment(department);

        List<PerformanceReview> reviews = List.of(review1, review2);
        PerformanceReviewDto dto1 = new PerformanceReviewDto(1, null, null, null, null, null, List.of(), null, null, null);
        PerformanceReviewDto dto2 = new PerformanceReviewDto(2, null, null, null, null, null, List.of(), null, null, null);
        List<PerformanceReviewDto> expectedDtos = List.of(dto1, dto2);

        when(performanceReviewRepository.findAllByDepartmentId(departmentId)).thenReturn(reviews);
        when(performanceReviewMapper.toDtos(reviews)).thenReturn(expectedDtos);

        // When
        List<PerformanceReviewDto> result = performanceReviewService.findByDepartment(departmentId);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedDtos, result);
        verify(performanceReviewRepository).findAllByDepartmentId(departmentId);
        verify(performanceReviewMapper).toDtos(reviews);
    }

    @Test
    void findByOrganization_ShouldReturnListOfDtos() {
        // Given
        Integer organizationId = 1;
        PerformanceReview review1 = new PerformanceReview();
        review1.setId(1);
        review1.setDepartment(department);
        PerformanceReview review2 = new PerformanceReview();
        review2.setId(2);
        review2.setDepartment(department);

        List<PerformanceReview> reviews = List.of(review1, review2);
        PerformanceReviewDto dto1 = new PerformanceReviewDto(1, null, null, null, null, null, List.of(), null, null, null);
        PerformanceReviewDto dto2 = new PerformanceReviewDto(2, null, null, null, null, null, List.of(), null, null, null);
        List<PerformanceReviewDto> expectedDtos = List.of(dto1, dto2);

        when(performanceReviewRepository.findAllByDepartmentOrganizationIdOrderByReviewDateTimeDesc(organizationId)).thenReturn(reviews);
        when(performanceReviewMapper.toDtos(reviews)).thenReturn(expectedDtos);

        // When
        List<PerformanceReviewDto> result = performanceReviewService.findByOrganization(organizationId);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedDtos, result);
        verify(performanceReviewRepository).findAllByDepartmentOrganizationIdOrderByReviewDateTimeDesc(organizationId);
        verify(performanceReviewMapper).toDtos(reviews);
    }

    @Test
    void findBySkill_ShouldReturnListOfDtos() {
        // Given
        Integer skillId = 1;
        PerformanceReview review1 = new PerformanceReview();
        review1.setId(1);
        PerformanceReview review2 = new PerformanceReview();
        review2.setId(2);

        List<PerformanceReview> reviews = List.of(review1, review2);
        PerformanceReviewDto dto1 = new PerformanceReviewDto(1, null, null, null, null, null, List.of(), null, null, null);
        PerformanceReviewDto dto2 = new PerformanceReviewDto(2, null, null, null, null, null, List.of(), null, null, null);
        List<PerformanceReviewDto> expectedDtos = List.of(dto1, dto2);

        when(performanceReviewRepository.findAllBySkillId(skillId)).thenReturn(reviews);
        when(performanceReviewMapper.toDtos(reviews)).thenReturn(expectedDtos);

        // When
        List<PerformanceReviewDto> result = performanceReviewService.findBySkill(skillId);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedDtos, result);
        verify(performanceReviewRepository).findAllBySkillId(skillId);
        verify(performanceReviewMapper).toDtos(reviews);
    }

    @Test
    void findByOccupation_ShouldReturnListOfDtos() {
        // Given
        Integer occupationId = 1;
        PerformanceReview review1 = new PerformanceReview();
        review1.setId(1);
        review1.setRefersTo(employee);
        PerformanceReview review2 = new PerformanceReview();
        review2.setId(2);
        review2.setRefersTo(employee);

        List<PerformanceReview> reviews = List.of(review1, review2);
        PerformanceReviewDto dto1 = new PerformanceReviewDto(1, null, null, null, null, null, List.of(), null, null, null);
        PerformanceReviewDto dto2 = new PerformanceReviewDto(2, null, null, null, null, null, List.of(), null, null, null);
        List<PerformanceReviewDto> expectedDtos = List.of(dto1, dto2);

        when(performanceReviewRepository.findAllByRefersToOccupationId(occupationId)).thenReturn(reviews);
        when(performanceReviewMapper.toDtos(reviews)).thenReturn(expectedDtos);

        // When
        List<PerformanceReviewDto> result = performanceReviewService.findByOccupation(occupationId);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedDtos, result);
        verify(performanceReviewRepository).findAllByRefersToOccupationId(occupationId);
        verify(performanceReviewMapper).toDtos(reviews);
    }

    @Test
    void deletePerformanceReview_ShouldCallRepositoryDelete() {
        // Given
        Integer reviewId = 999;

        // When
        performanceReviewService.deletePerformanceReview(reviewId);

        // Then
        verify(performanceReviewRepository).deleteById(reviewId);
    }

    @Test
    void addSkillEntryToReview_WhenEntryDateNull_ShouldUseReviewDate() {
        // Given
        Integer reviewId = 10;
        LocalDate reviewDate = LocalDate.of(2026, 1, 15);
        PerformanceReview review = new PerformanceReview();
        review.setId(reviewId);
        review.setReviewDate(reviewDate);
        review.setRefersTo(employee);
        review.setSkillEntries(new ArrayList<>());

        SaveSkillEntryDto dto = new SaveSkillEntryDto(skill1.getId(), 3.5, null);

        when(performanceReviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
        when(skillRepository.findById(skill1.getId())).thenReturn(Optional.of(skill1));
        when(performanceReviewRepository.save(any(PerformanceReview.class))).thenAnswer(inv -> inv.getArgument(0));
        when(performanceReviewMapper.toDto(any(PerformanceReview.class))).thenReturn(
                new PerformanceReviewDto(reviewId, null, null, null, reviewDate, reviewDate.atStartOfDay(), List.of(), null, null, null)
        );

        // When
        performanceReviewService.addSkillEntryToReview(reviewId, dto);

        // Then
        ArgumentCaptor<PerformanceReview> captor = ArgumentCaptor.forClass(PerformanceReview.class);
        verify(performanceReviewRepository).save(captor.capture());
        PerformanceReview saved = captor.getValue();
        assertNotNull(saved.getSkillEntries());
        assertEquals(1, saved.getSkillEntries().size());
        SkillEntry entry = saved.getSkillEntries().getFirst();
        assertEquals(skill1, entry.getSkill());
        assertEquals(3.5, entry.getRating());
        assertEquals(reviewDate, entry.getEntryDate());
        assertEquals(reviewDate.atStartOfDay(), entry.getEntryDateTime());
        assertEquals(employee, entry.getEmployee());
    }

    @Test
    void addSkillEntryToReview_WhenEntryDateProvided_ShouldUseProvidedDate() {
        // Given
        Integer reviewId = 10;
        LocalDate reviewDate = LocalDate.of(2026, 1, 15);
        LocalDate entryDate = LocalDate.of(2026, 1, 20);
        PerformanceReview review = new PerformanceReview();
        review.setId(reviewId);
        review.setReviewDate(reviewDate);
        review.setRefersTo(employee);
        review.setSkillEntries(new ArrayList<>());

        SaveSkillEntryDto dto = new SaveSkillEntryDto(skill1.getId(), 3.5, entryDate);

        when(performanceReviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
        when(skillRepository.findById(skill1.getId())).thenReturn(Optional.of(skill1));
        when(performanceReviewRepository.save(any(PerformanceReview.class))).thenAnswer(inv -> inv.getArgument(0));
        when(performanceReviewMapper.toDto(any(PerformanceReview.class))).thenReturn(
                new PerformanceReviewDto(reviewId, null, null, null, reviewDate, reviewDate.atStartOfDay(), List.of(), null, null, null)
        );

        // When
        performanceReviewService.addSkillEntryToReview(reviewId, dto);

        // Then
        ArgumentCaptor<PerformanceReview> captor = ArgumentCaptor.forClass(PerformanceReview.class);
        verify(performanceReviewRepository).save(captor.capture());
        PerformanceReview saved = captor.getValue();
        SkillEntry entry = saved.getSkillEntries().getFirst();
        assertEquals(entryDate, entry.getEntryDate());
        assertEquals(entryDate.atStartOfDay(), entry.getEntryDateTime());
    }

    @Test
    void addSkillEntryToReview_WhenReviewDateNull_ShouldUseCurrentDate() {
        // Given
        Integer reviewId = 10;
        PerformanceReview review = new PerformanceReview();
        review.setId(reviewId);
        review.setReviewDate(null);
        review.setRefersTo(employee);
        review.setSkillEntries(new ArrayList<>());

        SaveSkillEntryDto dto = new SaveSkillEntryDto(skill1.getId(), 3.5, null);

        when(performanceReviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
        when(skillRepository.findById(skill1.getId())).thenReturn(Optional.of(skill1));
        when(performanceReviewRepository.save(any(PerformanceReview.class))).thenAnswer(inv -> inv.getArgument(0));
        when(performanceReviewMapper.toDto(any(PerformanceReview.class))).thenReturn(
                new PerformanceReviewDto(reviewId, null, null, null, null, null, List.of(), null, null, null)
        );

        // When
        performanceReviewService.addSkillEntryToReview(reviewId, dto);

        // Then
        ArgumentCaptor<PerformanceReview> captor = ArgumentCaptor.forClass(PerformanceReview.class);
        verify(performanceReviewRepository).save(captor.capture());
        PerformanceReview saved = captor.getValue();
        SkillEntry entry = saved.getSkillEntries().getFirst();
        assertNotNull(entry.getEntryDate());
        assertEquals(entry.getEntryDate().atStartOfDay(), entry.getEntryDateTime());
    }

    @Test
    void addSkillEntryToReview_WhenSkillNotFound_ShouldThrow() {
        // Given
        Integer reviewId = 10;
        PerformanceReview review = new PerformanceReview();
        review.setId(reviewId);
        review.setRefersTo(employee);
        review.setSkillEntries(new ArrayList<>());

        SaveSkillEntryDto dto = new SaveSkillEntryDto(999, 3.5, null);

        when(performanceReviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
        when(skillRepository.findById(999)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException ex = assertThrows(RuntimeException.class, () -> performanceReviewService.addSkillEntryToReview(reviewId, dto));
        assertEquals("Skill not found", ex.getMessage());
        verify(performanceReviewRepository, never()).save(any());
    }

    @Test
    void addSkillEntriesToReview_ShouldAddAllAndUseDefaultDateWhenMissing() {
        // Given
        Integer reviewId = 10;
        LocalDate reviewDate = LocalDate.of(2026, 1, 15);
        PerformanceReview review = new PerformanceReview();
        review.setId(reviewId);
        review.setReviewDate(reviewDate);
        review.setRefersTo(employee);
        review.setSkillEntries(new ArrayList<>());

        SaveSkillEntryDto dto1 = new SaveSkillEntryDto(skill1.getId(), 2.0, null);
        SaveSkillEntryDto dto2 = new SaveSkillEntryDto(skill2.getId(), 4.0, LocalDate.of(2026, 1, 16));

        when(performanceReviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
        when(skillRepository.findById(skill1.getId())).thenReturn(Optional.of(skill1));
        when(skillRepository.findById(skill2.getId())).thenReturn(Optional.of(skill2));
        when(performanceReviewRepository.save(any(PerformanceReview.class))).thenAnswer(inv -> inv.getArgument(0));
        when(performanceReviewMapper.toDto(any(PerformanceReview.class))).thenReturn(
                new PerformanceReviewDto(reviewId, null, null, null, reviewDate, reviewDate.atStartOfDay(), List.of(), null, null, null)
        );

        // When
        performanceReviewService.addSkillEntriesToReview(reviewId, List.of(dto1, dto2));

        // Then
        ArgumentCaptor<PerformanceReview> captor = ArgumentCaptor.forClass(PerformanceReview.class);
        verify(performanceReviewRepository).save(captor.capture());
        PerformanceReview saved = captor.getValue();
        assertEquals(2, saved.getSkillEntries().size());

        SkillEntry entry1 = saved.getSkillEntries().getFirst();
        assertEquals(skill1, entry1.getSkill());
        assertEquals(2.0, entry1.getRating());
        assertEquals(reviewDate, entry1.getEntryDate());
        assertEquals(reviewDate.atStartOfDay(), entry1.getEntryDateTime());

        SkillEntry entry2 = saved.getSkillEntries().get(1);
        assertEquals(skill2, entry2.getSkill());
        assertEquals(4.0, entry2.getRating());
        assertEquals(LocalDate.of(2026, 1, 16), entry2.getEntryDate());
        assertEquals(LocalDate.of(2026, 1, 16).atStartOfDay(), entry2.getEntryDateTime());
    }

    @Test
    void addSkillEntriesToReview_WhenSkillNotFound_ShouldThrow() {
        // Given
        Integer reviewId = 10;
        PerformanceReview review = new PerformanceReview();
        review.setId(reviewId);
        review.setRefersTo(employee);
        review.setSkillEntries(new ArrayList<>());

        SaveSkillEntryDto dto1 = new SaveSkillEntryDto(skill1.getId(), 2.0, null);
        SaveSkillEntryDto dto2 = new SaveSkillEntryDto(999, 4.0, null);

        when(performanceReviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
        when(skillRepository.findById(skill1.getId())).thenReturn(Optional.of(skill1));
        when(skillRepository.findById(999)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException ex = assertThrows(RuntimeException.class, () -> 
                performanceReviewService.addSkillEntriesToReview(reviewId, List.of(dto1, dto2)));
        assertEquals("Skill not found with id: 999", ex.getMessage());
        verify(performanceReviewRepository, never()).save(any());
    }

    @Test
    void updateSkillEntryInReview_WhenEntryExists_ShouldUpdateAndNotSaveReview() {
        // Given
        Integer reviewId = 10;
        Integer entryId = 77;

        Skill oldSkill = new Skill();
        oldSkill.setId(123);
        oldSkill.setName("Old");

        SkillEntry existingEntry = new SkillEntry();
        existingEntry.setId(entryId);
        existingEntry.setSkill(oldSkill);
        existingEntry.setRating(1.0);

        PerformanceReview review = new PerformanceReview();
        review.setId(reviewId);
        review.setReviewDate(LocalDate.of(2026, 1, 15));
        review.setSkillEntries(new ArrayList<>(List.of(existingEntry)));

        SaveSkillEntryDto dto = new SaveSkillEntryDto(skill2.getId(), 4.25, LocalDate.of(2026, 1, 20));

        PerformanceReviewDto mapped = new PerformanceReviewDto(
                reviewId,
                null,
                null,
                null,
                review.getReviewDate(),
                review.getReviewDate().atStartOfDay(),
                List.of(),
                null,
                null,
                null
        );

        when(performanceReviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
        when(skillRepository.findById(skill2.getId())).thenReturn(Optional.of(skill2));
        when(performanceReviewMapper.toDto(review)).thenReturn(mapped);

        // When
        PerformanceReviewDto result = performanceReviewService.updateSkillEntryInReview(reviewId, entryId, dto);

        // Then
        assertEquals(mapped, result);
        assertEquals(skill2, existingEntry.getSkill());
        assertEquals(4.25, existingEntry.getRating());
        assertEquals(LocalDate.of(2026, 1, 20), existingEntry.getEntryDate());
        assertEquals(LocalDateTime.of(2026, 1, 20, 0, 0), existingEntry.getEntryDateTime());
        verify(performanceReviewRepository, never()).save(any());
    }

    @Test
    void updateSkillEntryInReview_WhenEntryNotFound_ShouldThrow() {
        // Given
        Integer reviewId = 10;
        Integer entryId = 999;

        PerformanceReview review = new PerformanceReview();
        review.setId(reviewId);
        review.setSkillEntries(new ArrayList<>());

        when(performanceReviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

        // When & Then
        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                performanceReviewService.updateSkillEntryInReview(reviewId, entryId, new SaveSkillEntryDto(skill1.getId(), 1.0, null))
        );
        assertEquals("Skill entry not found for this review", ex.getMessage());
        verify(performanceReviewMapper, never()).toDto(any());
    }

    @Test
    void updateSkillEntryInReview_WhenSkillIdNull_ShouldNotUpdateSkill() {
        // Given
        Integer reviewId = 10;
        Integer entryId = 77;

        Skill oldSkill = new Skill();
        oldSkill.setId(123);
        oldSkill.setName("Old");

        SkillEntry existingEntry = new SkillEntry();
        existingEntry.setId(entryId);
        existingEntry.setSkill(oldSkill);
        existingEntry.setRating(1.0);
        existingEntry.setEntryDate(LocalDate.of(2026, 1, 15));

        PerformanceReview review = new PerformanceReview();
        review.setId(reviewId);
        review.setReviewDate(LocalDate.of(2026, 1, 15));
        review.setSkillEntries(new ArrayList<>(List.of(existingEntry)));

        SaveSkillEntryDto dto = new SaveSkillEntryDto(null, 4.25, LocalDate.of(2026, 1, 20));

        PerformanceReviewDto mapped = new PerformanceReviewDto(
                reviewId,
                null,
                null,
                null,
                review.getReviewDate(),
                review.getReviewDate().atStartOfDay(),
                List.of(),
                null,
                null,
                null
        );

        when(performanceReviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
        when(performanceReviewMapper.toDto(review)).thenReturn(mapped);

        // When
        performanceReviewService.updateSkillEntryInReview(reviewId, entryId, dto);

        // Then
        assertEquals(oldSkill, existingEntry.getSkill());
        assertEquals(4.25, existingEntry.getRating());
        assertEquals(LocalDate.of(2026, 1, 20), existingEntry.getEntryDate());
        verify(skillRepository, never()).findById(anyInt());
    }

    @Test
    void updateSkillEntryInReview_WhenRatingNull_ShouldNotUpdateRating() {
        // Given
        Integer reviewId = 10;
        Integer entryId = 77;

        Skill oldSkill = new Skill();
        oldSkill.setId(123);
        oldSkill.setName("Old");

        SkillEntry existingEntry = new SkillEntry();
        existingEntry.setId(entryId);
        existingEntry.setSkill(oldSkill);
        existingEntry.setRating(1.0);
        existingEntry.setEntryDate(LocalDate.of(2026, 1, 15));

        PerformanceReview review = new PerformanceReview();
        review.setId(reviewId);
        review.setReviewDate(LocalDate.of(2026, 1, 15));
        review.setSkillEntries(new ArrayList<>(List.of(existingEntry)));

        SaveSkillEntryDto dto = new SaveSkillEntryDto(skill2.getId(), null, LocalDate.of(2026, 1, 20));

        PerformanceReviewDto mapped = new PerformanceReviewDto(
                reviewId,
                null,
                null,
                null,
                review.getReviewDate(),
                review.getReviewDate().atStartOfDay(),
                List.of(),
                null,
                null,
                null
        );

        when(performanceReviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
        when(skillRepository.findById(skill2.getId())).thenReturn(Optional.of(skill2));
        when(performanceReviewMapper.toDto(review)).thenReturn(mapped);

        // When
        performanceReviewService.updateSkillEntryInReview(reviewId, entryId, dto);

        // Then
        assertEquals(skill2, existingEntry.getSkill());
        assertEquals(1.0, existingEntry.getRating());
        assertEquals(LocalDate.of(2026, 1, 20), existingEntry.getEntryDate());
    }

    @Test
    void removeSkillEntryFromReview_WhenExists_ShouldRemoveAndSave() {
        // Given
        Integer reviewId = 10;
        Integer entryId = 5;

        SkillEntry entry = new SkillEntry();
        entry.setId(entryId);
        entry.setSkill(skill1);

        PerformanceReview review = new PerformanceReview();
        review.setId(reviewId);
        review.setSkillEntries(new ArrayList<>(List.of(entry)));

        when(performanceReviewRepository.findById(reviewId)).thenReturn(Optional.of(review));
        when(performanceReviewRepository.save(any(PerformanceReview.class))).thenAnswer(inv -> inv.getArgument(0));
        when(performanceReviewMapper.toDto(any(PerformanceReview.class))).thenReturn(
                new PerformanceReviewDto(reviewId, null, null, null, null, null, List.of(), null, null, null)
        );

        // When
        performanceReviewService.removeSkillEntryFromReview(reviewId, entryId);

        // Then
        ArgumentCaptor<PerformanceReview> captor = ArgumentCaptor.forClass(PerformanceReview.class);
        verify(performanceReviewRepository).save(captor.capture());
        assertTrue(captor.getValue().getSkillEntries().isEmpty());
    }

    @Test
    void removeSkillEntryFromReview_WhenMissing_ShouldThrowAndNotSave() {
        // Given
        Integer reviewId = 10;
        PerformanceReview review = new PerformanceReview();
        review.setId(reviewId);
        review.setSkillEntries(new ArrayList<>());
        when(performanceReviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

        // When & Then
        RuntimeException ex = assertThrows(RuntimeException.class, () -> performanceReviewService.removeSkillEntryFromReview(reviewId, 999));
        assertEquals("Skill entry not found for this review", ex.getMessage());
        verify(performanceReviewRepository, never()).save(any());
    }

    @Test
    void generateSkillEntries_WhenSkillsGenerated_ShouldMapAndFilterMissingSkills() {
        // Given
        GeneratedSkill g1 = new GeneratedSkill();
        g1.setEscoSkillId("ESCO-1");
        g1.setSkillName("Communication");
        g1.setRating(3.0);

        GeneratedSkill g2 = new GeneratedSkill();
        g2.setEscoSkillId("ESCO-UNKNOWN");
        g2.setSkillName("Unknown");
        g2.setRating(1.0);

        when(chatGptClient.analyzePerformanceReview("text")).thenReturn(List.of(g1, g2));
        when(skillRepository.findByEscoId("ESCO-1")).thenReturn(skill1);
        when(skillRepository.findByEscoId("ESCO-UNKNOWN")).thenReturn(null);

        // When
        List<GeneratedSkillEntryDto> result = performanceReviewService.generateSkillEntries("text");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(new GeneratedSkillEntryDto(skill1.getId(), skill1.getName(), 3.0), result.getFirst());
    }

    @Test
    void generateSkillEntries_WhenNoSkillsGenerated_ShouldReturnEmptyAndNotLookupSkills() {
        // Given
        when(chatGptClient.analyzePerformanceReview("text")).thenReturn(List.of());

        // When
        List<GeneratedSkillEntryDto> result = performanceReviewService.generateSkillEntries("text");

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(skillRepository, never()).findByEscoId(anyString());
    }

    @Test
    void generateSkillEntries_WhenNullReturned_ShouldReturnEmpty() {
        // Given
        when(chatGptClient.analyzePerformanceReview("text")).thenReturn(null);

        // When
        List<GeneratedSkillEntryDto> result = performanceReviewService.generateSkillEntries("text");

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(skillRepository, never()).findByEscoId(anyString());
    }
}


