const API_BASE_URL = 'http://localhost:8001'

export const DEFAULT_ORGANIZATION_ID = 1

export const GET_PERFORMANCE_REVIEWS_URL = (organizationId) => `${API_BASE_URL}/performance-reviews/organization/${organizationId}`
export const GET_PERFORMANCE_REVIEW_URL = (reviewId) => `${API_BASE_URL}/performance-reviews/${reviewId}`
export const CREATE_PERFORMANCE_REVIEW_URL = () => `${API_BASE_URL}/performance-reviews`
export const UPDATE_PERFORMANCE_REVIEW_URL = (reviewId) => `${API_BASE_URL}/performance-reviews/${reviewId}`
export const DELETE_PERFORMANCE_REVIEW_URL = (reviewId) => `${API_BASE_URL}/performance-reviews/${reviewId}`

export const GET_EMPLOYEE_URL = (employeeId) => `${API_BASE_URL}/employees/${employeeId}`
export const GET_EMPLOYEES_BY_ORGANIZATION_URL = (organizationId) => `${API_BASE_URL}/organizations/${organizationId}/employees`
export const GET_EMPLOYEE_LATEST_SKILL_ENTRIES_URL = (employeeId) => `${API_BASE_URL}/employees/${employeeId}/skill-entries/latest`
export const CREATE_EMPLOYEE_URL = () => `${API_BASE_URL}/employees`
export const UPDATE_EMPLOYEE_URL = (employeeId) => `${API_BASE_URL}/employees/${employeeId}`
export const DELETE_EMPLOYEE_URL = (employeeId) => `${API_BASE_URL}/employees/${employeeId}`
export const GET_DEPARTMENTS_BY_ORGANIZATION_URL = (organizationId) => `${API_BASE_URL}/organizations/${organizationId}/departments`
export const GET_DEPARTMENT_URL = (departmentId) => `${API_BASE_URL}/departments/${departmentId}`
export const GET_DEPARTMENT_EMPLOYEES_URL = (departmentId) => `${API_BASE_URL}/departments/${departmentId}/employees`
export const CREATE_DEPARTMENT_URL = () => `${API_BASE_URL}/departments`
export const UPDATE_DEPARTMENT_URL = (departmentId) => `${API_BASE_URL}/departments/${departmentId}`
export const ASSIGN_MANAGER_TO_DEPARTMENT_URL = (departmentId, managerId) => `${API_BASE_URL}/departments/${departmentId}/assign-manager/${managerId}`
export const DELETE_DEPARTMENT_URL = (departmentId) => `${API_BASE_URL}/departments/${departmentId}`

export const GET_OCCUPATIONS_BY_ORGANIZATION_URL = (organizationId) => `${API_BASE_URL}/occupations/organization/${organizationId}`
export const SEARCH_OCCUPATIONS_URL = (searchTerm) => `${API_BASE_URL}/occupations/search?q=${encodeURIComponent(searchTerm)}`
export const GET_SKILLS_BY_ORGANIZATION_URL = (organizationId) => `${API_BASE_URL}/skills/organization/${organizationId}`
export const GET_SKILLS_BY_DEPARTMENT_URL = (departmentId) => `${API_BASE_URL}/skills/department/${departmentId}`
export const SEARCH_SKILLS_URL = (searchTerm) => `${API_BASE_URL}/skills/search?q=${encodeURIComponent(searchTerm)}`
export const ADD_SKILL_ENTRY_TO_REVIEW_URL = (reviewId) => `${API_BASE_URL}/performance-reviews/${reviewId}/skill-entries`
export const DELETE_SKILL_ENTRY_FROM_REVIEW_URL = (reviewId, entryId) => `${API_BASE_URL}/performance-reviews/${reviewId}/skill-entries/${entryId}`
export const GENERATE_SKILL_ENTRIES_URL = () => `${API_BASE_URL}/performance-reviews/generate-skill-entries`

// Reporting endpoints
export const GET_ORG_DEPT_REPORT_URL = (orgId, deptId, skillId, startDate, endDate) => {
  const params = new URLSearchParams();
  // periodType defaults to QUARTER on backend, so we don't need to pass it
  if (deptId !== null && deptId !== undefined) params.append('deptId', deptId);
  if (skillId !== null && skillId !== undefined) params.append('skillId', skillId);
  if (startDate !== null && startDate !== undefined) params.append('startDate', startDate);
  if (endDate !== null && endDate !== undefined) params.append('endDate', endDate);
  return `${API_BASE_URL}/reports/org/${orgId}?${params.toString()}`;
}
export const GET_EMPLOYEE_REPORT_URL = (employeeId, skillId, startDate, endDate) => {
  const params = new URLSearchParams();
  // periodType defaults to QUARTER on backend, so we don't need to pass it
  if (skillId !== null && skillId !== undefined) params.append('skillId', skillId);
  if (startDate !== null && startDate !== undefined) params.append('startDate', startDate);
  if (endDate !== null && endDate !== undefined) params.append('endDate', endDate);
  return `${API_BASE_URL}/reports/employee/${employeeId}?${params.toString()}`;
}
export const GET_EMPLOYEE_SKILL_TIMELINE_URL = (employeeId, skillId, startDate, endDate) => {
  const params = new URLSearchParams();
  if (skillId !== null && skillId !== undefined) params.append('skillId', skillId);
  if (startDate !== null && startDate !== undefined) params.append('startDate', startDate);
  if (endDate !== null && endDate !== undefined) params.append('endDate', endDate);
  const queryString = params.toString();
  return `${API_BASE_URL}/reports/employees/${employeeId}/skills/timeline${queryString ? `?${queryString}` : ''}`;
}
export const GET_ORG_DEPT_SKILL_TIMELINE_URL = (organizationId, departmentId, skillId, startDate, endDate) => {
  const params = new URLSearchParams();
  if (departmentId !== null && departmentId !== undefined) params.append('departmentId', departmentId);
  if (skillId !== null && skillId !== undefined) params.append('skillId', skillId);
  if (startDate !== null && startDate !== undefined) params.append('startDate', startDate);
  if (endDate !== null && endDate !== undefined) params.append('endDate', endDate);
  const queryString = params.toString();
  return `${API_BASE_URL}/reports/organizations/${organizationId}/skills/timeline${queryString ? `?${queryString}` : ''}`;
}
