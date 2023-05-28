import Vue from 'vue'
import Vuex from 'vuex'
import LocalStorage from '@/plugins/local-storage.js'

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    apiToken: null
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
    }
  },
  actions: {
  },
  modules: {
  }
})
