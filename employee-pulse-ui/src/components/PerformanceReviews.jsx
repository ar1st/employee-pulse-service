import { useState, useEffect } from 'react'
import { Table, Spinner, Alert } from 'reactstrap'
import {GET_PERFORMANCE_REVIEWS_URL} from "../lib/apiUrls.js";
import '../styles/PerformanceReviews.css';

function PerformanceReviews() {
  const [performanceReviews, setPerformanceReviews] = useState([])
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState(null)

  useEffect(() => {
    fetchPerformanceReviews()
  }, [])

  const fetchPerformanceReviews = async () => {
    setLoading(true)
    setError(null)
    try {
      const response = await fetch(GET_PERFORMANCE_REVIEWS_URL)
      if (!response.ok) {
        throw new Error('Failed to fetch performance reviews')
      }
      const data = await response.json()
      setPerformanceReviews(data)
    } catch (err) {
      setError('Failed to load performance reviews: ' + err.message)
      setPerformanceReviews([])
    } finally {
      setLoading(false)
    }
  }

  function formatDateTime(dateTime) {
    // dateTime is "2025,3,22,9,0"
    const [year, month, day, hour, minute] = dateTime.map(Number);

    const date = new Date(year, month - 1, day, hour, minute);

    return date.toLocaleString('en', {
      day: 'numeric',
      month: 'long',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    });
  }

  const formatRating = (rating) => {
    return rating != null ? rating.toFixed(1) : 'N/A'
  }

  const handleEdit = (reviewId) => {
    // TODO: Implement edit functionality
    console.log('Edit performance review:', reviewId)
  }

  return (
    <div className="performance-reviews-wrapper">
      <h2>Performance Reviews</h2>
      <p>Track and manage employee performance reviews by organization.</p>

      {error && (
        <Alert color="danger" className="mt-3">
          {error}
        </Alert>
      )}

      {loading && (
        <div className="text-center mt-3">
          <Spinner color="primary" />
          <p>Loading performance reviews...</p>
        </div>
      )}

      {!loading && performanceReviews.length === 0 && !error && (
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
                      <svg
                        width="16"
                        height="16"
                        viewBox="0 0 16 16"
                        fill="none"
                        xmlns="http://www.w3.org/2000/svg"
                      >
                        <path
                          d="M11.3333 2.00004C11.5084 1.82493 11.7163 1.68606 11.9447 1.59131C12.1731 1.49655 12.4173 1.44775 12.6667 1.44775C12.916 1.44775 13.1602 1.49655 13.3886 1.59131C13.617 1.68606 13.8249 1.82493 14 2.00004C14.1751 2.17515 14.314 2.38309 14.4087 2.61147C14.5035 2.83985 14.5523 3.08405 14.5523 3.33337C14.5523 3.5827 14.5035 3.8269 14.4087 4.05528C14.314 4.28366 14.1751 4.4916 14 4.66671L5.00001 13.6667L1.33334 14.6667L2.33334 11L11.3333 2.00004Z"
                          stroke="currentColor"
                          strokeWidth="1.5"
                          strokeLinecap="round"
                          strokeLinejoin="round"
                        />
                      </svg>
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        </div>
      )}
    </div>
  )
}

export default PerformanceReviews

