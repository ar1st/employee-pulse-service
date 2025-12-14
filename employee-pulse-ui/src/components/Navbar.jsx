import { useState } from 'react';
import { Nav, NavItem, Dropdown, DropdownToggle, DropdownMenu, DropdownItem } from 'reactstrap'
import { NavLink, useLocation } from 'react-router-dom'

function Navbar() {
  const [reportsDropdownOpen, setReportsDropdownOpen] = useState(false);
  const location = useLocation();
  
  const isReportsActive = location.pathname.startsWith('/reports');

  return (
    <div className="navbar-top">
      <Nav className="navbar-nav">
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
          <Dropdown 
            isOpen={reportsDropdownOpen} 
            toggle={() => setReportsDropdownOpen(!reportsDropdownOpen)}
            className="nav-dropdown"
          >
            <DropdownToggle 
              className={`nav-link-custom ${isReportsActive ? 'active' : ''}`}
              tag="div"
              style={{ cursor: 'pointer' }}
            >
              Reports <i className={`bi bi-chevron-down ms-1 dropdown-arrow ${reportsDropdownOpen ? 'open' : ''}`} style={{ fontSize: '0.75rem' }}></i>
            </DropdownToggle>
            <DropdownMenu>
              <DropdownItem>
                <NavLink
                  to="/reports/organization"
                  className={({ isActive }) => 
                    `dropdown-link ${isActive ? 'active' : ''}`
                  }
                  onClick={() => setReportsDropdownOpen(false)}
                >
                  Organization
                </NavLink>
              </DropdownItem>
              <DropdownItem>
                <NavLink
                  to="/reports/employee"
                  className={({ isActive }) => 
                    `dropdown-link ${isActive ? 'active' : ''}`
                  }
                  onClick={() => setReportsDropdownOpen(false)}
                >
                  Employee
                </NavLink>
              </DropdownItem>
            </DropdownMenu>
          </Dropdown>
        </NavItem>
      </Nav>
    </div>
  )
}

export default Navbar

