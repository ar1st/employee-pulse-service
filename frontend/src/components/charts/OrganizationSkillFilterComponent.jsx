import { useState, useEffect, useMemo } from 'react';
import { Card, CardBody, FormGroup, Label, Row, Col } from 'reactstrap';
import Select from 'react-select';
import { GET_SKILLS_BY_ORGANIZATION_URL, GET_SKILLS_BY_DEPARTMENT_URL } from '../../lib/api/apiUrls.js';
import { axiosGet } from '../../lib/api/client.js';
import useCatch from '../../lib/api/useCatch.js';
import { useOrganizationFilter } from './OrganizationFilterContext.jsx';
import { useOrganization } from "../../context/OrganizationContext.jsx";

function OrganizationSkillFilterComponent() {
  const { cWrapper } = useCatch();
  const { filterValues, setFilterValues } = useOrganizationFilter();
  const [allSkills, setAllSkills] = useState([]);
  const [loadingSkills, setLoadingSkills] = useState(false);
  const { selectedOrganization } = useOrganization();

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
                isLoading={loadingSkills}
                isDisabled={loadingSkills || !selectedOrganization?.value}
                isClearable
                placeholder={selectedOrganization?.value ? "Select a skill..." : "Select an organization first"}
                menuPortalTarget={typeof document !== 'undefined' ? document.body : null}
                styles={{
                  control: (base) => ({ ...base, backgroundColor: '#e7f3ff' }),
                  menuPortal: (base) => ({ ...base, zIndex: 9999 })
                }}
              />
              {!selectedOrganization?.value && (
                <small className="form-text text-muted">
                  Please select an organization first
                </small>
              )}
              {selectedOrganization?.value && !filterValues.skillId && (
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

export default OrganizationSkillFilterComponent;


