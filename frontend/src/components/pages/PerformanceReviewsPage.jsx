import '../../styles/Common.css';
import PerformanceReviewsTable from "../tables/PerformanceReviewsTable.jsx";
import PerformanceReviewFilters from "../tables/filters/PerformanceReviewFilters.jsx";
import {PerformanceReviewFilterProvider} from "../tables/filters/PerformanceReviewFilterContext.jsx";
import {Button} from "reactstrap";
import {useNavigate} from "react-router-dom";

function PerformanceReviewsPage() {
  const navigate = useNavigate();

  return (
    <div className="page-wrapper">
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
      <PerformanceReviewFilterProvider>
        <PerformanceReviewFilters/>
        <PerformanceReviewsTable/>
      </PerformanceReviewFilterProvider>
    </div>
  )
}

export default PerformanceReviewsPage

