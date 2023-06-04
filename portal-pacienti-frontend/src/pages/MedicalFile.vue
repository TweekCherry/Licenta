<template>
  <v-card :loading="loading">
    <v-card-title class="d-flex justify-space-between">
      <div>My medical file</div>
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
            <v-btn icon @click="viewItem(item)">
              <v-icon>mdi-file-account-outline</v-icon>
            </v-btn>
          </div>
        </template>
        <template v-slot:[`item.appointment.timestamp`]="{ item }" >
          <div>{{formatter.fromISO(item.appointment.timestamp).toFormat('dd-LL-yyyy HH:mm')}}</div>
        </template>
        <template v-slot:[`item.appointment.status`]="{ item }" >
          <v-chip :color="getColor(item.appointment.status)" small>{{item.appointment.status}}</v-chip>
        </template>
        <template v-slot:[`item.appointment.medicData`]="{ item }" >
          <div class="body-2 text--secondary">{{item.appointment.medicData.title}}</div>
          <div class="body-1">
            {{item.appointment.medicData.profileData.firstName}} {{item.appointment.medicData.profileData.lastName}}
          </div>
          <div class="body-2 text--secondary">{{item.appointment.medicData.grade}}</div>
        </template>
      </v-data-table>
      <div class="text-center">
        <v-pagination v-model="pageNumber" :total-visible="5" :length="Math.ceil(filteredItems.length / pageLength)" circle></v-pagination>
      </div>
    </v-card-text>
    <ConsultationDetailsDialog :isVisible="showDetailsDialog" :consultation="selectedItem" @save="loadItems" @closed="toggleDetailsDialog"/>
  </v-card>
</template>
<script>
import backend from '@/plugins/backend'
import ConsultationDetailsDialog from '@/pages/components/ConsultationDetailsDialog.vue'
import { DateTime } from 'luxon'
export default {
  name: 'Consultations',
  components: {
    ConsultationDetailsDialog
  },
  computed: {
    filteredItems() {
      return this.items.filter(i => {
        return i.appointment.medicData.profileData.firstName.toUpperCase().includes(this.searchValue.toUpperCase()) ||
            i.appointment.medicData.profileData.lastName.toUpperCase().includes(this.searchValue.toUpperCase()) ||
           i.appointment.investigationData.department.toUpperCase().includes(this.searchValue.toUpperCase()) ||
           i.appointment.investigationData.name.toUpperCase().includes(this.searchValue.toUpperCase()) ||
           i.appointment.investigationData.type.toUpperCase().includes(this.searchValue.toUpperCase())
      })
    }
  },
  data() {
    return {
      items: [],
      formatter: DateTime,
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
      selectedItem: null,
      showDetailsDialog: false,
      headers: [
        { text: 'Medic', align: 'left', sortable: true, value: 'appointment.medicData' },
        { text: 'Diagnostic', align: 'left', sortable: false, value: 'diagnostic' },
        { text: 'Investigation', align: 'left', sortable: true, value: 'appointment.investigationData.name' },
        { text: 'Department', align: 'left', sortable: true, value: 'appointment.investigationData.department' },
        { text: 'Type', align: 'left', sortable: true, value: 'appointment.investigationData.type' },
        { text: 'Date', align: 'left', sortable: true, value: 'appointment.timestamp' },
        { text: 'Status', align: 'left', sortable: true, value: 'appointment.status' },
        { text: 'Actions', align: 'right', sortable: false, value: 'id' }
      ]
    }
  },
  mounted() {
    this.loadItems()
    if (this.$route.params.consultation !== undefined) {
      this.editItem(this.$route.params.consultation)
    }
  },
  methods: {
    loadItems() {
      this.loading = true
      return backend.$findConsultations().then(r => {
        this.items = r.data
      }).catch(e => { this.showErrorNotification('An error occured, try again later') })
        .then(() => { this.loading = false })
    },
    viewItem(item) {
      this.selectedItem = item
      this.showDetailsDialog = true
    },
    toggleDetailsDialog() {
      if (this.showDetailsDialog) {
        this.selectedItem = null
      }
      this.showDetailsDialog = !this.showDetailsDialog
    },
    getColor(item) {
      let color = 'error'
      if (item !== null) {
        if (item.status === 'SCHEDULED') {
          color = 'success'
        } else if (item.status === 'FINISHED') {
          color = 'blue'
        } else if (item.status === 'NOT_PRESENTED') {
          color = 'warning'
        }
      }
      return color
    }
  }
}
</script>
