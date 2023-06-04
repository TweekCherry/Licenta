<template>
  <v-app>
    <v-main>
      <v-container fluid class="fill-height align-center justify-center">
        <v-card :loading="loading">
          <v-card-title class="d-flex justify-space-around">
            <div>404</div>
          </v-card-title>
          <v-divider class="mx-4"></v-divider>
          <v-card-text>
            Page not found
          </v-card-text>
          <v-divider class="mx-4"></v-divider>
          <v-card-actions class="d-flex justify-space-around">
            <v-btn text color="primary" to="/home">Go home</v-btn>
          </v-card-actions>
        </v-card>
      </v-container>
    </v-main>
  </v-app>
</template>
<script>
export default {
  name: '404'
}
</script>
