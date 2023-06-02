import axios from 'axios'
import store from '../store'

// global url and headers setup for all ajax requests
const API_URL = process.env.VUE_APP_API_URL
const api = axios.create({
  baseURL: API_URL,
  headers: {
    'Content-Type': 'application/json'
  }
})
// global api token setup for all ajax requests
api.interceptors.request.use(function(config) {
  if (store.state.apiToken !== null) {
    config.headers.Authorization = store.state.apiToken.key
  }
  return config
})
// global setup for logout, in case the api token expires during an ajax request
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if ((error.response && error.response.status === 403) && store.state.apiToken != null) { // if token expired or server responds with status 401
      store.dispatch('logout') // trigger logout
    }
    return Promise.reject(error)
  }
)

export default {
  $login: function(email, password, rememberMe) {
    return api.post('/login', {
      email: email,
      password: password,
      rememberMe: rememberMe
    })
  },
  $logout: function() {
    return api.post('/logout')
  },
  $forgotPassword: function(redirectUrl, forgotPasswordRequest) {
    return api.post('/forgot-password', forgotPasswordRequest, {
      params: {
        url: redirectUrl
      }
    })
  },
  $completeForgotPassword: function(token, newPassword) {
    return api.put('/forgot-password', newPassword, {
      params: {
        token: token
      }
    })
  },
  $findUserProfile: function() {
    return api.get('/account/profile')
  },
  $findAppointments: function() {
    return api.get('/appointments')
  }
}
