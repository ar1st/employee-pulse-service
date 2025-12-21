import { useState, useEffect } from 'react';
import { Card, CardBody, CardHeader, Form, FormGroup, Label, Input, Button, Row, Col, Spinner, Collapse } from 'reactstrap';
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts';
import { DEFAULT_ORGANIZATION_ID, GET_EMPLOYEES_BY_ORGANIZATION_URL, GET_EMPLOYEE_REPORT_URL, GET_EMPLOYEE_LATEST_SKILL_ENTRIES_URL } from '../../lib/api/apiUrls.js';
import { axiosGet } from '../../lib/api/client.js';
import useCatch from '../../lib/api/useCatch.js';
import { handleChange } from '../../lib/formUtils.js';

function EmployeePerformanceReviewsSection() {
  const { cWrapper } = useCatch();
  const [isOpen, setIsOpen] = useState(true);
  const [skills, setSkills] = useState([]);
  const [employees, setEmployees] = useState([]);
  const [loadingSkills, setLoadingSkills] = useState(false);
  const [loadingEmployees, setLoadingEmployees] = useState(false);
  const [loadingReport, setLoadingReport] = useState(false);
  const [reportData, setReportData] = useState(null);
  const [chartData, setChartData] = useState([]);
  const [hasAttemptedReport, setHasAttemptedReport] = useState(false);

  const [formData, setFormData] = useState({
    employeeId: '',
    skillId: '',
    year: new Date().getFullYear().toString()
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
    if (!reportData || !formData.skillId) {
      setChartData([]);
      return;
    }

    const selectedSkill = skills.find(s => s.id === parseInt(formData.skillId));
    const selectedSkillName = selectedSkill?.name;
    const skillData = reportData.skills?.find(s => s.skillName === selectedSkillName);

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
  }, [reportData, formData.skillId, skills]);

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!formData.employeeId || !formData.skillId || !formData.year) {
      return;
    }

    setLoadingReport(true);
    setHasAttemptedReport(true);
    cWrapper(() =>
      axiosGet(GET_EMPLOYEE_REPORT_URL(
        parseInt(formData.employeeId),
        'QUARTER',
        null,
        parseInt(formData.year)
      ))
        .then((response) => {
          setReportData(response.data || null);
        })
        .catch(() => {
          setReportData(null);
        })
        .finally(() => setLoadingReport(false))
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
          <h4 className="mb-0">Employee Performance Reviews Report</h4>
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
                <Label for="year">Year *</Label>
                <Input
                  type="number"
                  name="year"
                  id="year"
                  value={formData.year}
                  onChange={(e) => handleChange(e, setFormData)}
                  min="2000"
                  max={new Date().getFullYear() + 1}
                  required
                />
              </FormGroup>
            </Col>
            <Col md={3} className="d-flex align-items-end" style={{marginBottom: '17px'}}>
              <Button
                type="submit"
                color="primary"
                disabled={loadingReport || !formData.employeeId || !formData.skillId || !formData.year}
              >
                {loadingReport ? (
                  <>
                    <Spinner size="sm" className="me-2" />
                    Loading...
                  </>
                ) : (
                  'Generate Report'
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
              {` - ${formData.year}`}
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

        {hasAttemptedReport && !loadingReport && (
          <>
            {reportData && chartData.length === 0 && (
              <div className="mt-4 text-muted">
                <p>No review data available for {selectedEmployee ? `${selectedEmployee.firstName} ${selectedEmployee.lastName}` : 'the selected employee'}{selectedSkillName ? ` - ${selectedSkillName}` : ''} for the year {formData.year}.</p>
              </div>
            )}
            {!reportData && (
              <div className="mt-4 text-muted">
                <p>No review data found for {selectedEmployee ? `${selectedEmployee.firstName} ${selectedEmployee.lastName}` : 'the selected employee'}{selectedSkillName ? ` - ${selectedSkillName}` : ''} for the year {formData.year}. Please try a different employee, skill, or year.</p>
              </div>
            )}
          </>
        )}
        </CardBody>
      </Collapse>
    </Card>
  );
}

export default EmployeePerformanceReviewsSection;

