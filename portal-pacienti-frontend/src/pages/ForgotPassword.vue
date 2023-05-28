<template>
  <v-app>
    <v-main>
      <v-container fluid class="d-flex flex-column justify-center align-center" style="height: 100vh">
        <div class="align-center d-flex flex-column h-100 justify-center">
          <img :src="logo" style="max-width: 300px" />
          <ValidationObserver ref="observer" v-slot="{ handleSubmit }">
            <v-form style="max-width: 500px" @submit.prevent="handleSubmit(sendForgotPasswordRequest)">
              <v-card elevation="2" :loading="loading">
                <div class="container">
                  <div class="row">
                    <div class="col-12 pt-8 active">
                      <ValidationProvider rules="required|email" name="Email" v-slot="{ errors }">
                        <v-text-field v-model="forgotPasswordRequest.email" label="Email" required dense :error-messages="errors" focus name="email">
                          <v-icon slot="prepend"> mdi-account-circle-outline </v-icon>
                        </v-text-field>
                      </ValidationProvider>
                    </div>
                    <div class="col-12">
                      <v-btn block elevation="2" color="primary" type="submit">Send</v-btn>
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
  name: 'ForgotPassword',
  components: {
    ValidationObserver, ValidationProvider
  },
  data() {
    return {
      logo: require('@/assets/logo.png'),
      forgotPasswordRequest: {
        email: ''
      },
      loading: false
    }
  },
  methods: {
    sendForgotPasswordRequest() {
      this.loading = true
      const redirectUrl = window.location.origin + '/forgot-password-complete'
      this.$forgotPassword(redirectUrl, this.forgotPasswordRequest).then(response => {
        alert('Please check your email')
      }).catch(() => {
        alert('An error occured')
      }).then(() => { this.loading = false })
    }
  }
}
</script>
