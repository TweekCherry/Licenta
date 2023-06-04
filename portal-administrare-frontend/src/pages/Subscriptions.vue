<template>
  <v-card :loading="loading">
    <v-card-title class="d-flex justify-space-between">
      <div>Subscriptions</div>
      <v-btn icon small color="primary" @click="toggleSubscriptionDetailsDialog">
        <v-icon>mdi-plus</v-icon>
      </v-btn>
    </v-card-title>
    <v-card-text>
      <div class="row" v-if="subscriptions.length === 0">
        <div class="col-12">
          No subbscriptions available
        </div>
      </div>
      <div class="row">
        <div class="col-12 col-md-6" v-for="subscription in subscriptions" :key="subscription.id">
          <v-card outlined class="mx-auto">
            <v-list-item two-line>
              <v-list-item-avatar tile size="80" color="grey">
                <img :src="subscription.image"/>
              </v-list-item-avatar>
              <v-list-item-content>
                <v-list-item-title class="text-h5 mb-1">{{ subscription.name }}</v-list-item-title>
                <div class="text-overline mb-4">{{ subscription.price }} ron</div>
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
                    <SubscriptionBenefitsTable :subscription="subscription"/>
                  </v-expansion-panel-content>
                </v-expansion-panel>
              </v-expansion-panels>
            </v-card-text>
            <v-card-actions>
              <v-btn outlined rounded text @click="editSubscription(subscription)">Edit</v-btn>
              <v-btn color="error" outlined rounded text @click="deleteSubscription(subscription.id)">Remove</v-btn>
            </v-card-actions>
          </v-card>
        </div>
      </div>
    </v-card-text>
    <SubscriptionDetailsDialog :isVisible="showSubscriptionDetailsDialog" :subscription="subscription" @save="loadSubscriptions" @closed="toggleSubscriptionDetailsDialog"/>
  </v-card>
</template>
<script>
import backend from '@/plugins/backend'
import SubscriptionBenefitsTable from '@/pages/components/SubscriptionBenefitsTable.vue'
import SubscriptionDetailsDialog from '@/pages/components/SubscriptionDetailsDialog.vue'
export default {
  name: 'Subscriptions',
  components: {
    SubscriptionDetailsDialog, SubscriptionBenefitsTable
  },
  data() {
    return {
      subscriptions: [],
      loading: false,
      subscription: null,
      showSubscriptionDetailsDialog: false
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
    deleteSubscription(id) {
      this.showConfirmationDialog('Do you want to remove this subscription').then(() => {
        this.loading = true
        backend.$removeSubscription(id).then(r => {
          this.showSuccessNotification('Subscription removed successfully')
          this.loadSubscriptions()
        }).catch(e => { this.showErrorNotification('An error occured, try again later') })
          .then(() => { this.loading = false })
      })
    },
    editSubscription(subscription) {
      this.subscription = subscription
      this.showSubscriptionDetailsDialog = true
    },
    toggleSubscriptionDetailsDialog() {
      if (this.showSubscriptionDetailsDialog) {
        this.subscription = null
      }
      this.showSubscriptionDetailsDialog = !this.showSubscriptionDetailsDialog
    }
  }
}
</script>
