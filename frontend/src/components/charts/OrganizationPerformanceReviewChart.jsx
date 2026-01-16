import { useState, useEffect } from 'react';
import { Card, CardBody, CardHeader, Collapse } from 'reactstrap';
import { BarChart, Bar, LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts';
import { DEFAULT_ORGANIZATION_ID, GET_ORG_DEPT_REPORT_URL } from '../../lib/api/apiUrls.js';
import { axiosGet } from '../../lib/api/client.js';
import useCatch from '../../lib/api/useCatch.js';
import { useOrganizationFilter } from './OrganizationFilterContext.jsx';

function OrganizationPerformanceReviewChart() {
  const { cWrapper } = useCatch();
  const [isOpen, setIsOpen] = useState(true);
  const { filterValues, triggerFetch } = useOrganizationFilter();
  const [chartResponseData, setChartResponseData] = useState(null);
  const [chartData, setChartData] = useState([]);
  const [hasAttemptedChart, setHasAttemptedChart] = useState(false);
  const [loadingChart, setLoadingChart] = useState(false);

  // Fetch data when triggerFetch changes (Generate Chart button clicked)
  useEffect(() => {
    if (triggerFetch === 0) return; // Don't fetch on initial mount
    
    if (!filterValues.skillId || !filterValues.startDate || !filterValues.endDate) {
      return;
    }

    setLoadingChart(true);
    setHasAttemptedChart(true);
    cWrapper(() =>
      axiosGet(GET_ORG_DEPT_REPORT_URL(
        DEFAULT_ORGANIZATION_ID,
        filterValues.departmentId ? parseInt(filterValues.departmentId) : null,
        parseInt(filterValues.skillId),
        filterValues.startDate || null,
        filterValues.endDate || null
      ))
        .then((response) => {
          setChartResponseData(response.data || null);
        })
        .catch(() => {
          setChartResponseData(null);
        })
        .finally(() => setLoadingChart(false))
    );
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [triggerFetch, cWrapper]);

  useEffect(() => {
    if (!chartResponseData || !filterValues.skillId) {
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
  }, [chartResponseData, filterValues.skillId]);

  const selectedSkillName = chartResponseData?.skills?.[0]?.skillName || '';
  const selectedDepartmentName = chartResponseData?.departmentName || null;

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
        {loadingChart && (
          <div className="mt-4 text-center">
            <p>Loading chart data...</p>
          </div>
        )}

        {chartData.length > 0 && (
          <div className="mt-4">
            <h5 className="mb-3">
              {selectedSkillName}
              {selectedDepartmentName && ` - ${selectedDepartmentName}`}
              {` - ${filterValues.startDate} to ${filterValues.endDate}`}
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

            {/* Employee Count Line Chart */}
            {/*<div className="mt-4">*/}
            {/*  <h6>Employee Count by Quarter</h6>*/}
            {/*  <div className="d-flex flex-wrap gap-3 align-items-center">*/}
            {/*    {chartData.map((data, index) => (*/}
            {/*      <span key={index} className="p-2 border rounded">*/}
            {/*        <span className="fw-bold">{data.quarter}:</span> {data.employeeCount || 0}*/}
            {/*      </span>*/}
            {/*    ))}*/}
            {/*  </div>*/}
            {/*</div>*/}

            <div className="mt-4">
              <h6>Employee Count by Quarter</h6>
              <ResponsiveContainer width="100%" height={300}>
                <LineChart data={chartData} margin={{ top: 20, right: 30, left: 20, bottom: 5 }}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="quarter" />
                  <YAxis 
                    label={{ value: 'Employee Count', angle: -90, position: 'insideLeft' }}
                    allowDecimals={false}
                  />
                  <Tooltip />
                  <Legend />
                  <Line 
                    type="monotone" 
                    dataKey="employeeCount" 
                    stroke="#82ca9d" 
                    name="Employee Count"
                    strokeWidth={2}
                    dot={{ r: 4 }}
                  />
                </LineChart>
              </ResponsiveContainer>
            </div>
          </div>
        )}

        {hasAttemptedChart && !loadingChart && (
          <>
            {chartResponseData && chartData.length === 0 && (
              <div className="mt-4 text-muted">
                <p>No data available for the selected skill and date range.</p>
              </div>
            )}
            {!chartResponseData && (
              <div className="mt-4 text-muted">
                <p>No data found for the selected filters. Please try a different skill, department, or date range.</p>
              </div>
            )}
          </>
        )}
        </CardBody>
      </Collapse>
    </Card>
  );
}

export default OrganizationPerformanceReviewChart;
