<template>
  <v-dialog v-model="visible" :fullscreen="fullscreen" scrollable width="800">
    <ValidationObserver ref="observer" v-slot="{ handleSubmit }">
      <v-form @submit.prevent="handleSubmit(saveSubscription)">
        <v-card :loading="loading">
          <v-card-title class="justify-space-between">
            <div>
              <div>Subscription details</div>
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
                  <v-text-field v-model="subscriptionData.name" label="Name" required dense :error-messages="errors" :success="valid" focus name="name"/>
                </ValidationProvider>
              </div>
              <div class="col-12">
                <ValidationProvider rules="required" name="Description" v-slot="{ errors, valid }">
                  <v-textarea v-model="subscriptionData.description" label="Description" required dense :error-messages="errors" :success="valid" focus rows="3" name="description"/>
                </ValidationProvider>
              </div>
              <div class="col-12">
                <ValidationProvider rules="required" name="Price" v-slot="{ errors, valid }">
                  <v-text-field type="number" v-model="subscriptionData.price" label="Price" required dense :error-messages="errors" :success="valid" focus min="0" name="price"/>
                </ValidationProvider>
              </div>
              <div class="col-12">
                <ImageInput v-model="subscriptionData.image" label="Image"/>
              </div>
            </div>
            <v-divider class="mx-4 pb-5"></v-divider>
            <div class="row">
              <div class="col-12 d-flex justify-space-between align-center">
                <span>Benefits</span>
                <v-btn icon color="primary" @click="addBenefit">
                  <v-icon>mdi-plus</v-icon>
                </v-btn>
              </div>
            </div>
            <div class="row" v-for="(benefit, index) in subscriptionData.benefits" :key="`benefit-${index}`">
              <div class="col-12 col-md-7 justify-center d-flex flex-column py-0">
                <ValidationProvider rules="required" :name="`Investigation-${index}`" v-slot="{ errors, valid }">
                  <v-select v-model="benefit.investigation" :items="availableInvestigations" label="Investigation" :error-messages="errors" :success="valid" item-text="name" item-value="id">
                  </v-select>
                </ValidationProvider>
              </div>
              <div class="col-12 col-md-5 justify-center d-flex flex-column py-0">
                <ValidationProvider rules="required" :name="`Discount-${index}`" v-slot="{ errors, valid }">
                  <v-text-field
                    type="number" v-model="benefit.discount" label="Discount" required :error-messages="errors" :success="valid" focus
                    min="0" max="100"
                    name="discount">
                      <v-btn slot="append" icon color="error" @click="removeBenefit(index)">
                        <v-icon>mdi-trash-can-outline</v-icon>
                      </v-btn>
                    </v-text-field>
                </ValidationProvider>
              </div>
              <div class="col-12">
                <div class="d-flex justify-space-between">
                  <div>Full price: </div>
                  <div>{{price(benefit.investigation)}} Ron</div>
                </div>
                <div class="d-flex justify-space-between">
                  <div>Discounted price: </div>
                  <div>{{computePrice(benefit.investigation, benefit.discount)}} Ron</div>
                </div>
              </div>
              <v-divider class="mx-4 pb-5"></v-divider>
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
import ImageInput from '@/pages/components/ImageInput.vue'
export default {
  name: 'SubscriptionDetailsDialog',
  components: {
    ValidationObserver, ValidationProvider, ImageInput
  },
  props: {
    isVisible: Boolean,
    subscription: Object
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
      subscriptionData: this.newSubscription(),
      availableInvestigations: []
    }
  },
  mounted() {
    this.loadAvailableInvestigations()
  },
  methods: {
    saveSubscription() {
      this.loading = true
      backend.$saveSubscription(this.subscriptionData).then(r => {
        this.$emit('save')
        this.showSuccessNotification('Data saved successfully')
        this.visible = false
      }).catch(e => { this.showErrorNotification('An error occured, try again later') })
        .then(() => { this.loading = false })
    },
    loadAvailableInvestigations() {
      this.loading = true
      backend.$findInvestigations().then(r => {
        this.availableInvestigations = r.data
      }).catch(e => { this.showErrorNotification('An error occured, try again later') })
        .then(() => { this.loading = false })
    },
    addBenefit() {
      this.subscriptionData.benefits.push({
        investigation: null,
        discount: 0.0
      })
    },
    removeBenefit(index) {
      this.subscriptionData.benefits.splice(index, 1)
    },
    price(investigationId) {
      const investigation = this.availableInvestigations.find(i => i.id === investigationId)
      if (investigation !== undefined) {
        return investigation.price
      }
      return 0
    },
    computePrice(investigationId, discount) {
      const investigation = this.availableInvestigations.find(i => i.id === investigationId)
      if (investigation !== undefined) {
        let discountValue = 0
        if (discount > 0) {
          discountValue = investigation.price * discount / 100
        }
        return (investigation.price - discountValue).toFixed(2)
      }
      return 0
    },
    newSubscription() {
      return {
        id: null,
        name: '',
        description: '',
        price: 0.0,
        benefits: [],
        image: null
      }
    }
  },
  watch: {
    isVisible: function(newValue, oldValue) {
      if (newValue) {
        if (this.$refs.observer) {
          this.$refs.observer.reset()
        }
        if (this.subscription !== null) {
          this.subscriptionData = this.clone(this.subscription)
        } else {
          this.subscriptionData = this.newSubscription()
        }
      }
    }
  }
}
</script>
