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
        <v-menu bottom right>
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
        :interval-count="9"
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
            <v-toolbar-title>{{selectedItem.investigationData.department}}</v-toolbar-title>
            <v-spacer></v-spacer>
          </v-toolbar>
          <v-card-text>
            <div class="font-weight-black text-h6">{{selectedItem.medicData.title}} {{selectedItem.medicData.profileData.firstName}} {{selectedItem.medicData.profileData.lastName}}</div>
            <div class="text-body-2">{{selectedItem.medicData.grade}}</div>
            <div class="font-weight-black text-body-2">{{ formatEventTime(selectedEvent.start) }} - {{ formatEventTime(selectedEvent.end) }}</div>
          </v-card-text>
          <v-card-actions>
            <v-btn text color="blue" @click="editItem(selectedItem)">Edit appointment</v-btn>
            <v-btn text color="error" @click="deleteItem(selectedItem.id)">Cancel appointment</v-btn>
          </v-card-actions>
        </v-card>
      </v-menu>
    </v-card-text>
    <AppointmentDetails :isVisible="showDetailsDialog" :appointment="selectedItem" :appointments="appointmentTimestmaps" @save="loadItems" @closed="toggleDetailsDialog" />
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
  computed: {
    title() {
      if (this.date !== null && this.date !== '') {
        const date = DateTime.fromISO(this.date)
        return date.toFormat('LLLL yyyy')
      }
      return DateTime.now().toFormat('LLLL yyyy')
    },
    color() {
      let color = 'error'
      if (this.selectedItem !== null) {
        if (this.selectedItem.status === 'SCHEDULED') {
          color = 'success'
        } else if (this.selectedItem.status === 'FINISHED') {
          color = 'blue'
        }
      }
      return color
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
      selectedItem: {
        investigationData: {},
        medicData: {
          profileData: {}
        }
      },
      selectedElement: null,
      selectedEvent: {},
      showDetailsDialog: false,
      showOverviewDialog: false,
      items: [],
      appointmentTimestmaps: []
    }
  },
  mounted() {
    this.loadItems()
    this.$refs.calendar.checkChange()
  },
  methods: {
    loadItems() {
      this.loading = true
      backend.$findAppointments().then(r => {
        this.items = []
        this.appointmentTimestmaps = []
        r.data.forEach(item => {
          const timestamp = DateTime.fromISO(item.timestamp)
          this.appointmentTimestmaps.push(timestamp)
          let color = 'error'
          if (item.status === 'SCHEDULED') {
            color = 'success'
          } else if (item.status === 'FINISHED') {
            color = 'blue'
          }
          this.items.push({
            start: timestamp.toFormat('yyyy-LL-dd HH:mm'),
            end: timestamp.plus({ hours: 1 }).toFormat('yyyy-LL-dd HH:mm'),
            color: color,
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
        this.selectedItem = event.data
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
    // viewDay({ date }) {
    //   this.date = date
    //   this.type = 'day'
    // }
  }
}
</script>
