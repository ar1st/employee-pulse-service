import { createContext, useContext, useState, useCallback, useMemo, useEffect } from 'react';
import { getDefaultDates } from '../../lib/dateUtils.js';
import { useOrganization } from '../../context/OrganizationContext.jsx';

const OrganizationFilterContext = createContext();

export const useOrganizationFilter = () => {
  const context = useContext(OrganizationFilterContext);
  if (!context) {
    throw new Error('useOrganizationFilter must be used within an OrganizationFilterProvider');
  }
  return context;
};

export const OrganizationFilterProvider = ({ children }) => {
  const { selectedOrganization } = useOrganization();
  const [filterValues, setFilterValues] = useState({
    departmentId: '',
    skillId: '',
    ...getDefaultDates()
  });
  const [triggerFetch, setTriggerFetch] = useState(0);
  const [triggerOverallRatingFetch, setTriggerOverallRatingFetch] = useState(0);

  const updateFilterValues = useCallback((updates) => {
    setFilterValues(prev => ({
      ...prev,
      ...updates
    }));
  }, []);

  const resetFilters = useCallback(() => {
    setFilterValues({
      departmentId: '',
      skillId: '',
      ...getDefaultDates()
    });
  }, []);

  const triggerChartGeneration = useCallback(() => {
    setTriggerFetch(prev => prev + 1);
  }, []);

  // Auto-trigger overall rating chart when organization, department, or date range changes
  useEffect(() => {
    if (selectedOrganization?.value) {
      setTriggerOverallRatingFetch(prev => prev + 1);
    }
  }, [selectedOrganization?.value, filterValues.departmentId, filterValues.startDate, filterValues.endDate]);

  // Auto-trigger skill charts when skill is selected or date range changes (if skill is already selected)
  useEffect(() => {
    if (selectedOrganization?.value && filterValues.skillId) {
      setTriggerFetch(prev => prev + 1);
    }
  }, [filterValues.skillId, filterValues.startDate, filterValues.endDate, selectedOrganization?.value]);

  const contextValue = useMemo(() => ({
    filterValues,
    setFilterValues: updateFilterValues,
    resetFilters,
    triggerFetch,
    triggerOverallRatingFetch,
    triggerChartGeneration
  }), [filterValues, updateFilterValues, resetFilters, triggerFetch, triggerOverallRatingFetch, triggerChartGeneration]);

  return (
    <OrganizationFilterContext.Provider value={contextValue}>
      {children}
    </OrganizationFilterContext.Provider>
  );
};

