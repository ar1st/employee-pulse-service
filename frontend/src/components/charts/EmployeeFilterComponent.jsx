import { useState, useEffect, useMemo } from 'react';
import { Card, CardBody, FormGroup, Label, Row, Col } from 'reactstrap';
import Select from 'react-select';
import { GET_SKILLS_BY_ORGANIZATION_URL, GET_EMPLOYEES_BY_ORGANIZATION_URL, GET_DEPARTMENTS_BY_ORGANIZATION_URL, GET_EMPLOYEE_LATEST_SKILL_ENTRIES_URL } from '../../lib/api/apiUrls.js';
import { axiosGet } from '../../lib/api/client.js';
import useCatch from '../../lib/api/useCatch.js';
import { useEmployeeFilter } from './EmployeeFilterContext.jsx';
import { useOrganization } from "../../context/OrganizationContext.jsx";
import DateInput from "../forms/DateInput.jsx";

function EmployeeFilterComponent() {
  const { cWrapper } = useCatch();
  const { selectedOrganization } = useOrganization();
  const { filterValues, setFilterValues, triggerChartGeneration } = useEmployeeFilter();
  const [allSkills, setAllSkills] = useState([]);
  const [employees, setEmployees] = useState([]);
  const [departments, setDepartments] = useState([]);
  const [employeeSkillIds, setEmployeeSkillIds] = useState(new Set());
  const [loadingSkills, setLoadingSkills] = useState(false);
  const [loadingEmployees, setLoadingEmployees] = useState(false);
  const [loadingDepartments, setLoadingDepartments] = useState(false);
  const [loadingEmployeeSkills, setLoadingEmployeeSkills] = useState(false);

  // Load skills, employees, and departments on mount
  useEffect(() => {
    setLoadingSkills(true);
    setLoadingEmployees(true);
    setLoadingDepartments(true);

    const orgId = selectedOrganization?.value;

    cWrapper(() =>
      Promise.all([
        axiosGet(GET_SKILLS_BY_ORGANIZATION_URL(orgId)),
        axiosGet(GET_EMPLOYEES_BY_ORGANIZATION_URL(orgId)),
        axiosGet(GET_DEPARTMENTS_BY_ORGANIZATION_URL(orgId))
      ])
        .then(([skillsResponse, employeesResponse, departmentsResponse]) => {
          setAllSkills(skillsResponse.data || []);
          const emps = employeesResponse.data.content || employeesResponse.data || [];
          setEmployees(Array.isArray(emps) ? emps : []);
          const depts = departmentsResponse.data.content || departmentsResponse.data || [];
          setDepartments(Array.isArray(depts) ? depts : []);
        })
        .finally(() => {
          setLoadingSkills(false);
          setLoadingEmployees(false);
          setLoadingDepartments(false);
        })
    );
  }, [cWrapper, selectedOrganization]);

  // Load employee skills when employee is selected
  useEffect(() => {
    if (!filterValues.employeeId) {
      setEmployeeSkillIds(new Set());
      return;
    }

    setLoadingEmployeeSkills(true);
    cWrapper(() =>
      axiosGet(GET_EMPLOYEE_LATEST_SKILL_ENTRIES_URL(parseInt(filterValues.employeeId)))
        .then((response) => {
          const skillEntries = response.data || [];
          const skillIds = new Set(skillEntries.map(entry => entry.skillId).filter(id => id != null));
          setEmployeeSkillIds(skillIds);

          // Clear skill selection if current skill is not in employee's skills
          if (filterValues.skillId && !skillIds.has(parseInt(filterValues.skillId))) {
            setFilterValues({ skillId: '' });
          }
        })
        .finally(() => setLoadingEmployeeSkills(false))
    );
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [filterValues.employeeId, cWrapper]);

  // Filter employees by department
  const filteredEmployees = useMemo(() => {
    if (!filterValues.departmentId) {
      return employees;
    }
    return employees.filter(emp => emp.departmentId?.toString() === filterValues.departmentId);
  }, [employees, filterValues.departmentId]);

  // Convert departments to react-select options
  const departmentOptions = useMemo(
    () =>
      departments.map((department) => ({
        value: department.id,
        label: department.name
      })),
    [departments]
  );

  // Find selected department option
  const selectedDepartmentOption = useMemo(
    () =>
      departmentOptions.find(
        (opt) => opt.value?.toString() === filterValues.departmentId
      ) || null,
    [departmentOptions, filterValues.departmentId]
  );

  const employeeOptions = useMemo(
    () =>
      filteredEmployees.map((employee) => ({
        value: employee.id,
        label: `${employee.firstName} ${employee.lastName}`
      })),
    [filteredEmployees]
  );

  // Filter skills based on selected employee
  const filteredSkills = useMemo(() => {
    if (!filterValues.employeeId || employeeSkillIds.size === 0) {
      return [];
    }
    return allSkills.filter(skill => employeeSkillIds.has(skill.id));
  }, [allSkills, employeeSkillIds, filterValues.employeeId]);

  // Convert filtered skills to react-select options
  const skillOptions = useMemo(
    () =>
      filteredSkills.map((skill) => ({
        value: skill.id,
        label: skill.name
      })),
    [filteredSkills]
  );

  const selectedEmployeeOption = useMemo(
    () =>
      employeeOptions.find(
        (opt) => opt.value?.toString() === filterValues.employeeId
      ) || null,
    [employeeOptions, filterValues.employeeId]
  );

  // Find selected skill option
  const selectedSkillOption = useMemo(
    () =>
      skillOptions.find(
        (opt) => opt.value?.toString() === filterValues.skillId
      ) || null,
    [skillOptions, filterValues.skillId]
  );

  const handleFormChange = (e) => {
    const { name, value } = e.target;
    setFilterValues({ [name]: value });
  };

  const handleDepartmentChange = (selected) => {
    const newDepartmentId = selected ? selected.value.toString() : '';
    
    // If department changes, check if current employee is still valid
    const currentEmployee = employees.find(emp => emp.id?.toString() === filterValues.employeeId);
    const shouldClearEmployee = newDepartmentId && currentEmployee?.departmentId?.toString() !== newDepartmentId;
    
    setFilterValues({ 
      departmentId: newDepartmentId,
      ...(shouldClearEmployee ? { employeeId: '' } : {})
    });
  };

  const handleEmployeeChange = (selected) => {
    setFilterValues({ 
      employeeId: selected ? selected.value.toString() : '',
      skillId: '' // Clear skill when employee changes
    });
  };

  return (
    <Card className="mb-4">
      <CardBody className="filter-card-body">
        <div className="">
          <h5 className="mb-3">Overall Rating Filters</h5>
          <Row>
            <Col md={2}>
              <FormGroup>
                <Label for="departmentId">Department</Label>
                <Select
                  inputId="departmentId"
                  options={departmentOptions}
                  value={selectedDepartmentOption}
                  onChange={handleDepartmentChange}
                  isLoading={loadingDepartments}
                  isDisabled={loadingDepartments}
                  isClearable
                  placeholder="All Departments"
                  menuPortalTarget={typeof document !== 'undefined' ? document.body : null}
                  styles={{
                    control: (base) => ({ ...base, backgroundColor: '#e7f3ff' }),
                    menuPortal: (base) => ({ ...base, zIndex: 9999 })
                  }}
                />
              </FormGroup>
            </Col>

            <Col md={3}>
              <FormGroup>
                <Label for="employeeId">Employee *</Label>
                <Select
                  inputId="employeeId"
                  options={employeeOptions}
                  value={selectedEmployeeOption}
                  onChange={handleEmployeeChange}
                  isLoading={loadingEmployees}
                  isDisabled={loadingEmployees}
                  isClearable
                  placeholder="Select an employee..."
                  menuPortalTarget={typeof document !== 'undefined' ? document.body : null}
                  styles={{
                    control: (base) => ({ ...base, backgroundColor: '#e7f3ff' }),
                    menuPortal: (base) => ({ ...base, zIndex: 9999 })
                  }}
                />
              </FormGroup>
            </Col>

            <Col md={3}>
              <FormGroup>
                <Label for="startDate">Start Date</Label>
                <DateInput
                  name="startDate"
                  id="startDate"
                  value={filterValues.startDate}
                  onChange={handleFormChange}
                />
              </FormGroup>
            </Col>

            <Col md={4}>
              <FormGroup>
                <Label for="endDate">End Date</Label>
                <DateInput
                  name="endDate"
                  id="endDate"
                  value={filterValues.endDate}
                  onChange={handleFormChange}
                />
              </FormGroup>
            </Col>
          </Row>
        </div>

      </CardBody>
    </Card>
  );
}

export default EmployeeFilterComponent;

