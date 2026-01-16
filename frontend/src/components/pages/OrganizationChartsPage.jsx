import '../../styles/Common.css';
import { OrganizationFilterProvider } from '../charts/OrganizationFilterContext.jsx';
import OrganizationFilterComponent from '../charts/OrganizationFilterComponent.jsx';
import OrganizationPerformanceReviewChart from '../charts/OrganizationPerformanceReviewChart.jsx';
import OrganizationSkillTimelineChart from '../charts/OrganizationSkillTimelineChart.jsx';

function OrganizationChartsPage() {
  return (
    <div className="page-wrapper">
      <h2>Organization Charts</h2>
      <p className="mb-4">Visualize charts for skills and performance metrics by organization and department.</p>

      <OrganizationFilterProvider>
        <OrganizationFilterComponent />
        <OrganizationPerformanceReviewChart />
        <OrganizationSkillTimelineChart />
      </OrganizationFilterProvider>
    </div>
  );
}

export default OrganizationChartsPage;
