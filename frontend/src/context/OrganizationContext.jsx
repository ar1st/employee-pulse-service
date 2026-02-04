import { createContext, useContext, useEffect, useState } from 'react';
import { DEFAULT_ORGANIZATION_ID } from '../lib/api/apiUrls.js';
import { setOrganizationHeader } from '../lib/api/client.js';

const OrganizationContext = createContext(null);

export function OrganizationProvider({ children }) {
  const [selectedOrganizationId, setSelectedOrganizationId] = useState(DEFAULT_ORGANIZATION_ID);
  const [selectedOrganization, setSelectedOrganization] = useState(null);

  useEffect(() => {
    if (selectedOrganizationId != null) {
      setOrganizationHeader(selectedOrganizationId);
    }
  }, [selectedOrganizationId]);

  const setOrganization = (org) => {
    if (org && typeof org === 'object') {       
      setSelectedOrganization(org);
      setSelectedOrganizationId(org.value ?? null);
    } else {
      setSelectedOrganization(null);
      setSelectedOrganizationId(null);
    }
  };

  const value = {
    selectedOrganizationId: selectedOrganizationId ?? DEFAULT_ORGANIZATION_ID,
    selectedOrganization,
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
  console.log('ctx ', ctx)
  if (!ctx) {
    throw new Error('useOrganization must be used within an OrganizationProvider');
  }
  return ctx;
}


