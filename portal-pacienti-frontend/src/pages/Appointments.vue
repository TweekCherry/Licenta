<template>
  <v-card :loading="loading">
    <v-card-title class="d-flex justify-space-between">
      <div>My appointments</div>
      <v-btn icon small color="primary" @click="toggleDetailsDialog">
        <v-icon>mdi-plus</v-icon>
      </v-btn>
    </v-card-title>
    <v-card-text>
      <v-toolbar flat>
        <v-btn outlined class="mr-4" color="primary" @click="setToday">Today</v-btn>
        <v-btn fab text small color="primary" @click="prev">
          <v-icon small>mdi-chevron-left</v-icon>
        </v-btn>
        <v-btn fab text small color="primary" @click="next">
          <v-icon small>mdi-chevron-right</v-icon>
        </v-btn>
        <v-toolbar-title>{{ title }}</v-toolbar-title>
        <v-spacer></v-spacer>
        <v-menu bottom right v-if="calendarType === null">
          <template v-slot:activator="{ on, attrs }">
            <v-btn outlined color="primary" v-bind="attrs" v-on="on">
              <span>{{ typeToLabel[type] }}</span>
              <v-icon right>mdi-menu-down</v-icon>
            </v-btn>
          </template>
          <v-list>
            <v-list-item @click="type = 'day'">
              <v-list-item-title>Day</v-list-item-title>
            </v-list-item>
            <v-list-item @click="type = 'week'">
              <v-list-item-title>Week</v-list-item-title>
            </v-list-item>
            <v-list-item @click="type = 'month'">
              <v-list-item-title>Month</v-list-item-title>
            </v-list-item>
          </v-list>
        </v-menu>
      </v-toolbar>
      <v-calendar
        ref="calendar"
        v-model="date"
        :first-interval="8"
        :interval-count="13"
        :interval-minutes="60"
        :interval-format="intervalFormat"
        :weekdays="[1, 2, 3, 4, 5, 6, 0]"
        :events="items"
        :type="type"
        event-overlap-mode="column"
        event-color="primary"
        @click:event="showAppointmentDetails">
        <template v-slot:event="{ event }">
          <div class="d-flex justify-space-between align-center px-2">
            <div>
                <div>{{ event.data.investigationData.department }}</div>
                <div>{{ formatEventTime(event.start) }} - {{ formatEventTime(event.end) }}</div>
            </div>
          </div>
        </template>
      </v-calendar>
      <!-- Show appointment overview below -->
      <v-menu v-model="showOverviewDialog" :close-on-content-click="false" :activator="selectedElement" offset-x>
        <v-card color="grey lighten-4" min-width="350px" flat>
          <v-toolbar :color="color" dark>
            <v-btn icon>
              <v-icon>mdi-hospital-box-outline</v-icon>
            </v-btn>
            <v-toolbar-title>{{selectedEvent.data.investigationData.department}}</v-toolbar-title>
            <v-spacer></v-spacer>
          </v-toolbar>
          <v-card-text>
            <div class="font-weight-black text-h6">{{selectedEvent.data.medicData.title}} {{selectedEvent.data.medicData.profileData.firstName}} {{selectedEvent.data.medicData.profileData.lastName}}</div>
            <div class="d-flex justify-space-between align-center">
              <div>
                <div class="text-body-2">{{selectedEvent.data.medicData.grade}}</div>
                <div class="font-weight-black text-body-2">{{ formatEventTime(selectedEvent.start) }} - {{ formatEventTime(selectedEvent.end) }}</div>
              </div>
              <div>
                <div class="text-body-2 font-weight-bold">{{computeInvestigationPrice(selectedEvent.data.investigationData)}} Ron</div>
                <div class="text-decoration-line-through red--text font-weight-regular text-body-2" v-if="isReduced(selectedEvent.data.investigationData)">{{selectedEvent.data.investigationData.price}} Ron</div>
              </div>
            </div>
          </v-card-text>
          <v-card-actions v-if="selectedEvent.data.status === 'SCHEDULED'">
            <v-btn text color="blue" @click="editItem(selectedEvent.data)">Edit appointment</v-btn>
            <v-btn text color="error" @click="deleteItem(selectedEvent.data.id)">Cancel appointment</v-btn>
          </v-card-actions>
          <div class="pl-4 pb-4" v-else>
            <v-chip :color="color">{{translateStatus(selectedEvent.data.status)}}</v-chip>
          </div>
        </v-card>
      </v-menu>
    </v-card-text>
    <AppointmentDetails :isVisible="showDetailsDialog" :appointment="selectedItem" :appointments="items" @save="loadItems" @closed="toggleDetailsDialog" />
  </v-card>
</template>
<script>
import backend from '@/plugins/backend'
import AppointmentDetails from '@/pages/components/AppointmentDetails.vue'
import { DateTime as d } from 'luxon'
const DateTime = d
export default {
  name: 'Appointments',
  components: {
    AppointmentDetails
  },
  props: {
    calendarType: {
      type: String,
      default: null
    },
    appointment: {
      type: Object,
      default: () => null
    },
    onlyScheduled: {
      type: Boolean,
      default: false
    }
  },
  computed: {
    title() {
      if (this.date !== null && this.date !== '') {
        const date = DateTime.fromISO(this.date)
        return date.toFormat('LLLL yyyy')
      }
      return DateTime.now().toFormat('LLLL yyyy')
    },
    color() {
      if (this.selectedEvent !== null) {
        return this.selectedEvent.color
      }
      return 'error'
    }
  },
  data() {
    return {
      type: 'week',
      typeToLabel: {
        month: 'Month',
        week: 'Week',
        day: 'Day'
      },
      date: null,
      loading: false,
      selectedItem: null,
      selectedElement: null,
      selectedEvent: {
        data: {
          investigationData: {},
          medicData: {
            profileData: {}
          }
        }
      },
      showDetailsDialog: false,
      showOverviewDialog: false,
      items: []
    }
  },
  mounted() {
    if (this.calendarType !== null) {
      this.type = this.calendarType
    }
    this.loadItems()
    this.$refs.calendar.checkChange()
    if (this.$route.params.appointmentData !== undefined) {
      this.editItem(this.$route.params.appointmentData)
    }
    this.$getEventStream('AppointmentCancelled').subscribe(event => {
      if (this.onlyScheduled) {
        this.items = this.items.filter(i => i.data.id !== event.appointment.id)
        if (this.selectedEvent !== null && this.selectedEvent.data.id === event.appointment.id) {
          this.showOverviewDialog = false
        }
        return
      }
      const item = this.items.find(i => i.data.id === event.appointment.id)
      if (item !== undefined) {
        item.color = this.getColor(event.appointment)
        item.status = event.appointment.status
      }
    })
    this.$getEventStream('AppointmentStarted').subscribe(event => {
      const item = this.items.find(i => i.data.id === event.appointment.id)
      if (item !== undefined) {
        item.color = this.getColor(event.appointment)
        item.data.status = event.appointment.status
      }
    })
    this.$getEventStream('AppointmentFinished').subscribe(event => {
      if (this.onlyScheduled) {
        this.items = this.items.filter(i => i.data.id !== event.appointment.id)
        if (this.selectedEvent !== null && this.selectedEvent.data.id === event.appointment.id) {
          this.showOverviewDialog = false
        }
        return
      }
      const item = this.items.find(i => i.data.id === event.appointment.id)
      if (item !== undefined) {
        item.color = this.getColor(event.appointment)
        item.data.status = event.appointment.status
      }
    })
  },
  methods: {
    loadItems() {
      this.loading = true
      backend.$findAppointments(this.onlyScheduled).then(r => {
        this.items = []
        r.data.forEach(item => {
          const timestamp = DateTime.fromISO(item.timestamp).plus({ minutes: DateTime.local().offset })
          this.items.push({
            start: timestamp.toFormat('yyyy-LL-dd HH:mm'),
            end: timestamp.plus({ hours: 1 }).toFormat('yyyy-LL-dd HH:mm'),
            color: this.getColor(item),
            data: item
          })
        })
      }).catch(e => this.showErrorNotification('An error occured, try again later'))
        .then(() => { this.loading = false })
    },
    deleteItem(id) {
      this.showOverviewDialog = false
      this.showConfirmationDialog('Do you want to cancel this appointment').then(() => {
        this.loading = true
        backend.$deleteAppointment(id).then(() => {
          this.showSuccessNotification('Appointment cancelled')
          this.loadItems()
        }).catch(e => this.showErrorNotification('An error occured, try again later'))
          .then(() => { this.loading = false })
      })
    },
    showAppointmentDetails({ nativeEvent, event }) {
      const open = () => {
        this.selectedEvent = event
        this.selectedElement = nativeEvent.target
        requestAnimationFrame(() => requestAnimationFrame(() => {
          this.showOverviewDialog = true
        }))
      }
      if (this.showOverviewDialog) { // in case we click on an event with an already opened overview
        this.showOverviewDialog = false // hide the current one
        requestAnimationFrame(() => requestAnimationFrame(() => open())) // wait for it to hide then show it again with new data
      } else {
        open()
      }
      nativeEvent.stopPropagation()
    },
    editItem(item) {
      this.selectedItem = item
      this.showDetailsDialog = true
      this.showOverviewDialog = false
    },
    toggleDetailsDialog() {
      if (this.showDetailsDialog) {
        this.selectedItem = null
      }
      this.showDetailsDialog = !this.showDetailsDialog
      this.$emit('closed')
    },
    getColor(item) {
      let color = 'error'
      if (item !== null) {
        if (item.status === 'SCHEDULED') {
          color = 'blue darken-1'
        } else if (item.status === 'FINISHED' || item.status === 'IN_PROGRESS') {
          color = 'success'
        } else if (item.status === 'NOT_PRESENTED') {
          color = 'warning'
        }
      }
      return color
    },
    translateStatus(status) {
      if (status === 'SCHEDULED') {
        return 'Scheduled'
      } else if (status === 'FINISHED') {
        return 'Finished'
      } else if (status === 'NOT_PRESENTED') {
        return 'Not presented'
      } else if (status === 'CANCELLED') {
        return 'Cancelled'
      } else if (status === 'IN_PROGRESS') {
        return 'In progress'
      }
    },
    intervalFormat(locale, getOptions) {
      return locale.time
    },
    formatEventTime(date) {
      return new Date(date).toLocaleTimeString('en-US', {
        hour: '2-digit',
        minute: '2-digit',
        hour12: false
      })
    },
    prev() {
      this.$refs.calendar.prev()
    },
    next() {
      this.$refs.calendar.next()
    },
    setToday() {
      this.date = ''
    }
  },
  watch: {
    appointment: function(newValue, oldValue) {
      if (newValue !== null) {
        this.editItem(newValue)
      }
    }
  }
}
</script>
