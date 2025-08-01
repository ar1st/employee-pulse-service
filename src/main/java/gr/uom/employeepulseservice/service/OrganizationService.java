package gr.uom.employeepulseservice.service;

import gr.uom.employeepulseservice.controller.dto.OrganizationDto;
import gr.uom.employeepulseservice.controller.dto.SaveOrganizationDto;
import gr.uom.employeepulseservice.mapper.OrganizationMapper;
import gr.uom.employeepulseservice.model.Organization;
import gr.uom.employeepulseservice.repository.OrganizationRepository;
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

        organization.setName(dto.name());
        organization.setLocation(dto.location());
    }

    @Transactional
    public void deleteOrganization(Integer id) {
        organizationRepository.deleteById(id);
    }
}