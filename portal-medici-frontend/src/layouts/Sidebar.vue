<template>
  <v-navigation-drawer app :value="visible">
    <v-list-item class="py-1 elevation-4">
      <img src="@/assets/logo-sidebar.png"/>
    </v-list-item>
    <v-list class="pt-0">
      <v-list-item link v-for="(item, index) in links" :key="index" :to="item.route">
        <v-list-item-icon class="mr-3">
          <v-icon>{{item.icon}}</v-icon>
        </v-list-item-icon>
        <v-list-item-content>
          <v-list-item-title>{{item.title}}</v-list-item-title>
        </v-list-item-content>
      </v-list-item>
    </v-list>
  </v-navigation-drawer>
</template>
<script>
export default {
  name: 'Sidebar',
  props: {
    visible: {
      type: Boolean,
      default: true
    }
  },
  data() {
    return {
      links: [
        { icon: 'mdi-home-account', title: 'Home', route: '/home' },
        { icon: 'mdi-clipboard-pulse', title: 'Consultations', route: '/consultations' },
        { icon: 'mdi-medical-bag', title: 'Appointments', route: '/appointments' }
      ]
    }
  }
}
</script>
