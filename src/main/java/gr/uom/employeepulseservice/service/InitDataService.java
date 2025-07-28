package gr.uom.employeepulseservice.service;

import gr.uom.employeepulseservice.model.Department;
import gr.uom.employeepulseservice.model.Organization;
import gr.uom.employeepulseservice.repository.DepartmentRepository;
import gr.uom.employeepulseservice.repository.OrganizationRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InitDataService {

    private final OrganizationRepository organizationRepository;
    private final DepartmentRepository departmentRepository;

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

        Department department1 = new Department();
        department1.setName("HR");
        department1.setOrganization(organization1);

        Department department2 = new Department();
        department2.setName("Marketing");
        department2.setOrganization(organization1);

        Department department3 = new Department();
        department3.setName("Supply");
        department3.setOrganization(organization1);
        departmentRepository.saveAll(List.of(department1, department2, department3));
        
    }

}
