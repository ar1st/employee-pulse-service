import '../../styles/Common.css';
import OrganizationPerformanceReviewChart from '../charts/OrganizationPerformanceReviewChart.jsx';
import OrganizationSkillTimelineChart from '../charts/OrganizationSkillTimelineChart.jsx';

function OrganizationChartsPage() {
  return (
    <div className="page-wrapper">
      <h2>Organization Charts</h2>
      <p className="mb-4">Visualize charts for skills and performance metrics by organization and department.</p>

      <OrganizationPerformanceReviewChart />

      <OrganizationSkillTimelineChart />
    </div>
  );
}

export default OrganizationChartsPage;
