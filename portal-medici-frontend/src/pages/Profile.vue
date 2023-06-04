<template>
  <v-card :loading="loading">
    <v-card-title class="d-flex justify-space-between">
      <div>Profile</div>
    </v-card-title>
    <v-card-text>
      <div class="container">
        <div class="row">
          <div class="col-12">
            <div class="row">
              <div class="col-12">
                <span>Personal</span>
                <v-divider></v-divider>
              </div>
              <div class="col-12">
                <v-text-field v-model="medic.grade" label="Grade" required dense focus readonly disabled/>
              </div>
              <div class="col-12 col-md-4">
                <v-text-field v-model="medic.title" label="Title" required dense focus readonly disabled/>
              </div>
              <div class="col-12 col-md-4">
                <v-text-field v-model="profile.firstName" label="First Name" required dense focus readonly disabled/>
              </div>
              <div class="col-12 col-md-4">
                <v-text-field v-model="profile.lastName" label="Last Name" required dense focus readonly disabled/>
              </div>
              <div class="col-12">
                <v-text-field v-model="profile.phoneNumber" label="Phone number" required dense focus readonly disabled/>
              </div>
              <div class="col-12">
                <v-text-field v-model="medic.clinicData.name" label="Clinic" required dense focus readonly disabled :hint="medic.clinicData.phoneNumber" persistent-hint/>
              </div>
              <div class="col-12">
                <v-select
                  v-model="medic.departments"
                  :items="medic.departments"
                  attach
                  chips
                  label="Chips"
                  multiple
                  disabled
                  readonly
                ></v-select>
              </div>
            </div>
          </div>
        </div>
      </div>
    </v-card-text>
  </v-card>
</template>
<script>
export default {
  name: 'Profile',
  data() {
    return {
      loading: false,
      readOnly: true,
      profile: {
        address: {

        }
      },
      medic: {
        departments: []
      },
      isPasswordVisible: false
    }
  },
  mounted() {
    this.profile = this.clone(this.$store.state.profile)
    this.medic = this.clone(this.$store.state.medic)
  }
}
</script>
