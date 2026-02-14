import { useState, useEffect, useMemo } from 'react';
import { Card, CardBody, FormGroup, Label, Row, Col } from 'reactstrap';
import Select from 'react-select';
import { GET_SKILLS_BY_ORGANIZATION_URL, GET_EMPLOYEE_LATEST_SKILL_ENTRIES_URL } from '../../lib/api/apiUrls.js';
import { axiosGet } from '../../lib/api/client.js';
import useCatch from '../../lib/api/useCatch.js';
import { useEmployeeFilter } from './EmployeeFilterContext.jsx';
import { useOrganization } from '../../context/OrganizationContext.jsx';

function EmployeeSkillFilterComponent() {
  const { cWrapper } = useCatch();
  const { selectedOrganization } = useOrganization();
  const { filterValues, setFilterValues } = useEmployeeFilter();

  const [allSkills, setAllSkills] = useState([]);
  const [employeeSkillIds, setEmployeeSkillIds] = useState(new Set());
  const [loadingSkills, setLoadingSkills] = useState(false);
  const [loadingEmployeeSkills, setLoadingEmployeeSkills] = useState(false);

  // Load all skills for the organization
  useEffect(() => {
    const orgId = selectedOrganization?.value;
    if (!orgId) {
      setAllSkills([]);
      return;
    }

    setLoadingSkills(true);
    cWrapper(() =>
      axiosGet(GET_SKILLS_BY_ORGANIZATION_URL(orgId))
        .then((skillsResponse) => {
          setAllSkills(skillsResponse.data || []);
        })
        .finally(() => {
          setLoadingSkills(false);
        })
    );
  }, [cWrapper, selectedOrganization]);

  // Load employee skills when employee is selected
  useEffect(() => {
    if (!filterValues.employeeId) {
      setEmployeeSkillIds(new Set());
      // Clear selected skill when employee is cleared
      setFilterValues({ skillId: '' });
      return;
    }

    setLoadingEmployeeSkills(true);
    cWrapper(() =>
      axiosGet(GET_EMPLOYEE_LATEST_SKILL_ENTRIES_URL(parseInt(filterValues.employeeId)))
        .then((response) => {
          const skillEntries = response.data || [];
          const skillIds = new Set(
            skillEntries.map(entry => entry.skillId).filter(id => id != null)
          );
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

  // Find selected skill option
  const selectedSkillOption = useMemo(
    () =>
      skillOptions.find(
        (opt) => opt.value?.toString() === filterValues.skillId
      ) || null,
    [skillOptions, filterValues.skillId]
  );

  const handleSkillChange = (selected) => {
    setFilterValues({
      skillId: selected ? selected.value.toString() : ''
    });
  };

  return (
    <Card className="mb-4">
      <CardBody className="filter-card-body">
        <h5 className="mb-3">Skill Filter</h5>
        <Row>
          <Col md={6}>
            <FormGroup>
              <Select
                inputId="skillId"
                options={skillOptions}
                value={selectedSkillOption}
                onChange={handleSkillChange}
                isLoading={loadingSkills || loadingEmployeeSkills}
                isDisabled={loadingSkills || loadingEmployeeSkills || !filterValues.employeeId}
                isClearable
                placeholder={filterValues.employeeId ? "Select a skill..." : "Select an employee first"}
                menuPortalTarget={typeof document !== 'undefined' ? document.body : null}
                styles={{
                  control: (base) => ({ ...base, backgroundColor: '#e7f3ff' }),
                  menuPortal: (base) => ({ ...base, zIndex: 9999 })
                }}
              />
              {loadingEmployeeSkills && (
                <small className="text-muted">
                  Loading employee skills...
                </small>
              )}
              {!filterValues.employeeId && (
                <small className="form-text text-muted">
                  Please select an employee first
                </small>
              )}
              {filterValues.employeeId && !filterValues.skillId && (
                <small className="form-text text-muted">
                  Skill charts will appear when a skill is selected
                </small>
              )}
            </FormGroup>
          </Col>
        </Row>
      </CardBody>
    </Card>
  );
}

export default EmployeeSkillFilterComponent;



