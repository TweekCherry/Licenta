<template>
  <v-dialog v-model="visible" :fullscreen="true" scrollable persistent>
    <ConsultationDetails :consultation="consultationData" @save="visible = false" @closed="visible = false"/>
  </v-dialog>
</template>
<script>
import ConsultationDetails from '@/pages/components/ConsultationDetails.vue'
export default {
  name: 'ConsultationDetailsDialog',
  components: {
    ConsultationDetails
  },
  props: {
    isVisible: Boolean,
    consultation: Object
  },
  computed: {
    visible: {
      get: function() {
        return this.isVisible
      },
      set: function(v) {
        this.$emit('closed')
      }
    }
  },
  data() {
    return {
      fullscreen: false,
      loading: false,
      consultationData: this.newConsultationData()
    }
  },
  methods: {
    newConsultationData() {
      return {
        id: null,
        diagnostic: '',
        prescription: {
          drugs: []
        },
        appointment: {
          id: null,
          user: null,
          timestamp: null,
          status: null
        }
      }
    }
  },
  watch: {
    isVisible: function(newValue, oldValue) {
      if (newValue) {
        if (this.$refs.observer) {
          this.$refs.observer.reset()
        }
        if (this.consultation !== null) {
          this.consultationData = this.clone(this.consultation)
        } else {
          this.consultationData = this.newConsultationData()
        }
      }
    }
  }
}
</script>
