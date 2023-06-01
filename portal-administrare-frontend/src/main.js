import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import vuetify from './plugins/vuetify'

import '@/registerServiceWorker'
import '@/vee-validate'
import '@/mixins.js'

store.commit('loadLocalStorage') // load api token from local storage on page refresh

Vue.config.devtools = true
Vue.config.productionTip = false
new Vue({
  router,
  store,
  vuetify,
  render: (h) => h(App)
}).$mount('#app')
