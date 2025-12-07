const API_BASE_URL = 'http://localhost:8001'

export const DEFAULT_ORGANIZATION_ID = 1

export const GET_PERFORMANCE_REVIEWS_URL = (organizationId) => `${API_BASE_URL}/performance-reviews/organization/${organizationId}`