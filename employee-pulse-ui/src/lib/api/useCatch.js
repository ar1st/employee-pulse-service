import {useCallback} from 'react'
import {useAlert} from "./GlobalAlert.jsx";

export default function useCatch() {
  const { setAlert } = useAlert()

  const cWrapper = useCallback((innerFunction, swallowError = false) => {
    innerFunction()
      ?.catch((error) => {
        if (swallowError) {
          return
        }
        let errorMessage = 'An error occurred. Please try again.';
        
        if (error.response) {
          errorMessage = error.response.data;
        } else if (error.message) {
          errorMessage = error.message;
        }

        setAlert({
          message: errorMessage,
          status: 'danger',
        })
      })

  }, [setAlert])

  return {
    cWrapper,
  }
}
