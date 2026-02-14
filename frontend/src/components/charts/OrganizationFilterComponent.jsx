import { useState, useEffect, useMemo } from 'react';
import { Card, CardBody, FormGroup, Label, Row, Col } from 'reactstrap';
import Select from 'react-select';
import { GET_DEPARTMENTS_BY_ORGANIZATION_URL } from '../../lib/api/apiUrls.js';
import { axiosGet } from '../../lib/api/client.js';
import useCatch from '../../lib/api/useCatch.js';
import { useOrganizationFilter } from './OrganizationFilterContext.jsx';
import { useOrganization } from "../../context/OrganizationContext.jsx";
import DateInput from "../forms/DateInput.jsx";

function OrganizationFilterComponent() {
  const { cWrapper } = useCatch();
  const { filterValues, setFilterValues } = useOrganizationFilter();
  const [departments, setDepartments] = useState([]);
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

  const handleDepartmentChange = (selected) => {
    setFilterValues({
      departmentId: selected && selected.value !== '' ? selected.value.toString() : '',
      skillId: '' // Clear skill when department changes
    });
  };

  const handleFormChange = (e) => {
    const { name, value } = e.target;
    setFilterValues({ [name]: value });
  };

  return (
    <Card className="mb-4">
      <CardBody className="filter-card-body">
        {/* Overall Rating Filters Section */}
        <div className="mb-4">
          <h5 className="mb-3">Overall Rating Filters</h5>
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

            <Col md={6}>
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

export default OrganizationFilterComponent;

