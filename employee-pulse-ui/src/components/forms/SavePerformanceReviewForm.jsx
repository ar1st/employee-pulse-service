import {Button, Col, Form, FormGroup, Input, Label, Row, Spinner, Table} from "reactstrap";
import {
  CREATE_PERFORMANCE_REVIEW_URL, DEFAULT_ORGANIZATION_ID,
  GET_DEPARTMENT_URL, GET_DEPARTMENTS_BY_ORGANIZATION_URL,
  GET_EMPLOYEES_BY_ORGANIZATION_URL, GET_SKILLS_BY_ORGANIZATION_URL,
  ADD_SKILL_ENTRY_TO_REVIEW_URL
} from "../../lib/api/apiUrls.js";
import {axiosGet, axiosPost} from "../../lib/api/client.js";
import {useEffect, useState, useMemo} from "react";
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

  const [skills, setSkills] = useState([]);
  const [skillSearchTerm, setSkillSearchTerm] = useState('');
  const [selectedSkillId, setSelectedSkillId] = useState('');
  const [skillRating, setSkillRating] = useState('');
  const [skillEntries, setSkillEntries] = useState([]);

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
        axiosGet(GET_EMPLOYEES_BY_ORGANIZATION_URL(DEFAULT_ORGANIZATION_ID)),
        axiosGet(GET_SKILLS_BY_ORGANIZATION_URL(DEFAULT_ORGANIZATION_ID))
      ])
        .then(([departmentsResponse, employeesResponse, skillsResponse]) => {
          setDepartments(departmentsResponse.data);
          setAllEmployees(employeesResponse.data);
          setSkills(skillsResponse.data);
        })
        .finally(() => setLoading(false))
    );

  }, [cWrapper]);

  const filteredSkills = useMemo(() => {
    if (!skillSearchTerm) return skills;
    const searchLower = skillSearchTerm.toLowerCase();
    return skills.filter(skill =>
      skill.name.toLowerCase().includes(searchLower) ||
      skill.id.toString().includes(searchLower) ||
      (skill.description && skill.description.toLowerCase().includes(searchLower))
    );
  }, [skills, skillSearchTerm]);

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

  const handleAddSkillEntry = () => {
    if (!selectedSkillId || !skillRating) {
      return;
    }

    const skill = skills.find(s => s.id === parseInt(selectedSkillId));
    if (!skill) return;

    // Check if skill already added
    if (skillEntries.some(entry => entry.skillId === skill.id)) {
      return;
    }

    const newEntry = {
      skillId: skill.id,
      skillName: skill.name,
      rating: parseFloat(skillRating),
      tempId: Date.now() // Temporary ID for display
    };

    setSkillEntries([...skillEntries, newEntry]);
    setSelectedSkillId('');
    setSkillRating('');
    setSkillSearchTerm('');
  };

  const handleRemoveSkillEntry = (tempId) => {
    setSkillEntries(skillEntries.filter(entry => entry.tempId !== tempId));
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

    cWrapper(async () => {
      axiosPost(CREATE_PERFORMANCE_REVIEW_URL(), payload)
        .then((createPerformanceReviewResponse) => {
          const performanceReviewId = createPerformanceReviewResponse.data.performanceReviewId

          skillEntries.map(entry =>
            axiosPost(ADD_SKILL_ENTRY_TO_REVIEW_URL(performanceReviewId), {
              skillId: entry.skillId,
              rating: entry.rating
            })
          );

          navigate('/performance-reviews');
        })
        .finally(() => setSubmitting(false));

    });
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
      <Label for="rawText">Performance Review Text</Label>
      <Input
        type="textarea"
        name="rawText"
        id="rawText"
        rows="6"
        value={formData.rawText}
        onChange={(e) => handleChange(e, setFormData)}
        placeholder="Enter the performance review text. This will be analyzed to extract skills and ratings."
      />
    </FormGroup>

    <FormGroup>
      <Label><strong>Skill Entries</strong></Label>
      <p className="text-muted">Add skill entries manually to this performance review.</p>

      <Row className="mb-3">
        <Col md={6}>
          <Label for="skillSearch">Search Skill (by name or ID)</Label>
          <Input
            type="text"
            id="skillSearch"
            value={skillSearchTerm}
            onChange={(e) => setSkillSearchTerm(e.target.value)}
            placeholder="Type to search skills..."
          />
        </Col>
        <Col md={4}>
          <Label for="selectedSkill">Select Skill *</Label>
          <Input
            type="select"
            id="selectedSkill"
            value={selectedSkillId}
            onChange={(e) => setSelectedSkillId(e.target.value)}
            disabled={filteredSkills.length === 0}
          >
            <option value="">Choose a skill...</option>
            {filteredSkills.length === 0 && skillSearchTerm ? (
              <option value="" disabled>No skills found matching "{skillSearchTerm}"</option>
            ) : (
              filteredSkills.map((skill) => (
                <option key={skill.id} value={skill.id}>
                  {skill.name} (ID: {skill.id})
                </option>
              ))
            )}
          </Input>
          {skillSearchTerm && filteredSkills.length === 0 && (
            <small className="form-text text-muted">
              No skills found. Try a different search term.
            </small>
          )}
        </Col>
        <Col md={2}>
          <Label for="skillRating">Rating *</Label>
          <Input
            type="number"
            id="skillRating"
            min="0"
            max="5"
            step="0.1"
            value={skillRating}
            onChange={(e) => setSkillRating(e.target.value)}
            placeholder="0-5"
            disabled={!selectedSkillId}
          />
        </Col>
      </Row>

      <Button
        type="button"
        color="success"
        onClick={handleAddSkillEntry}
        disabled={!selectedSkillId || !skillRating || skillEntries.some(e => e.skillId === parseInt(selectedSkillId))}
        className="mb-3"
      >
        <i className="bi bi-plus-circle me-2"></i>
        Add Skill Entry
      </Button>

      {skillEntries.length > 0 && (
        <div className="mt-3">
          <h5>Added Skill Entries</h5>
          <Table striped bordered hover responsive>
            <thead>
            <tr>
              <th>Skill Name</th>
              <th>Skill ID</th>
              <th>Rating</th>
              <th>Actions</th>
            </tr>
            </thead>
            <tbody>
            {skillEntries.map((entry) => (
              <tr key={entry.tempId}>
                <td>{entry.skillName}</td>
                <td>{entry.skillId}</td>
                <td>{entry.rating.toFixed(1)}</td>
                <td>
                  <Button
                    type="button"
                    color="danger"
                    size="sm"
                    onClick={() => handleRemoveSkillEntry(entry.tempId)}
                  >
                    <i className="bi bi-trash"></i> Remove
                  </Button>
                </td>
              </tr>
            ))}
            </tbody>
          </Table>
        </div>
      )}
    </FormGroup>

    <hr className="my-4"/>


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