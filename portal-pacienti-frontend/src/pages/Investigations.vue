<template>
  <v-card :loading="loading">
    <v-card-title class="d-flex justify-space-between">
      <div>Investigations</div>
    </v-card-title>
    <v-card-text>
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
        <template v-slot:[`item.id`]="{ item }" >
          <div class="d-flex justify-end">
            <v-btn icon @click="bookInvestigation(item)">
              <v-icon>mdi-book-plus-outline</v-icon>
            </v-btn>
          </div>
        </template>
        <template v-slot:[`item.price`]="{ item }" >
          <div class="text-body-1 font-weight-bold">{{computeInvestigationPrice(item)}} Ron</div>
          <div class="text-decoration-line-through red--text font-weight-regular text-body-2" v-if="isReduced(item)">({{item.price}} Ron)</div>
        </template>
        <template v-slot:[`item.clinics`]="{ item }" >
          <v-chip v-for="clinic in item.clinicsData" :key="`${item.id}-${clinic.id}`" color="primary" class="mr-1">{{clinic.name}}</v-chip>
        </template>
      </v-data-table>
      <div class="text-center">
        <v-pagination v-model="pageNumber" :total-visible="5" :length="Math.ceil(filteredItems.length / pageLength)" circle></v-pagination>
      </div>
    </v-card-text>
  </v-card>
</template>
<script>
import backend from '@/plugins/backend'
export default {
  name: 'Investigations',
  computed: {
    filteredItems() {
      const searchHint = this.searchValue.trim()
      return this.items.filter(i => {
        return i.name.toUpperCase().includes(searchHint.toUpperCase()) ||
           i.department.toUpperCase().includes(searchHint.toUpperCase()) ||
           i.type.toUpperCase().includes(searchHint.toUpperCase())
      })
    }
  },
  data() {
    return {
      items: [],
      searchValue: '',
      pageNumber: 1,
      pageLength: 10,
      pageLengthOptions: [
        { text: '10', value: 10 },
        { text: '25', value: 25 },
        { text: '50', value: 50 },
        { text: '100', value: 100 }
      ],
      loading: false,
      headers: [
        { text: 'Name', align: 'left', sortable: true, value: 'name' },
        { text: 'Price', align: 'left', sortable: true, value: 'price' },
        { text: 'Department', align: 'left', sortable: true, value: 'department' },
        { text: 'Type', align: 'left', sortable: true, value: 'type' },
        { text: 'Clinics', align: 'right', sortable: false, value: 'clinics' },
        { text: 'Actions', align: 'right', sortable: false, value: 'id' }
      ]
    }
  },
  mounted() {
    this.loadItems()
  },
  methods: {
    loadItems() {
      this.loading = true
      backend.$findInvestigations().then(r => {
        this.items = r.data
      }).catch(e => { this.showErrorNotification('An error occured, try again later') })
        .then(() => { this.loading = false })
    },
    bookInvestigation(investigation) {
      this.showSelectPromptDialog('Choose your clinic', 'Clinic', 'required', investigation.clinicsData, 'name', 'id').then(result => {
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
    }
  }
}
</script>
