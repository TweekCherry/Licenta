<template>
  <v-card :loading="loading">
    <ValidationObserver ref="observer" v-slot="{ handleSubmit }" tag="div" class="fill-height">
      <v-form @submit.prevent="handleSubmit(finishConsultation)" class="d-flex fill-height flex-column">
        <v-card-title class="justify-space-between">
          <div>
            <div>Consultation details</div>
          </div>
          <div v-if="closeable">
            <v-btn icon small @click="$emit('closed')">
              <v-icon>mdi-close</v-icon>
            </v-btn>
          </div>
        </v-card-title>
        <v-divider class="mx-4 pb-5"></v-divider>
        <v-card-text class="fill-height">
          <div class="row">
            <div class="col-12">
              <div class="text--secondary">Patient data</div>
              <v-divider class="pb-3 mt-3"></v-divider>
            </div>
            <div class="col-12 col-md-6">
              <v-text-field :value="consultationData.appointment.userData.firstName" label="First name" dense readonly disabled/>
            </div>
            <div class="col-12 col-md-6">
              <v-text-field :value="consultationData.appointment.userData.lastName" label="Last name" dense readonly disabled/>
            </div>
            <div class="col-12 col-md-6">
              <v-text-field :value="consultationData.appointment.userData.age" label="Birth year" dense readonly disabled/>
            </div>
            <div class="col-12 col-md-6">
              <v-text-field :value="consultationData.appointment.userData.gender" label="Gender" dense readonly disabled/>
            </div>
          </div>
          <div class="row">
            <div class="col-12">
              <v-textarea rows="4" :value="consultationData.appointment.investigationData.name" label="Investigation" dense readonly hide-details />
            </div>
          </div>
          <div class="row">
            <div class="col-12">
              <ValidationProvider rules="required" name="Diagnostic" v-slot="{ errors, valid }">
                <v-textarea v-model="consultationData.diagnostic" label="Diagnostic" required dense :error-messages="errors" :success="valid" focus name="diagnostic"/>
              </ValidationProvider>
            </div>
          </div>
          <v-divider class="mx-4 pb-5"></v-divider>
          <div class="row">
            <div class="col-12 d-flex justify-space-between align-center">
              <span>Prescription</span>
              <v-btn icon color="primary" @click="addDrug">
                <v-icon>mdi-plus</v-icon>
              </v-btn>
            </div>
          </div>
          <div class="row" v-for="(drug, index) in consultationData.prescription.drugs" :key="`drug-${index}`">
            <div class="col-12 col-md-5">
              <ValidationProvider rules="required" :name="`Drug-name-${index}`" v-slot="{ errors, valid }">
                <v-text-field v-model="drug.name" label="Drug name" required dense :error-messages="errors" :success="valid" focus name="name"/>
              </ValidationProvider>
            </div>
            <div class="col-12 col-md-5">
              <ValidationProvider rules="required" :name="`Drug-doze-${index}`" v-slot="{ errors, valid }">
                <v-text-field v-model="drug.doze" label="Drug doze" required dense :error-messages="errors" :success="valid" focus name="doze"/>
              </ValidationProvider>
            </div>
            <div class="col-12 col-md-2">
              <v-btn icon color="error" @click="removeDrug(index)">
                <v-icon>mdi-trash-can-outline</v-icon>
              </v-btn>
            </div>
            <v-divider class="mx-4 pb-5"></v-divider>
          </div>
        </v-card-text>
        <v-divider class="mx-4"></v-divider>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn plain color="red" @click="visible = false" v-if="closeable">Cancel</v-btn>
          <v-btn plain type="submit" color="green">Save</v-btn>
          <v-spacer></v-spacer>
        </v-card-actions>
      </v-form>
    </ValidationObserver>
  </v-card>
</template>
<script>
import backend from '@/plugins/backend'
import { ValidationObserver, ValidationProvider } from 'vee-validate'
export default {
  name: 'ConsultationDetails',
  components: {
    ValidationObserver, ValidationProvider
  },
  props: {
    consultation: {
      type: Object,
      default: () => null
    },
    closeable: {
      type: Boolean,
      default: true
    }
  },
  data() {
    return {
      loading: false,
      consultationData: {
        id: null,
        diagnostic: '',
        prescription: {
          drugs: []
        },
        appointment: {
          id: null,
          user: null,
          timestamp: null,
          status: null,
          userData: {
            firstName: '',
            lastName: '',
            age: null,
            gender: null
          },
          investigationData: {
            name: null
          }
        }
      }
    }
  },
  mounted() {
    if (this.consultation !== null) {
      this.consultationData = this.clone(this.consultation)
    }
  },
  methods: {
    finishConsultation() {
      this.loading = true
      backend.$finishConsultation(this.consultationData).then(r => {
        this.$emit('save')
        this.$store.commit('activeConsultation', null)
        this.showSuccessNotification('Data saved successfully')
      }).catch(e => { this.showErrorNotification('An error occured, try again later') })
        .then(() => { this.loading = false })
    },
    addDrug() {
      this.consultationData.prescription.drugs.push({
        name: '',
        doze: ''
      })
    },
    removeDrug(index) {
      this.consultationData.prescription.drugs.splice(index, 1)
    }
  },
  watch: {
    consultation: function(newValue) {
      if (newValue !== null) {
        this.consultationData = this.clone(newValue)
      }
    }
  }
}
</script>
