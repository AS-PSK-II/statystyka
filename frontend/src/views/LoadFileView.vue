<template>
  <v-card class="pa-6 fancy-card" elevation="0">
    <div class="mb-4 d-flex align-center">
      <v-avatar color="primary" size="36" class="mr-3">
        <v-icon size="20">mdi-file-delimited</v-icon>
      </v-avatar>
      <div>
        <div class="text-subtitle-1 font-weight-medium">Wczytaj plik Excel</div>
      </div>
    </div>

    <v-row>
      <v-col cols="12" md="12">
        <v-file-input
            v-model="file"
            label="Wybierz plik Excel"
            variant="solo-filled"
            rounded="lg"
            prepend-inner-icon="mdi-tray-arrow-up"
            accept=".xls,.xlsx,.csv,text/csv"
            show-size
            hide-details="auto"
            @click:clear="clearFile()"
            @update:model-value="onFileLoad"
        />
      </v-col>
      <v-col cols="12">
        <v-alert v-if="errorMsg" type="error" class="mt-4" variant="tonal">{{ errorMsg }}</v-alert>
        <v-alert v-if="successMsg" type="success" class="mt-4" variant="tonal">{{ successMsg }}</v-alert>
      </v-col>
      <v-col
          v-if="fileLoaded"
          cols="6"
          class="mx-auto"
      >
        <v-btn
            v-if="!successMsg"
            :loading="uploading"
            :disabled="!selectedFile || uploading"
            color="primary"
            block
            height="45"
            rounded
            @click="uploadFile()"
        ><v-icon start>mdi-upload</v-icon> Załaduj plik</v-btn>
        <v-btn
            v-if="successMsg"
            :loading="downloading"
            :disabled="downloading"
            color="primary"
            block
            height="45"
            rounded
            @click="getData()"
        ><v-icon start>mdi-download</v-icon>Pobierz dane</v-btn>
      </v-col>
    </v-row>
  </v-card>
</template>

<script>
import ChartsView from "@/views/ChartsView.vue";
import api from "@/utils/api.js";

export default {
  name: 'LoadFileView',
  components: {ChartsView},
  data () {
    return {
      tabIndex: 0,
      file: null,
      fileLoaded: false,
      uploading: false,
      errorMsg: '',
      successMsg: '',
      downloading: false
    }
  },
  computed: {
    selectedFile () {
      if (!this.file) return null
      return Array.isArray(this.file) ? this.file[0] : this.file
    }
  },
  watch: {
  },
  methods: {
    onFileLoad () {
      this.errorMsg = ''
      this.successMsg = ''
      this.fileLoaded = !!this.selectedFile
    },
    clearFile () {
      this.file = null
      this.fileLoaded = false
      this.errorMsg = ''
      this.successMsg = ''
    },
    async uploadFile () {
      try {
        this.errorMsg = ''
        this.successMsg = ''
        if (!this.selectedFile) {
          this.errorMsg = 'Nie wybrano pliku.'
          return
        }

        const form = new FormData()
        form.append('file', this.selectedFile, this.selectedFile.name)

        this.uploading = true

        const { data } = await api.post('/api/v1/statistics', form, {
          headers: { 'Content-Type': 'multipart/form-data' }
        })
        this.successMsg = 'Plik załadowany pomyślnie.'

      } catch (e) {
        this.errorMsg = e?.response?.data?.message || e.message || 'Błąd podczas wysyłania pliku.'
      } finally {
        this.uploading = false
      }
    },
    async getData () {
      this.downloading = true
      try {
        const responseRowData = await api.get('/api/v1/statistics/raw')
        const responseStatisticsData = await api.get('/api/v1/statistics')
        let fullData = {
          rowData: this.sortByTimestamp(responseRowData.data),
          statisticsData: responseStatisticsData.data
        }
        this.$emit('loaded', fullData)
      } catch (e) {
        console.log(e)
      }
      this.downloading = false
    },
    sortByTimestamp(data) {
      const romanMap = { I:1, II:2, III:3, IV:4 };

      return data.sort((a, b) => {
        const [rA, _, yA] = a.timestamp.split(" ");
        const [rB, __, yB] = b.timestamp.split(" ");

        const yearA = Number(yA);
        const yearB = Number(yB);

        const qA = romanMap[rA] || 0;
        const qB = romanMap[rB] || 0;

        if (yearA !== yearB) return yearA - yearB;
        return qA - qB;
      });
    }
  }
}
</script>