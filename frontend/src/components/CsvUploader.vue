<template>
  <v-card class="pa-6 fancy-card" elevation="0">
    <div class="mb-4 d-flex align-center">
      <v-avatar color="primary" size="36" class="mr-3">
        <v-icon size="20">mdi-file-delimited</v-icon>
      </v-avatar>
      <div>
        <div class="text-subtitle-1 font-weight-medium">Wczytaj pliki CSV</div>
      </div>
      <v-spacer />
      <v-btn color="primary" rounded="lg" size="large" @click="$emit('uploaded')">
       Załaduj próbkę
      </v-btn>
    </div>

    <v-row>
      <v-col cols="12" md="9">
        <v-file-input
            label="Wybierz pliki CSV"
            variant="solo-filled"
            rounded="lg"
            prepend-inner-icon="mdi-tray-arrow-up"
            accept=".csv,text/csv"
            multiple
            show-size
            hide-details="auto"
        />
      </v-col>
      <v-col cols="12" md="3" class="d-flex align-end">
        <v-btn block variant="tonal" color="secondary" rounded="lg" @click="$emit('clearView')">
          <v-icon start>mdi-broom</v-icon>Wyczyść
        </v-btn>
      </v-col>
    </v-row>
  </v-card>
</template>

<script>
export default { name: 'CsvUploader' }
</script>
