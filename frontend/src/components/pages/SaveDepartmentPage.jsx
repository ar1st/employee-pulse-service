import '../../styles/Common.css';
import SaveDepartmentForm from "../forms/SaveDepartmentForm.jsx";
import {useSearchParams} from "react-router-dom";

export default function SaveDepartmentPage() {
  const [searchParams] = useSearchParams();
  const departmentId = searchParams.get('id');
  const isEditMode = !!departmentId;

  return (
    <div className="page-wrapper">
      <h2>{isEditMode ? 'Edit Department' : 'Create Department'}</h2>
      <p>{isEditMode ? 'Update the details of the department.' : 'Fill in the details to create a new department.'}</p>

      <SaveDepartmentForm departmentId={departmentId ? parseInt(departmentId) : null} />
    </div>
  );
}

