import { useState, useEffect } from 'react';
import { Card, CardBody, CardHeader, Form, FormGroup, Label, Input, Button, Row, Col, Spinner, Collapse } from 'reactstrap';
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts';
import { DEFAULT_ORGANIZATION_ID, GET_EMPLOYEES_BY_ORGANIZATION_URL, GET_EMPLOYEE_REPORT_URL, GET_EMPLOYEE_LATEST_SKILL_ENTRIES_URL } from '../../lib/api/apiUrls.js';
import { axiosGet } from '../../lib/api/client.js';
import useCatch from '../../lib/api/useCatch.js';
import { handleChange } from '../../lib/formUtils.js';
import { getDefaultDates } from '../../lib/dateUtils.js';

function EmployeePerformanceReviewChart() {
  const { cWrapper } = useCatch();
  const [isOpen, setIsOpen] = useState(true);
  const [skills, setSkills] = useState([]);
  const [employees, setEmployees] = useState([]);
  const [loadingSkills, setLoadingSkills] = useState(false);
  const [loadingEmployees, setLoadingEmployees] = useState(false);
  const [loadingCharts, setLoadingCharts] = useState(false);
  const [chartResponseData, setChartResponseData] = useState(null);
  const [chartData, setChartData] = useState([]);
  const [hasAttemptedChart, setHasAttemptedChart] = useState(false);

  const [formData, setFormData] = useState({
    employeeId: '',
    skillId: '',
    ...getDefaultDates()
  });

  // Load employees on mount
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

  useEffect(() => {
    if (!chartResponseData || !formData.skillId) {
      setChartData([]);
      return;
    }

    const selectedSkill = skills.find(s => s.id === parseInt(formData.skillId));
    const selectedSkillName = selectedSkill?.name;
    const skillData = chartResponseData.skills?.find(s => s.skillName === selectedSkillName);

    if (!skillData || !skillData.periods || skillData.periods.length === 0) {
      setChartData([]);
      return;
    }

    // Transform periods into chart data
    const chartDataPoints = skillData.periods.map(period => {
      const date = new Date(period.periodStart);
      const quarter = `Q${Math.floor(date.getMonth() / 3) + 1} ${date.getFullYear()}`;
      
      return {
        quarter,
        avgRating: period.avgRating || 0,
        minRating: period.minRating || 0,
        maxRating: period.maxRating || 0
      };
    });

    // Sort by date (oldest first)
    chartDataPoints.sort((a, b) => {
      const [qA, yA] = a.quarter.split(' ');
      const [qB, yB] = b.quarter.split(' ');
      if (yA !== yB) return parseInt(yA) - parseInt(yB);
      return parseInt(qA[1]) - parseInt(qB[1]);
    });

    setChartData(chartDataPoints);
  }, [chartResponseData, formData.skillId, skills]);

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!formData.employeeId || !formData.skillId || !formData.startDate || !formData.endDate) {
      return;
    }

    setLoadingCharts(true);
    setHasAttemptedChart(true);
    cWrapper(() =>
      axiosGet(GET_EMPLOYEE_REPORT_URL(
        parseInt(formData.employeeId),
        formData.startDate || null,
        formData.endDate || null
      ))
        .then((response) => {
          setChartResponseData(response.data || null);
        })
        .catch(() => {
          setChartResponseData(null);
        })
        .finally(() => setLoadingCharts(false))
    );
  };

  const selectedSkillName = skills.find(s => s.id === parseInt(formData.skillId))?.name || '';

  const selectedEmployee = employees.find(e => e.id === parseInt(formData.employeeId));

  return (
    <Card className="mb-4">
      <CardHeader 
        style={{ cursor: 'pointer' }}
        onClick={() => setIsOpen(!isOpen)}
      >
        <div className="d-flex justify-content-between align-items-center">
          <h4 className="mb-0">Employee Performance Review Chart</h4>
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
                <Label for="skillId">Skill *</Label>
                <Input
                  type="select"
                  name="skillId"
                  id="skillId"
                  value={formData.skillId}
                  onChange={(e) => handleChange(e, setFormData)}
                  required
                  disabled={loadingSkills}
                >
                  <option value="">Select a skill</option>
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
              </FormGroup>
            </Col>
            <Col md={3}>
              <FormGroup>
                <Label for="startDate">Start Date *</Label>
                <Input
                  type="date"
                  name="startDate"
                  id="startDate"
                  value={formData.startDate}
                  onChange={(e) => handleChange(e, setFormData)}
                  required
                />
              </FormGroup>
            </Col>
            <Col md={3}>
              <FormGroup>
                <Label for="endDate">End Date *</Label>
                <Input
                  type="date"
                  name="endDate"
                  id="endDate"
                  value={formData.endDate}
                  onChange={(e) => handleChange(e, setFormData)}
                  required
                />
              </FormGroup>
            </Col>
          </Row>
          <Row>
            <Col md={12} className="d-flex justify-content-end">
              <Button
                type="submit"
                color="primary"
                disabled={loadingCharts || !formData.employeeId || !formData.skillId || !formData.startDate || !formData.endDate}
              >
                {loadingCharts ? (
                  <>
                    <Spinner size="sm" className="me-2" />
                    Loading...
                  </>
                ) : (
                  'Generate Chart'
                )}
              </Button>
            </Col>
          </Row>
        </Form>

        {chartData.length > 0 && (
          <div className="mt-4">
            <h5 className="mb-3">
              {selectedEmployee && `${selectedEmployee.firstName} ${selectedEmployee.lastName}`}
              {selectedSkillName && ` - ${selectedSkillName}`}
              {` - ${formData.startDate} to ${formData.endDate}`}
            </h5>
            <ResponsiveContainer width="100%" height={400}>
              <BarChart data={chartData} margin={{ top: 20, right: 30, left: 20, bottom: 5 }}>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="quarter" />
                <YAxis 
                  label={{ value: 'Rating', angle: -90, position: 'insideLeft' }}
                  domain={[0, 5]}
                  ticks={[0, 1, 2, 3, 4, 5]}
                />
                <Tooltip />
                <Legend />
                <Bar dataKey="avgRating" fill="#8884d8" name="Average Rating" />
                <Bar dataKey="maxRating" fill="#ff7300" name="Max Rating" />
                <Bar dataKey="minRating" fill="#ffc658" name="Min Rating" />
              </BarChart>
            </ResponsiveContainer>
          </div>
        )}

        {hasAttemptedChart && !loadingCharts && (
          <>
            {chartResponseData && chartData.length === 0 && (
              <div className="mt-4 text-muted">
                <p>No review data available for {selectedEmployee ? `${selectedEmployee.firstName} ${selectedEmployee.lastName}` : 'the selected employee'}{selectedSkillName ? ` - ${selectedSkillName}` : ''} for the selected date range.</p>
              </div>
            )}
            {!chartResponseData && (
              <div className="mt-4 text-muted">
                <p>No review data found for {selectedEmployee ? `${selectedEmployee.firstName} ${selectedEmployee.lastName}` : 'the selected employee'}{selectedSkillName ? ` - ${selectedSkillName}` : ''} for the selected date range. Please try a different employee, skill, or date range.</p>
              </div>
            )}
          </>
        )}
        </CardBody>
      </Collapse>
    </Card>
  );
}

export default EmployeePerformanceReviewChart;

