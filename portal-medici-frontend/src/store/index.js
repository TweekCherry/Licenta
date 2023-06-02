import Vue from 'vue'
import Vuex from 'vuex'
import LocalStorage from '@/plugins/local-storage.js'
import router from '../router'

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    apiToken: null,
    profile: null
  },
  getters: {
  },
  mutations: {
    login(state, apiToken) {
      state.apiToken = apiToken
      LocalStorage.put('apiToken', apiToken)
    },
    logout(state) {
      state.apiToken = null
      LocalStorage.remove('apiToken')
    },
    loadLocalStorage(state) {
      const apiToken = LocalStorage.get('apiToken')
      if (apiToken !== null) {
        state.apiToken = apiToken
      }
    },
    userProfile(state, profile) {
      state.profile = profile
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
