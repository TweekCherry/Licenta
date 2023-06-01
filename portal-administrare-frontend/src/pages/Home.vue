<template>
  <div class="row">
    <div class="col-12 col-md-4 col-lg-2" v-for="(stat, index) in stats" :key="index">
      <v-card outlined :loading="loading">
        <v-card-title class="text-h5">{{stat.name}}</v-card-title>
        <v-card-subtitle class="pt-2">
          {{stat.description}} <v-chip color="primary">{{stat.counter}}</v-chip>
        </v-card-subtitle>
        <v-card-actions>
          <v-btn color="primary" plain v-if="stat.route !== null" :to="stat.route">View</v-btn>
        </v-card-actions>
      </v-card>
    </div>
  </div>
</template>
<script>
import backend from '@/plugins/backend'
export default {
  name: 'Home',
  data() {
    return {
      loading: false,
      stats: [
        { name: 'Investigations', description: 'Available investigations: ', counter: 0, route: '/investigations' },
        { name: 'Subscriptions', description: 'Available subscriptions: ', counter: 0, route: '/subscriptions' },
        { name: 'Physicians', description: 'Available physicians: ', counter: 0, route: '/physicians' },
        { name: 'Clinics', description: 'Available clinics: ', counter: 0, route: '/clinics' },
        { name: 'Departments', description: 'Available departments: ', counter: 0, route: '/departments' },
        { name: 'Consultations', description: 'Registered consultations: ', counter: 0, route: null },
        { name: 'Patients', description: 'Registered patients: ', counter: 0, route: null }
      ]
    }
  },
  mounted() {
    this.findStats()
  },
  methods: {
    findStats() {
      this.loading = true
      backend.$findStats().then(r => {
        this.stats[0].counter = r.data.investigationsCounter
        this.stats[1].counter = r.data.subscriptionsCounter
        this.stats[2].counter = r.data.medicsCounter
        this.stats[3].counter = r.data.clinicsCounter
        this.stats[4].counter = r.data.departmentsCounter
        this.stats[5].counter = r.data.consultationsCounter
        this.stats[6].counter = r.data.patientsCounter
      }).catch(e => this.showErrorNotification('An error occured, try again later'))
        .then(() => { this.loading = false })
    }
  }
}
</script>
