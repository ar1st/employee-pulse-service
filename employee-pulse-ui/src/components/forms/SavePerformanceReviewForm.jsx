import {Button, Col, Form, FormGroup, Input, Label, Row, Spinner} from "reactstrap";
import {
  CREATE_PERFORMANCE_REVIEW_URL, DEFAULT_ORGANIZATION_ID,
  GET_DEPARTMENT_URL, GET_DEPARTMENTS_BY_ORGANIZATION_URL,
  GET_EMPLOYEES_BY_ORGANIZATION_URL,
  GET_PERFORMANCE_REVIEW_URL,
  UPDATE_PERFORMANCE_REVIEW_URL,
  ADD_SKILL_ENTRY_TO_REVIEW_URL,
  ADD_SKILL_ENTRIES_BULK_TO_REVIEW_URL,
  DELETE_SKILL_ENTRY_FROM_REVIEW_URL
} from "../../lib/api/apiUrls.js";
import {axiosGet, axiosPost, axiosPut, axiosDelete} from "../../lib/api/client.js";
import {useEffect, useState} from "react";
import useCatch from "../../lib/api/useCatch.js";
import {useNavigate} from "react-router-dom";
import {handleChange} from "../../lib/formUtils.js";
import SkillEntrySection from "./SkillEntrySection.jsx";

export default function SavePerformanceReviewForm({ reviewId = null }) {
  const isEditMode = !!reviewId;
  const navigate = useNavigate();
  const {cWrapper} = useCatch();

  const [departments, setDepartments] = useState([]);
  const [allEmployees, setAllEmployees] = useState([]);
  const [departmentEmployees, setDepartmentEmployees] = useState([]);
  const [departmentManager, setDepartmentManager] = useState(null);
  const [loading, setLoading] = useState(false);
  const [loadingDepartment, setLoadingDepartment] = useState(false);
  const [loadingReview, setLoadingReview] = useState(false);
  const [submitting, setSubmitting] = useState(false);

  const [skillEntries, setSkillEntries] = useState([]);
  const [existingSkillEntryIds, setExistingSkillEntryIds] = useState([]);

  const [formData, setFormData] = useState({
    departmentId: '',
    employeeId: '',
    reporterId: '',
    rawText: '',
    comments: '',
    overallRating: '',
    reviewDate: new Date().toISOString().split('T')[0] // Default to today's date
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

  useEffect(() => {
    if (isEditMode && reviewId && departments.length > 0 && allEmployees.length > 0) {
      setLoadingReview(true);
      cWrapper(() =>
        axiosGet(GET_PERFORMANCE_REVIEW_URL(reviewId))
          .then((response) => {
            const review = response.data;
            const department = departments.find(d => d.name === review.departmentName);
            const departmentId = department ? department.id.toString() : '';

            // Format reviewDate for HTML date input (YYYY-MM-DD)
            let formattedReviewDate = '';
            if (review.reviewDate) {
              const date = new Date(review.reviewDate);
              formattedReviewDate = date.toISOString().split('T')[0];
            } else if (review.reviewDateTime) {
              const date = new Date(review.reviewDateTime);
              formattedReviewDate = date.toISOString().split('T')[0];
            }

            setFormData({
              departmentId: departmentId,
              employeeId: '',
              reporterId: '',
              rawText: review.rawText || '',
              comments: review.comments || '',
              overallRating: review.overallRating?.toString() || '',
              reviewDate: formattedReviewDate
            });

            // Load department and employee info
            if (department) {
              loadDepartmentData(department.id, review);
            }

            // Load skill entries
            if (review.skillEntryDtos && review.skillEntryDtos.length > 0) {
              const entries = review.skillEntryDtos.map(entry => ({
                skillId: entry.skillId,
                skillName: entry.skillName,
                rating: entry.rating,
                tempId: entry.id || Date.now() + Math.random(),
                entryId: entry.id // Store the actual entry ID for deletion
              }));
              setSkillEntries(entries);
              setExistingSkillEntryIds(entries.filter(e => e.entryId).map(e => e.entryId));
            }
          })
          .finally(() => setLoadingReview(false))
      );
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [isEditMode, reviewId, departments, allEmployees, cWrapper]);

  const loadDepartmentData = (departmentId, review = null) => {
    setLoadingDepartment(true);
    cWrapper(() =>
      axiosGet(GET_DEPARTMENT_URL(departmentId))
        .then((departmentResponse) => {
          const department = departmentResponse.data;
          setDepartmentEmployees(allEmployees.filter(it => (it.departmentId === department.id) && it.id !== department.managerId));

          let manager = null;
          if (department.managerId) {
            manager = allEmployees.find(emp => emp.id === department.managerId);
          }

          if (manager) {
            setDepartmentManager(manager);
            setFormData(prev => ({
              ...prev,
              departmentId: department.id.toString(),
              reporterId: manager.id.toString()
            }));
          }

          // If editing, set the employee
          if (review && review.employeeName) {
            const employee = allEmployees.find(emp => 
              `${emp.firstName} ${emp.lastName}` === review.employeeName
            );
            if (employee) {
              setFormData(prev => ({
                ...prev,
                employeeId: employee.id.toString()
              }));
            }
          }
        })
        .finally(() => setLoadingDepartment(false))
    );
  };

  const handleDepartmentChange = (e) => {
    if (isEditMode) {
      return; // Don't allow changing department in edit mode
    }

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

    loadDepartmentData(departmentId);
  };


  const handleSubmit = (e) => {
    e.preventDefault();
    setSubmitting(true);

    if (isEditMode) {
      // Update existing performance review
      const payload = {
        rawText: formData.rawText,
        comments: formData.comments,
        overallRating: parseFloat(formData.overallRating),
        reviewDate: formData.reviewDate || null
      };

      cWrapper(async () => {
        // Update the performance review
        await axiosPut(UPDATE_PERFORMANCE_REVIEW_URL(reviewId), payload);

        // Delete all existing skill entries
        const deletePromises = existingSkillEntryIds.map(entryId =>
          axiosDelete(DELETE_SKILL_ENTRY_FROM_REVIEW_URL(reviewId, entryId))
        );
        await Promise.all(deletePromises);

        // Add all new skill entries in bulk
        if (skillEntries.length > 0) {
          const skillEntriesPayload = skillEntries.map(entry => ({
            skillId: entry.skillId,
            rating: entry.rating,
            entryDate: formData.reviewDate || null
          }));
          await axiosPost(ADD_SKILL_ENTRIES_BULK_TO_REVIEW_URL(reviewId), skillEntriesPayload);
        }

        navigate('/performance-reviews');
      })
        .finally(() => setSubmitting(false));
    } else {
      // Create new performance review
      const payload = {
        employeeId: parseInt(formData.employeeId),
        reporterId: parseInt(formData.reporterId),
        rawText: formData.rawText,
        comments: formData.comments,
        overallRating: parseFloat(formData.overallRating),
        reviewDate: formData.reviewDate || null
      };

      cWrapper(async () => {
        const createPerformanceReviewResponse = await axiosPost(CREATE_PERFORMANCE_REVIEW_URL(), payload);
        const performanceReviewId = createPerformanceReviewResponse.data.performanceReviewId;

        // Add all skill entries in bulk
        if (skillEntries.length > 0) {
          const skillEntriesPayload = skillEntries.map(entry => ({
            skillId: entry.skillId,
            rating: entry.rating,
            entryDate: formData.reviewDate || null
          }));
          await axiosPost(ADD_SKILL_ENTRIES_BULK_TO_REVIEW_URL(performanceReviewId), skillEntriesPayload);
        }

        navigate('/performance-reviews');
      })
        .finally(() => setSubmitting(false));
    }
  };

  const handleCancel = () => {
    navigate('/performance-reviews');
  };

  if (loading || loadingReview) {
    return (
      <div className="text-center mt-3">
        <Spinner color="primary"/>
        <p>{loadingReview ? 'Loading performance review...' : 'Loading employees...'}</p>
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
            disabled={isEditMode}
            readOnly={isEditMode}
            className={isEditMode ? "bg-light" : ""}
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
              disabled={!formData.departmentId || isEditMode}
              readOnly={isEditMode}
              className={isEditMode ? "bg-light" : ""}
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

    <hr className="my-3"/>

    <SkillEntrySection
      rawText={formData.rawText}
      onRawTextChange={(e) => handleChange(e, setFormData)}
      skillEntries={skillEntries}
      onSkillEntriesChange={setSkillEntries}
    />

    <hr className="my-3"/>

    <FormGroup>
      <Label for="reviewDate">Review Date *</Label>
      <Input
        type="date"
        name="reviewDate"
        id="reviewDate"
        value={formData.reviewDate}
        onChange={(e) => handleChange(e, setFormData)}
        required
      />
      <small className="form-text text-muted">
        Select the date of the performance review
      </small>
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
        max="5"
        step="0.5"
        value={formData.overallRating}
        onChange={(e) => handleChange(e, setFormData)}
        placeholder="Enter overall rating (0-5)"
        required
      />
      <small className="form-text text-muted">
        Enter a rating between 0 and 5
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
            {isEditMode ? 'Updating...' : 'Creating...'}
          </>
        ) : (
          isEditMode ? 'Update Performance Review' : 'Create Performance Review'
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