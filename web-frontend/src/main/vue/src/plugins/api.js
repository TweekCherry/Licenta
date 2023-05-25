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
  config.headers.Authorization = store.getters.apiToken
  return config
})
// global setup for logout, in case the api token expires during an ajax request
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if ((error.response && error.response.status === 401) && store.getters.apiToken != null) { // if token expired or server responds with status 401
      store.dispatch('logout') // trigger logout
    }
    return Promise.reject(error)
  }
)

export default {
  install(Vue, options = {}) {
    Vue.prototype.$login = function(username, password, rememberMe) {
      return api.post('/login', {
        username: username,
        password: password,
        rememberMe: rememberMe
      })
    }
    Vue.prototype.$logout = function() {
      return api.post('/logout')
    }
    Vue.prototype.$findUserProfile = function() {
      return api.post('/account/profile')
    }
    Vue.prototype.$findMedics = function() {
      return api.get('/medics')
    }
  }
}
