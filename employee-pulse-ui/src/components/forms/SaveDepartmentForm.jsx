import {Button, Form, FormGroup, Input, Label, Spinner} from "reactstrap";
import {
  DEFAULT_ORGANIZATION_ID,
  GET_DEPARTMENT_URL,
  CREATE_DEPARTMENT_URL,
  UPDATE_DEPARTMENT_URL
} from "../../lib/api/apiUrls.js";
import {axiosGet, axiosPost, axiosPut} from "../../lib/api/client.js";
import {useEffect, useState} from "react";
import useCatch from "../../lib/api/useCatch.js";
import {useNavigate} from "react-router-dom";
import {handleChange} from "../../lib/formUtils.js";

export default function SaveDepartmentForm({ departmentId = null }) {
  const navigate = useNavigate();
  const {cWrapper} = useCatch();
  const isEditMode = !!departmentId;

  const [loading, setLoading] = useState(false);
  const [submitting, setSubmitting] = useState(false);

  const [formData, setFormData] = useState({
    name: ''
  });

  useEffect(() => {
    if (isEditMode && departmentId) {
      setLoading(true);
      cWrapper(() =>
        axiosGet(GET_DEPARTMENT_URL(departmentId))
          .then((response) => {
            const department = response.data;
            setFormData({
              name: department.name || ''
            });
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
        organizationId: DEFAULT_ORGANIZATION_ID
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

