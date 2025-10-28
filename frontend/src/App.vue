<template>
  <v-app>
    <v-app-bar app flat class="appbar">
      <v-app-bar-title class="font-weight-bold d-flex align-center">
        <v-icon class="mr-2">mdi-chart-timeline-variant</v-icon>
        GBS Flights Analyzer
      </v-app-bar-title>
      <v-spacer />
      <v-btn to="/charts" variant="text" rounded="lg">Wykresy</v-btn>
    </v-app-bar>

    <v-main class="bg">
      <v-container class="py-8">
        <router-view />
      </v-container>
    </v-main>
  </v-app>
</template>

<script>
export default { name: 'App' }
</script>