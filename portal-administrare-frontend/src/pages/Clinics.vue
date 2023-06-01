<template>
  <v-card :loading="loading">
    <v-card-title class="d-flex justify-space-between">
      <div>Clinics</div>
      <v-btn icon small color="primary" @click="toggleDetailsDialog">
        <v-icon>mdi-plus</v-icon>
      </v-btn>
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
            <v-btn icon @click="editItem(item)">
              <v-icon>mdi-home-edit-outline</v-icon>
            </v-btn>
            <v-btn icon @click="deleteItem(item.id)">
              <v-icon>mdi-delete-outline</v-icon>
            </v-btn>
          </div>
        </template>
      </v-data-table>
      <div class="text-center">
        <v-pagination v-model="pageNumber" :total-visible="5" :length="Math.ceil(filteredItems.length / pageLength)" circle></v-pagination>
      </div>
    </v-card-text>
    <ClinicDetailsDialog :isVisible="showDetailsDialog" :clinic="selectedItem" @save="loadItems" @closed="toggleDetailsDialog"/>
  </v-card>
</template>
<script>
import backend from '@/plugins/backend'
import ClinicDetailsDialog from '@/pages/components/ClinicDetailsDialog.vue'
export default {
  name: 'Clinics',
  components: {
    ClinicDetailsDialog
  },
  computed: {
    filteredItems() {
      return this.items.filter(i => i.name.toUpperCase().includes(this.searchValue.toUpperCase()))
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
      selectedItem: null,
      showDetailsDialog: false,
      headers: [
        { text: 'Name', align: 'left', sortable: true, value: 'name' },
        { text: 'Phone number', align: 'right', sortable: true, value: 'phoneNumber' },
        { text: 'City', align: 'right', sortable: true, value: 'address.city' },
        { text: 'County', align: 'right', sortable: true, value: 'address.county' },
        { text: 'Street', align: 'right', sortable: true, value: 'address.street' },
        { text: 'Number', align: 'right', sortable: true, value: 'address.number' },
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
      backend.$findClinics().then(r => {
        this.items = r.data
      }).catch(e => { alert('An error occured, try again later') })
        .then(() => { this.loading = false })
    },
    deleteItem(id) {
      this.loading = true
      backend.$removeClinic(id).then(r => {
        this.loadItems()
      }).catch(e => { alert('An error occured, try again later') })
        .then(() => { this.loading = false })
    },
    editItem(item) {
      this.selectedItem = item
      this.showDetailsDialog = true
    },
    toggleDetailsDialog() {
      if (this.showDetailsDialog) {
        this.selectedItem = null
      }
      this.showDetailsDialog = !this.showDetailsDialog
    }
  }
}
</script>
