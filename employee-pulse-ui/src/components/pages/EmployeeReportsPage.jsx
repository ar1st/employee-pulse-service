import '../../styles/Common.css';
import EmployeePerformanceReviewsSection from '../reports/EmployeePerformanceReviewsSection.jsx';
import EmployeeSkillTimelineSection from '../reports/EmployeeSkillTimelineSection.jsx';

function EmployeeReportsPage() {

  return (
    <div className="page-wrapper">
      <h2>Employee Reports</h2>
      <p className="mb-4">Visualize reports for individual employee skills and performance metrics.</p>

      <EmployeePerformanceReviewsSection />

      <EmployeeSkillTimelineSection />
    </div>
  );
}

export default EmployeeReportsPage;
