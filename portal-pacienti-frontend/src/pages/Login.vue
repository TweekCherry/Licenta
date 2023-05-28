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
                      <ValidationProvider rules="required|email" name="Email" v-slot="{ errors }">
                        <v-text-field v-model="email" label="Email" required dense :error-messages="errors" focus name="email">
                          <v-icon slot="prepend"> mdi-account-circle-outline </v-icon>
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
                    <div v-if="loginFailedMessage" class="col-12 text-center red--text">{{ loginFailedMessage }}</div>
                    <div class="col-12 d-flex justify-space-between align-center">
                      <v-checkbox v-model="rememberMe" label="Remember me" dense />
                      <router-link class="font-weight-regular text-decoration-none" to="/forgot-password">Forgot password?</router-link>
                    </div>
                    <div class="col-12">
                      <v-btn block elevation="2" color="primary" type="submit">Sign in</v-btn>
                    </div>
                    <div class="col-12 d-flex justify-center">
                      Don't have an account, <router-link class="font-weight-regular text-decoration-none pl-1" to="/register">sign up</router-link>
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
  name: 'Login',
  components: {
    ValidationObserver, ValidationProvider
  },
  data() {
    return {
      logo: require('@/assets/logo.png'),
      email: '',
      password: '',
      rememberMe: false,
      isPasswordVisible: false,
      loginFailedMessage: '',
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
    sendLoginRequest() {
      this.loading = true

      this.$login(this.email, this.password, this.rememberMe)
        .then(response => {
          this.$store.commit('login', response.data)
          this.$router.push({ name: 'Home' })
        }).catch(error => {
          if (error.response && error.response.status === 401) {
            this.loginFailedMessage = 'Invalid credentials'
          } else {
            this.loginFailedMessage = 'An error occured, try again later'
          }
        })
        .then(() => { this.loading = false })
    }
  }
}
</script>
