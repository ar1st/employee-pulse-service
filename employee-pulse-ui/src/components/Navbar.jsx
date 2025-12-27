import { useState } from 'react';
import { Nav, NavItem, Dropdown, DropdownToggle, DropdownMenu, DropdownItem } from 'reactstrap'
import { NavLink, useLocation } from 'react-router-dom'

function Navbar() {
  const [analyticsDropdownOpen, setAnalyticsDropdownOpen] = useState(false);
  const location = useLocation();
  
  const isAnalytics = location.pathname.startsWith('/analytics');

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
            isOpen={analyticsDropdownOpen}
            toggle={() => setAnalyticsDropdownOpen(!analyticsDropdownOpen)}
            className="nav-dropdown"
            direction="down"
          >
            <DropdownToggle 
              className={`nav-link-custom ${isAnalytics ? 'active' : ''}`}
              tag="div"
              style={{ cursor: 'pointer' }}
            >
              Analytics <i className={`bi bi-chevron-down ms-1 dropdown-arrow ${analyticsDropdownOpen ? 'open' : ''}`} style={{ fontSize: '0.75rem' }}></i>
            </DropdownToggle>
            <DropdownMenu className="nav-dropdown-menu">
              <DropdownItem>
                <NavLink
                  to="/analytics/organization"
                  className={({ isActive }) => 
                    `dropdown-link ${isActive ? 'active' : ''}`
                  }
                  onClick={() => setAnalyticsDropdownOpen(false)}
                >
                  Organization
                </NavLink>
              </DropdownItem>
              <DropdownItem>
                <NavLink
                  to="/analytics/employee"
                  className={({ isActive }) => 
                    `dropdown-link ${isActive ? 'active' : ''}`
                  }
                  onClick={() => setAnalyticsDropdownOpen(false)}
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

