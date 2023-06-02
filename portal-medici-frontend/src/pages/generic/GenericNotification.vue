<template>
  <v-snackbar v-model="shown" vertical :color="type" :timeout="timeout" outlined @mouseleave.native="startProgress" @mouseenter.native="stopProgress">
   <v-progress-linear :color="type" absolute top :value="100 - Math.ceil((100 * currentTime) / timeout)"/>
    <div>
      <b>{{title}}</b>
    </div>
    <div>{{text}}</div>
    <template v-slot:action="{ }">
      <v-btn text :color="type" @click="shown = false">Close</v-btn>
    </template>
  </v-snackbar>
</template>
<script>
export default {
  name: 'GenericNotification',
  props: {
    title: {
      type: String,
      default: ''
    },
    text: String,
    type: {
      type: String,
      default: 'success'
    }
  },
  computed: {
    shown: {
      get: function() {
        return this.isVisible
      },
      set: function() {
        this.isVisible = false
        this.$nextTick(() => {
          this.$emit('hidden')
        })
      }
    }
  },
  data: () => ({
    isVisible: true,
    timeout: 1500,
    currentTime: 0,
    poolingRate: 100,
    progressOff: false
  }),
  mounted() {
    this.syncProgress()
  },
  methods: {
    syncProgress() {
      setTimeout(() => {
        if (!this.progressOff) {
          this.currentTime += this.poolingRate
          if (this.timeout >= this.currentTime) {
            this.syncProgress()
          }
        }
      }, this.poolingRate)
    },
    stopProgress() {
      this.progressOff = true
      this.currentTime = 0
    },
    startProgress() {
      this.progressOff = false
      this.syncProgress()
    }
  }
}
</script>
