<template>
  <v-dialog v-model="visible" :fullscreen="fullscreen" scrollable width="800" persistent>
    <ValidationObserver ref="observer" v-slot="{ handleSubmit }">
      <v-form @submit.prevent="handleSubmit(saveMedic)">
        <v-card :loading="loading">
          <v-card-title class="justify-space-between">
            <div>
              <div>Medic details</div>
            </div>
            <div>
              <v-btn icon small @click="fullscreen = !fullscreen">
                <v-icon v-if="!fullscreen">mdi-window-maximize</v-icon>
                <v-icon v-if="fullscreen">mdi-window-restore</v-icon>
              </v-btn>
              <v-btn icon small @click="visible = false">
                <v-icon>mdi-close</v-icon>
              </v-btn>
            </div>
          </v-card-title>
          <v-divider class="mx-4 pb-5"></v-divider>
          <v-card-text>
            <div class="row">
              <div class="col-12">
                <ValidationProvider rules="required" name="First name" v-slot="{ errors, valid }">
                  <v-text-field v-model="medicData.profileData.firstName" label="First name" required dense :error-messages="errors" :success="valid" focus name="firstName"/>
                </ValidationProvider>
              </div>
              <div class="col-12">
                <ValidationProvider rules="required" name="Last name" v-slot="{ errors, valid }">
                  <v-text-field v-model="medicData.profileData.lastName" label="Last name" required dense :error-messages="errors" :success="valid" focus name="firstName"/>
                </ValidationProvider>
              </div>
              <div class="col-12">
                <ValidationProvider rules="required" name="Title" v-slot="{ errors, valid }">
                  <v-text-field v-model="medicData.title" label="Title" required dense :error-messages="errors" :success="valid" focus name="title"/>
                </ValidationProvider>
              </div>
              <div class="col-12">
                <ValidationProvider rules="required" name="Grade" v-slot="{ errors, valid }">
                  <v-text-field v-model="medicData.grade" label="Grade" required dense :error-messages="errors" :success="valid" focus name="grade"/>
                </ValidationProvider>
              </div>
              <div class="col-12">
                <ValidationProvider rules="required|email" name="Email" v-slot="{ errors, valid }">
                  <v-text-field v-model="medicData.accountData.email" label="Email" required dense :error-messages="errors" :success="valid" focus name="email"/>
                </ValidationProvider>
              </div>
              <div class="col-12">
                <ValidationProvider :rules="passwordRules" name="Password" v-slot="{ errors, valid }">
                  <v-text-field :type="passwordInputType" v-model="password" label="Password" required dense :error-messages="errors" :success="valid" focus name="password">
                    <v-icon slot="prepend"> mdi-lock-outline </v-icon>
                    <v-icon slot="append" @click="togglePasswordVisible">{{ passwordInputIcon }}</v-icon>
                  </v-text-field>
                </ValidationProvider>
              </div>
              <div class="col-12">
                <ValidationProvider :rules="`${passwordRules}|confirmed:Password`" name="Confirm password" v-slot="{ errors, valid }">
                  <v-text-field  :type="passwordInputType" v-model="confirmPassword" label="Confirm password" required dense :error-messages="errors" :success="valid" focus name="confirmPassword">
                    <v-icon slot="prepend"> mdi-lock-outline </v-icon>
                  </v-text-field>
                </ValidationProvider>
              </div>
              <div class="col-12">
                <ValidationProvider rules="required" name="Departments" v-slot="{ errors, valid }">
                  <v-autocomplete
                    v-model="medicData.departments" multiple :items="availableDepartments" dense label="Departments" :error-messages="errors" :success="valid" item-text="name" item-value="name">
                  </v-autocomplete>
                </ValidationProvider>
              </div>
              <div class="col-12">
                <ValidationProvider rules="required" name="Clinics" v-slot="{ errors, valid }">
                  <v-autocomplete
                    v-model="medicData.clinic" :items="availableClinics" dense label="Available in" :error-messages="errors" :success="valid" item-text="name" item-value="id">
                  </v-autocomplete>
                </ValidationProvider>
              </div>
              <div class="col-12">
                <ImageInput v-model="medicData.image" label="Profile"/>
              </div>
            </div>
          </v-card-text>
          <v-divider class="mx-4"></v-divider>
          <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn plain color="red" @click="visible = false">Cancel</v-btn>
            <v-btn plain type="submit" color="green">Save</v-btn>
            <v-spacer></v-spacer>
          </v-card-actions>
        </v-card>
      </v-form>
    </ValidationObserver>
  </v-dialog>
</template>
<script>
import backend from '@/plugins/backend'
import { ValidationObserver, ValidationProvider } from 'vee-validate'
import ImageInput from '@/pages/components/ImageInput.vue'
export default {
  name: 'PhysiciansDetailsDialog',
  components: {
    ValidationObserver, ValidationProvider, ImageInput
  },
  props: {
    isVisible: Boolean,
    medic: Object,
    editMode: {
      type: Boolean,
      default: false
    }
  },
  computed: {
    visible: {
      get: function() {
        return this.isVisible
      },
      set: function(v) {
        this.$emit('closed')
      }
    },
    passwordRules() {
      if (this.medic === null) {
        return 'required'
      }
      return ''
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
    },
    requestData() {
      const medic = {
        id: this.medicData.id,
        image: this.medicData.image,
        title: this.medicData.title,
        grade: this.medicData.grade,
        departments: this.medicData.departments,
        clinic: this.medicData.clinic,
        deleted: false
      }
      const account = this.medicData.accountData
      const profile = this.medicData.profileData
      const request = {
        medic: medic,
        account: account,
        profile: profile,
        password: this.password
      }
      return request
    }
  },
  data() {
    return {
      fullscreen: false,
      loading: false,
      medicData: this.newMedicData(),
      availableDepartments: [],
      availableClinics: [],
      password: null,
      confirmPassword: null,
      isPasswordVisible: false
    }
  },
  methods: {
    saveMedic() {
      this.loading = true
      let request = null
      if (this.medic !== null) {
        request = backend.$updateMedic(this.requestData)
      } else {
        request = backend.$saveMedic(this.requestData)
      }
      request.then(r => {
        this.$emit('save')
        this.visible = false
      }).catch(e => { alert('An error occured, try again later') })
        .then(() => { this.loading = false })
    },
    loadAvailableClinics() {
      return backend.$findClinics().then(r => {
        this.availableClinics = r.data
      }).catch(e => { console.log(e) })
    },
    loadAvailableDepartments() {
      return backend.$findDepartments().then(r => {
        this.availableDepartments = r.data
      }).catch(e => { console.log(e) })
    },
    newMedicData() {
      return {
        id: null,
        profile: null,
        clinic: null,
        image: null,
        title: '',
        grade: '',
        deleted: false,
        departments: [],
        profileData: {
          firstName: '',
          lastName: ''
        },
        clinicData: null,
        accountData: {
          email: null,
          password: null
        }
      }
    },
    togglePasswordVisible() {
      this.isPasswordVisible = !this.isPasswordVisible
    }
  },
  watch: {
    isVisible: function(newValue, oldValue) {
      if (newValue) {
        if (this.$refs.observer) {
          this.$refs.observer.reset()
        }
        this.medicData = this.newMedicData()
        if (this.medic !== null) {
          this.medicData = this.clone(this.medic)
        }
        this.loading = true
        Promise.all([
          this.loadAvailableClinics(),
          this.loadAvailableDepartments()]
        ).then(() => { this.loading = false })
      }
    }
  }
}
</script>
