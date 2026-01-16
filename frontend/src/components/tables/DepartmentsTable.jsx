import {useEffect, useState} from "react";
import {
  DELETE_DEPARTMENT_URL,
  GET_DEPARTMENTS_BY_ORGANIZATION_URL, DEFAULT_ORGANIZATION_ID
} from "../../lib/api/apiUrls.js";
import {Alert, Spinner, Table} from "reactstrap";
import {axiosGet, axiosDelete} from "../../lib/api/client.js";
import useCatch from "../../lib/api/useCatch.js";
import ConfirmModal from "../ConfirmModal.jsx";
import {useNavigate} from "react-router-dom";

export default function DepartmentsTable() {
  const navigate = useNavigate()
  const [departments, setDepartments] = useState([])
  const [loading, setLoading] = useState(false)
  const [deleteModalOpen, setDeleteModalOpen] = useState(false)
  const [departmentToDelete, setDepartmentToDelete] = useState(null)
  const [deleting, setDeleting] = useState(false)
  const {cWrapper} = useCatch()

  const loadDepartments = () => {
    setLoading(true)
    cWrapper(() =>
      axiosGet(GET_DEPARTMENTS_BY_ORGANIZATION_URL(DEFAULT_ORGANIZATION_ID))
        .then((response) => {
          setDepartments(response.data)
        })
        .finally(() => setLoading(false))
    )
  }

  useEffect(() => {
    loadDepartments()
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [cWrapper])

  const handleEdit = (departmentId) => {
    navigate(`/departments/save?id=${departmentId}`)
  }

  const handleDeleteClick = (department) => {
    setDepartmentToDelete(department)
    setDeleteModalOpen(true)
  }

  const handleDeleteConfirm = () => {
    if (!departmentToDelete) return

    setDeleting(true)
    cWrapper(() =>
      axiosDelete(DELETE_DEPARTMENT_URL(departmentToDelete.id))
        .then(() => {
          setDeleteModalOpen(false)
          setDepartmentToDelete(null)
          loadDepartments()
        })
        .finally(() => setDeleting(false))
    )
  }

  const handleDeleteCancel = () => {
    setDeleteModalOpen(false)
    setDepartmentToDelete(null)
  }

  return <>
    {loading && (
      <div className="text-center mt-3">
        <Spinner color="primary"/>
        <p>Loading departments...</p>
      </div>
    )}

    {!loading && departments.length === 0 && (
      <Alert color="info" className="mt-3">
        No departments found.
      </Alert>
    )}

    {!loading && departments.length > 0 && (
      <div className="table-container">
        <Table striped hover responsive className="data-table">
          <thead>
          <tr>
            <th>ID</th>
            <th>Name</th>
            <th>Manager</th>
            <th>Actions</th>
          </tr>
          </thead>
          <tbody>
          {departments.map((department) => (
            <tr key={department.id}>
              <td>{department.id}</td>
              <td>{department.name || 'N/A'}</td>
              <td>{department.managerId || 'No manager assigned'}</td>
              <td>
                <div className="d-flex gap-2">
                  <button
                    className="edit-button"
                    onClick={() => handleEdit(department.id)}
                    title="Edit department"
                    aria-label="Edit department"
                  >
                    <i className="bi bi-pencil"></i>
                  </button>
                  <button
                    className="edit-button"
                    onClick={() => handleDeleteClick(department)}
                    title="Delete department"
                    aria-label="Delete department"
                    style={{color: '#dc3545'}}
                  >
                    <i className="bi bi-trash"></i>
                  </button>
                </div>
              </td>
            </tr>
          ))}
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
      message="Are you sure you want to delete this department?"
      itemDetails={departmentToDelete ? {
        'ID': departmentToDelete.id,
        'Name': departmentToDelete.name || 'N/A',
        'Organization ID': departmentToDelete.organizationId || 'N/A'
      } : null}
      detailsLabel="Department Details"
      loading={deleting}
      loadingText="Deleting..."
      confirmButtonText="Delete"
      cancelButtonText="Cancel"
      confirmButtonColor="danger"
      showWarning={true}
      warningMessage="This action cannot be undone. The department cannot be deleted if it has employees assigned to it."
    />
  </>
}

