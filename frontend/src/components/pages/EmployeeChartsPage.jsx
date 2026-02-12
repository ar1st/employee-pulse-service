import '../../styles/Common.css';
import {EmployeeFilterProvider} from '../charts/EmployeeFilterContext.jsx';
import EmployeeFilterComponent from '../charts/EmployeeFilterComponent.jsx';
import EmployeeOverallRatingTimelineChart from '../charts/EmployeeOverallRatingTimelineChart.jsx';
import EmployeePerformanceReviewChart from '../charts/EmployeePerformanceReviewChart.jsx';
import EmployeeSkillTimelineChart from '../charts/EmployeeSkillTimelineChart.jsx';

function EmployeeChartsPage() {
  return (
    <div className="page-wrapper">
      <h2>Employee Charts</h2>
      <p className="mb-4">Visualize charts for individual employee skills and performance metrics.</p>

      <EmployeeFilterProvider>
        <EmployeeFilterComponent/>
        <EmployeeOverallRatingTimelineChart/>
        <EmployeePerformanceReviewChart/>
        <EmployeeSkillTimelineChart/>
      </EmployeeFilterProvider>

    </div>
  );
}

export default EmployeeChartsPage;
