<template>
  <v-app>
    <v-main>
      <v-container fluid class="d-flex flex-column justify-center align-center" style="height: 100vh">
        <div class="align-center d-flex flex-column h-100 justify-center">
          <img :src="logo" style="max-width: 300px" />
          <ValidationObserver ref="observer" v-slot="{ handleSubmit }">
            <v-form style="max-width: 500px" @submit.prevent="handleSubmit(sendRegisterRequest)">
              <v-card elevation="2" :loading="loading">
                <div class="container">
                  <div class="row pt-8">
                    <div class="col-12 col-md-6">
                      <ValidationProvider rules="required" name="First Name" v-slot="{ errors, valid }">
                        <v-text-field v-model="registerRequest.firstName" label="First Name" required dense :error-messages="errors" :success="valid" focus name="firstname"/>
                      </ValidationProvider>
                    </div>
                    <div class="col-12 col-md-6">
                      <ValidationProvider rules="required" name="Last Name" v-slot="{ errors, valid }">
                        <v-text-field v-model="registerRequest.lastName" label="Last Name" required dense :error-messages="errors" :success="valid" focus name="lastName"/>
                      </ValidationProvider>
                    </div>
                    <div class="col-12">
                      <ValidationProvider rules="required|email" name="Email" v-slot="{ errors, valid }">
                        <v-text-field v-model="registerRequest.email" label="Email" required dense :error-messages="errors" :success="valid" focus name="email"/>
                      </ValidationProvider>
                    </div>
                    <div class="col-12">
                      <ValidationProvider rules="required" name="Password" v-slot="{ errors, valid }">
                        <v-text-field :type="passwordInputType" v-model="registerRequest.password" label="Password" required dense :error-messages="errors" :success="valid" focus name="password">
                          <v-icon slot="prepend"> mdi-lock-outline </v-icon>
                          <v-icon slot="append" @click="togglePasswordVisible">{{ passwordInputIcon }}</v-icon>
                        </v-text-field>
                      </ValidationProvider>
                    </div>
                    <div class="col-12">
                      <ValidationProvider rules="required|confirmed:Password" name="Confirm password" v-slot="{ errors, valid }">
                        <v-text-field  :type="passwordInputType" v-model="registerRequest.confirmPassword" label="Confirm password" required dense :error-messages="errors" :success="valid" focus name="confirmPassword">
                          <v-icon slot="prepend"> mdi-lock-outline </v-icon>
                        </v-text-field>
                      </ValidationProvider>
                    </div>
                    <div class="col-12">
                      <ValidationProvider rules="required" name="phoneNumber" v-slot="{ errors, valid }">
                        <v-text-field v-model="registerRequest.phoneNumber" label="phoneNumber" required dense :error-messages="errors" :success="valid" focus name="phoneNumber"/>
                      </ValidationProvider>
                    </div>
                    <div class="col-12">
                      <ValidationProvider rules="required|length:13|cnp" name="CNP" v-slot="{ errors, valid }">
                        <v-text-field v-model="registerRequest.cnp" label="CNP" required dense :error-messages="errors" :success="valid" focus name="cnp"/>
                      </ValidationProvider>
                    </div>
                    <v-divider></v-divider>
                    <div class="col-12">Address</div>
                    <div class="col-12">
                      <ValidationProvider rules="required" name="city" v-slot="{ errors, valid }">
                        <v-text-field v-model="registerRequest.city" label="city" required dense :error-messages="errors" :success="valid" focus name="city"/>
                      </ValidationProvider>
                    </div>
                    <div class="col-12">
                      <ValidationProvider rules="required" name="county" v-slot="{ errors, valid }">
                        <v-text-field v-model="registerRequest.county" label="county" required dense :error-messages="errors" :success="valid" focus name="county"/>
                      </ValidationProvider>
                    </div>
                    <div class="col-12">
                      <ValidationProvider rules="required" name="street" v-slot="{ errors, valid }">
                        <v-text-field v-model="registerRequest.street" label="street" required dense :error-messages="errors" :success="valid" focus name="street"/>
                      </ValidationProvider>
                    </div>
                    <div class="col-12">
                      <v-text-field v-model="registerRequest.number" label="number" required dense focus name="number"/>
                    </div>
                    <div class="col-12">
                      <v-textarea rows="3" v-model="registerRequest.details" label="details" required dense focus name="details"/>
                    </div>

                    <div v-if="registerFailedMessage" class="col-12 text-center red--text">{{ registerFailedMessage }}</div>
                    <div class="col-12">
                      <v-btn block elevation="2" color="primary" type="submit">Register</v-btn>
                    </div>
                  </div>
                </div>
              </v-card>
            </v-form>
          </ValidationObserver>
        </div>
      </v-container>
    </v-main>
  </v-app>
</template>

<script>
import { ValidationObserver, ValidationProvider } from 'vee-validate'
import backend from '@/plugins/backend'

export default {
  name: 'Register',
  components: {
    ValidationObserver, ValidationProvider
  },
  data() {
    return {
      logo: require('@/assets/logo.png'),
      registerRequest: {
        firstName: '',
        lastName: '',
        email: '',
        password: '',
        confirmPassword: '',
        phoneNumber: '',
        cnp: '',
        city: '',
        county: '',
        street: '',
        number: '',
        details: ''
      },
      isPasswordVisible: false,
      registerFailedMessage: '',
      loading: false
    }
  },
  computed: {
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
  methods: {
    togglePasswordVisible() {
      this.isPasswordVisible = !this.isPasswordVisible
    },
    sendRegisterRequest() {
      this.loading = true
      const redirectUrl = window.location.origin + '/register-complete'
      backend.$register(redirectUrl, this.registerRequest)
        .then(response => {
          alert('Your account created successfully, please check your email for confirmation')
        }).catch(() => {
          this.registerFailedMessage = 'An error occured, try again later'
        })
        .then(() => { this.loading = false })
    }
  },
  mounted() {
    this.registerFailedMessage = ''
  }
}
</script>
