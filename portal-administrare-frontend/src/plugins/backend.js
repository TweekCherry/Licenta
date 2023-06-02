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
    if ((error.response && error.response.status === 401) && store.state.apiToken != null) { // if token expired or server responds with status 401
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
  $findSubscriptions: function() {
    return api.get('/subscriptions')
  },
  $saveSubscription: function(subscription) {
    return api.post('/subscriptions', subscription)
  },
  $removeSubscription: function(id) {
    return api.delete(`/subscriptions/${id}`)
  },
  $findDepartments: function() {
    return api.get('/departments')
  },
  $saveDepartment: function(id, department) {
    return api.post('/departments', department, {
      params: {
        id: id
      }
    })
  },
  $removeDepartment: function(id) {
    return api.delete(`/departments/${id}`)
  },
  $findClinics: function() {
    return api.get('/clinic')
  },
  $saveClinic: function(clinic) {
    return api.post('/clinic', clinic)
  },
  $removeClinic: function(id) {
    return api.delete(`/clinic/${id}`)
  },
  $findInvestigations: function() {
    return api.get('/investigations')
  },
  $saveInvestigation: function(investigation) {
    return api.post('/investigations', investigation)
  },
  $removeInvestigation: function(id) {
    return api.delete(`/investigations/${id}`)
  },
  $findInvestigationTypes: function() {
    return api.get('/investigations/types')
  },
  $saveInvestigationType: function(id, investigationType) {
    return api.post('/investigations/types', investigationType, {
      params: {
        id: id
      }
    })
  },
  $removeInvestigationType: function(id) {
    return api.delete(`/investigations/types/${id}`)
  },
  $findMedics: function() {
    return api.get('/medics')
  },
  $saveMedic: function(medic) {
    return api.post('/medics', medic)
  },
  $updateMedic: function(medic) {
    return api.put('/medics', medic)
  },
  $removeMedic: function(id) {
    return api.delete(`/medics/${id}`)
  },
  $findStats: function() {
    return api.get('/stats')
  }
}
