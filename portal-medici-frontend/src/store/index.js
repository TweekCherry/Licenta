import Vue from 'vue'
import Vuex from 'vuex'
import LocalStorage from '@/plugins/local-storage.js'
import router from '../router'

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    apiToken: null,
    profile: null,
    medic: null,
    activeConsultation: null
  },
  getters: {
  },
  mutations: {
    login(state, apiToken) {
      state.apiToken = apiToken
      LocalStorage.put('apiToken', apiToken)
      this._vm.$connect(state.apiToken.key) // open the websocket
    },
    logout(state) {
      state.apiToken = null
      LocalStorage.remove('apiToken')
      this._vm.$disconnect() // close the websocket
    },
    loadLocalStorage(state) {
      const apiToken = LocalStorage.get('apiToken')
      if (apiToken !== null) {
        state.apiToken = apiToken
        this._vm.$connect(state.apiToken.key) // open the websocket
      }
    },
    userProfile(state, profile) {
      state.profile = profile
    },
    medicProfile(state, medic) {
      state.medic = medic
    },
    activeConsultation(state, consultation) {
      if (consultation !== '') {
        state.activeConsultation = consultation
      }
    }
  },
  actions: {
    logout() {
      this.commit('logout')
      router.push({ name: 'Login' })
    }
  },
  modules: {
  }
})
