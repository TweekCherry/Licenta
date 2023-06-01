<template>
  <v-dialog v-model="visible" :fullscreen="fullscreen" scrollable width="800">
    <ValidationObserver ref="observer" v-slot="{ handleSubmit }">
      <v-form @submit.prevent="handleSubmit(saveClinic)">
        <v-card :loading="loading">
          <v-card-title class="justify-space-between">
            <div>
              <div>Clinic details</div>
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
                  <v-text-field v-model="clinicData.name" label="Name" required dense :error-messages="errors" :success="valid" focus name="name"/>
                </ValidationProvider>
              </div>
              <div class="col-12">
                <ValidationProvider rules="required" name="PhoneNumber" v-slot="{ errors, valid }">
                  <v-text-field v-model="clinicData.phoneNumber" label="PhoneNumber" required dense :error-messages="errors" :success="valid" focus name="phoneNumber"/>
                </ValidationProvider>
              </div>
              <v-divider></v-divider>
              <div class="col-12">Address</div>
              <div class="col-12">
                <ValidationProvider rules="required" name="City" v-slot="{ errors, valid }">
                  <v-text-field v-model="clinicData.address.city" label="City" required dense :error-messages="errors" :success="valid" focus name="city"/>
                </ValidationProvider>
              </div>
              <div class="col-12">
                <ValidationProvider rules="required" name="County" v-slot="{ errors, valid }">
                  <v-text-field v-model="clinicData.address.county" label="County" required dense :error-messages="errors" :success="valid" focus name="county"/>
                </ValidationProvider>
              </div>
              <div class="col-12">
                <ValidationProvider rules="required" name="Street" v-slot="{ errors, valid }">
                  <v-text-field v-model="clinicData.address.street" label="Street" required dense :error-messages="errors" :success="valid" focus name="street"/>
                </ValidationProvider>
              </div>
              <div class="col-12">
                <v-text-field v-model="clinicData.address.number" label="Number" required dense focus name="number"/>
              </div>
              <div class="col-12">
                <v-textarea rows="3" v-model="clinicData.address.details" label="Details" required dense focus name="details"/>
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
    clinic: Object
  },
  computed: {
    visible: {
      get: function() {
        return this.isVisible
      },
      set: function(v) {
        this.$emit('closed')
      }
    }
  },
  data() {
    return {
      fullscreen: false,
      loading: false,
      clinicData: this.newClinicData(),
      availableInvestigations: []
    }
  },
  methods: {
    saveClinic() {
      this.loading = true
      backend.$saveClinic(this.clinicData).then(r => {
        this.$emit('save')
        this.showSuccessNotification('Data saved successfully')
        this.visible = false
      }).catch(e => { this.showErrorNotification('An error occured, try again later') })
        .then(() => { this.loading = false })
    },
    newClinicData() {
      return {
        id: null,
        name: '',
        phoneNumber: '',
        address: {
          city: '',
          county: '',
          street: '',
          number: '',
          details: ''
        }
      }
    }
  },
  watch: {
    isVisible: function(newValue, oldValue) {
      if (newValue) {
        if (this.$refs.observer) {
          this.$refs.observer.reset()
        }
        if (this.clinic !== null) {
          this.clinicData = this.clone(this.clinic)
        } else {
          this.clinicData = this.newClinicData()
        }
      }
    }
  }
}
</script>
