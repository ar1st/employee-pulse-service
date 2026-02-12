import { useState, useEffect } from 'react';
import { Card, CardBody, CardHeader, Collapse } from 'reactstrap';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend, ResponsiveContainer } from 'recharts';
import { GET_ORG_DEPT_REPORT_URL } from '../../lib/api/apiUrls.js';
import { axiosGet } from '../../lib/api/client.js';
import useCatch from '../../lib/api/useCatch.js';
import { useOrganizationFilter } from './OrganizationFilterContext.jsx';
import { useOrganization } from '../../context/OrganizationContext.jsx';
import { formatDateToDDMMYYYY } from '../../lib/dateUtils.js';

function OrganizationOverallRatingTimelineChart() {
  const { cWrapper } = useCatch();
  const [isOpen, setIsOpen] = useState(true);
  const { filterValues, triggerOverallRatingFetch } = useOrganizationFilter();
  const { selectedOrganization } = useOrganization();
  const [chartResponseData, setChartResponseData] = useState(null);
  const [chartData, setChartData] = useState([]);
  const [hasAttemptedChart, setHasAttemptedChart] = useState(false);
  const [loadingChart, setLoadingChart] = useState(false);

  // Fetch data when triggerOverallRatingFetch changes or organization/department/date range changes
  useEffect(() => {
    const orgId = selectedOrganization?.value;
    
    if (!orgId) {
      setChartResponseData(null);
      setChartData([]);
      setHasAttemptedChart(false);
      return;
    }

    // Skip initial mount - wait for trigger to be set
    if (triggerOverallRatingFetch === 0) {
      return;
    }

    setLoadingChart(true);
    setHasAttemptedChart(true);
    cWrapper(() =>
      axiosGet(GET_ORG_DEPT_REPORT_URL(
        orgId,
        filterValues.departmentId ? parseInt(filterValues.departmentId) : null,
        null, // No skill filter for overall rating
        filterValues.startDate || null,
        filterValues.endDate || null
      ))
        .then((response) => {
          const data = response.data;
          // Ensure overallRatings is always an array, even if missing
          if (data && !data.overallRatings) {
            data.overallRatings = [];
          }
          setChartResponseData(data || null);
        })
        .catch((error) => {
          console.error('Error fetching overall rating data:', error);
          setChartResponseData(null);
        })
        .finally(() => setLoadingChart(false))
    );
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [triggerOverallRatingFetch, selectedOrganization, cWrapper]);

  useEffect(() => {
    if (!chartResponseData) {
      setChartData([]);
      return;
    }

    // Check if overallRatings exists and is an array
    if (!chartResponseData.overallRatings || !Array.isArray(chartResponseData.overallRatings) || chartResponseData.overallRatings.length === 0) {
      setChartData([]);
      return;
    }

    // Transform overall ratings into chart data
    const chartDataPoints = chartResponseData.overallRatings
      .filter(period => period && period.periodStart) // Filter out invalid periods
      .map(period => {
        const date = new Date(period.periodStart);
        if (isNaN(date.getTime())) {
          return null; // Skip invalid dates
        }
        const quarter = `Q${Math.floor(date.getMonth() / 3) + 1} ${date.getFullYear()}`;
        
        return {
          quarter,
          date: date.toLocaleDateString('en-US', { 
            year: 'numeric', 
            month: 'short', 
            day: 'numeric' 
          }),
          dateValue: date.getTime(),
          avgOverallRating: period.avgOverallRating || 0
        };
      })
      .filter(point => point !== null); // Remove null entries

    // Sort by date (oldest first)
    chartDataPoints.sort((a, b) => a.dateValue - b.dateValue);

    setChartData(chartDataPoints);
  }, [chartResponseData]);

  return (
    <Card className="mb-4">
      <CardHeader 
        style={{ cursor: 'pointer' }}
        onClick={() => setIsOpen(!isOpen)}
      >
        <div className="d-flex justify-content-between align-items-center">
          <h4 className="mb-0">Organization Overall Rating Timeline</h4>
          <i className={`bi bi-chevron-${isOpen ? 'up' : 'down'}`}></i>
        </div>
      </CardHeader>
      <Collapse isOpen={isOpen}>
        <CardBody style={{ overflow: 'visible', width: '100%' }}>
        {loadingChart && (
          <div className="mt-4 text-center">
            <p>Loading chart data...</p>
          </div>
        )}

        {chartData.length > 0 && (
          <div className="mt-4" style={{ width: '100%', overflow: 'visible' }}>
            <h5 className="mb-3">
              {chartResponseData?.organizationName}
              {chartResponseData?.departmentName && ` - ${chartResponseData.departmentName}`}
              {filterValues.startDate && filterValues.endDate && 
                ` - ${formatDateToDDMMYYYY(filterValues.startDate)} to ${formatDateToDDMMYYYY(filterValues.endDate)}`}
            </h5>
            <div style={{ width: '100%', minHeight: '400px', position: 'relative' }}>
              <ResponsiveContainer width="100%" height={400}>
                <LineChart data={chartData} margin={{ top: 20, right: 30, left: 20, bottom: 60 }}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis 
                    dataKey="quarter" 
                    angle={-45}
                    textAnchor="end"
                    height={80}
                  />
                  <YAxis 
                    label={{ value: 'Average Overall Rating', angle: -90, position: 'insideLeft' }}
                    domain={[0, 5]}
                    ticks={[0, 1, 2, 3, 4, 5]}
                  />
                  <Tooltip />
                  <Legend />
                  <Line 
                    type="monotone" 
                    dataKey="avgOverallRating" 
                    stroke="#8884d8" 
                    name="Average Overall Rating"
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
                <p>No overall rating data available{chartResponseData?.organizationName ? ` for ${chartResponseData.organizationName}` : ''}{filterValues.startDate && filterValues.endDate ? ` and date range` : ''}.</p>
              </div>
            )}
            {!chartResponseData && (
              <div className="mt-4 text-muted">
                <p>No overall rating data found{selectedOrganization?.label ? ` for ${selectedOrganization.label}` : ''}{filterValues.startDate && filterValues.endDate ? ` and date range` : ''}. Please try a different organization or date range.</p>
              </div>
            )}
          </>
        )}
        </CardBody>
      </Collapse>
    </Card>
  );
}

export default OrganizationOverallRatingTimelineChart;


