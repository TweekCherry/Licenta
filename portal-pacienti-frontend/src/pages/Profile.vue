<template>
  <v-card :loading="loading">
    <v-card-title class="d-flex justify-space-between">
      <div>Profile</div>
      <v-btn icon small color="primary" @click="toggleProfileEdit">
        <v-icon>{{editIcon}}</v-icon>
      </v-btn>
    </v-card-title>
    <v-card-text>
      <ValidationObserver ref="observer" v-slot="{ handleSubmit }">
        <v-form @submit.prevent="handleSubmit(updateProfile)">
          <div class="container">
            <div class="row">
              <div class="col-12 col-md-6">
                <div class="row">
                  <div class="col-12">
                    <span>Personal</span>
                    <v-divider></v-divider>
                  </div>
                  <div class="col-12 col-md-6">
                    <ValidationProvider rules="required" name="First Name" v-slot="{ errors, valid }">
                      <v-text-field v-model="profile.firstName" label="First Name" required dense :error-messages="errors" :success="valid" focus name="firstname" :readonly="readOnly" :disabled="readOnly"/>
                    </ValidationProvider>
                  </div>
                  <div class="col-12 col-md-6">
                    <ValidationProvider rules="required" name="Last Name" v-slot="{ errors, valid }">
                      <v-text-field v-model="profile.lastName" label="Last Name" required dense :error-messages="errors" :success="valid" focus name="lastName" :readonly="readOnly" :disabled="readOnly"/>
                    </ValidationProvider>
                  </div>
                  <div class="col-12">
                    <ValidationProvider rules="required" name="phoneNumber" v-slot="{ errors, valid }">
                      <v-text-field v-model="profile.phoneNumber" label="phoneNumber" required dense :error-messages="errors" :success="valid" focus name="phoneNumber" :readonly="readOnly" :disabled="readOnly"/>
                    </ValidationProvider>
                  </div>
                  <div class="col-12">
                    <ValidationProvider rules="required|length:13|cnp" name="CNP" v-slot="{ errors, valid }">
                      <v-text-field v-model="profile.cnp" label="CNP" required dense :error-messages="errors" :success="valid" focus name="cnp" :readonly="readOnly" :disabled="readOnly"/>
                    </ValidationProvider>
                  </div>
                </div>
              </div>
              <div class="col-12 col-md-6">
                <div class="row">
                  <div class="col-12">
                    <span>Address</span>
                  <v-divider></v-divider>
                  </div>
                  <div class="col-12">
                    <ValidationProvider rules="required" name="city" v-slot="{ errors, valid }">
                      <v-text-field v-model="profile.address.city" label="city" required dense :error-messages="errors" :success="valid" focus name="city" :readonly="readOnly" :disabled="readOnly"/>
                    </ValidationProvider>
                  </div>
                  <div class="col-12">
                    <ValidationProvider rules="required" name="county" v-slot="{ errors, valid }">
                      <v-text-field v-model="profile.address.county" label="county" required dense :error-messages="errors" :success="valid" focus name="county" :readonly="readOnly" :disabled="readOnly"/>
                    </ValidationProvider>
                  </div>
                  <div class="col-12">
                    <ValidationProvider rules="required" name="street" v-slot="{ errors, valid }">
                      <v-text-field v-model="profile.address.street" label="street" required dense :error-messages="errors" :success="valid" focus name="street" :readonly="readOnly" :disabled="readOnly"/>
                    </ValidationProvider>
                  </div>
                  <div class="col-12">
                    <v-text-field v-model="profile.address.number" label="number" required dense focus name="number" :readonly="readOnly" :disabled="readOnly"/>
                  </div>
                  <div class="col-12">
                    <v-textarea rows="3" v-model="profile.address.details" label="details" required dense focus name="details" :readonly="readOnly" :disabled="readOnly"/>
                  </div>
                </div>
              </div>
            </div>
            <div class="row">
              <div class="col-12" v-if="!readOnly">
                <v-btn block elevation="2" color="primary" type="submit">Submit</v-btn>
              </div>
            </div>
          </div>
        </v-form>
      </ValidationObserver>
    </v-card-text>
  </v-card>
</template>
<script>
import { ValidationObserver, ValidationProvider } from 'vee-validate'
import backend from '@/plugins/backend'
export default {
  name: 'Profile',
  components: {
    ValidationObserver, ValidationProvider
  },
  data() {
    return {
      loading: false,
      readOnly: true,
      profile: {
        address: {

        }
      },
      isPasswordVisible: false
    }
  },
  computed: {
    editIcon() {
      if (this.readOnly) {
        return 'mdi-pencil'
      }
      return 'mdi-close'
    },
    passwordInputType() {
      if (this.isPasswordVisible) {
        return 'text'
      }
      return 'password'
    },
    passwordInputIcon() {
      if (this.isPasswordVisible) {
        return 'mdi-eye-outline'
      }
      return 'mdi-eye-off-outline'
    }
  },
  mounted() {
    this.profile = this.clone(this.$store.state.profile)
    if (this.profile.address === null) {
      this.profile.address = {
        street: null,
        city: null,
        county: null,
        number: null,
        details: null
      }
    }
  },
  methods: {
    toggleProfileEdit() {
      if (!this.readOnly) {
        this.profile = this.clone(this.$store.state.profile) // restore the data here, maybe it was changed and user clicked the x button
      }
      this.readOnly = !this.readOnly
    },
    togglePasswordVisible() {
      this.isPasswordVisible = !this.isPasswordVisible
    },
    updateProfile() {
      this.loading = true
      backend.$updateUserProfile(this.profile).then(r => {
        this.$store.commit('userProfile', r.data)
        this.readOnly = true
      }).catch(e => { alert('An error occured') })
        .then(() => { this.loading = false })
    }
  }
}
</script>
