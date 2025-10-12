package gr.uom.employeepulseservice.service;

import gr.uom.employeepulseservice.controller.dto.OccupationDto;
import gr.uom.employeepulseservice.controller.dto.SaveOccupationDto;
import gr.uom.employeepulseservice.mapper.OccupationMapper;
import gr.uom.employeepulseservice.model.Occupation;
import gr.uom.employeepulseservice.repository.OccupationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OccupationService {

    private final OccupationRepository occupationRepository;
    private final OccupationMapper occupationMapper;

    @Transactional(readOnly = true)
    public List<OccupationDto> findAll() {
        List<Occupation> occupations = occupationRepository.findAll();

        return occupationMapper.toDtos(occupations);
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

    @Transactional
    public void bulkCreateOccupations(List<SaveOccupationDto> dtos) {
        List<Occupation> toSave = dtos.stream()
                .map(occupationMapper::toEntity)
                .toList();

        occupationRepository.saveAll(toSave);
    }

}