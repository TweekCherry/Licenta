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
  $register: function(redirectUrl, registerRequest) {
    return api.post('/register', registerRequest, {
      params: {
        url: redirectUrl
      }
    })
  },
  $completeRegistration: function(token) {
    return api.put('/register', null, {
      params: {
        token: token
      }
    })
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
  $updateUserProfile: function(profile) {
    return api.post('/account/profile', profile)
  },

  $findSubscriptions: function() {
    return api.get('/subscriptions')
  },
  $registerSubscription: function(id, months) {
    return api.put(`/subscriptions/subscribe/${id}`, null, {
      params: {
        months: months
      }
    })
  },
  $findAppointments: function(onlyScheduled) {
    return api.get('/appointments', {
      params: {
        onlyScheduled: onlyScheduled
      }
    })
  },
  $deleteAppointment: function(id) {
    return api.delete(`/appointments/${id}`)
  },
  $saveAppointment: function(appointment) {
    return api.post('/appointments', appointment)
  },
  $findDepartments: function() {
    return api.get('/departments')
  },
  $findClinics: function() {
    return api.get('/clinic')
  },
  $findClinicsByInvestigation: function(id) {
    return api.get(`/clinic/investigations/${id}`)
  },
  $findMedicsByClinic: function(clinic) {
    return api.get(`/medics/clinic/${clinic}`)
  },
  $findInvestigationsByClinic: function(clinic) {
    return api.get(`/investigations/clinic/${clinic}`)
  },
  $findBookedAppointmentDates: function(clinic, medic, id) {
    return api.get('/appointments/check-dates', {
      params: {
        clinic: clinic,
        medic: medic,
        id: id
      }
    })
  },

  $findInvestigations: function() {
    return api.get('/investigations')
  },

  $findMedics: function() {
    return api.get('/medics')
  },

  $findConsultations: function() {
    return api.get('/consultations')
  },

  $downloadConsultation: function(id) {
    return api.get(`/consultations/${id}`, {
      responseType: 'blob'
    })
  }
}
