package gr.uom.employeepulseservice.service;

import gr.uom.employeepulseservice.controller.dto.SaveSkillDto;
import gr.uom.employeepulseservice.controller.dto.SkillDto;
import gr.uom.employeepulseservice.mapper.SkillMapper;
import gr.uom.employeepulseservice.model.Skill;
import gr.uom.employeepulseservice.repository.SkillRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SkillServiceTest {

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private SkillMapper skillMapper;

    @InjectMocks
    private SkillService skillService;

    private Skill skill;
    private SkillDto skillDto;
    private SaveSkillDto saveSkillDto;

    @BeforeEach
    void setUp() {
        skill = new Skill();
        skill.setId(1);
        skill.setName("Java");
        skill.setDescription("Programming language");
        skill.setEscoId("ESCO-123");

        skillDto = new SkillDto(1, "Java", "Programming language", "ESCO-123");
        saveSkillDto = new SaveSkillDto("Java", "Programming language", "ESCO-123");
    }

    @Test
    void findAll_ShouldReturnPageOfSkillDtos() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<Skill> skills = Collections.singletonList(skill);
        Page<Skill> skillPage = new PageImpl<>(skills, pageable, 1);

        when(skillRepository.findAll(pageable)).thenReturn(skillPage);
        when(skillMapper.toDto(skill)).thenReturn(skillDto);

        // When
        Page<SkillDto> result = skillService.findAll(pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(skillDto, result.getContent().getFirst());
        verify(skillRepository).findAll(pageable);
        verify(skillMapper).toDto(skill);
    }

    @Test
    void findSkillById_WhenSkillExists_ShouldReturnSkillDto() {
        // Given
        when(skillRepository.findById(1)).thenReturn(Optional.of(skill));
        when(skillMapper.toDto(skill)).thenReturn(skillDto);

        // When
        SkillDto result = skillService.findSkillById(1);

        // Then
        assertNotNull(result);
        assertEquals(skillDto, result);
        verify(skillRepository).findById(1);
        verify(skillMapper).toDto(skill);
    }

    @Test
    void findSkillById_WhenSkillNotFound_ShouldThrowRuntimeException() {
        // Given
        when(skillRepository.findById(999)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                skillService.findSkillById(999));

        assertEquals("Skill not found", exception.getMessage());
        verify(skillRepository).findById(999);
        verify(skillMapper, never()).toDto(any());
    }

    @Test
    void createSkill_WhenValid_ShouldSaveSkill() {
        // Given
        Skill newSkill = new Skill();
        newSkill.setName("Java");
        newSkill.setDescription("Programming language");
        newSkill.setEscoId("ESCO-123");

        when(skillMapper.toEntity(saveSkillDto)).thenReturn(newSkill);
        when(skillRepository.save(any(Skill.class))).thenReturn(newSkill);

        // When
        skillService.createSkill(saveSkillDto);

        // Then
        verify(skillMapper).toEntity(saveSkillDto);
        verify(skillRepository).save(newSkill);
    }

    @Test
    void updateSkill_WhenSkillExists_ShouldUpdateSkill() {
        // Given
        SaveSkillDto updateDto = new SaveSkillDto("Advanced Java", "Advanced concepts", "ESCO-456");
        when(skillRepository.findById(1)).thenReturn(Optional.of(skill));

        // When
        skillService.updateSkill(1, updateDto);

        // Then
        verify(skillRepository).findById(1);
        verify(skillMapper).updateFromDto(skill, updateDto);
    }

    @Test
    void updateSkill_WhenSkillNotFound_ShouldThrowRuntimeException() {
        // Given
        SaveSkillDto updateDto = new SaveSkillDto("Advanced Java", "Advanced concepts", "ESCO-456");
        when(skillRepository.findById(999)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                skillService.updateSkill(999, updateDto));

        assertEquals("Skill not found", exception.getMessage());
        verify(skillRepository).findById(999);
        verify(skillMapper, never()).updateFromDto(any(), any());
    }

    @Test
    void deleteSkill_ShouldDeleteSkill() {
        // When
        skillService.deleteSkill(1);

        // Then
        verify(skillRepository).deleteById(1);
    }

    @Test
    void bulkCreateSkills_WhenValidJson_ShouldSaveAllSkills() {
        // Given
        String json = "[{\"name\":\"Java\",\"description\":\"Programming language\",\"escoId\":\"ESCO-1\"}," +
                "{\"name\":\"Python\",\"description\":\"Programming language\",\"escoId\":\"ESCO-2\"}]";

        SaveSkillDto dto1 = new SaveSkillDto("Java", "Programming language", "ESCO-1");
        SaveSkillDto dto2 = new SaveSkillDto("Python", "Programming language", "ESCO-2");

        Skill skill1 = new Skill();
        skill1.setName("Java");
        Skill skill2 = new Skill();
        skill2.setName("Python");

        when(skillMapper.toEntity(dto1)).thenReturn(skill1);
        when(skillMapper.toEntity(dto2)).thenReturn(skill2);
        when(skillRepository.saveAll(anyList())).thenReturn(List.of(skill1, skill2));

        // When
        skillService.bulkCreateSkills(json);

        // Then
        verify(skillRepository).saveAll(anyList());
        verify(skillMapper, times(2)).toEntity(any(SaveSkillDto.class));
    }

    @Test
    void bulkCreateSkills_WhenEmptyJson_ShouldSaveEmptyList() {
        // Given
        String json = "[]";
        when(skillRepository.saveAll(anyList())).thenReturn(Collections.emptyList());

        // When
        skillService.bulkCreateSkills(json);

        // Then
        verify(skillRepository).saveAll(anyList());
        verify(skillMapper, never()).toEntity(any());
    }

    @Test
    void findByOrganizationId_WhenSkillsExist_ShouldReturnListOfSkillDtos() {
        // Given
        List<Skill> skills = Collections.singletonList(skill);
        List<SkillDto> expectedDtos = Collections.singletonList(skillDto);

        when(skillRepository.findSkillsByOrganizationId(1)).thenReturn(skills);
        when(skillMapper.toDtos(skills)).thenReturn(expectedDtos);

        // When
        List<SkillDto> result = skillService.findByOrganizationId(1);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(expectedDtos, result);
        verify(skillRepository).findSkillsByOrganizationId(1);
        verify(skillMapper).toDtos(skills);
    }

    @Test
    void findByDepartmentId_WhenSkillsExist_ShouldReturnListOfSkillDtos() {
        // Given
        List<Skill> skills = Collections.singletonList(skill);
        List<SkillDto> expectedDtos = Collections.singletonList(skillDto);

        when(skillRepository.findSkillsByDepartmentId(1)).thenReturn(skills);
        when(skillMapper.toDtos(skills)).thenReturn(expectedDtos);

        // When
        List<SkillDto> result = skillService.findByDepartmentId(1);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(expectedDtos, result);
        verify(skillRepository).findSkillsByDepartmentId(1);
        verify(skillMapper).toDtos(skills);
    }

    @Test
    void searchSkills_WhenSearchTermProvided_ShouldReturnMatchingSkills() {
        // Given
        String searchTerm = "java";
        List<Skill> skills = Collections.singletonList(skill);
        List<SkillDto> expectedDtos = Collections.singletonList(skillDto);

        when(skillRepository.searchSkills(searchTerm)).thenReturn(skills);
        when(skillMapper.toDtos(skills)).thenReturn(expectedDtos);

        // When
        List<SkillDto> result = skillService.searchSkills(searchTerm);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(expectedDtos, result);
        verify(skillRepository).searchSkills(searchTerm);
        verify(skillMapper).toDtos(skills);
    }

    @Test
    void searchSkills_WhenSearchTermIsNull_ShouldReturnEmptyList() {
        // When
        List<SkillDto> result = skillService.searchSkills(null);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(skillRepository, never()).searchSkills(anyString());
        verify(skillMapper, never()).toDtos(any());
    }

    @Test
    void searchSkills_WhenSearchTermIsEmpty_ShouldReturnEmptyList() {
        // When
        List<SkillDto> result = skillService.searchSkills("");

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(skillRepository, never()).searchSkills(anyString());
        verify(skillMapper, never()).toDtos(any());
    }

    @Test
    void searchSkills_WhenSearchTermIsWhitespace_ShouldReturnEmptyList() {
        // When
        List<SkillDto> result = skillService.searchSkills("   ");

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(skillRepository, never()).searchSkills(anyString());
        verify(skillMapper, never()).toDtos(any());
    }

    @Test
    void searchSkills_WhenSearchTermIsNumericId_ShouldReturnSkillByIdFirst() {
        // Given
        String searchTerm = "1";
        List<Skill> searchResults = new ArrayList<>();
        List<SkillDto> expectedDtos = Collections.singletonList(skillDto);

        when(skillRepository.searchSkills(searchTerm)).thenReturn(searchResults);
        when(skillRepository.findById(1)).thenReturn(Optional.of(skill));
        when(skillMapper.toDtos(anyList())).thenReturn(expectedDtos);

        // When
        List<SkillDto> result = skillService.searchSkills(searchTerm);

        // Then
        assertNotNull(result);
        verify(skillRepository).searchSkills(searchTerm);
        verify(skillRepository).findById(1);

        ArgumentCaptor<List<Skill>> captor = ArgumentCaptor.forClass(List.class);
        verify(skillMapper).toDtos(captor.capture());

        List<Skill> passedToMapper = captor.getValue();
        assertEquals(1, passedToMapper.size());
        assertEquals(skill, passedToMapper.getFirst());
    }

    @Test
    void searchSkills_WhenSearchTermIsNumericIdAndAlreadyInResults_ShouldNotDuplicate() {
        // Given
        String searchTerm = "1";
        List<Skill> searchResults = new ArrayList<>(Collections.singletonList(skill));
        List<SkillDto> expectedDtos = Collections.singletonList(skillDto);

        when(skillRepository.searchSkills(searchTerm)).thenReturn(searchResults);
        when(skillRepository.findById(1)).thenReturn(Optional.of(skill));
        when(skillMapper.toDtos(anyList())).thenReturn(expectedDtos);

        // When
        List<SkillDto> result = skillService.searchSkills(searchTerm);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(skillRepository).searchSkills(searchTerm);
        verify(skillRepository).findById(1);

        ArgumentCaptor<List<Skill>> captor = ArgumentCaptor.forClass(List.class);
        verify(skillMapper).toDtos(captor.capture());
        assertEquals(1, captor.getValue().size());
    }

    @Test
    void searchSkills_WhenSearchTermIsNumericIdButNotFound_ShouldReturnSearchResultsOnly() {
        // Given
        String searchTerm = "999";
        List<Skill> searchResults = new ArrayList<>(Collections.singletonList(skill));
        List<SkillDto> expectedDtos = Collections.singletonList(skillDto);

        when(skillRepository.searchSkills(searchTerm)).thenReturn(searchResults);
        when(skillRepository.findById(999)).thenReturn(Optional.empty());
        when(skillMapper.toDtos(searchResults)).thenReturn(expectedDtos);

        // When
        List<SkillDto> result = skillService.searchSkills(searchTerm);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(skillRepository).searchSkills(searchTerm);
        verify(skillRepository).findById(999);
        verify(skillMapper).toDtos(searchResults);
    }

    @Test
    void searchSkills_WhenSearchTermHasLeadingTrailingWhitespace_ShouldTrimAndSearch() {
        // Given
        String searchTerm = "  java  ";
        String trimmedTerm = "java";
        List<Skill> skills = Collections.singletonList(skill);
        List<SkillDto> expectedDtos = Collections.singletonList(skillDto);

        when(skillRepository.searchSkills(trimmedTerm)).thenReturn(skills);
        when(skillMapper.toDtos(skills)).thenReturn(expectedDtos);

        // When
        List<SkillDto> result = skillService.searchSkills(searchTerm);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(skillRepository).searchSkills(trimmedTerm);
        verify(skillMapper).toDtos(skills);
    }
}




