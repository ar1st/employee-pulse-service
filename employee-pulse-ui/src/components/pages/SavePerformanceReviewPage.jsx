import '../../styles/PerformanceReviews.css';
import SavePerformanceReviewForm from "../forms/SavePerformanceReviewForm.jsx";

export default function SavePerformanceReviewPage() {

  return (
    <div className="performance-reviews-wrapper">
      <h2>Create Performance Review</h2>
      <p>Fill in the details to create a new performance review.</p>

      <SavePerformanceReviewForm />
    </div>
  );
}
