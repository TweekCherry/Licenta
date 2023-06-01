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
                              <div>{{computePrice(benefit.investigationData.price, benefit.discount)}} Ron</div>
                            </td>
                          </tr>
                        </tbody>
                      </template>
                    </v-simple-table>
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
import SubscriptionDetailsDialog from '@/pages/components/SubscriptionDetailsDialog.vue'
export default {
  name: 'Subscriptions',
  components: {
    SubscriptionDetailsDialog
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
      }).catch(e => { alert('An error occured, try again later') })
        .then(() => { this.loading = false })
    },
    deleteSubscription(id) {
      this.loading = true
      backend.$removeSubscription(id).then(r => {
        this.loadSubscriptions()
      }).catch(e => { alert('An error occured, try again later') })
        .then(() => { this.loading = false })
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
    },
    computePrice(price, discount) {
      if (discount > 0) {
        return price - (price * discount / 100)
      }
      return price
    }
  }
}
</script>
