
import { useCallback } from 'react'
import {useAlert} from "./GlobalAlert.jsx";

export default function useCatch() {
  const { setAlert } = useAlert()

  const cWrapper = useCallback((innerFunction, swallowError = false) => {
    innerFunction()
      ?.catch((error) => {
        if (swallowError) {
          return
        }

        setAlert({
          message: "something is wrong",
          status: 'danger',
        })
      })

    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [setAlert])

  return {
    cWrapper,
  }
}
