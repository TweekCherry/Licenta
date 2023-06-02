import Vue from 'vue'
import clonedeep from 'lodash.clonedeep'
import { v4 as randomUUID } from 'uuid'
import vuetify from './plugins/vuetify'
import GenericPrompt from '@/pages/generic/GenericPrompt.vue'
import GenericNotification from '@/pages/generic/GenericNotification.vue'

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
    }
  }
})
