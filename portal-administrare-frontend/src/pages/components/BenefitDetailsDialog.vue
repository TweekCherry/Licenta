<template>
  <v-dialog v-model="visible" scrollable width="500">
    <ValidationObserver ref="observer" v-slot="{ handleSubmit }">
      <v-form @submit.prevent="handleSubmit(saveBenefit)">
        <v-card>
          <v-card-title class="justify-space-between">
            <div>
              <div>Benefit details</div>
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
              <div class="col-12 col-md-7 justify-center d-flex flex-column py-0">
                <ValidationProvider rules="required" name="Investigation" v-slot="{ errors, valid }">
                  <v-autocomplete v-model="benefitData.investigation" :items="availableInvestigations" label="Investigation" :error-messages="errors" :success="valid" item-text="name" item-value="id" @input="updateInvestigationData">
                  </v-autocomplete>
                </ValidationProvider>
              </div>
              <div class="col-12 col-md-5 justify-center d-flex flex-column py-0">
                <ValidationProvider rules="required" name="Discount" v-slot="{ errors, valid }">
                  <v-text-field
                    type="number" v-model="benefitData.discount" label="Discount" required :error-messages="errors" :success="valid" focus
                    min="0" max="100"
                    name="discount">
                    </v-text-field>
                </ValidationProvider>
              </div>
              <div class="col-12">
                <div class="d-flex justify-space-between">
                  <div>Full price: </div>
                  <div>{{price(benefitData.investigation)}} Ron</div>
                </div>
                <div class="d-flex justify-space-between">
                  <div>Discounted price: </div>
                  <div>{{computePrice(benefitData.investigation, benefitData.discount)}} Ron</div>
                </div>
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
import { ValidationObserver, ValidationProvider } from 'vee-validate'
export default {
  name: 'BenefitDetailsDialog',
  components: {
    ValidationObserver, ValidationProvider
  },
  props: {
    isVisible: Boolean,
    benefit: Object,
    availableInvestigations: Array
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
      benefitData: {
        discount: 0,
        investigation: null,
        investigationData: null
      }
    }
  },
  methods: {
    saveBenefit() {
      this.$emit('save', this.benefitData)
    },
    updateInvestigationData(investigationId) {
      this.benefitData.investigationData = this.availableInvestigations.find(i => i.id === investigationId)
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
    }
  },
  watch: {
    isVisible: function(newValue, oldValue) {
      if (newValue) {
        if (this.$refs.observer) {
          this.$refs.observer.reset()
        }
        if (this.benefit !== null) {
          this.benefitData = this.clone(this.benefit)
        } else {
          this.benefitData = {
            discount: 0,
            investigation: null,
            investigationData: null
          }
        }
      }
    }
  }
}
</script>
