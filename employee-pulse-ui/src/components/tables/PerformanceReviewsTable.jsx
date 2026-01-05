import {useEffect, useState, useMemo} from "react";
import {DEFAULT_ORGANIZATION_ID, GET_PERFORMANCE_REVIEWS_URL, DELETE_PERFORMANCE_REVIEW_URL} from "../../lib/api/apiUrls.js";
import {Alert, Spinner, Table} from "reactstrap";
import {axiosGet, axiosDelete} from "../../lib/api/client.js";
import useCatch from "../../lib/api/useCatch.js";
import {formatDateTime} from "../../lib/dateUtils.js";
import ConfirmModal from "../ConfirmModal.jsx";
import {useNavigate} from "react-router-dom";
import {usePerformanceReviewFilter} from "./filters/PerformanceReviewFilterContext.jsx";

export default function PerformanceReviewsTable() {
  const navigate = useNavigate()
  const [performanceReviews, setPerformanceReviews] = useState([])
  const [loading, setLoading] = useState(false)
  const [deleteModalOpen, setDeleteModalOpen] = useState(false)
  const [reviewToDelete, setReviewToDelete] = useState(null)
  const [deleting, setDeleting] = useState(false)
  const {cWrapper} = useCatch()
  const {filterValues} = usePerformanceReviewFilter()

  const formatRating = (rating) => {
    return rating != null ? rating.toFixed(1) : 'N/A'
  }

  const loadPerformanceReviews = () => {
    setLoading(true)
    cWrapper(() =>
      axiosGet(
        GET_PERFORMANCE_REVIEWS_URL(DEFAULT_ORGANIZATION_ID),
      ).then((response) => {
        setPerformanceReviews(response.data)
      })
        .finally(() => setLoading(false)),
    )
  }

  useEffect(() => {
    loadPerformanceReviews()
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [cWrapper])

  const handleEdit = (reviewId) => {
    navigate(`/performance-reviews/save?id=${reviewId}`)
  }

  const handleDeleteClick = (review) => {
    setReviewToDelete(review)
    setDeleteModalOpen(true)
  }

  const handleDeleteConfirm = () => {
    if (!reviewToDelete) return

    setDeleting(true)
    cWrapper(() =>
      axiosDelete(DELETE_PERFORMANCE_REVIEW_URL(reviewToDelete.id))
        .then(() => {
          setDeleteModalOpen(false)
          setReviewToDelete(null)
          loadPerformanceReviews()
        })
        .finally(() => setDeleting(false))
    )
  }

  const handleDeleteCancel = () => {
    setDeleteModalOpen(false)
    setReviewToDelete(null)
  }

  const filteredPerformanceReviews = useMemo(() => {
    return performanceReviews.filter(review => {
      const idMatch = !filterValues.id || String(review.id).toLowerCase().includes(filterValues.id.toLowerCase())
      const departmentMatch = !filterValues.department || (review.departmentName || '').toLowerCase().includes(filterValues.department.toLowerCase())
      const employeeMatch = !filterValues.employee || (review.employeeName || '').toLowerCase().includes(filterValues.employee.toLowerCase())
      const reporterMatch = !filterValues.reporter || (review.reporterName || '').toLowerCase().includes(filterValues.reporter.toLowerCase())
      
      // Rating range filtering
      let ratingMatch = true
      if (filterValues.overallRatingMin || filterValues.overallRatingMax) {
        const rating = review.overallRating != null ? review.overallRating : -1
        if (filterValues.overallRatingMin && filterValues.overallRatingMax) {
          const min = parseFloat(filterValues.overallRatingMin)
          const max = parseFloat(filterValues.overallRatingMax)
          ratingMatch = rating >= min && rating <= max
        } else if (filterValues.overallRatingMin) {
          const min = parseFloat(filterValues.overallRatingMin)
          ratingMatch = rating >= min
        } else if (filterValues.overallRatingMax) {
          const max = parseFloat(filterValues.overallRatingMax)
          ratingMatch = rating <= max
        }
      }
      
      // Date range filtering
      let reviewDateMatch = true
      if (filterValues.reviewDateStart || filterValues.reviewDateEnd) {
        if (review.reviewDateTime && Array.isArray(review.reviewDateTime)) {
          const [year, month, day] = review.reviewDateTime.map(Number)
          const reviewDate = new Date(year, month - 1, day)
          reviewDate.setHours(0, 0, 0, 0)
          
          if (filterValues.reviewDateStart && filterValues.reviewDateEnd) {
            const startDate = new Date(filterValues.reviewDateStart)
            startDate.setHours(0, 0, 0, 0)
            const endDate = new Date(filterValues.reviewDateEnd)
            endDate.setHours(23, 59, 59, 999)
            reviewDateMatch = reviewDate >= startDate && reviewDate <= endDate
          } else if (filterValues.reviewDateStart) {
            const startDate = new Date(filterValues.reviewDateStart)
            startDate.setHours(0, 0, 0, 0)
            reviewDateMatch = reviewDate >= startDate
          } else if (filterValues.reviewDateEnd) {
            const endDate = new Date(filterValues.reviewDateEnd)
            endDate.setHours(23, 59, 59, 999)
            reviewDateMatch = reviewDate <= endDate
          }
        } else {
          reviewDateMatch = false
        }
      }
      
      return idMatch && departmentMatch && employeeMatch && reporterMatch && ratingMatch && reviewDateMatch
    })
  }, [performanceReviews, filterValues])

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
      <div className="table-container">
        <Table striped hover responsive className="data-table">
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
          {filteredPerformanceReviews.length === 0 ? (
            <tr>
              <td colSpan={8} className="text-center">
                No performance reviews match the current filters.
              </td>
            </tr>
          ) : (
            filteredPerformanceReviews.map((review) => (
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
                <div className="d-flex gap-2">
                  <button
                    className="edit-button"
                    onClick={() => handleEdit(review.id)}
                    title="Edit performance review"
                    aria-label="Edit performance review"
                  >
                    <i className="bi bi-pencil"></i>
                  </button>
                  <button
                    className="edit-button"
                    onClick={() => handleDeleteClick(review)}
                    title="Delete performance review"
                    aria-label="Delete performance review"
                    style={{ color: '#dc3545' }}
                  >
                    <i className="bi bi-trash"></i>
                  </button>
                </div>
              </td>
            </tr>
            ))
          )}
          </tbody>
        </Table>
      </div>
    )}

    <ConfirmModal
      isOpen={deleteModalOpen}
      onToggle={handleDeleteCancel}
      onConfirm={handleDeleteConfirm}
      onCancel={handleDeleteCancel}
      title="Confirm Delete"
      message="Are you sure you want to delete this performance review?"
      itemDetails={reviewToDelete ? {
        'ID': reviewToDelete.id,
        'Employee': reviewToDelete.employeeName || 'N/A',
        'Department': reviewToDelete.departmentName || 'N/A',
        'Date': formatDateTime(reviewToDelete.reviewDateTime)
      } : null}
      detailsLabel="Review Details"
      loading={deleting}
      loadingText="Deleting..."
      confirmButtonText="Delete"
      cancelButtonText="Cancel"
      confirmButtonColor="danger"
      showWarning={true}
      warningMessage="This action cannot be undone."
    />
  </>
}