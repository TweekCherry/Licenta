<template>
    <v-app-bar app>
      <v-app-bar-nav-icon @click="toggleSidebar"></v-app-bar-nav-icon>
      <v-spacer></v-spacer>
      <v-menu left offset-y>
        <template v-slot:activator="{ on, attrs }">
          <v-btn icon v-bind="attrs" v-on="on">
            <v-icon>mdi-account-circle-outline</v-icon>
          </v-btn>
        </template>
        <v-list>
          <v-list-item link v-for="(item, index) in links" :key="index" :to="item.route">
            <v-list-item-icon class="mr-3">
              <v-icon>{{item.icon}}</v-icon>
            </v-list-item-icon>
            <v-list-item-content>
              <v-list-item-title>{{item.title}}</v-list-item-title>
            </v-list-item-content>
          </v-list-item>
        </v-list>
      </v-menu>
    </v-app-bar>
</template>
<script>
export default {
  name: 'Headbar',
  props: {
    drawer: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      links: [
        { icon: 'mdi-face-agent', title: 'Profile', route: '/profile' },
        { icon: 'mdi-face-agent', title: 'Support', route: '/support' },
        { icon: 'mdi-face-agent', title: 'Logout', route: '/login' }
      ]
    }
  },
  methods: {
    toggleSidebar() {
      this.$emit('update:drawer', !this.drawer)
    }
  }
}
</script>
