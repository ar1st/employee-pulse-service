import '../../styles/PerformanceReviews.css';
import PerformanceReviewsTable from "../tables/PerformanceReviewsTable.jsx";
import {Button} from "reactstrap";
import {useNavigate} from "react-router-dom";

function PerformanceReviewsPage() {
  const navigate = useNavigate();

  return (
    <div className="performance-reviews-wrapper">
      <h2>Performance Reviews</h2>
      <p>Track and manage employee performance reviews by organization.</p>

      <div>

        <Button
          color="primary"
          onClick={() => navigate('/performance-reviews/save')}
        >
          Create Performance Review
        </Button>
      </div>
      <PerformanceReviewsTable/>

    </div>
  )
}

export default PerformanceReviewsPage

