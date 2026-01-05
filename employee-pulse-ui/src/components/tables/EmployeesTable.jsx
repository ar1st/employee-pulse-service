import {useEffect, useState, useMemo} from "react";
import {DEFAULT_ORGANIZATION_ID, GET_EMPLOYEES_BY_ORGANIZATION_URL, DELETE_EMPLOYEE_URL} from "../../lib/api/apiUrls.js";
import {Alert, Spinner, Table, Input, Row, Col, Card, CardBody, FormGroup, Label, Button} from "reactstrap";
import {axiosGet, axiosDelete} from "../../lib/api/client.js";
import useCatch from "../../lib/api/useCatch.js";
import {formatDate} from "../../lib/dateUtils.js";
import ConfirmModal from "../ConfirmModal.jsx";
import {useNavigate} from "react-router-dom";

export default function EmployeesTable() {
  const navigate = useNavigate()
  const [employees, setEmployees] = useState([])
  const [loading, setLoading] = useState(false)
  const [deleteModalOpen, setDeleteModalOpen] = useState(false)
  const [employeeToDelete, setEmployeeToDelete] = useState(null)
  const [deleting, setDeleting] = useState(false)
  const {cWrapper} = useCatch()
  
  const [filters, setFilters] = useState({
    id: '',
    firstName: '',
    lastName: '',
    email: '',
    hireDate: '',
    department: '',
    occupation: ''
  })

  const loadEmployees = () => {
    setLoading(true)
    cWrapper(() =>
      axiosGet(GET_EMPLOYEES_BY_ORGANIZATION_URL(DEFAULT_ORGANIZATION_ID))
        .then((response) => {
          setEmployees(response.data)
        })
        .finally(() => setLoading(false))
    )
  }

  useEffect(() => {
    loadEmployees()
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [cWrapper])

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

  const handleFilterChange = (field, value) => {
    setFilters(prev => ({
      ...prev,
      [field]: value
    }))
  }

  const filteredEmployees = useMemo(() => {
    return employees.filter(employee => {
      const idMatch = !filters.id || String(employee.id).toLowerCase().includes(filters.id.toLowerCase())
      const firstNameMatch = !filters.firstName || (employee.firstName || '').toLowerCase().includes(filters.firstName.toLowerCase())
      const lastNameMatch = !filters.lastName || (employee.lastName || '').toLowerCase().includes(filters.lastName.toLowerCase())
      const emailMatch = !filters.email || (employee.email || '').toLowerCase().includes(filters.email.toLowerCase())
      const hireDateMatch = !filters.hireDate || formatDate(employee.hireDate).toLowerCase().includes(filters.hireDate.toLowerCase())
      const departmentMatch = !filters.department || (employee.departmentName || '').toLowerCase().includes(filters.department.toLowerCase())
      const occupationMatch = !filters.occupation || (employee.occupationTitle || '').toLowerCase().includes(filters.occupation.toLowerCase())
      
      return idMatch && firstNameMatch && lastNameMatch && emailMatch && hireDateMatch && departmentMatch && occupationMatch
    })
  }, [employees, filters])

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
      <>
        <Card className="mt-3" style={{backgroundColor: '#f8f9fa'}}>
          <CardBody>
            <Row className="align-items-end g-3">
              <Col md={1}>
                <FormGroup className="mb-0">
                  <Label for="filter-id" className="small mb-1">ID</Label>
                  <Input
                    id="filter-id"
                    type="text"
                    placeholder="Filter ID"
                    value={filters.id}
                    onChange={(e) => handleFilterChange('id', e.target.value)}
                    bsSize="sm"
                  />
                </FormGroup>
              </Col>
              <Col md={1}>
                <FormGroup className="mb-0">
                  <Label for="filter-firstName" className="small mb-1">First Name</Label>
                  <Input
                    id="filter-firstName"
                    type="text"
                    placeholder="Filter First Name"
                    value={filters.firstName}
                    onChange={(e) => handleFilterChange('firstName', e.target.value)}
                    bsSize="sm"
                  />
                </FormGroup>
              </Col>
              <Col md={1}>
                <FormGroup className="mb-0">
                  <Label for="filter-lastName" className="small mb-1">Last Name</Label>
                  <Input
                    id="filter-lastName"
                    type="text"
                    placeholder="Filter Last Name"
                    value={filters.lastName}
                    onChange={(e) => handleFilterChange('lastName', e.target.value)}
                    bsSize="sm"
                  />
                </FormGroup>
              </Col>
              <Col md={2}>
                <FormGroup className="mb-0">
                  <Label for="filter-email" className="small mb-1">Email</Label>
                  <Input
                    id="filter-email"
                    type="text"
                    placeholder="Filter Email"
                    value={filters.email}
                    onChange={(e) => handleFilterChange('email', e.target.value)}
                    bsSize="sm"
                  />
                </FormGroup>
              </Col>
              <Col md={1}>
                <FormGroup className="mb-0">
                  <Label for="filter-hireDate" className="small mb-1">Hire Date</Label>
                  <Input
                    id="filter-hireDate"
                    type="text"
                    placeholder="Filter Date"
                    value={filters.hireDate}
                    onChange={(e) => handleFilterChange('hireDate', e.target.value)}
                    bsSize="sm"
                  />
                </FormGroup>
              </Col>
              <Col md={2}>
                <FormGroup className="mb-0">
                  <Label for="filter-department" className="small mb-1">Department</Label>
                  <Input
                    id="filter-department"
                    type="text"
                    placeholder="Filter Department"
                    value={filters.department}
                    onChange={(e) => handleFilterChange('department', e.target.value)}
                    bsSize="sm"
                  />
                </FormGroup>
              </Col>
              <Col md={2}>
                <FormGroup className="mb-0">
                  <Label for="filter-occupation" className="small mb-1">Occupation</Label>
                  <Input
                    id="filter-occupation"
                    type="text"
                    placeholder="Filter Occupation"
                    value={filters.occupation}
                    onChange={(e) => handleFilterChange('occupation', e.target.value)}
                    bsSize="sm"
                  />
                </FormGroup>
              </Col>
              <Col md={2} className="d-flex align-items-end">
                {(Object.values(filters).some(f => f)) && (
                  <Button
                    color="secondary"
                    size="sm"
                    onClick={() => setFilters({
                      id: '',
                      firstName: '',
                      lastName: '',
                      email: '',
                      hireDate: '',
                      department: '',
                      occupation: ''
                    })}
                    className="w-100"
                  >
                    <i className="bi bi-x-circle me-1"></i>
                    Clear Filters
                  </Button>
                )}
              </Col>
            </Row>
          </CardBody>
        </Card>
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
      </>
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

