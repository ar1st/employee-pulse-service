import { useState, useEffect } from 'react';
import { Card, CardBody, CardHeader, Collapse, Form, FormGroup, Label, Input, Button, Row, Col, Spinner } from 'reactstrap';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts';
import { DEFAULT_ORGANIZATION_ID, GET_EMPLOYEES_BY_ORGANIZATION_URL, GET_EMPLOYEE_SKILL_TIMELINE_URL, GET_EMPLOYEE_LATEST_SKILL_ENTRIES_URL } from '../../lib/api/apiUrls.js';
import { axiosGet } from '../../lib/api/client.js';
import useCatch from '../../lib/api/useCatch.js';
import { handleChange } from '../../lib/formUtils.js';
import {formatDateForInput} from "../../lib/dateUtils.js";

function EmployeeSkillTimelineChart() {
  const { cWrapper } = useCatch();
  const [isOpen, setIsOpen] = useState(true);
  const [skills, setSkills] = useState([]);
  const [employees, setEmployees] = useState([]);
  const [loadingSkills, setLoadingSkills] = useState(false);
  const [loadingEmployees, setLoadingEmployees] = useState(false);
  const [loadingChart, setLoadingChart] = useState(false);
  const [chartData, setChartData] = useState(null);
  const [hasAttemptedChart, setHasAttemptedChart] = useState(false);

  const getDefaultDates = () => {
    const endDate = new Date(2025, 11, 31);
    const startDate = new Date(2025, 0, 1);
    return {
      startDate: formatDateForInput(startDate),
      endDate: formatDateForInput(endDate)
    };
  };

  const [formData, setFormData] = useState({
    employeeId: '',
    skillId: '',
    ...getDefaultDates()
  });

  useEffect(() => {
    setLoadingEmployees(true);
    
    cWrapper(() =>
      axiosGet(GET_EMPLOYEES_BY_ORGANIZATION_URL(DEFAULT_ORGANIZATION_ID))
        .then((employeesResponse) => {
          const emps = employeesResponse.data.content || employeesResponse.data || [];
          setEmployees(Array.isArray(emps) ? emps : []);
        })
        .finally(() => {
          setLoadingEmployees(false);
        })
    );
  }, [cWrapper]);

  // Load skills when employee is selected
  useEffect(() => {
    if (!formData.employeeId) {
      setSkills([]);
      setFormData(prev => ({ ...prev, skillId: '' }));
      return;
    }

    setLoadingSkills(true);
    cWrapper(() =>
      axiosGet(GET_EMPLOYEE_LATEST_SKILL_ENTRIES_URL(parseInt(formData.employeeId)))
        .then((response) => {
          // Convert SkillToRatingDto to skill format with id and name
          const employeeSkills = (response.data || []).map(skillEntry => ({
            id: skillEntry.skillId,
            name: skillEntry.skillName
          }));
          setSkills(employeeSkills);
          // Clear skill selection when employee changes
          setFormData(prev => ({ ...prev, skillId: '' }));
        })
        .catch(() => {
          setSkills([]);
        })
        .finally(() => {
          setLoadingSkills(false);
        })
    );
  }, [formData.employeeId, cWrapper]);

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!formData.employeeId) {
      return;
    }

    setLoadingChart(true);
    setHasAttemptedChart(true);
    cWrapper(() =>
      axiosGet(GET_EMPLOYEE_SKILL_TIMELINE_URL(
        parseInt(formData.employeeId),
        formData.skillId ? parseInt(formData.skillId) : null,
        formData.startDate || null,
        formData.endDate || null
      ))
        .then((response) => {
          setChartData(response.data);
        })
        .catch(() => {
          setChartData(null);
        })
        .finally(() => setLoadingChart(false))
    );
  };

  // Transform timeline data for chart
  const getChartDataForSkill = (skill) => {
    if (!skill || !skill.timeline || skill.timeline.length === 0) {
      return [];
    }

    return skill.timeline.map(point => {
      const date = new Date(point.date);
      
      return {
        date: date.toLocaleDateString('en-US', { 
          year: 'numeric', 
          month: 'short', 
          day: 'numeric' 
        }),
        dateValue: date.getTime(),
        rating: point.rating || 0
      };
    }).sort((a, b) => a.dateValue - b.dateValue);
  };

  const selectedEmployee = employees.find(e => e.id === parseInt(formData.employeeId));
  const selectedSkillName = formData.skillId
    ? skills.find(s => s.id === parseInt(formData.skillId))?.name
    : null;

  return (
    <Card className="mb-4">
      <CardHeader 
        style={{ cursor: 'pointer' }}
        onClick={() => setIsOpen(!isOpen)}
      >
        <div className="d-flex justify-content-between align-items-center">
          <div>
            <h4 className="mb-0">Employee Skill Timeline</h4>
          </div>
          <i className={`bi bi-chevron-${isOpen ? 'up' : 'down'}`}></i>
        </div>
      </CardHeader>
      <Collapse isOpen={isOpen}>
        <CardBody>
          <Form onSubmit={handleSubmit}>
            <Row>
              <Col md={3}>
                <FormGroup>
                  <Label for="employeeId">Employee *</Label>
                  <Input
                    type="select"
                    name="employeeId"
                    id="employeeId"
                    value={formData.employeeId}
                    onChange={(e) => handleChange(e, setFormData)}
                    required
                    disabled={loadingEmployees}
                  >
                    <option value="">Select an employee</option>
                    {employees.map((employee) => (
                      <option key={employee.id} value={employee.id}>
                        {employee.firstName} {employee.lastName}
                      </option>
                    ))}
                  </Input>
                  {loadingEmployees && (
                    <small className="text-muted">
                      <Spinner size="sm" className="me-1" />
                      Loading employees...
                    </small>
                  )}
                </FormGroup>
              </Col>

              <Col md={3}>
                <FormGroup>
                  <Label for="skillId">Skill</Label>
                  <Input
                    type="select"
                    name="skillId"
                    id="skillId"
                    value={formData.skillId}
                    onChange={(e) => handleChange(e, setFormData)}
                    disabled={loadingSkills || !formData.employeeId || skills.length === 0}
                  >
                    <option value="">All Skills</option>
                    {skills.map((skill) => (
                      <option key={skill.id} value={skill.id}>
                        {skill.name}
                      </option>
                    ))}
                  </Input>
                  {loadingSkills && (
                    <small className="text-muted">
                      <Spinner size="sm" className="me-1" />
                      Loading skills...
                    </small>
                  )}
                  {!loadingSkills && formData.employeeId && skills.length === 0 && (
                    <small className="text-muted">
                      No skills found for this employee.
                    </small>
                  )}
                </FormGroup>
              </Col>

              <Col md={3}>
                <FormGroup>
                  <Label for="startDate">Start Date</Label>
                  <Input
                    type="date"
                    name="startDate"
                    id="startDate"
                    value={formData.startDate}
                    onChange={(e) => handleChange(e, setFormData)}
                  />
                </FormGroup>
              </Col>

              <Col md={3}>
                <FormGroup>
                  <Label for="endDate">End Date</Label>
                  <Input
                    type="date"
                    name="endDate"
                    id="endDate"
                    value={formData.endDate}
                    onChange={(e) => handleChange(e, setFormData)}
                  />
                </FormGroup>
              </Col>
            </Row>
            <Row>
              <Col md={12} className="d-flex justify-content-end">
                <Button
                  type="submit"
                  color="primary"
                  disabled={loadingChart || !formData.employeeId}
                >
                  {loadingChart ? (
                    <>
                      <Spinner size="sm" className="me-2" />
                      Loading...
                    </>
                  ) : (
                    'Generate Timeline'
                  )}
                </Button>
              </Col>
            </Row>
          </Form>

          {chartData && chartData.skills && chartData.skills.length > 0 && (
            <div className="mt-4">
              <h5 className="mb-3">
                {chartData.firstName} {chartData.lastName}
                {selectedSkillName && ` - ${selectedSkillName}`}
              </h5>

              {chartData.skills.map((skill) => {
                const chartData = getChartDataForSkill(skill);
                if (chartData.length === 0) return null;

                return (
                  <div key={skill.skillId} className="mb-5">
                    <h6 className="mb-3">
                      {skill.skillName}
                      {skill.avgRating !== null && skill.avgRating !== undefined && (
                        <span className="ms-3 text-muted" style={{ fontSize: '0.9rem' }}>
                          (Avg: {skill.avgRating.toFixed(2)}, Min: {skill.minRating?.toFixed(2) || 'N/A'}, Max: {skill.maxRating?.toFixed(2) || 'N/A'})
                        </span>
                      )}
                    </h6>
                    <ResponsiveContainer width="100%" height={400}>
                      <LineChart data={chartData} margin={{ top: 20, right: 30, left: 20, bottom: 5 }}>
                        <CartesianGrid strokeDasharray="3 3" />
                        <XAxis 
                          dataKey="date" 
                          angle={-45}
                          textAnchor="end"
                          height={80}
                        />
                        <YAxis 
                          label={{ value: 'Rating', angle: -90, position: 'insideLeft' }}
                          domain={[0, 5]}
                          ticks={[0, 1, 2, 3, 4, 5]}
                        />
                        <Tooltip />
                        <Legend />
                        <Line 
                          type="monotone" 
                          dataKey="rating" 
                          stroke="#8884d8" 
                          name="Rating"
                          strokeWidth={2}
                          dot={{ r: 4 }}
                        />
                      </LineChart>
                    </ResponsiveContainer>
                  </div>
                );
              })}
            </div>
          )}

          {hasAttemptedChart && !loadingChart && (
            <>
              {chartData && (!chartData.skills || chartData.skills.length === 0) && (
                <div className="mt-4 text-muted">
                  <p>No timeline data available for {selectedEmployee ? `${selectedEmployee.firstName} ${selectedEmployee.lastName}` : 'the selected employee'}{selectedSkillName ? ` - ${selectedSkillName}` : ''}.</p>
                </div>
              )}

              {!chartData && (
                <div className="mt-4 text-muted">
                  <p>No timeline data found for {selectedEmployee ? `${selectedEmployee.firstName} ${selectedEmployee.lastName}` : 'the selected employee'}{selectedSkillName ? ` - ${selectedSkillName}` : ''}. Please try a different employee or skill.</p>
                </div>
              )}
            </>
          )}
        </CardBody>
      </Collapse>
    </Card>
  );
}

export default EmployeeSkillTimelineChart;

