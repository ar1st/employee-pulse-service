import { createContext, useContext, useState, useCallback, useMemo, useEffect } from 'react';
import { getDefaultDates } from '../../lib/dateUtils.js';

const EmployeeFilterContext = createContext();

export const useEmployeeFilter = () => {
  const context = useContext(EmployeeFilterContext);
  if (!context) {
    throw new Error('useEmployeeFilter must be used within an EmployeeFilterProvider');
  }
  return context;
};

export const EmployeeFilterProvider = ({ children }) => {
  const [filterValues, setFilterValues] = useState({
    departmentId: '',
    employeeId: '',
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
      employeeId: '',
      skillId: '',
      ...getDefaultDates()
    });
  }, []);

  const triggerChartGeneration = useCallback(() => {
    setTriggerFetch(prev => prev + 1);
  }, []);

  // Auto-trigger overall rating chart when employee or date range changes
  useEffect(() => {
    if (filterValues.employeeId) {
      setTriggerOverallRatingFetch(prev => prev + 1);
    }
  }, [filterValues.employeeId, filterValues.startDate, filterValues.endDate]);

  // Auto-trigger skill charts when skill is selected or date range changes (if skill is already selected)
  useEffect(() => {
    if (filterValues.employeeId && filterValues.skillId) {
      setTriggerFetch(prev => prev + 1);
    }
  }, [filterValues.skillId, filterValues.startDate, filterValues.endDate]);

  const contextValue = useMemo(() => ({
    filterValues,
    setFilterValues: updateFilterValues,
    resetFilters,
    triggerFetch,
    triggerOverallRatingFetch,
    triggerChartGeneration
  }), [filterValues, updateFilterValues, resetFilters, triggerFetch, triggerOverallRatingFetch, triggerChartGeneration]);

  return (
    <EmployeeFilterContext.Provider value={contextValue}>
      {children}
    </EmployeeFilterContext.Provider>
  );
};

