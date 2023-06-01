<template>
  <v-dialog v-model="visible" max-width="600" scrollable persistent overlay-opacity="0.65">
    <v-card>
      <ValidationObserver ref="observer" v-slot="{ handleSubmit }">
        <v-form @submit.prevent="handleSubmit(emitValue)">
          <v-card-title class="justify-center">{{title}}</v-card-title>
          <v-divider class="mx-4"></v-divider>
          <v-card-text>
            <div class="row">
              <div class="col-12 text-center" v-if="type === 'alert' || type === 'confirmation'">{{text}}</div>
              <div class="col-12" v-if="type === 'prompt'">
                <ValidationProvider :rules="validationRules" :name="inputLabel" v-slot="{ errors, valid }">
                  <v-text-field v-model="inputModel" :label="inputLabel" required dense :error-messages="errors" :success="valid"></v-text-field>
                </ValidationProvider>
              </div>
              <div class="col-12" v-if="type === 'select'">
                <ValidationProvider :rules="validationRules" :name="inputLabel" v-slot="{ errors, valid }">
                <v-autocomplete
                  v-model="inputModel"
                  :label="inputLabel"
                  required
                  dense
                  :items="selectOptions"
                  :item-text="selectItemText"
                  return-object
                  :error-messages="errors"
                  :success="valid">
                  </v-autocomplete>
                </ValidationProvider>
              </div>
              <div class="col-12" v-if="type === 'file'">
                <ValidationProvider :rules="validationRules" :name="inputLabel" v-slot="{ errors, valid }">
                  <v-file-input
                    show-size
                    :label="inputLabel"
                    v-model="inputModel"
                    required
                    :error-messages="errors"
                    :success="valid">
                  </v-file-input>
                </ValidationProvider>
              </div>
            </div>
          </v-card-text>
          <v-divider class="mx-4"></v-divider>
          <v-card-actions class="d-flex w-100 justify-center">
            <div class="pr-1 w-100" v-if="type !== 'alert'">
              <v-btn plain block color="error" @click="visible = false">{{cancelText}}</v-btn>
            </div>
            <div class="pl-1 w-100">
              <v-btn plain block color="success" type="submit">{{okText}}</v-btn>
            </div>
          </v-card-actions>
        </v-form>
        </ValidationObserver>
      </v-card>
  </v-dialog>
</template>
<script>
import { ValidationProvider, ValidationObserver } from 'vee-validate'
export default {
  name: 'GenericPrompt',
  components: {
    ValidationProvider, ValidationObserver
  },
  props: {
    title: {
      type: String,
      default: ''
    },
    text: String,
    type: {
      type: String,
      default: 'alert'
    },
    success: {
      type: Boolean,
      default: false
    },
    error: {
      type: Boolean,
      default: false
    },
    cancelText: {
      type: String,
      default: 'Cancel'
    },
    okText: {
      type: String,
      default: 'Ok'
    },
    inputLabel: {
      type: String,
      default: 'Enter your value'
    },
    inputType: {
      type: String,
      default: 'text'
    },
    inputValue: {
      type: [String, Object, File, Array],
      default: () => null
    },
    selectValue: {
      type: Object,
      default: null
    },
    selectOptions: {
      type: Array,
      default: null
    },
    selectItemText: {
      type: String,
      default: 'label'
    },
    validationRules: {
      type: String,
      default: ''
    }
  },
  computed: {
    visible: {
      get: function() {
        return this.isVisible
      },
      set: function() {
        this.isVisible = false
        this.$nextTick(() => {
          this.$emit('cancelled')
        })
      }
    }
  },
  data: () => ({
    inputModel: null,
    isVisible: true
  }),
  mounted() {
    if (this.inputValue !== '') {
      this.inputModel = this.inputValue
    } else {
      this.inputModel = this.selectValue
    }
  },
  methods: {
    emitValue() {
      this.isVisible = false
      this.$nextTick(() => {
        this.$emit('confirmed', this.inputModel)
      })
    }
  }
}
</script>
