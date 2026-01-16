import { createContext, useContext, useEffect, useState, useCallback, useMemo } from 'react'
import { Alert } from "reactstrap";

const AlertContext = createContext()

export const useAlert = () => {
  const context = useContext(AlertContext)
  if (!context) {
    throw new Error('useAlert must be used within an AlertProvider')
  }
  return context
}

export const AlertProvider = ({ children }) => {
  const [alert, setAlert] = useState(null)

  const showAlert = useCallback((alertData) => {
    setAlert(alertData)
  }, [])

  useEffect(() => {
    if (!alert?.message) {
      return
    }

    const timer = setTimeout(() => {
      setAlert(null)
    }, 5000)

    return () => {
      clearTimeout(timer)
    }
  }, [alert?.message])

  const handleDismiss = useCallback(() => {
    setAlert(null)
  }, [])

  const contextValue = useMemo(() => ({ setAlert: showAlert }), [showAlert])

  return (
    <AlertContext.Provider value={contextValue}>
      {alert?.message && (
        <div 
          className="position-fixed top-0 start-50 translate-middle-x mt-3"
          style={{ zIndex: 9999, maxWidth: '600px', width: '90%' }}
        >
          <Alert 
            color={alert.status || 'danger'} 
            toggle={handleDismiss}
            fade
            className="shadow-lg"
          >
            <strong>
              {alert.status === 'danger' ? 'Something went wrong!' :
               alert.status === 'success' ? 'Success' : 
               alert.status === 'warning' ? 'Warning' : 'Info'}
            </strong>
            <div className="mt-2">
              {alert.message}
            </div>
          </Alert>
        </div>
      )}
      {children}
    </AlertContext.Provider>
  )
}