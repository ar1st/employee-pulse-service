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
        Organization uom = new Organization();
        uom.setName("University of Macedonia");
        uom.setLocation("Thessaloniki");

        Organization ihu = new Organization();
        ihu.setName("International Hellenic University");
        ihu.setLocation("Thessaloniki");

        Organization bestSecret = new Organization();
        bestSecret.setName("BestSecret");
        bestSecret.setLocation("Munich");

        organizationRepository.saveAll(List.of(uom, ihu, bestSecret));

        /*
         * Departments
         */
        Department hr = new Department();
        hr.setName("HR");
        hr.setOrganization(uom);

        Department marketing = new Department();
        marketing.setName("Marketing");
        marketing.setOrganization(uom);

        Department supply = new Department();
        supply.setName("Supply");
        supply.setOrganization(uom);

        Department slt = new Department();
        slt.setName("Senior Leadership Team");
        slt.setOrganization(uom);

        Department tech = new Department();
        tech.setName("Technology");
        tech.setOrganization(uom);

        // Departments for IHU
        Department ihuAdmin = new Department();
        ihuAdmin.setName("Administration");
        ihuAdmin.setOrganization(ihu);

        Department ihuIt = new Department();
        ihuIt.setName("IT Services");
        ihuIt.setOrganization(ihu);

        // Departments for BestSecret
        Department bsEng = new Department();
        bsEng.setName("Engineering");
        bsEng.setOrganization(bestSecret);

        Department bsOps = new Department();
        bsOps.setName("Operations");
        bsOps.setOrganization(bestSecret);

        departmentRepository.saveAll(List.of(
                hr, marketing, supply, slt, tech,
                ihuAdmin, ihuIt,
                bsEng, bsOps
        ));

        /*
         * Skills
         */
        Skill java = new Skill();
        java.setName("Java");
        java.setDescription("Software development with Java & Spring.");
        java.setEscoId("19a8293b-8e95-4de3-983f-77484079c389");

        Skill sql = new Skill();
        sql.setName("SQL");
        sql.setDescription("Relational database querying and optimization.");
        sql.setEscoId("598de5b0-5b58-4ea7-8058-a4bc4d18c742");

        Skill python = new Skill();
        python.setName("Python");
        python.setDescription("Software development with Python.");
        python.setEscoId("ccd0a1d9-afda-43d9-b901-96344886e14d");

        Skill communication = new Skill();
        communication.setName("Communication");
        communication.setDescription("Clear written and verbal communication.");
        communication.setEscoId("15d76317-c71a-4fa2-aadc-2ecc34e627b7");

        Skill leadership = new Skill();
        leadership.setName("Leadership");
        leadership.setDescription("People leadership and decision-making.");
        leadership.setEscoId("esco-leadership-001");

        Skill analytics = new Skill();
        analytics.setName("Analytics");
        analytics.setDescription("Data analysis and insights.");
        analytics.setEscoId("esco-analytics-001");

        skillRepository.saveAll(List.of(java, sql, python, communication, leadership, analytics));

        /*
         * Occupations
         */
        Occupation swe = new Occupation();
        swe.setTitle("Software Engineer");
        swe.setDescription("Software Engineer");
        swe.setEscoId("ESCO-SE");

        Occupation set = new Occupation();
        set.setTitle("Software Engineer in Test");
        set.setDescription("Software Engineer in Test");
        set.setEscoId("ESCO-SET");

        Occupation hrOfficer = new Occupation();
        hrOfficer.setTitle("HR Officer");
        hrOfficer.setDescription("HR Officer");
        hrOfficer.setEscoId("ESCO-HR");

        Occupation ceo = new Occupation();
        ceo.setTitle("CEO");
        ceo.setDescription("Chief Executive Officer");
        ceo.setEscoId("ESCO-CEO");

        Occupation da = new Occupation();
        da.setTitle("Data Analyst");
        da.setDescription("Data Analyst");
        da.setEscoId("ESCO-DA");

        occupationRepository.saveAll(List.of(swe, set, hrOfficer, ceo, da));

        /*
         * Employees (create a few across orgs & departments)
         */
        Employee nikosNikas = new Employee();
        nikosNikas.setFirstName("Nikos");
        nikosNikas.setLastName("Nikas");
        nikosNikas.setEmail("nikos.nikas@gmail.com");
        nikosNikas.setHireDate(LocalDate.now().minusYears(5));
        nikosNikas.setOccupation(ceo);
        nikosNikas.setDepartment(slt);
        nikosNikas.setOrganization(uom);

        Employee giorgosGiorgou = new Employee();
        giorgosGiorgou.setFirstName("Giorgos");
        giorgosGiorgou.setLastName("Giorgou");
        giorgosGiorgou.setEmail("giorgos.giorgou@gmail.com");
        giorgosGiorgou.setHireDate(LocalDate.now().minusYears(2));
        giorgosGiorgou.setOccupation(swe);
        giorgosGiorgou.setDepartment(tech);
        giorgosGiorgou.setOrganization(uom);

        Employee mariaIhu = new Employee();
        mariaIhu.setFirstName("Maria");
        mariaIhu.setLastName("Papadopoulou");
        mariaIhu.setEmail("maria.p@ihu.edu");
        mariaIhu.setHireDate(LocalDate.now().minusYears(3));
        mariaIhu.setOccupation(hrOfficer);
        mariaIhu.setDepartment(ihuAdmin);
        mariaIhu.setOrganization(ihu);

        Employee alexIhu = new Employee();
        alexIhu.setFirstName("Alexandros");
        alexIhu.setLastName("Ioannou");
        alexIhu.setEmail("alex.i@ihu.edu");
        alexIhu.setHireDate(LocalDate.now().minusMonths(18));
        alexIhu.setOccupation(set);
        alexIhu.setDepartment(ihuIt);
        alexIhu.setOrganization(ihu);

        Employee lenaBs = new Employee();
        lenaBs.setFirstName("Elena");
        lenaBs.setLastName("K.");
        lenaBs.setEmail("elena.k@bestsecret.com");
        lenaBs.setHireDate(LocalDate.now().minusYears(1));
        lenaBs.setOccupation(da);
        lenaBs.setDepartment(bsEng);
        lenaBs.setOrganization(bestSecret);

        Employee tomBs = new Employee();
        tomBs.setFirstName("Thomas");
        tomBs.setLastName("M.");
        tomBs.setEmail("thomas.m@bestsecret.com");
        tomBs.setHireDate(LocalDate.now().minusMonths(8));
        tomBs.setOccupation(swe);
        tomBs.setDepartment(bsOps);
        tomBs.setOrganization(bestSecret);

        // Persist employees (and later cascaded children)
        employeeRepository.saveAll(List.of(nikosNikas, giorgosGiorgou, mariaIhu, alexIhu, lenaBs, tomBs));


    }

}
