<template>
  <div>
    <div class="row">
      <div class="col-12 col-md-3 d-flex align-center">
        <v-text-field label="Search" hide-details append-icon="mdi-magnify" v-model="searchValue" class="mb-4"/>
      </div>
      <div class="col-12 col-md-6"></div>
      <div class="col-12 col-md-3 d-flex align-center">
        <v-select v-model="pageLength" :items="pageLengthOptions" label="Page length"  item-value="value" item-text="text" dense hide-details attach></v-select>
      </div>
    </div>
    <v-data-table
      :headers="headers"
      :items="filteredItems"
      :items-per-page="pageLength"
      :page="pageNumber"
      dense
      hide-default-footer>
      <template v-slot:[`item.name`]="{ item }" >
        <div class="body-1">{{item.investigationData.name}}</div>
        <div class="caption">{{item.investigationData.type}}</div>
        <div class="caption">{{item.investigationData.department}}</div>
      </template>
      <template v-slot:[`item.discount`]="{ item }" >{{item.discount}}%</template>
      <template v-slot:[`item.price`]="{ item }" >
        <div class="text-decoration-line-through red--text">{{item.investigationData.price}} Ron</div>
        <div>{{computePrice(item.investigationData.price, item.discount)}} Ron</div>
      </template>
      <template v-slot:[`item.id`]="{ item }" >
        <div class="d-flex justify-end">
          <v-btn icon @click="bookInvestigation(item)">
            <v-icon>mdi-book-plus-outline</v-icon>
          </v-btn>
        </div>
      </template>
    </v-data-table>
    <div class="text-center">
      <v-pagination v-model="pageNumber" :total-visible="5" :length="Math.ceil(filteredItems.length / pageLength)" circle></v-pagination>
    </div>
  </div>
</template>
<script>
export default {
  name: 'SubscriptionBenefitsTable',
  props: {
    subscription: {
      type: Object,
      default: () => {
        return { benefits: [] }
      }
    },
    enableBooking: {
      type: Boolean,
      default: false
    }
  },
  computed: {
    filteredItems() {
      return this.subscription.benefits.filter(b => {
        return b.investigationData.name.toUpperCase().includes(this.searchValue.toUpperCase()) ||
           b.investigationData.department.toUpperCase().includes(this.searchValue.toUpperCase()) ||
           b.investigationData.type.toUpperCase().includes(this.searchValue.toUpperCase())
      })
    },
    headers() {
      const headers = [
        { text: 'Name', align: 'left', sortable: true, value: 'name' },
        { text: 'Discount', align: 'left', sortable: true, value: 'discount' },
        { text: 'Price', align: 'left', sortable: true, value: 'price' }
      ]
      if (this.enableBooking) {
        headers.push({ text: 'Actions', align: 'left', sortable: true, value: 'id' })
      }
      return headers
    }
  },
  data() {
    return {
      searchValue: '',
      pageNumber: 1,
      pageLength: 10,
      pageLengthOptions: [
        { text: '10', value: 10 },
        { text: '25', value: 25 },
        { text: '50', value: 50 },
        { text: '100', value: 100 }
      ]
    }
  },
  methods: {
    computePrice(price, discount) {
      if (discount > 0) {
        return (price - (price * discount / 100)).toFixed(2)
      }
      return price.toFixed(2)
    },
    bookInvestigation(item) {
      this.$emit('book', item.investigationData)
    }
  }
}
</script>
