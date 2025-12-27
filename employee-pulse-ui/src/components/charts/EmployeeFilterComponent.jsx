import { useState, useEffect, useMemo } from 'react';
import { Card, CardBody, Form, FormGroup, Label, Input, Button, Row, Col } from 'reactstrap';
import Select from 'react-select';
import { DEFAULT_ORGANIZATION_ID, GET_SKILLS_BY_ORGANIZATION_URL, GET_EMPLOYEES_BY_ORGANIZATION_URL, GET_DEPARTMENTS_BY_ORGANIZATION_URL } from '../../lib/api/apiUrls.js';
import { axiosGet } from '../../lib/api/client.js';
import useCatch from '../../lib/api/useCatch.js';
import { useEmployeeFilter } from './EmployeeFilterContext.jsx';

function EmployeeFilterComponent() {
  const { cWrapper } = useCatch();
  const { filterValues, setFilterValues, triggerChartGeneration } = useEmployeeFilter();
  const [skills, setSkills] = useState([]);
  const [employees, setEmployees] = useState([]);
  const [departments, setDepartments] = useState([]);
  const [loadingSkills, setLoadingSkills] = useState(false);
  const [loadingEmployees, setLoadingEmployees] = useState(false);
  const [loadingDepartments, setLoadingDepartments] = useState(false);

  // Load skills, employees, and departments on mount
  useEffect(() => {
    setLoadingSkills(true);
    setLoadingEmployees(true);
    setLoadingDepartments(true);

    cWrapper(() =>
      Promise.all([
        axiosGet(GET_SKILLS_BY_ORGANIZATION_URL(DEFAULT_ORGANIZATION_ID)),
        axiosGet(GET_EMPLOYEES_BY_ORGANIZATION_URL(DEFAULT_ORGANIZATION_ID)),
        axiosGet(GET_DEPARTMENTS_BY_ORGANIZATION_URL(DEFAULT_ORGANIZATION_ID))
      ])
        .then(([skillsResponse, employeesResponse, departmentsResponse]) => {
          setSkills(skillsResponse.data || []);
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
  }, [cWrapper]);

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

  // Convert skills to react-select options
  const skillOptions = useMemo(
    () =>
      skills.map((skill) => ({
        value: skill.id,
        label: skill.name
      })),
    [skills]
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
      employeeId: selected ? selected.value.toString() : '' 
    });
  };

  const handleSkillChange = (selected) => {
    setFilterValues({ 
      skillId: selected ? selected.value.toString() : '' 
    });
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    if (filterValues.startDate && filterValues.endDate && filterValues.employeeId && filterValues.skillId) {
      triggerChartGeneration();
    }
  };

  const isGenerateDisabled = !filterValues.startDate || !filterValues.endDate || !filterValues.employeeId || !filterValues.skillId;

  return (
    <Card className="mb-4">
      <CardBody>
        <Form onSubmit={handleSubmit}>
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
                    menuPortal: (base) => ({ ...base, zIndex: 9999 })
                  }}
                />
              </FormGroup>
            </Col>

            <Col md={3}>
              <FormGroup>
                <Label for="skillId">Skill *</Label>
                <Select
                  inputId="skillId"
                  options={skillOptions}
                  value={selectedSkillOption}
                  onChange={handleSkillChange}
                  isLoading={loadingSkills}
                  isDisabled={loadingSkills}
                  isClearable
                  placeholder="Select a skill..."
                  menuPortalTarget={typeof document !== 'undefined' ? document.body : null}
                  styles={{
                    menuPortal: (base) => ({ ...base, zIndex: 9999 })
                  }}
                />
              </FormGroup>
            </Col>

            <Col md={2}>
              <FormGroup>
                <Label for="startDate">Start Date *</Label>
                <Input
                  type="date"
                  name="startDate"
                  id="startDate"
                  value={filterValues.startDate}
                  onChange={handleFormChange}
                  required
                />
              </FormGroup>
            </Col>

            <Col md={2}>
              <FormGroup>
                <Label for="endDate">End Date *</Label>
                <Input
                  type="date"
                  name="endDate"
                  id="endDate"
                  value={filterValues.endDate}
                  onChange={handleFormChange}
                  required
                />
              </FormGroup>
            </Col>
          </Row>
          <Row>
            <Col md={12} className="d-flex justify-content-end">
              <Button
                type="submit"
                color="primary"
                disabled={isGenerateDisabled}
              >
                Generate Chart
              </Button>
            </Col>
          </Row>
        </Form>
      </CardBody>
    </Card>
  );
}

export default EmployeeFilterComponent;

