<template>
  <v-dialog v-model="visible" :fullscreen="fullscreen" scrollable width="800" persistent>
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
                  <v-text-field v-model="investigationData.name" label="Name" required dense :error-messages="errors" :success="valid" focus name="name"/>
                </ValidationProvider>
              </div>
              <div class="col-12">
                <ValidationProvider rules="required" name="Price" v-slot="{ errors, valid }">
                  <v-text-field type="number" min="0" v-model="investigationData.price" label="Price" required dense :error-messages="errors" :success="valid" focus name="price"/>
                </ValidationProvider>
              </div>
              <div class="col-12">
                <ValidationProvider rules="required" name="Department" v-slot="{ errors, valid }">
                  <v-autocomplete
                    v-model="investigationData.department" :items="availableDepartments" dense label="Department" :error-messages="errors" :success="valid" item-text="name" item-value="name">
                  </v-autocomplete>
                </ValidationProvider>
              </div>
              <div class="col-12">
                <ValidationProvider rules="required" name="Type" v-slot="{ errors, valid }">
                  <v-autocomplete
                    v-model="investigationData.type" :items="availableInvestigationTypes" dense label="Type" :error-messages="errors" :success="valid" item-text="name" item-value="name">
                  </v-autocomplete>
                </ValidationProvider>
              </div>
              <div class="col-12">
                <ValidationProvider rules="required" name="Clinics" v-slot="{ errors, valid }">
                  <v-autocomplete
                    v-model="investigationData.clinics" multiple :items="availableClinics" dense label="Available in" :error-messages="errors" :success="valid" item-text="name" item-value="id">
                  </v-autocomplete>
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
    investigation: Object
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
      investigationData: this.newInvestigationData(),
      availableDepartments: [],
      availableClinics: [],
      availableInvestigationTypes: []
    }
  },
  methods: {
    saveClinic() {
      this.loading = true
      backend.$saveInvestigation(this.investigationData).then(r => {
        this.$emit('save')
        this.visible = false
      }).catch(e => { alert('An error occured, try again later') })
        .then(() => { this.loading = false })
    },
    loadAvailableClinics() {
      return backend.$findClinics().then(r => {
        this.availableClinics = r.data
      }).catch(e => { console.log(e) })
    },
    loadAvailableDepartments() {
      return backend.$findDepartments().then(r => {
        this.availableDepartments = r.data
      }).catch(e => { console.log(e) })
    },
    loadAvailableInvestigationTypes() {
      return backend.$findInvestigationTypes().then(r => {
        this.availableInvestigationTypes = r.data
      }).catch(e => { console.log(e) })
    },
    newInvestigationData() {
      return {
        id: null,
        name: '',
        price: 0,
        department: '',
        type: '',
        clinics: []
      }
    }
  },
  watch: {
    isVisible: function(newValue, oldValue) {
      if (newValue) {
        if (this.$refs.observer) {
          this.$refs.observer.reset()
        }
        if (this.investigation !== null) {
          this.investigationData = this.clone(this.investigation)
        } else {
          this.investigationData = this.newInvestigationData()
        }
        this.loading = true
        Promise.all([
          this.loadAvailableClinics(),
          this.loadAvailableDepartments(),
          this.loadAvailableInvestigationTypes()]
        ).then(() => { this.loading = false })
      }
    }
  }
}
</script>
