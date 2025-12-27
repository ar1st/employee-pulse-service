import '../../styles/Common.css';
import EmployeePerformanceReviewChart from '../charts/EmployeePerformanceReviewChart.jsx';
import EmployeeSkillTimelineChart from '../charts/EmployeeSkillTimelineChart.jsx';

function EmployeeChartsPage() {

  return (
    <div className="page-wrapper">
      <h2>Employee Charts</h2>
      <p className="mb-4">Visualize charts for individual employee skills and performance metrics.</p>

      <EmployeePerformanceReviewChart />

      <EmployeeSkillTimelineChart />
    </div>
  );
}

export default EmployeeChartsPage;
