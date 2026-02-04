import { useState, useEffect } from 'react';
import { Nav, NavItem, Dropdown, DropdownToggle, DropdownMenu, DropdownItem } from 'reactstrap'
import { NavLink, useLocation } from 'react-router-dom'
import Select from 'react-select'
import { DEFAULT_ORGANIZATION_ID, GET_ORGANIZATIONS_URL } from '../lib/api/apiUrls.js'
import { axiosGet } from '../lib/api/client.js'
import useCatch from '../lib/api/useCatch.js'
import { useOrganization } from '../context/OrganizationContext.jsx'

function Navbar() {
  const [analyticsDropdownOpen, setAnalyticsDropdownOpen] = useState(false);
  const [organizations, setOrganizations] = useState([]);
  const { selectedOrganization, setOrganization } = useOrganization();
  const location = useLocation();
  const { cWrapper } = useCatch();
  
  const isAnalytics = location.pathname.startsWith('/analytics');

  useEffect(() => {
    cWrapper(() =>
      axiosGet(GET_ORGANIZATIONS_URL())
        .then((response) => {
          const options = (response.data || []).map((org) => ({
            value: org.id,
            label: org.name,
          }));
          setOrganizations(options);

          const defaultOption =
            options.find((opt) => opt.value === DEFAULT_ORGANIZATION_ID) ||
            options[0] ||
            null;

          if (defaultOption) {
            setOrganization(defaultOption);
          }
        })
        .catch(() => {
          setOrganizations([]);
          setOrganization(null);
        })
    );
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

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
        <NavItem className="navbar-brand-right">
          <div style={{ minWidth: 220, padding: '1rem 1.5rem' }}>
            <Select
              classNamePrefix="org-select"
              options={organizations}
              value={selectedOrganization}
              onChange={setOrganization}
              placeholder="Select organization..."
              isClearable={false}
              // isSearchable={false}
            />
          </div>
        </NavItem>
      </Nav>
    </div>
  )
}

export default Navbar

