/* eslint-disable camelcase */
import { extend } from 'vee-validate'
import { required, email, between, numeric, double } from 'vee-validate/dist/rules'
import { DateTime } from 'luxon'

extend('email', email)
extend('between', between)
extend('numeric', numeric)
extend('double', double)
// Add the required rule
extend('required', {
  ...required,
  message: (field) => {
    return `${field} is required`
  }
})

export const dateFormat = 'dd-LL-yyyy'

extend('dateFormat', {
  validate: function(value, args) {
    return DateTime.fromFormat(value, args[0]).invalid === null
  },
  message: (field, params, data) => {
    return `${field} is invalid, use ${params[0]} format`
  }
})
extend('after', {
  validate: (value, args) => {
    let target = args[0]
    if (typeof args[0] === 'string') {
      target = DateTime.fromISO(args[0], { zone: 'utc' })
    }
    const current = DateTime.fromFormat(value, dateFormat, { zone: 'utc' })
    return current >= target
  },
  message: (field, params, data) => {
    let target = params[0]
    if (typeof params[0] === 'string') {
      target = DateTime.fromISO(params[0], { zone: 'utc' })
    }
    return `${field} value must be after ${target.toFormat(dateFormat)}`
  }
})
extend('before', {
  validate: (value, args) => {
    let target = args[0]
    if (typeof args[0] === 'string') {
      target = DateTime.fromISO(args[0], { zone: 'utc' })
    }
    const current = DateTime.fromFormat(value, dateFormat, { zone: 'utc' })
    return current <= target
  },
  message: (field, params, data) => {
    let target = params[0]
    if (typeof params[0] === 'string') {
      target = DateTime.fromISO(params[0], { zone: 'utc' })
    }
    return `${field} value must be before ${target.toFormat(dateFormat)}`
  }
})
