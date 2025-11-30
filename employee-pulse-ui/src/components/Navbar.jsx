import { Col, Nav, NavItem } from 'reactstrap'
import { NavLink } from 'react-router-dom'

function Navbar() {
  return (
    <Col className="sidebar-col">
      <Nav vertical className="sidebar-nav">
        <NavItem>
          <NavLink
            to="/departments"
            className={({ isActive }) => 
              `nav-link-custom ${isActive ? 'active' : ''}`
            }
          >
            Departments
          </NavLink>
        </NavItem>
        <NavItem>
          <NavLink
            to="/employees"
            className={({ isActive }) => 
              `nav-link-custom ${isActive ? 'active' : ''}`
            }
          >
            Employees
          </NavLink>
        </NavItem>
        <NavItem>
          <NavLink
            to="/performance-reviews"
            className={({ isActive }) => 
              `nav-link-custom ${isActive ? 'active' : ''}`
            }
          >
            Performance Reviews
          </NavLink>
        </NavItem>
        <NavItem>
          <NavLink
            to="/reports"
            className={({ isActive }) => 
              `nav-link-custom ${isActive ? 'active' : ''}`
            }
          >
            Reports
          </NavLink>
        </NavItem>
      </Nav>
    </Col>
  )
}

export default Navbar

