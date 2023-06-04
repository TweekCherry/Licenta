import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import vuetify from './plugins/vuetify'
import WebSocket from '@/plugins/websocket.js'

import '@/assets/scss/utils.scss'

import '@/registerServiceWorker'
import '@/vee-validate'
import '@/mixins.js'

Vue.use(WebSocket, {
  pingTimeout: 1000 * 30,
  pongTimeout: 1000 * 30,
  reconnectTimeout: 1000 * 15,
  pingMessage: 'heartbeat',
  store: store
})
store.commit('loadLocalStorage') // load api token from local storage on page refresh
Vue.config.devtools = true
Vue.config.productionTip = false
new Vue({
  router,
  store,
  vuetify,
  render: (h) => h(App)
}).$mount('#app')
