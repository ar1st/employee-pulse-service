package gr.uom.employee_pulse_service.service;

import gr.uom.employee_pulse_service.model.Organization;
import gr.uom.employee_pulse_service.repository.OrganizationRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InitDataService {

    private final OrganizationRepository organizationRepository;

    @PostConstruct
    public void initData() {
        Organization organization1 = new Organization();
        organization1.setName("University of Macedonia");
        organization1.setLocation("Thessaloniki");

        Organization organization2 = new Organization();
        organization2.setName("International Hellenic University");
        organization2.setLocation("Thessaloniki");

        Organization organization3 = new Organization();
        organization3.setName("BestSecret");
        organization3.setLocation("Munich");
        organizationRepository.saveAll(List.of(organization1, organization2, organization3));
    }

}
