package gr.uom.employee_pulse_service.service;

import gr.uom.employee_pulse_service.controller.dto.OrganizationDto;
import gr.uom.employee_pulse_service.controller.dto.SaveOrganizationDto;
import gr.uom.employee_pulse_service.mapper.OrganizationMapper;
import gr.uom.employee_pulse_service.model.Organization;
import gr.uom.employee_pulse_service.repository.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final OrganizationMapper organizationMapper;

    @Transactional(readOnly = true)
    public List<OrganizationDto> findAll() {
        List<Organization> organizations = organizationRepository.findAll();

        return organizationMapper.toDtos(organizations);
    }

    @Transactional(readOnly = true)
    public OrganizationDto findOrganizationById(Integer id) {
        Organization organization = findById(id);

        return organizationMapper.toDto(organization);
    }

    private Organization findById(Integer id) {
        return organizationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Organization not found"));
    }

    @Transactional
    public void createOrganization(SaveOrganizationDto dto) {
        Organization organization = organizationMapper.toEntity(dto);

        organizationRepository.save(organization);
    }

    @Transactional
    public void updateOrganization(Integer id, SaveOrganizationDto dto) {
        Organization organization = findById(id);

        organization.setName(dto.getName());
        organization.setLocation(dto.getLocation());
    }

    @Transactional
    public void deleteOrganization(Integer id) {
        organizationRepository.deleteById(id);
    }
}