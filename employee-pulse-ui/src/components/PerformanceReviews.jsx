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

  const formatDateTime = (dateTime) => {
    if (!dateTime) return 'N/A'
    return new Date(dateTime).toLocaleString()
  }

  const formatRating = (rating) => {
    return rating != null ? rating.toFixed(1) : 'N/A'
  }

  return (
    <div>
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
          <Table striped bordered hover className="performance-reviews-table">
            <thead>
              <tr>
                <th>ID</th>
                <th>Review Date</th>
                <th>Overall Rating</th>
                <th>Comments</th>
                <th>Raw Text</th>
                <th>Skill Entries</th>
              </tr>
            </thead>
            <tbody>
              {performanceReviews.map((review) => (
                <tr key={review.id}>
                  <td>{review.id}</td>
                  <td>{formatDateTime(review.reviewDateTime)}</td>
                  <td>{formatRating(review.overallRating)}</td>
                  <td>{review.comments || 'N/A'}</td>
                  <td>
                    <div style={{ maxWidth: '300px', overflow: 'hidden', textOverflow: 'ellipsis' }}>
                      {review.rawText || 'N/A'}
                    </div>
                  </td>
                  <td>
                    {review.skillEntryDtos && review.skillEntryDtos.length > 0 ? (
                      <ul style={{ margin: 0, paddingLeft: '20px' }}>
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

