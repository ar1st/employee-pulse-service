import '../../styles/Common.css';
import EmployeesTable from "../tables/EmployeesTable.jsx";

function EmployeesPage() {
  return (
    <div className="page-wrapper">
      <h2>Employees</h2>
      <p>View and manage employee information.</p>
      <EmployeesTable/>
    </div>
  )
}

export default EmployeesPage

