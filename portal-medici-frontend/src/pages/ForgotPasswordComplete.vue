<template>
  <v-app>
    <v-main>
      <v-container fluid class="d-flex flex-column justify-center align-center" style="height: 100vh">
        <div class="align-center d-flex flex-column h-100 justify-center">
          <img :src="logo" style="max-width: 300px" />
          <ValidationObserver ref="observer" v-slot="{ handleSubmit }">
            <v-form style="max-width: 500px" @submit.prevent="handleSubmit(sendResetPasswordConfirmation)">
              <v-card elevation="2" :loading="loading">
                <div class="container">
                  <div class="row pt-8">
                    <div class="col-12">
                      <ValidationProvider rules="required" name="Password" v-slot="{ errors, valid }">
                        <v-text-field :type="passwordInputType" v-model="password" label="Password" required dense :error-messages="errors" :success="valid" focus name="password">
                          <v-icon slot="prepend"> mdi-lock-outline </v-icon>
                          <v-icon slot="append" @click="togglePasswordVisible">{{ passwordInputIcon }}</v-icon>
                        </v-text-field>
                      </ValidationProvider>
                    </div>
                    <div class="col-12">
                      <ValidationProvider rules="required|confirmed:Password" name="Confirm password" v-slot="{ errors, valid }">
                        <v-text-field  :type="passwordInputType" v-model="confirmPassword" label="Confirm password" required dense :error-messages="errors" :success="valid" focus name="confirmPassword">
                          <v-icon slot="prepend"> mdi-lock-outline </v-icon>
                        </v-text-field>
                      </ValidationProvider>
                    </div>
                    <div class="col-12">
                      <v-btn block elevation="2" color="primary" type="submit">Reset password</v-btn>
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
  name: 'ForgotPasswordComplete',
  components: {
    ValidationObserver, ValidationProvider
  },
  data() {
    return {
      logo: require('@/assets/logo.png'),
      loading: false,
      password: '',
      confirmPassword: '',
      isPasswordVisible: false
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
    sendResetPasswordConfirmation() {
      this.loading = true
      backend.$completeForgotPassword(this.$route.query.token, this.password)
        .then(response => {
          this.$store.commit('login', response.data)
          this.$router.push({ name: 'Home' })
        }).catch(() => {
          this.showErrorNotification('An error occured, try again later')
        })
        .then(() => { this.loading = false })
    }
  }
}
</script>
