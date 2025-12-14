import {Button, Col, Form, FormGroup, Input, Label, Row, Spinner} from "reactstrap";
import {
  DEFAULT_ORGANIZATION_ID,
  GET_EMPLOYEE_URL,
  GET_DEPARTMENTS_BY_ORGANIZATION_URL,
  GET_OCCUPATIONS_BY_ORGANIZATION_URL,
  SEARCH_OCCUPATIONS_URL,
  CREATE_EMPLOYEE_URL,
  UPDATE_EMPLOYEE_URL
} from "../../lib/api/apiUrls.js";
import {axiosGet, axiosPost, axiosPut} from "../../lib/api/client.js";
import {useEffect, useState, useMemo, useRef} from "react";
import useCatch from "../../lib/api/useCatch.js";
import {useNavigate} from "react-router-dom";
import {handleChange} from "../../lib/formUtils.js";

export default function SaveEmployeeForm({ employeeId = null }) {
  const navigate = useNavigate();
  const {cWrapper} = useCatch();
  const isEditMode = !!employeeId;

  const [loadingData, setLoadingData] = useState(false);
  const [submitting, setSubmitting] = useState(false);

  const [departments, setDepartments] = useState([]);
  const [organizationOccupations, setOrganizationOccupations] = useState([]);
  const [searchedOccupations, setSearchedOccupations] = useState([]);
  const [searchingOccupations, setSearchingOccupations] = useState(false);
  const [occupationSearchTerm, setOccupationSearchTerm] = useState('');
  const searchTimeoutRef = useRef(null);

  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    email: '',
    hireDate: '',
    departmentId: '',
    occupationId: ''
  });

  // Load departments and organization occupations on mount
  useEffect(() => {
    setLoadingData(true);
    const loadData = () => {
      return Promise.all([
        axiosGet(GET_DEPARTMENTS_BY_ORGANIZATION_URL(DEFAULT_ORGANIZATION_ID)),
        axiosGet(GET_OCCUPATIONS_BY_ORGANIZATION_URL(DEFAULT_ORGANIZATION_ID))
      ])
        .then(([departmentsResponse, occupationsResponse]) => {
          const depts = departmentsResponse.data.content || departmentsResponse.data || [];
          const occs = occupationsResponse.data || [];
          setDepartments(Array.isArray(depts) ? depts : []);
          setOrganizationOccupations(Array.isArray(occs) ? occs : []);
          
          // If in edit mode, also load employee data after occupations are loaded
          if (isEditMode && employeeId) {
            return axiosGet(GET_EMPLOYEE_URL(employeeId))
              .then((employeeResponse) => {
                const employee = employeeResponse.data;
                // Format hireDate for date input (YYYY-MM-DD)
                let formattedHireDate = '';
                if (employee.hireDate) {
                  const date = new Date(employee.hireDate);
                  if (!isNaN(date.getTime())) {
                    formattedHireDate = date.toISOString().split('T')[0];
                  }
                }
                
                setFormData({
                  firstName: employee.firstName || '',
                  lastName: employee.lastName || '',
                  email: employee.email || '',
                  hireDate: formattedHireDate,
                  departmentId: employee.departmentId ? employee.departmentId.toString() : '',
                  occupationId: employee.occupationId ? employee.occupationId.toString() : ''
                });
              });
          }
        });
    };

    cWrapper(() =>
      loadData()
        .finally(() => setLoadingData(false))
    );
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [isEditMode, employeeId, cWrapper]);

  // Search occupations when user types (with debouncing)
  useEffect(() => {
    // Clear previous timeout
    if (searchTimeoutRef.current) {
      clearTimeout(searchTimeoutRef.current);
    }

    // If search term is empty, clear searched occupations and show organization occupations
    if (!occupationSearchTerm.trim()) {
      setSearchedOccupations([]);
      return;
    }

    // Debounce the search - wait 300ms after user stops typing
    setSearchingOccupations(true);
    searchTimeoutRef.current = setTimeout(() => {
      cWrapper(() =>
        axiosGet(SEARCH_OCCUPATIONS_URL(occupationSearchTerm.trim()))
          .then((response) => {
            setSearchedOccupations(response.data || []);
          })
          .finally(() => setSearchingOccupations(false))
      );
    }, 300);

    // Cleanup timeout on unmount or when search term changes
    return () => {
      if (searchTimeoutRef.current) {
        clearTimeout(searchTimeoutRef.current);
      }
    };
  }, [occupationSearchTerm, cWrapper]);

  // Determine which occupations to show: searched occupations if searching, otherwise organization occupations
  const occupationsToShow = useMemo(() => {
    if (occupationSearchTerm.trim()) {
      return searchedOccupations;
    }
    return organizationOccupations;
  }, [occupationSearchTerm, searchedOccupations, organizationOccupations]);

  const handleSubmit = (e) => {
    e.preventDefault();
    setSubmitting(true);

    const payload = {
      firstName: formData.firstName,
      lastName: formData.lastName,
      email: formData.email,
      hireDate: formData.hireDate || null,
      organizationId: DEFAULT_ORGANIZATION_ID,
      departmentId: formData.departmentId ? parseInt(formData.departmentId) : null,
      occupationId: formData.occupationId ? parseInt(formData.occupationId) : null
    };

    if (isEditMode) {
      cWrapper(() =>
        axiosPut(UPDATE_EMPLOYEE_URL(employeeId), payload)
          .then(() => {
            navigate('/employees');
          })
          .finally(() => setSubmitting(false))
      );
    } else {
      cWrapper(() =>
        axiosPost(CREATE_EMPLOYEE_URL(), payload)
          .then(() => {
            navigate('/employees');
          })
          .finally(() => setSubmitting(false))
      );
    }
  };

  const handleCancel = () => {
    navigate('/employees');
  };

  if (loadingData) {
    return (
      <div className="text-center mt-3">
        <Spinner color="primary"/>
        <p>Loading data...</p>
      </div>
    );
  }

  return <Form onSubmit={handleSubmit} className="mt-4">
    <Row>
      <Col md={6} style={{paddingLeft: 0}}>
        <FormGroup>
          <Label for="firstName">First Name *</Label>
          <Input
            type="text"
            name="firstName"
            id="firstName"
            value={formData.firstName}
            onChange={(e) => handleChange(e, setFormData)}
            placeholder="Enter first name"
            required
          />
        </FormGroup>
      </Col>
      <Col md={6}>
        <FormGroup>
          <Label for="lastName">Last Name *</Label>
          <Input
            type="text"
            name="lastName"
            id="lastName"
            value={formData.lastName}
            onChange={(e) => handleChange(e, setFormData)}
            placeholder="Enter last name"
            required
          />
        </FormGroup>
      </Col>
    </Row>

    <Row>
      <Col md={6} style={{paddingLeft: 0}}>
        <FormGroup>
          <Label for="email">Email *</Label>
          <Input
            type="email"
            name="email"
            id="email"
            value={formData.email}
            onChange={(e) => handleChange(e, setFormData)}
            placeholder="Enter email"
            required
          />
        </FormGroup>
      </Col>
      <Col md={6}>
        <FormGroup>
          <Label for="hireDate">Hire Date *</Label>
          <Input
            type="date"
            name="hireDate"
            id="hireDate"
            value={formData.hireDate}
            onChange={(e) => handleChange(e, setFormData)}
            required
          />
        </FormGroup>
      </Col>
    </Row>

    <Row>
      <Col md={6} style={{paddingLeft: 0}}>
        <FormGroup>
          <Label for="departmentId">Department *</Label>
          <Input
            type="select"
            name="departmentId"
            id="departmentId"
            value={formData.departmentId}
            onChange={(e) => handleChange(e, setFormData)}
            required
          >
            <option value="">No department</option>
            {departments.map((department) => (
              <option key={department.id} value={department.id}>
                {department.name}
              </option>
            ))}
          </Input>
        </FormGroup>
      </Col>
      <Col md={6}>
        <FormGroup>
          <Label for="occupationSearch">Occupation *</Label>
          <div className="position-relative mb-2">
            <Input
              type="text"
              id="occupationSearch"
              value={occupationSearchTerm}
              onChange={(e) => setOccupationSearchTerm(e.target.value)}
              placeholder="Type to search occupations..."
            />
            {searchingOccupations && (
              <div className="position-absolute top-50 end-0 translate-middle-y pe-3">
                <Spinner size="sm" color="primary" />
              </div>
            )}
          </div>
          {occupationSearchTerm && !searchingOccupations && searchedOccupations.length === 0 && (
            <small className="form-text text-muted mb-2 d-block">
              No occupations found matching "{occupationSearchTerm}"
            </small>
          )}
          <Input
            type="select"
            name="occupationId"
            id="occupationId"
            value={formData.occupationId}
            onChange={(e) => {
              handleChange(e, setFormData);
              // Clear search when an occupation is selected
              setOccupationSearchTerm('');
            }}
            required

          >
            <option value="">No occupation</option>
            {occupationsToShow.map((occupation) => (
              <option key={occupation.id} value={occupation.id}>
                {occupation.title}
              </option>
            ))}
          </Input>
        </FormGroup>
      </Col>
    </Row>

    <div className="d-flex gap-2 mt-4">
      <Button
        type="submit"
        color="primary"
        disabled={submitting}
      >
        {submitting ? (
          <>
            <Spinner size="sm" className="me-2"/>
            {isEditMode ? 'Updating...' : 'Creating...'}
          </>
        ) : (
          isEditMode ? 'Update Employee' : 'Create Employee'
        )}
      </Button>
      <Button
        type="button"
        color="secondary"
        onClick={handleCancel}
        disabled={submitting}
      >
        Cancel
      </Button>
    </div>
  </Form>
}

