import {useEffect, useState} from "react";
import {DEFAULT_ORGANIZATION_ID, GET_PERFORMANCE_REVIEWS_URL} from "../../lib/api/apiUrls.js";
import {Alert, Spinner, Table} from "reactstrap";
import {axiosGet} from "../../lib/api/client.js";
import useCatch from "../../lib/api/useCatch.js";
import {formatDateTime} from "../../lib/dateUtils.js";

export default function PerformanceReviewsTable() {
  const [performanceReviews, setPerformanceReviews] = useState([])
  const [loading, setLoading] = useState(false)
  const {cWrapper} = useCatch()

  useEffect(() => {
    // eslint-disable-next-line react-hooks/set-state-in-effect
    setLoading(true)

    cWrapper(() =>
      axiosGet(
        GET_PERFORMANCE_REVIEWS_URL(DEFAULT_ORGANIZATION_ID),
      ).then((response) => {
        setPerformanceReviews(response.data)
      })
        .finally(() => setLoading(false)),
    )

  }, [cWrapper])

  const formatRating = (rating) => {
    return rating != null ? rating.toFixed(1) : 'N/A'
  }

  const handleEdit = (reviewId) => {
    // TODO: Implement edit functionality
    console.log('Edit performance review:', reviewId)
  }

  return <>
    {loading && (
      <div className="text-center mt-3">
        <Spinner color="primary"/>
        <p>Loading performance reviews...</p>
      </div>
    )}

    {!loading && performanceReviews.length === 0 && (
      <Alert color="info" className="mt-3">
        No performance reviews found for this organization.
      </Alert>
    )}

    {!loading && performanceReviews.length > 0 && (
      <div className="performance-reviews-table-container">
        <Table striped bordered hover responsive className="performance-reviews-table">
          <thead>
          <tr>
            <th>ID</th>
            <th>Department</th>
            <th>Employee</th>
            <th>Reporter</th>
            <th>Overall Rating</th>
            <th>Review Date</th>
            <th>Skill Entries</th>
            <th>Actions</th>
          </tr>
          </thead>
          <tbody>
          {performanceReviews.map((review) => (
            <tr key={review.id}>
              <td>{review.id}</td>
              <td>{review.departmentName || 'N/A'}</td>
              <td>{review.employeeName || 'N/A'}</td>
              <td>{review.reporterName || 'N/A'}</td>
              <td>{formatRating(review.overallRating)}</td>
              <td>{formatDateTime(review.reviewDateTime)}</td>
              <td>
                {review.skillEntryDtos && review.skillEntryDtos.length > 0 ? (
                  <ul style={{margin: 0, paddingLeft: '20px'}}>
                    {review.skillEntryDtos.map((entry) => (
                      <li key={entry.id}>
                        {entry.skillName}: {formatRating(entry.rating)}
                      </li>
                    ))}
                  </ul>
                ) : (
                  'No skills'
                )}
              </td>
              <td>
                <button
                  className="edit-button"
                  onClick={() => handleEdit(review.id)}
                  title="Edit performance review"
                  aria-label="Edit performance review"
                >
                  <i className="bi bi-pencil"></i>
                </button>
              </td>
            </tr>
          ))}
          </tbody>
        </Table>
      </div>
    )}</>
}