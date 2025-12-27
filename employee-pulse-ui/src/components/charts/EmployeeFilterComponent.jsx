import { useState, useEffect } from 'react';
import { Card, CardBody, Form, FormGroup, Label, Input, Button, Row, Col, Spinner } from 'reactstrap';
import { DEFAULT_ORGANIZATION_ID, GET_SKILLS_BY_ORGANIZATION_URL, GET_EMPLOYEES_BY_ORGANIZATION_URL } from '../../lib/api/apiUrls.js';
import { axiosGet } from '../../lib/api/client.js';
import useCatch from '../../lib/api/useCatch.js';
import { useEmployeeFilter } from './EmployeeFilterContext.jsx';

function EmployeeFilterComponent() {
  const { cWrapper } = useCatch();
  const { filterValues, setFilterValues, triggerChartGeneration } = useEmployeeFilter();
  const [skills, setSkills] = useState([]);
  const [employees, setEmployees] = useState([]);
  const [loadingSkills, setLoadingSkills] = useState(false);
  const [loadingEmployees, setLoadingEmployees] = useState(false);

  // Load skills and employees on mount
  useEffect(() => {
    setLoadingSkills(true);
    setLoadingEmployees(true);

    cWrapper(() =>
      Promise.all([
        axiosGet(GET_SKILLS_BY_ORGANIZATION_URL(DEFAULT_ORGANIZATION_ID)),
        axiosGet(GET_EMPLOYEES_BY_ORGANIZATION_URL(DEFAULT_ORGANIZATION_ID))
      ])
        .then(([skillsResponse, employeesResponse]) => {
          setSkills(skillsResponse.data || []);
          const emps = employeesResponse.data.content || employeesResponse.data || [];
          setEmployees(Array.isArray(emps) ? emps : []);
        })
        .finally(() => {
          setLoadingSkills(false);
          setLoadingEmployees(false);
        })
    );
  }, [cWrapper]);

  const handleFormChange = (e) => {
    const { name, value } = e.target;
    setFilterValues({ [name]: value });
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
            <Col md={3}>
              <FormGroup>
                <Label for="employeeId">Employee *</Label>
                <Input
                  type="select"
                  name="employeeId"
                  id="employeeId"
                  value={filterValues.employeeId}
                  onChange={handleFormChange}
                  disabled={loadingEmployees}
                  required
                >
                  <option value="">Select an employee</option>
                  {employees.map((employee) => (
                    <option key={employee.id} value={employee.id}>
                      {employee.firstName} {employee.lastName}
                    </option>
                  ))}
                </Input>
                {loadingEmployees && (
                  <small className="text-muted">
                    <Spinner size="sm" className="me-1" />
                    Loading employees...
                  </small>
                )}
              </FormGroup>
            </Col>

            <Col md={3}>
              <FormGroup>
                <Label for="skillId">Skill *</Label>
                <Input
                  type="select"
                  name="skillId"
                  id="skillId"
                  value={filterValues.skillId}
                  onChange={handleFormChange}
                  disabled={loadingSkills}
                  required
                >
                  <option value="">Select a skill</option>
                  {skills.map((skill) => (
                    <option key={skill.id} value={skill.id}>
                      {skill.name}
                    </option>
                  ))}
                </Input>
                {loadingSkills && (
                  <small className="text-muted">
                    <Spinner size="sm" className="me-1" />
                    Loading skills...
                  </small>
                )}
              </FormGroup>
            </Col>

            <Col md={3}>
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

            <Col md={3}>
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

