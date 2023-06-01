<template>
  <v-card :loading="loading">
    <v-card-title class="d-flex justify-space-between">
      <div>Investigations</div>
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
            <v-btn icon @click="deleteItem(item.id)" color="error">
              <v-icon>mdi-delete-outline</v-icon>
            </v-btn>
          </div>
        </template>
        <template v-slot:[`item.price`]="{ item }" >
          <div>{{item.price}} Ron</div>
        </template>
        <template v-slot:[`item.clinics`]="{ item }" >
          <v-chip v-for="clinic in item.clinicsData" :key="`${item.id}-${clinic.id}`" color="primary" class="mr-1">{{clinic.name}}</v-chip>
        </template>
      </v-data-table>
      <div class="text-center">
        <v-pagination v-model="pageNumber" :total-visible="5" :length="Math.ceil(filteredItems.length / pageLength)" circle></v-pagination>
      </div>
    </v-card-text>
    <InvestigationDetailsDialog :isVisible="showDetailsDialog" :investigation="selectedItem" @save="loadItems" @closed="toggleDetailsDialog"/>
  </v-card>
</template>
<script>
import backend from '@/plugins/backend'
import InvestigationDetailsDialog from '@/pages/components/InvestigationDetailsDialog.vue'
export default {
  name: 'Investigations',
  components: {
    InvestigationDetailsDialog
  },
  computed: {
    filteredItems() {
      return this.items.filter(i => {
        return i.name.toUpperCase().includes(this.searchValue.toUpperCase()) ||
           i.department.toUpperCase().includes(this.searchValue.toUpperCase()) ||
           i.type.toUpperCase().includes(this.searchValue.toUpperCase())
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
      selectedItem: null,
      showDetailsDialog: false,
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
      }).catch(e => { alert('An error occured, try again later') })
        .then(() => { this.loading = false })
    },
    deleteItem(id) {
      this.loading = true
      backend.$removeInvestigation(id).then(r => {
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
