import { useState, useEffect } from 'react';
import { Card, CardBody, CardHeader, Collapse } from 'reactstrap';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts';
import { GET_EMPLOYEE_SKILL_TIMELINE_URL } from '../../lib/api/apiUrls.js';
import { axiosGet } from '../../lib/api/client.js';
import useCatch from '../../lib/api/useCatch.js';
import { useEmployeeFilter } from './EmployeeFilterContext.jsx';

function EmployeeSkillTimelineChart() {
  const { cWrapper } = useCatch();
  const [isOpen, setIsOpen] = useState(true);
  const { filterValues, triggerFetch } = useEmployeeFilter();
  const [chartData, setChartData] = useState(null);
  const [hasAttemptedChart, setHasAttemptedChart] = useState(false);
  const [loadingChart, setLoadingChart] = useState(false);

  // Fetch data when triggerFetch changes (auto-triggers when skill is selected or date range changes)
  useEffect(() => {
    if (triggerFetch === 0) return; // Don't fetch on initial mount
    
    if (!filterValues.employeeId || !filterValues.skillId) {
      return;
    }

    setLoadingChart(true);
    setHasAttemptedChart(true);
    cWrapper(() =>
      axiosGet(GET_EMPLOYEE_SKILL_TIMELINE_URL(
        parseInt(filterValues.employeeId),
        filterValues.skillId ? parseInt(filterValues.skillId) : null,
        filterValues.startDate || null,
        filterValues.endDate || null
      ))
        .then((response) => {
          setChartData(response.data || null);
        })
        .catch(() => {
          setChartData(null);
        })
        .finally(() => setLoadingChart(false))
    );
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [triggerFetch, cWrapper]);

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

  const selectedSkillName = filterValues.skillId
    ? chartData?.skills?.find(s => s.skillId === parseInt(filterValues.skillId))?.skillName || null
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
          {loadingChart && (
            <div className="mt-4 text-center">
              <p>Loading chart data...</p>
            </div>
          )}

          {chartData && chartData.skills && chartData.skills.length > 0 && (
            <div className="mt-4">
              <h5 className="mb-3">
                {chartData.firstName && chartData.lastName && `${chartData.firstName} ${chartData.lastName}`}
                {selectedSkillName && ` - ${selectedSkillName}`}
              </h5>

              {chartData.skills.map((skill) => {
                const skillChartData = getChartDataForSkill(skill);
                if (skillChartData.length === 0) return null;

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
                      <LineChart data={skillChartData} margin={{ top: 20, right: 30, left: 20, bottom: 5 }}>
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
                  <p>No timeline data available{selectedSkillName ? ` for ${selectedSkillName}` : ''}.</p>
                </div>
              )}

              {!chartData && (
                <div className="mt-4 text-muted">
                  <p>No timeline data found{selectedSkillName ? ` for ${selectedSkillName}` : ''}. Please try a different skill or date range.</p>
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

