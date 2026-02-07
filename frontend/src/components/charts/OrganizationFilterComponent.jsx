import { useState, useEffect, useMemo } from 'react';
import { Card, CardBody, Form, FormGroup, Label, Input, Button, Row, Col } from 'reactstrap';
import Select from 'react-select';
import { GET_SKILLS_BY_ORGANIZATION_URL, GET_SKILLS_BY_DEPARTMENT_URL, GET_DEPARTMENTS_BY_ORGANIZATION_URL } from '../../lib/api/apiUrls.js';
import { axiosGet } from '../../lib/api/client.js';
import useCatch from '../../lib/api/useCatch.js';
import { useOrganizationFilter } from './OrganizationFilterContext.jsx';
import { useOrganization } from "../../context/OrganizationContext.jsx";
import DateInput from "../forms/DateInput.jsx";

function OrganizationFilterComponent() {
  const { cWrapper } = useCatch();
  const { filterValues, setFilterValues, triggerChartGeneration } = useOrganizationFilter();
  const [allSkills, setAllSkills] = useState([]);
  const [departments, setDepartments] = useState([]);
  const [loadingSkills, setLoadingSkills] = useState(false);
  const [loadingDepartments, setLoadingDepartments] = useState(false);
  const { selectedOrganization } = useOrganization();

  useEffect(() => {
    setLoadingDepartments(true);

    const orgId = selectedOrganization?.value;

    cWrapper(() =>
      axiosGet(GET_DEPARTMENTS_BY_ORGANIZATION_URL(orgId))
        .then((departmentsResponse) => {
          const depts = departmentsResponse.data.content || departmentsResponse.data || [];
          setDepartments(Array.isArray(depts) ? depts : []);
        })
        .finally(() => {
          setLoadingDepartments(false);
        })
    );
  }, [cWrapper, selectedOrganization]);

  useEffect(() => {
    setLoadingSkills(true);

    const orgId = selectedOrganization?.value;

    const loadSkills = filterValues.departmentId
      ? axiosGet(GET_SKILLS_BY_DEPARTMENT_URL(parseInt(filterValues.departmentId)))
      : axiosGet(GET_SKILLS_BY_ORGANIZATION_URL(orgId));

    cWrapper(() =>
      loadSkills
        .then((skillsResponse) => {
          setAllSkills(skillsResponse.data || []);
          
          // Clear skill selection if current skill is not in the filtered skills
          if (filterValues.skillId) {
            const skillIds = (skillsResponse.data || []).map(s => s.id.toString());
            if (!skillIds.includes(filterValues.skillId)) {
              setFilterValues({ skillId: '' });
            }
          }
        })
        .finally(() => {
          setLoadingSkills(false);
        })
    );
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [filterValues.departmentId, cWrapper, selectedOrganization]);

  // Convert departments to react-select options
  const departmentOptions = useMemo(
    () =>
      departments.map((department) => ({
        value: department.id,
        label: department.name
      })),
    [departments]
  );

  // Find selected department option (null for "All Departments")
  const selectedDepartmentOption = useMemo(
    () =>
      filterValues.departmentId
        ? departmentOptions.find(
            (opt) => opt.value?.toString() === filterValues.departmentId
          ) || null
        : { value: '', label: 'All Departments' },
    [departmentOptions, filterValues.departmentId]
  );

  // Convert skills to react-select options
  const skillOptions = useMemo(
    () =>
      allSkills.map((skill) => ({
        value: skill.id,
        label: skill.name
      })),
    [allSkills]
  );

  // Find selected skill option
  const selectedSkillOption = useMemo(
    () =>
      skillOptions.find(
        (opt) => opt.value?.toString() === filterValues.skillId
      ) || null,
    [skillOptions, filterValues.skillId]
  );

  const handleDepartmentChange = (selected) => {
    setFilterValues({
      departmentId: selected && selected.value !== '' ? selected.value.toString() : '',
      skillId: '' // Clear skill when department changes
    });
  };

  const handleSkillChange = (selected) => {
    setFilterValues({
      skillId: selected ? selected.value.toString() : ''
    });
  };

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
                <Select
                  inputId="departmentId"
                  options={[{ value: '', label: 'All Departments' }, ...departmentOptions]}
                  value={selectedDepartmentOption}
                  onChange={handleDepartmentChange}
                  isLoading={loadingDepartments}
                  isDisabled={loadingDepartments}
                  isClearable={false}
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

            <Col md={3}>
              <FormGroup>
                <Label for="startDate">Start Date *</Label>
                <DateInput
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
                <DateInput
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

