import {useEffect, useState} from "react";
import {DEFAULT_ORGANIZATION_ID, GET_EMPLOYEES_BY_ORGANIZATION_URL} from "../../lib/api/apiUrls.js";
import {Alert, Spinner, Table} from "reactstrap";
import {axiosGet} from "../../lib/api/client.js";
import useCatch from "../../lib/api/useCatch.js";
import {formatDate} from "../../lib/dateUtils.js";

export default function EmployeesTable() {
  const [employees, setEmployees] = useState([])
  const [loading, setLoading] = useState(false)
  const {cWrapper} = useCatch()

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
        <Table striped bordered hover responsive className="data-table">
          <thead>
          <tr>
            <th>ID</th>
            <th>First Name</th>
            <th>Last Name</th>
            <th>Email</th>
            <th>Hire Date</th>
            <th>Department</th>
            <th>Occupation</th>
          </tr>
          </thead>
          <tbody>
          {employees.map((employee) => (
            <tr key={employee.id}>
              <td>{employee.id}</td>
              <td>{employee.firstName || 'N/A'}</td>
              <td>{employee.lastName || 'N/A'}</td>
              <td>{employee.email || 'N/A'}</td>
              <td>{formatDate(employee.hireDate)}</td>
              <td>{employee.departmentName || 'N/A'}</td>
              <td>{employee.occupationTitle || 'N/A'}</td>
            </tr>
          ))}
          </tbody>
        </Table>
      </div>
    )}
  </>
}

