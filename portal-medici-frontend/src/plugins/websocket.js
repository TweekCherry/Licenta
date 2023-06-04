import { BehaviorSubject, ReplaySubject } from 'rxjs'
import { filter, map } from 'rxjs/operators'
const API_URL = process.env.VUE_APP_API_URL + '/websocket'

export default {
  install(Vue, options = {
    url: API_URL,
    pingTimeout: 1000 * 10,
    pongTimeout: 1000 * 10,
    reconnectTimeout: 1000 * 5,
    pingMessage: 'heartbeat',
    apiKey: null,
    store: null
  }) {
    if (options.url === undefined) {
      options.url = API_URL
    }
    const emitter = new BehaviorSubject({
      type: null,
      payload: null
    })
    let sessionId = null
    let shouldReconnect = true
    let pongTimeoutId = 0
    let webSocket = null
    const subscriptions = []
    let sessionSubject = new ReplaySubject(null)
    // ------------------------ Plugin private methods ---------------------------
    const getUrl = function() {
      const urlPartsRegex = /(.*):\/\/(.*)/g
      const matcher = urlPartsRegex.exec(options.url)
      let protocol = 'ws://'
      if (matcher[1] === 'https') {
        protocol = 'wss://'
      }
      return `${protocol}${matcher[2]}?Authorization=${options.apiKey}`
    }
    const connect = function() {
      console.log('Connecting')
      try {
        const url = getUrl()
        webSocket = new WebSocket(url)
        initEventHandlers()
      } catch (e) {
        reconnect()
      }
    }
    const disconnect = function() {
      try {
        sessionId = null
        webSocket.close()
        shouldReconnect = false
      } catch (e) {
        console.log(e)
      }
    }
    const reconnect = function() {
      if (!shouldReconnect) {
        return
      }
      shouldReconnect = false
      sessionId = null
      setTimeout(() => {
        shouldReconnect = true
        connect()
      }, options.reconnectTimeout)
    }
    const startHeartBeat = function() {
      clearTimeout(pongTimeoutId)
      setTimeout(() => {
        console.log('sending ping')
        if (webSocket.readyState === WebSocket.CLOSED) {
          return
        }
        webSocket.send(options.pingMessage)
        pongTimeoutId = setTimeout(() => {
          console.log('timeout on pong received')
          webSocket.close()
          reconnect()
        }, options.pongTimeout)
      }, options.pingTimeout)
    }
    const initEventHandlers = function() {
      webSocket.onclose = (event) => {
        console.log(event)
        removeSubscriptions()
        if (shouldReconnect) {
          reconnect()
        } else {
          shouldReconnect = true
        }
      }
      webSocket.onerror = (event) => {
        console.log(event)
        removeSubscriptions()
        reconnect()
      }
      webSocket.onopen = (event) => {
        startHeartBeat()
      }
      webSocket.onmessage = (event) => {
        if (event.data === options.pingMessage) {
          console.log('pong received')
          startHeartBeat()
        } else {
          const eventData = JSON.parse(event.data)
          if (eventData.type === 'DefaultSessionId') {
            sessionId = eventData.payload.id
            sessionSubject.next(sessionId)
            console.log(`Session id received ${eventData.payload.id}`)
          }
          emitter.next(eventData)
        }
      }
    }
    const removeSubscriptions = function() {
      while (subscriptions.length > 0) {
        subscriptions[0].unsubscribe()
        subscriptions.splice(0, 1)
      }
    }
    // ------------------------ Vue instance methods -----------------------------
    Vue.prototype.$onConnection = function() {
      return sessionSubject
    }
    Vue.prototype.$connect = function(apiKey) {
      console.log('connect')
      options.apiKey = apiKey
      sessionSubject = new ReplaySubject(null)
      connect()
    }
    Vue.prototype.$disconnect = function() {
      console.log('disconnect')
      disconnect()
    }
    Vue.prototype.$getEventStream = function(eventType) {
      return emitter.pipe(filter(e => e.type === eventType))
        .pipe(map(e => e.payload))
    }
  }
}
