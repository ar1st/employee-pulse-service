import '../../styles/Common.css';
import SavePerformanceReviewForm from "../forms/SavePerformanceReviewForm.jsx";
import {useSearchParams} from "react-router-dom";

export default function SavePerformanceReviewPage() {
  const [searchParams] = useSearchParams();
  const reviewId = searchParams.get('id');
  const isEditMode = !!reviewId;

  return (
    <div className="page-wrapper">
      <h2>{isEditMode ? 'Edit Performance Review' : 'Create Performance Review'}</h2>
      <p>{isEditMode ? 'Update the details of the performance review.' : 'Fill in the details to create a new performance review.'}</p>

      <SavePerformanceReviewForm reviewId={reviewId ? parseInt(reviewId) : null} />
    </div>
  );
}
