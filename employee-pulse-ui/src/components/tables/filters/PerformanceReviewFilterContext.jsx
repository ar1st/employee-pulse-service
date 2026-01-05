import { createContext, useContext, useState, useCallback, useMemo } from 'react';

const PerformanceReviewFilterContext = createContext();

export const usePerformanceReviewFilter = () => {
  const context = useContext(PerformanceReviewFilterContext);
  if (!context) {
    throw new Error('usePerformanceReviewFilter must be used within a PerformanceReviewFilterProvider');
  }
  return context;
};

export const PerformanceReviewFilterProvider = ({ children }) => {
  const [filterValues, setFilterValues] = useState({
    id: '',
    department: '',
    employee: '',
    reporter: '',
    overallRatingMin: '',
    overallRatingMax: '',
    reviewDateStart: '',
    reviewDateEnd: ''
  });

  const updateFilterValues = useCallback((updates) => {
    setFilterValues(prev => ({
      ...prev,
      ...updates
    }));
  }, []);

  const resetFilters = useCallback(() => {
    setFilterValues({
      id: '',
      department: '',
      employee: '',
      reporter: '',
      overallRatingMin: '',
      overallRatingMax: '',
      reviewDateStart: '',
      reviewDateEnd: ''
    });
  }, []);

  const contextValue = useMemo(() => ({
    filterValues,
    setFilterValues: updateFilterValues,
    resetFilters
  }), [filterValues, updateFilterValues, resetFilters]);

  return (
    <PerformanceReviewFilterContext.Provider value={contextValue}>
      {children}
    </PerformanceReviewFilterContext.Provider>
  );
};

