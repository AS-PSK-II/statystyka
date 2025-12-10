<template>
  <v-container>
    <div class="display-1 mb-6 page-title">Twarze Chernoffa</div>

    <v-card class="pa-5 fancy-card mb-6" elevation="0">
      <v-row>
        <v-col cols="12" md="8">
          <v-select
              v-model="selectedAttrs"
              :items="airports"
              label="Wybierz atrybuty (2–5 lotnisk)"
              variant="solo-filled"
              rounded="lg"
              multiple
              chips
              closable-chips
              prepend-inner-icon="mdi-filter-variant"
          />
        </v-col>

        <v-col cols="12" md="4" class="d-flex align-end justify-end">
          <v-btn
              color="primary"
              rounded
              height="44"
              class="mr-3"
              @click="generateFaces"
          >
            <v-icon start>mdi-emoticon-happy-outline</v-icon>
            Generuj Twarze
          </v-btn>

          <v-chip
              v-if="isReady"
              color="green"
              text-color="white"
              class="font-weight-medium"
              label
          >
            <v-icon start>mdi-check-circle</v-icon>
            Gotowe do analizy
          </v-chip>
          <v-chip
              v-else
              color="orange"
              text-color="black"
              label
          >
            <v-icon start>mdi-alert</v-icon>
            Wybierz 2–5 atrybutów
          </v-chip>
        </v-col>
      </v-row>

      <v-alert type="info" variant="tonal" class="mt-3">
        Każde lotnisko steruje innym elementem twarzy (np. wielkością oczu,
        krzywizną ust, brwiami). Wyższe wartości &rarr; silniejsze „emocje”.
      </v-alert>
    </v-card>

    <v-row v-if="faces.length">
      <v-col
          v-for="(row, index) in series"
          :key="index"
          cols="12"
          sm="6"
          md="4"
          lg="3"
      >
        <v-card class="face-card" elevation="0">
          <div class="pa-3 d-flex justify-space-between align-center">
            <div class="text-subtitle-2 font-weight-medium">
              Twarz dla {{ row.timestamp }}
            </div>
            <v-chip size="x-small" color="primary" variant="flat">
              #{{ index + 1 }}
            </v-chip>
          </div>

          <div class="d-flex justify-center pb-2">
            <ChernoffFace :params="faces[index]" :size="160" />
          </div>

          <v-divider />

          <div class="pa-3">
            <div class="text-caption text-medium-emphasis mb-2">
              Rozkład atrybutów:
            </div>
            <div class="d-flex flex-wrap gap-1">
              <v-chip
                  v-for="(attr, i) in selectedAttrs"
                  :key="attr"
                  size="x-small"
                  :color="palette[i % palette.length]"
                  variant="outlined"
                  class="mr-1 mb-1"
              >
                {{ shortName(attr) }}:
                {{ fmt(rowValues[index][attr]) }}
              </v-chip>
            </div>
          </div>
        </v-card>
      </v-col>
    </v-row>

    <v-alert
        v-else
        type="info"
        variant="tonal"
        class="mt-6"
    >
      Wybierz 2–5 lotnisk i kliknij „Generuj Twarze”, aby zobaczyć wizualizację.
    </v-alert>
  </v-container>
</template>

<script>
import ChernoffFace from "@/components/ChernoffFace.vue";

export default {
  name: 'ChernoffFacesView',
  components: { ChernoffFace },
  props: {
    chartData: {
      type: Array,
      required: true
    }
  },
  data () {
    return {
      series: [],
      airports: [],
      selectedAttrs: [],
      faces: [],               // parametry twarzy per wiersz
      rowValues: [],           // oryginalne wartości wybranych lotnisk per wiersz
      palette: ['#6366F1','#22C1C3','#EF4444','#F59E0B','#10B981','#8B5CF6','#EC4899','#14B8A6']
    }
  },
  created () {
    this.initFromData()
  },
  watch: {
    chartData: {
      handler () {
        this.initFromData()
      },
      deep: true
    }
  },
  computed: {
    isReady () {
      const c = this.selectedAttrs.length
      return c >= 2 && c <= 5
    }
  },
  methods: {
    initFromData () {
      // kopia i sortowanie rosnące po czasie
      const clone = JSON.parse(JSON.stringify(this.chartData || []))
      this.series = this.sortByTimestamp(clone)
      this.airports = Array.from(
          new Set(this.series.flatMap(s => s.dataPoints.map(d => d.id)))
      )
      this.selectedAttrs = this.airports.slice(0, 3) // domyślnie 3 pierwsze
      this.faces = []
      this.rowValues = []
    },

    sortByTimestamp (data) {
      const romanMap = { I: 1, II: 2, III: 3, IV: 4 }
      return data.sort((a, b) => {
        const [rA,, yA] = a.timestamp.split(' ')
        const [rB,, yB] = b.timestamp.split(' ')
        const yearA = Number(yA)
        const yearB = Number(yB)
        const qA = romanMap[rA] || 0
        const qB = romanMap[rB] || 0
        if (yearA !== yearB) return yearA - yearB
        return qA - qB
      })
    },

    generateFaces () {
      if (!this.isReady || !this.series.length) {
        this.faces = []
        return
      }

      // zbieramy wartości: per wiersz -> { airportId: value }
      const rowValues = this.series.map(row => {
        const map = {}
        row.dataPoints.forEach(dp => { map[dp.id] = dp.value })
        return map
      })
      this.rowValues = rowValues

      // normalizacja per lotnisko
      const normalized = {}
      this.selectedAttrs.forEach(attr => {
        const vals = rowValues.map(r => Number(r[attr] || 0))
        const min = Math.min(...vals)
        const max = Math.max(...vals)
        const span = max - min || 1
        normalized[attr] = vals.map(v => (v - min) / span)
      })

      // mapowanie atrybutów -> cechy twarzy
      const faces = this.series.map((_, rowIdx) => {
        const vals = this.selectedAttrs.map(a => normalized[a][rowIdx])

        const [v0 = 0.5, v1 = 0.5, v2 = 0.5, v3 = 0.5, v4 = 0.5] = vals

        return {
          eyeSize: v0,          // atrybut 1 -> wielkość oczu
          eyeSep: 0.5,          // stałe, żeby nie przesadzić
          browTilt: v2,         // atrybut 3 -> „gniew” / „smutek”
          mouthCurve: v1,       // atrybut 2 -> uśmiech / smutek
          faceW: v3,            // atrybut 4 -> szerokość twarzy
          faceH: 0.5,
          noseLen: v4 || 0.5    // atrybut 5 -> długość nosa
        }
      })

      this.faces = faces
    },

    shortName (name) {
      return name.length > 18 ? name.slice(0, 16) + '…' : name
    },

    fmt (n) {
      if (n == null) return '-'
      return new Intl.NumberFormat('pl-PL', { notation: 'compact' }).format(n)
    }
  }
}
</script>

<style scoped>
.page-title{
  font-weight: 700;
  letter-spacing: .2px;
}
.fancy-card{
  border-radius: 18px;
  background: linear-gradient(180deg, rgba(15,23,42,.9), rgba(15,23,42,.95));
  border: 1px solid rgba(148,163,184,.4);
}
.face-card{
  border-radius: 18px;
  background: linear-gradient(180deg, rgba(15,23,42,.9), rgba(15,23,42,.97));
  border: 1px solid rgba(30,64,175,.5);
}
</style>
