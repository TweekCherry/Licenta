<template>
  <v-dialog v-model="visible" scrollable width="800" persistent>
    <ValidationObserver ref="observer" v-slot="{ handleSubmit }">
      <v-form @submit.prevent="handleSubmit(saveAppointment)" class="d-flex flex-column">
        <v-card :loading="loading">
          <v-card-title class="justify-space-between">
            <div>
              <div>Appointment details</div>
            </div>
            <div>
              <v-btn icon small @click="visible = false">
                <v-icon>mdi-close</v-icon>
              </v-btn>
            </div>
          </v-card-title>
          <v-divider class="mx-4 pb-5"></v-divider>
          <v-card-text>
            <div class="row">
              <div class="col-12">
                <ValidationProvider rules="required" name="Department" v-slot="{ errors, valid }">
                  <v-autocomplete
                    v-model="department" :items="avaialbleDepartments" dense label="Department" :error-messages="errors" :success="valid" item-text="name" item-value="name" @change="onDepartmentChange">
                  </v-autocomplete>
                </ValidationProvider>
              </div>
              <div class="col-12" v-if="department !== null">
                <ValidationProvider rules="required" name="Clinic" v-slot="{ errors, valid }">
                  <v-autocomplete
                    v-model="appointmentData.clinic"
                    :items="availableClinics" dense label="Clinic"
                    :error-messages="errors"
                    :success="valid" item-text="name" item-value="id"
                    @change="onClinicChange">
                  </v-autocomplete>
                </ValidationProvider>
              </div>
              <div class="col-12" v-if="appointmentData.clinic !== null">
                <ValidationProvider rules="required" name="Clinics" v-slot="{ errors, valid }">
                  <v-autocomplete v-model="appointmentData.medic" :items="filteredMedics" dense label="Medic" :error-messages="errors" :success="valid" item-text="profileData.firstName" item-value="id" @change="onMedicChange">
                    <template v-slot:[`item`]="{ item }" >
                      <div class="d-flex align-center py-2">
                        <img :src="item.image" width="64" height="64"/>
                        <div class="pl-2">
                          <div>{{item.profileData.firstName}} {{item.profileData.lastName}}</div>
                          <div>{{item.grade}}</div>
                        </div>
                      </div>
                    </template>
                    <template v-slot:[`selection`]="{ item }" >
                      <div class="d-flex align-center py-2">
                        <img :src="item.image" width="64" height="64"/>
                        <div class="pl-2">
                          <div>{{item.profileData.firstName}} {{item.profileData.lastName}}</div>
                          <div>{{item.grade}}</div>
                        </div>
                      </div>
                    </template>
                  </v-autocomplete>
                </ValidationProvider>
              </div>
              <div class="col-12" v-if="appointmentData.clinic !== null">
                <ValidationProvider rules="required" name="Investigation" v-slot="{ errors, valid }">
                  <v-autocomplete v-model="appointmentData.investigation" :items="filteredInvestigations" dense label="Investigation" :error-messages="errors" :success="valid" item-text="name" item-value="id">
                    <template v-slot:[`item`]="{ item }" >
                      <div class="d-flex justify-space-between align-center py-2" style="width: 100%">
                        <div>{{item.name}}</div>
                        <div class="font-weight-bold">
                          <span class="text-body-1 font-weight-bold">{{computeInvestigationPrice(item)}} Ron</span>
                          <span class="text-decoration-line-through red--text font-weight-regular text-body-2" v-if="isReduced(item)"> ({{item.price}} Ron)</span>
                        </div>
                      </div>
                    </template>
                    <template v-slot:[`selection`]="{ item }" >
                      <div class="d-flex justify-space-between align-center py-2" style="width: 100%">
                        <div>{{item.name}}</div>
                        <div>
                          <div class="text-body-1 font-weight-bold">{{computeInvestigationPrice(item)}} Ron</div>
                          <div class="text-decoration-line-through red--text font-weight-regular text-body-2" v-if="isReduced(item)">({{item.price}} Ron)</div>
                        </div>
                      </div>
                    </template>
                  </v-autocomplete>
                </ValidationProvider>
              </div>
            </div>
            <div class="row" v-if="appointmentData.medic !== null && appointmentData.investigation !== null">
              <div class="col-12">
                <v-date-picker v-model="date" :min="minDate" :allowed-dates="itsValidDate" full-width class="mt-4" @change="hour = null"></v-date-picker>
              </div>
              <div class="col-12">
                <ValidationProvider rules="required" name="Hour" v-slot="{ errors }">
                  <v-radio-group v-model="hour" row :error-messages="errors">
                    <v-radio color="primary" class="py-2" v-for="(hour, index) in availableHours" :key="`hour-${index}`" :value="hour.value" :label="hour.value" :disabled="hour.disabled"></v-radio>
                  </v-radio-group>
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
import { DateTime as d } from 'luxon'
const DateTime = d
export default {
  name: 'AppointmentDetails',
  components: {
    ValidationObserver, ValidationProvider
  },
  props: {
    isVisible: Boolean,
    appointment: Object,
    appointments: Array
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
    filteredMedics() {
      if (this.department !== null) {
        return this.availableMedics.filter(m => m.departments.indexOf(this.department) >= 0)
      }
      return this.availableMedics
    },
    filteredInvestigations() {
      if (this.department !== null) {
        return this.availableInvestigations.filter(i => i.department === this.department)
      }
      return this.availableInvestigations
    },
    bookedTimestamps() { // merge appointment dates from current user with appointment dates from selected medic(those are booked, so not available for new appointments)
      const timestmaps = new Map()
      this.currentAppointmentTimestamps.forEach((value, key) => {
        let hours = timestmaps.get(key)
        if (hours === undefined) {
          hours = new Set()
          timestmaps.set(key, hours)
        }
        value.forEach(v => hours.add(v))
      })
      this.currentMedicTimestamps.forEach((value, key) => {
        let hours = timestmaps.get(key)
        if (hours === undefined) {
          hours = new Set()
          timestmaps.set(key, hours)
        }
        value.forEach(v => hours.add(v))
      })
      return timestmaps
    },
    bookedHours() { // store the hours that are already booked
      const bookedHours = this.bookedTimestamps.get(this.date)
      if (bookedHours !== undefined) {
        return bookedHours
      }
      return new Set()
    },
    availableHours() {
      const hours = []
      // const currentHour = this.now.hour
      this.hours.forEach(hour => {
        const disabled = this.bookedHours.has(hour.value)
        // if (!disabled) {
        //   disabled = hour.data <= currentHour
        // }
        hours.push({ ...hour, disabled: disabled })
      })
      return hours
    },
    selectedTimestamp() {
      return `${this.date}T${this.hour}:00`
    }
  },
  data() {
    return {
      department: null,
      hour: null,
      now: DateTime.now(),
      date: DateTime.now().toISODate(),
      minDate: DateTime.now().toISODate(),
      loading: false,
      appointmentData: this.newAppointmentData(),
      availableClinics: [],
      availableMedics: [],
      availableInvestigations: [],
      avaialbleDepartments: [],
      currentAppointmentTimestamps: new Map(), // appointment dates from this user
      currentMedicTimestamps: new Map(), // appointment dates from the selected medic
      hours: [
        { value: '08:00', disabled: false, data: 8 },
        { value: '09:00', disabled: false, data: 9 },
        { value: '10:00', disabled: false, data: 10 },
        { value: '11:00', disabled: false, data: 11 },
        { value: '12:00', disabled: false, data: 12 },
        { value: '13:00', disabled: false, data: 13 },
        { value: '14:00', disabled: false, data: 14 },
        { value: '15:00', disabled: false, data: 15 },
        { value: '16:00', disabled: false, data: 16 },
        { value: '17:00', disabled: false, data: 17 },
        { value: '18:00', disabled: false, data: 18 },
        { value: '19:00', disabled: false, data: 19 }
      ]
    }
  },
  methods: {
    saveAppointment() {
      if (this.bookedHours.has(this.hour)) {
        this.showErrorNotification('Please choose a valid hour')
        return
      }
      this.loading = true
      this.appointmentData.timestamp = DateTime.fromISO(this.selectedTimestamp).toUTC().toISO({ includeOffset: false }) // save as utc
      this.appointmentData.investigationData = null
      this.appointmentData.medicData = null
      this.appointmentData.clinicData = null
      this.appointmentData.userData = null
      backend.$saveAppointment(this.appointmentData).then(r => {
        this.$emit('save')
        this.showSuccessNotification('Data saved successfully')
        this.visible = false
      }).catch(e => { this.showErrorNotification('An error occured, try again later') })
        .then(() => { this.loading = false })
    },
    loadAvailableDepartments() {
      return backend.$findDepartments().then(r => {
        this.avaialbleDepartments = r.data
      }).catch(e => { this.showErrorNotification('An error occured, try again later') })
    },
    loadAvailableClinics() {
      return backend.$findClinics().then(r => {
        this.availableClinics = r.data
      }).catch(e => { this.showErrorNotification('An error occured, try again later') })
    },
    loadAvailableMedics() {
      if (this.appointmentData.clinic !== null) {
        return backend.$findMedicsByClinic(this.appointmentData.clinic).then(r => {
          this.availableMedics = r.data
        }).catch(e => { this.showErrorNotification('An error occured, try again later') })
      }
      return Promise.resolve()
    },
    loadAvailableInvestigations() {
      if (this.appointmentData.clinic !== null) {
        return backend.$findInvestigationsByClinic(this.appointmentData.clinic).then(r => {
          this.availableInvestigations = r.data
        }).catch(e => { this.showErrorNotification('An error occured, try again later') })
      }
      return Promise.resolve()
    },
    onDepartmentChange() {
      this.appointmentData.medic = null
      this.appointmentData.investigation = null
      this.appointmentData.clinic = null
    },
    onClinicChange() {
      this.appointmentData.investigation = null
      this.appointmentData.medic = null
      this.loadData()
    },
    onMedicChange() {
      this.checkForBookedDates()
      this.loadAvailableInvestigations()
    },
    loadData() {
      this.loadAvailableMedics()
      this.loadAvailableInvestigations()
    },
    checkForBookedDates() {
      this.loading = true
      if (this.appointmentData.clinic !== null && this.appointmentData.medic !== null) {
        backend.$findBookedAppointmentDates(this.appointmentData.clinic, this.appointmentData.medic, this.appointmentData.id).then(r => {
          const bookedTimestampsMap = new Map()
          r.data.forEach(d => {
            const timestamp = DateTime.fromISO(d)
            const date = timestamp.toISODate() // get the date part of this timestmap
            let times = bookedTimestampsMap.get(date) // get the set with the booked hours based on the date
            if (times === undefined) { // if we don't have any, then create a new set with booked hours
              times = new Set()
              bookedTimestampsMap.set(date, times)
            }
            times.add(timestamp.toFormat('HH:mm')) // push the time part to the set
          })
          this.currentMedicTimestamps = bookedTimestampsMap
        }).catch(e => { this.showErrorNotification('An error occured, try again later') })
          .then(() => { this.loading = false })
      }
    },
    itsValidDate(timestamp) {
      console.log(timestamp)
      const bookedHours = this.bookedTimestamps.get(timestamp)
      if (bookedHours !== undefined) {
        return bookedHours.size !== this.hours.length
      }
      return true
    },
    newAppointmentData() {
      return {
        id: null,
        user: null,
        clinic: null,
        medic: null,
        investigation: null,
        timestamp: this.getNewAppointmentTimestamp(),
        status: 'SCHEDULED'
      }
    }
  },
  watch: {
    isVisible: function(newValue, oldValue) {
      if (newValue) {
        if (this.$refs.observer) {
          this.$refs.observer.reset()
        }
        this.minDate = DateTime.now().toISODate()
        this.department = null
        this.appointmentData = this.newAppointmentData()
        if (this.appointment !== null) {
          this.appointmentData = this.clone(this.appointment)
          this.department = this.appointment.investigationData.department
          this.loadData()
          this.checkForBookedDates()
        }
        const timestamp = DateTime.fromISO(this.appointmentData.timestamp).plus({ minutes: DateTime.local().offset })
        this.minDate = timestamp.toISODate()
        this.date = timestamp.toISODate()
        this.hour = timestamp.toFormat('HH:mm')
        this.loading = true
        this.currentAppointmentTimestamps = new Map()
        this.appointments
          .filter(a => a.data.status === 'SCHEDULED')
          .filter(a => a.data.id !== this.appointmentData.id)
          .map(a => DateTime.fromISO(a.data.timestamp).plus({ minutes: DateTime.local().offset }))
          .forEach(timestamp => {
            // const timestamp = DateTime.fromISO(d)
            const date = timestamp.toISODate() // get the date part of this timestmap
            let times = this.currentAppointmentTimestamps.get(date) // get the set with the booked hours based on the date
            if (times === undefined) { // if we don't have any, then create a new set with booked hours
              times = new Set()
              this.currentAppointmentTimestamps.set(date, times)
            }
            times.add(timestamp.toFormat('HH:mm')) // push the time part to the set
          })
        Promise.all([
          this.loadAvailableClinics(),
          this.loadAvailableDepartments()
        ]).then(() => { this.loading = false })
      }
    }
  }
}
</script>
