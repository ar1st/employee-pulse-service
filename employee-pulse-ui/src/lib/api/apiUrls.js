const API_BASE_URL = 'http://localhost:8001'

export const DEFAULT_ORGANIZATION_ID = 1

export const GET_PERFORMANCE_REVIEWS_URL = (organizationId) => `${API_BASE_URL}/performance-reviews/organization/${organizationId}`
export const GET_PERFORMANCE_REVIEW_URL = (reviewId) => `${API_BASE_URL}/performance-reviews/${reviewId}`
export const CREATE_PERFORMANCE_REVIEW_URL = () => `${API_BASE_URL}/performance-reviews`
export const UPDATE_PERFORMANCE_REVIEW_URL = (reviewId) => `${API_BASE_URL}/performance-reviews/${reviewId}`
export const DELETE_PERFORMANCE_REVIEW_URL = (reviewId) => `${API_BASE_URL}/performance-reviews/${reviewId}`

export const GET_EMPLOYEES_BY_ORGANIZATION_URL = (organizationId) => `${API_BASE_URL}/organizations/${organizationId}/employees`
export const GET_DEPARTMENTS_BY_ORGANIZATION_URL = (organizationId) => `${API_BASE_URL}/organizations/${organizationId}/departments`
export const CHANGE_EMPLOYEE_DEPARTMENT_URL = (employeeId, departmentId) => `${API_BASE_URL}/employees/${employeeId}/departments/${departmentId}`
export const GET_DEPARTMENT_URL = (departmentId) => `${API_BASE_URL}/departments/${departmentId}`
export const GET_DEPARTMENT_EMPLOYEES_URL = (departmentId) => `${API_BASE_URL}/departments/${departmentId}/employees`
export const CREATE_DEPARTMENT_URL = () => `${API_BASE_URL}/departments`
export const UPDATE_DEPARTMENT_URL = (departmentId) => `${API_BASE_URL}/departments/${departmentId}`
export const ASSIGN_MANAGER_TO_DEPARTMENT_URL = (departmentId, managerId) => `${API_BASE_URL}/departments/${departmentId}/assign-manager/${managerId}`
export const DELETE_DEPARTMENT_URL = (departmentId) => `${API_BASE_URL}/departments/${departmentId}`

export const GET_SKILLS_BY_ORGANIZATION_URL = (organizationId) => `${API_BASE_URL}/skills/organization/${organizationId}`
export const SEARCH_SKILLS_URL = (searchTerm) => `${API_BASE_URL}/skills/search?q=${encodeURIComponent(searchTerm)}`
export const ADD_SKILL_ENTRY_TO_REVIEW_URL = (reviewId) => `${API_BASE_URL}/performance-reviews/${reviewId}/skill-entries`
export const DELETE_SKILL_ENTRY_FROM_REVIEW_URL = (reviewId, entryId) => `${API_BASE_URL}/performance-reviews/${reviewId}/skill-entries/${entryId}`
export const GENERATE_SKILL_ENTRIES_URL = () => `${API_BASE_URL}/performance-reviews/generate-skill-entries`