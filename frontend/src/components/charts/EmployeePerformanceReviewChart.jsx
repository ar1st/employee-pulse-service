import { useState, useEffect } from 'react';
import { Card, CardBody, CardHeader, Collapse } from 'reactstrap';
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts';
import { GET_EMPLOYEE_REPORT_URL } from '../../lib/api/apiUrls.js';
import { axiosGet } from '../../lib/api/client.js';
import useCatch from '../../lib/api/useCatch.js';
import { useEmployeeFilter } from './EmployeeFilterContext.jsx';

function EmployeePerformanceReviewChart() {
  const { cWrapper } = useCatch();
  const [isOpen, setIsOpen] = useState(true);
  const { filterValues, triggerFetch } = useEmployeeFilter();
  const [chartResponseData, setChartResponseData] = useState(null);
  const [chartData, setChartData] = useState([]);
  const [hasAttemptedChart, setHasAttemptedChart] = useState(false);
  const [loadingCharts, setLoadingCharts] = useState(false);

  // Fetch data when triggerFetch changes (Generate Chart button clicked)
  useEffect(() => {
    if (triggerFetch === 0) return; // Don't fetch on initial mount
    
    if (!filterValues.employeeId || !filterValues.skillId || !filterValues.startDate || !filterValues.endDate) {
      return;
    }

    setLoadingCharts(true);
    setHasAttemptedChart(true);
    cWrapper(() =>
      axiosGet(GET_EMPLOYEE_REPORT_URL(
        parseInt(filterValues.employeeId),
        filterValues.skillId ? parseInt(filterValues.skillId) : null,
        filterValues.startDate || null,
        filterValues.endDate || null
      ))
        .then((response) => {
          setChartResponseData(response.data || null);
        })
        .catch(() => {
          setChartResponseData(null);
        })
        .finally(() => setLoadingCharts(false))
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
  }, [chartResponseData, filterValues.skillId]);

  const selectedSkillName = chartResponseData?.skills?.[0]?.skillName || '';

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
        {loadingCharts && (
          <div className="mt-4 text-center">
            <p>Loading chart data...</p>
          </div>
        )}

        {chartData.length > 0 && (
          <div className="mt-4">
            <h5 className="mb-3">
              {chartResponseData?.firstName && chartResponseData?.lastName && `${chartResponseData.firstName} ${chartResponseData.lastName}`}
              {selectedSkillName && ` - ${selectedSkillName}`}
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
          </div>
        )}

        {hasAttemptedChart && !loadingCharts && (
          <>
            {chartResponseData && chartData.length === 0 && (
              <div className="mt-4 text-muted">
                <p>No review data available{selectedSkillName ? ` for ${selectedSkillName}` : ''} for the selected date range.</p>
              </div>
            )}
            {!chartResponseData && (
              <div className="mt-4 text-muted">
                <p>No review data found{selectedSkillName ? ` for ${selectedSkillName}` : ''} for the selected date range. Please try a different skill or date range.</p>
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

