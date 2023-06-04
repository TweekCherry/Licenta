import Vue from 'vue'
import clonedeep from 'lodash.clonedeep'
import { v4 as randomUUID } from 'uuid'
import vuetify from './plugins/vuetify'
import GenericPrompt from '@/pages/generic/GenericPrompt.vue'
import GenericNotification from '@/pages/generic/GenericNotification.vue'
import store from './store'
import { DateTime } from 'luxon'

Vue.mixin({
  methods: {
    randomUUID,
    clone(target) {
      return clonedeep(target)
    },
    showNotification(config) {
      const Component = Vue.extend(GenericNotification)
      const instance = new Component({
        vuetify,
        propsData: {
          ...config
        }
      })
      instance.$mount()
      this.$root.$el.appendChild(instance.$el)
      return new Promise((resolve) => {
        if (config.type === 'download') {
          resolve(instance)
        } else {
          instance.$on('hidden', () => {
            resolve()
            instance.$destroy()
          })
        }
      })
    },
    showDialog(config) {
      const Component = Vue.extend(GenericPrompt)
      const instance = new Component({
        vuetify,
        propsData: {
          ...config
        }
      })
      instance.$mount()
      // eslint-disable-next-line no-unused-vars
      return new Promise((resolve, reject) => {
        instance.$on('confirmed', r => {
          resolve(r)
          instance.$destroy()
        })
        // eslint-disable-next-line no-unused-vars
        instance.$on('cancelled', r => {
          if (config.triggerCancellation === true) {
            reject(new Error('cancelled', r))
          }
          instance.$destroy()
        })
      })
    },
    showConfirmationDialog(message, triggerCancellation) {
      const config = {
        type: 'confirmation',
        text: message,
        cancelText: 'No',
        okText: 'Yes',
        triggerCancellation: triggerCancellation
      }
      return this.showDialog(config)
    },
    showErrorNotification(message) {
      const config = {
        title: message,
        type: 'error'
      }
      return this.showNotification(config)
    },
    showSuccessNotification(message) {
      const config = {
        title: message,
        type: 'success'
      }
      return this.showNotification(config)
    },
    showSelectPromptDialog(title, label, rules, selectOptions, itemText = 'label', value = null) {
      const config = {
        type: 'select',
        title: title,
        selectValue: value,
        inputLabel: label,
        selectOptions: selectOptions,
        selectItemText: itemText,
        validationRules: rules
      }
      return this.showDialog(config)
    },
    downloadFile(fileName, dataBlob) {
      var fileURL = window.URL.createObjectURL(dataBlob)
      var fileLink = document.createElement('a')

      fileLink.href = fileURL
      fileLink.setAttribute('download', fileName)
      document.body.appendChild(fileLink)

      fileLink.click()
      fileLink.remove()
    },
    computeInvestigationPrice(investigation) {
      if (store.state.profile.subscription !== null) {
        const benefit = store.state.profile.subscription.benefits.find(benefit => benefit.investigation === investigation.id)
        if (benefit !== undefined) {
          return investigation.price - (investigation.price * benefit.discount / 100)
        }
      }
      return investigation.price
    },
    isReduced(investigation) {
      if (store.state.profile.subscription !== null) {
        const benefit = store.state.profile.subscription.benefits.find(benefit => benefit.investigation === investigation.id)
        if (benefit !== undefined) {
          return benefit.discount > 0
        }
      }
      return false
    },
    getNewAppointmentTimestamp() {
      let timestamp = DateTime.now().set({ minute: 0 })// take the current date
      if (timestamp.hour < 8) { // if the current hour it's outside of the work programs
        timestamp = timestamp.set({ hour: 8 }) // work program starts at 8:00
      } else if (timestamp.hour >= 19) { // work program ends at 20:00(last appointment)
        timestamp = timestamp.set({ hour: 8 }).plus({ days: 1 }) // set hour to 8:00 and increase day by 1h
      }
      return timestamp.setZone('utc', { keepLocalTime: true }).toISO()
    }
  }
})
