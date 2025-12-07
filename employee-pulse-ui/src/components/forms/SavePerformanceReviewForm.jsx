import {Button, Col, Form, FormGroup, Input, Label, Row, Spinner} from "reactstrap";
import {
  CREATE_PERFORMANCE_REVIEW_URL, DEFAULT_ORGANIZATION_ID,
  GET_DEPARTMENT_URL, GET_DEPARTMENTS_BY_ORGANIZATION_URL,
  GET_EMPLOYEES_BY_ORGANIZATION_URL
} from "../../lib/api/apiUrls.js";
import {axiosGet, axiosPost} from "../../lib/api/client.js";
import {useEffect, useState} from "react";
import useCatch from "../../lib/api/useCatch.js";
import {useNavigate} from "react-router-dom";
import {handleChange} from "../../lib/formUtils.js";

export default function SavePerformanceReviewForm() {
  const navigate = useNavigate();
  const {cWrapper} = useCatch();

  const [departments, setDepartments] = useState([]);
  const [allEmployees, setAllEmployees] = useState([]);
  const [departmentEmployees, setDepartmentEmployees] = useState([]);
  const [departmentManager, setDepartmentManager] = useState(null);
  const [loading, setLoading] = useState(false);
  const [loadingDepartment, setLoadingDepartment] = useState(false);
  const [submitting, setSubmitting] = useState(false);

  const [formData, setFormData] = useState({
    departmentId: '',
    employeeId: '',
    reporterId: '',
    rawText: '',
    comments: '',
    overallRating: ''
  });

  useEffect(() => {
    // eslint-disable-next-line react-hooks/set-state-in-effect
    setLoading(true);

    cWrapper(() =>
      Promise.all([
        axiosGet(GET_DEPARTMENTS_BY_ORGANIZATION_URL(DEFAULT_ORGANIZATION_ID)),
        axiosGet(GET_EMPLOYEES_BY_ORGANIZATION_URL(DEFAULT_ORGANIZATION_ID))
      ])
        .then(([departmentsResponse, employeesResponse]) => {
          setDepartments(departmentsResponse.data);
          setAllEmployees(employeesResponse.data);
        })
        .finally(() => setLoading(false))
    );

  }, [cWrapper]);


  const handleDepartmentChange = (e) => {
    const departmentId = e.target.value;
    setFormData(prev => ({
      ...prev,
      departmentId: departmentId,
      employeeId: '',
      reporterId: ''
    }));
    setDepartmentEmployees([]);
    setDepartmentManager(null);

    if (!departmentId) {
      return;
    }

    setLoadingDepartment(true);

    cWrapper(() =>
      axiosGet(GET_DEPARTMENT_URL(departmentId))
        .then((departmentResponse) => {
          const department = departmentResponse.data;
          setDepartmentEmployees(allEmployees.filter(it => it.departmentId === department.id));

          let manager = null;
          if (department.managerId) {
            manager = allEmployees.find(emp => emp.id === department.managerId);
          }

          if (manager) {
            setDepartmentManager(manager);
            setFormData(prev => ({
              ...prev,
              reporterId: manager.id.toString()
            }));
          }
        })
        .finally(() => setLoadingDepartment(false))
    );
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    setSubmitting(true);

    const payload = {
      employeeId: parseInt(formData.employeeId),
      reporterId: parseInt(formData.reporterId),
      rawText: formData.rawText,
      comments: formData.comments,
      overallRating: parseFloat(formData.overallRating)
    };

    cWrapper(() =>
      axiosPost(CREATE_PERFORMANCE_REVIEW_URL(), payload)
        .then(() => {
          navigate('/performance-reviews');
        })
        .catch((err) => {
        })
        .finally(() => setSubmitting(false))
    );
  };

  const handleCancel = () => {
    navigate('/performance-reviews');
  };

  if (loading) {
    return (
      <div className="text-center mt-3">
        <Spinner color="primary"/>
        <p>Loading employees...</p>
      </div>
    );
  }

  return <Form onSubmit={handleSubmit} className="mt-4">
    <Row>
      <Col md={12} style={{paddingLeft: 0}}>
        <FormGroup>
          <Label for="departmentId">Department *</Label>
          <Input
            type="select"
            name="departmentId"
            id="departmentId"
            value={formData.departmentId}
            onChange={handleDepartmentChange}
            required
          >
            <option value="">Select a department</option>
            {departments.map((department) => (
              <option key={department.id} value={department.id}>
                {department.name}
              </option>
            ))}
          </Input>
        </FormGroup>
      </Col>
    </Row>

    <Row>
      <Col md={6} style={{paddingLeft: 0}}>
        <FormGroup>
          <Label for="reporterId">Reporter *</Label>
          {departmentManager ? (
            <>
              <Input
                type="text"
                name="reporterId"
                id="reporterId"
                value={`${departmentManager.firstName} ${departmentManager.lastName} (${departmentManager.email})`}
                readOnly
                disabled
                className="bg-light"
              />
              <small className="form-text text-muted">
                Manager of selected department
              </small>
            </>
          ) : (
            <>
              <Input
                type="text"
                name="reporterId"
                id="reporterId"
                value={formData.departmentId ? "No manager assigned to this department" : "Please select a department first"}
                readOnly
                disabled
                className="bg-light"
              />
              {formData.departmentId && !departmentManager && (
                <small className="form-text text-warning">
                  Manager not found for this department. Please assign one first.
                </small>
              )}
              {!formData.departmentId && (
                <small className="form-text text-muted">
                  Please select a department first
                </small>
              )}
            </>
          )}
        </FormGroup>
      </Col>

      <Col md={6}>
        <FormGroup>
          <Label for="employeeId">Employee *</Label>
          {loadingDepartment ? (
            <div>
              <Spinner size="sm" className="me-2"/>
              <span>Loading employees...</span>
            </div>
          ) : (
            <Input
              type="select"
              name="employeeId"
              id="employeeId"
              value={formData.employeeId}
              onChange={(e) => handleChange(e, setFormData)}
              required
              disabled={!formData.departmentId}
            >
              <option value="">Select an employee</option>
              {departmentEmployees.map((employee) => (
                <option key={employee.id} value={employee.id}>
                  {employee.firstName} {employee.lastName} ({employee.email})
                </option>
              ))}
            </Input>
          )}
          {!formData.departmentId && (
            <small className="form-text text-muted">
              Please select a department first
            </small>
          )}
        </FormGroup>
      </Col>

    </Row>

    <FormGroup>
      <Label for="rawText">Performance Review Text *</Label>
      <Input
        type="textarea"
        name="rawText"
        id="rawText"
        rows="6"
        value={formData.rawText}
        onChange={(e) => handleChange(e, setFormData)}
        placeholder="Enter the performance review text. This will be analyzed to extract skills and ratings."
        required
      />
    </FormGroup>

    <FormGroup>
      <Label for="comments">Comments</Label>
      <Input
        type="textarea"
        name="comments"
        id="comments"
        rows="4"
        value={formData.comments}
        onChange={(e) => handleChange(e, setFormData)}
        placeholder="Enter any additional comments"
      />
    </FormGroup>

    <FormGroup>
      <Label for="overallRating">Overall Rating *</Label>
      <Input
        type="number"
        name="overallRating"
        id="overallRating"
        min="0"
        max="10"
        step="0.5"
        value={formData.overallRating}
        onChange={(e) => handleChange(e, setFormData)}
        placeholder="Enter overall rating (0-10)"
        required
      />
      <small className="form-text text-muted">
        Enter a rating between 0 and 10
      </small>
    </FormGroup>

    <div className="d-flex gap-2 mt-4">
      <Button
        type="submit"
        color="primary"
        disabled={submitting}
      >
        {submitting ? (
          <>
            <Spinner size="sm" className="me-2"/>
            Creating...
          </>
        ) : (
          'Create Performance Review'
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