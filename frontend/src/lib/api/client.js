import axios from 'axios'

const api = axios.create()

let organizationId = null

export const setOrganizationHeader = (orgId) => {
  organizationId = orgId
}

api.interceptors.request.use((config) => {
  const updatedConfig = { ...config }
  updatedConfig.headers = updatedConfig.headers ?? {}

  if (organizationId != null) {
    updatedConfig.headers['X-Organization-Id'] = organizationId
  }

  return updatedConfig
})

export const axiosGet = async (url, parameters) => {
  return await api.get(url, {
    params: { ...parameters },
  })
    .catch((error) => {
      console.log('GET error ', url)
      console.log('GET error ', error)
      throw error
    })
}

export const axiosPost = async (url, data, config = {}) => {
  return await api
    .post(url, data, config)
    .catch((error) => {
      console.log('POST error ', url)
      console.log('POST error ', error)
      throw error
    })
}

export const axiosPut = async (url, data) => {
  return await api.put(url, data).catch((error) => {
    console.log('PUT error ', url)
    console.log('PUT error ', error)
    throw error
  })
}

export const axiosDelete = async (url) => {
  return await api.delete(url)
    .catch((error) => {
      console.log('DELETE error ', url)
      console.log('DELETE error ', error)
      throw error
    })
}