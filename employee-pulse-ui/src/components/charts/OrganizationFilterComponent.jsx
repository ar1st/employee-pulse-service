import { useState, useEffect } from 'react';
import { Card, CardBody, Form, FormGroup, Label, Input, Button, Row, Col, Spinner } from 'reactstrap';
import { DEFAULT_ORGANIZATION_ID, GET_SKILLS_BY_ORGANIZATION_URL, GET_DEPARTMENTS_BY_ORGANIZATION_URL } from '../../lib/api/apiUrls.js';
import { axiosGet } from '../../lib/api/client.js';
import useCatch from '../../lib/api/useCatch.js';
import { useOrganizationFilter } from './OrganizationFilterContext.jsx';

function OrganizationFilterComponent() {
  const { cWrapper } = useCatch();
  const { filterValues, setFilterValues, triggerChartGeneration } = useOrganizationFilter();
  const [skills, setSkills] = useState([]);
  const [departments, setDepartments] = useState([]);
  const [loadingSkills, setLoadingSkills] = useState(false);
  const [loadingDepartments, setLoadingDepartments] = useState(false);

  // Load skills and departments on mount
  useEffect(() => {
    setLoadingSkills(true);
    setLoadingDepartments(true);

    cWrapper(() =>
      Promise.all([
        axiosGet(GET_SKILLS_BY_ORGANIZATION_URL(DEFAULT_ORGANIZATION_ID)),
        axiosGet(GET_DEPARTMENTS_BY_ORGANIZATION_URL(DEFAULT_ORGANIZATION_ID))
      ])
        .then(([skillsResponse, departmentsResponse]) => {
          setSkills(skillsResponse.data || []);
          const depts = departmentsResponse.data.content || departmentsResponse.data || [];
          setDepartments(Array.isArray(depts) ? depts : []);
        })
        .finally(() => {
          setLoadingSkills(false);
          setLoadingDepartments(false);
        })
    );
  }, [cWrapper]);

  const handleFormChange = (e) => {
    const { name, value } = e.target;
    setFilterValues({ [name]: value });
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    if (filterValues.startDate && filterValues.endDate && filterValues.skillId) {
      triggerChartGeneration();
    }
  };

  const isGenerateDisabled = !filterValues.startDate || !filterValues.endDate || !filterValues.skillId;

  return (
    <Card className="mb-4">
      <CardBody>
        <Form onSubmit={handleSubmit}>
          <Row>
            <Col md={3}>
              <FormGroup>
                <Label for="departmentId">Department</Label>
                <Input
                  type="select"
                  name="departmentId"
                  id="departmentId"
                  value={filterValues.departmentId}
                  onChange={handleFormChange}
                  disabled={loadingDepartments}
                >
                  <option value="">All Departments</option>
                  {departments.map((department) => (
                    <option key={department.id} value={department.id}>
                      {department.name}
                    </option>
                  ))}
                </Input>
                {loadingDepartments && (
                  <small className="text-muted">
                    <Spinner size="sm" className="me-1" />
                    Loading departments...
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

export default OrganizationFilterComponent;

