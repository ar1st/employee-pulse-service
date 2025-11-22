package gr.uom.employeepulseservice.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import gr.uom.employeepulseservice.controller.dto.OccupationDto;
import gr.uom.employeepulseservice.controller.dto.SaveOccupationDto;
import gr.uom.employeepulseservice.mapper.OccupationMapper;
import gr.uom.employeepulseservice.model.Occupation;
import gr.uom.employeepulseservice.repository.OccupationRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OccupationService {

    private final OccupationRepository occupationRepository;
    private final OccupationMapper occupationMapper;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional(readOnly = true)
    public Page<OccupationDto> findAll(Pageable pageable) {
        return occupationRepository.findAll(pageable)
                .map(occupationMapper::toDto);
    }

    @Transactional(readOnly = true)
    public OccupationDto findOccupationById(Integer id) {
        Occupation occupation = findById(id);

        return occupationMapper.toDto(occupation);
    }

    private Occupation findById(Integer id) {
        return occupationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Occupation not found"));
    }

    @Transactional
    public void createOccupation(SaveOccupationDto dto) {
        Occupation occupation = occupationMapper.toEntity(dto);

        occupationRepository.save(occupation);
    }

    @Transactional
    public void updateOccupation(Integer id, SaveOccupationDto dto) {
        Occupation occupation = findById(id);

        occupationMapper.updateFromDto(occupation, dto);
    }

    @Transactional
    public void deleteOccupation(Integer id) {
        occupationRepository.deleteById(id);
    }

    @SneakyThrows
    @Transactional
    public void bulkCreateOccupations(String json) {
        List<SaveOccupationDto> dtos = objectMapper.readValue(json, new TypeReference<>() {});

        List<Occupation> toSave = dtos.stream()
                .map(occupationMapper::toEntity)
                .toList();

        occupationRepository.saveAll(toSave);
    }

}