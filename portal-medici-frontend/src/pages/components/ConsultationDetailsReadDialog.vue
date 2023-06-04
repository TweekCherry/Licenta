<template>
  <v-dialog v-model="visible" scrollable width="400" :fullscreen="fullscreen">
    <v-card :loading="loading">
      <v-card-title class="justify-space-between">
        <div>
          <div>Consultation details</div>
        </div>
        <div>
          <v-btn icon small @click="visible = false">
            <v-icon>mdi-close</v-icon>
          </v-btn>
        </div>
      </v-card-title>
      <v-divider class="mx-4 pb-5"></v-divider>
      <v-card-text class="pt-4">
        <div class="row">
          <div class="col-12">
            <v-text-field :value="medicName" :label="consultationData.appointment.medicData.title" dense readonly hide-details/>
          </div>
          <div class="col-12">
            <v-text-field :value="patientName" label="Patient" dense readonly hide-details/>
          </div>
          <div class="col-12">
            <v-text-field :value="date" label="Date" dense readonly hide-details/>
          </div>
          <div class="col-12">
            <v-textarea rows="4" :value="consultationData.appointment.investigationData.name" label="Investigation" dense readonly hide-details />
          </div>
          <div class="col-12">
            <v-textarea rows="4" :value="consultationData.diagnostic" label="Diagnostic" dense readonly hide-details/>
          </div>
        </div>
        <div class="row">
          <div class="col-12 d-flex align-center text-body-1">Prescription</div>
        </div>
        <v-divider></v-divider>
        <v-simple-table dense>
          <template v-slot:default>
            <thead>
              <tr>
                <th class="text-left">
                  Name
                </th>
                <th class="text-right">
                  Doze
                </th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="(drug, index) in consultationData.prescription.drugs" :key="`drug-${index}`">
                <td>{{ drug.name }}</td>
                <td class="text-right">{{ drug.doze }}</td>
              </tr>
              <tr v-if="consultationData.prescription.drugs.length === 0">
                <td colspan="2" class="text-center">No prescription available</td>
              </tr>
            </tbody>
          </template>
        </v-simple-table>
        <v-divider class="mx-4"></v-divider>
      </v-card-text>
    </v-card>
  </v-dialog>
</template>
<script>
import { DateTime } from 'luxon'
export default {
  name: 'ConsultationDetailsReadDialog',
  props: {
    isVisible: Boolean,
    consultation: Object
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
    medicName() {
      return `${this.consultationData.appointment.medicData.profileData.firstName} ${this.consultationData.appointment.medicData.profileData.lastName}`
    },
    patientName() {
      return `${this.consultationData.appointment.userData.firstName} ${this.consultationData.appointment.userData.lastName}`
    },
    date() {
      return DateTime.fromISO(this.consultationData.appointment.timestamp).toFormat('dd-LL-yyyy HH:mm')
    }
  },
  data() {
    return {
      fullscreen: false,
      loading: false,
      consultationData: this.newConsultationData()
    }
  },
  methods: {
    newConsultationData() {
      return {
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
          investigationData: {
            name: ''
          },
          userData: {
            firstName: '',
            lastName: ''
          },
          medicData: {
            profileData: {
              firstName: '',
              lastName: '',
              title: '',
              grade: ''
            }
          },
          prescription: {
            drugs: []
          }
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
        if (this.consultation !== null) {
          this.consultationData = this.clone(this.consultation)
        }
      }
    }
  }
}
</script>
