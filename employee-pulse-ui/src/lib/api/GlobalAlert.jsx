import { createContext, useContext, useEffect, useState } from 'react'
import {Alert} from "reactstrap";

const AlertContext = createContext()

export const useAlert = () => {
  const setAlert = useContext(AlertContext)

  return { setAlert }
}

export const AlertProvider = ({ children }) => {
  const [alert, setAlert] = useState({})


  useEffect(() => {
    let timer
    if (alert.message) {
      timer = setTimeout(() => {
        setAlert({ message: '' })
      }, 5000)
    }

    return () => clearTimeout(timer)
  }, [alert.message])

  return (
    <AlertContext.Provider value={setAlert}>
      {alert.message &&
        <div className="fixed-bottom d-flex justify-content-center p-2 pe-none">
          <Alert className="fixed-alert pe-auto" variant={alert.status} onClose={() => setAlert({ message: '' })}
                 dismissible>
            {alert.status === 'danger' && <Alert.Heading>Something went wrong</Alert.Heading>}
            <p>
              This is the error
            </p>
          </Alert>
        </div>
      }
      {children}
    </AlertContext.Provider>
  )
}