import { createContext, useContext, useEffect, useState } from 'react';
import { setOrganizationHeader } from '../lib/api/client.js';

const OrganizationContext = createContext(null);

const DEFAULT_ORGANIZATION = {
  value: 1,
  label: 'University of Macedonia',
};

export function OrganizationProvider({ children }) {
  const [selectedOrganization, setSelectedOrganization] = useState(DEFAULT_ORGANIZATION);

  useEffect(() => {
    const id = selectedOrganization?.label ?? 'University of Macedonia';
    setOrganizationHeader(id);
  }, [selectedOrganization]);

  const setOrganization = (org) => {
    if (org && typeof org === 'object') {
      setSelectedOrganization(org);
    } else {
      setSelectedOrganization(DEFAULT_ORGANIZATION);
    }
  };

  const value = {
    selectedOrganization,
    selectedOrganizationName: selectedOrganization?.label ?? '',
    setOrganization,
  };

  return (
    <OrganizationContext.Provider value={value}>
      {children}
    </OrganizationContext.Provider>
  );
}

export function useOrganization() {
  const ctx = useContext(OrganizationContext);
  if (!ctx) {
    throw new Error('useOrganization must be used within an OrganizationProvider');
  }
  return ctx;
}

