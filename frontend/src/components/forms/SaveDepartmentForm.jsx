import {Button, Form, FormGroup, Input, Label, Spinner} from "reactstrap";
import {
  GET_DEPARTMENT_URL,
  GET_DEPARTMENT_EMPLOYEES_URL,
  CREATE_DEPARTMENT_URL,
  UPDATE_DEPARTMENT_URL,
  ASSIGN_MANAGER_TO_DEPARTMENT_URL
} from "../../lib/api/apiUrls.js";
import {axiosGet, axiosPost, axiosPut} from "../../lib/api/client.js";
import {useEffect, useState} from "react";
import useCatch from "../../lib/api/useCatch.js";
import {useNavigate} from "react-router-dom";
import {handleChange} from "../../lib/formUtils.js";
import { useOrganization } from "../../context/OrganizationContext.jsx";

export default function SaveDepartmentForm({ departmentId = null }) {
  const navigate = useNavigate();
  const {cWrapper} = useCatch();
  const isEditMode = !!departmentId;
  const { selectedOrganization } = useOrganization();

  const [loading, setLoading] = useState(false);
  const [loadingEmployees, setLoadingEmployees] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [assigningManager, setAssigningManager] = useState(false);

  const [departmentEmployees, setDepartmentEmployees] = useState([]);
  const [currentManagerId, setCurrentManagerId] = useState(null);

  const [formData, setFormData] = useState({
    name: '',
    managerId: ''
  });

  useEffect(() => {
    if (isEditMode && departmentId) {
      setLoading(true);
      cWrapper(() =>
        Promise.all([
          axiosGet(GET_DEPARTMENT_URL(departmentId)),
          axiosGet(GET_DEPARTMENT_EMPLOYEES_URL(departmentId))
        ])
          .then(([departmentResponse, employeesResponse]) => {
            const department = departmentResponse.data;
            const employees = employeesResponse.data;
            
            setFormData({
              name: department.name || '',
              managerId: department.managerId ? department.managerId.toString() : ''
            });
            
            setCurrentManagerId(department.managerId);
            setDepartmentEmployees(employees);
          })
          .finally(() => setLoading(false))
      );
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [isEditMode, departmentId, cWrapper]);

  const handleSubmit = (e) => {
    e.preventDefault();
    setSubmitting(true);

    if (isEditMode) {
      // Update existing department
      const payload = {
        name: formData.name
      };

      cWrapper(() =>
        axiosPut(UPDATE_DEPARTMENT_URL(departmentId), payload)
          .then(() => {
            navigate('/departments');
          })
          .finally(() => setSubmitting(false))
      );
    } else {
      // Create new department
      const payload = {
        name: formData.name,
        organizationId: selectedOrganization?.value
      };

      cWrapper(() =>
        axiosPost(CREATE_DEPARTMENT_URL(), payload)
          .then(() => {
            navigate('/departments');
          })
          .finally(() => setSubmitting(false))
      );
    }
  };

  const handleAssignManager = () => {
    if (!formData.managerId) {
      return;
    }

    setAssigningManager(true);
    cWrapper(() =>
      axiosPost(ASSIGN_MANAGER_TO_DEPARTMENT_URL(departmentId, parseInt(formData.managerId)))
        .then(() => {
          setCurrentManagerId(parseInt(formData.managerId));
          // Reload employees to refresh the list
          setLoadingEmployees(true);
          return axiosGet(GET_DEPARTMENT_EMPLOYEES_URL(departmentId));
        })
        .then((response) => {
          setDepartmentEmployees(response.data);
        })
        .finally(() => {
          setAssigningManager(false);
          setLoadingEmployees(false);
        })
    );
  };

  const handleCancel = () => {
    navigate('/departments');
  };

  if (loading) {
    return (
      <div className="text-center mt-3">
        <Spinner color="primary"/>
        <p>Loading department...</p>
      </div>
    );
  }

  return <Form onSubmit={handleSubmit} className="mt-4">
    <FormGroup>
      <Label for="name">Department Name *</Label>
      <Input
        type="text"
        name="name"
        id="name"
        value={formData.name}
        onChange={(e) => handleChange(e, setFormData)}
        placeholder="Enter department name"
        required
      />
    </FormGroup>

    {isEditMode && (
      <>
        <hr className="my-3"/>
        <FormGroup>
          <Label for="managerId">Manager</Label>
          {loadingEmployees ? (
            <div>
              <Spinner size="sm" className="me-2"/>
              <span>Loading employees...</span>
            </div>
          ) : (
            <>
              <Input
                type="select"
                name="managerId"
                id="managerId"
                value={formData.managerId}
                onChange={(e) => handleChange(e, setFormData)}
              >
                <option value="">No manager assigned</option>
                {departmentEmployees.map((employee) => (
                  <option key={employee.id} value={employee.id}>
                    {employee.firstName} {employee.lastName} ({employee.email})
                  </option>
                ))}
              </Input>
              {departmentEmployees.length === 0 && (
                <small className="form-text text-muted">
                  No employees in this department. Add employees to assign a manager.
                </small>
              )}
              {formData.managerId && formData.managerId !== (currentManagerId?.toString() || '') && (
                <div className="mt-2">
                  <Button
                    type="button"
                    color="success"
                    size="sm"
                    onClick={handleAssignManager}
                    disabled={assigningManager}
                  >
                    {assigningManager ? (
                      <>
                        <Spinner size="sm" className="me-2"/>
                        Assigning...
                      </>
                    ) : (
                      <>
                        <i className="bi bi-person-check me-2"></i>
                        Assign Manager
                      </>
                    )}
                  </Button>
                </div>
              )}
              {currentManagerId && formData.managerId === currentManagerId.toString() && (
                <small className="form-text text-success">
                  <i className="bi bi-check-circle me-1"></i>
                  Manager is currently assigned
                </small>
              )}
            </>
          )}
        </FormGroup>
      </>
    )}

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
          isEditMode ? 'Update Department' : 'Create Department'
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

