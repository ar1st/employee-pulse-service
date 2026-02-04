import {useEffect, useState, useMemo} from "react";
import {GET_EMPLOYEES_BY_ORGANIZATION_URL, DELETE_EMPLOYEE_URL} from "../../lib/api/apiUrls.js";
import {Alert, Spinner, Table} from "reactstrap";
import {axiosGet, axiosDelete} from "../../lib/api/client.js";
import useCatch from "../../lib/api/useCatch.js";
import {formatDate} from "../../lib/dateUtils.js";
import ConfirmModal from "../ConfirmModal.jsx";
import {useNavigate} from "react-router-dom";
import {useEmployeeFilter} from "./filters/EmployeeFilterContext.jsx";
import { useOrganization } from "../../context/OrganizationContext.jsx";

export default function EmployeesTable() {
  const navigate = useNavigate()
  const [employees, setEmployees] = useState([])
  const [loading, setLoading] = useState(false)
  const [deleteModalOpen, setDeleteModalOpen] = useState(false)
  const [employeeToDelete, setEmployeeToDelete] = useState(null)
  const [deleting, setDeleting] = useState(false)
  const {cWrapper} = useCatch()
  const {filterValues} = useEmployeeFilter()
  const { selectedOrganizationId } = useOrganization();

  const loadEmployees = () => {
    setLoading(true)
    cWrapper(() =>
      axiosGet(GET_EMPLOYEES_BY_ORGANIZATION_URL(selectedOrganizationId))
        .then((response) => {
          setEmployees(response.data)
        })
        .finally(() => setLoading(false))
    )
  }

  useEffect(() => {
    loadEmployees()
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [cWrapper, selectedOrganizationId])

  const handleEdit = (employeeId) => {
    navigate(`/employees/save?id=${employeeId}`)
  }

  const handleDeleteClick = (employee) => {
    setEmployeeToDelete(employee)
    setDeleteModalOpen(true)
  }

  const handleDeleteConfirm = () => {
    if (!employeeToDelete) return

    setDeleting(true)
    cWrapper(() =>
      axiosDelete(DELETE_EMPLOYEE_URL(employeeToDelete.id))
        .then(() => {
          setDeleteModalOpen(false)
          setEmployeeToDelete(null)
          loadEmployees()
        })
        .finally(() => setDeleting(false))
    )
  }

  const handleDeleteCancel = () => {
    setDeleteModalOpen(false)
    setEmployeeToDelete(null)
  }

  const filteredEmployees = useMemo(() => {
    return employees.filter(employee => {
      const idMatch = !filterValues.id || String(employee.id).toLowerCase().includes(filterValues.id.toLowerCase())
      const firstNameMatch = !filterValues.firstName || (employee.firstName || '').toLowerCase().includes(filterValues.firstName.toLowerCase())
      const lastNameMatch = !filterValues.lastName || (employee.lastName || '').toLowerCase().includes(filterValues.lastName.toLowerCase())
      const emailMatch = !filterValues.email || (employee.email || '').toLowerCase().includes(filterValues.email.toLowerCase())
      
      // Date range filtering
      let hireDateMatch = true
      if (filterValues.hireDateStart || filterValues.hireDateEnd) {
        if (employee.hireDate) {
          const employeeDate = new Date(employee.hireDate)
          employeeDate.setHours(0, 0, 0, 0)
          
          if (filterValues.hireDateStart && filterValues.hireDateEnd) {
            // Both dates provided - check if employee date is within range
            const startDate = new Date(filterValues.hireDateStart)
            startDate.setHours(0, 0, 0, 0)
            const endDate = new Date(filterValues.hireDateEnd)
            endDate.setHours(23, 59, 59, 999) // Include entire end date
            hireDateMatch = employeeDate >= startDate && employeeDate <= endDate
          } else if (filterValues.hireDateStart) {
            // Only start date - check if employee date is >= start date
            const startDate = new Date(filterValues.hireDateStart)
            startDate.setHours(0, 0, 0, 0)
            hireDateMatch = employeeDate >= startDate
          } else if (filterValues.hireDateEnd) {
            // Only end date - check if employee date is <= end date
            const endDate = new Date(filterValues.hireDateEnd)
            endDate.setHours(23, 59, 59, 999)
            hireDateMatch = employeeDate <= endDate
          }
        } else {
          hireDateMatch = false
        }
      }
      
      const departmentMatch = !filterValues.department || (employee.departmentName || '').toLowerCase().includes(filterValues.department.toLowerCase())
      const occupationMatch = !filterValues.occupation || (employee.occupationTitle || '').toLowerCase().includes(filterValues.occupation.toLowerCase())
      
      return idMatch && firstNameMatch && lastNameMatch && emailMatch && hireDateMatch && departmentMatch && occupationMatch
    })
  }, [employees, filterValues])

  return <>
    {loading && (
      <div className="text-center mt-3">
        <Spinner color="primary"/>
        <p>Loading employees...</p>
      </div>
    )}

    {!loading && employees.length === 0 && (
      <Alert color="info" className="mt-3">
        No employees found.
      </Alert>
    )}

    {!loading && employees.length > 0 && (
      <div className="table-container">
          <Table stripe hover responsive className="data-table">
          <thead>
          <tr>
            <th>ID</th>
            <th>First Name</th>
            <th>Last Name</th>
            <th>Email</th>
            <th>Hire Date</th>
            <th>Department</th>
            <th>Occupation</th>
            <th>Actions</th>
          </tr>
          </thead>
          <tbody>
          {filteredEmployees.length === 0 ? (
            <tr>
              <td colSpan={8} className="text-center">
                No employees match the current filters.
              </td>
            </tr>
          ) : (
            filteredEmployees.map((employee) => (
            <tr key={employee.id}>
              <td>{employee.id}</td>
              <td>{employee.firstName || 'N/A'}</td>
              <td>{employee.lastName || 'N/A'}</td>
              <td>{employee.email || 'N/A'}</td>
              <td>{formatDate(employee.hireDate)}</td>
              <td>{employee.departmentName || 'N/A'}</td>
              <td>{employee.occupationTitle || 'N/A'}</td>
              <td>
                <div className="d-flex gap-2">
                  <button
                    className="edit-button"
                    onClick={() => handleEdit(employee.id)}
                    title="Edit employee"
                    aria-label="Edit employee"
                  >
                    <i className="bi bi-pencil"></i>
                  </button>
                  <button
                    className="edit-button"
                    onClick={() => handleDeleteClick(employee)}
                    title="Delete employee"
                    aria-label="Delete employee"
                    style={{color: '#dc3545'}}
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
      message="Are you sure you want to delete this employee?"
      itemDetails={employeeToDelete ? {
        'ID': employeeToDelete.id,
        'Name': `${employeeToDelete.firstName || ''} ${employeeToDelete.lastName || ''}`.trim() || 'N/A',
        'Email': employeeToDelete.email || 'N/A',
        'Department': employeeToDelete.departmentName || 'N/A'
      } : null}
      detailsLabel="Employee Details"
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

