import { createContext, useContext, useState, useCallback, useMemo } from 'react';
import { getDefaultDates } from '../../lib/dateUtils.js';

const OrganizationFilterContext = createContext();

export const useOrganizationFilter = () => {
  const context = useContext(OrganizationFilterContext);
  if (!context) {
    throw new Error('useOrganizationFilter must be used within an OrganizationFilterProvider');
  }
  return context;
};

export const OrganizationFilterProvider = ({ children }) => {
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
    <OrganizationFilterContext.Provider value={contextValue}>
      {children}
    </OrganizationFilterContext.Provider>
  );
};

