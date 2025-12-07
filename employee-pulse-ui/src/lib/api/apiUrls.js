const API_BASE_URL = 'http://localhost:8001'

export const DEFAULT_ORGANIZATION_ID = 1

export const GET_PERFORMANCE_REVIEWS_URL = (organizationId) => `${API_BASE_URL}/performance-reviews/organization/${organizationId}`
export const CREATE_PERFORMANCE_REVIEW_URL = () => `${API_BASE_URL}/performance-reviews`

export const GET_EMPLOYEES_BY_ORGANIZATION_URL = (organizationId) => `${API_BASE_URL}/organizations/${organizationId}/employees`
export const GET_DEPARTMENTS_BY_ORGANIZATION_URL = (organizationId) => `${API_BASE_URL}/organizations/${organizationId}/departments`

export const GET_DEPARTMENT_URL = (departmentId) => `${API_BASE_URL}/departments/${departmentId}`

export const GET_SKILLS_BY_ORGANIZATION_URL = (organizationId) => `${API_BASE_URL}/skills/organization/${organizationId}`
export const SEARCH_SKILLS_URL = (searchTerm) => `${API_BASE_URL}/skills/search?q=${encodeURIComponent(searchTerm)}`
export const ADD_SKILL_ENTRY_TO_REVIEW_URL = (reviewId) => `${API_BASE_URL}/performance-reviews/${reviewId}/skill-entries`