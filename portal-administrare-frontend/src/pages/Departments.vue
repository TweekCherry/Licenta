<template>
  <v-card :loading="loading">
    <v-card-title class="d-flex justify-space-between">
      <div>Departments</div>
    </v-card-title>
    <v-card-text>
      <div class="d-flex align-center">
        <v-text-field label="Search" hide-details append-icon="mdi-magnify" v-model="searchValue" class="mb-4"/>
        <v-btn color="green" fab small dark @click="toggleDepartmentDetailsDialog" icon dense>
          <v-icon>mdi-plus</v-icon>
        </v-btn>
      </div>
      <v-virtual-scroll
        v-if="filteredItems.length > 0"
        :items="filteredItems"
        :bench="2"
        height="420"
        item-height="42">
        <template v-slot:default="{ item }">
          <v-list-item :key="item.name" dense style="border-style: ridge; border-width: thin;">
            <v-list-item-content>
              <v-list-item-title>{{item.name}}</v-list-item-title>
            </v-list-item-content>
            <v-list-item-action class="d-flex flex-row align-center my-0">
              <v-btn icon @click="editDepartment(item)">
                <v-icon>mdi-pencil-box-outline</v-icon>
              </v-btn>
              <v-btn icon color="red" @click="deleteDepartment(item.name)">
                <v-icon>mdi-delete-forever</v-icon>
              </v-btn>
            </v-list-item-action>
          </v-list-item>
        </template>
      </v-virtual-scroll>
      <div v-else>No data available</div>
    </v-card-text>
    <DepartmentDetailsDialog :isVisible="showDepartmentDetailsDialog" :department="department" @save="loadDepartments" @closed="toggleDepartmentDetailsDialog"/>
  </v-card>
</template>
<script>
import backend from '@/plugins/backend'
import DepartmentDetailsDialog from '@/pages/components/DepartmentDetailsDialog.vue'
export default {
  name: 'Departments',
  components: {
    DepartmentDetailsDialog
  },
  computed: {
    filteredItems() {
      return this.departments.filter(i => i.name.toUpperCase().includes(this.searchValue.toUpperCase()))
    }
  },
  data() {
    return {
      searchValue: '',
      departments: [],
      loading: false,
      department: null,
      showDepartmentDetailsDialog: false
    }
  },
  mounted() {
    this.loadDepartments()
  },
  methods: {
    loadDepartments() {
      this.loading = true
      backend.$findDepartments().then(r => {
        this.departments = r.data
      }).catch(e => { alert('An error occured, try again later') })
        .then(() => { this.loading = false })
    },
    deleteDepartment(id) {
      this.loading = true
      backend.$removeDepartment(id).then(r => {
        this.loadDepartments()
      }).catch(e => { alert('An error occured, try again later') })
        .then(() => { this.loading = false })
    },
    editDepartment(department) {
      this.department = department
      this.showDepartmentDetailsDialog = true
    },
    toggleDepartmentDetailsDialog() {
      if (this.showDepartmentDetailsDialog) {
        this.department = null
      }
      this.showDepartmentDetailsDialog = !this.showDepartmentDetailsDialog
    }
  }
}
</script>
