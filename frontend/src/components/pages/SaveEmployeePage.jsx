import '../../styles/Common.css';
import SaveEmployeeForm from "../forms/SaveEmployeeForm.jsx";
import {useSearchParams} from "react-router-dom";

export default function SaveEmployeePage() {
  const [searchParams] = useSearchParams();
  const employeeId = searchParams.get('id');
  const isEditMode = !!employeeId;

  return (
    <div className="page-wrapper">
      <h2>{isEditMode ? 'Edit Employee' : 'Create Employee'}</h2>
      <p>{isEditMode ? 'Update the details of the employee.' : 'Fill in the details to create a new employee.'}</p>

      <SaveEmployeeForm employeeId={employeeId ? parseInt(employeeId) : null} />
    </div>
  );
}

