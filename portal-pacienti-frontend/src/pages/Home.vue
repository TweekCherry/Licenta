<template>
  <v-card :loading="loading">
    <v-card-title class="d-flex justify-space-between">
      <div>Home</div>
    </v-card-title>
    <v-divider class="mx-4"></v-divider>
    <v-card-text>
      <div class="row">
        <div class="col-12 col-md-6">
          <Appointments calendarType="day" onlyScheduled :appointment="appointmentData" @closed="appointmentData = null"/>
        </div>
        <div class="col-12 col-md-6" v-if="hasSubscription">
          <v-card outlined class="mx-auto">
            <v-card-title class="d-flex justify-space-between">
              <div>My Subscription</div>
            </v-card-title>
            <v-list-item three-line>
              <v-list-item-avatar tile size="80" color="grey">
                <img :src="subscription.image"/>
              </v-list-item-avatar>
              <v-list-item-content>
                <div class="d-flex justify-space-between">
                  <div>
                    <v-list-item-title class="text-h5 mb-1">{{ subscription.name }}</v-list-item-title>
                    <div class="text-overline">
                      <div>{{ subscription.price }} ron</div>
                      <div>Expires at: {{subscriptionExpirationDate}}</div>
                    </div>
                  </div>
                  <div>
                    <v-icon color="warning">mdi-star</v-icon>
                  </div>
                </div>
              </v-list-item-content>
            </v-list-item>
            <v-card-text>
              <div>{{ subscription.description }}</div>
              <v-expansion-panels flat v-model="expanded">
                <v-expansion-panel>
                  <v-expansion-panel-header>
                    Available investigations
                  </v-expansion-panel-header>
                  <v-expansion-panel-content>
                    <v-simple-table>
                      <template v-slot:default>
                        <thead>
                          <tr>
                            <th class="text-left">
                              Name
                            </th>
                            <th class="text-left">
                              Discount
                            </th>
                            <th class="text-left">
                              Price
                            </th>
                            <th class="text-left">
                            </th>
                          </tr>
                        </thead>
                        <tbody>
                          <tr v-for="benefit in subscription.benefits" :key="`${subscription.id}-${benefit.investigation}`">
                            <td>
                              <div class="body-1">{{benefit.investigationData.name}}</div>
                              <div class="caption">{{benefit.investigationData.type}}</div>
                              <div class="caption">{{benefit.investigationData.department}}</div>
                            </td>
                            <td>{{benefit.discount}}%</td>
                            <td>
                              <div class="text-decoration-line-through red--text">{{benefit.investigationData.price}} Ron</div>
                              <div>{{computeInvestigationPrice(benefit.investigationData)}} Ron</div>
                            </td>
                            <td>
                              <v-btn icon @click="bookInvestigation(benefit.investigationData)">
                                <v-icon>mdi-book-plus-outline</v-icon>
                              </v-btn>
                            </td>
                          </tr>
                        </tbody>
                      </template>
                    </v-simple-table>
                  </v-expansion-panel-content>
                </v-expansion-panel>
              </v-expansion-panels>
            </v-card-text>
          </v-card>
        </div>
      </div>
    </v-card-text>
  </v-card>
</template>
<script>
import backend from '@/plugins/backend'
import { DateTime } from 'luxon'
import Appointments from '@/pages/Appointments.vue'
export default {
  name: 'Home',
  components: {
    Appointments
  },
  data() {
    return {
      loading: false,
      expanded: 0,
      appointmentData: null
    }
  },
  computed: {
    hasSubscription() {
      return this.$store.state.profile.subscription !== null
    },
    subscription() {
      return this.$store.state.profile.subscription
    },
    subscriptionExpirationDate() {
      if (this.$store.state.profile.subscriptionExpirationDate !== null) {
        return DateTime.fromISO(this.$store.state.profile.subscriptionExpirationDate).toFormat('dd-LL-yyyy')
      }
      return ''
    }
  },
  mounted() {

  },
  methods: {
    bookInvestigation(investigation) {
      this.loading = true
      backend.$findClinicsByInvestigation(investigation.id).then(r => { // load clinics based on selected investigation
        return this.showSelectPromptDialog('Choose your clinic', 'Clinic', 'required', r.data, 'name') // let the user select the clinic
      }).then(result => {
        this.appointmentData = { // build the appointment data
          id: null,
          user: null,
          clinic: result.id,
          medic: null,
          investigation: investigation.id,
          timestamp: this.getNewAppointmentTimestamp(),
          status: 'SCHEDULED',
          investigationData: {
            department: investigation.department
          },
          clinicData: result
        }
      }).catch(e => { this.showErrorNotification('An error occured, try again later') })
        .then(() => { this.loading = false })
    }
  }
}
</script>
