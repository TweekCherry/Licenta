import Vue from 'vue'
import clonedeep from 'lodash.clonedeep'
import { v4 as randomUUID } from 'uuid'

Vue.mixin({
  methods: {
    randomUUID,
    clone(target) {
      return clonedeep(target)
    }
  }
})
