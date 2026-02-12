import { useState, useEffect } from 'react';
import { Card, CardBody, CardHeader, Collapse } from 'reactstrap';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts';
import { GET_ORG_DEPT_SKILL_TIMELINE_URL } from '../../lib/api/apiUrls.js';
import { axiosGet } from '../../lib/api/client.js';
import useCatch from '../../lib/api/useCatch.js';
import { useOrganizationFilter } from './OrganizationFilterContext.jsx';
import { useOrganization } from "../../context/OrganizationContext.jsx";

function OrganizationSkillTimelineChart() {
  const { cWrapper } = useCatch();
  const [isOpen, setIsOpen] = useState(true);
  const { filterValues, triggerFetch } = useOrganizationFilter();
  const [chartData, setChartData] = useState(null);
  const [hasAttemptedChart, setHasAttemptedChart] = useState(false);
  const [loadingChart, setLoadingChart] = useState(false);
  const { selectedOrganization } = useOrganization();

  // Fetch data when triggerFetch changes (auto-triggers when skill is selected or date range changes)
  useEffect(() => {
    if (!selectedOrganization?.value || !filterValues.skillId) {
      setChartData(null);
      setHasAttemptedChart(false);
      return;
    }

    // Skip initial mount - wait for trigger to be set
    if (triggerFetch === 0) {
      return;
    }

    setLoadingChart(true);
    setHasAttemptedChart(true);
    const orgId = selectedOrganization?.value;

    cWrapper(() =>
      axiosGet(GET_ORG_DEPT_SKILL_TIMELINE_URL(
        orgId,
        filterValues.departmentId ? parseInt(filterValues.departmentId) : null,
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
  }, [triggerFetch, cWrapper, selectedOrganization]);

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
            <h4 className="mb-0">Organization Skill Timeline</h4>
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

          {hasAttemptedChart && !loadingChart && (
            <>
              {chartData && (!chartData.skills || chartData.skills.length === 0) && (
                <div className="mt-4 text-muted">
                  <p>No timeline data available for the selected filters.</p>
                </div>
              )}
              {!chartData && (
                <div className="mt-4 text-muted">
                  <p>No timeline data found for the selected filters. Please try a different department, skill, or date range.</p>
                </div>
              )}
            </>
          )}
        </CardBody>
      </Collapse>
    </Card>
  );
}

export default OrganizationSkillTimelineChart;

