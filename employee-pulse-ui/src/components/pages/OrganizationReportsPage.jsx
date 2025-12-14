import '../../styles/Common.css';
import OrganizationPerformanceReviewsSection from '../reports/OrganizationPerformanceReviewsSection.jsx';
import OrganizationSkillTimelineSection from '../reports/OrganizationSkillTimelineSection.jsx';

function OrganizationReportsPage() {
  return (
    <div className="page-wrapper">
      <h2>Organization Reports</h2>
      <p className="mb-4">Visualize reports for skills and performance metrics by organization and department.</p>

      <OrganizationPerformanceReviewsSection />

      <OrganizationSkillTimelineSection />
    </div>
  );
}

export default OrganizationReportsPage;
