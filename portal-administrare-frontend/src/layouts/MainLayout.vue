<template>
  <v-app>
    <Sidebar :visible.sync="drawer"></Sidebar>
    <Headbar :drawer.sync="drawer"></Headbar>
    <!-- Sizes your content based upon application components -->
    <v-main>
      <!-- Provides the application the proper gutter -->
      <v-container fluid>
        <!-- If using vue-router -->
        <router-view></router-view>
      </v-container>
    </v-main>
    <Footer></Footer>
  </v-app>
</template>
<script>
import Sidebar from '@/layouts/Sidebar.vue' // legare componente
import Headbar from '@/layouts/Headbar.vue'
import Footer from '@/layouts/Footer.vue'
import backend from '@/plugins/backend'
import store from '@/store'
export default {
  name: 'MainLayout',
  components: {
    Sidebar, Headbar, Footer
  },
  data() {
    return {
      drawer: true
    }
  },
  beforeRouteEnter(to, from, next) {
    return backend.$findUserProfile().then(r => {
      store.commit('userProfile', r.data)
    }).catch(e => { console.log(e) })
      .then(() => next())
  },
  mounted() {
    if (this.$vuetify.breakpoint.mobile) {
      this.drawer = false
    }
  }
}
</script>
