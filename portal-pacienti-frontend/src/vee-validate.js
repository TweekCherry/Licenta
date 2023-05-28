/* eslint-disable camelcase */
import { extend } from 'vee-validate'
import { required, email, confirmed, length } from 'vee-validate/dist/rules'

// Add the required rule
extend('length', length)
extend('cnp', {
  validate: (value, args) => {
    return /^[1-6]\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\d|3[01])(0[1-9]|[1-4]\d|5[0-2]|99)(00[1-9]|0[1-9]\d|[1-9]\d\d)\d$/.test(value)
  },
  message: (field) => {
    return 'CNP invalid'
  }
})
extend('confirmed', confirmed)
extend('email', {
  ...email,
  message: (field) => {
    return `${field} must be a valid email address`
  }
})
extend('required', {
  ...required,
  message: (field) => {
    return `${field} is required`
  }
})
