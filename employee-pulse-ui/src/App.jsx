import { Routes, Route, Navigate } from 'react-router-dom'
import { Container } from 'reactstrap'
import './styles/App.css'
import Navbar from './components/Navbar'
import DepartmentsPage from './components/pages/DepartmentsPage.jsx'
import SaveDepartmentPage from './components/pages/SaveDepartmentPage.jsx'
import EmployeesPage from './components/pages/EmployeesPage.jsx'
import SaveEmployeePage from './components/pages/SaveEmployeePage.jsx'
import PerformanceReviewsPage from './components/pages/PerformanceReviewsPage.jsx'
import SavePerformanceReviewPage from './components/pages/SavePerformanceReviewPage.jsx'
import OrganizationReportsPage from "./components/pages/OrganizationReportsPage.jsx";
import EmployeeReportsPage from "./components/pages/EmployeeReportsPage.jsx";

function App() {
  return (
    <Container fluid className="app-container">
      <Navbar />
      <div className="content-col">
        <div className="content-area">
          <Routes>
            <Route path="/" element={<Navigate to="/departments" replace />} />
            <Route path="/departments" element={<DepartmentsPage />} />
            <Route path="/departments/save" element={<SaveDepartmentPage />} />
            <Route path="/employees" element={<EmployeesPage />} />
            <Route path="/employees/save" element={<SaveEmployeePage />} />
            <Route path="/performance-reviews" element={<PerformanceReviewsPage />} />
            <Route path="/performance-reviews/save" element={<SavePerformanceReviewPage />} />
            <Route path="/analytics/organization" element={<OrganizationReportsPage />} />
            <Route path="/analytics/employee" element={<EmployeeReportsPage />} />
          </Routes>
        </div>
      </div>
    </Container>
  )
}

export default App
