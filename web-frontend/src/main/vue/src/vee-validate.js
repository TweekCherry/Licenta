/* eslint-disable camelcase */
import { extend } from 'vee-validate'
import { required } from 'vee-validate/dist/rules'

// Add the required rule
extend('required', {
  ...required,
  message: (field) => {
    return `${field} is required`
  }
})
