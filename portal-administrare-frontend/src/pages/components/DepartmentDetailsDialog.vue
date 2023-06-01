<template>
  <v-dialog v-model="visible" :fullscreen="fullscreen" scrollable width="800">
    <ValidationObserver ref="observer" v-slot="{ handleSubmit }">
      <v-form @submit.prevent="handleSubmit(saveSubscription)">
        <v-card :loading="loading">
          <v-card-title class="justify-space-between">
            <div>
              <div>Department details</div>
            </div>
            <div>
              <v-btn icon small @click="fullscreen = !fullscreen">
                <v-icon v-if="!fullscreen">mdi-window-maximize</v-icon>
                <v-icon v-if="fullscreen">mdi-window-restore</v-icon>
              </v-btn>
              <v-btn icon small @click="visible = false">
                <v-icon>mdi-close</v-icon>
              </v-btn>
            </div>
          </v-card-title>
          <v-divider class="mx-4 pb-5"></v-divider>
          <v-card-text>
            <div class="row">
              <div class="col-12">
                <ValidationProvider rules="required" name="Name" v-slot="{ errors, valid }">
                  <v-text-field v-model="departmentData.name" label="Name" required dense :error-messages="errors" :success="valid" focus name="name"/>
                </ValidationProvider>
              </div>
            </div>
          </v-card-text>
          <v-divider class="mx-4"></v-divider>
          <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn plain color="red" @click="visible = false">Cancel</v-btn>
            <v-btn plain type="submit" color="green">Save</v-btn>
            <v-spacer></v-spacer>
          </v-card-actions>
        </v-card>
      </v-form>
    </ValidationObserver>
  </v-dialog>
</template>
<script>
import backend from '@/plugins/backend'
import { ValidationObserver, ValidationProvider } from 'vee-validate'
export default {
  name: 'SubscriptionDetailsDialog',
  components: {
    ValidationObserver, ValidationProvider
  },
  props: {
    isVisible: Boolean,
    department: Object
  },
  computed: {
    visible: {
      get: function() {
        return this.isVisible
      },
      set: function(v) {
        this.$emit('closed')
      }
    },
    id() {
      if (this.department !== null && this.departmentData.name !== this.department.name) {
        return this.department.name
      }
      return null
    }
  },
  data() {
    return {
      fullscreen: false,
      loading: false,
      departmentData: this.newDepartment()
    }
  },
  methods: {
    saveSubscription() {
      this.loading = true
      backend.$saveDepartment(this.id, this.departmentData).then(r => {
        this.$emit('save')
        this.visible = false
      }).catch(e => { alert('An error occured, try again later') })
        .then(() => { this.loading = false })
    },
    newDepartment() {
      return {
        name: ''
      }
    }
  },
  watch: {
    isVisible: function(newValue, oldValue) {
      if (newValue) {
        if (this.$refs.observer) {
          this.$refs.observer.reset()
        }
        if (this.department !== null) {
          this.departmentData = this.clone(this.department)
        } else {
          this.departmentData = this.newDepartment()
        }
      }
    }
  }
}
</script>
