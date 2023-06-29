<template>
  <v-card :loading="loading">
    <v-card-title class="d-flex justify-space-between">
      <div>Subscriptions</div>
    </v-card-title>
    <v-card-text>
      <div class="row" v-if="subscriptions.length === 0">
        <div class="col-12">
          No subbscriptions available
        </div>
      </div>
      <div class="row">
        <div class="col-12 col-md-6" v-for="subscription in subscriptionsData" :key="subscription.id">
          <v-card outlined class="mx-auto">
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
                      <div v-if="subscription.favorite">Expires at: {{formatExpirationDate(subscription)}}</div>
                    </div>
                  </div>
                  <div v-if="subscription.favorite">
                    <v-icon color="warning">mdi-star</v-icon>
                  </div>
                  <v-btn color="primary" outlined rounded text @click="subscribe(subscription)" v-if="userHasNoSubscription">Subscribe</v-btn>
                </div>
              </v-list-item-content>
            </v-list-item>
            <v-card-text>
              <div>{{ subscription.description }}</div>
              <v-expansion-panels flat>
                <v-expansion-panel>
                  <v-expansion-panel-header>
                    Available investigations
                  </v-expansion-panel-header>
                  <v-expansion-panel-content>
                    <SubscriptionBenefitsTable :subscription="subscription" enableBooking @book="bookInvestigation"/>
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
import SubscriptionBenefitsTable from '@/pages/components/SubscriptionBenefitsTable.vue'
export default {
  name: 'Subscriptions',
  components: {
    SubscriptionBenefitsTable
  },
  data() {
    return {
      subscriptions: [],
      loading: false
    }
  },
  computed: {
    userHasNoSubscription() {
      return this.$store.state.profile.subscription === null
    },
    subscriptionsData() {
      if (!this.userHasNoSubscription) {
        const subscriptions = this.subscriptions.filter(s => s.id !== this.$store.state.profile.subscription.id)
        subscriptions.push({
          ...this.$store.state.profile.subscription,
          favorite: true,
          expirationDate: this.$store.state.profile.subscriptionExpirationDate
        })
        return subscriptions
      }
      return this.subscriptions
    }
  },
  mounted() {
    this.loadSubscriptions()
  },
  methods: {
    loadSubscriptions() {
      this.loading = true
      backend.$findSubscriptions().then(r => {
        this.subscriptions = r.data
      }).catch(e => { this.showErrorNotification('An error occured, try again later') })
        .then(() => { this.loading = false })
    },
    subscribe(subscription) {
      const availableSubscriptionPeriods = []
      availableSubscriptionPeriods.push(this.createSubscriptionPeriod(1, subscription.price))
      availableSubscriptionPeriods.push(this.createSubscriptionPeriod(3, subscription.price))
      availableSubscriptionPeriods.push(this.createSubscriptionPeriod(6, subscription.price))
      availableSubscriptionPeriods.push(this.createSubscriptionPeriod(12, subscription.price))
      this.showSelectPromptDialog('Do you want to purchase this subscription', `${subscription.name} subscription period`, 'required', availableSubscriptionPeriods, 'name').then(period => {
        this.loading = true
        backend.$registerSubscription(subscription.id, period.value).then(r => {
          this.showSuccessNotification('Subscription purchased successfully')
          this.$store.commit('userProfile', r.data)
        }).catch(e => { this.showErrorNotification('An error occured, try again later') })
          .then(() => { this.loading = false })
      })
    },
    createSubscriptionPeriod(months, price) {
      return {
        name: `${months} Month (${(months * price).toFixed(2)} Ron)`,
        value: months
      }
    },
    formatExpirationDate(subscription) {
      if (subscription.expirationDate !== undefined) {
        return DateTime.fromISO(subscription.expirationDate).toFormat('dd-LL-yyyy')
      }
      return ''
    },
    computePrice(price, discount) {
      if (discount > 0) {
        return price - (price * discount / 100)
      }
      return price
    },
    bookInvestigation(investigation) {
      backend.$findClinicsByInvestigation(investigation.id).then(r => {
        this.showSelectPromptDialog('Choose your clinic', 'Clinic', 'required', r.data, 'name', 'id').then(result => {
          const appointmentData = {
            id: null,
            user: null,
            clinic: result.id,
            medic: null,
            investigation: investigation.id,
            timestamp: this.getNewAppointmentTimestamp(),
            status: 'SCHEDULED',
            investigationData: investigation,
            clinicData: result
          }
          this.$router.push({
            name: 'Appointments',
            params: {
              appointmentData: appointmentData
            }
          })
        })
      })
    }
  }
}
</script>
