<template>
  <v-card :loading="loading">
    <v-card-title class="d-flex justify-space-between">
      <div>Physicians</div>
    </v-card-title>
    <v-card-text>
      <div class="row">
        <div class="col-12 d-flex align-center">
          <v-text-field label="Search" hide-details append-icon="mdi-magnify" v-model="searchValue" class="mb-4"/>
        </div>
      </div>
      <div class="row">
        <div class="col-12 col-md-3" v-for="medic in filteredItems" :key="medic.id">
          <v-card outlined class="fill-height">
            <v-list-item three-line>
              <v-list-item-avatar tile size="80" color="grey">
                <v-img :src="medic.image"></v-img>
              </v-list-item-avatar>
              <v-list-item-content>
                <div class="d-flex justify-space-between">
                  <div class="text-overline">{{medic.title}}</div>
                  <v-btn icon @click="bookAppointment(medic)" color="primary">
                    <v-icon>mdi-notebook-plus-outline</v-icon>
                  </v-btn>
                </div>
                <v-list-item-title class="text-h5 mb-1">
                  {{medic.profileData.firstName}} {{medic.profileData.lastName}}
                </v-list-item-title>
                <v-list-item-subtitle>
                  <div>{{medic.grade}}</div>
                  {{medic.clinicData.name}}
                </v-list-item-subtitle>
              </v-list-item-content>
            </v-list-item>
            <v-divider class="mx-3"></v-divider>
            <v-card-text>
              <v-chip v-for="(department, index) in medic.departments" :key="`${medic.id}-department-${index}`" outlined color="primary" class="mr-2 mb-2" @click="bookAppointmentDepartment(medic, department)">
                {{department}}
              </v-chip>
            </v-card-text>
          </v-card>
        </div>
      </div>
    </v-card-text>
  </v-card>
</template>
<script>
import backend from '@/plugins/backend'
export default {
  name: 'Physicians',
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
      loading: false
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
    bookAppointmentDepartment(medic, department) {
      const appointmentData = {
        id: null,
        user: null,
        clinic: medic.clinicData.id,
        medic: medic.id,
        investigation: null,
        timestamp: this.getNewAppointmentTimestamp(),
        status: 'SCHEDULED',
        medicData: medic,
        clinicData: medic.clinicData,
        investigationData: {
          department: department
        }
      }
      this.$router.push({
        name: 'Appointments',
        params: {
          appointmentData: appointmentData
        }
      })
    },
    bookAppointment(medic) {
      this.showSelectPromptDialog('Choose your department', 'Department', 'required', medic.departments, 'name').then(result => {
        const appointmentData = {
          id: null,
          user: null,
          clinic: medic.clinicData.id,
          medic: medic.id,
          investigation: null,
          timestamp: this.getNewAppointmentTimestamp(),
          status: 'SCHEDULED',
          medicData: medic,
          clinicData: medic.clinicData,
          investigationData: {
            department: result
          }
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
