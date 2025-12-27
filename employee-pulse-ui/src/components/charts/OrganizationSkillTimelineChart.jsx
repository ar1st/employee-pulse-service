import { useState, useEffect } from 'react';
import { Card, CardBody, CardHeader, Collapse, Form, FormGroup, Label, Input, Button, Row, Col, Spinner } from 'reactstrap';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts';
import { DEFAULT_ORGANIZATION_ID, GET_SKILLS_BY_ORGANIZATION_URL, GET_DEPARTMENTS_BY_ORGANIZATION_URL, GET_ORG_DEPT_SKILL_TIMELINE_URL } from '../../lib/api/apiUrls.js';
import { axiosGet } from '../../lib/api/client.js';
import useCatch from '../../lib/api/useCatch.js';
import { handleChange } from '../../lib/formUtils.js';
import {formatDateForInput, getDefaultDates} from "../../lib/dateUtils.js";

function OrganizationSkillTimelineChart() {
  const { cWrapper } = useCatch();
  const [isOpen, setIsOpen] = useState(true);
  const [skills, setSkills] = useState([]);
  const [departments, setDepartments] = useState([]);
  const [loadingSkills, setLoadingSkills] = useState(false);
  const [loadingDepartments, setLoadingDepartments] = useState(false);
  const [loadingChart, setLoadingChart] = useState(false);
  const [chartData, setChartData] = useState(null);

  const [formData, setFormData] = useState({
    departmentId: '',
    skillId: '',
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

  const handleSubmit = (e) => {
    e.preventDefault();
    setLoadingChart(true);
    cWrapper(() =>
      axiosGet(GET_ORG_DEPT_SKILL_TIMELINE_URL(
        DEFAULT_ORGANIZATION_ID,
        formData.departmentId ? parseInt(formData.departmentId) : null,
        formData.skillId ? parseInt(formData.skillId) : null,
        formData.startDate || null,
        formData.endDate || null
      ))
        .then((response) => {
          setChartData(response.data);
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
        avgRating: point.avgRating || 0,
        minRating: point.minRating || 0,
        maxRating: point.maxRating || 0
      };
    }).sort((a, b) => a.dateValue - b.dateValue);
  };

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
            <h4 className="mb-0">Organization Skill Timeline</h4>
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
                  <Label for="skillId">Skill</Label>
                  <Input
                    type="select"
                    name="skillId"
                    id="skillId"
                    value={formData.skillId}
                    onChange={(e) => handleChange(e, setFormData)}
                    disabled={loadingSkills}
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
                  disabled={loadingChart}
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
                {chartData.organizationName}
                {chartData.departmentName && ` - ${chartData.departmentName}`}
                {selectedSkillName && ` - ${selectedSkillName}`}
              </h5>

              {chartData.skills.map((skill) => {
                const chartData = getChartDataForSkill(skill);
                if (chartData.length === 0) return null;

                // Calculate min/max/avg from the timeline points for the selected range
                const ratings = skill.timeline.map(point => point.avgRating).filter(r => r != null);
                const minRating = ratings.length > 0 ? Math.min(...ratings) : null;
                const maxRating = ratings.length > 0 ? Math.max(...ratings) : null;
                const avgRating = ratings.length > 0 
                  ? ratings.reduce((sum, r) => sum + r, 0) / ratings.length 
                  : null;

                return (
                  <div key={skill.skillId} className="mb-5">
                    <h6 className="mb-3">
                      {skill.skillName}
                      {avgRating !== null && (
                        <span className="ms-3 text-muted" style={{ fontSize: '0.9rem' }}>
                          (Avg: {avgRating.toFixed(2)}, Min: {minRating?.toFixed(2) || 'N/A'}, Max: {maxRating?.toFixed(2) || 'N/A'})
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
                          dataKey="avgRating" 
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

          {chartData && (!chartData.skills || chartData.skills.length === 0) && (
            <div className="mt-4 text-muted">
              <p>No timeline data available for the selected filters.</p>
            </div>
          )}
        </CardBody>
      </Collapse>
    </Card>
  );
}

export default OrganizationSkillTimelineChart;

