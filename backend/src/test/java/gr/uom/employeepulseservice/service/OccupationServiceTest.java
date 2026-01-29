package gr.uom.employeepulseservice.service;

import gr.uom.employeepulseservice.controller.dto.OccupationDto;
import gr.uom.employeepulseservice.controller.dto.SaveOccupationDto;
import gr.uom.employeepulseservice.mapper.OccupationMapper;
import gr.uom.employeepulseservice.model.Occupation;
import gr.uom.employeepulseservice.repository.OccupationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OccupationServiceTest {

    @Mock
    private OccupationRepository occupationRepository;

    @Mock
    private OccupationMapper occupationMapper;

    @InjectMocks
    private OccupationService occupationService;

    private Occupation occupation;
    private OccupationDto occupationDto;
    private SaveOccupationDto saveOccupationDto;

    @BeforeEach
    void setUp() {
        occupation = new Occupation();
        occupation.setId(1);
        occupation.setTitle("Software Engineer");
        occupation.setDescription("Develops software applications");
        occupation.setEscoId("ESCO-123");

        occupationDto = new OccupationDto(1, "Software Engineer", "Develops software applications", "ESCO-123");

        saveOccupationDto = new SaveOccupationDto("Software Engineer", "Develops software applications", "ESCO-123");
    }

    @Test
    void findAll_ShouldReturnPageOfOccupationDtos() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);
        List<Occupation> occupations = Collections.singletonList(occupation);
        Page<Occupation> occupationPage = new PageImpl<>(occupations, pageable, 1);

        when(occupationRepository.findAll(pageable)).thenReturn(occupationPage);
        when(occupationMapper.toDto(occupation)).thenReturn(occupationDto);

        // When
        Page<OccupationDto> result = occupationService.findAll(pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(occupationDto, result.getContent().getFirst());
        verify(occupationRepository).findAll(pageable);
        verify(occupationMapper).toDto(occupation);
    }

    @Test
    void findOccupationById_WhenOccupationExists_ShouldReturnOccupationDto() {
        // Given
        when(occupationRepository.findById(1)).thenReturn(Optional.of(occupation));
        when(occupationMapper.toDto(occupation)).thenReturn(occupationDto);

        // When
        OccupationDto result = occupationService.findOccupationById(1);

        // Then
        assertNotNull(result);
        assertEquals(occupationDto, result);
        verify(occupationRepository).findById(1);
        verify(occupationMapper).toDto(occupation);
    }

    @Test
    void findOccupationById_WhenOccupationNotFound_ShouldThrowRuntimeException() {
        // Given
        when(occupationRepository.findById(999)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> 
            occupationService.findOccupationById(999));

        assertEquals("Occupation not found", exception.getMessage());
        verify(occupationRepository).findById(999);
        verify(occupationMapper, never()).toDto(any());
    }

    @Test
    void createOccupation_WhenValid_ShouldSaveOccupation() {
        // Given
        Occupation newOccupation = new Occupation();
        newOccupation.setTitle("Software Engineer");
        newOccupation.setDescription("Develops software applications");
        newOccupation.setEscoId("ESCO-123");

        when(occupationMapper.toEntity(saveOccupationDto)).thenReturn(newOccupation);
        when(occupationRepository.save(any(Occupation.class))).thenReturn(newOccupation);

        // When
        occupationService.createOccupation(saveOccupationDto);

        // Then
        verify(occupationMapper).toEntity(saveOccupationDto);
        verify(occupationRepository).save(newOccupation);
    }

    @Test
    void updateOccupation_WhenOccupationExists_ShouldUpdateOccupation() {
        // Given
        SaveOccupationDto updateDto = new SaveOccupationDto("Senior Software Engineer", 
            "Develops complex software applications", "ESCO-456");

        when(occupationRepository.findById(1)).thenReturn(Optional.of(occupation));

        // When
        occupationService.updateOccupation(1, updateDto);

        // Then
        verify(occupationRepository).findById(1);
        verify(occupationMapper).updateFromDto(occupation, updateDto);
    }

    @Test
    void updateOccupation_WhenOccupationNotFound_ShouldThrowRuntimeException() {
        // Given
        SaveOccupationDto updateDto = new SaveOccupationDto("Senior Software Engineer", 
            "Develops complex software applications", "ESCO-456");

        when(occupationRepository.findById(999)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> 
            occupationService.updateOccupation(999, updateDto));

        assertEquals("Occupation not found", exception.getMessage());
        verify(occupationRepository).findById(999);
        verify(occupationMapper, never()).updateFromDto(any(), any());
    }

    @Test
    void deleteOccupation_ShouldDeleteOccupation() {
        // When
        occupationService.deleteOccupation(1);

        // Then
        verify(occupationRepository).deleteById(1);
    }

    @Test
    void bulkCreateOccupations_WhenValidJson_ShouldSaveAllOccupations() {
        // Given
        String json = "[{\"title\":\"Software Engineer\",\"description\":\"Develops software\",\"escoId\":\"ESCO-1\"}," +
                      "{\"title\":\"Data Scientist\",\"description\":\"Analyzes data\",\"escoId\":\"ESCO-2\"}]";

        SaveOccupationDto dto1 = new SaveOccupationDto("Software Engineer", "Develops software", "ESCO-1");
        SaveOccupationDto dto2 = new SaveOccupationDto("Data Scientist", "Analyzes data", "ESCO-2");

        Occupation occupation1 = new Occupation();
        occupation1.setTitle("Software Engineer");
        Occupation occupation2 = new Occupation();
        occupation2.setTitle("Data Scientist");

        when(occupationMapper.toEntity(dto1)).thenReturn(occupation1);
        when(occupationMapper.toEntity(dto2)).thenReturn(occupation2);
        when(occupationRepository.saveAll(anyList())).thenReturn(List.of(occupation1, occupation2));

        // When
        occupationService.bulkCreateOccupations(json);

        // Then
        verify(occupationRepository).saveAll(anyList());
        verify(occupationMapper, times(2)).toEntity(any(SaveOccupationDto.class));
    }

    @Test
    void bulkCreateOccupations_WhenEmptyJson_ShouldSaveEmptyList() {
        // Given
        String json = "[]";

        when(occupationRepository.saveAll(anyList())).thenReturn(Collections.emptyList());

        // When
        occupationService.bulkCreateOccupations(json);

        // Then
        verify(occupationRepository).saveAll(anyList());
        verify(occupationMapper, never()).toEntity(any());
    }

    @Test
    void findByOrganizationId_WhenOccupationsExist_ShouldReturnListOfOccupationDtos() {
        // Given
        List<Occupation> occupations = Collections.singletonList(occupation);
        List<OccupationDto> expectedDtos = Collections.singletonList(occupationDto);

        when(occupationRepository.findOccupationsByOrganizationId(1)).thenReturn(occupations);
        when(occupationMapper.toDtos(occupations)).thenReturn(expectedDtos);

        // When
        List<OccupationDto> result = occupationService.findByOrganizationId(1);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(expectedDtos, result);
        verify(occupationRepository).findOccupationsByOrganizationId(1);
        verify(occupationMapper).toDtos(occupations);
    }

    @Test
    void findByOrganizationId_WhenNoOccupations_ShouldReturnEmptyList() {
        // Given
        when(occupationRepository.findOccupationsByOrganizationId(1)).thenReturn(Collections.emptyList());
        when(occupationMapper.toDtos(Collections.emptyList())).thenReturn(Collections.emptyList());

        // When
        List<OccupationDto> result = occupationService.findByOrganizationId(1);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(occupationRepository).findOccupationsByOrganizationId(1);
        verify(occupationMapper).toDtos(Collections.emptyList());
    }

    @Test
    void searchOccupations_WhenSearchTermProvided_ShouldReturnMatchingOccupations() {
        // Given
        String searchTerm = "engineer";
        List<Occupation> occupations = Collections.singletonList(occupation);
        List<OccupationDto> expectedDtos = Collections.singletonList(occupationDto);

        when(occupationRepository.searchOccupations(searchTerm)).thenReturn(occupations);
        when(occupationMapper.toDtos(occupations)).thenReturn(expectedDtos);

        // When
        List<OccupationDto> result = occupationService.searchOccupations(searchTerm);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(expectedDtos, result);
        verify(occupationRepository).searchOccupations(searchTerm);
        verify(occupationMapper).toDtos(occupations);
    }

    @Test
    void searchOccupations_WhenSearchTermIsNull_ShouldReturnEmptyList() {
        // When
        List<OccupationDto> result = occupationService.searchOccupations(null);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(occupationRepository, never()).searchOccupations(anyString());
        verify(occupationMapper, never()).toDtos(any());
    }

    @Test
    void searchOccupations_WhenSearchTermIsEmpty_ShouldReturnEmptyList() {
        // When
        List<OccupationDto> result = occupationService.searchOccupations("");

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(occupationRepository, never()).searchOccupations(anyString());
        verify(occupationMapper, never()).toDtos(any());
    }

    @Test
    void searchOccupations_WhenSearchTermIsWhitespace_ShouldReturnEmptyList() {
        // When
        List<OccupationDto> result = occupationService.searchOccupations("   ");

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(occupationRepository, never()).searchOccupations(anyString());
        verify(occupationMapper, never()).toDtos(any());
    }

    @Test
    void searchOccupations_WhenSearchTermIsNumericId_ShouldReturnOccupationById() {
        // Given
        String searchTerm = "1";
        List<Occupation> searchResults = new java.util.ArrayList<>();

        when(occupationRepository.searchOccupations(searchTerm)).thenReturn(searchResults);
        when(occupationRepository.findById(1)).thenReturn(Optional.of(occupation));
        when(occupationMapper.toDtos(anyList())).thenAnswer(invocation -> {
            List<Occupation> occupations = invocation.getArgument(0);
            return occupations.stream()
                    .map(occ -> occupationDto)
                    .toList();
        });

        // When
        List<OccupationDto> result = occupationService.searchOccupations(searchTerm);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(occupationRepository).searchOccupations(searchTerm);
        verify(occupationRepository).findById(1);
        verify(occupationMapper).toDtos(anyList());
    }

    @Test
    void searchOccupations_WhenSearchTermIsNumericIdAndAlreadyInResults_ShouldNotDuplicate() {
        // Given
        String searchTerm = "1";
        List<Occupation> searchResults = Collections.singletonList(occupation);
        List<OccupationDto> expectedDtos = Collections.singletonList(occupationDto);

        when(occupationRepository.searchOccupations(searchTerm)).thenReturn(searchResults);
        when(occupationRepository.findById(1)).thenReturn(Optional.of(occupation));
        when(occupationMapper.toDtos(searchResults)).thenReturn(expectedDtos);

        // When
        List<OccupationDto> result = occupationService.searchOccupations(searchTerm);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(occupationRepository).searchOccupations(searchTerm);
        verify(occupationRepository).findById(1);
        verify(occupationMapper).toDtos(searchResults);
    }

    @Test
    void searchOccupations_WhenSearchTermIsNumericIdButNotFound_ShouldReturnSearchResultsOnly() {
        // Given
        String searchTerm = "999";
        List<Occupation> searchResults = Collections.singletonList(occupation);
        List<OccupationDto> expectedDtos = Collections.singletonList(occupationDto);

        when(occupationRepository.searchOccupations(searchTerm)).thenReturn(searchResults);
        when(occupationRepository.findById(999)).thenReturn(Optional.empty());
        when(occupationMapper.toDtos(searchResults)).thenReturn(expectedDtos);

        // When
        List<OccupationDto> result = occupationService.searchOccupations(searchTerm);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(occupationRepository).searchOccupations(searchTerm);
        verify(occupationRepository).findById(999);
        verify(occupationMapper).toDtos(searchResults);
    }

    @Test
    void searchOccupations_WhenSearchTermHasLeadingTrailingWhitespace_ShouldTrimAndSearch() {
        // Given
        String searchTerm = "  engineer  ";
        String trimmedTerm = "engineer";
        List<Occupation> occupations = Collections.singletonList(occupation);
        List<OccupationDto> expectedDtos = Collections.singletonList(occupationDto);

        when(occupationRepository.searchOccupations(trimmedTerm)).thenReturn(occupations);
        when(occupationMapper.toDtos(occupations)).thenReturn(expectedDtos);

        // When
        List<OccupationDto> result = occupationService.searchOccupations(searchTerm);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(occupationRepository).searchOccupations(trimmedTerm);
        verify(occupationMapper).toDtos(occupations);
    }
}

