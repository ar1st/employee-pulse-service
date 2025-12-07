import '../../styles/PerformanceReviews.css';
import PerformanceReviewsTable from "../tables/PerformanceReviewsTable.jsx";

function PerformanceReviewsPage() {


  return (
    <div className="performance-reviews-wrapper">
      <h2>Performance Reviews</h2>
      <p>Track and manage employee performance reviews by organization.</p>

      <PerformanceReviewsTable/>

    </div>
  )
}

export default PerformanceReviewsPage

