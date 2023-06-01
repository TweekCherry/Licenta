<template>
  <div>
    <div class="d-flex align-center justify-space-between" v-if="hasFile">
      <div>{{label}}</div>
      <v-btn icon color="red" @click="removeFile">
        <v-icon>mdi-close</v-icon>
      </v-btn>
    </div>
    <div @click="launchFilePicker()">
      <v-avatar tile height="auto" v-if="!value" class="grey lighten-3 w-100 elevation-2 py-2">
        <div class="d-flex flex-column">
          <v-icon style="cursor:pointer">mdi-upload</v-icon>
          <span>Click to upload {{label.toLowerCase()}}</span>
        </div>
      </v-avatar>
      <v-avatar tile v-else class="w-100 elevation-2">
        <img :src="value" alt="loginLogo">
      </v-avatar>
    </div>
    <ValidationProvider :rules="rules" :name="label" v-slot="{ errors, validate }" ref="validator">
      <input type="file" ref="file" name="file" @change="onFileChange($event, validate)" :error-messages="errors" class="d-none">
      <div v-if="errors.length > 0" class="col-12 text-center red--text">{{errors[0]}}</div>
    </ValidationProvider>
  </div>
</template>

<script>
import { ValidationProvider } from 'vee-validate'

export default {
  name: 'ImageInput',
  components: {
    ValidationProvider
  },
  model: {
    prop: 'value',
    event: 'input'
  },
  props: {
    label: String,
    value: String,
    required: {
      type: Boolean,
      dafault: false
    }
  },
  data: function() {
    return {
      errorDialog: null,
      errorText: ''
    }
  },
  computed: {
    hasFile() {
      return this.value !== null && this.value !== undefined
    },
    rules() {
      if (this.required) {
        return 'required|image'
      }
      return 'image'
    }
  },
  methods: {
    launchFilePicker() {
      this.$refs.file.click()
    },
    onFileChange(event, validator) {
      validator(event).then(r => {
        if (r.valid) {
          this.getBase64ImageFormat(event.target.files[0])
            .then(data => this.$emit('input', data))
        }
      })
    },
    getBase64ImageFormat(file) {
      return new Promise((resolve, reject) => {
        const reader = new FileReader()
        reader.readAsDataURL(file)
        reader.onload = () => resolve(reader.result)
        reader.onerror = error => reject(error)
      })
    },
    removeFile() {
      this.$emit('input', null)
      this.$refs.file.value = null
      this.$refs.file.dispatchEvent(new Event('change'))
    }
  },
  watch: {
    value(newValue, oldValue) {
      this.$refs.file.value = null
      this.$refs.file.dispatchEvent(new Event('change'))
      this.$refs.validator.reset()
    }
  }
}
</script>
<style lang="scss">
.w-100 {
  width: 100% !important;
}
</style>
