import { Routes, Route, Navigate } from 'react-router-dom'
import { Container } from 'reactstrap'
import './styles/App.css'
import Navbar from './components/Navbar'
import Departments from './components/Departments'
import Employees from './components/Employees'
import PerformanceReviews from './components/PerformanceReviews'
import Reports from './components/Reports'

function App() {
  return (
    <Container fluid className="app-container">
      <Navbar />
      <div className="content-col">
        <div className="content-area">
          <Routes>
            <Route path="/" element={<Navigate to="/departments" replace />} />
            <Route path="/departments" element={<Departments />} />
            <Route path="/employees" element={<Employees />} />
            <Route path="/performance-reviews" element={<PerformanceReviews />} />
            <Route path="/reports" element={<Reports />} />
          </Routes>
        </div>
      </div>
    </Container>
  )
}

export default App
