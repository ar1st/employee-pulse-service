import { createContext, useContext, useState, useCallback, useMemo } from 'react';

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
    id: '',
    firstName: '',
    lastName: '',
    email: '',
    hireDate: '',
    department: '',
    occupation: ''
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
      firstName: '',
      lastName: '',
      email: '',
      hireDate: '',
      department: '',
      occupation: ''
    });
  }, []);

  const contextValue = useMemo(() => ({
    filterValues,
    setFilterValues: updateFilterValues,
    resetFilters
  }), [filterValues, updateFilterValues, resetFilters]);

  return (
    <EmployeeFilterContext.Provider value={contextValue}>
      {children}
    </EmployeeFilterContext.Provider>
  );
};

