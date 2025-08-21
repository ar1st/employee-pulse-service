package gr.uom.employeepulseservice.service;

import gr.uom.employeepulseservice.model.*;
import gr.uom.employeepulseservice.repository.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InitDataService {

    private final OrganizationRepository organizationRepository;
    private final DepartmentRepository departmentRepository;
    private final SkillRepository skillRepository;
    private final OccupationRepository occupationRepository;
    private final EmployeeRepository employeeRepository;

    @PostConstruct
    public void initData() {
        /*
         * Organizations
         */
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

        /*
         * Departments
         */
        Department department1 = new Department();
        department1.setName("HR");
        department1.setOrganization(organization1);

        Department department2 = new Department();
        department2.setName("Marketing");
        department2.setOrganization(organization1);

        Department department3 = new Department();
        department3.setName("Supply");
        department3.setOrganization(organization1);

        Department department4 = new Department();
        department4.setName("Senior Leadership Team");
        department4.setOrganization(organization1);

        Department department5 = new Department();
        department5.setName("Technology");
        department5.setOrganization(organization1);

        departmentRepository.saveAll(List.of(department1, department2, department3, department4, department5));

        /*
         * Skills
         */
        Skill skill1 = new Skill();
        skill1.setName("Java");
        skill1.setDescription("The techniques and principles of software development, such as analysis, algorithms, coding, testing and compiling of programming paradigms in Java.");
        skill1.setEscoId("19a8293b-8e95-4de3-983f-77484079c389");

        Skill skill2 = new Skill();
        skill2.setName("SQL");
        skill2.setDescription("The computer language SQL is a query language for retrieval of information from a database and of documents containing the needed information. It is developed by the American National Standards Institute and the International Organization for Standardization.");
        skill2.setEscoId("598de5b0-5b58-4ea7-8058-a4bc4d18c742");

        Skill skill3 = new Skill();
        skill3.setName("Python");
        skill3.setDescription("The techniques and principles of software development, such as analysis, algorithms, coding, testing and compiling of programming paradigms in Python.");
        skill3.setEscoId("ccd0a1d9-afda-43d9-b901-96344886e14d");

        Skill skill4 = new Skill();
        skill4.setName("Communication");
        skill4.setDescription("The exchange and conveying of information, ideas, concepts, thoughts, and feelings through the use of a shared system of words, signs, and semiotic rules via a medium.");
        skill4.setEscoId("15d76317-c71a-4fa2-aadc-2ecc34e627b7");
        skillRepository.saveAll(List.of(skill1, skill2, skill3, skill4));

        /*
         * Occupations
         */

        Occupation occupation1 = new Occupation();
        occupation1.setTitle("Software Engineer");
        occupation1.setDescription("Software Engineer");
        occupation1.setEscoId("Software Engineer");

        Occupation occupation2 = new Occupation();
        occupation2.setTitle("Software Engineer in Test");
        occupation2.setDescription("Software Engineer");
        occupation2.setEscoId("Software Engineer");

        Occupation occupation3 = new Occupation();
        occupation3.setTitle("HR Officer");
        occupation3.setDescription("Software Engineer");
        occupation3.setEscoId("Software Engineer");

        Occupation ceo = new Occupation();
        occupation3.setTitle("CEO");
        occupation3.setDescription("CEO - Description");
        occupation3.setEscoId("CEO - ESCO ID");
        occupationRepository.saveAll(List.of(occupation1, occupation2, occupation3, ceo));

        /*
         * Employees
         */

        Employee nikosNikas = new Employee();
        nikosNikas.setFirstName("Nikos");
        nikosNikas.setLastName("Nikas");
        nikosNikas.setEmail("nikos.nikas@gmail.com");
        nikosNikas.setHireDate(LocalDate.now());
        nikosNikas.setOccupation(ceo);
        nikosNikas.setDepartment(department4);
        nikosNikas.setOrganization(organization1);

        Employee giorgosGiorgou = new Employee();
        giorgosGiorgou.setFirstName("Giorgos");
        giorgosGiorgou.setLastName("Giorgou");
        giorgosGiorgou.setEmail("giorgos.giorgou@gmail.com");
        giorgosGiorgou.setHireDate(LocalDate.now());
        giorgosGiorgou.setOccupation(occupation1);
        giorgosGiorgou.setManager(nikosNikas);
        giorgosGiorgou.setDepartment(department5);
        giorgosGiorgou.setOrganization(organization1);

        employeeRepository.saveAll(List.of(nikosNikas, giorgosGiorgou));

    }

}
