import { createContext, useContext, useState, useCallback, useMemo } from 'react';
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
    skillId: '',
    ...getDefaultDates()
  });
  const [triggerFetch, setTriggerFetch] = useState(0);

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

  const contextValue = useMemo(() => ({
    filterValues,
    setFilterValues: updateFilterValues,
    resetFilters,
    triggerFetch,
    triggerChartGeneration
  }), [filterValues, updateFilterValues, resetFilters, triggerFetch, triggerChartGeneration]);

  return (
    <EmployeeFilterContext.Provider value={contextValue}>
      {children}
    </EmployeeFilterContext.Provider>
  );
};

