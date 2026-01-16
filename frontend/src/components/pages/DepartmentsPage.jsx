import '../../styles/Common.css';
import DepartmentsTable from "../tables/DepartmentsTable.jsx";
import {Button} from "reactstrap";
import {useNavigate} from "react-router-dom";

function DepartmentsPage() {
  const navigate = useNavigate();

  return (
    <div className="page-wrapper">
      <h2>Departments</h2>
      <p>Manage and view all departments in the organization.</p>

      <div>
        <Button
          color="primary"
          onClick={() => navigate('/departments/save')}
        >
          Create Department
        </Button>
      </div>
      <DepartmentsTable/>
    </div>
  )
}

export default DepartmentsPage

