<template>
  <v-card :loading="loading">
    <v-card-title class="d-flex justify-space-between">
      <div>Physicians</div>
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
        <template v-slot:[`item.image`]="{ item }" >
          <div class="d-flex justify-start align-center py-2">
            <img :src="item.image" width="64" height="64" />
          </div>
        </template>
        <template v-slot:[`item.profile`]="{ item }" >
          <div>{{item.profileData.firstName}} {{item.profileData.lastName}}</div>
        </template>
        <template v-slot:[`item.departments`]="{ item }" >
          <div v-for="(department, index) in item.departments" :key="`${item.id}-${index}`" >{{department}}</div>
        </template>
        <template v-slot:[`item.clinic`]="{ item }" >
          <div v-if="item.clinicData">{{item.clinicData.name}}</div>
        </template>
        <template v-slot:[`item.id`]="{ item }" >
          <div class="d-flex justify-end">
            <v-btn icon @click="editItem(item)">
              <v-icon>mdi-file-edit-outline</v-icon>
            </v-btn>
            <v-btn icon @click="deleteItem(item.id)" color="error">
              <v-icon>mdi-delete-outline</v-icon>
            </v-btn>
          </div>
        </template>
      </v-data-table>
      <div class="text-center">
        <v-pagination v-model="pageNumber" :total-visible="5" :length="Math.ceil(filteredItems.length / pageLength)" circle></v-pagination>
      </div>
    </v-card-text>
    <PhysiciansDetailsDialog :isVisible="showDetailsDialog" :medic="selectedItem" @save="loadItems" @closed="toggleDetailsDialog"/>
  </v-card>
</template>
<script>
import backend from '@/plugins/backend'
import PhysiciansDetailsDialog from '@/pages/components/PhysiciansDetailsDialog.vue'
export default {
  name: 'Physicians',
  components: {
    PhysiciansDetailsDialog
  },
  computed: {
    filteredItems() {
      return this.items.filter(i => {
        return i.profileData.firstName.toUpperCase().includes(this.searchValue.toUpperCase()) ||
            i.profileData.lastName.toUpperCase().includes(this.searchValue.toUpperCase()) ||
           i.departments.find(d => d.toUpperCase().includes(this.searchValue.toUpperCase())) !== undefined ||
           i.title.toUpperCase().includes(this.searchValue.toUpperCase()) ||
           i.grade.toUpperCase().includes(this.searchValue.toUpperCase())
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
        { text: 'Profile', align: 'left', sortable: true, value: 'image' },
        { text: 'Name', align: 'left', sortable: true, value: 'profile' },
        { text: 'Titlu', align: 'left', sortable: true, value: 'title' },
        { text: 'Grade', align: 'left', sortable: true, value: 'grade' },
        { text: 'Departments', align: 'left', sortable: true, value: 'departments' },
        { text: 'Clinic', align: 'left', sortable: true, value: 'clinic' },
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
      backend.$findMedics().then(r => {
        this.items = r.data
      }).catch(e => { this.showErrorNotification('An error occured, try again later') })
        .then(() => { this.loading = false })
    },
    deleteItem(id) {
      this.showConfirmationDialog('Do you want to remove this medic, it will cancel all his consultations?').then(() => {
        this.loading = true
        backend.$removeMedic(id).then(r => {
          this.showSuccessNotification('Medic removed successfully')
          this.loadItems()
        }).catch(e => { this.showErrorNotification('An error occured, try again later') })
          .then(() => { this.loading = false })
      })
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
