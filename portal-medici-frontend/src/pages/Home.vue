<template>
  <v-card :loading="loading" class="d-flex flex-column h-100">
    <v-card-title class="d-flex justify-space-between">
      <div>Home</div>
    </v-card-title>
    <v-divider class="mx-4"></v-divider>
    <v-card-text>
      <div class="row">
        <div class="col-12 col-md-6">
          <Appointments calendarType="day" onlyScheduled stopNavigationOnOpen/>
        </div>
        <div class="col-12 col-md-6">
          <ConsultationDetails :consultation="consultation" :closeable="false" v-if="consultation !== null" />
          <v-card v-else>
            <v-card-title class="justify-space-between">
              <div>
                <div>Consultation details</div>
              </div>
            </v-card-title>
            <v-divider class="mx-4 pb-5"></v-divider>
            <v-card-text class="fill-height">
              No active consultation
            </v-card-text>
          </v-card>
        </div>
      </div>
    </v-card-text>
  </v-card>
</template>
<script>
import Appointments from '@/pages/Appointments.vue'
import ConsultationDetails from '@/pages/components/ConsultationDetails.vue'
export default {
  name: 'Home',
  components: {
    Appointments, ConsultationDetails
  },
  data() {
    return {
      loading: false
    }
  },
  computed: {
    consultation() {
      return this.$store.state.activeConsultation
    }
  }
}
</script>
