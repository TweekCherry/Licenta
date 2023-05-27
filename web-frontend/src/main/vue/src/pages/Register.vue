<template>
  <v-app>
    <v-main>
      <v-container fluid class="d-flex flex-column justify-center align-center" style="height: 100vh">
        <div class="align-center d-flex flex-column h-100 justify-center">
          <img :src="logo" style="max-width: 300px" />
          <ValidationObserver ref="observer" v-slot="{ handleSubmit }">
            <v-form style="max-width: 500px" @submit.prevent="handleSubmit(sendLoginRequest)">
              <v-card elevation="2" :loading="loading">
                <div class="container">
                  <div class="row">
                    <div class="col-12 pt-8 active">
                      <ValidationProvider rules="required" name="First Name" v-slot="{ errors }">
                        <v-text-field v-model="username" label="First Name" required dense :error-messages="errors" focus name="firstname">
                          <v-icon slot="prepend"> mdi-account-circle-outline </v-icon>
                        </v-text-field>
                      </ValidationProvider>
                    </div>
                    <div class="col-12 pt-8 active">
                      <ValidationProvider rules="required" name="Last Name" v-slot="{ errors }">
                        <v-text-field v-model="username" label="Last Name" required dense :error-messages="errors" focus name="lastname">
                          <v-icon slot="prepend"> mdi-account-circle-outline </v-icon>
                        </v-text-field>
                      </ValidationProvider>
                    </div>
                    <div class="col-12 active">
                    <ValidationProvider rules="required" name="Email" v-slot="{ errors }">
                        <v-text-field v-model="email" label="Email" required dense :error-messages="errors" focus name="email">
                          <v-icon slot="prepend"> mdi-email </v-icon>
                        </v-text-field>
                      </ValidationProvider>
                    </div>
                    <div class="col-12 active">
                      <ValidationProvider rules="required" name="Password" v-slot="{ errors }">
                        <v-text-field v-model="password" label="Password" required dense :type="passwordInputType" :error-messages="errors" name="password">
                          <v-icon slot="prepend"> mdi-lock-outline </v-icon>
                          <v-icon slot="append" @click="togglePasswordVisible">{{ passwordInputIcon }}</v-icon>
                        </v-text-field>
                      </ValidationProvider>
                      </div>
                      <div class="col-12 active">
                      <ValidationProvider rules="required" name="Password" v-slot="{ errors }">
                        <v-text-field v-model="passwordConfirm" label="Password confirm" required dense :type="passwordInputType" :error-messages="errors" name="password confirm">
                          <v-icon slot="prepend"> mdi-lock-outline </v-icon>
                          <v-icon slot="append" @click="togglePasswordVisible">{{ passwordInputIcon }}</v-icon>
                        </v-text-field>
                      </ValidationProvider>
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

export default {
  name: 'Register',
  components: {
    ValidationObserver, ValidationProvider
  },
  data() {
    return {
      logo: require('@/assets/logo.png'),
      username: '',
      password: '',
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
      this.register = true

      this.$login(this.username, this.password, this.email)
        .then(response => {
          this.$store.dispatch('register', response.data)
          this.$router.push({ name: 'Home' })
        }).catch(error => {
          if (error.response && error.response.status === 401) {
            this.loginFailedMessage = 'Invalid credentials'
          } else {
            this.loginFailedMessage = 'An error occured, try again later'
          }
        })
        .then(() => { this.loading = false })
        .then(() => { alert('Test 1') })
        .then(() => { alert('Test 2') })
        .then(() => { alert('Test 3') })
    }
  }
}
</script>
