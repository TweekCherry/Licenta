<template>
  <v-app>
    <v-main>
      <v-container fluid class="d-flex flex-column justify-center align-center" style="height: 100vh">
        <div class="align-center d-flex flex-column h-100 justify-center">
          <img :src="logo" style="max-width: 300px" />
            <v-card elevation="2">
              <div class="container">
                <div class="row">
                  <div class="col-12 pt-8 d-flex justify-center">
                    Invalid token
                    <router-link class="font-weight-regular text-decoration-none pl-1" to="/login">Go to login</router-link>
                  </div>
                </div>
              </div>
            </v-card>
        </div>
      </v-container>
    </v-main>
  </v-app>
</template>
<script>
import backend from '@/plugins/backend'
export default {
  name: 'RegisterComplete',
  data() {
    return {
      logo: require('@/assets/logo.png')
    }
  },
  beforeRouteEnter(to, from, next) {
    next(vm => {
      return backend.$completeRegistration(to.query.token).then(response => {
        vm.$store.commit('login', response.data)
        vm.$router.push({ name: 'Home' })
      }).catch(e => { })
    })
  }
}
</script>
