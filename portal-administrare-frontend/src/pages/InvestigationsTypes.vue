<template>
  <v-card :loading="loading">
    <v-card-title class="d-flex justify-space-between">
      <div>Investigation Types</div>
    </v-card-title>
    <v-card-text>
      <div class="d-flex align-center">
        <v-text-field label="Search" hide-details append-icon="mdi-magnify" v-model="searchValue" class="mb-4"/>
        <v-btn color="green" fab small dark @click="toggleDetailsDialog" icon dense>
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
              <v-btn icon @click="editItem(item)">
                <v-icon>mdi-pencil-box-outline</v-icon>
              </v-btn>
              <v-btn icon color="red" @click="deleteItem(item.name)">
                <v-icon>mdi-delete-forever</v-icon>
              </v-btn>
            </v-list-item-action>
          </v-list-item>
        </template>
      </v-virtual-scroll>
      <div v-else>No data available</div>
    </v-card-text>
    <InvestigationTypeDetailsDialog :isVisible="showDetailsDialog" :investigationType="item" @save="loadItems" @closed="toggleDetailsDialog"/>
  </v-card>
</template>
<script>
import backend from '@/plugins/backend'
import InvestigationTypeDetailsDialog from '@/pages/components/InvestigationTypeDetailsDialog.vue'
export default {
  name: 'InvestigationTypes',
  components: {
    InvestigationTypeDetailsDialog
  },
  computed: {
    filteredItems() {
      return this.items.filter(i => i.name.toUpperCase().includes(this.searchValue.toUpperCase()))
    }
  },
  data() {
    return {
      searchValue: '',
      items: [],
      loading: false,
      item: null,
      showDetailsDialog: false
    }
  },
  mounted() {
    this.loadItems()
  },
  methods: {
    loadItems() {
      this.loading = true
      backend.$findInvestigationTypes().then(r => {
        this.items = r.data
      }).catch(e => { this.showErrorNotification('An error occured, try again later') })
        .then(() => { this.loading = false })
    },
    deleteItem(id) {
      this.showConfirmationDialog('Do you want to remove this investigation type?').then(() => {
        this.loading = true
        backend.$removeInvestigationType(id).then(r => {
          this.showSuccessNotification('Investigation type removed successfully')
          this.loadItems()
        }).catch(e => { this.showErrorNotification('An error occured, try again later') })
          .then(() => { this.loading = false })
      })
    },
    editItem(item) {
      this.item = item
      this.showDetailsDialog = true
    },
    toggleDetailsDialog() {
      if (this.showDetailsDialog) {
        this.item = null
      }
      this.showDetailsDialog = !this.showDetailsDialog
    }
  }
}
</script>
