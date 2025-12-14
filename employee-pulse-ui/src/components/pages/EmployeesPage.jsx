import '../../styles/Common.css';
import EmployeesTable from "../tables/EmployeesTable.jsx";
import {Button} from "reactstrap";
import {useNavigate} from "react-router-dom";

function EmployeesPage() {
  const navigate = useNavigate();

  return (
    <div className="page-wrapper">
      <h2>Employees</h2>
      <p>View and manage employee information.</p>

      <div>
        <Button
          color="primary"
          onClick={() => navigate('/employees/save')}
        >
          Create Employee
        </Button>
      </div>
      <EmployeesTable/>
    </div>
  )
}

export default EmployeesPage

