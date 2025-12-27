import { useState, useEffect } from 'react';
import { Card, CardBody, CardHeader, Form, FormGroup, Label, Input, Button, Row, Col, Spinner, Collapse, Table } from 'reactstrap';
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts';
import { DEFAULT_ORGANIZATION_ID, GET_SKILLS_BY_ORGANIZATION_URL, GET_DEPARTMENTS_BY_ORGANIZATION_URL, GET_ORG_DEPT_REPORT_URL } from '../../lib/api/apiUrls.js';
import { axiosGet } from '../../lib/api/client.js';
import useCatch from '../../lib/api/useCatch.js';
import { handleChange } from '../../lib/formUtils.js';
import {getDefaultDates} from '../../lib/dateUtils.js';

function OrganizationPerformanceReviewChart() {
  const { cWrapper } = useCatch();
  const [isOpen, setIsOpen] = useState(true);
  const [skills, setSkills] = useState([]);
  const [departments, setDepartments] = useState([]);
  const [loadingSkills, setLoadingSkills] = useState(false);
  const [loadingDepartments, setLoadingDepartments] = useState(false);
  const [loadingChart, setLoadingChart] = useState(false);
  const [chartResponseData, setChartResponseData] = useState(null);
  const [chartData, setChartData] = useState([]);

  const [formData, setFormData] = useState({
    skillId: '',
    departmentId: '',
    ...getDefaultDates()
  });

  // Load skills and departments on mount
  useEffect(() => {
    setLoadingSkills(true);
    setLoadingDepartments(true);

    cWrapper(() =>
      Promise.all([
        axiosGet(GET_SKILLS_BY_ORGANIZATION_URL(DEFAULT_ORGANIZATION_ID)),
        axiosGet(GET_DEPARTMENTS_BY_ORGANIZATION_URL(DEFAULT_ORGANIZATION_ID))
      ])
        .then(([skillsResponse, departmentsResponse]) => {
          setSkills(skillsResponse.data || []);
          const depts = departmentsResponse.data.content || departmentsResponse.data || [];
          setDepartments(Array.isArray(depts) ? depts : []);
        })
        .finally(() => {
          setLoadingSkills(false);
          setLoadingDepartments(false);
        })
    );
  }, [cWrapper]);

  useEffect(() => {
    if (!chartResponseData || !formData.skillId) {
      setChartData([]);
      return;
    }

    // Since we're filtering by skillId on the backend, there should be at most one skill in the response
    const selectedSkill = chartResponseData.skills?.[0];

    if (!selectedSkill || !selectedSkill.periods || selectedSkill.periods.length === 0) {
      setChartData([]);
      return;
    }

    // Transform periods into chart data
    const chartDataPoints = selectedSkill.periods.map(period => {
      const date = new Date(period.periodStart);
      const quarter = `Q${Math.floor(date.getMonth() / 3) + 1} ${date.getFullYear()}`;

      return {
        quarter,
        avgRating: period.avgRating || 0,
        minRating: period.minRating || 0,
        maxRating: period.maxRating || 0,
        employeeCount: period.employeeCount || 0
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
  }, [chartResponseData, formData.skillId]);

  const handleSubmit = (e) => {
    e.preventDefault();
    if (!formData.skillId || !formData.startDate || !formData.endDate) {
      return;
    }

    setLoadingChart(true);
    cWrapper(() =>
      axiosGet(GET_ORG_DEPT_REPORT_URL(
        DEFAULT_ORGANIZATION_ID,
        formData.departmentId ? parseInt(formData.departmentId) : null, // deptId
        parseInt(formData.skillId), // skillId
        formData.startDate || null,
        formData.endDate || null
      ))
        .then((response) => {
          setChartResponseData(response.data);
        })
        .finally(() => setLoadingChart(false))
    );
  };

  const selectedSkillName = chartResponseData?.skills?.[0]?.skillName ||
    skills.find(s => s.id === parseInt(formData.skillId))?.name || '';

  const selectedDepartmentName = formData.departmentId
    ? departments.find(d => d.id === parseInt(formData.departmentId))?.name
    : null;

  return (
    <Card className="mb-4">
      <CardHeader
        style={{ cursor: 'pointer' }}
        onClick={() => setIsOpen(!isOpen)}
      >
        <div className="d-flex justify-content-between align-items-center">
          <h4 className="mb-0">Organization Performance Review Chart</h4>
          <i className={`bi bi-chevron-${isOpen ? 'up' : 'down'}`}></i>
        </div>
      </CardHeader>
      <Collapse isOpen={isOpen}>
        <CardBody>
        <Form onSubmit={handleSubmit}>
          <Row>
            <Col md={3}>
              <FormGroup>
                <Label for="departmentId">Department</Label>
                <Input
                  type="select"
                  name="departmentId"
                  id="departmentId"
                  value={formData.departmentId}
                  onChange={(e) => handleChange(e, setFormData)}
                  disabled={loadingDepartments}
                >
                  <option value="">All Departments</option>
                  {departments.map((department) => (
                    <option key={department.id} value={department.id}>
                      {department.name}
                    </option>
                  ))}
                </Input>
                {loadingDepartments && (
                  <small className="text-muted">
                    <Spinner size="sm" className="me-1" />
                    Loading departments...
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
                disabled={loadingChart || !formData.skillId || !formData.startDate || !formData.endDate}
              >
                {loadingChart ? (
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
              {selectedSkillName}
              {selectedDepartmentName && ` - ${selectedDepartmentName}`}
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

            {/* Employee Count */}
            <div className="mt-4">
              <h6>Employee Count by Quarter</h6>
              <div className="d-flex flex-wrap gap-3 align-items-center">
                {chartData.map((data, index) => (
                  <span key={index} className="p-2 border rounded">
                    <span className="fw-bold">{data.quarter}:</span> {data.employeeCount || 0}
                  </span>
                ))}
              </div>
            </div>
          </div>
        )}

        {chartResponseData && chartData.length === 0 && (
          <div className="mt-4 text-muted">
            <p>No data available for the selected skill and date range.</p>
          </div>
        )}
        </CardBody>
      </Collapse>
    </Card>
  );
}

export default OrganizationPerformanceReviewChart;
